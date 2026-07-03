package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Services.MonthService;

@Service
public class MonthServiceImpl implements MonthService{

	@Autowired
	private MonthsRepository monthsRepository;

	public String poblateMonths() {
		
		System.out.println("Actualizando los Meses.");

		String resultado = "Meses actualizados correctamente.";
		
		List<MonthsEntity> allMonths = this.monthsRepository.findAll();
		
		if(allMonths.isEmpty()) {
			
			List<MonthsEntity> monthsParaDDB = new ArrayList<>();
			
			monthsParaDDB.add(this.createMonth("Prierno", false, 1, 1, false));
			monthsParaDDB.add(this.createMonth("Seguerno", false, 2, 1, false));
			monthsParaDDB.add(this.createMonth("Terno", false, 3, 1, false));
			monthsParaDDB.add(this.createMonth("Pinera", false, 1, 2, false));
			monthsParaDDB.add(this.createMonth("Seguera", false, 2, 2, false));
			monthsParaDDB.add(this.createMonth("Tera", false, 3, 2, false));
			monthsParaDDB.add(this.createMonth("Prano", false, 1, 3, false));
			monthsParaDDB.add(this.createMonth("Segano", false, 2, 3, false));
			monthsParaDDB.add(this.createMonth("Tano", false, 3, 3, false));
			monthsParaDDB.add(this.createMonth("Pridor", false, 1, 4, false));
			monthsParaDDB.add(this.createMonth("Sedor", false, 2, 4, false));
			monthsParaDDB.add(this.createMonth("Tor", false, 3, 4, false));
			monthsParaDDB.add(this.createMonth("Invera", true, 0, 2, false));
			monthsParaDDB.add(this.createMonth("Primano", true, 0, 3, false));
			monthsParaDDB.add(this.createMonth("Verdor", true, 0, 4, false));
			monthsParaDDB.add(this.createMonth("Oterno", true, 0, 1, false));
			monthsParaDDB.add(this.createMonth("Oterno liminal", true, 0, 1, true));
			monthsParaDDB.add(this.createMonth("Nomon", false, 0, 0, false));
			
			
			this.monthsRepository.saveAll(monthsParaDDB);
			
		}
		else {
			System.out.println("Ya hay meses en la base de datos.");
			resultado = "Error al actualizar los meses: ya hay meses en la base de datos.";
		}
		System.out.println("Meses actualizados");
		return resultado;
	}
	
	private MonthsEntity createMonth(String name, boolean hibrid, int monthOfSeason, int season, boolean liminal) {
		
		MonthsEntity newMonth = new MonthsEntity();
		newMonth.setName(name);
		newMonth.setHibrid(hibrid);
		newMonth.setMonthOfSeason(monthOfSeason);
		newMonth.setSeason(season);
		newMonth.setLiminal(liminal);
		
		return newMonth;
	}


	
}
