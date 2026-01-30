package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MonthsEntity;

public interface MonthsRepository extends JpaRepository <MonthsEntity, Long> {
	MonthsEntity findBySeasonAndMonthOfSeasonAndHibrid(Integer season, Integer monthOfSeason, Boolean hibrid);

}
