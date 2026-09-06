package CCASolutions.Calendario.Repositories;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/**
 * EN: Solstices and equinoxes (the "soes") from year 1 to 2100, four per year. Each one
 * carries the season it opens: 1 winter, 2 spring, 3 summer, 4 autumn.
 * ES: Solsticios y equinoccios (los "soes") del año 1 al 2100, cuatro por año. Cada uno
 * lleva la estación que abre: 1 invierno, 2 primavera, 3 verano, 4 otoño.
 */
public interface SolsticiosYEquinocciosRepository extends JpaRepository <SolsticiosYEquinocciosEntity, Long> {

	/**
	 * EN: One solstice or equinox falling between the two dates.
	 * ES: Un solsticio o equinoccio que caiga entre las dos fechas.
	 */
	public abstract SolsticiosYEquinocciosEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	/**
	 * EN: Solstices and equinoxes in a range, excluding the lower bound and including the
	 * upper one.
	 * ES: Solsticios y equinoccios de un rango, excluyendo la cota inferior e incluyendo la
	 * superior.
	 */
	public abstract  List<SolsticiosYEquinocciosEntity>	findByDateAfterAndDateLessThanEqual (LocalDateTime from, LocalDateTime to);

	/**
	 * EN: Every solstice and equinox of the years in the given range.
	 * ES: Todos los solsticios y equinoccios de los años del rango dado.
	 */
	public abstract  List<SolsticiosYEquinocciosEntity>	findByYearBetween (int from, int to);

	/**
	 * EN: The solstice or equinox of one year that opens a given season.
	 * ES: El solsticio o equinoccio de un año que abre una estación dada.
	 */
	public abstract  SolsticiosYEquinocciosEntity	findByYearAndStartingSeason (int year, int startingSeason);

	/**
	 * EN: The four solstices and equinoxes of one year.
	 * ES: Los cuatro solsticios y equinoccios de un año.
	 */
	public abstract  List<SolsticiosYEquinocciosEntity>	findByYear (int year);

	/**
	 * EN: Latest solstice or equinox stored.
	 * ES: Último solsticio o equinoccio almacenado.
	 */
	public abstract SolsticiosYEquinocciosEntity findTopByOrderByDateDesc();

	/**
	 * EN: Every solstice and equinox in chronological order. The midsisons pair each one
	 * with the next by position, so the ordering is part of the contract.
	 * ES: Todos los solsticios y equinoccios en orden cronológico. Los midsisons emparejan
	 * cada uno con el siguiente por posición, así que el orden forma parte del contrato.
	 */
	public abstract List<SolsticiosYEquinocciosEntity> findAllByOrderByDateAsc();

	/**
	 * EN: First solstice or equinox after the given date.
	 * ES: Primer solsticio o equinoccio posterior a la fecha dada.
	 */
	public abstract SolsticiosYEquinocciosEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);

	/**
	 * EN: Last solstice or equinox before the given date.
	 * ES: Último solsticio o equinoccio anterior a la fecha dada.
	 */
	public abstract SolsticiosYEquinocciosEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);


}
