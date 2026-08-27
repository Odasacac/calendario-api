package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import CCASolutions.Calendario.Entities.EclipsesEntity;

public interface EclipsesRepository extends JpaRepository <EclipsesEntity, Long>{

	EclipsesEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	public abstract List<EclipsesEntity> findByYear(int year);
	List<EclipsesEntity> findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(LocalDateTime inicio, LocalDateTime fin);
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(LocalDateTime date);
	
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeLunaIsTrueOrderByDateAsc(LocalDateTime date);
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeSolIsTrueOrderByDateAsc(LocalDateTime date);
	
	EclipsesEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
	EclipsesEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);
	
	@Query("""
		    SELECT e
		    FROM EclipsesEntity e
		    WHERE e.date BETWEEN :desde AND :hasta
		      AND (
		          e.deSol = true
		          OR (e.deLuna = true AND e.esTotal = true)
		      )
		""")
		List<EclipsesEntity> findEclipsesAbsoluteQuery(
		    LocalDateTime desde,
		    LocalDateTime hasta
		);
}
