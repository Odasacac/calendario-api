package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class DatesServiceImpl implements DatesService {


	
	

	public LocalDateTime getDateOFromDateVAU(DateDTOFromDB dateVAU) {

		LocalDateTime dateO = LocalDateTime.now();
		
		if(dateVAU.getMonth().getHibrid()) {
		
			dateO=this.getDateOIfHibrid(dateVAU);
		}
		else {
			
			dateO=this.getDateOIfNoHibrid(dateVAU);

		}
		
		return dateO;
	}

	
	public DateDTO getDateVAUFromDateO (LocalDateTime dateO) {
		
		DateDTO dateVAU = new DateDTO();
		
	
		
		return dateVAU;
		
	}
	
	public DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU) {

		DateDTOFromDB dateVAUDTOFromDB = new DateDTOFromDB();
	
		
		return dateVAUDTOFromDB;
	}


	
	
	// ========================= METODOS PRIVADOS
	

	
	private LocalDateTime getDateOIfHibrid(DateDTOFromDB dateVAU) {
		
		LocalDateTime dateO = LocalDateTime.now();
				
		
		
		return dateO;
	}
	
	
	private LocalDateTime getDateOIfOterno(DateDTOFromDB dateVAU) {
		
		LocalDateTime dateO = LocalDateTime.now();
		
	
		
		return dateO;
	}
	
	
	private LocalDateTime getDateOIfHibridButNoOterno(DateDTOFromDB dateVAU) {
		
		LocalDateTime dateO = LocalDateTime.now();
		
		
		return dateO;
	}
	
	
	
	
	private LocalDateTime getDateOIfNoHibrid (DateDTOFromDB dateVAU) {
		
		LocalDateTime dateO = LocalDateTime.now();
		
		
		
		return dateO;
	}
	
	private String getVAUYear(LocalDateTime dateO, LocalDateTime dateLastMeton, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauYear = "-";
		
		
		
		return vauYear;
		
	}
	
	
	
	private String getVAUMonth(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauMonth = "-";
		
		
		
		
		return vauMonth;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		
		return vauWeekAndDay;
	}



	
}







