package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.FestividadesDTO;

public interface FestividadesService {

	public abstract String poblateFestividades();
	public abstract FestividadesDTO getFestividades(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
}
