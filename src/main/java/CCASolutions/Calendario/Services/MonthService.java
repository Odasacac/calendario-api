package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;

public interface MonthService {
	
	public abstract int getSeasonDelMesHibridoSiLoEs (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, FenomenoDTO nextSOE, LocalDateTime dateO);

}
