package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class DatesServiceImpl implements DatesService {	
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository; 
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private MonthsRepository monthsRepository;
	
	// METODOS PUBLICOS

	public LocalDateTime getDateOFromDateVAU(DateDTOFromDB dateVAU) {

		LocalDateTime dateO = LocalDateTime.now();
		
		return dateO;
	}

	
	public DateDTO getDateVAUFromDateO (LocalDateTime dateO) {
		
		DateDTO dateVAU = new DateDTO();
	
		// Lo primero es la fecha del último métono que haya ocurrido hasta la fecha a consultar
		MetonsEntity lastMeton = this.metonsRepository.findTopByDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateDesc(dateO);
		
		if(lastMeton != null) {

			// Con esto, lo primero es obtener todos los solsticios y equinoccios ocurridos entre el métono y la fecha a consultar mas un año			
			List<SolsticiosYEquinocciosEntity> soesDesdeElMetonoHastaUnAnyoMas = this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(lastMeton.getDate(), dateO.plusYears(1));
			
			// Y todas las lunas, desde un año antes hasta un año despues de la fecha a consultar			
			List<LunasEntity> lunasDesdeElAnyoAnteriorHasElSiguiente = this.lunasRepository.findByDateBetween(dateO.minusYears(1), dateO.plusYears(1));
		
			
			if(soesDesdeElMetonoHastaUnAnyoMas.isEmpty() || lunasDesdeElAnyoAnteriorHasElSiguiente.isEmpty()) {
				
				System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas.");
			}
			else {
				
				// Lo primero es obtener el añoVAU				
				dateVAU.setYear(this.getVAUYear(dateO, soesDesdeElMetonoHastaUnAnyoMas));
				
				// Luego el mesVau
				dateVAU.setMonth(this.getVAUMonth(dateO, soesDesdeElMetonoHastaUnAnyoMas, lunasDesdeElAnyoAnteriorHasElSiguiente));
			}
			
		}
		else {
			System.out.println("Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
		}
		
		return dateVAU;
		
	}
	
	public DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU) {

		DateDTOFromDB dateVAUDTOFromDB = new DateDTOFromDB();
	
		
		return dateVAUDTOFromDB;
	}


	
	
	// ========================= METODOS PRIVADOS
	


	private String getVAUYear(LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElMetonoHastaUnAnyoMas) {
		
		String vauYear = "-";
		
		boolean caeEnSolsticioDeInvierno=false;
		
		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		
		int year = 0;
		
		for(int i = 0; i<soesDesdeElMetonoHastaUnAnyoMas.size() && !caeEnSolsticioDeInvierno; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElMetonoHastaUnAnyoMas.get(i);
			
			if(soe.isSolsticioInvierno()) {				
		
					if(soe.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
						
						caeEnSolsticioDeInvierno=true;
					}
					else if (soe.getDate().toLocalDate().isBefore(dateO.toLocalDate())){
					
						year=year+1;
				}
			}
			
		}
		
		if(caeEnSolsticioDeInvierno) {
			
			vauYear = "No pertenece a ningún año, es el día del solsticio de invierno.";
		}
		else {
			vauYear = String.valueOf(year);
		}
		
		return vauYear;
		
	}
	
	
	
	private String getVAUMonth(LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElMetonoHastaUnAnyoMas, List<LunasEntity> fasesLunaresDelAnyoYDelAnterior) {
		
		String vauMonthName = "-";
		
		// Lo primero es coger los solsticios y equinoccios mas cercanos a la fecha a consultar
		SolsticiosYEquinocciosEntity lastSOE = null;
		SolsticiosYEquinocciosEntity nextSOE = null;
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaConNextSOE = Long.MAX_VALUE;
		
		// Si cae en SOE, ya tenemos el mes
		boolean caeEnSOE = false;
		
		for(int i = 0; i<soesDesdeElMetonoHastaUnAnyoMas.size() && !caeEnSOE; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElMetonoHastaUnAnyoMas.get(i);
			
			if(soe.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
				caeEnSOE = true;
				lastSOE = soe;
				nextSOE = soe;
			}
			else if(soe.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
				
				long diasDeDiferenciaEntreLastSOEYFecha = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), dateO.toLocalDate());
				
				if(diasDeDiferenciaEntreLastSOEYFecha < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYFecha;
					lastSOE = soe;
				}
				
			}
			else if(soe.getDate().toLocalDate().isAfter(dateO.toLocalDate())) {
				
				long diasDeDiferenciaEntreNextSOEYFecha = ChronoUnit.DAYS.between(dateO.toLocalDate(), soe.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreNextSOEYFecha < diasMinimosDeDiferenciaConNextSOE) {
					diasMinimosDeDiferenciaConNextSOE = diasDeDiferenciaEntreNextSOEYFecha;
					nextSOE = soe;
				}
			}			
		}
		
		if(lastSOE != null && nextSOE != null) {
			// Luego, coger las lunas nuevas que se encuentran entre ambos lastSOE y nextSOE
			// Si cae en Luna nueva, ya tenemos el mes
			
			List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE = new ArrayList<>();
			
			boolean caeEnLunaNueva = false;				
			for(int i = 0; i<fasesLunaresDelAnyoYDelAnterior.size() && !caeEnLunaNueva; i++) {
				
				LunasEntity luna = fasesLunaresDelAnyoYDelAnterior.get(i);
				
				if(luna.isNueva()) {
					
					if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
						
						caeEnLunaNueva = true;
					}
					else if(luna.getDate().toLocalDate().isAfter(lastSOE.getDate().toLocalDate()) || luna.getDate().toLocalDate().isEqual(lastSOE.getDate().toLocalDate())) {
						
						if(luna.getDate().toLocalDate().isBefore(nextSOE.getDate().toLocalDate())) {
							lunasNuevasEntreLastSOEYNextSOE.add(luna);
						}					
					}	
				}				
			}
			
			// Si cae en luna nueva, no pertenece a ningun mes.
			if(caeEnLunaNueva) {
				
				vauMonthName = "No pertenece a ningún mes, es el día de luna nueva.";
			}
			else {
				
				MonthsEntity vauMonth = new MonthsEntity();
				
				// Si cae en soe, pertenece al mes hibrido de ese soe.
				if(caeEnSOE) {

					vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);
					
				}
				else {
					
					// Si no, hay que calcular cuantas lunas nuevas han pasado desde el lastSOE hasta la fecha a consultar
					
					int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
					long diasMinimosDeDiferenciaLunaNuevaConNextSOE = Long.MAX_VALUE;
					LunasEntity lastLNBeforeNextSOE = null;
					
					for(LunasEntity luna : lunasNuevasEntreLastSOEYNextSOE) {
						
						long diasDeDiferenciaEntreNextSOEYLN = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), nextSOE.getDate().toLocalDate());
						
						if(diasDeDiferenciaEntreNextSOEYLN < diasMinimosDeDiferenciaLunaNuevaConNextSOE) {
							
							lastLNBeforeNextSOE=luna;
							diasMinimosDeDiferenciaLunaNuevaConNextSOE = diasDeDiferenciaEntreNextSOEYLN;
							
						}
						
						if(dateO.toLocalDate().isAfter(luna.getDate().toLocalDate())) {
							
							lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO+1;						
						}
					}
					
					// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
					
					if(lastLNBeforeNextSOE != null) {
						
						if(dateO.toLocalDate().isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && dateO.toLocalDate().isBefore(nextSOE.getDate().toLocalDate())) {
		
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(nextSOE.getStartingSeason(), 0, false);

						}
						else {
							
							//Si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
							
							if(lunasNuevasPasadasDesdeLastSOEHastaDateO == 0 && lastSOE.isSolsticioInvierno()) {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);								
							}
							else {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, false);
							}
							
							
						}	
					}
					else {
						System.out.println("Error, no hay lastLNBeforeNextSOE.");
					}
									
				}
				
				vauMonthName = vauMonth.getName();
			}
		}
		else {
			
			System.out.println("Error, no se han encontrado nextSOE y/o lastSOE.");
		}
		

		return vauMonthName;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		
		return vauWeekAndDay;
	}



	
}







