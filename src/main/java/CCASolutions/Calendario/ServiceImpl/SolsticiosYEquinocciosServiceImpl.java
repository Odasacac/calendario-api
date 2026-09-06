package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.GASYEFDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.AllSoEsEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.AllSoEsRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;
import CCASolutions.Calendario.Utils.FechasApi;

/**
 * EN: Manages the solstices and equinoxes and works out the VAU year from them.
 * ES: Gestiona los solsticios y equinoccios y calcula a partir de ellos el año VAU.
 */
@Service
public class SolsticiosYEquinocciosServiceImpl implements SolsticiosYEquinocciosService{

	private static final Logger LOG = LoggerFactory.getLogger(SolsticiosYEquinocciosServiceImpl.class);

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	@Autowired
	private DatosRepository datosRepository;

	@Autowired
	private AllSoEsRepository allSoEsRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	private final static String API_SOES = "ASYEF";

	private final static String SI = "WinterSolstice";
	private final static String EP = "VernalEquinox";
	private final static String SV = "SummerSolstice";
	private final static String EO = "AutumnalEquinox";

	private final static int PRIMER_ANYO_API = -4700;
	private final static int ULTIMO_ANYO_API = 2100;


	/**
	 * EN: Counts the winter solstices between the reference meton and the date, which is what
	 * the VAU year is. A date landing exactly on a winter solstice belongs to no year and gets
	 * a dash instead of a number.
	 * ES: Cuenta los solsticios de invierno entre el métono de referencia y la fecha, que es
	 * justo lo que es el año VAU. Una fecha que cae exactamente en un solsticio de invierno no
	 * pertenece a ningún año y recibe un guion en lugar de un número.
	 *
	 * @param lastEclipenoIN                                EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param date                                          EN: date being consulted. / ES: fecha que se consulta.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas EN: solstices and equinoxes in range. / ES: solsticios y equinoccios del rango.
	 * @param lastMetonIN                                   EN: last winter new meton. / ES: último métono invernal nuevo.
	 * @return EN: the VAU year and the winter solstice flag. / ES: el año VAU y la marca de solsticio de invierno.
	 */
	public YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN) {

		YearDTO vauYear = new YearDTO();

		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		boolean caeEnSolsticioDeInvierno = false;
		int year = 0;

		LocalDate fechaMetonoIN = lastMetonIN.getDate().toLocalDate();

		for (SolsticiosYEquinocciosEntity soe : soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas) {

			if (!soe.isSolsticioInvierno()) {
				continue;
			}

			LocalDate fechaSoe = soe.getDate().toLocalDate();

			if (fechaSoe.isEqual(date)) {

				caeEnSolsticioDeInvierno = true;
				break;
			}

			if (fechaSoe.isBefore(date) && fechaSoe.isAfter(fechaMetonoIN)) {
				year++;
			}
		}

		vauYear.setEsSolsticioDeInvierno(caeEnSolsticioDeInvierno);
		vauYear.setSolsticiosDeInviernoSinceLastMetonIN(caeEnSolsticioDeInvierno ? "-" : String.valueOf(year));

		int numberOfYear = year + 1;

		if(lastEclipenoIN.getDate().toLocalDate().isEqual(date) || fechaMetonoIN.isEqual(date)) {

			numberOfYear = numberOfYear - 1;
		}

		vauYear.setNumberOfYear(numberOfYear);

		return vauYear;
	}


	/**
	 * EN: Downloads the four solstices and equinoxes of every year from -4700 to 2100. Years
	 * after 1 also go into the working table; the whole range goes into the historical one.
	 * ES: Descarga los cuatro solsticios y equinoccios de cada año del -4700 al 2100. Los años
	 * posteriores al 1 van además a la tabla de trabajo; el rango completo va a la histórica.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateSolsticiosYEquinocciosFromOpale() {

		String resultado = "Solsticios y equinoccios actualizados sin problema";

		DatosEntity apiGetSYEUrl = datosRepository.findByConcepto(API_SOES);

		if (apiGetSYEUrl == null || apiGetSYEUrl.getValor() == null) {

			LOG.error("La URL de la API para obtener los soes es nula.");
			return "Error al actualizar los solsticios y equinoccios: la URL de la API para obtener los soes es nula.";
		}

		if (this.solsticiosYEquinocciosRepository.count() > 0) {

			LOG.warn("Ya hay soes en la base de datos.");
			return "Error al actualizar los solsticios y equinoccios: ya hay soes en la base de datos.";
		}

		for (int anyo = PRIMER_ANYO_API; anyo < ULTIMO_ANYO_API; anyo++) {

			LOG.info("Actualizando los solsticios y equinoccios del anyo: {}", anyo);

			try {
				List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = this.getSolsticiosYEquinocciosDelAnyoViaAPI(String.valueOf(anyo), apiGetSYEUrl.getValor());

				if (solsticiosYEquinocciosDelAnyo.isEmpty()) {

					LOG.warn("No se han obtenido solsticios ni equinoccios de la API para el anyo {}.", anyo);
					resultado = "Error al actualizar solsticios y equinoccios: no se han obtenido solsticios ni equinoccios de la API.";
					continue;
				}

				List<SolsticiosYEquinocciosEntity> soesDelAnyo = new ArrayList<>();
				List<AllSoEsEntity> todosLosSoesDelAnyo = new ArrayList<>();

				for (FenomenoDTO soeAPI : solsticiosYEquinocciosDelAnyo) {

					if (anyo > 0) {
						soesDelAnyo.add(this.crearSoe(soeAPI));
					}

					todosLosSoesDelAnyo.add(this.crearAllSoe(soeAPI));
				}

				// Un INSERT por lote en vez de uno por fenómeno
				if (!soesDelAnyo.isEmpty()) {
					this.solsticiosYEquinocciosRepository.saveAll(soesDelAnyo);
				}

				this.allSoEsRepository.saveAll(todosLosSoesDelAnyo);

				LOG.info("Actualizados los solsticios y equinoccios del anyo: {}", anyo);
			}
			catch (Exception e) {

				LOG.error("Error al actualizar solsticios y equinoccios del anyo {}", anyo, e);
				resultado = "Error al actualizar solsticios y equinoccios, checkear logs.";
			}
		}

		return resultado;
	}

	/**
	 * EN: Builds one working-table row, tagging which season it opens.
	 * ES: Construye una fila de la tabla de trabajo, etiquetando qué estación abre.
	 *
	 * @param soeAPI EN: phenomenon as returned by the API. / ES: fenómeno tal y como lo devuelve la API.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private SolsticiosYEquinocciosEntity crearSoe(FenomenoDTO soeAPI) {

		SolsticiosYEquinocciosEntity soeParaDB = new SolsticiosYEquinocciosEntity();

		switch (soeAPI.getPhenomena()) {

			case SI:
				soeParaDB.setSolsticioInvierno(true);
				soeParaDB.setStartingSeason(1);
				break;

			case EP:
				soeParaDB.setEquinoccioPrimavera(true);
				soeParaDB.setStartingSeason(2);
				break;

			case SV:
				soeParaDB.setSolsticioVerano(true);
				soeParaDB.setStartingSeason(3);
				break;

			case EO:
				soeParaDB.setEquinoccioOtonyo(true);
				soeParaDB.setStartingSeason(4);
				break;
		}

		LocalDateTime fecha = LocalDateTime.parse(soeAPI.getDate());

		soeParaDB.setYear(fecha.getYear());
		soeParaDB.setDate(fecha);

		return soeParaDB;
	}

	/**
	 * EN: Builds one historical-table row, with the date split into numeric fields so that
	 * years before 1 fit.
	 * ES: Construye una fila de la tabla histórica, con la fecha troceada en campos numéricos
	 * para que quepan los años anteriores al 1.
	 *
	 * @param soeAPI EN: phenomenon as returned by the API. / ES: fenómeno tal y como lo devuelve la API.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private AllSoEsEntity crearAllSoe(FenomenoDTO soeAPI) {

		AllSoEsEntity allSoEsParaDB = new AllSoEsEntity();

		switch (soeAPI.getPhenomena()) {

			case SI:
				allSoEsParaDB.setSolsticioInvierno(true);
				break;

			case EP:
				allSoEsParaDB.setEquinoccioPrimavera(true);
				break;

			case SV:
				allSoEsParaDB.setSolsticioVerano(true);
				break;

			case EO:
				allSoEsParaDB.setEquinoccioOtonyo(true);
				break;
		}

		FechasApi.Descompuesta descompuesta = FechasApi.descomponer(soeAPI.getDate());

		allSoEsParaDB.setYear(descompuesta.getYear());
		allSoEsParaDB.setMonth(descompuesta.getMonth());
		allSoEsParaDB.setDay(descompuesta.getDay());
		allSoEsParaDB.setHour(descompuesta.getHour());
		allSoEsParaDB.setMinute(descompuesta.getMinute());
		allSoEsParaDB.setSecond(descompuesta.getSecond());

		return allSoEsParaDB;
	}


	/**
	 * EN: Single call to the OPALE API for the solstices and equinoxes of one year.
	 * ES: Llamada única a la API de OPALE para los solsticios y equinoccios de un año.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template. / ES: plantilla de URL.
	 * @return EN: the phenomena of that year, empty if the call fails. / ES: los fenómenos de ese año, vacío si la llamada falla.
	 */
	public List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url) {

		// https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}
		String urlParaLlamada = url.replace("{{YYYY}}", anyo).replace("{{NNNN}}", "1");

		try {
			return this.getGASYEFDTO(urlParaLlamada);
		}
		catch (Exception e) {

			LOG.error("Error al llamar a GASYEF API", e);
			return new ArrayList<>();
		}
	}


	// PRIVATE METHODS

	/**
	 * EN: Unwraps the API response and returns its data, or an empty list if any level of the
	 * structure is missing.
	 * ES: Desenvuelve la respuesta de la API y devuelve sus datos, o una lista vacía si falta
	 * algún nivel de la estructura.
	 *
	 * @param url EN: full URL to call. / ES: URL completa a la que llamar.
	 * @return EN: the phenomena found. / ES: los fenómenos encontrados.
	 */
	private List<FenomenoDTO> getGASYEFDTO(String url){

		GASYEFDTO responseOPALEAPI = restTemplate.getForObject(url, GASYEFDTO.class);

		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			return responseOPALEAPI.getResponse().getData();
		}

		return new ArrayList<>();
	}
}
