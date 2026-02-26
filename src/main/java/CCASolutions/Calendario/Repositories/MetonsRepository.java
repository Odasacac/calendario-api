package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

public interface MetonsRepository extends JpaRepository <MetonsEntity, Long> {	
	
	MetonsEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	List<MetonsEntity> findByYear(int year);
	MetonsEntity findByYearAndInicialIsTrueAndNuevoIsTrue(int year);
	MetonsEntity findTopByDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime fecha);
	MetonsEntity findFirstByYearGreaterThanAndInicialIsTrueAndNuevoIsTrueOrderByYearAsc(int year);
	List<MetonsEntity> findByDateBetweenAndInicialIsTrueAndNuevoIsTrueOrderByDateDesc(LocalDateTime from, LocalDateTime to);
	List<MetonsEntity> findByYearGreaterThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(int year);
	List<MetonsEntity> findAllByInicialIsTrueAndNuevoIsTrue();
}
