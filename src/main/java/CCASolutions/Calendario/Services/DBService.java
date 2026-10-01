package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;
import CCASolutions.Calendario.Enums.ModoPoblacionEnum;

public interface DBService {

	public abstract String poblateDB(PoblateDBDTO poblateDBDTO);
	public abstract String poblateDBDesdeArranque(ModoPoblacionEnum modoPoblacion);
}
