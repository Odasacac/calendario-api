package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SolsticiosYEquinocciosService {
	
	public abstract String poblateSolsticiosYEquinoccios();
	public abstract int getAnyoDelUltimoSOEGuardado();
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url);
	public abstract void saveOne(SolsticiosYEquinocciosEntity soeParaDB);
	public abstract List<SolsticiosYEquinocciosEntity> getSolsticiosYEquinocciosDesdeElMetono (LocalDateTime dateO, LocalDateTime dateLastMeton);
	public abstract List<SolsticiosYEquinocciosEntity> getLastAndNextSOEFrom (LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono);
}
