package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.EclipenosEntity;

/**
 * EN: Eclipenos: metons that also coincide with an eclipse. There are only a couple of
 * hundred rows in the whole two-thousand-year range, which is why the date conversion can
 * afford to load them all.
 * ES: Eclípenos: métonos que además coinciden con un eclipse. En los dos mil años de rango
 * hay sólo un par de centenares de filas, y por eso la conversión de fechas puede
 * permitirse cargarlos todos.
 */
public interface EclipenosRepository extends JpaRepository <EclipenosEntity, Long>{

	/**
	 * EN: One eclipeno falling between the two dates.
	 * ES: Un eclípeno que caiga entre las dos fechas.
	 */
	EclipenosEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: Most recent winter new eclipeno, with an annular or total eclipse, on or before
	 * the given date. The two date parameters are the same value: Spring Data needs it
	 * repeated because the condition is expressed as two OR-ed branches.
	 * ES: Eclípeno invernal nuevo más reciente, con eclipse anular o total, en la fecha dada
	 * o anterior. Los dos parámetros de fecha son el mismo valor: Spring Data lo necesita
	 * repetido porque la condición se expresa como dos ramas unidas por OR.
	 */
	EclipenosEntity findTopByDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueAndEsTotalIsTrueOrderByDateDesc(@Param("dateO") LocalDateTime dateO, @Param("dateOO") LocalDateTime dateOO);

	/**
	 * EN: Winter new eclipeno of a year, with an annular or total eclipse. Same duplicated
	 * parameter trick as above, this time on the year.
	 * ES: Eclípeno invernal nuevo de un año, con eclipse anular o total. Mismo truco del
	 * parámetro duplicado que arriba, esta vez sobre el año.
	 */
	EclipenosEntity findTopByYearAndInvernalIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrYearAndInvernalIsTrueAndNuevoIsTrueAndEsTotalIsTrue(@Param("yearUno") int yearUno, @Param("yearDos") int yearDos);

	/**
	 * EN: The next two winter eclipenos from the given year onwards.
	 * ES: Los dos siguientes eclípenos invernales desde el año dado en adelante.
	 */
	List<EclipenosEntity> findTop2ByYearGreaterThanEqualAndInvernalIsTrueAndInvernalIsTrueOrderByYearAsc(int year);

	/**
	 * EN: First eclipeno after the given date.
	 * ES: Primer eclípeno posterior a la fecha dada.
	 */
	EclipenosEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Last eclipeno before the given date.
	 * ES: Último eclípeno anterior a la fecha dada.
	 */
	EclipenosEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: Last eclipeno strictly before the given date.
	 * ES: Último eclípeno estrictamente anterior a la fecha dada.
	 */
    EclipenosEntity findTopByDateLessThanOrderByDateDesc(LocalDateTime date);

	/**
	 * EN: First eclipeno strictly after the given date.
	 * ES: Primer eclípeno estrictamente posterior a la fecha dada.
	 */
    EclipenosEntity findTopByDateGreaterThanOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Every eclipeno, most recent first. This is what the date conversion loads, and
	 * the ordering matters: several calculations read the first element expecting the most
	 * recent eclipeno.
	 * ES: Todos los eclípenos, del más reciente al más antiguo. Es lo que carga la
	 * conversión de fechas, y el orden importa: varios cálculos leen el primer elemento
	 * esperando el eclípeno más reciente.
	 */
    List<EclipenosEntity> findAllByOrderByDateDesc();
}
