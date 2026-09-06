package CCASolutions.Calendario.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * EN: The closest phenomenon to a given date on all three sides: the one falling on
 * that same day, the nearest one before it and the nearest one after it.
 * <p>
 * ES: Fenómeno más cercano a una fecha por los tres lados: el que cae ese mismo día,
 * el anterior más próximo y el posterior más próximo.
 * <p>
 * EN: Replaces the "diasMinimosDeDiferenciaEntreXPasadoYDate" loops that were repeated
 * dozens of times across the project. It walks the list once and converts each
 * LocalDateTime into a LocalDate once per element, instead of the three or four
 * conversions per element the original loops performed.
 * <p>
 * ES: Sustituye a los bucles "diasMinimosDeDiferenciaEntreXPasadoYDate" que estaban
 * repetidos decenas de veces por el proyecto. Recorre la lista una sola vez y
 * convierte cada LocalDateTime a LocalDate una sola vez por elemento, en vez de
 * las tres o cuatro conversiones por elemento que hacían los bucles originales.
 *
 * @param <T> EN: type of the elements being searched (moon phases, eclipses, metons...).
 *            ES: tipo de los elementos sobre los que se busca (fases lunares, eclipses, métonos...).
 */
public final class Vecindad<T> {

	private final T actual;
	private final T anterior;
	private final T proximo;
	private final long diasHastaAnterior;
	private final long diasHastaProximo;

	/**
	 * EN: Private constructor; instances are always built through the static factories.
	 * ES: Constructor privado; las instancias se crean siempre con las factorías estáticas.
	 */
	private Vecindad(T actual, T anterior, T proximo, long diasHastaAnterior, long diasHastaProximo) {
		this.actual = actual;
		this.anterior = anterior;
		this.proximo = proximo;
		this.diasHastaAnterior = diasHastaAnterior;
		this.diasHastaProximo = diasHastaProximo;
	}

	/**
	 * EN: Builds the neighbourhood of {@code referencia}. On a distance tie the first
	 * element found in the list wins.
	 * ES: Construye la vecindad de {@code referencia}. En caso de empate en distancia se
	 * queda con el primero que aparece en la lista.
	 *
	 * @param elementos  EN: elements to scan; may be {@code null}. / ES: elementos a recorrer; admite {@code null}.
	 * @param fecha      EN: how to read the date of each element. / ES: cómo obtener la fecha de cada elemento.
	 * @param referencia EN: date the neighbourhood is computed around. / ES: fecha alrededor de la cual se calcula la vecindad.
	 * @return EN: the neighbourhood; every slot may be {@code null}. / ES: la vecindad; cualquiera de sus huecos puede ser {@code null}.
	 */
	public static <T> Vecindad<T> de(List<T> elementos, Function<T, LocalDateTime> fecha, LocalDate referencia) {
		return calcular(elementos, fecha, referencia, false);
	}

	/**
	 * EN: Same as {@link #de}, but on a distance tie the last element in the list wins.
	 * Needed for metons, where the fasal and apoperico ones share the soe date and the
	 * historical behaviour was to keep the last one.
	 * ES: Igual que {@link #de}, pero en caso de empate en distancia se queda con el último
	 * que aparece en la lista. Hace falta para los métonos, donde el fasal y el apopérico
	 * comparten la fecha del soe y el comportamiento histórico era quedarse con el último.
	 *
	 * @param elementos  EN: elements to scan; may be {@code null}. / ES: elementos a recorrer; admite {@code null}.
	 * @param fecha      EN: how to read the date of each element. / ES: cómo obtener la fecha de cada elemento.
	 * @param referencia EN: date the neighbourhood is computed around. / ES: fecha alrededor de la cual se calcula la vecindad.
	 * @return EN: the neighbourhood; every slot may be {@code null}. / ES: la vecindad; cualquiera de sus huecos puede ser {@code null}.
	 */
	public static <T> Vecindad<T> deUltimoEnEmpate(List<T> elementos, Function<T, LocalDateTime> fecha, LocalDate referencia) {
		return calcular(elementos, fecha, referencia, true);
	}

	/**
	 * EN: Single pass that classifies every element as same-day, before or after the
	 * reference date and keeps the closest one on each side. Elements with a null date
	 * are skipped.
	 * ES: Recorrido único que clasifica cada elemento como del mismo día, anterior o
	 * posterior a la fecha de referencia y se queda con el más cercano por cada lado.
	 * Los elementos con fecha nula se descartan.
	 *
	 * @param ultimoEnEmpate EN: {@code true} to let the last tied element win. / ES: {@code true} para que gane el último elemento empatado.
	 * @return EN: the resulting neighbourhood. / ES: la vecindad resultante.
	 */
	private static <T> Vecindad<T> calcular(List<T> elementos, Function<T, LocalDateTime> fecha, LocalDate referencia, boolean ultimoEnEmpate) {

		T actual = null;
		T anterior = null;
		T proximo = null;
		long mejorAnterior = Long.MAX_VALUE;
		long mejorProximo = Long.MAX_VALUE;

		if (elementos != null) {

			long diaReferencia = referencia.toEpochDay();

			for (T elemento : elementos) {

				LocalDateTime instante = fecha.apply(elemento);

				if (instante == null) {
					continue;
				}

				long dia = instante.toLocalDate().toEpochDay();

				if (dia == diaReferencia) {
					actual = elemento;
				}
				else if (dia < diaReferencia) {

					long distancia = diaReferencia - dia;

					if (distancia < mejorAnterior || (ultimoEnEmpate && distancia == mejorAnterior)) {
						mejorAnterior = distancia;
						anterior = elemento;
					}
				}
				else {

					long distancia = dia - diaReferencia;

					if (distancia < mejorProximo || (ultimoEnEmpate && distancia == mejorProximo)) {
						mejorProximo = distancia;
						proximo = elemento;
					}
				}
			}
		}

		return new Vecindad<>(actual, anterior, proximo, mejorAnterior, mejorProximo);
	}

	/**
	 * EN: Element falling on the reference date itself, or {@code null} if there is none.
	 * ES: Elemento que cae en la propia fecha de referencia, o {@code null} si no hay ninguno.
	 */
	public T getActual() {
		return actual;
	}

	/**
	 * EN: Closest element strictly before the reference date, or {@code null} if there is none.
	 * ES: Elemento más cercano estrictamente anterior a la fecha de referencia, o {@code null} si no hay ninguno.
	 */
	public T getAnterior() {
		return anterior;
	}

	/**
	 * EN: Closest element strictly after the reference date, or {@code null} if there is none.
	 * ES: Elemento más cercano estrictamente posterior a la fecha de referencia, o {@code null} si no hay ninguno.
	 */
	public T getProximo() {
		return proximo;
	}

	/**
	 * EN: Whole days between the previous element and the reference date.
	 * {@code Long.MAX_VALUE} when there is no previous element.
	 * ES: Días naturales entre el elemento anterior y la fecha de referencia.
	 * {@code Long.MAX_VALUE} si no hay ningún elemento anterior.
	 */
	public long getDiasHastaAnterior() {
		return diasHastaAnterior;
	}

	/**
	 * EN: Whole days between the reference date and the next element.
	 * {@code Long.MAX_VALUE} when there is no later element.
	 * ES: Días naturales entre la fecha de referencia y el elemento siguiente.
	 * {@code Long.MAX_VALUE} si no hay ningún elemento posterior.
	 */
	public long getDiasHastaProximo() {
		return diasHastaProximo;
	}
}
