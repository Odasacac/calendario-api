package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasRepository extends JpaRepository <LunasEntity, Long> {

	LunasEntity findTopByOrderByDateDesc();
	List<LunasEntity> findByYearOrderByDateAsc(int year);
}
