package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.EclipenosEntity;

public interface EclipenosRepository extends JpaRepository <EclipenosEntity, Long>{

	EclipenosEntity findTopByDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrueOrderByDateDesc(@Param("dateO") LocalDateTime dateO, @Param("dateOO") LocalDateTime dateOO);
	EclipenosEntity findTopByYearAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrYearAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrue(@Param("yearUno") int yearUno, @Param("yearDos") int yearDos);
}
