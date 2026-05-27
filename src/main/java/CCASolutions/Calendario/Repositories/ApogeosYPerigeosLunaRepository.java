package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;

public interface ApogeosYPerigeosLunaRepository extends JpaRepository <ApogeosYPerigeosLunaEntity, Long>{

	 ApogeosYPerigeosLunaEntity findTopByDateLessThanEqualOrderByDateDesc(LocalDateTime date);
}
