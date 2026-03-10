package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.DateVAUDTO;

public interface DaysService {
	
	public abstract long getDiasASumarALaLunaNueva(DateVAUDTO dateVAU);
}
