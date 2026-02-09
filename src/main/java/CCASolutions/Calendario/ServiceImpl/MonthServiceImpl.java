package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Services.MonthService;

@Service
public class MonthServiceImpl implements MonthService{

	public int getSeasonDelMesHibridoSiLoEs (List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, SolsticiosYEquinocciosEntity lastSOE, SolsticiosYEquinocciosEntity nextSOE, LocalDateTime dateO) {

		int season = 0;
		
		LunasEntity lastLP = new LunasEntity();
		Long diasLastLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		LunasEntity nextLP = new LunasEntity();
		Long diasNextLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasNextLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		for(LunasEntity luna : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {
			
			if(luna.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
				
				diasLastLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(luna.getDate(), dateO);
				
				if(diasLastLPDeDiferenciaConLaFechaO < diasLastLPConMenorDiferenciaConLaFechaO) {
					
					lastLP.setDate(luna.getDate());
					lastLP.setNueva(luna.isNueva());
					lastLP.setCuartoCreciente(luna.isCuartoCreciente());
					lastLP.setLlena(luna.isLlena());
					lastLP.setCuartoMenguante(luna.isCuartoMenguante());
					diasLastLPConMenorDiferenciaConLaFechaO = diasLastLPDeDiferenciaConLaFechaO;

				}	
			}
			else if(luna.getDate().toLocalDate().isAfter(dateO.toLocalDate())) {
				
				diasNextLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(dateO, luna.getDate());
				
				if(diasNextLPDeDiferenciaConLaFechaO < diasNextLPConMenorDiferenciaConLaFechaO) {
					
					nextLP.setDate(luna.getDate());
					nextLP.setNueva(luna.isNueva());
					nextLP.setCuartoCreciente(luna.isCuartoCreciente());
					nextLP.setLlena(luna.isLlena());
					nextLP.setCuartoMenguante(luna.isCuartoMenguante());
					diasNextLPConMenorDiferenciaConLaFechaO = diasNextLPDeDiferenciaConLaFechaO;

				}	
			}
		}
		
		if(lastLP.getDate() == null) {
			
			if(lastSOE.isSolsticioInvierno()) {
				
				season = 1;
			}
			else if(lastSOE.isEquinoccioPrimavera()) {
				
				season = 2;
			}
			else if(lastSOE.isSolsticioVerano()) {
				
				season = 3;
			}
			else if(lastSOE.isEquinoccioOtonyo()) {
				
				season = 4;
			}
		}
		else if (nextLP.getDate() == null) {
			
			if(nextSOE.isSolsticioInvierno()) {
				
				season = 1;
			}
			else if(nextSOE.isEquinoccioPrimavera()) {
				
				season = 2;
			}
			else if(nextSOE.isSolsticioVerano()) {
				
				season = 3;
			}
			else if(nextSOE.isEquinoccioOtonyo()) {
				
				season = 4;
			}
		}
		
		return season;
	}
	
	
}
