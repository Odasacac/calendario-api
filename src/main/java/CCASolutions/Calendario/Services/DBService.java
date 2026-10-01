package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;

public interface DBService {

	public abstract String poblateDB(PoblateDBDTO poblateDBDTO);
	public abstract String poblateDBDesdeArranque(boolean poblarConDUMP, boolean poblarConRequest, boolean poblarAlArrancarDesdeCeroConTablasExtra, boolean poblarAlArrancarDesdeCeroSinTablasExtra);
}
