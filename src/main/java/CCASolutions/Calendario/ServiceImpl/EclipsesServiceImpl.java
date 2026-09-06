package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.LEPYDTO;
import CCASolutions.Calendario.DTOs.LunarEclipseDTO;
import CCASolutions.Calendario.DTOs.SEPYDTO;
import CCASolutions.Calendario.DTOs.SolarEclipseDTO;
import CCASolutions.Calendario.Entities.AllEclipsesEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.AllEclipsesRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.Utils.FechasApi;


/**
 * EN: Manages solar and lunar eclipses: downloads them from the OPALE API and counts the
 * absolute ones for a VAU date.
 * ES: Gestiona los eclipses solares y lunares: los descarga de la API de OPALE y cuenta los
 * absolutos para una fecha VAU.
 */
@Service
public class EclipsesServiceImpl implements EclipsesService{

	private static final Logger LOG = LoggerFactory.getLogger(EclipsesServiceImpl.class);

	@Autowired
	private DatosRepository datosRepository;

	@Autowired
	private EclipsesRepository eclipsesRepository;

	@Autowired
	private AllEclipsesRepository allEclipsesRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	private final static String API_LUNAR_ECLIPSES = "LEPY";
	private final static String API_SOLAR_ECLIPSES = "SEPY";

	private final static String TOTAL = "TotalEclipse";
	private final static String PARTIAL = "PartialEclipse";
	private final static String PENUMBRAL = "PenumbralEclipse";
	private final static String NON_CENTRAL_PARTIAL = "NonCentralPartialEclipse";
	private final static String CENTRAL_ANULAR = "CentralAnnularEclipse";
	private final static String CENTRAL_TOTAL = "CentralTotalEclipse";

	private final static int PRIMER_ANYO_API = -4700;
	private final static int ULTIMO_ANYO_API = 2100;


	/**
	 * EN: Counts the absolute eclipses gone by since the reference eclipeno and since the last
	 * winter new meton, split into solar and lunar. On the day of an eclipeno nothing is
	 * counted, and during the first meton one solar eclipse is discounted because it is the
	 * one that produced the eclipeno itself.
	 * ES: Cuenta los eclipses absolutos transcurridos desde el eclípeno de referencia y desde
	 * el último métono invernal nuevo, desglosados en solares y lunares. El día de un eclípeno
	 * no se cuenta nada, y durante el primer métono se descuenta un eclipse solar porque es el
	 * que produjo el propio eclípeno.
	 *
	 * @param dateVAU                              EN: VAU date being built. / ES: fecha VAU en construcción.
	 * @param eclipsesAbsolutosDesdeLastEclipenoIN EN: absolute eclipses in range. / ES: eclipses absolutos del rango.
	 * @param date                                 EN: date being consulted. / ES: fecha que se consulta.
	 * @param lastMetonIN                          EN: last winter new meton. / ES: último métono invernal nuevo.
	 * @return EN: the six eclipse counters. / ES: los seis contadores de eclipses.
	 */
	public AbsoluteEclipsesDTO getVAUAbsoluteEclipses(DateDTO dateVAU, List<EclipsesEntity> eclipsesAbsolutosDesdeLastEclipenoIN, LocalDate date, MetonsEntity lastMetonIN) {

		AbsoluteEclipsesDTO absoluteEclipses = new AbsoluteEclipsesDTO ();

		int eclipsesSolaresDesdeLastEclipenoIN = 0;
		int eclipsesLunaresDesdeLastEclipenoIN = 0;
		int solaresDesdeElUltimoMetonoIN = 0;
		int lunaresDesdeElUltimoMetonoIN = 0;

		if(!dateVAU.getEclipenoVAU().isEclipenoINDay()) {

			//Si estamos en el primer métono, hay que restarle 1 porque viene el propio del eclípeno
			if(dateVAU.getMetonoVAU().getMetonsIN().getMetonosINSinceLastEclipenoIN() == 0) {
				solaresDesdeElUltimoMetonoIN = -1;
			}

			LocalDate fechaUltimoMetonoIN = lastMetonIN.getDate().toLocalDate();

			for (EclipsesEntity eclipse : eclipsesAbsolutosDesdeLastEclipenoIN){

				LocalDate fechaEclipse = eclipse.getDate().toLocalDate();

				if(!fechaEclipse.isBefore(date)) {
					continue;
				}

				boolean posteriorAlMetono = !fechaEclipse.isBefore(fechaUltimoMetonoIN);

				if(eclipse.isDeSol()) {

					eclipsesSolaresDesdeLastEclipenoIN++;

					if(posteriorAlMetono) {
						solaresDesdeElUltimoMetonoIN++;
					}
				}
				else if (eclipse.isDeLuna()){

					eclipsesLunaresDesdeLastEclipenoIN++;

					if(posteriorAlMetono) {
						lunaresDesdeElUltimoMetonoIN++;
					}
				}
			}

			if(solaresDesdeElUltimoMetonoIN == -1) {
				solaresDesdeElUltimoMetonoIN = 0;
			}
		}

		absoluteEclipses.setSolarSinceLastEclipenoIN(eclipsesSolaresDesdeLastEclipenoIN);
		absoluteEclipses.setSolarSinceLastMetonoIN(solaresDesdeElUltimoMetonoIN);

		absoluteEclipses.setLunarSinceLastEclipenoIN(eclipsesLunaresDesdeLastEclipenoIN);
		absoluteEclipses.setLunarSinceLastMetonoIN(lunaresDesdeElUltimoMetonoIN);

		absoluteEclipses.setSinceLastEclipenoIN(eclipsesSolaresDesdeLastEclipenoIN + eclipsesLunaresDesdeLastEclipenoIN);
		absoluteEclipses.setSinceLastMetonoIN(solaresDesdeElUltimoMetonoIN + lunaresDesdeElUltimoMetonoIN);

		return absoluteEclipses;
	}



	/**
	 * EN: Downloads every eclipse from year -4700 to 2100, year by year and in two calls per
	 * year, one for lunar and one for solar eclipses.
	 * ES: Descarga todos los eclipses del año -4700 al 2100, año por año y en dos llamadas por
	 * año, una para los lunares y otra para los solares.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateEclipsesFromOpale() {

		if (this.eclipsesRepository.count() > 0) {

			LOG.warn("Ya hay eclipses en la base de datos.");
			return "Error al actualizar los eclipses: ya hay eclipses en la base de datos.";
		}

		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList(API_LUNAR_ECLIPSES, API_SOLAR_ECLIPSES));

		String apiEclipsesLunares = null;
		String apiEclipsesSolares = null;

		for (DatosEntity url : urls) {

			switch (url.getConcepto()) {

				case API_LUNAR_ECLIPSES:
					apiEclipsesLunares = url.getValor();
					break;

				case API_SOLAR_ECLIPSES:
					apiEclipsesSolares = url.getValor();
					break;
			}
		}

		// Antes se inicializaban a cadena vacia y luego se comprobaba != null, con lo que
		// el control nunca saltaba y se llamaba a la API con una URL vacia
		if (apiEclipsesLunares == null || apiEclipsesSolares == null) {

			LOG.error("La URL de la API para obtener los eclipses es nula.");
			return "Error al evaluar los eclipses: la URL de la API para obtener los eclipses es nula.";
		}

		try {

			for (int anyo = PRIMER_ANYO_API; anyo <= ULTIMO_ANYO_API; anyo++) {

				this.actualizarEclipsesLunaresDelAnyo(anyo, apiEclipsesLunares);
				this.actualizarEclipsesSolaresDelAnyo(anyo, apiEclipsesSolares);
			}
		}
		catch (Exception e) {

			LOG.error("Error al evaluar los eclipses", e);
			return "Error al evaluar los eclipses, revisar logs";
		}

		return "Eclipses actualizados sin problema.";
	}

	// PRIVATE METHODS

	/**
	 * EN: Downloads and stores the lunar eclipses of one year. Years after 1 also go into the
	 * working table; the whole range goes into the historical one. Errors are logged and the
	 * year is skipped, so one bad year does not abort the whole download.
	 * ES: Descarga y almacena los eclipses lunares de un año. Los años posteriores al 1 van
	 * además a la tabla de trabajo; el rango completo va a la histórica. Los errores se
	 * registran y el año se salta, de modo que un año malo no aborta toda la descarga.
	 *
	 * @param anyo EN: year to process. / ES: año que se procesa.
	 * @param url  EN: URL template of the lunar eclipse API. / ES: plantilla de URL de la API de eclipses lunares.
	 */
	private void actualizarEclipsesLunaresDelAnyo (int anyo, String url){

		LOG.info("Actualizando los eclipses lunares del anyo: {}", anyo);

		try {

			List<LunarEclipseDTO> eclipsesLunaresDelAnyo = this.getEclipsesLunaresDelAnyoViaAPI(String.valueOf(anyo), url);

			List<EclipsesEntity> eclipsesParaBD = new ArrayList<>();
			List<AllEclipsesEntity> todosLosEclipsesParaBD = new ArrayList<>();

			for(LunarEclipseDTO eclipse : eclipsesLunaresDelAnyo) {

				if(anyo > 0) {

					EclipsesEntity eclipseParaBD = new EclipsesEntity();
					eclipseParaBD.setDeLuna(true);
					eclipseParaBD.setDate(LocalDateTime.parse(eclipse.getDate()));
					eclipseParaBD.setYear(anyo);

					switch(eclipse.getType()) {

						case TOTAL:
							eclipseParaBD.setEsTotal(true);
							break;

						case PARTIAL:
							eclipseParaBD.setEsParcial(true);
							break;

						case PENUMBRAL:
							eclipseParaBD.setEsPenumbral(true);
							break;
					}

					eclipsesParaBD.add(eclipseParaBD);
				}

				AllEclipsesEntity allEclipseParaDB = new AllEclipsesEntity();
				allEclipseParaDB.setDeLuna(true);

				switch(eclipse.getType()) {

					case TOTAL:
						allEclipseParaDB.setTotal(true);
						break;

					case PARTIAL:
						allEclipseParaDB.setParcial(true);
						break;

					case PENUMBRAL:
						allEclipseParaDB.setPenumbral(true);
						break;
				}

				this.rellenarFechaDescompuesta(allEclipseParaDB, eclipse.getDate());

				todosLosEclipsesParaBD.add(allEclipseParaDB);
			}

			this.guardar(eclipsesParaBD, todosLosEclipsesParaBD);

			LOG.info("Actualizados los eclipses lunares del anyo: {}", anyo);
		}
		catch (Exception e) {

			LOG.error("Error al actualizar los eclipses lunares del anyo {}", anyo, e);
		}
	}


	/**
	 * EN: Downloads and stores the solar eclipses of one year, the same way as the lunar ones.
	 * ES: Descarga y almacena los eclipses solares de un año, igual que los lunares.
	 *
	 * @param anyo EN: year to process. / ES: año que se procesa.
	 * @param url  EN: URL template of the solar eclipse API. / ES: plantilla de URL de la API de eclipses solares.
	 */
	private void actualizarEclipsesSolaresDelAnyo (int anyo, String url){

		LOG.info("Actualizando los eclipses solares del anyo: {}", anyo);

		try {

			List<SolarEclipseDTO> eclipsesSolaresDelAnyo = this.getEclipsesSolaresDelAnyoViaAPI(String.valueOf(anyo), url);

			List<EclipsesEntity> eclipsesParaBD = new ArrayList<>();
			List<AllEclipsesEntity> todosLosEclipsesParaBD = new ArrayList<>();

			for(SolarEclipseDTO eclipse : eclipsesSolaresDelAnyo) {

				if(anyo > 0) {

					EclipsesEntity eclipseParaBD = new EclipsesEntity();
					eclipseParaBD.setDeSol(true);
					eclipseParaBD.setDate(LocalDateTime.parse(eclipse.getDate()));
					eclipseParaBD.setYear(anyo);

					switch(eclipse.getType()) {

						case NON_CENTRAL_PARTIAL:
							eclipseParaBD.setEsParcial(true);
							break;

						case CENTRAL_ANULAR:
							eclipseParaBD.setEsAnular(true);
							break;

						case CENTRAL_TOTAL:
							eclipseParaBD.setEsTotal(true);
							break;
					}

					eclipsesParaBD.add(eclipseParaBD);
				}

				AllEclipsesEntity allEclipseParaDB = new AllEclipsesEntity();
				// La tabla historica marcaba los eclipses solares como "de luna"
				allEclipseParaDB.setDeSol(true);

				switch(eclipse.getType()) {

					case NON_CENTRAL_PARTIAL:
						allEclipseParaDB.setParcial(true);
						break;

					case CENTRAL_ANULAR:
						allEclipseParaDB.setAnular(true);
						break;

					case CENTRAL_TOTAL:
						allEclipseParaDB.setTotal(true);
						break;
				}

				this.rellenarFechaDescompuesta(allEclipseParaDB, eclipse.getDate());

				todosLosEclipsesParaBD.add(allEclipseParaDB);
			}

			this.guardar(eclipsesParaBD, todosLosEclipsesParaBD);

			LOG.info("Actualizados los eclipses solares del anyo: {}", anyo);
		}
		catch (Exception e) {

			LOG.error("Error al actualizar los eclipses solares del anyo {}", anyo, e);
		}
	}

	/**
	 * EN: Writes both batches of a year in a single statement each, instead of one INSERT per
	 * eclipse.
	 * ES: Escribe los dos lotes de un año en una sola sentencia cada uno, en lugar de un INSERT
	 * por eclipse.
	 *
	 * @param eclipses         EN: rows for the working table. / ES: filas para la tabla de trabajo.
	 * @param todosLosEclipses EN: rows for the historical table. / ES: filas para la tabla histórica.
	 */
	private void guardar(List<EclipsesEntity> eclipses, List<AllEclipsesEntity> todosLosEclipses) {

		// Un INSERT por lote en vez de uno por eclipse
		if (!eclipses.isEmpty()) {
			this.eclipsesRepository.saveAll(eclipses);
		}

		if (!todosLosEclipses.isEmpty()) {
			this.allEclipsesRepository.saveAll(todosLosEclipses);
		}
	}

	/**
	 * EN: Fills in the six numeric date fields of a historical row, which is how dates before
	 * year 1 are stored.
	 * ES: Rellena los seis campos numéricos de fecha de una fila histórica, que es la forma en
	 * que se guardan las fechas anteriores al año 1.
	 *
	 * @param entidad  EN: row being filled in. / ES: fila que se está rellenando.
	 * @param fechaApi EN: ISO date as returned by the API. / ES: fecha ISO tal y como la devuelve la API.
	 */
	private void rellenarFechaDescompuesta(AllEclipsesEntity entidad, String fechaApi) {

		FechasApi.Descompuesta descompuesta = FechasApi.descomponer(fechaApi);

		entidad.setYear(descompuesta.getYear());
		entidad.setMonth(descompuesta.getMonth());
		entidad.setDay(descompuesta.getDay());
		entidad.setHour(descompuesta.getHour());
		entidad.setMinute(descompuesta.getMinute());
		entidad.setSecond(descompuesta.getSecond());
	}

	/**
	 * EN: Single call to the solar eclipse API for one year. On failure it logs and returns an
	 * empty list, so the loop can carry on.
	 * ES: Llamada única a la API de eclipses solares para un año. Si falla, lo registra y
	 * devuelve una lista vacía, de modo que el bucle pueda continuar.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template. / ES: plantilla de URL.
	 * @return EN: the solar eclipses of that year. / ES: los eclipses solares de ese año.
	 */
	private List<SolarEclipseDTO> getEclipsesSolaresDelAnyoViaAPI(String anyo, String url) {

		// https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}
		String urlParaLlamadaAPISolar = url.replace("{{YYYY}}", anyo);

		try {
			return this.getSEPYDTO(urlParaLlamadaAPISolar);
		}
		catch (Exception e) {

			LOG.error("Error al llamar a SEPY API", e);
			return new ArrayList<>();
		}
	}

	/**
	 * EN: Single call to the lunar eclipse API for one year.
	 * ES: Llamada única a la API de eclipses lunares para un año.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template. / ES: plantilla de URL.
	 * @return EN: the lunar eclipses of that year. / ES: los eclipses lunares de ese año.
	 */
	private List<LunarEclipseDTO> getEclipsesLunaresDelAnyoViaAPI(String anyo, String url) {

		// https://opale.imcce.fr/api/v1/phenomena/eclipses/301/{{YYYY}}
		String urlParaLlamadaAPILunar = url.replace("{{YYYY}}", anyo);

		try {
			return this.getLEPYDTO(urlParaLlamadaAPILunar);
		}
		catch (Exception e) {

			LOG.error("Error al llamar a LEPY API", e);
			return new ArrayList<>();
		}
	}

	/**
	 * EN: Maps the lunar eclipse API response onto the internal DTO, keeping only the eclipses
	 * that carry a greatest-eclipse instant.
	 * ES: Traduce la respuesta de la API de eclipses lunares al DTO interno, conservando sólo
	 * los eclipses que traen instante de máximo.
	 *
	 * @param url EN: full URL to call. / ES: URL completa a la que llamar.
	 * @return EN: the lunar eclipses found. / ES: los eclipses lunares encontrados.
	 */
	private List<LunarEclipseDTO> getLEPYDTO(String url) {

		List<LunarEclipseDTO> eclipsesLunares = new ArrayList<>();

		LEPYDTO apiResponse = restTemplate.getForObject(url, LEPYDTO.class);

		if(apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getLunareclipse() != null) {

			for (LEPYDTO.LunarEclipse eclipse : apiResponse.getResponse().getLunareclipse()) {

				if (eclipse.getEvents() != null && eclipse.getEvents().getGreatest() != null && eclipse.getEvents().getGreatest().getDate() != null) {

					eclipsesLunares.add(new LunarEclipseDTO(String.valueOf(eclipse.getEvents().getGreatest().getDate()), eclipse.getType()));
				}
			}
		}

		return eclipsesLunares;
	}

	/**
	 * EN: Maps the solar eclipse API response onto the internal DTO.
	 * ES: Traduce la respuesta de la API de eclipses solares al DTO interno.
	 *
	 * @param url EN: full URL to call. / ES: URL completa a la que llamar.
	 * @return EN: the solar eclipses found. / ES: los eclipses solares encontrados.
	 */
	private List<SolarEclipseDTO> getSEPYDTO(String url) {

		List<SolarEclipseDTO> eclipsesSolares = new ArrayList<>();

		SEPYDTO apiResponse = restTemplate.getForObject(url, SEPYDTO.class);

		if (apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getData() != null) {

			for (SEPYDTO.SolarEclipse eclipse : apiResponse.getResponse().getData()) {

				if (eclipse.getEvents() != null && eclipse.getEvents().getGreatest() != null && eclipse.getEvents().getGreatest().getDate() != null) {

					eclipsesSolares.add(new SolarEclipseDTO(String.valueOf(eclipse.getEvents().getGreatest().getDate()), eclipse.getType()));
				}
			}
		}

		return eclipsesSolares;
	}
}
