package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AGPDTO;
import CCASolutions.Calendario.DTOs.ApogeosDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Services.ApogeosYPerigeosLunaService;
import CCASolutions.Calendario.Utils.IndiceTemporal;

/**
 * EN: Manages the apogees and perigees of the moon and their pairing with the moon phases.
 * ES: Gestiona los apogeos y perigeos de la luna y su emparejamiento con las fases lunares.
 */
@Service
public class ApogeosYPerigeosLunaServiceImpl implements ApogeosYPerigeosLunaService{

	private static final Logger LOG = LoggerFactory.getLogger(ApogeosYPerigeosLunaServiceImpl.class);

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	@Autowired
	private DatosRepository datosRepository;

	@Autowired
	private LunasRepository lunasRepository;

	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	private final static String API_APOPERIS = "APG";

	private static final String APOGEO = "MaximalDistance";
	private static final String PERIGEO = "MinimalDistance";

	private static final DateTimeFormatter FORMATTER_API_REQUEST =
	        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final DateTimeFormatter FORMATTER_API_RESPONSE =
	        new DateTimeFormatterBuilder()
	                .appendPattern("yyyy-MM-dd'T'HH:mm:")
	                .appendValue(ChronoField.SECOND_OF_MINUTE)
	                .optionalStart()
	                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
	                .optionalEnd()
	                .toFormatter();

	/**
	 * EN: Downloads every apogee and perigee between years 1000 and 2100. The API is paged by
	 * date rather than by year, so the loop advances to just after the last phenomenon it
	 * received on each round.
	 * ES: Descarga todos los apogeos y perigeos entre los años 1000 y 2100. La API se pagina
	 * por fecha y no por año, así que el bucle avanza hasta justo después del último fenómeno
	 * recibido en cada vuelta.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateApogeosFromOpale() {

		DatosEntity apiGetApogeosUrl = datosRepository.findByConcepto(API_APOPERIS);

		// Se llamaba a getValor() sin comprobar nada: si faltaba la fila el arranque
		// del poblado moria con NullPointerException
		if (apiGetApogeosUrl == null || apiGetApogeosUrl.getValor() == null) {

			LOG.error("La URL de la API para obtener los apoperis es nula.");
			return "Error al actualizar los apogeos: la URL de la API para obtener los apoperis es nula.";
		}

		if (this.apogeosYPerigeosLunaRepository.count() > 0) {

			LOG.warn("Ya hay apoperis en la base de datos.");
			return "Error al actualizar los apogeos: ya hay apoperis en la base de datos.";
		}

		List<ApogeosDTO> allApogeosAPI = this.getApogeosViaAPI(apiGetApogeosUrl.getValor());

		if (allApogeosAPI.isEmpty()) {

			LOG.error("No se han obtenido apogeos por la API.");
			return "Error al actualizar los apogeos: no se han obtenido apogeos por la API.";
		}

		List<ApogeosYPerigeosLunaEntity> apogeosParaDB = new ArrayList<>(allApogeosAPI.size());

		for (ApogeosDTO apogeo : allApogeosAPI) {

			ApogeosYPerigeosLunaEntity apogeoParaDB = new ApogeosYPerigeosLunaEntity();
			apogeoParaDB.setDate(LocalDateTime.parse(apogeo.getDate(), FORMATTER_API_RESPONSE).truncatedTo(ChronoUnit.SECONDS));
			apogeoParaDB.setYear(apogeoParaDB.getDate().getYear());

			switch (apogeo.getPhenomena()) {

				case APOGEO:
					apogeoParaDB.setEsApogeo(true);
					break;

				case PERIGEO:
					apogeoParaDB.setEsPerigeo(true);
					break;
			}

			apogeoParaDB.setDistance(apogeo.getDistance());

			apogeosParaDB.add(apogeoParaDB);
		}

		LOG.info("Almacenando {} apoperis en la BD.", apogeosParaDB.size());
		this.apogeosYPerigeosLunaRepository.saveAll(apogeosParaDB);
		LOG.info("Apoperis almacenados en la BD.");

		return "Apogeos actualizados sin problema.";
	}


	/**
	 * EN: Pairs every apogee or perigee with the moon phases falling within one sidereal day
	 * and marks both sides. All the changes are written in two batches at the end instead of
	 * two UPDATEs per match.
	 * ES: Empareja cada apogeo o perigeo con las fases lunares que caen dentro de un día
	 * sideral y marca a ambos lados. Todos los cambios se escriben en dos lotes al final, en
	 * lugar de dos UPDATE por coincidencia.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String updateLunasYApoperisConSelectoOInvertido() {

		List<ApogeosYPerigeosLunaEntity> apogeosYPerigeosExistentesEnBD = this.apogeosYPerigeosLunaRepository.findAll();

		if (apogeosYPerigeosExistentesEnBD.isEmpty()) {

			LOG.error("No hay apoperis en la base de datos.");
			return "Error al actualizar las lunas con sus apogeos: no hay apoperis en la base de datos.";
		}

		LOG.info("Comparando lunas con apogeos...");

		List<LunasEntity> allLunas = this.lunasRepository.findAll();

		if (allLunas.isEmpty()) {

			LOG.error("No hay lunas en la base de datos.");
			return "Error al actualizar las lunas con sus apogeos: no hay lunas en la base de datos.";
		}

		// El cruce original era de unos 29.000 apoperis x 104.000 lunas (3.000 millones
		// de comparaciones) y ademas hacia dos UPDATE sueltos por cada coincidencia
		IndiceTemporal<LunasEntity> indiceLunas = IndiceTemporal.de(allLunas, LunasEntity::getDate);

		Set<ApogeosYPerigeosLunaEntity> apoperisModificados = new LinkedHashSet<>();
		Set<LunasEntity> lunasModificadas = new LinkedHashSet<>();

		for (ApogeosYPerigeosLunaEntity apoperi : apogeosYPerigeosExistentesEnBD) {

			for (LunasEntity luna : indiceLunas.enVentana(apoperi.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

				boolean emparejados = this.emparejar(apoperi, luna);

				if (emparejados) {

					apoperisModificados.add(apoperi);
					lunasModificadas.add(luna);
				}
			}
		}

		this.apogeosYPerigeosLunaRepository.saveAll(apoperisModificados);
		this.lunasRepository.saveAll(lunasModificadas);

		LOG.info("Lunas y apoperis actualizados: {} parejas", lunasModificadas.size());

		return "Lunas y apoperis actualizados correctamente";
	}

	/**
	 * EN: Marks one apogee or perigee and one moon phase as a pair, if they go together.
	 * A full moon at perigee or a new moon at apogee is "selecta"; a full moon at apogee or a
	 * new moon at perigee is "invertida". Quarter moons pair with nothing.
	 * ES: Marca un apogeo o perigeo y una fase lunar como pareja, si es que casan.
	 * Luna llena con perigeo o luna nueva con apogeo -&gt; selecta.
	 * Luna llena con apogeo o luna nueva con perigeo -&gt; invertida.
	 * Los cuartos no emparejan con nada.
	 *
	 * @param apoperi EN: apogee or perigee. / ES: apogeo o perigeo.
	 * @param luna    EN: moon phase falling within a sidereal day of it. / ES: fase lunar que cae dentro de un día sideral.
	 * @return EN: {@code true} if they were paired. / ES: {@code true} si se han emparejado.
	 */
	private boolean emparejar(ApogeosYPerigeosLunaEntity apoperi, LunasEntity luna) {

		boolean selecta;

		if (luna.isLlena()) {

			if (apoperi.isEsApogeo()) {
				selecta = false;
			}
			else if (apoperi.isEsPerigeo()) {
				selecta = true;
			}
			else {
				return false;
			}
		}
		else if (luna.isNueva()) {

			if (apoperi.isEsPerigeo()) {
				selecta = false;
			}
			else if (apoperi.isEsApogeo()) {
				selecta = true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}

		// Solo se marca la bandera que corresponde; la contraria se deja como estaba
		// para no alterar el comportamiento cuando una luna casa con dos apoperis
		if (selecta) {
			apoperi.setEsSelecto(true);
			luna.setSelecta(true);
		}
		else {
			apoperi.setEsInvertido(true);
			luna.setInvertida(true);
		}

		apoperi.setLunaId(luna.getId());
		luna.setApoperiId(apoperi.getId());

		LOG.debug("Luna {} encontrada en {}", selecta ? "selecta" : "transicional", luna.getDate().toLocalDate());

		return true;
	}

	/**
	 * EN: Calls the apogee and perigee API repeatedly until the whole range is covered. If a
	 * round brings nothing new it steps forward fifty days, so the loop can never stall.
	 * ES: Llama repetidamente a la API de apogeos y perigeos hasta cubrir todo el rango. Si una
	 * vuelta no trae nada nuevo avanza cincuenta días, de modo que el bucle no se pueda quedar
	 * atascado.
	 *
	 * @param url EN: URL template with the date and count placeholders. / ES: plantilla de URL con los marcadores de fecha y número de días.
	 * @return EN: every apogee and perigee obtained. / ES: todos los apogeos y perigeos obtenidos.
	 */
	private List<ApogeosDTO> getApogeosViaAPI(String url){

		List<ApogeosDTO> allApogeos = new ArrayList<>();

		// https://opale.imcce.fr/api/v1/phenomena/distances?date={{YYYY-MM-DD}}&nbd={{DDDD}}&bodies=399,301&calendar=gregorian

		LocalDateTime fechaParaLlamada = LocalDateTime.parse("1000-01-01T00:00:00.000");
		LocalDateTime fechaTope = LocalDateTime.parse("2100-01-01T00:00:00.000");

		String urlConDias = url.replace("{{DDDD}}", "500");

		LOG.info("Haciendo llamada a API para apogeos");

		try {

			while (fechaParaLlamada.isBefore(fechaTope)) {

				String fechaParaLlamadaFormateada = fechaParaLlamada.format(FORMATTER_API_REQUEST);
				String urlParaLlamada = urlConDias.replace("{{YYYY-MM-DD}}", fechaParaLlamadaFormateada);

				LOG.info("Haciendo llamada para {}", fechaParaLlamada.toLocalDate());

				List<ApogeosDTO> apogeosFromAPI = this.getAPGDTO(urlParaLlamada);

				LocalDateTime nuevaFechaParaLlamada = fechaParaLlamada;

				for (ApogeosDTO apogeo : apogeosFromAPI) {

					LocalDateTime apogeoDate = LocalDateTime.parse(apogeo.getDate(), FORMATTER_API_RESPONSE);

					if (apogeoDate.isAfter(nuevaFechaParaLlamada)) {
						nuevaFechaParaLlamada = apogeoDate;
					}
				}

				allApogeos.addAll(apogeosFromAPI);

				if (nuevaFechaParaLlamada.isAfter(fechaParaLlamada)) {
					fechaParaLlamada = nuevaFechaParaLlamada.plusSeconds(1);
				}
				else {
					fechaParaLlamada = fechaParaLlamada.plusDays(50);
				}
			}

			LOG.info("Fin de llamada a la API.");
		}
		catch (Exception e) {

			LOG.error("Error al llamar a APG API", e);
		}

		return allApogeos;
	}

	/**
	 * EN: Maps one page of the API response onto the internal DTO.
	 * ES: Traduce una página de la respuesta de la API al DTO interno.
	 *
	 * @param url EN: full URL to call. / ES: URL completa a la que llamar.
	 * @return EN: the phenomena on that page, empty if there are none. / ES: los fenómenos de esa página, vacío si no hay ninguno.
	 */
	private List<ApogeosDTO> getAPGDTO(String url) {

	    AGPDTO responseOPALEAPI = restTemplate.getForObject(url, AGPDTO.class);

	    if (responseOPALEAPI != null &&
	        responseOPALEAPI.getResponse() != null &&
	        responseOPALEAPI.getResponse().getData() != null) {

	        return responseOPALEAPI.getResponse()
	                .getData()
	                .stream()
	                .map(item -> new ApogeosDTO(
	                        item.getDate(),
	                        item.getPhenomena(),
	                        item.getDistance()
	                ))
	                .toList();
	    }

	    return new ArrayList<>();
	}
}
