package CCASolutions.Calendario.Services;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public interface SolsticiosYEquinocciosService {
	
	public abstract List<SolsticiosYEquinocciosEntity> getSolsticiosYEquinocciosEntityFromFenomenoDTO(List<FenomenoDTO> fenomenoDTO);
	public abstract String updateSolsticiosYEquinoccios();
	public abstract int getAnyoDelUltimoSOEGuardado();
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url);
	public abstract void saveOne(SolsticiosYEquinocciosEntity soeParaDB);
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetonoViaAPI (LocalDateTime dateO, LocalDateTime dateLastMeton, String url);
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetonoViaDB (LocalDateTime dateO);
	public abstract List<FenomenoDTO> getLastAndNextSOEFrom (LocalDateTime dateO, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono);
}
