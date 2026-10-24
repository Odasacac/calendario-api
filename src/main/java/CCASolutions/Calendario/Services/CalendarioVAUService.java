package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AnyoIndiceDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MesIndiceDTO;
import CCASolutions.Calendario.DTOs.MetonoIndiceDTO;

/** Calendario VAU por lunaciones y su índice métono -> año VAU -> mes fasal. */
public interface CalendarioVAUService {

	/** Rango de fechas que se pueden consultar: del 21-12-1433 (primer eclípeno invernal apofasal remoto) al final de los datos. */
	public static final LocalDate FECHA_MINIMA = LocalDate.of(1433, 12, 21);
	public static final LocalDate FECHA_MAXIMA = LocalDate.of(2099, 12, 31);

	/** Lunación que contiene la fecha. Se guarda en caché: las lunaciones no cambian. */
	public abstract LunacionDTO getLunacion(LocalDate fecha);

	/** Métonos invernales nuevos que tocan el rango navegable, del más antiguo al más reciente. */
	public abstract List<MetonoIndiceDTO> getMetonos();

	/** Años VAU del métono, del 0 en adelante. */
	public abstract List<AnyoIndiceDTO> getAnyos(Long idMetono);

	/** Meses fasales del año VAU que va de inicio a fin. */
	public abstract List<MesIndiceDTO> getMeses(LocalDate inicio, LocalDate fin);
}
