package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DateDTO;
//import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.DateVAUDTO;
import CCASolutions.Calendario.Responses.FromDateVAUToDateOResponse;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDate date);
	public abstract FromDateVAUToDateOResponse getDateOFromDateVAU (DateVAUDTO dateVAU);
	//public abstract DateDTOFromDB getDateDTOFromDB(DateVAUDTO dateVAU);
}
