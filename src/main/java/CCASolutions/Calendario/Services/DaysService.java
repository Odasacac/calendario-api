package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.DateDTOFromDB;

public interface DaysService {
	
	public abstract long getDiasASumarALaLunaNueva(DateDTOFromDB dateVAU);
}
