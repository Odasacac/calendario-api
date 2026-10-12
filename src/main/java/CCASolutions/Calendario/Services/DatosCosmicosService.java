package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosRangoDTO;

public interface DatosCosmicosService {

	public abstract DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date);
	public abstract DatosCosmicosRangoDTO getDatosCosmicosRango(LocalDate desde, LocalDate hasta);
	public abstract DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date, DatosCosmicosRangoDTO datosCosmicosRango);
}
