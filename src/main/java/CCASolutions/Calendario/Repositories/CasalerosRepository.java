package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.CasalerosEntity;

/**
 * EN: Casaleros: one per eclipeno, naming it after the first phenomenon that happens once
 * the eclipeno has passed.
 * ES: Casaleros: uno por cada eclípeno, que lo bautiza según el primer fenómeno que ocurre
 * una vez pasado ese eclípeno.
 */
public interface CasalerosRepository extends JpaRepository <CasalerosEntity, Long>{

	/**
	 * EN: The casalero belonging to one eclipeno.
	 * ES: El casalero que pertenece a un eclípeno.
	 */
	CasalerosEntity findByEclipenoId(Long id);
}
