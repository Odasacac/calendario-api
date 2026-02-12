package CCASolutions.Calendario.Services;

import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;


public interface SolsticiosYEquinocciosService {
	
	public abstract String poblateSolsticiosYEquinoccios();
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url);
}
