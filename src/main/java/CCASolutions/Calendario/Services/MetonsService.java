package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;

import CCASolutions.Calendario.Entities.MetonsEntity;


public interface MetonsService {
	
	public abstract LocalDateTime getLastMetonDate (LocalDateTime dateO);
	public abstract MetonsEntity getNextMetonDateByYear (int year);
	public abstract String checkMetonosSinceTo(int since, int to);
}
