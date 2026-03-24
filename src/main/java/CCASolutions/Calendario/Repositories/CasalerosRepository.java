package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.CasalerosEntity;

public interface CasalerosRepository extends JpaRepository <CasalerosEntity, Long>{

	CasalerosEntity findByEclipenoId(Long id);
}
