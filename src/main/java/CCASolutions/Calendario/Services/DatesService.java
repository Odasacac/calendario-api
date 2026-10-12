package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.DateDTO;

public interface DatesService {

	public abstract DateDTO getDateVAUFromDateO (LocalDate date);
	public abstract List<DateDTO> getDatesVAUFromRange (LocalDate desde, LocalDate hasta);

}
