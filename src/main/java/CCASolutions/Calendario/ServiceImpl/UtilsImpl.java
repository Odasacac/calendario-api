package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.Utils;

@Service
public class UtilsImpl implements Utils{
	
	@Autowired
	MetonsRepository metonsRepository;

	public LocalDateTime getLastMeton(LocalDateTime dateO) {
		LocalDateTime lastMeton = null;
		
		try {
			Optional<MetonsEntity> lastMetonOpt= this.metonsRepository.findFirstByDateLessThanEqualAndNuevoTrueAndSolsticialTrueAndInicialTrueOrderByDateDesc(dateO);
			
			if(lastMetonOpt.isPresent()) {
				lastMeton = lastMetonOpt.get().getDate();
			}
		}
		catch (Exception e) {
			System.out.println("Error getting last meton: " + e);
		}
		return lastMeton;
	}
}
