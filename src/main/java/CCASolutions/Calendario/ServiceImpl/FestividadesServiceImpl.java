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
			
			festividadParaDDB.add(this.crearFestividad("CM", "Cambio de métono"));
			festividadParaDDB.add(this.crearFestividad("CA", "Cambio de año"));
			festividadParaDDB.add(this.crearFestividad("IPMA", "Inicio del primer mes del año"));
			festividadParaDDB.add(this.crearFestividad("BP", "Bienvenida de la primavera"));
			festividadParaDDB.add(this.crearFestividad("MA", "Mitad de año"));
			festividadParaDDB.add(this.crearFestividad("PO", "Paso al otoño"));
			festividadParaDDB.add(this.crearFestividad("DA", "Despedida del año"));

			
			this.festividadesRepository.saveAll(festividadParaDDB);
			
		}
		else {
			System.out.println("Ya hay festividades en la base de datos.");
			resultado = "Error al actualizar las festividades, checkear logs.";
		}
		System.out.println("Festividades actualizadas");
		return resultado;
	}
	
	private FestividadesEntity crearFestividad(String code, String name) {
		
		FestividadesEntity newFestividad = new FestividadesEntity();

		newFestividad.setCode(code);
		newFestividad.setNombre(name);		
		
		return newFestividad;
	}

}
