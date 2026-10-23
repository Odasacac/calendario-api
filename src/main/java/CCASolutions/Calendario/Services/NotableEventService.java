package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EventoDelDiaDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;

public interface NotableEventService {

	public abstract NotableEventDTO getNotableEvent(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);

	/** Fenómenos de ese día y nombre de su evento reseñable (el mismo que NotableEventDTO.today). */
	public abstract EventoDelDiaDTO getEventoDelDia(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
}
