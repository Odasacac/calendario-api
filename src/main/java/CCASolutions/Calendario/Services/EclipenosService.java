package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;

public interface EclipenosService {
	public abstract String poblateEclipenos();
	public abstract EclipenoINDTO getVAUEclipeno(List<EclipenosEntity> allEclipenos, EclipenosEntity lastEclipenoSelecto, LocalDate date);
	public abstract EclipenoSelectoDTO getVAUEclipenoSelecto(EclipenosEntity lastEclipenoSelecto, LocalDate date);
	public abstract EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date);
	public abstract EclipenosEntity getLastEclipenoInvernalApofasalRemoto(List<EclipenosEntity> allEclipenos, LocalDate date);
}
