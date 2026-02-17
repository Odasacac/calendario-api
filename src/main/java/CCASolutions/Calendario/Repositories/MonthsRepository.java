package CCASolutions.Calendario.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MonthsEntity;

public interface MonthsRepository extends JpaRepository <MonthsEntity, Long> {

	public abstract MonthsEntity findByName(String name);
	public abstract MonthsEntity findBySeasonAndMonthOfSeasonAndLiminal (int season, int monthOfSeason, boolean liminal);
}
