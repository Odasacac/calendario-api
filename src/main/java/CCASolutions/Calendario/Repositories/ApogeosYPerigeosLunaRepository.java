package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;

public interface ApogeosYPerigeosLunaRepository extends JpaRepository <ApogeosYPerigeosLunaEntity, Long>{

	List<ApogeosYPerigeosLunaEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
