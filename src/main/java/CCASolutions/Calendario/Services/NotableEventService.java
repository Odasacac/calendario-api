package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;

public interface NotableEventService {

	public abstract NotableEventDTO getNotableEvent(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
}
