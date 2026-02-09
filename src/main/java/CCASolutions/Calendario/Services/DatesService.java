package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDateTime dateO);
	public abstract LocalDateTime getDateOFromDateVAU (DateDTOFromDB dateVAU);
	public abstract DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU);
}
