package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.EclipenosEntity;

public interface EclipenosRepository extends JpaRepository <EclipenosEntity, Long>{

	EclipenosEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	EclipenosEntity findTopByDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrDateLessThanEqualAndInvernalIsTrueAndNuevoIsTrueAndEsTotalIsTrueOrderByDateDesc(@Param("dateO") LocalDateTime dateO, @Param("dateOO") LocalDateTime dateOO);
	EclipenosEntity findTopByYearAndInvernalIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrYearAndInvernalIsTrueAndNuevoIsTrueAndEsTotalIsTrue(@Param("yearUno") int yearUno, @Param("yearDos") int yearDos);
	List<EclipenosEntity> findTop2ByYearGreaterThanEqualAndInvernalIsTrueAndInvernalIsTrueOrderByYearAsc(int year);
	
	EclipenosEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
	EclipenosEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);


    EclipenosEntity findTopByDateLessThanOrderByDateDesc(LocalDateTime date);
    EclipenosEntity findTopByDateGreaterThanOrderByDateAsc(LocalDateTime date);
    
    List<EclipenosEntity> findAllByOrderByDateDesc();
}
