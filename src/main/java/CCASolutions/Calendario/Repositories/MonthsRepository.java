package CCASolutions.Calendario.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MonthsEntity;

/**
 * EN: The eighteen VAU months: three ordinary ones per season, one hybrid month per
 * season, the liminal month and Nomon, the placeholder.
 * ES: Los dieciocho meses VAU: tres corrientes por estación, uno híbrido por estación, el
 * mes liminal y Nomon, el de relleno.
 */
public interface MonthsRepository extends JpaRepository <MonthsEntity, Long> {

	/**
	 * EN: The month with a given name.
	 * ES: El mes con un nombre dado.
	 */
	public abstract MonthsEntity findByName(String name);

	/**
	 * EN: The month at a position within a season. Position 0 means the hybrid month, and
	 * the liminal flag picks out the month between the winter solstice and the first new
	 * moon after it.
	 * ES: El mes que ocupa una posición dentro de una estación. La posición 0 es el mes
	 * híbrido, y la bandera liminal distingue el mes que va del solsticio de invierno a la
	 * primera luna nueva posterior.
	 */
	public abstract MonthsEntity findBySeasonAndMonthOfSeasonAndLiminal (int season, int monthOfSeason, boolean liminal);
}
