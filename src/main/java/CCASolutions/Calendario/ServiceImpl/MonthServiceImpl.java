package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Services.MonthService;

@Service
public class MonthServiceImpl implements MonthService{

	public int getSeasonDelMesHibridoSiLoEs (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, FenomenoDTO nextSOE, LocalDateTime dateO) {

		int season = 0;
		
		LunarPhaseDTO lastLP = new LunarPhaseDTO();
		Long diasLastLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		LunarPhaseDTO nextLP = new LunarPhaseDTO();
		Long diasNextLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasNextLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		for(LunarPhaseDTO luna : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {
			
			if(luna.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
				
				diasLastLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(luna.getDate(), dateO);
				
				if(diasLastLPDeDiferenciaConLaFechaO < diasLastLPConMenorDiferenciaConLaFechaO) {
					
					lastLP.setDate(luna.getDate());
					lastLP.setMoonPhase(luna.getMoonPhase());
					diasLastLPConMenorDiferenciaConLaFechaO = diasLastLPDeDiferenciaConLaFechaO;

				}	
			}
			else if(luna.getDate().toLocalDate().isAfter(dateO.toLocalDate())) {
				
				diasNextLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(dateO, luna.getDate());
				
				if(diasNextLPDeDiferenciaConLaFechaO < diasNextLPConMenorDiferenciaConLaFechaO) {
					
					nextLP.setDate(luna.getDate());
					nextLP.setMoonPhase(luna.getMoonPhase());
					diasNextLPConMenorDiferenciaConLaFechaO = diasNextLPDeDiferenciaConLaFechaO;

				}	
			}
		}
		
		if(lastLP.getDate() == null) {
			
			switch (lastSOE.getPhenomena()) {
			
				case "VernalEquinox":
					season = 2;
					break;
				case "SummerSolstice":
					season = 3;
					break;
				case "AutumnalEquinox":
					season = 4;
					break;
				case "WinterSolstice":
					season = 1;
					break;
			}
		}
		else if (nextLP.getDate() == null) {
			
			switch (nextSOE.getPhenomena()) {
			
				case "VernalEquinox":
					season = 2;
					break;
				case "SummerSolstice":
					season = 3;
					break;
				case "AutumnalEquinox":
					season = 4;
					break;
				case "WinterSolstice":
					season = 1;
					break;
			}
		}
		
		return season;
	}
	
	
}
