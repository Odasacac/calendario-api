package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DateDTO;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDate date);

}
