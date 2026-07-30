package CCASolutions.Calendario.Support;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/*
 * ==============================================================================
 * EN: Binary-search slicing of a list already ordered by date, descending.
 *
 *     The catalog keeps the small immutable tables fully in memory, but the
 *     calculators must still see exactly the same date window the old SQL query
 *     returned - "ORDER BY date DESC" over "date BETWEEN from AND to". Slicing the
 *     cached list with two binary searches reproduces that window in O(log n)
 *     without allocating a copy: subList is a view over the original list.
 *
 * ES: Recorte por busqueda binaria de una lista ya ordenada por fecha, descendente.
 *
 *     El catalogo mantiene las tablas pequenas e inmutables enteras en memoria, pero
 *     los calculadores deben seguir viendo exactamente la misma ventana de fechas que
 *     devolvia la consulta SQL antigua - "ORDER BY date DESC" sobre "date BETWEEN
 *     from AND to". Recortar la lista cacheada con dos busquedas binarias reproduce
 *     esa ventana en O(log n) sin copiar nada: subList es una vista sobre la lista
 *     original.
 * ==============================================================================
 */
public final class Rangos {

	private Rangos() {
	}

	/*
	 * EN: The elements whose date falls in [desde, hasta], both bounds included,
	 *     keeping the descending order of the source list. Equivalent to the JPA
	 *     derived query findByDateBetweenOrderByDateDesc(desde, hasta).
	 * ES: Los elementos cuya fecha cae en [desde, hasta], ambos limites incluidos,
	 *     conservando el orden descendente de la lista de origen. Equivale a la
	 *     consulta derivada JPA findByDateBetweenOrderByDateDesc(desde, hasta).
	 */
	public static <T> List<T> entre(List<T> descendentePorFecha, Function<T, LocalDateTime> fechaDe,
			LocalDateTime desde, LocalDateTime hasta) {

		if (descendentePorFecha.isEmpty() || desde.isAfter(hasta)) {
			return Collections.emptyList();
		}

		// EN: First position whose date is already <= hasta (the newest element in range).
		// ES: Primera posicion cuya fecha ya es <= hasta (el elemento mas nuevo del rango).
		int inicio = primerIndiceQueCumple(descendentePorFecha, fechaDe, hasta, true);

		// EN: First position whose date has fallen below desde (one past the last in range).
		// ES: Primera posicion cuya fecha ya cae por debajo de desde (uno despues del ultimo).
		int fin = primerIndiceQueCumple(descendentePorFecha, fechaDe, desde, false);

		if (inicio >= fin) {
			return Collections.emptyList();
		}
		return descendentePorFecha.subList(inicio, fin);
	}

	/*
	 * EN: Because the list is sorted descending, both predicates used here
	 *     ("date <= limite" and "date < limite") are monotone along the index: false
	 *     for a prefix and true from some point on. That is exactly what a binary
	 *     search needs.
	 * ES: Como la lista esta ordenada descendentemente, los dos predicados que se usan
	 *     aqui ("date <= limite" y "date < limite") son monotonos a lo largo del
	 *     indice: falsos en un prefijo y ciertos a partir de cierto punto. Eso es
	 *     precisamente lo que necesita una busqueda binaria.
	 */
	private static <T> int primerIndiceQueCumple(List<T> descendentePorFecha, Function<T, LocalDateTime> fechaDe,
			LocalDateTime limite, boolean incluirElLimite) {

		int inferior = 0;
		int superior = descendentePorFecha.size();

		while (inferior < superior) {
			int medio = (inferior + superior) >>> 1;
			LocalDateTime fecha = fechaDe.apply(descendentePorFecha.get(medio));
			boolean cumple = incluirElLimite ? !fecha.isAfter(limite) : fecha.isBefore(limite);
			if (cumple) {
				superior = medio;
			} else {
				inferior = medio + 1;
			}
		}
		return inferior;
	}
}
