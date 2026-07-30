package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.WeeksEntity;

/*
 * ==============================================================================
 * EN: VAU weeks (6 rows). Served from CatalogoCalendario on the read path;
 *     findByName is still used when converting a VAU date back to a real one.
 *
 *     findByWeekOfMonth(String) is gone: the column is an int, so every call paid
 *     for a string-to-int conversion on top of the round trip.
 *
 * ES: Semanas VAU (6 filas). Se sirven desde CatalogoCalendario en el camino de lectura;
 *     findByName se sigue usando al convertir una fecha VAU de vuelta a una real.
 *
 *     findByWeekOfMonth(String) desaparece: la columna es un int, asi que cada llamada
 *     pagaba una conversion de cadena a entero ademas del viaje a la base de datos.
 * ==============================================================================
 */
public interface WeeksRepository extends JpaRepository<WeeksEntity, Long> {

	WeeksEntity findByName(String name);
}
