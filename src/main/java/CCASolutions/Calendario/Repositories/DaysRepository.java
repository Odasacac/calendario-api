package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.DaysEntity;

public interface DaysRepository extends JpaRepository <DaysEntity, Long> {

	DaysEntity findByDayOfWeek(long dayOfWeek);
}
