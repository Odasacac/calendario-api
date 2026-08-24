package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;

public interface CasalerosService {
	
	public abstract String poblateCasaleros();
	public abstract CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN);
}
