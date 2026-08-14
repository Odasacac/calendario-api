package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;

public interface CasaleroService {
	
	public abstract CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN);
}
