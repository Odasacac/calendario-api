package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;

public interface EclipsesService {
	
	public abstract String poblateEclipsesFromOpale();
	public abstract AbsoluteEclipsesDTO getVAUAbsoluteEclipses(DateDTO dateVAU, List<EclipsesEntity> eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN, LocalDate date, MetonsEntity lastMetonIN);
}
