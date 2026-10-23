package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.CasalerosEntity;

public interface CasalerosRepository extends JpaRepository <CasalerosEntity, Long>{

	CasalerosEntity findByEclipenoId(Long id);
	List<CasalerosEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
