package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;

/**
 * EN: Apogees and perigees of the moon (the "apoperis"): the moments when it is farthest
 * from and closest to the Earth, together with the distance in kilometres.
 * ES: Apogeos y perigeos de la luna (los "apoperis"): los momentos en que está más lejos y
 * más cerca de la Tierra, junto con la distancia en kilómetros.
 */
public interface ApogeosYPerigeosLunaRepository extends JpaRepository <ApogeosYPerigeosLunaEntity, Long>{

	/**
	 * EN: Every apogee and perigee between the two dates, both ends included.
	 * ES: Todos los apogeos y perigeos entre las dos fechas, ambos extremos incluidos.
	 */
	List<ApogeosYPerigeosLunaEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);

	/**
	 * EN: One apogee or perigee by its identifier.
	 * ES: Un apogeo o perigeo por su identificador.
	 */
	Optional<ApogeosYPerigeosLunaEntity> findById(Long id);
}
