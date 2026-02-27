package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.EclipsesEntity;

public interface EclipsesRepository extends JpaRepository <EclipsesEntity, Long>{

	EclipsesEntity findByDateBetween(LocalDateTime inicio, LocalDateTime fin);
	public abstract List<EclipsesEntity> findByYear(int year);
	List<EclipsesEntity> findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(LocalDateTime inicio, LocalDateTime fin);
}
