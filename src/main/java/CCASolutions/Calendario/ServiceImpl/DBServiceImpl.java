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
import CCASolutions.Calendario.Services.MidsisonService;
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
	
	@Autowired
	private MidsisonService midsisonService;
	
	public String poblateDB() {
		
		String resultado = "~ Resultados población de la Base de Datos ~";
		boolean ejecutarSoloLoNuevo=true;
		boolean llamadasAAPis=false;
		
		try {
			resultado = resultado + "\n - DATOS: " + this.datosService.poblateDatos();
			
			if(llamadasAAPis) {
				resultado = resultado + "\n - LUNAS: " + this.lunasService.poblateLunasFromOpale();
				resultado = resultado + "\n - APOPERI LUNARES: " + this.apogeosYPerigeosLunaService.poblateApogeosFromOpale();
				resultado = resultado + "\n - SOES: " + this.solsticiosYEquinocciosService.poblateSolsticiosYEquinocciosFromOpale();	
				resultado = resultado + "\n - ECLIPSES: " +this.eclipsesService.poblateEclipsesFromOpale();	
			}
			
			resultado = resultado + "\n - ACTUALIZAR APOPERIS Y FASES: " + this.apogeosYPerigeosLunaService.updateLunasYApoperisConSelectoOInvertido();		
			resultado = resultado + "\n - MIDSISONS: " + this.midsisonService.poblateMidsison();
			
			resultado = resultado + "\n - METONOS: " + this.metonsService.poblateMetonos();			
			resultado = resultado + "\n - ECLIPENOS: " +this.eclipenosService.poblateEclipenos();
			resultado = resultado + "\n - CASALEROS:" + this.casalerosService.poblateCasaleros();
			
			resultado = resultado + "\n - DÍAS: " + this.daysService.poblateDays();
			resultado = resultado + "\n - SEMANAS: " + this.weeksService.poblateWeeks();
			resultado = resultado + "\n - MESES: " + this.monthsService.poblateMonths();
			resultado = resultado + "\n - FESTIVIDADES: " +this.festividadesService.poblateFestividades();
		}
		catch(Exception e) {
			
			System.out.println("Error poblando la base de datos: " + e);
			resultado = resultado + "\n - Ha habido un error poblando la base de datos: chequear logs";
		}
		
		
		return resultado;
	}

}
