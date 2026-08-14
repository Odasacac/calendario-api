package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;

public interface UtilsService {

	public abstract DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date);
}
