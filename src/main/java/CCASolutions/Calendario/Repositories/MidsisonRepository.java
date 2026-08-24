package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MidsisonEntity;

public interface MidsisonRepository extends JpaRepository<MidsisonEntity, Long>{
	List<MidsisonEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
