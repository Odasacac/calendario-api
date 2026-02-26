package CCASolutions.Calendario.Repositories;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SolsticiosYEquinocciosRepository extends JpaRepository <SolsticiosYEquinocciosEntity, Long> {
	
	public abstract SolsticiosYEquinocciosEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);

	public abstract  List<SolsticiosYEquinocciosEntity>	findByDateAfterAndDateLessThanEqual (LocalDateTime from, LocalDateTime to);
	
	public abstract  List<SolsticiosYEquinocciosEntity>	findByYearBetween (int from, int to);
	
	public abstract  SolsticiosYEquinocciosEntity	findByYearAndStartingSeason (int year, int startingSeason);
	
	public abstract  List<SolsticiosYEquinocciosEntity>	findByYear (int year);

	public abstract SolsticiosYEquinocciosEntity findTopByOrderByDateDesc();
	

}
