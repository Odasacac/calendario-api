package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasRepository extends JpaRepository <LunasEntity, Long> {
	
	public abstract LunasEntity findTopByOrderByDateDesc();
	
	public abstract List<LunasEntity> findByDateBetweenAndNuevaTrue(LocalDateTime yearFrom, LocalDateTime yearTo);
	
	public abstract List<LunasEntity> findByYearBetweenAndNuevaTrue (int from, int to);

}
