package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

/*
 * ==============================================================================
 * EN: Metonic cycles (~1.800 rows).
 *
 *     The read path no longer queries this table at all: the whole thing is small
 *     and immutable, so CatalogoCalendario keeps it in memory and slices the date
 *     window with a binary search.
 *
 *     Twenty derived queries that nothing called were removed. That is not only
 *     dead code: Spring Data parses, validates and builds a query for every single
 *     method of every repository at startup, so unused finders cost boot time and
 *     retained memory for nothing.
 *
 * ES: Metonos (~1.800 filas).
 *
 *     El camino de lectura ya no consulta esta tabla: es pequena e inmutable, asi que
 *     CatalogoCalendario la mantiene en memoria y recorta la ventana de fechas con una
 *     busqueda binaria.
 *
 *     Se han eliminado veinte consultas derivadas que nadie llamaba. No es solo codigo
 *     muerto: Spring Data analiza, valida y construye una consulta para cada metodo de
 *     cada repositorio al arrancar, por lo que los buscadores sin usar cuestan tiempo
 *     de arranque y memoria retenida a cambio de nada.
 * ==============================================================================
 */
public interface MetonsRepository extends JpaRepository<MetonsEntity, Long> {

	/*
	 * EN: Used by the population job to chain one metono to the next.
	 * ES: Lo usa el proceso de poblacion para encadenar un metono con el siguiente.
	 */
	MetonsEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
}
