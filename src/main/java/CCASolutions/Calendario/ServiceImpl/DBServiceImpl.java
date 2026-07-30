package CCASolutions.Calendario.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
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
import CCASolutions.Calendario.Support.CatalogoCalendario;

/*
 * ==============================================================================
 * EN: Generates the whole calendar, step by step.
 *
 *     This is the only thing in the application that writes to the tables the read
 *     path caches, so it is also the only thing that has to invalidate those caches.
 *     Without that, a repopulated database would keep serving the previous calendar
 *     from memory until the next restart.
 *
 *     The result string is built with a StringBuilder now: it was concatenated
 *     thirteen times in a row, which allocates and copies a new String each time.
 *
 * ES: Genera el calendario completo, paso a paso.
 *
 *     Es lo unico de la aplicacion que escribe en las tablas que cachea el camino de
 *     lectura, asi que tambien es lo unico que tiene que invalidar esas caches. Sin eso,
 *     una base de datos repoblada seguiria sirviendo el calendario anterior desde memoria
 *     hasta el siguiente reinicio.
 *
 *     La cadena de resultado se construye ahora con un StringBuilder: se concatenaba trece
 *     veces seguidas, y cada concatenacion crea y copia un String nuevo.
 * ==============================================================================
 */
@Service
public class DBServiceImpl implements DBService {

	private static final Logger log = LoggerFactory.getLogger(DBServiceImpl.class);

	/*
	 * EN: Set to true to re-download the ephemeris from the external API. That is the
	 *     three hour part of this job; with it off, only the derived tables are rebuilt.
	 * ES: Poner a true para volver a descargar las efemerides de la API externa. Esa es la
	 *     parte de tres horas de este trabajo; con esto apagado solo se reconstruyen las
	 *     tablas derivadas.
	 */
	private static final boolean LLAMAR_A_LAS_APIS = false;

	private final LunasService lunasService;
	private final SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	private final MetonsService metonsService;
	private final EclipsesService eclipsesService;
	private final EclipenosService eclipenosService;
	private final CasalerosService casalerosService;
	private final DatosService datosService;
	private final DaysService daysService;
	private final WeeksService weeksService;
	private final MonthService monthsService;
	private final FestividadesService festividadesService;
	private final ApogeosYPerigeosLunaService apogeosYPerigeosLunaService;
	private final CatalogoCalendario catalogo;
	private final CacheManager cacheManager;

	public DBServiceImpl(LunasService lunasService,
			SolsticiosYEquinocciosService solsticiosYEquinocciosService,
			MetonsService metonsService,
			EclipsesService eclipsesService,
			EclipenosService eclipenosService,
			CasalerosService casalerosService,
			DatosService datosService,
			DaysService daysService,
			WeeksService weeksService,
			MonthService monthsService,
			FestividadesService festividadesService,
			ApogeosYPerigeosLunaService apogeosYPerigeosLunaService,
			CatalogoCalendario catalogo,
			CacheManager cacheManager) {
		this.lunasService = lunasService;
		this.solsticiosYEquinocciosService = solsticiosYEquinocciosService;
		this.metonsService = metonsService;
		this.eclipsesService = eclipsesService;
		this.eclipenosService = eclipenosService;
		this.casalerosService = casalerosService;
		this.datosService = datosService;
		this.daysService = daysService;
		this.weeksService = weeksService;
		this.monthsService = monthsService;
		this.festividadesService = festividadesService;
		this.apogeosYPerigeosLunaService = apogeosYPerigeosLunaService;
		this.catalogo = catalogo;
		this.cacheManager = cacheManager;
	}

	@Override
	public String poblateDB() {

		StringBuilder resultado = new StringBuilder(512);
		resultado.append("~ Resultados población de la Base de Datos ~");

		try {
			resultado.append("\n - DATOS: ").append(this.datosService.poblateDatos());

			// EN: The slow part: one HTTP call per year against the ephemeris API.
			// ES: La parte lenta: una llamada HTTP por año contra la API de efemerides.
			if (LLAMAR_A_LAS_APIS) {
				resultado.append("\n - LUNAS: ").append(this.lunasService.poblateLunasFromOpale());
				resultado.append("\n - APOPERI LUNARES: ")
						.append(this.apogeosYPerigeosLunaService.poblateApogeosFromOpale());
				resultado.append("\n - SOES: ")
						.append(this.solsticiosYEquinocciosService.poblateSolsticiosYEquinocciosFromOpale());
				resultado.append("\n - ECLIPSES: ").append(this.eclipsesService.poblateEclipsesFromOpale());
			}

			resultado.append("\n - ACTUALIZAR APOPERIS Y FASES: ")
					.append(this.apogeosYPerigeosLunaService.updateLunasYApoperisConSelectoOInvertido());

			resultado.append("\n - METONOS: ").append(this.metonsService.poblateMetonos());
			resultado.append("\n - ECLIPENOS: ").append(this.eclipenosService.poblateEclipenos());
			resultado.append("\n - CASALEROS:").append(this.casalerosService.poblateCasaleros());

			resultado.append("\n - DÍAS: ").append(this.daysService.poblateDays());
			resultado.append("\n - SEMANAS: ").append(this.weeksService.poblateWeeks());
			resultado.append("\n - MESES: ").append(this.monthsService.poblateMonths());
			resultado.append("\n - FESTIVIDADES: ").append(this.festividadesService.poblateFestividades());

		} catch (Exception e) {

			log.error("Error poblando la base de datos", e);
			resultado.append("\n - Ha habido un error poblando la base de datos: chequear logs");

		} finally {

			/*
			 * EN: Always invalidate, even after a failure: a partially rebuilt calendar must
			 *     not be mixed with the previous one held in memory.
			 * ES: Invalidar siempre, incluso tras un fallo: un calendario reconstruido a
			 *     medias no debe mezclarse con el anterior que se mantiene en memoria.
			 */
			invalidarCaches();
		}

		return resultado.toString();
	}

	/*
	 * EN: Drops the in-memory catalog of immutable tables and the cache of converted
	 *     dates, so the next request rebuilds both from the new data.
	 * ES: Descarta el catalogo en memoria de tablas inmutables y la cache de fechas
	 *     convertidas, para que la siguiente peticion reconstruya ambos con los datos nuevos.
	 */
	private void invalidarCaches() {

		this.catalogo.invalidar();

		Cache fechas = this.cacheManager.getCache(DatesServiceImpl.CACHE_FECHAS_VAU);
		if (fechas != null) {
			fechas.clear();
			log.info("Caché de fechas VAU limpiada.");
		}
	}
}
