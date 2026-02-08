package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;


public interface MetonsService {
	
	public abstract LocalDateTime getLastMetonDate (LocalDateTime dateO);
	public abstract String checkMetonosSinceTo(int since, int to);
}
