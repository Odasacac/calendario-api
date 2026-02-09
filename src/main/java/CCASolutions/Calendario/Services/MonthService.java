package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface MonthService {
	
	public abstract int getSeasonDelMesHibridoSiLoEs (List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, SolsticiosYEquinocciosEntity lastSOE, SolsticiosYEquinocciosEntity nextSOE, LocalDateTime dateO);

}
