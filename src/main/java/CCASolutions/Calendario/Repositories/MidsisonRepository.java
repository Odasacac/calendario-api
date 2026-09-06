package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MidsisonEntity;

/**
 * EN: Midsisons: the instant exactly halfway between one solstice or equinox and the
 * next. There is one per pair, roughly 8.400 rows in the whole range.
 * ES: Midsisons: el instante exactamente equidistante entre un solsticio o equinoccio y el
 * siguiente. Hay uno por pareja, unas 8.400 filas en todo el rango.
 */
public interface MidsisonRepository extends JpaRepository<MidsisonEntity, Long>{

	/**
	 * EN: Every midsison between the two dates, both ends included.
	 * ES: Todos los midsisons entre las dos fechas, ambos extremos incluidos.
	 */
	List<MidsisonEntity> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
