package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.AllFasesLunaresEntity;

/**
 * EN: Historical table of every moon phase from year -4700 to 2100, with the date split
 * into numeric fields so negative years fit. Kept as a record only; the calendar
 * calculations use the lunas table instead.
 * ES: Tabla histórica de todas las fases lunares del año -4700 al 2100, con la fecha
 * troceada en campos numéricos para que quepan los años negativos. Se conserva sólo como
 * registro; los cálculos del calendario usan la tabla de lunas.
 */
public interface AllFasesLunaresRepository extends JpaRepository <AllFasesLunaresEntity, Long> {

}
