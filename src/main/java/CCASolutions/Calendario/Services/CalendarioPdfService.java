package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.DateDTO;

public interface CalendarioPdfService {

	public abstract byte[] generarCalendarioAnual(int anyoCivil, LocalDate solsticioInicioAnyo, LocalDate ultimoEclipenoInvernalApofasalRemoto, List<DateDTO> dias);
}
