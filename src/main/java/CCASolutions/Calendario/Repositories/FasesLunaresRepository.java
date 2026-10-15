package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.FasesLunaresEntity;

public interface FasesLunaresRepository extends JpaRepository <FasesLunaresEntity, Long> {
	
	public abstract FasesLunaresEntity findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);
	
	public abstract List<FasesLunaresEntity> findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	
	public abstract Optional<FasesLunaresEntity> findById (Long id);
	
	public abstract FasesLunaresEntity findTopByOrderByDateDesc();
	
	public abstract FasesLunaresEntity findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(LocalDateTime date);
	
	public abstract List<FasesLunaresEntity> findByDateBetweenAndNuevaTrue(LocalDateTime yearFrom, LocalDateTime yearTo);
	
	public abstract List<FasesLunaresEntity> findByDateBetweenAndNuevaTrueOrderByDateAsc(LocalDateTime yearFrom, LocalDateTime yearTo);
	
	public abstract List<FasesLunaresEntity> findByYearBetweenAndNuevaTrue (int from, int to);
	
	public abstract List<FasesLunaresEntity> findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDateTime date);
	
	public abstract FasesLunaresEntity findFirstByDateAfterOrderByDateAsc(LocalDateTime date);
	
	public abstract FasesLunaresEntity findFirstByDateBeforeOrderByDateDesc(LocalDateTime date);
	
	public abstract FasesLunaresEntity findFirstByDateAndLlenaIsTrue(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateAndNuevaIsTrue(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateBeforeAndLlenaIsTrue(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateBeforeAndNuevaIsTrue(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateBeforeAndLlenaIsTrueOrderByDateDesc(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateBeforeAndNuevaIsTrueOrderByDateDesc (LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateAfterAndLlenaIsTrue(LocalDateTime start);
	
	public abstract FasesLunaresEntity findFirstByDateAfterAndNuevaIsTrue(LocalDateTime start);
	
	
	@Query("""
		    SELECT l
		    FROM FasesLunaresEntity l
		    WHERE l.date BETWEEN :startDate AND :endDate
		    AND (l.nueva = true OR l.llena = true)
		""")
		List<FasesLunaresEntity> findLunasNuevasOLlenasEntreFechas(
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate
		);

	
}

