package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;


public interface SolsticiosYEquinocciosService {
	
	public abstract String poblateSolsticiosYEquinocciosFromOpale();
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url);
	public abstract YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN);
}
