package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;

/*
 * ==============================================================================
 * EN: Lunar apogees and perigees (~29.200 rows).
 *
 *     Only ever read as a narrow window (three months either side of the requested
 *     date, so about a dozen rows), which was already efficient in shape - but
 *     without an index it still scanned all 29.200 rows. The index added to the
 *     entity turns it into a range scan; the explicit ORDER BY makes the
 *     "nearest apogee" tie-breaks deterministic.
 *
 *     The redundant findById declaration was dropped: JpaRepository already
 *     provides it.
 *
 * ES: Apogeos y perigeos lunares (~29.200 filas).
 *
 *     Solo se leen como una ventana estrecha (tres meses a cada lado de la fecha
 *     consultada, o sea una docena de filas), lo cual ya tenia buena forma - pero sin
 *     indice seguia recorriendo las 29.200 filas. El indice anadido a la entidad lo
 *     convierte en un recorrido de rango; el ORDER BY explicito hace deterministas los
 *     desempates de "apogeo mas cercano".
 *
 *     Se ha eliminado la declaracion redundante de findById: JpaRepository ya la
 *     proporciona.
 * ==============================================================================
 */
public interface ApogeosYPerigeosLunaRepository extends JpaRepository<ApogeosYPerigeosLunaEntity, Long> {

	/*
	 * EN: Apogees and perigees in [desde, hasta], oldest first.
	 * ES: Apogeos y perigeos en [desde, hasta], del mas antiguo al mas nuevo.
	 */
	List<ApogeosYPerigeosLunaEntity> findByDateBetweenOrderByDateAsc(LocalDateTime desde, LocalDateTime hasta);
}
