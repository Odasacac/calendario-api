package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import CCASolutions.Calendario.Entities.EclipsesEntity;

/**
 * EN: Solar and lunar eclipses from year 1 to 2100, each flagged with its type: annular,
 * total, partial, penumbral or hybrid.
 * ES: Eclipses solares y lunares del año 1 al 2100, cada uno marcado con su tipo: anular,
 * total, parcial, penumbral o híbrido.
 */
public interface EclipsesRepository extends JpaRepository <EclipsesEntity, Long>{

	/**
	 * EN: One eclipse falling between the two dates.
	 * ES: Un eclipse que caiga entre las dos fechas.
	 */
	EclipsesEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: Every eclipse of one year.
	 * ES: Todos los eclipses de un año.
	 */
	public abstract List<EclipsesEntity> findByYear(int year);

	/**
	 * EN: Eclipses between two dates that are neither partial nor penumbral, that is, the
	 * ones the calendar counts as absolute.
	 * ES: Eclipses entre dos fechas que no son ni parciales ni penumbrales, es decir, los
	 * que el calendario cuenta como absolutos.
	 */
	List<EclipsesEntity> findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: First absolute eclipse after the given date. Used when building the casaleros.
	 * ES: Primer eclipse absoluto posterior a la fecha dada. Se usa al construir los casaleros.
	 */
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First absolute lunar eclipse after the given date.
	 * ES: Primer eclipse lunar absoluto posterior a la fecha dada.
	 */
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeLunaIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First absolute solar eclipse after the given date.
	 * ES: Primer eclipse solar absoluto posterior a la fecha dada.
	 */
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeSolIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First eclipse after the given date, whatever its type.
	 * ES: Primer eclipse posterior a la fecha dada, sea del tipo que sea.
	 */
	EclipsesEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Last eclipse before the given date, whatever its type.
	 * ES: Último eclipse anterior a la fecha dada, sea del tipo que sea.
	 */
	EclipsesEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: Absolute eclipses between two dates: every solar one, plus the lunar ones that
	 * are total. These are the eclipses the VAU counters take into account.
	 * ES: Eclipses absolutos entre dos fechas: todos los solares, más los lunares que sean
	 * totales. Son los eclipses que tienen en cuenta los contadores VAU.
	 *
	 * @param desde EN: lower bound, included. / ES: cota inferior, incluida.
	 * @param hasta EN: upper bound, included. / ES: cota superior, incluida.
	 * @return EN: the matching eclipses. / ES: los eclipses que encajan.
	 */
	@Query("""
		    SELECT e
		    FROM EclipsesEntity e
		    WHERE e.date BETWEEN :desde AND :hasta
		      AND (
		          e.deSol = true
		          OR (e.deLuna = true AND e.esTotal = true)
		      )
		""")
		List<EclipsesEntity> findEclipsesAbsoluteQuery(
		    LocalDateTime desde,
		    LocalDateTime hasta
		);
}
