package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SolsticiosYEquinocciosRepository extends JpaRepository <SolsticiosYEquinocciosEntity, Long> {

	public abstract SolsticiosYEquinocciosEntity findTopByOrderByDateDesc();
		
}
