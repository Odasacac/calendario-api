package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Repositories.FestividadesRepository;
import CCASolutions.Calendario.Services.FestividadesService;

@Service
public class FestividadesServiceImpl implements FestividadesService {

	@Autowired
	private FestividadesRepository festividadesRepository;

	public String poblateFestividades() {

		System.out.println("Actualizando las Festividades.");
		
		String resultado = "Festividades actualizadas correctamente.";
		
		List<FestividadesEntity> allFestividades = this.festividadesRepository.findAll();
		
		if(allFestividades.isEmpty()) {
			
			List<FestividadesEntity> festividadParaDDB = new ArrayList<>();
			
			festividadParaDDB.add(this.crearFestividad("CA", "Solsticio de invierno", "Cambio de año", false, false, true, false, false, false, false, false, false, false, 2, 4));

			festividadParaDDB.add(this.crearFestividad("MA", "Solsticio de verano", "Mitad del año", false, false, false, true, false, false, false, false, false, false, 4, 2));

			festividadParaDDB.add(this.crearFestividad("CM", "Métono incial nuevo", "Cambio de métono", false, false, true, false, false, true, true, false, true, false, 2, 4));

			festividadParaDDB.add(this.crearFestividad("BP", "Equinoccio de primavera", "Bienvenida de la primavera", false, true, false, false, false, false, false, false, false, false, 3, 1));

			festividadParaDDB.add(this.crearFestividad("PM", "Primera luna nueva del año", "Inicio del primer mes del año", false, false, false, false, false, true, false, false, false, false, 2, 1));

			festividadParaDDB.add(this.crearFestividad("BO", "Equinoccio de otoño", "Bienvenida del otoño", true, false, false, false, false, false, false, false, false, false, 1, 3));

			festividadParaDDB.add(this.crearFestividad("CE", "Eclípeno inicial nuevo", "Cambio de eclípeno", false, false, true, false, false, true, true, true, false, true, 2, 4));
		
			
			this.festividadesRepository.saveAll(festividadParaDDB);
			
		}
		else {
			System.out.println("Ya hay festividades en la base de datos.");
			resultado = "Error al actualizar las festividades, checkear logs.";
		}
		System.out.println("Festividades actualizadas");
		return resultado;
	}
	
	private FestividadesEntity crearFestividad(String code, String descripcion, String name, boolean equinoccioO, boolean equinoccioP, boolean solsticioI, boolean solsticioV, boolean lunaLlena, boolean lunaNueva, boolean metono, boolean eclipeno, boolean eclipseSol, boolean eclipseLuna, int nextSoe, int pastSoe ) {
		
		FestividadesEntity newFestividad = new FestividadesEntity();
		newFestividad.setCode(code);
		newFestividad.setDescripcion(descripcion);
		newFestividad.setName(name);
		newFestividad.setEsEquinoccioOtonyo(equinoccioO);
		newFestividad.setEsEquinoccioPrimavera(equinoccioP);
		newFestividad.setEsSolsticioInvierno(solsticioI);
		newFestividad.setEsSolsticioVerano(solsticioV);
		newFestividad.setEsLunaLlena(lunaLlena);
		newFestividad.setEsLunaNueva(lunaNueva);
		newFestividad.setEsMetono(metono);
		newFestividad.setEsEclipeno(eclipeno);
		newFestividad.setHayEclipseDeSol(eclipseSol);
		newFestividad.setHayEclipseDeLuna(eclipseLuna);
		newFestividad.setNextSOE(nextSoe);
		newFestividad.setPreviousSOE(pastSoe);
		
		return newFestividad;
	}

}
