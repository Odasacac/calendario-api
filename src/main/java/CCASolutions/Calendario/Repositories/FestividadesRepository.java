package CCASolutions.Calendario.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.FestividadesEntity;

public interface FestividadesRepository extends JpaRepository <FestividadesEntity, Long>{

	FestividadesEntity findByCode(String code);
	
	@Query("""
			SELECT f
			FROM FestividadesEntity f
			WHERE f.esLunaNueva = :esLunaNueva
				AND f.esLunaLlena = :esLunaLlena
				AND f.esSolsticioInvierno = :esSolsticioInvierno
				AND f.esSolsticioVerano = :esSolsticioVerano
				AND f.esEquinoccioPrimavera = :esEquinoccioPrimavera
				AND f.esEquinoccioOtonyo = :esEquinoccioOtonyo
				AND f.esEclipeno = :esEclipeno
				AND f.esMetono = :esMetono
				AND (:soeSeason = 0 OR f.previousSOE = :soeSeason)
		""")
		FestividadesEntity buscarFestividad(
			@Param("esLunaNueva") Boolean esLunaNueva,
			@Param("esLunaLlena") Boolean esLunaLlena,
			@Param("esSolsticioInvierno") Boolean esSolsticioInvierno,
			@Param("esSolsticioVerano") Boolean esSolsticioVerano,
			@Param("esEquinoccioPrimavera") Boolean esEquinoccioPrimavera,
			@Param("esEquinoccioOtonyo") Boolean esEquinoccioOtonyo,
			@Param("esEclipeno") Boolean esEclipeno,
			@Param("esMetono") Boolean esMetono,
			@Param("soeSeason") int soeSeason
		);
}
