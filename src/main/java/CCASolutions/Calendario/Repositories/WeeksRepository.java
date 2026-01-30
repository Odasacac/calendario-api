package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.WeeksEntity;

public interface WeeksRepository extends JpaRepository <WeeksEntity, Long>{
	WeeksEntity findByWeekOfMonth(String weekOfMonth);
}
