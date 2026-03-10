package CCASolutions.Calendario.ServiceImpl;

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
}
