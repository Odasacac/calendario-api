package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface LunasService {
	
	public abstract LunasEntity getNewMoonBeforeADate(LocalDateTime date);
	public abstract LunasEntity getNewMoonFromSOEAndMonthOfSeason(SolsticiosYEquinocciosEntity lastSOE, int monthOfSeason);
	public abstract LunasEntity getLastNewMoonForADateO(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo);
	public abstract List<LunasEntity> getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(List<LunasEntity> fasesLunaresDelAnyo, SolsticiosYEquinocciosEntity lastSOE, SolsticiosYEquinocciosEntity nextSOE);
	public abstract String poblateLunas();
	public abstract List<LunasEntity> getFasesLunaresDelAnyo(String anyo);
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
	public abstract int getAnyoDeLaUltimaLunaGuardada();
	public abstract void saveOne(LunasEntity lunaParaDB);
	public abstract boolean esDateOLunaNueva(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo);
	public abstract int getLunasNuevasPasadasDesdeLastSOEHastaDateO (List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, SolsticiosYEquinocciosEntity lastSOE, LocalDateTime dateO);
}
