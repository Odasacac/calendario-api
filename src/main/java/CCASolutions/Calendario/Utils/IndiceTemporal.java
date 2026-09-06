package CCASolutions.Calendario.Utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * EN: Date-ordered index that retrieves, in logarithmic time, the elements falling
 * inside a time window around a given instant.
 * <p>
 * ES: Índice ordenado por fecha que permite recuperar en tiempo logarítmico los
 * elementos que caen dentro de una ventana temporal alrededor de un instante.
 * <p>
 * EN: Populating the database used to cross whole tables with nested loops
 * (soes x moons, apoperis x moons, midsisons x moons...), which with the data from
 * year 1 to 2100 means around 10^9 comparisons. With this index every cross becomes
 * O(n log m) over the few rows that are actually close by.
 * <p>
 * ES: El poblado de la base de datos cruzaba tablas completas con bucles anidados
 * (soes x lunas, apoperis x lunas, midsisons x lunas...), lo que con los datos
 * del año 1 al 2100 supone del orden de 10^9 comparaciones. Con este índice cada
 * cruce pasa a ser O(n log m) sobre las pocas filas que realmente están cerca.
 *
 * @param <T> EN: type of the indexed elements. / ES: tipo de los elementos indexados.
 */
public final class IndiceTemporal<T> {

	/**
	 * EN: Seconds in a sidereal day: the tolerance used across the whole domain.
	 * ES: Segundos de un día sideral: es la tolerancia que usa todo el dominio.
	 */
	public static final long DIA_SIDERAL_EN_SEGUNDOS = 86164L;

	private final List<T> elementos;
	private final long[] instantes;

	/**
	 * EN: Private constructor; instances are always built through {@link #de}.
	 * ES: Constructor privado; las instancias se crean siempre con {@link #de}.
	 */
	private IndiceTemporal(List<T> elementos, long[] instantes) {
		this.elementos = elementos;
		this.instantes = instantes;
	}

	/**
	 * EN: Builds the index. Copies the elements, drops the ones with no date, sorts them
	 * chronologically and precomputes their instants as epoch seconds so later searches
	 * compare longs instead of dates.
	 * ES: Construye el índice. Copia los elementos, descarta los que no tienen fecha, los
	 * ordena cronológicamente y precalcula sus instantes en segundos de época, de modo que
	 * las búsquedas posteriores comparan números y no fechas.
	 *
	 * @param origen EN: source elements; may be {@code null}. / ES: elementos de origen; admite {@code null}.
	 * @param fecha  EN: how to read the date of each element. / ES: cómo obtener la fecha de cada elemento.
	 * @return EN: an index ready to be queried. / ES: un índice listo para consultar.
	 */
	public static <T> IndiceTemporal<T> de(List<T> origen, Function<T, LocalDateTime> fecha) {

		List<T> ordenados = new ArrayList<>();

		if (origen != null) {

			for (T elemento : origen) {

				if (elemento != null && fecha.apply(elemento) != null) {
					ordenados.add(elemento);
				}
			}
		}

		Collections.sort(ordenados, Comparator.comparing(fecha));

		long[] instantes = new long[ordenados.size()];

		for (int i = 0; i < ordenados.size(); i++) {
			instantes[i] = segundos(fecha.apply(ordenados.get(i)));
		}

		return new IndiceTemporal<>(ordenados, instantes);
	}

	/**
	 * EN: Turns a date into epoch seconds. UTC is used consistently, so differences
	 * between two instants match {@code ChronoUnit.SECONDS.between} exactly.
	 * ES: Convierte una fecha a segundos de época. Se usa UTC de forma consistente, así que
	 * las diferencias entre dos instantes coinciden exactamente con
	 * {@code ChronoUnit.SECONDS.between}.
	 */
	private static long segundos(LocalDateTime fecha) {
		return fecha.toEpochSecond(ZoneOffset.UTC);
	}

	/**
	 * EN: Elements whose date is at most {@code tolerancia} seconds away from
	 * {@code centro}, in ascending chronological order.
	 * ES: Elementos cuya fecha dista como mucho {@code tolerancia} segundos de
	 * {@code centro}, en orden cronológico ascendente.
	 *
	 * @param centro     EN: centre of the window; {@code null} yields an empty list. / ES: centro de la ventana; con {@code null} devuelve lista vacía.
	 * @param tolerancia EN: half-width of the window, in seconds. / ES: semianchura de la ventana, en segundos.
	 * @return EN: matching elements, possibly empty, never {@code null}. / ES: los elementos que encajan, quizá ninguno, nunca {@code null}.
	 */
	public List<T> enVentana(LocalDateTime centro, long tolerancia) {

		if (centro == null || elementos.isEmpty()) {
			return Collections.emptyList();
		}

		long referencia = segundos(centro);

		int desde = limiteInferior(referencia - tolerancia);
		int hasta = limiteInferior(referencia + tolerancia + 1);

		if (desde >= hasta) {
			return Collections.emptyList();
		}

		return elementos.subList(desde, hasta);
	}

	/**
	 * EN: First element strictly later than {@code instante}, or {@code null} if there is
	 * none. Equivalent to a findFirstByDateAfterOrderByDateAsc without hitting the database.
	 * ES: Primer elemento cuya fecha es estrictamente posterior a {@code instante}, o
	 * {@code null} si no hay ninguno. Equivale a un findFirstByDateAfterOrderByDateAsc pero
	 * sin ir a la base de datos.
	 *
	 * @param instante EN: lower bound, exclusive. / ES: cota inferior, excluida.
	 * @return EN: the next element, or {@code null}. / ES: el siguiente elemento, o {@code null}.
	 */
	public T primeroDespuesDe(LocalDateTime instante) {

		if (instante == null || elementos.isEmpty()) {
			return null;
		}

		int posicion = limiteInferior(segundos(instante) + 1);

		return posicion < elementos.size() ? elementos.get(posicion) : null;
	}

	/**
	 * EN: Binary search for the first index whose instant is greater than or equal to the
	 * given value. Walks back over repeated instants, since {@code Arrays.binarySearch}
	 * does not guarantee it returns the first match.
	 * ES: Búsqueda binaria del primer índice cuyo instante es mayor o igual que el valor
	 * dado. Retrocede sobre los instantes repetidos, porque {@code Arrays.binarySearch}
	 * no garantiza que devuelva la primera coincidencia.
	 *
	 * @param valor EN: instant in epoch seconds. / ES: instante en segundos de época.
	 * @return EN: insertion position, between 0 and the number of elements. / ES: posición de inserción, entre 0 y el número de elementos.
	 */
	private int limiteInferior(long valor) {

		int posicion = Arrays.binarySearch(instantes, valor);

		if (posicion < 0) {
			return -posicion - 1;
		}

		// EN: binarySearch does not guarantee the first match when there are duplicates
		// ES: binarySearch no garantiza la primera coincidencia cuando hay repetidos
		while (posicion > 0 && instantes[posicion - 1] == valor) {
			posicion--;
		}

		return posicion;
	}
}
