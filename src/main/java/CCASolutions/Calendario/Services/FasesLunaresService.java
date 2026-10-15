package CCASolutions.Calendario.Services;


import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.ApoperisLunaresEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;


public interface FasesLunaresService {
	
	public abstract String poblateLunasFromOpale(boolean poblarTablasExtras);
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
	public abstract AponovosDTO getAponovos(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
	public abstract EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApoperisLunaresEntity> allApoperis);
	public abstract VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<FasesLunaresEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas);
	
}
