package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;

import CCASolutions.Calendario.DTOs.DateDTO;

public interface DatesService {
	public abstract DateDTO getDateVAU (LocalDateTime dateO);
}
