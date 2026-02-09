package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.DateDTOFromDB;

public interface DaysService {
	
	public abstract int getDiasASumarALaLunaNueva(DateDTOFromDB dateVAU);
}
