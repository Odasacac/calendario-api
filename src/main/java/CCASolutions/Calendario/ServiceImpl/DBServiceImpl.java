package CCASolutions.Calendario.ServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;

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
	
	private static final int numeroDeHorasQueTardaEnFullPoblate = 11;
	
	
	public String  poblateDBDesdeArranque(boolean poblarBaseDeDatosAlArrancar, boolean poblarTablasExtra) {
		
		String resultado = "";
		
		if(poblarBaseDeDatosAlArrancar) {
			
			System.out.println("Iniciando la población de la base de datos desde cero.");
			System.out.println("Suele tardar algo más de " + numeroDeHorasQueTardaEnFullPoblate + " horas.");
			String texto = "";
			
			if(poblarTablasExtra) {
				texto="Se poblarán las tablas extra.";
			}
			else {
				texto="No se poblarán las tablas extra";
			}
			
			System.out.println(texto);
			
			PoblateDBDTO poblateDBDTO = new PoblateDBDTO(poblarBaseDeDatosAlArrancar, poblarTablasExtra);
			
			LocalDateTime startingPoblate = LocalDateTime.now();
			resultado = this.poblateDB(poblateDBDTO);	
			LocalDateTime finishingPoblate = LocalDateTime.now();			
			
			Duration duration = Duration.between(startingPoblate, finishingPoblate);		
			System.out.println("Ha tardado: " + duration.toHours() + " horas y " + duration.toMinutesPart() + " minutos.");
		}
		else {
			
			System.out.println("Incluyendo base de datos SOLO adminPW.");
			resultado = this.datosService.poblateSoloPassword();		
		}
			
		return resultado;
	}
	
	public String poblateDB(PoblateDBDTO poblateDBDTO) {
		
		System.out.println("Iniciando poblateDB.");
		String resultado = "";				
		
		if(poblateDBDTO.isPoblar() || poblateDBDTO.isEditar() || poblateDBDTO.isLlamadasAAPis()) {
			
			try {
				
				resultado = "~ Resultados población de la Base de Datos ~";
				if(poblateDBDTO.isPoblar()) {
					
					resultado = resultado + "\n - DATOS: " + this.datosService.poblateDatos();
				}
				
				if(poblateDBDTO.isLlamadasAAPis() && poblateDBDTO.isPoblar()) {
					
					resultado = resultado + "\n - LUNAS: " + this.lunasService.poblateLunasFromOpale(poblateDBDTO.isPoblarTablasExtras());
					resultado = resultado + "\n - APOPERI LUNARES: " + this.apogeosYPerigeosLunaService.poblateApogeosFromOpale();
					resultado = resultado + "\n - SOES: " + this.solsticiosYEquinocciosService.poblateSolsticiosYEquinocciosFromOpale(poblateDBDTO.isPoblarTablasExtras());	
					resultado = resultado + "\n - ECLIPSES: " +this.eclipsesService.poblateEclipsesFromOpale(poblateDBDTO.isPoblarTablasExtras());	
				}
				
				if(poblateDBDTO.isEditar()) {
					
					resultado = resultado + "\n - ACTUALIZAR APOPERIS Y FASES: " + this.apogeosYPerigeosLunaService.updateLunasYApoperisConSelectoOInvertido();	
				}
				
				if(poblateDBDTO.isPoblar()) {
					
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
		
		System.out.println("PoblateDB finalizado.");
		return resultado;
	}

}
