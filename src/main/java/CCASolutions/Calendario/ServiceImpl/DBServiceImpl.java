package CCASolutions.Calendario.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;
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
import CCASolutions.Calendario.Services.SeasonsService;
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
	
	@Autowired
	private SeasonsService seasonsService;
	
	public String poblateDB(PoblateDBDTO poblateDBDTO) {
		
		String resultado = "";
		
		boolean poblar= false;
		boolean editar = false;
		boolean llamadasAApis = false;
		boolean ejecutarPoblate = false;
		
		if(poblateDBDTO.isVacia()) {
			poblar = poblateDBDTO.isPoblarDesdeCero();
			editar = poblateDBDTO.isPoblarDesdeCero();
			llamadasAApis = poblateDBDTO.isPoblarDesdeCero();
			
			if(poblateDBDTO.isPoblarDesdeCero()) {
				System.out.println("Iniciando la población de la base de datos desde cero.");
				ejecutarPoblate = true;
			}
			else {
				System.out.println("Incluyendo base de datos SOLO adminPW.");
				System.out.println(this.datosService.poblateSoloPassword());
				ejecutarPoblate = false;
			}
			
		}
		else {
			poblar = poblateDBDTO.isPoblar();
			editar = poblateDBDTO.isEditar();
			llamadasAApis = poblateDBDTO.isLlamadasAAPis();
			System.out.println("Iniciando la actualización de la base de datos por petición externa.");
			ejecutarPoblate = true;
		}
		
		if((poblar || editar || llamadasAApis) && ejecutarPoblate) {
			
			try {
				resultado = "~ Resultados población de la Base de Datos ~";
				if(poblar) {
					resultado = resultado + "\n - DATOS: " + this.datosService.poblateDatos(poblateDBDTO.isPoblarDesdeCero());
				}
				
				if(llamadasAApis && poblar) {
					resultado = resultado + "\n - LUNAS: " + this.lunasService.poblateLunasFromOpale();
					resultado = resultado + "\n - APOPERI LUNARES: " + this.apogeosYPerigeosLunaService.poblateApogeosFromOpale();
					resultado = resultado + "\n - SOES: " + this.solsticiosYEquinocciosService.poblateSolsticiosYEquinocciosFromOpale();	
					resultado = resultado + "\n - ECLIPSES: " +this.eclipsesService.poblateEclipsesFromOpale();	
				}
				
				if(editar) {
					resultado = resultado + "\n - ACTUALIZAR APOPERIS Y FASES: " + this.apogeosYPerigeosLunaService.updateLunasYApoperisConSelectoOInvertido();	
				}
				
				if(poblar) {
					resultado = resultado + "\n - MIDSISONS: " + this.midsisonService.poblateMidsison();			
					resultado = resultado + "\n - METONOS: " + this.metonsService.poblateMetonos();			
					resultado = resultado + "\n - ECLIPENOS: " +this.eclipenosService.poblateEclipenos();
					resultado = resultado + "\n - CASALEROS:" + this.casalerosService.poblateCasaleros();		
					resultado = resultado + "\n - DÍAS: " + this.daysService.poblateDays();
					resultado = resultado + "\n - SEMANAS: " + this.weeksService.poblateWeeks();
					resultado = resultado + "\n - MESES: " + this.monthsService.poblateMonths();
					resultado = resultado + "\n - SEASONS: " + this.seasonsService.poblateSeasons();
					resultado = resultado + "\n - FESTIVIDADES: " +this.festividadesService.poblateFestividades();
				}
			}	
				
			catch(Exception e) {
				
				System.out.println("Error poblando la base de datos: " + e);
				resultado = resultado + "\n - Ha habido un error poblando la base de datos: chequear logs";
			}
		}
		else {
			resultado = "No se ha poblado la base de datos.";
		}
		
		
		
		
		return resultado;
	}

}
