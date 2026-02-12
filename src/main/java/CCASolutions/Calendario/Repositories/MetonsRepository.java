package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.MetonsEntity;

public interface MetonsRepository extends JpaRepository <MetonsEntity, Long> {	

	List<MetonsEntity> findByYear(int year);

}
