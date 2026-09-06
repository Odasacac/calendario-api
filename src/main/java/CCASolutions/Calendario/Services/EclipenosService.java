package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;

/**
 * EN: Manages the eclipenos: a meton that also coincides with an eclipse. They are the
 * longest cycle of the VAU calendar and the anchor every other counter hangs from.
 * ES: Gestiona los eclípenos: un métono que además coincide con un eclipse. Son el ciclo
 * más largo del calendario VAU y el ancla de la que cuelgan todos los demás contadores.
 */
public interface EclipenosService {

	/**
	 * EN: Goes through every fasal meton and creates an eclipeno for each eclipse of the
	 * same year falling within one sidereal day of it.
	 * ES: Recorre todos los métonos fasales y crea un eclípeno por cada eclipse del mismo
	 * año que caiga dentro de un día sideral.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateEclipenos();

	/**
	 * EN: Counts the winter new eclipenos elapsed since the reference eclipeno and works
	 * out which eclipeno the date belongs to, plus its qualifier when it is apofasal.
	 * ES: Cuenta los eclípenos invernales nuevos transcurridos desde el eclípeno de
	 * referencia y determina a qué eclípeno pertenece la fecha, junto con su apellido
	 * cuando es apofasal.
	 *
	 * @param allEclipenos          EN: every eclipeno in the database. / ES: todos los eclípenos de la base de datos.
	 * @param lastEclipenoSelecto   EN: reference eclipeno the count hangs from. / ES: eclípeno de referencia del que cuelga la cuenta.
	 * @param date                  EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the eclipeno counter. / ES: el contador de eclípeno.
	 */
	public abstract EclipenoINDTO getVAUEclipeno(List<EclipenosEntity> allEclipenos, EclipenosEntity lastEclipenoSelecto, LocalDate date);

	/**
	 * EN: Days elapsed since the reference eclipeno, and whether the date is that very day.
	 * ES: Días transcurridos desde el eclípeno de referencia, y si la fecha es ese mismo día.
	 *
	 * @param lastEclipenoSelecto EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param date                EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: elapsed days as text and the same-day flag. / ES: los días transcurridos como texto y la marca de mismo día.
	 */
	public abstract EclipenoSelectoDTO getVAUEclipenoSelecto(EclipenosEntity lastEclipenoSelecto, LocalDate date);

	/**
	 * EN: Most recent winter new eclipeno, with an annular or total eclipse, on or before
	 * the date.
	 * ES: Eclípeno invernal nuevo más reciente, con eclipse anular o total, anterior o igual
	 * a la fecha.
	 *
	 * @param allEclipenos EN: eclipenos to search. / ES: eclípenos donde buscar.
	 * @param date         EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the eclipeno, or {@code null} if there is none. / ES: el eclípeno, o {@code null} si no hay ninguno.
	 */
	public abstract EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date);

	/**
	 * EN: Most recent winter apofasal remote eclipeno on or before the date: winter
	 * solstice, new moon and apogee within one sidereal day, plus an annular or total
	 * eclipse. It is the rarest phenomenon in the calendar and the origin of every count.
	 * ES: Eclípeno invernal apofasal remoto más reciente anterior o igual a la fecha:
	 * solsticio de invierno, luna nueva y apogeo dentro de un día sideral, más un eclipse
	 * anular o total. Es el fenómeno más excepcional del calendario y el origen de todas
	 * las cuentas.
	 *
	 * @param allEclipenos EN: eclipenos to search. / ES: eclípenos donde buscar.
	 * @param date         EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the eclipeno, or {@code null} if there is none. / ES: el eclípeno, o {@code null} si no hay ninguno.
	 */
	public abstract EclipenosEntity getLastEclipenoInvernalApofasalRemoto(List<EclipenosEntity> allEclipenos, LocalDate date);
}
