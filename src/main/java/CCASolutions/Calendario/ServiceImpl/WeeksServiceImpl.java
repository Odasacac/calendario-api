package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.WeeksService;

@Service
public class WeeksServiceImpl implements WeeksService{

	@Autowired
	private WeeksRepository weeksRepository;
	
	public String poblateWeeks() {
	
		System.out.println("Actualizando las Semanas.");
		
		String resultado = "Semanas actualizadas correctamente.";
		
		List<WeeksEntity> allWeeks = this.weeksRepository.findAll();
		
		if(allWeeks.isEmpty()) {
			
			List<WeeksEntity> weeksParaDDB = new ArrayList<>();
			
			weeksParaDDB.add(this.createWeek("Primana", 1));
			weeksParaDDB.add(this.createWeek("Segana", 2));
			weeksParaDDB.add(this.createWeek("Terana", 3));
			weeksParaDDB.add(this.createWeek("Curana", 4));
			weeksParaDDB.add(this.createWeek("Limana", 5));
			weeksParaDDB.add(this.createWeek("Nomana", 0));
			
			this.weeksRepository.saveAll(weeksParaDDB);
			
		}
		else {
			System.out.println("Ya hay semanas en la base de datos.");
			resultado = "Error al actualizar las semanas: ya hay semanas en la base de datos.";
		}
		System.out.println("Semanas actualizadas");
		return resultado;
	}
	
	private WeeksEntity createWeek(String name, int weekOfMonths) {
		
		WeeksEntity newWeek = new WeeksEntity();
		newWeek.setName(name);
		newWeek.setWeekOfMonth(weekOfMonths);
		
		return newWeek;
	}

}
