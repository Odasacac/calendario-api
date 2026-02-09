package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

public interface MetonsRepository extends JpaRepository <MetonsEntity, Long> {
	Optional<MetonsEntity> findFirstByDateLessThanEqualAndNuevoTrueAndInicialTrueOrderByDateDesc(LocalDateTime date);
	Optional<MetonsEntity> findFirstByYearGreaterThanAndNuevoTrueAndInicialTrueOrderByDateAsc(int year);

	List<MetonsEntity> findByYear(int year);
	MetonsEntity findByNuevoTrueAndInicialTrueAndYear(int year);
}
