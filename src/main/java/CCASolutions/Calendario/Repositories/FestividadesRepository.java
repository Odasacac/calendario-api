package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.FestividadesEntity;

public interface FestividadesRepository extends JpaRepository <FestividadesEntity, Long>{

	FestividadesEntity findByCode(String code);
	
}
