package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.DaysEntity;

/**
 * EN: The ten VAU days, from Terra (day 0, the new moon itself) to Caelumbra (day 9).
 * ES: Los diez días VAU, desde Terra (día 0, la propia luna nueva) hasta Caelumbra (día 9).
 */
public interface DaysRepository extends JpaRepository <DaysEntity, Long> {

	/**
	 * EN: The day at a given position within the week, 0 to 9.
	 * ES: El día que ocupa una posición dada dentro de la semana, del 0 al 9.
	 */
	DaysEntity findByDayOfWeek(int dayOfWeek);

	/**
	 * EN: The day with a given name; used for the reverse conversion.
	 * ES: El día con un nombre dado; se usa para la conversión inversa.
	 */
	DaysEntity findByName(String name);
}
