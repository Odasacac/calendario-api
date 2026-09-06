package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.LunasEntity;

/**
 * EN: Moon phases from year 1 to 2100: new moon, first quarter, full moon and last
 * quarter, each flagged as "selecta" or "invertida" when it coincides with an apogee or
 * perigee.
 * ES: Fases lunares del año 1 al 2100: luna nueva, cuarto creciente, luna llena y cuarto
 * menguante, cada una marcada como "selecta" o "invertida" cuando coincide con un apogeo
 * o un perigeo.
 */
public interface LunasRepository extends JpaRepository <LunasEntity, Long> {

	/**
	 * EN: First new moon on or after the given date.
	 * ES: Primera luna nueva en la fecha dada o posterior.
	 */
	public abstract LunasEntity findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Every moon phase between the two dates, both ends included.
	 * ES: Todas las fases lunares entre las dos fechas, ambos extremos incluidos.
	 */
	public abstract List<LunasEntity> findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: One moon phase by its identifier.
	 * ES: Una fase lunar por su identificador.
	 */
	public abstract Optional<LunasEntity> findById (Long id);

	/**
	 * EN: Latest moon phase stored, whatever its type.
	 * ES: Última fase lunar almacenada, sea del tipo que sea.
	 */
	public abstract LunasEntity findTopByOrderByDateDesc();

	/**
	 * EN: Last new moon strictly before the given date.
	 * ES: Última luna nueva estrictamente anterior a la fecha dada.
	 */
	public abstract LunasEntity findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: Every new moon between the two dates.
	 * ES: Todas las lunas nuevas entre las dos fechas.
	 */
	public abstract List<LunasEntity> findByDateBetweenAndNuevaTrue(LocalDateTime yearFrom, LocalDateTime yearTo);

	/**
	 * EN: Every new moon at apogee (the aponovos) between the two dates. Used to count the
	 * aponovos without loading the whole moon-phase table.
	 * ES: Todas las lunas nuevas en apogeo (los aponovos) entre las dos fechas. Sirve para
	 * contar los aponovos sin cargar la tabla entera de fases lunares.
	 */
	public abstract List<LunasEntity> findByDateBetweenAndNuevaTrueAndSelectaTrue(LocalDateTime desde, LocalDateTime hasta);

	/**
	 * EN: How many new moons fall between the two dates. Resolved on the (nueva, date)
	 * index, so it never brings the rows back into memory.
	 * ES: Cuántas lunas nuevas caen entre las dos fechas. Se resuelve sobre el índice
	 * (nueva, date), de modo que no trae las filas a memoria.
	 */
	public abstract long countByDateBetweenAndNuevaTrue(LocalDateTime desde, LocalDateTime hasta);

	/**
	 * EN: Every new moon between the two dates, oldest first.
	 * ES: Todas las lunas nuevas entre las dos fechas, de la más antigua a la más reciente.
	 */
	public abstract List<LunasEntity> findByDateBetweenAndNuevaTrueOrderByDateAsc(LocalDateTime yearFrom, LocalDateTime yearTo);

	/**
	 * EN: Every new moon of the years in the given range.
	 * ES: Todas las lunas nuevas de los años del rango dado.
	 */
	public abstract List<LunasEntity> findByYearBetweenAndNuevaTrue (int from, int to);

	/**
	 * EN: The next three new moons on or after the given date.
	 * ES: Las tres siguientes lunas nuevas en la fecha dada o posteriores.
	 */
	public abstract List<LunasEntity> findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First moon phase after the given date, whatever its type.
	 * ES: Primera fase lunar posterior a la fecha dada, sea del tipo que sea.
	 */
	public abstract LunasEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Last moon phase before the given date, whatever its type.
	 * ES: Última fase lunar anterior a la fecha dada, sea del tipo que sea.
	 */
	public abstract LunasEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: Full moon happening at exactly the given instant.
	 * ES: Luna llena que ocurre exactamente en el instante dado.
	 */
	public abstract LunasEntity findFirstByDateAndLlenaIsTrue(LocalDateTime start);

	/**
	 * EN: New moon happening at exactly the given instant.
	 * ES: Luna nueva que ocurre exactamente en el instante dado.
	 */
	public abstract LunasEntity findFirstByDateAndNuevaIsTrue(LocalDateTime start);

	/**
	 * EN: Some full moon before the given date; no ordering is applied.
	 * ES: Alguna luna llena anterior a la fecha dada; no se aplica ordenación.
	 */
	public abstract LunasEntity findFirstByDateBeforeAndLlenaIsTrue(LocalDateTime start);

	/**
	 * EN: Some new moon before the given date; no ordering is applied.
	 * ES: Alguna luna nueva anterior a la fecha dada; no se aplica ordenación.
	 */
	public abstract LunasEntity findFirstByDateBeforeAndNuevaIsTrue(LocalDateTime start);

	/**
	 * EN: Last full moon before the given date.
	 * ES: Última luna llena anterior a la fecha dada.
	 */
	public abstract LunasEntity findFirstByDateBeforeAndLlenaIsTrueOrderByDateDesc(LocalDateTime start);

	/**
	 * EN: Last new moon before the given date.
	 * ES: Última luna nueva anterior a la fecha dada.
	 */
	public abstract LunasEntity findFirstByDateBeforeAndNuevaIsTrueOrderByDateDesc (LocalDateTime start);

	/**
	 * EN: Some full moon after the given date; no ordering is applied.
	 * ES: Alguna luna llena posterior a la fecha dada; no se aplica ordenación.
	 */
	public abstract LunasEntity findFirstByDateAfterAndLlenaIsTrue(LocalDateTime start);

	/**
	 * EN: Some new moon after the given date; no ordering is applied.
	 * ES: Alguna luna nueva posterior a la fecha dada; no se aplica ordenación.
	 */
	public abstract LunasEntity findFirstByDateAfterAndNuevaIsTrue(LocalDateTime start);


	/**
	 * EN: New and full moons between two dates, which are the only two phases that can
	 * turn a solstice or equinox into a meton.
	 * ES: Lunas nuevas y llenas entre dos fechas, que son las dos únicas fases capaces de
	 * convertir un solsticio o equinoccio en un métono.
	 *
	 * @param startDate EN: lower bound, included. / ES: cota inferior, incluida.
	 * @param endDate   EN: upper bound, included. / ES: cota superior, incluida.
	 * @return EN: the matching phases. / ES: las fases que encajan.
	 */
	@Query("""
		    SELECT l
		    FROM LunasEntity l
		    WHERE l.date BETWEEN :startDate AND :endDate
		    AND (l.nueva = true OR l.llena = true)
		""")
		List<LunasEntity> findLunasNuevasOLlenasEntreFechas(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate
		);


}
