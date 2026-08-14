package CCASolutions.Calendario.Services;


import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;


public interface LunasService {
	
	public abstract String poblateLunasFromOpale();
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);
	public abstract AponovosDTO getAponovos(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
	public abstract EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis);
	public abstract VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente);
	public abstract MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente);
}
