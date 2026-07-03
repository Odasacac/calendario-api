package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateVAUDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DaysService;

@Service
public class DaysServiceImpl implements DaysService{

	@Autowired
	private WeeksRepository weeksRepository;
	
	@Autowired
	private DaysRepository daysRepository;
	
	public String poblateDays() {
		
		System.out.println("Actualizando los Días.");
		
		String resultado = "Días actualizados correctamente.";
		
		List<DaysEntity> allDays = this.daysRepository.findAll();
		
		if(allDays.isEmpty()) {
			
			List<DaysEntity> daysParaBD = new ArrayList<>();
			
			daysParaBD.add(this.createDay(0, "Terra"));
			daysParaBD.add(this.createDay(1, "Luno"));
			daysParaBD.add(this.createDay(2, "Sole"));
			daysParaBD.add(this.createDay(3, "Merco"));
			daysParaBD.add(this.createDay(4, "Venuro"));
			daysParaBD.add(this.createDay(5, "Marto"));
			daysParaBD.add(this.createDay(6, "Júpeno"));
			daysParaBD.add(this.createDay(7, "Saturino"));
			daysParaBD.add(this.createDay(8, "Liminol"));
			daysParaBD.add(this.createDay(9, "Caelumbra"));
			
			this.daysRepository.saveAll(daysParaBD);
		}
		else {
			System.out.println("Ya hay días en la base de datos.");
			resultado = "Error al actualizar los días: ya hay días en la base de datos.";
		}
		System.out.println("Days actualizados");
		return resultado;
		
	}
	
	public long getDiasASumarALaLunaNueva(DateVAUDTO dateVAU) {
		
		long diasASumarleALaLunaNueva = 0L;
		
		WeeksEntity semana = this.weeksRepository.findByName(dateVAU.getWeek());
		DaysEntity dia = this.daysRepository.findByName(dateVAU.getDay());
		
		int semanaDelMes = semana.getWeekOfMonth();
		

		switch(semanaDelMes) {
			
			case 1:
				diasASumarleALaLunaNueva = Integer.valueOf(dia.getDayOfWeek());
				break;

			case 2:
				diasASumarleALaLunaNueva = Integer.valueOf(dia.getDayOfWeek())+7;
				break;
				
			case 3:
				diasASumarleALaLunaNueva = Integer.valueOf(dia.getDayOfWeek())+14;
				break;

			case 4:
				diasASumarleALaLunaNueva = Integer.valueOf(dia.getDayOfWeek())+21;
				break;

			case 5:
				diasASumarleALaLunaNueva = Integer.valueOf(dia.getDayOfWeek())+21;
				break;							
			
		}
					
		return diasASumarleALaLunaNueva;
	}
	
	private DaysEntity createDay(int dayOfWeek, String name) {
		
		DaysEntity newDato = new DaysEntity();
		newDato.setDayOfWeek(dayOfWeek);
		newDato.setName(name);
		
		return newDato;
	}
}
