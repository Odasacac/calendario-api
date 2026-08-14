package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonoInvernalApofasalRemotoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;

public interface MetonsService {
	
	public abstract String poblateMetonos();
	public abstract MetonDTO getVAUMeton (MetonsEntity lastMetonIApofasalRemoto, EclipenosEntity lastEclipenoINSelecto, List<MetonsEntity> metons, LocalDate date);
	public abstract MetonoInvernalApofasalRemotoDTO getMetonoInvernalApofasalRemoto(EclipenosEntity lastEclipenoInvernalApofasalRemoto, List<MetonsEntity> allMetons, LocalDate date);
	public abstract MetonsEntity getLastMetonIApofasalRemoto(List<MetonsEntity> allMetons, LocalDate date);
	public abstract MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date); 
}
