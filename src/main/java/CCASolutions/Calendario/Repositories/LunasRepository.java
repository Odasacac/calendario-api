package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasRepository extends JpaRepository <LunasEntity, Long> {
	
	//getNewMoonBeforeADate
	LunasEntity findFirstByDateBeforeAndNuevaTrueOrderByDateDesc(LocalDateTime date);
	
	//getNewMoonAfterADate
	LunasEntity findFirstByDateAfterAndNuevaTrueOrderByDateAsc(LocalDateTime date);
	
	LunasEntity findTopByOrderByDateDesc();

}
