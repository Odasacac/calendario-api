package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.SeasonDTO;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SeasonsService {


	public abstract String poblateSeasons();
	public abstract SeasonDTO getVAUSeason(LocalDate date, List<SolsticiosYEquinocciosEntity> allSoes);
}
