package CCASolutions.Calendario.Support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/*
 * ==============================================================================
 * EN: Date helpers shared by the whole read path.
 *
 *     The original code repeated three expensive idioms inside loops that run over
 *     thousands of entities:
 *
 *       1. entity.getDate().toLocalDate()      -> allocates a new LocalDate every call
 *       2. ChronoUnit.DAYS.between(a, b)       -> generic, much slower than epoch-day math
 *       3. Comparing a LocalDateTime column against a LocalDate
 *
 *     This class centralises them: distances are computed on epoch days (a plain
 *     long subtraction) and the SQL bounds for "the same calendar day" are built
 *     once instead of being re-derived at every comparison.
 *
 * ES: Utilidades de fechas compartidas por todo el camino de lectura.
 *
 *     El codigo original repetia tres modismos costosos dentro de bucles que
 *     recorren miles de entidades:
 *
 *       1. entity.getDate().toLocalDate()      -> crea un LocalDate nuevo en cada llamada
 *       2. ChronoUnit.DAYS.between(a, b)       -> generico, mucho mas lento que aritmetica
 *                                                 de dias epoch
 *       3. Comparar una columna LocalDateTime contra un LocalDate
 *
 *     Esta clase los centraliza: las distancias se calculan sobre dias epoch (una
 *     simple resta de long) y los limites SQL para "el mismo dia natural" se
 *     construyen una vez en lugar de recalcularse en cada comparacion.
 * ==============================================================================
 */
public final class Fechas {

	/*
	 * EN: Utility class: never instantiated.
	 * ES: Clase de utilidades: nunca se instancia.
	 */
	private Fechas() {
	}

	/*
	 * EN: Epoch day of the calendar day a timestamp falls on. This is the cheap
	 *     replacement for "getDate().toLocalDate()" followed by a comparison:
	 *     LocalDateTime already stores its LocalDate internally, so toLocalDate()
	 *     is a field read, and toEpochDay() turns it into a comparable long.
	 * ES: Dia epoch del dia natural en el que cae una marca de tiempo. Es el
	 *     sustituto barato de "getDate().toLocalDate()" seguido de una comparacion:
	 *     LocalDateTime ya guarda su LocalDate internamente, asi que toLocalDate()
	 *     es una lectura de campo, y toEpochDay() lo convierte en un long comparable.
	 */
	public static long diaEpoch(LocalDateTime momento) {
		return momento.toLocalDate().toEpochDay();
	}

	/*
	 * EN: Signed distance in days from "desde" to "hasta", both given as epoch days.
	 *     Equivalent to ChronoUnit.DAYS.between(desde, hasta) for whole days.
	 * ES: Distancia con signo en dias desde "desde" hasta "hasta", ambos como dias
	 *     epoch. Equivale a ChronoUnit.DAYS.between(desde, hasta) para dias completos.
	 */
	public static long dias(long desdeDiaEpoch, long hastaDiaEpoch) {
		return hastaDiaEpoch - desdeDiaEpoch;
	}

	/*
	 * EN: Absolute distance in days between two epoch days.
	 * ES: Distancia absoluta en dias entre dos dias epoch.
	 */
	public static long diasAbs(long unDiaEpoch, long otroDiaEpoch) {
		long diferencia = unDiaEpoch - otroDiaEpoch;
		return diferencia < 0 ? -diferencia : diferencia;
	}

	/*
	 * EN: Kept for the places that still need day distance between two LocalDates.
	 *     Uses epoch days instead of ChronoUnit for the same result at lower cost.
	 * ES: Se mantiene para los sitios que aun necesitan la distancia en dias entre
	 *     dos LocalDate. Usa dias epoch en vez de ChronoUnit para el mismo resultado
	 *     a menor coste.
	 */
	public static long dias(LocalDate desde, LocalDate hasta) {
		return hasta.toEpochDay() - desde.toEpochDay();
	}

	/*
	 * EN: First instant of a calendar day: the inclusive lower bound of a
	 *     "date >= this day" SQL predicate.
	 * ES: Primer instante de un dia natural: el limite inferior inclusivo de un
	 *     predicado SQL "date >= este dia".
	 */
	public static LocalDateTime inicioDelDia(LocalDate dia) {
		return dia.atStartOfDay();
	}

	/*
	 * EN: Last instant of a calendar day: the inclusive upper bound of a
	 *     "date <= this day" SQL predicate. Matches the LocalTime.MAX the original
	 *     code used to build its query windows.
	 * ES: Ultimo instante de un dia natural: el limite superior inclusivo de un
	 *     predicado SQL "date <= este dia". Coincide con el LocalTime.MAX que usaba
	 *     el codigo original para construir sus ventanas de consulta.
	 */
	public static LocalDateTime finDelDia(LocalDate dia) {
		return dia.atTime(LocalTime.MAX);
	}

	/*
	 * EN: Exclusive upper bound for "the timestamp falls strictly before this day":
	 *     any instant of an earlier day is < startOfDay(dia), and any instant of
	 *     "dia" or later is >=. This is what makes "toLocalDate().isBefore(date)"
	 *     expressible as a plain SQL comparison.
	 * ES: Limite superior exclusivo para "la marca de tiempo cae estrictamente antes
	 *     de este dia": cualquier instante de un dia anterior es < startOfDay(dia), y
	 *     cualquier instante de "dia" o posterior es >=. Esto es lo que permite
	 *     expresar "toLocalDate().isBefore(date)" como una comparacion SQL simple.
	 */
	public static LocalDateTime antesDelDia(LocalDate dia) {
		return dia.atStartOfDay();
	}

	/*
	 * EN: Inclusive lower bound for "the timestamp falls strictly after this day".
	 * ES: Limite inferior inclusivo para "la marca de tiempo cae estrictamente
	 *     despues de este dia".
	 */
	public static LocalDateTime despuesDelDia(LocalDate dia) {
		return dia.plusDays(1).atStartOfDay();
	}

	/*
	 * EN: Midpoint between two instants, in seconds. Used to place the "midsison",
	 *     the halfway point between two consecutive solstices/equinoxes.
	 * ES: Punto medio entre dos instantes, en segundos. Se usa para situar el
	 *     "midsison", el punto intermedio entre dos solsticios/equinoccios
	 *     consecutivos.
	 */
	public static LocalDateTime puntoMedio(LocalDateTime desde, LocalDateTime hasta) {
		return desde.plusSeconds(ChronoUnit.SECONDS.between(desde, hasta) / 2);
	}

	/*
	 * EN: "day"/"days" in Spanish, for the human readable messages.
	 * ES: "dia"/"dias" para los mensajes legibles.
	 */
	public static String literalDias(long cantidad) {
		return cantidad == 1 ? "día" : "días";
	}
}
