package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateWithEntitiesDTO;

public interface DatesService {
	public abstract DateDTO getDateVAUFromDateO (LocalDateTime dateO);
	public abstract LocalDateTime getDateOFromDateVAU (DateDTO dateVAU);
	public abstract DateWithEntitiesDTO validateDTO(DateDTO dateVAU);
}
