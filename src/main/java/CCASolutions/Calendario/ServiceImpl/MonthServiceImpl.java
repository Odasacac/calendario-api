package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Services.MonthService;

@Service
public class MonthServiceImpl implements MonthService{

	@Autowired
	private MonthsRepository monthsRepository;
	
	public MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente) {
		
		MonthDTO month = new MonthDTO();
		
		List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente = new ArrayList<>();
		List<LunasEntity> lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente = new ArrayList<>();
		
		for(LunasEntity luna : lunasDesdeElAnyoAnteriorHastaElSiguiente) {
			if(luna.isNueva()) {
				lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.add(luna);
			}
			else if(luna.isLlena()) {
				lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.add(luna);
			}
		}
		
		// Lo primero es coger los solsticios y equinoccios mas cercanos a la fecha a consultar
		SolsticiosYEquinocciosEntity lastSOE = null;
		SolsticiosYEquinocciosEntity nextSOE = null;
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaConNextSOE = Long.MAX_VALUE;
		
		// Si cae en SOE, ya tenemos el mes
		boolean caeEnSOE = false;
		
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSOE; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.getDate().toLocalDate().isEqual(date)) {
				caeEnSOE = true;
				lastSOE = soe;
				nextSOE = soe;
			}
			else if(soe.getDate().toLocalDate().isBefore(date)) {
				
				long diasDeDiferenciaEntreLastSOEYFecha = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreLastSOEYFecha < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYFecha;
					lastSOE = soe;
				}
				
			}
			else if(soe.getDate().toLocalDate().isAfter(date)) {
				
				long diasDeDiferenciaEntreNextSOEYFecha = ChronoUnit.DAYS.between(date, soe.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreNextSOEYFecha < diasMinimosDeDiferenciaConNextSOE) {
					diasMinimosDeDiferenciaConNextSOE = diasDeDiferenciaEntreNextSOEYFecha;
					nextSOE = soe;
				}
			}			
		}
		
		if(lastSOE != null && nextSOE != null) {
			// Luego, coger las lunas nuevas que se encuentran entre ambos lastSOE y nextSOE
			// Si cae en Luna nueva, ya tenemos el mes
			
			LunasEntity lunaNuevaAnteriorMasCercanaALaFecha = new LunasEntity();
			LunasEntity lunaNuevaPosteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
			Long numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = Long.MAX_VALUE;	
			Long numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate = Long.MAX_VALUE;	
			
			List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE = new ArrayList<>();
			boolean caeEnLunaNueva = false;
			String surname = "";
			for(int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.size(); i++) {
				
				LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.get(i);
	
				if(luna.getDate().toLocalDate().isEqual(date)) {
						
					lunasNuevasEntreLastSOEYNextSOE.add(luna);	
					caeEnLunaNueva = true;
					
					if(luna.isSelecta()) {
						surname = "selecto";
					}
					else if(luna.isInvertida()) {
						surname = "invertido";
					}
						
				}
				else if(!luna.getDate().toLocalDate().isBefore(lastSOE.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(nextSOE.getDate().toLocalDate())){							
							
					lunasNuevasEntreLastSOEYNextSOE.add(luna);						
				}


				if(luna.getDate().toLocalDate().isBefore(date)) {
					
					long diasDeDiferenciaEntreLNAnteriorYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
					
					if(diasDeDiferenciaEntreLNAnteriorYDate < numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate) {
						
						numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = diasDeDiferenciaEntreLNAnteriorYDate;
						lunaNuevaAnteriorMasCercanaALaFecha = luna;
					}			
				}
				else if(luna.getDate().toLocalDate().isAfter(date)) {
				
					long diasDeDiferenciaEntreLNPosteriorYDate = ChronoUnit.DAYS.between( date, luna.getDate().toLocalDate());
					
					if(diasDeDiferenciaEntreLNPosteriorYDate < numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate) {
						
						numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate = diasDeDiferenciaEntreLNPosteriorYDate;
						lunaNuevaPosteriorMasCercanaALaFecha = luna;
					}		
				}
			}
			
			if(caeEnLunaNueva) {
				month.setName("-");
			}
			else {
				LunasEntity lunaLlenaAnteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
				LunasEntity lunaLlenaPosteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
				Long numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate = Long.MAX_VALUE;	
				Long numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate = Long.MAX_VALUE;	
				boolean caeEnLunaLlena = false;  // Ya tendra utilidad
				
				for(int i = 0; i<lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.size(); i++) {
					
					LunasEntity luna = lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.get(i);
					
					if(luna.getDate().toLocalDate().isBefore(date)) {
						
						long diasDeDiferenciaEntreLLAnteriorYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
						
						if(diasDeDiferenciaEntreLLAnteriorYDate < numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate) {
							
							numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate = diasDeDiferenciaEntreLLAnteriorYDate;
							lunaLlenaAnteriorMasCercanaALaFecha = luna;
						}		
					}
					else if(luna.getDate().toLocalDate().isAfter(date)) {
						long diasDeDiferenciaEntreLLPosteriorYDate = ChronoUnit.DAYS.between(date, luna.getDate().toLocalDate());
						
						if(diasDeDiferenciaEntreLLPosteriorYDate < numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate) {
							
							numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate = diasDeDiferenciaEntreLLPosteriorYDate;
							lunaLlenaPosteriorMasCercanaALaFecha = luna;
						}		
					}
					else if(luna.getDate().toLocalDate().isEqual(date)) {
						caeEnLunaLlena = true;
					}
					
				}
				
				
				MonthsEntity vauMonth = new MonthsEntity();
				// Si cae en soe, pertenece al mes hibrido de ese soe.
				// A no ser que sea luna nueva, en ese caso seria el mes siguiente
				if(caeEnSOE) {

					if(caeEnLunaNueva) {
						
						// Basicamente si hay un metono (da igual el tipo)
						MonthDTO monthIfLN = getVAUMonth(date.plusDays(1), soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasDesdeElAnyoAnteriorHastaElSiguiente);
						vauMonth.setName(monthIfLN.getName());
						
					}
					else {
						vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);
					}
					

				}
				else{
						
					// Si no cae en SOE, hay que calcular cuantas lunas nuevas han pasado desde el lastSOE hasta la fecha a consultar
					// Tambien obtenemos la luna nueva anterior al nextSOE y la luna nueva posterior al lastSOE
					int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
					
					long diasMinimosDeDiferenciaLunaNuevaConNextSOE = Long.MAX_VALUE;
					LunasEntity lastLNBeforeNextSOE = null;
					
					long diasMinimosDeDiferenciaLunaNuevaConLastSOE = Long.MAX_VALUE;
					LunasEntity firstLNAfterLastSOE = null;
						
					for(LunasEntity luna : lunasNuevasEntreLastSOEYNextSOE) {
							
						long diasDeDiferenciaEntreNextSOEYLN = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), nextSOE.getDate().toLocalDate());
						long diasDeDiferenciaEntreLastSOEYLN = ChronoUnit.DAYS.between(lastSOE.getDate().toLocalDate(), luna.getDate().toLocalDate());
							
						if(diasDeDiferenciaEntreNextSOEYLN < diasMinimosDeDiferenciaLunaNuevaConNextSOE) {
								
							lastLNBeforeNextSOE=luna;
							diasMinimosDeDiferenciaLunaNuevaConNextSOE = diasDeDiferenciaEntreNextSOEYLN;
								
						}
						
						if(diasDeDiferenciaEntreLastSOEYLN < diasMinimosDeDiferenciaLunaNuevaConLastSOE) {
							
							firstLNAfterLastSOE=luna;
							diasMinimosDeDiferenciaLunaNuevaConLastSOE = diasDeDiferenciaEntreLastSOEYLN;
								
						}
							
						if(date.isAfter(luna.getDate().toLocalDate())) {
								
							lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO+1;						
						}
					}
						
					
						
					if(lastLNBeforeNextSOE != null || firstLNAfterLastSOE != null) {
						
						// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
						if(date.isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && date.isBefore(nextSOE.getDate().toLocalDate())) {
			
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(nextSOE.getStartingSeason(), 0, false);

						}
						// Si la fecha a consultar esta entre el lastSOE y la primera luna, pertenece al mes hibrido de ese soe.
						// Pero si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
						// A no ser que sea luna nueva, que en ese caso será Prierno
						else if (date.isBefore(firstLNAfterLastSOE.getDate().toLocalDate()) && date.isAfter(lastSOE.getDate().toLocalDate())) {						

							if(lastSOE.isSolsticioInvierno()) {						
		
								if(caeEnLunaNueva) {
									
									vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO+1, false);
								}
								else {
									
									vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);
								}
							}
							else {
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);
							}
									
						
						}
						else {											

							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, false);			
						}
						
						month.setName(vauMonth.getName());
						if(lunaNuevaAnteriorMasCercanaALaFecha.isSelecta()) {
							
							month.setSurname("selecto");
						}
						else if(lunaNuevaAnteriorMasCercanaALaFecha.isInvertida()) {
							
							month.setSurname("invertido");
						}	
					}
					else {
						System.out.println("Error, no hay lastLNBeforeNextSOE o firstLNAfterLastSOE.");
					}
										
				}			
				
				
			}

			month.setNewMoon(caeEnLunaNueva);	
			
		}
		else {
			
			System.out.println("Error, no se han encontrado nextSOE y/o lastSOE.");
		}
		

		return month;
	}
	

	public String poblateMonths() {
		
		System.out.println("Actualizando los Meses.");

		String resultado = "Meses actualizados correctamente.";
		
		List<MonthsEntity> allMonths = this.monthsRepository.findAll();
		
		if(allMonths.isEmpty()) {
			
			List<MonthsEntity> monthsParaDDB = new ArrayList<>();
			
			monthsParaDDB.add(this.createMonth("Prierno", false, 1, 1, false));
			monthsParaDDB.add(this.createMonth("Seguerno", false, 2, 1, false));
			monthsParaDDB.add(this.createMonth("Terno", false, 3, 1, false));
			monthsParaDDB.add(this.createMonth("Pinera", false, 1, 2, false));
			monthsParaDDB.add(this.createMonth("Seguera", false, 2, 2, false));
			monthsParaDDB.add(this.createMonth("Tera", false, 3, 2, false));
			monthsParaDDB.add(this.createMonth("Prano", false, 1, 3, false));
			monthsParaDDB.add(this.createMonth("Segano", false, 2, 3, false));
			monthsParaDDB.add(this.createMonth("Tano", false, 3, 3, false));
			monthsParaDDB.add(this.createMonth("Pridor", false, 1, 4, false));
			monthsParaDDB.add(this.createMonth("Sedor", false, 2, 4, false));
			monthsParaDDB.add(this.createMonth("Tor", false, 3, 4, false));
			monthsParaDDB.add(this.createMonth("Invera", true, 0, 2, false));
			monthsParaDDB.add(this.createMonth("Primano", true, 0, 3, false));
			monthsParaDDB.add(this.createMonth("Verdor", true, 0, 4, false));
			monthsParaDDB.add(this.createMonth("Oterno", true, 0, 1, false));
			monthsParaDDB.add(this.createMonth("Oterno liminal", true, 0, 1, true));
			monthsParaDDB.add(this.createMonth("Nomon", false, 0, 0, false));
			
			
			this.monthsRepository.saveAll(monthsParaDDB);
			
		}
		else {
			System.out.println("Ya hay meses en la base de datos.");
			resultado = "Error al actualizar los meses: ya hay meses en la base de datos.";
		}
		System.out.println("Meses actualizados");
		return resultado;
	}
	
	private MonthsEntity createMonth(String name, boolean hibrid, int monthOfSeason, int season, boolean liminal) {
		
		MonthsEntity newMonth = new MonthsEntity();
		newMonth.setName(name);
		newMonth.setHibrid(hibrid);
		newMonth.setMonthOfSeason(monthOfSeason);
		newMonth.setSeason(season);
		newMonth.setLiminal(liminal);
		
		return newMonth;
	}


	
}
