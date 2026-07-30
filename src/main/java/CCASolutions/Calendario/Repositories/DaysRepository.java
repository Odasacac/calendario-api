package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.DaysEntity;

/*
 * ==============================================================================
 * EN: VAU days (10 rows). Served from CatalogoCalendario on the read path;
 *     findByName is still used when converting a VAU date back to a real one.
 * ES: Dias VAU (10 filas). Se sirven desde CatalogoCalendario en el camino de lectura;
 *     findByName se sigue usando al convertir una fecha VAU de vuelta a una real.
 * ==============================================================================
 */
public interface DaysRepository extends JpaRepository<DaysEntity, Long> {

	DaysEntity findByName(String name);
}
