package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasRepository extends JpaRepository <LunasEntity, Long> {
	
	LunasEntity findFirstByDateBeforeAndNuevaTrueOrderByDateDesc(LocalDateTime date);
	LunasEntity findTopByOrderByDateDesc();
	List<LunasEntity> findByYearOrderByDateAsc(int year);
	List<LunasEntity> findTop4ByDateAfterAndNuevaIsTrueOrderByDateAsc(LocalDateTime fecha);

}
