package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonoInvernalApofasalRemotoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;

/**
 * EN: Manages the metons: a solstice or equinox that coincides, within one sidereal day,
 * with a moon phase (fasal meton) or with an apogee or perigee (apoperico meton). When
 * both coincide at once the meton is "apofasal".
 * ES: Gestiona los métonos: un solsticio o equinoccio que coincide, dentro de un día
 * sideral, con una fase lunar (métono fasal) o con un apogeo o perigeo (métono apopérico).
 * Cuando coinciden ambos a la vez el métono es "apofasal".
 */
public interface MetonsService {

	/**
	 * EN: Goes through every solstice and equinox and creates a meton for each moon phase
	 * or apogee/perigee falling within one sidereal day, then marks the apofasal ones.
	 * ES: Recorre todos los solsticios y equinoccios y crea un métono por cada fase lunar o
	 * apogeo/perigeo que caiga dentro de un día sideral, y después marca los apofasales.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateMetonos();

	/**
	 * EN: Counts the winter new metons (IN) and the winter aporic metons (IA) elapsed
	 * since the reference meton, which is what gives a VAU date its meton numbers.
	 * ES: Cuenta los métonos invernales nuevos (IN) y los métonos invernales apóricos (IA)
	 * transcurridos desde el métono de referencia, que es lo que da a una fecha VAU sus
	 * números de métono.
	 *
	 * @param lastMetonIApofasalRemoto EN: reference meton the count hangs from. / ES: métono de referencia del que cuelga la cuenta.
	 * @param lastEclipenoINSelecto    EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param metons                   EN: metons in range. / ES: métonos del rango.
	 * @param date                     EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the IN and IA meton counters. / ES: los contadores de métono IN e IA.
	 */
	public abstract MetonDTO getVAUMeton (MetonsEntity lastMetonIApofasalRemoto, EclipenosEntity lastEclipenoINSelecto, List<MetonsEntity> metons, LocalDate date);

	/**
	 * EN: Counts the winter apofasal remote metons (winter solstice with a new moon at
	 * apogee) elapsed since the reference eclipeno. It is the longest cycle in the
	 * calendar below the eclipeno itself.
	 * ES: Cuenta los métonos invernales apofasales remotos (solsticio de invierno con luna
	 * nueva en apogeo) transcurridos desde el eclípeno de referencia. Es el ciclo más largo
	 * del calendario por debajo del propio eclípeno.
	 *
	 * @param lastEclipenoInvernalApofasalRemoto EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param allMetons                          EN: metons in range. / ES: métonos del rango.
	 * @param date                               EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the counter for this cycle. / ES: el contador de este ciclo.
	 */
	public abstract MetonoInvernalApofasalRemotoDTO getMetonoInvernalApofasalRemoto(EclipenosEntity lastEclipenoInvernalApofasalRemoto, List<MetonsEntity> allMetons, LocalDate date);

	/**
	 * EN: Most recent winter apofasal remote meton on or before the date.
	 * ES: Métono invernal apofasal remoto más reciente anterior o igual a la fecha.
	 *
	 * @param allMetons EN: metons to search. / ES: métonos donde buscar.
	 * @param date      EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the meton, or {@code null} if there is none. / ES: el métono, o {@code null} si no hay ninguno.
	 */
	public abstract MetonsEntity getLastMetonIApofasalRemoto(List<MetonsEntity> allMetons, LocalDate date);

	/**
	 * EN: Most recent winter new meton on or before the date.
	 * ES: Métono invernal nuevo más reciente anterior o igual a la fecha.
	 *
	 * @param allMetons EN: metons to search. / ES: métonos donde buscar.
	 * @param date      EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the meton, or {@code null} if there is none. / ES: el métono, o {@code null} si no hay ninguno.
	 */
	public abstract MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date);
}
