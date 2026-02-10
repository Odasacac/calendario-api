package CCASolutions.Calendario.ServiceImpl;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.Services.DaysService;

@Service
public class DaysServiceImpl implements DaysService{

	public int getDiasASumarALaLunaNueva(DateDTOFromDB dateVAU) {
		
		int diasASumarleALaLunaNueva = 0;
		
		switch(Integer.valueOf(dateVAU.getWeek().getWeekOfMonth())) {
		
			case 1:
				diasASumarleALaLunaNueva = Integer.valueOf(dateVAU.getDay().getDayOfWeek());
				break;

			case 2:
				diasASumarleALaLunaNueva = Integer.valueOf(dateVAU.getDay().getDayOfWeek())+7;
				break;
				
			case 3:
				diasASumarleALaLunaNueva = Integer.valueOf(dateVAU.getDay().getDayOfWeek())+14;
				break;

			case 4:
				diasASumarleALaLunaNueva = Integer.valueOf(dateVAU.getDay().getDayOfWeek())+21;
				break;

			case 5:
				diasASumarleALaLunaNueva = Integer.valueOf(dateVAU.getDay().getDayOfWeek())+21;
				break;							
		}
		
		return diasASumarleALaLunaNueva;
	}
}
