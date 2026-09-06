package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.AllSoEsEntity;

/**
 * EN: Historical table of every solstice and equinox from year -4700 to 2100, with the
 * date split into numeric fields so negative years fit. Kept as a record only; the
 * calendar calculations use the sye table instead.
 * ES: Tabla histórica de todos los solsticios y equinoccios del año -4700 al 2100, con la
 * fecha troceada en campos numéricos para que quepan los años negativos. Se conserva sólo
 * como registro; los cálculos del calendario usan la tabla sye.
 */
public interface AllSoEsRepository extends JpaRepository <AllSoEsEntity,Long>{

}
