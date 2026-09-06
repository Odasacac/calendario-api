package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SeasonsEntity;

/**
 * EN: The four VAU seasons plus the placeholder used when a date belongs to none.
 * ES: Las cuatro estaciones VAU más la de relleno que se usa cuando una fecha no pertenece
 * a ninguna.
 */
public interface SeasonsRepository extends JpaRepository<SeasonsEntity, Long>{

	/**
	 * EN: The season with a given number: 1 winter, 2 spring, 3 summer, 4 autumn, 0 none.
	 * ES: La estación con un número dado: 1 invierno, 2 primavera, 3 verano, 4 otoño, 0 ninguna.
	 */
	public abstract SeasonsEntity findBySeasonOfTheYear(int seasohOfTheYear);
}
