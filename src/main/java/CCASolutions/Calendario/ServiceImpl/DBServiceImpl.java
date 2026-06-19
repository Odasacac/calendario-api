package CCASolutions.Calendario.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Services.ApogeosYPerigeosLunaService;
import CCASolutions.Calendario.Services.CasalerosService;
import CCASolutions.Calendario.Services.DBService;
import CCASolutions.Calendario.Services.DatosService;
import CCASolutions.Calendario.Services.DaysService;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.Services.FestividadesService;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.MonthService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;
import CCASolutions.Calendario.Services.WeeksService;

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
	
	@Autowired
	private CasalerosService casalerosService;
	
	@Autowired
	private DatosService datosService;
	
	@Autowired
	private DaysService daysService;
	
	@Autowired
	private WeeksService weeksService;
	
	@Autowired
	private MonthService monthsService;
	
	@Autowired
	private FestividadesService festividadesService;
	
	@Autowired
	private ApogeosYPerigeosLunaService apogeosYPerigeosLunaService;
	
	
	public String poblateDB() {
		
		String resultado = "~ Resultados población de la Base de Datos ~";
		
		try {
			
			resultado = resultado + "\n - DATOS: " + this.datosService.poblateDatos();
			
			resultado = resultado + "\n - DÍAS: " + this.daysService.poblateDays();
			
			resultado = resultado + "\n - SEMANAS: " + this.weeksService.poblateWeeks();
			
			resultado = resultado + "\n - MESES: " + this.monthsService.poblateMonths();
			
			resultado = resultado + "\n - FESTIVIDADES: " +this.festividadesService.poblateFestividades();
			
			resultado = resultado + "\n - LUNAS: " + this.lunasService.poblateLunas();
			
			//resultado = resultado + "\n - APOPERI LUNARES: " + this.apogeosYPerigeosLunaService.poblateApogeos();
						
			resultado = resultado + "\n - SOES: " + this.solsticiosYEquinocciosService.poblateSolsticiosYEquinoccios();		
					
			resultado = resultado + "\n - METONOS: " + this.metonsService.poblateMetonosViaDB();	
						
			resultado = resultado + "\n - ECLIPSES: " +this.eclipsesService.poblateEclipses();			
							
			resultado = resultado + "\n - ECLIPENOS: " +this.eclipenosService.poblateEclipenos();

			resultado = resultado + "\n - CASALEROS:" + this.casalerosService.poblateCasaleros();
								
		}
		catch(Exception e) {
			
			System.out.println("Error poblando la base de datos: " + e);
		}
		
		
		return resultado;
	}

}
