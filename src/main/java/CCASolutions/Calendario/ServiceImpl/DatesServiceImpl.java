package CCASolutions.Calendario.ServiceImpl;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.Utils;

@Service
public class DatesServiceImpl implements DatesService {
	
	@Autowired
	Utils utils;

	public DateDTO getDateVAUFromDateO(LocalDateTime dateO) {
		
		DateDTO dateVAU = new DateDTO();
		
		// 1 - Obtener el año, para ello hay que saber cuando fue el útimo métono
		LocalDateTime lastMeton = utils.getLastMeton(dateO);
		
		// Sabiendo cuando fue el último métono, el año es la diferencia
		dateVAU.setYear(ChronoUnit.YEARS.between(lastMeton, dateO));
		
		
		
		return dateVAU;
	}
}
