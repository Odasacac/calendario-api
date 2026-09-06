package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.AllEclipsesEntity;

/**
 * EN: Historical table of every eclipse from year -4700 to 2100. Its dates are stored as
 * separate numeric fields, because years before 1 do not fit into a LocalDateTime. Kept as
 * a record only; the calendar calculations use the eclipses table instead.
 * ES: Tabla histórica de todos los eclipses del año -4700 al 2100. Sus fechas se guardan
 * como campos numéricos sueltos, porque los años anteriores al 1 no caben en un
 * LocalDateTime. Se conserva sólo como registro; los cálculos del calendario usan la tabla
 * de eclipses.
 */
public interface AllEclipsesRepository extends JpaRepository <AllEclipsesEntity, Long> {

}
