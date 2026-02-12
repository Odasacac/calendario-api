package CCASolutions.Calendario.Services;

import java.util.List;

import CCASolutions.Calendario.DTOs.LunarPhaseDTO;

public interface LunasService {
	
	public abstract String poblateLunas();
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
}
