package CCASolutions.Calendario.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import CCASolutions.Calendario.Services.TablasReferenciaService;
import CCASolutions.Calendario.Services.WeeksService;

/**
 * EN: Runs the whole database population in the right order and empties the reference
 * table cache afterwards.
 * ES: Ejecuta el poblado completo de la base de datos en el orden correcto y vacía después
 * la caché de las tablas de referencia.
 */
@Service
public class DBServiceImpl implements DBService {

	private static final Logger LOG = LoggerFactory.getLogger(DBServiceImpl.class);

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

	@Autowired
	private TablasReferenciaService tablasReferenciaService;

	/**
	 * EN: Chains every population phase in dependency order: base data first, then the
	 * downloads from the OPALE APIs, then the pairing of moon phases with apogees and
	 * perigees, and finally everything derived from those (midsisons, metons, eclipenos,
	 * casaleros) plus the fixed reference tables. Each phase reports its own outcome and a
	 * failure in one does not stop the rest.
	 * ES: Encadena todas las fases del poblado en orden de dependencia: primero los datos
	 * base, después las descargas de las APIs de OPALE, luego el emparejamiento de fases
	 * lunares con apogeos y perigeos, y por último todo lo que se deriva de ello (midsisons,
	 * métonos, eclípenos, casaleros) más las tablas de referencia fijas. Cada fase informa
	 * de su propio resultado y un fallo en una no detiene las demás.
	 *
	 * @param poblateDBDTO EN: flags choosing which phases to run. / ES: banderas que eligen qué fases se ejecutan.
	 * @return EN: a multi-line report, one line per phase. / ES: un informe de varias líneas, una por fase.
	 */
	public String poblateDB(PoblateDBDTO poblateDBDTO) {

		StringBuilder resultado = new StringBuilder("~ Resultados población de la Base de Datos ~");

		try {

			if(poblateDBDTO.isPoblar()) {
				resultado.append("\n - DATOS: ").append(this.datosService.poblateDatos());
			}

			if(poblateDBDTO.isLlamadasAAPis() && poblateDBDTO.isPoblar()) {
				resultado.append("\n - LUNAS: ").append(this.lunasService.poblateLunasFromOpale());
				resultado.append("\n - APOPERI LUNARES: ").append(this.apogeosYPerigeosLunaService.poblateApogeosFromOpale());
				resultado.append("\n - SOES: ").append(this.solsticiosYEquinocciosService.poblateSolsticiosYEquinocciosFromOpale());
				resultado.append("\n - ECLIPSES: ").append(this.eclipsesService.poblateEclipsesFromOpale());
			}

			if(poblateDBDTO.isEditar()) {
				resultado.append("\n - ACTUALIZAR APOPERIS Y FASES: ").append(this.apogeosYPerigeosLunaService.updateLunasYApoperisConSelectoOInvertido());
			}

			if(poblateDBDTO.isPoblar()) {
				resultado.append("\n - MIDSISONS: ").append(this.midsisonService.poblateMidsison());
				resultado.append("\n - METONOS: ").append(this.metonsService.poblateMetonos());
				resultado.append("\n - ECLIPENOS: ").append(this.eclipenosService.poblateEclipenos());
				resultado.append("\n - CASALEROS: ").append(this.casalerosService.poblateCasaleros());
				resultado.append("\n - DÍAS: ").append(this.daysService.poblateDays());
				resultado.append("\n - SEMANAS: ").append(this.weeksService.poblateWeeks());
				resultado.append("\n - MESES: ").append(this.monthsService.poblateMonths());
				resultado.append("\n - SEASONS: ").append(this.seasonsService.poblateSeasons());
				resultado.append("\n - FESTIVIDADES: ").append(this.festividadesService.poblateFestividades());
			}
		}
		catch(Exception e) {

			LOG.error("Error poblando la base de datos", e);
			resultado.append("\n - Ha habido un error poblando la base de datos: chequear logs");
		}
		finally {

			// Las tablas de referencia viven en cache: tras repoblar hay que descartarla
			this.tablasReferenciaService.limpiarCache();
		}

		return resultado.toString();
	}
}
