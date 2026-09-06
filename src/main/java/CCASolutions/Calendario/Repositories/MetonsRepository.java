package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

/**
 * EN: Metons: solstices or equinoxes coinciding, within one sidereal day, with a moon
 * phase (fasal) or with an apogee or perigee (apoperico). A meton always carries the date
 * of its solstice or equinox.
 * ES: Métonos: solsticios o equinoccios que coinciden, dentro de un día sideral, con una
 * fase lunar (fasal) o con un apogeo o perigeo (apopérico). Un métono lleva siempre la
 * fecha de su solsticio o equinoccio.
 */
public interface MetonsRepository extends JpaRepository <MetonsEntity, Long> {

	/**
	 * EN: One meton falling between the two dates.
	 * ES: Un métono que caiga entre las dos fechas.
	 */
	MetonsEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: Every meton of one year.
	 * ES: Todos los métonos de un año.
	 */
	List<MetonsEntity> findByYear(int year);

	/**
	 * EN: Winter new meton of one year.
	 * ES: Métono invernal nuevo de un año.
	 */
	MetonsEntity findByYearAndInvernalIsTrueAndNuevoIsTrue(int year);

	/**
	 * EN: Most recent winter new meton on or before the given date.
	 * ES: Métono invernal nuevo más reciente en la fecha dada o anterior.
	 */
	MetonsEntity findTopByDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime fecha);

	/**
	 * EN: First winter new meton of a year later than the given one.
	 * ES: Primer métono invernal nuevo de un año posterior al dado.
	 */
	MetonsEntity findFirstByYearGreaterThanAndInvernalIsTrueAndNuevoIsTrueOrderByYearAsc(int year);

	/**
	 * EN: Winter new metons between two dates, most recent first.
	 * ES: Métonos invernales nuevos entre dos fechas, del más reciente al más antiguo.
	 */
	List<MetonsEntity> findByDateBetweenAndInvernalIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime from, LocalDateTime to);

	/**
	 * EN: Winter new metons from the given year onwards, oldest first.
	 * ES: Métonos invernales nuevos desde el año dado en adelante, del más antiguo al más reciente.
	 */
	List<MetonsEntity> findByYearGreaterThanEqualAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(int year);

	/**
	 * EN: Every winter new meton in the database.
	 * ES: Todos los métonos invernales nuevos de la base de datos.
	 */
	List<MetonsEntity> findAllByInvernalIsTrueAndNuevoIsTrue();

	/**
	 * EN: Winter new metons of the years in the given range, oldest first.
	 * ES: Métonos invernales nuevos de los años del rango dado, del más antiguo al más reciente.
	 */
	List<MetonsEntity> findByYearBetweenAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(int yearInicio, int yearFin);

	/**
	 * EN: Every meton between two dates, most recent first. This is the query the date
	 * conversion uses, and the ordering matters: the calculations read the first element
	 * expecting the most recent meton.
	 * ES: Todos los métonos entre dos fechas, del más reciente al más antiguo. Es la
	 * consulta que usa la conversión de fechas, y el orden importa: los cálculos leen el
	 * primer elemento esperando el métono más reciente.
	 */
	List<MetonsEntity> findByDateBetweenOrderByDateDesc(LocalDateTime from, LocalDateTime to);

	/**
	 * EN: First winter new meton after the given date.
	 * ES: Primer métono invernal nuevo posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First winter full meton after the given date.
	 * ES: Primer métono invernal lleno posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndInvernalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First spring new meton after the given date.
	 * ES: Primer métono primaveral nuevo posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndPrimaveralIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First spring full meton after the given date.
	 * ES: Primer métono primaveral lleno posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndPrimaveralIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First summer new meton after the given date.
	 * ES: Primer métono estival nuevo posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndEstivalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First summer full meton after the given date.
	 * ES: Primer métono estival lleno posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndEstivalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First autumn new meton after the given date.
	 * ES: Primer métono otoñal nuevo posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndOtonyalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: First autumn full meton after the given date.
	 * ES: Primer métono otoñal lleno posterior a la fecha dada.
	 */
	MetonsEntity findFirstByDateAfterAndOtonyalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Last meton before the given date, whatever its type.
	 * ES: Último métono anterior a la fecha dada, sea del tipo que sea.
	 */
	MetonsEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: First meton after the given date, whatever its type. Used when building the
	 * casaleros.
	 * ES: Primer métono posterior a la fecha dada, sea del tipo que sea. Se usa al construir
	 * los casaleros.
	 */
	MetonsEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
}
