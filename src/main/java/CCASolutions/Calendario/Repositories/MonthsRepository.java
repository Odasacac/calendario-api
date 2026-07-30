package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MonthsEntity;

/*
 * ==============================================================================
 * EN: VAU months (18 rows).
 *
 *     findBySeasonAndMonthOfSeasonAndLiminal was called between one and four times
 *     per request - a database round trip each - to read one of eighteen immutable
 *     rows. CatalogoCalendario now indexes those eighteen rows in a map, so the
 *     lookup is a hash of a packed int and the finder is no longer needed.
 *
 * ES: Meses VAU (18 filas).
 *
 *     findBySeasonAndMonthOfSeasonAndLiminal se llamaba entre una y cuatro veces por
 *     peticion - un viaje a la base de datos cada vez - para leer una de dieciocho filas
 *     inmutables. Ahora CatalogoCalendario indexa esas dieciocho filas en un mapa, asi
 *     que la busqueda es un hash de un int empaquetado y el buscador ya no hace falta.
 * ==============================================================================
 */
public interface MonthsRepository extends JpaRepository<MonthsEntity, Long> {
}
