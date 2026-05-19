package CCASolutions.Calendario.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	
	public String poblateDB() {
		
		String resultado = "~ Resultados población de la Base de Datos ~";
		String resultadoDatos="";
		String resultadoDias="";
		String resultadoWeeks="";
		String resultadoMonths ="";
		String resultadoLunas="";
		String resultadoSoes="";
		String resultadoMetonos="";
		String resultadoEclipses="";
		String resultadoEclipenos="";
		String resultadoCasaleros="";
		String resultadoFestividades="";
		
		try {
			
			resultadoDatos = this.datosService.poblateDatos();
			
			resultadoDias = this.daysService.poblateDays();
			
			resultadoWeeks = this.weeksService.poblateWeeks();
			
			resultadoMonths = this.monthsService.poblateMonths();
			
			resultadoFestividades= this.festividadesService.poblateFestividades();
			
			resultadoLunas = this.lunasService.poblateLunas();		
						
			resultadoSoes = this.solsticiosYEquinocciosService.poblateSolsticiosYEquinoccios();		
					
			resultadoMetonos = this.metonsService.checkMetonosSinceToViaAPI();				
						
			resultadoEclipses = this.eclipsesService.poblateEclipses();			
							
			resultadoEclipenos = this.eclipenosService.poblateEclipenos();

			resultadoCasaleros = this.casalerosService.poblateCasaleros();
								
		}
		catch(Exception e) {
			
			System.out.println("Error poblando la base de datos: " + e);
		}
		
		
		return resultado + "\n" + resultadoDatos + "\n" + resultadoDias + "\n" + resultadoWeeks + "\n" + resultadoMonths + "\n" + resultadoFestividades + "\n" + resultadoLunas + "\n" + resultadoSoes + "\n" + resultadoMetonos + "\n" + resultadoEclipses + "\n" + resultadoEclipenos + "\n" + resultadoCasaleros;
	}

}
