package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

public interface MetonsRepository extends JpaRepository <MetonsEntity, Long> {	
	
	MetonsEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	List<MetonsEntity> findByYear(int year);
	MetonsEntity findByYearAndInvernalIsTrueAndNuevoIsTrue(int year);
	MetonsEntity findTopByDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime fecha);
	MetonsEntity findFirstByYearGreaterThanAndInvernalIsTrueAndNuevoIsTrueOrderByYearAsc(int year);
	List<MetonsEntity> findByDateBetweenAndInvernalIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime from, LocalDateTime to);
	List<MetonsEntity> findByYearGreaterThanEqualAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(int year);
	List<MetonsEntity> findAllByInvernalIsTrueAndNuevoIsTrue();
	List<MetonsEntity> findByYearBetweenAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(int yearInicio, int yearFin);
	List<MetonsEntity> findByDateBetweenOrderByDateDesc(LocalDateTime from, LocalDateTime to);
	
	MetonsEntity findFirstByDateAfterAndInvernalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndInvernalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndPrimaveralIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndPrimaveralIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndEstivalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndEstivalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndOtonyalIsTrueAndNuevoIsTrueOrderByDateAsc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterAndOtonyalIsTrueAndLlenoIsTrueOrderByDateAsc(LocalDateTime date);
	
	MetonsEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);
	MetonsEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
}
