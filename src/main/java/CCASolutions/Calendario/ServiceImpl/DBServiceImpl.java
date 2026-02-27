package CCASolutions.Calendario.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Services.DBService;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
public class DBServiceImpl implements DBService {
	
	@Autowired
	private LunasService lunasService;
	
	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Autowired
	private MetonsService metonsService;
	
	@Autowired
	private EclipsesService eclipsesService;
	
	@Autowired
	private EclipenosService eclipenosService;

	public String poblateDB() {
		
		String resultado = "Error al actualizar la base de datos, revisar logs.";
		
		try {
			
			System.out.println(lunasService.poblateLunas());			
			System.out.println(solsticiosYEquinocciosService.poblateSolsticiosYEquinoccios());			
			System.out.println(metonsService.checkMetonosSinceToViaAPI());			
			System.out.println(eclipsesService.poblateEclipses());			
			System.out.println(eclipenosService.poblateEclipenos());
			resultado = "Base de datos actualizada correctamente";
		}
		catch(Exception e) {
			
			System.out.println("Error poblando la base de datos: " + e);
		}
		
		
		return resultado;
	}

}
