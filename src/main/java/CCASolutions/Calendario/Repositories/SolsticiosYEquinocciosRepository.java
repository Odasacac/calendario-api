package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SolsticiosYEquinocciosRepository extends JpaRepository <SolsticiosYEquinocciosEntity, Long> {

	public abstract SolsticiosYEquinocciosEntity findTopByOrderByDateDesc();
	public abstract List<SolsticiosYEquinocciosEntity> findByYearBetweenOrderByDateAsc(int yearFrom, int yearTo);
	public abstract SolsticiosYEquinocciosEntity findByYearAndStartingSeason(int year, int startingSeason);
		
}
