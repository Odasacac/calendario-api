package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.EclipenosEntity;

/*
 * ==============================================================================
 * EN: Eclipenos (~220 rows).
 *
 *     The read path used to call findAllByOrderByDateDesc() on every single request
 *     and then scan the result twice in Java to find two anchor eclipenos. The table
 *     is tiny and immutable, so CatalogoCalendario now holds it in memory and no
 *     query is issued at all.
 *
 *     Nine unused derived queries were removed, including a 200 character finder
 *     that nothing referenced.
 *
 * ES: Eclipenos (~220 filas).
 *
 *     El camino de lectura llamaba a findAllByOrderByDateDesc() en cada peticion y
 *     luego recorria el resultado dos veces en Java para encontrar dos eclipenos ancla.
 *     La tabla es minuscula e inmutable, asi que ahora CatalogoCalendario la mantiene en
 *     memoria y no se lanza ninguna consulta.
 *
 *     Se han eliminado nueve consultas derivadas sin uso, incluido un buscador de 200
 *     caracteres al que nadie hacia referencia.
 * ==============================================================================
 */
public interface EclipenosRepository extends JpaRepository<EclipenosEntity, Long> {
}
