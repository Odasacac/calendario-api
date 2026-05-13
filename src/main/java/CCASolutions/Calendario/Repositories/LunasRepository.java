package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasRepository extends JpaRepository <LunasEntity, Long> {
	
	public abstract LunasEntity findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);
	
	public abstract LunasEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	
	public abstract LunasEntity findTopByOrderByDateDesc();
	
	public abstract LunasEntity findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(LocalDateTime date);
	
	public abstract List<LunasEntity> findByDateBetweenAndNuevaTrue(LocalDateTime yearFrom, LocalDateTime yearTo);
	
	public abstract List<LunasEntity> findByYearBetweenAndNuevaTrue (int from, int to);
	
	public abstract List<LunasEntity> findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);
	
	public abstract LunasEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
	
	public abstract LunasEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);
	
	public abstract LunasEntity findFirstByDateAndLlenaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findFirstByDateAndNuevaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findFirstByDateBeforeAndLlenaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findFirstByDateBeforeAndNuevaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findFirstByDateAfterAndLlenaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findFirstByDateAfterAndNuevaIsTrue(LocalDateTime start);
	
	public abstract LunasEntity findByDateBetweenAndNuevaIsTrueAndLlenaIsTrue(LocalDateTime yearFrom, LocalDateTime yearTo);

}
