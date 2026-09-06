package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.WeeksEntity;

/**
 * EN: The six VAU weeks: the placeholder one (0, the day of the new moon) plus Primana,
 * Segana, Terana, Curana and Limana.
 * ES: Las seis semanas VAU: la de relleno (0, el día de la luna nueva) más Primana,
 * Segana, Terana, Curana y Limana.
 */
public interface WeeksRepository extends JpaRepository<WeeksEntity, Long> {

	/**
	 * EN: The week at a given position within the month, 0 to 5.
	 * ES: La semana que ocupa una posición dada dentro del mes, del 0 al 5.
	 */
	WeeksEntity findByWeekOfMonth(int weekOfMonth);

	/**
	 * EN: The week with a given name; used for the reverse conversion.
	 * ES: La semana con un nombre dado; se usa para la conversión inversa.
	 */
	WeeksEntity findByName(String name);
}
