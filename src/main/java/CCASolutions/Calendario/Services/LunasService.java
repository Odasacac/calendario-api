package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Entities.LunasEntity;

public interface LunasService {
	
	public abstract List<LunarPhaseDTO> getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(List<LunarPhaseDTO> fasesLunaresDelAnyo, FenomenoDTO lastSOE, FenomenoDTO nextSOE);
	public abstract List<LunasEntity> getLunasEntityFromLunarPhaseDTO(List<LunarPhaseDTO> lunarPhases);
	public abstract String poblateLunas();
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaDB(String anyo);
	public abstract int getAnyoDeLaUltimaLunaGuardada();
	public abstract void saveOne(LunasEntity lunaParaDB);
	public abstract boolean esDateOLunaNueva(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo);
	public abstract int lunasNuevasPasadasDesdeLastSOEHastaDateO (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, LocalDateTime dateO);
}
