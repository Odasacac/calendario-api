package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.EclipsesEntity;

public interface EclipsesRepository extends JpaRepository <EclipsesEntity, Long>{

	public abstract List<EclipsesEntity> findByYear(int year);
}
