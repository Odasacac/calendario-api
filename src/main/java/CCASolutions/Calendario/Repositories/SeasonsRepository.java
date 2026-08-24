package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SeasonsEntity;

public interface SeasonsRepository extends JpaRepository<SeasonsEntity, Long>{

	public abstract SeasonsEntity findBySeasonOfTheYear(int seasohOfTheYear);
}
