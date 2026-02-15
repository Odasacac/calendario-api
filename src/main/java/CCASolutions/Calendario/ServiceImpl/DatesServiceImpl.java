package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
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
			List<SolsticiosYEquinocciosEntity> soesDesdeElMetono = this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(lastMeton.getDate(), dateO.plusYears(1));
			
			// Y todas las lunas, desde un año antes hasta un año despues de la fecha a consultar			
			List<LunasEntity> lunasDesdeElAnyoAnteriorHasElSiguiente = this.lunasRepository.findByDateBetween(dateO.minusYears(1), dateO.plusYears(1));
		
			
			if(soesDesdeElMetono.isEmpty() || lunasDesdeElAnyoAnteriorHasElSiguiente.isEmpty()) {
				
				System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas.");
			}
			else {
				
				// Lo primero es obtener el añoVAU				
				dateVAU.setYear(this.getVAUYear(dateO, soesDesdeElMetono));
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
	


	private String getVAUYear(LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauYear = "-";
		
		boolean caeEnSolsticioDeInvierno=false;
		
		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		
		int year = 0;
		
		for(int i = 0; i<solsticiosYEquinocciosDesdeElMetono.size() && !caeEnSolsticioDeInvierno; i++) {
			
			SolsticiosYEquinocciosEntity soe = solsticiosYEquinocciosDesdeElMetono.get(i);
			
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
			
			vauYear = "No pertenece a ningún año, es el día del solsticio de invierno";
		}
		else {
			vauYear = String.valueOf(year);
		}
		
		return vauYear;
		
	}
	
	
	
	private String getVAUMonth(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyoYDelAnterior) {
		
		String vauMonth = "-";
		

		return vauMonth;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		
		return vauWeekAndDay;
	}



	
}







