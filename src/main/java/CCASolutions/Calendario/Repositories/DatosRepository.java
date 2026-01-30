package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.DatosEntity;

public interface DatosRepository extends JpaRepository <DatosEntity, Long> {
	 List<DatosEntity> findByConceptoIn(List<String> conceptos);
}
