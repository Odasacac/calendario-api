package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasService {
	
	public abstract String poblateLunas();
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
	public abstract LunasEntity getPrimeraLunaNuevaAnteriorAFecha(List<LunasEntity> lunasDesdeAnyoMinimoAAnyoMaximo, LocalDate fecha);
	public abstract LunasEntity getPrimeraLunaNuevaPosteriorAFecha(List<LunasEntity> lunasDesdeAnyoMinimoAAnyoMaximo, LocalDate fecha);
}
