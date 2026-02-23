package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDate date);
	public abstract LocalDate getDateOFromDateVAU (DateDTOFromDB dateVAU);
	public abstract DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU);
}
