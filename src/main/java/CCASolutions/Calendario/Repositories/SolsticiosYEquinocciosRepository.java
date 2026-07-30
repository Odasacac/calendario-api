package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/*
 * ==============================================================================
 * EN: Solstices and equinoxes (~8.400 rows).
 *
 *     The window the read path asks for is naturally small (a dozen years at most,
 *     so a few dozen rows), so it stays a database query - but it now has an index
 *     behind it and an explicit ORDER BY.
 *
 *     The ordering matters: several calculators pick "the nearest solstice in either
 *     direction", and a date exactly halfway between two of them is a genuine tie
 *     that the original code broke by relying on the order MySQL happened to
 *     return. Sorting by date ascending makes that deterministic.
 *
 *     Eight unused derived queries were removed.
 *
 * ES: Solsticios y equinoccios (~8.400 filas).
 *
 *     La ventana que pide el camino de lectura es pequena por naturaleza (una docena de
 *     anos como maximo, o sea unas pocas decenas de filas), asi que sigue siendo una
 *     consulta a base de datos - pero ahora tiene un indice detras y un ORDER BY
 *     explicito.
 *
 *     El orden importa: varios calculadores eligen "el solsticio mas cercano en
 *     cualquier direccion", y una fecha exactamente a mitad de camino entre dos de ellos
 *     es un empate real que el codigo original resolvia confiando en el orden que
 *     devolviera MySQL. Ordenar por fecha ascendente lo hace determinista.
 *
 *     Se han eliminado ocho consultas derivadas sin uso.
 * ==============================================================================
 */
public interface SolsticiosYEquinocciosRepository extends JpaRepository<SolsticiosYEquinocciosEntity, Long> {

	/*
	 * EN: Solstices and equinoxes in (desde, hasta], oldest first.
	 * ES: Solsticios y equinoccios en (desde, hasta], del mas antiguo al mas nuevo.
	 */
	List<SolsticiosYEquinocciosEntity> findByDateAfterAndDateLessThanEqualOrderByDateAsc(LocalDateTime desde,
			LocalDateTime hasta);
}
