package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDateTime dateO);
	public abstract LocalDate getDateOFromDateVAU (DateDTOFromDB dateVAU);
	public abstract DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU);
}
