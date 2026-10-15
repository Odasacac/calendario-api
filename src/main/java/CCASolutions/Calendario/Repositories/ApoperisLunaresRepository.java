package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.ApoperisLunaresEntity;

public interface ApoperisLunaresRepository extends JpaRepository <ApoperisLunaresEntity, Long>{

	List<ApoperisLunaresEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);
	Optional<ApoperisLunaresEntity> findById(Long id);
}
