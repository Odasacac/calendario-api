package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.DTOs.ComportamientoLunaDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.AllFasesLunaresEntity;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.AllFasesLunaresRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.TablasReferenciaService;
import CCASolutions.Calendario.Utils.FechasApi;
import CCASolutions.Calendario.Utils.Vecindad;

/**
 * EN: Everything the moon drives in the VAU calendar: the week and the day, which count
 * from the last new moon, the aponovos and the state of the moon.
 * ES: Todo lo que la luna determina en el calendario VAU: la semana y el día, que se cuentan
 * desde la última luna nueva, los aponovos y el estado de la luna.
 */
@Service
public class LunasServiceImpl implements LunasService {

	private static final Logger LOG = LoggerFactory.getLogger(LunasServiceImpl.class);

	@Autowired
	private DatosRepository datosRepository;

	@Autowired
	private LunasRepository lunasRepository;

	@Autowired
	private AllFasesLunaresRepository allFasesLunaresRepository;

	@Autowired
	private TablasReferenciaService tablasReferenciaService;

	private final RestTemplate restTemplate = new RestTemplate();

	private final static String API_LUNAR_FASES = "YLP";

	private final static String NEW_MOON = "NewMoon";
	private final static String FIRST_QUARTER = "FirstQuarter";
	private final static String FULLMOON = "FullMoon";
	private final static String LAST_QUARTER = "LastQuarter";

	private final static int PRIMER_ANYO_API = -4700;
	private final static int ULTIMO_ANYO_API = 2100;


	// METODOS PUBLICOS


	/**
	 * EN: Works out the VAU week and day. Both come from the number of days elapsed since the
	 * last new moon: 0 to 7 is the first week, 8 to 14 the second, and so on. A date landing
	 * on a new moon gets the placeholder week and day zero. A day that also falls on a
	 * solstice or equinox is marked "desdoblado".
	 * ES: Calcula la semana y el día VAU. Ambos salen del número de días transcurridos desde la
	 * última luna nueva: del 0 al 7 es la primera semana, del 8 al 14 la segunda, y así
	 * sucesivamente. Una fecha que cae en luna nueva recibe la semana de relleno y el día cero.
	 * Un día que además cae en solsticio o equinoccio se marca como "desdoblado".
	 *
	 * @param date                                          EN: date being consulted. / ES: fecha que se consulta.
	 * @param lunasDesdeElAnyoAnteriorHastaElSiguiente      EN: moon phases around the date. / ES: fases lunares alrededor de la fecha.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas EN: solstices and equinoxes around the date. / ES: solsticios y equinoccios alrededor de la fecha.
	 * @return EN: names of the week and the day; empty if there is no earlier new moon. / ES: nombres de la semana y el día; vacíos si no hay luna nueva anterior.
	 */
	public VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas) {

		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();

		boolean caeEnLunaNueva = false;
		long diasDesdeLaLunaNueva = Long.MAX_VALUE;

		if (lunasDesdeElAnyoAnteriorHastaElSiguiente != null) {

			long diaReferencia = date.toEpochDay();

			for (LunasEntity luna : lunasDesdeElAnyoAnteriorHastaElSiguiente) {

				if (!luna.isNueva() || luna.getDate() == null) {
					continue;
				}

				long dia = luna.getDate().toLocalDate().toEpochDay();

				if (dia == diaReferencia) {

					caeEnLunaNueva = true;
					diasDesdeLaLunaNueva = 0;
					break;
				}

				if (dia < diaReferencia && diaReferencia - dia < diasDesdeLaLunaNueva) {

					diasDesdeLaLunaNueva = diaReferencia - dia;
				}
			}
		}

		// Sin luna nueva anterior no hay semana ni dia VAU que calcular. Antes de este
		// control se consultaba el dia numero Long.MAX_VALUE-21 y se lanzaba un NPE.
		if (diasDesdeLaLunaNueva != Long.MAX_VALUE) {

			int semanaDelMes = this.getSemanaDelMes(diasDesdeLaLunaNueva, caeEnLunaNueva);
			long diaDeLaSemana = this.getDiaDeLaSemana(diasDesdeLaLunaNueva);

			WeeksEntity semana = this.tablasReferenciaService.getSemanaPorNumero(semanaDelMes);
			DaysEntity dia = this.tablasReferenciaService.getDiaPorNumero(diaDeLaSemana);

			if (semana != null) {
				vauWeekAndDay.setWeek(semana.getName());
			}

			if (dia != null) {

				String nombreDelDia = dia.getName();

				if (!caeEnLunaNueva && this.caeEnSoe(date, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas)) {

					nombreDelDia = nombreDelDia + " desdoblado";
				}

				vauWeekAndDay.setDay(nombreDelDia);
			}
		}
		else {
			LOG.warn("No se ha encontrado ninguna luna nueva anterior o igual a {}", date);
		}

		return vauWeekAndDay;
	}

	/**
	 * EN: Which week of the VAU month a number of days since the new moon falls in. The day of
	 * the new moon itself is week zero, the placeholder.
	 * ES: En qué semana del mes VAU cae un número de días desde la luna nueva. El propio día de
	 * la luna nueva es la semana cero, la de relleno.
	 *
	 * @param diasDesdeLaLunaNueva EN: days elapsed since the new moon. / ES: días transcurridos desde la luna nueva.
	 * @param caeEnLunaNueva       EN: whether the date is the new moon itself. / ES: si la fecha es la propia luna nueva.
	 * @return EN: week number, 0 to 5. / ES: número de semana, del 0 al 5.
	 */
	private int getSemanaDelMes(long diasDesdeLaLunaNueva, boolean caeEnLunaNueva) {

		if (diasDesdeLaLunaNueva <= 7) {
			return caeEnLunaNueva ? 0 : 1;
		}

		if (diasDesdeLaLunaNueva <= 14) {
			return 2;
		}

		if (diasDesdeLaLunaNueva <= 21) {
			return 3;
		}

		if (diasDesdeLaLunaNueva <= 28) {
			return 4;
		}

		return 5;
	}

	/**
	 * EN: Position within the week for a number of days since the new moon. From the fourth
	 * week onwards the offset stays at 21, because the last week of the month is shorter.
	 * ES: Posición dentro de la semana para un número de días desde la luna nueva. A partir de
	 * la cuarta semana el desfase se mantiene en 21, porque la última semana del mes es más
	 * corta.
	 *
	 * @param diasDesdeLaLunaNueva EN: days elapsed since the new moon. / ES: días transcurridos desde la luna nueva.
	 * @return EN: day number within the week. / ES: número de día dentro de la semana.
	 */
	private long getDiaDeLaSemana(long diasDesdeLaLunaNueva) {

		if (diasDesdeLaLunaNueva <= 7) {
			return diasDesdeLaLunaNueva;
		}

		if (diasDesdeLaLunaNueva <= 14) {
			return diasDesdeLaLunaNueva - 7;
		}

		if (diasDesdeLaLunaNueva <= 21) {
			return diasDesdeLaLunaNueva - 14;
		}

		// A partir de la cuarta semana el desfase se mantiene en 21 dias
		return diasDesdeLaLunaNueva - 21;
	}

	/**
	 * EN: Whether the date falls on a solstice or an equinox, which is what splits a VAU day.
	 * ES: Si la fecha cae en un solsticio o un equinoccio, que es lo que desdobla un día VAU.
	 *
	 * @param date EN: date being consulted. / ES: fecha que se consulta.
	 * @param soes EN: solstices and equinoxes to check; may be {@code null}. / ES: solsticios y equinoccios a comprobar; admite {@code null}.
	 * @return EN: {@code true} if one of them falls on that day. / ES: {@code true} si alguno cae ese día.
	 */
	private boolean caeEnSoe(LocalDate date, List<SolsticiosYEquinocciosEntity> soes) {

		if (soes == null) {
			return false;
		}

		long diaReferencia = date.toEpochDay();

		for (SolsticiosYEquinocciosEntity soe : soes) {

			if (soe.getDate() != null && soe.getDate().toLocalDate().toEpochDay() == diaReferencia) {
				return true;
			}
		}

		return false;
	}


	/**
	 * EN: Counts the aponovos. An aponovo is a new moon at apogee, marked as "selecta". The
	 * first count is how many have gone by since the reference meton, skipping the one that
	 * belongs to that meton; the second is how many ordinary new moons have passed since the
	 * last aponovo, which gives the month within the aponovo.
	 * ES: Cuenta los aponovos. Un aponovo es una luna nueva en apogeo, marcada como "selecta".
	 * La primera cuenta es cuántos han pasado desde el métono de referencia, saltándose el que
	 * pertenece a ese métono; la segunda es cuántas lunas nuevas corrientes han pasado desde el
	 * último aponovo, lo que da el mes dentro del aponovo.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: aponovo number and month within the aponovo. / ES: número de aponovo y mes dentro del aponovo.
	 */
	public AponovosDTO getAponovos(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		AponovosDTO aponovosDTO = new AponovosDTO();

		if (datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto() == null
				|| datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate() == null) {

			LOG.warn("No hay metono invernal apofasal remoto para calcular los aponovos de {}", date);
			return aponovosDTO;
		}

		// Se sacaban del DTO dentro del bucle, una vez por luna
		LocalDate fechaLimiteInferior = datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate().toLocalDate();
		Long lunaDelMetonoIAR = datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getLunaId();

		int aponovosPasados = 0;
		LunasEntity lunaNSmasCercanaADate = null;
		long diasMinimosEntreDateYLNS = Long.MAX_VALUE;

		List<LunasEntity> lunas = datosCosmicosParaVAUDTO.getLunas();

		if (lunas != null) {

			for (LunasEntity luna : lunas) {

				if (!luna.isNueva() || !luna.isSelecta() || luna.getDate() == null) {
					continue;
				}

				LocalDate fechaLuna = luna.getDate().toLocalDate();

				if (fechaLuna.isBefore(date)
						&& fechaLuna.isAfter(fechaLimiteInferior)
						&& !luna.getId().equals(lunaDelMetonoIAR)) {

					aponovosPasados++;

					long diasEntreDateYLNS = ChronoUnit.DAYS.between(fechaLuna, date);

					if (diasEntreDateYLNS < diasMinimosEntreDateYLNS) {
						diasMinimosEntreDateYLNS = diasEntreDateYLNS;
						lunaNSmasCercanaADate = luna;
					}
				}
			}
		}

		aponovosDTO.setAponovosPasadosDesdeLastMetonoIAR(aponovosPasados);
		aponovosDTO.setNumeroDeAponovo(aponovosPasados + 1);

		int lunasNuevasDesdeLastLNSHastaDate = 0;

		if (lunaNSmasCercanaADate != null) {

			LocalDate fechaUltimoAponovo = lunaNSmasCercanaADate.getDate().toLocalDate();

			// Cuenta las lunas nuevas de los días estrictamente comprendidos entre el
			// último aponovo y la fecha consultada. Se resuelve con un COUNT sobre el
			// índice (nueva, date) en vez de recorrer la lista, que ya no llega tan atrás.
			lunasNuevasDesdeLastLNSHastaDate = (int) this.lunasRepository.countByDateBetweenAndNuevaTrue(
					fechaUltimoAponovo.plusDays(1).atStartOfDay(),
					date.atStartOfDay().minusNanos(1));
		}

		aponovosDTO.setLunasNuevasPasadasDesdeLastAponovo(lunasNuevasDesdeLastLNSHastaDate);
		aponovosDTO.setMesAponoval(lunasNuevasDesdeLastLNSHastaDate + 1);

		return aponovosDTO;
	}

	/**
	 * EN: State of the moon on the date, that is, how it is behaving with respect to its
	 * distance from the Earth.
	 * ES: Estado de la luna en la fecha, es decir, cómo se está comportando respecto a su
	 * distancia a la Tierra.
	 *
	 * @param date        EN: date being consulted. / ES: fecha que se consulta.
	 * @param allApoperis EN: apogees and perigees around that date. / ES: apogeos y perigeos alrededor de esa fecha.
	 * @return EN: the state of the moon. / ES: el estado de la luna.
	 */
	public EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis) {

		EstadoLunaDTO estadoLuna = new EstadoLunaDTO();

		estadoLuna.setComportamientoLunaDTO(this.getComportamientoLuna(date, allApoperis));

		return estadoLuna;
	}

	/**
	 * EN: Describes the movement of the moon. If an apogee or perigee falls on the date
	 * itself, the moon has just reached its farthest or closest point. Otherwise it reports
	 * how many days it has been drawing closer (after an apogee) or moving away (after a
	 * perigee).
	 * ES: Describe el movimiento de la luna. Si un apogeo o perigeo cae en la propia fecha, la
	 * luna acaba de alcanzar su punto más lejano o más cercano. Si no, informa de cuántos días
	 * lleva acercándose (tras un apogeo) o alejándose (tras un perigeo).
	 *
	 * @param date        EN: date being consulted. / ES: fecha que se consulta.
	 * @param allApoperis EN: apogees and perigees around that date. / ES: apogeos y perigeos alrededor de esa fecha.
	 * @return EN: the described behaviour; empty if there is no earlier apogee or perigee. / ES: el comportamiento descrito; vacío si no hay apogeo ni perigeo anterior.
	 */
	private ComportamientoLunaDTO getComportamientoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis) {

		ComportamientoLunaDTO comportamientoLuna = new ComportamientoLunaDTO();

		Vecindad<ApogeosYPerigeosLunaEntity> vecindad = Vecindad.de(allApoperis, ApogeosYPerigeosLunaEntity::getDate, date);

		ApogeosYPerigeosLunaEntity apoperiDelDia = vecindad.getActual();

		if (apoperiDelDia != null) {

			if (apoperiDelDia.isEsApogeo()) {
				comportamientoLuna.setDireccion("Ha alcanzado su punto más lejano");
			}
			else if (apoperiDelDia.isEsPerigeo()) {
				comportamientoLuna.setDireccion("Ha alcanzado su punto más cercano");
			}

			comportamientoLuna.setDate(apoperiDelDia.getDate());

			return comportamientoLuna;
		}

		ApogeosYPerigeosLunaEntity apoperiAnterior = vecindad.getAnterior();

		// Sin apoperi previo no hay direccion que informar. El codigo anterior partia de
		// una entidad vacia y reventaba con NPE al leer su fecha.
		if (apoperiAnterior == null) {

			LOG.warn("No se ha encontrado ningún apoperi anterior o igual a {}", date);
			return comportamientoLuna;
		}

		String accion = "";

		if (apoperiAnterior.isEsApogeo()) {
			accion = "acercándose";
		}
		else if (apoperiAnterior.isEsPerigeo()) {
			accion = "alejándose";
		}

		long dias = vecindad.getDiasHastaAnterior();

		comportamientoLuna.setDireccion("Lleva " + dias + " " + (dias == 1 ? "día" : "días") + " " + accion);

		return comportamientoLuna;
	}


	/**
	 * EN: Downloads every moon phase from year -4700 to 2100, one call per year, and stores
	 * each year in a single batch. A failing year is logged and skipped.
	 * ES: Descarga todas las fases lunares del año -4700 al 2100, una llamada por año, y
	 * almacena cada año en un único lote. Un año que falle se registra y se salta.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateLunasFromOpale() {

		String resultado = "Lunas actualizadas sin problema.";

		DatosEntity apiGetLunasUrl = datosRepository.findByConcepto(API_LUNAR_FASES);

		if (apiGetLunasUrl == null || apiGetLunasUrl.getValor() == null) {

			LOG.error("La URL de la API para obtener las lunas es nula.");
			return "Error al actualizar lunas: la URL de la API para obtener las lunas es nula.";
		}

		if (this.lunasRepository.count() > 0) {

			LOG.warn("Ya hay lunas en la base de datos.");
			return "Error al actualizar lunas: ya hay lunas en la base de datos.";
		}

		for (int anyo = PRIMER_ANYO_API; anyo < ULTIMO_ANYO_API; anyo++) {

			LOG.info("Actualizando lunas del anyo: {}", anyo);

			try {
				List<LunarPhaseDTO> fasesLunaresDelAnyo = this.getFasesLunaresDelAnyoViaAPI(String.valueOf(anyo), apiGetLunasUrl.getValor());

				if (fasesLunaresDelAnyo.isEmpty()) {

					LOG.warn("No se han obtenido lunas de la API para el anyo {}.", anyo);
					resultado = "Error al actualizar lunas: no se han obtenido lunas de la API.";
					continue;
				}

				List<LunasEntity> lunasDelAnyo = new ArrayList<>();
				List<AllFasesLunaresEntity> todasLasFasesDelAnyo = new ArrayList<>();

				for (LunarPhaseDTO faseLunarAPI : fasesLunaresDelAnyo) {

					if (anyo > 0) {
						lunasDelAnyo.add(this.crearLuna(faseLunarAPI));
					}

					todasLasFasesDelAnyo.add(this.crearFaseLunar(faseLunarAPI));
				}

				// Una unica sentencia por lote en vez de un INSERT por fase lunar
				if (!lunasDelAnyo.isEmpty()) {
					this.lunasRepository.saveAll(lunasDelAnyo);
				}

				this.allFasesLunaresRepository.saveAll(todasLasFasesDelAnyo);

				LOG.info("Actualizadas las lunas del anyo: {}", anyo);
			}
			catch (Exception e) {
				LOG.error("Error al actualizar lunas del anyo {}", anyo, e);
				resultado = "Error al actualizar lunas, checkear logs.";
			}
		}

		return resultado;
	}

	/**
	 * EN: Builds one working-table row from an API moon phase. The selecta and invertida flags
	 * start off false; they are set later, when the phases are paired with the apogees and
	 * perigees.
	 * ES: Construye una fila de la tabla de trabajo a partir de una fase lunar de la API. Las
	 * banderas selecta e invertida parten en falso; se ponen más tarde, al emparejar las fases
	 * con los apogeos y perigeos.
	 *
	 * @param faseLunarAPI EN: phase as returned by the API. / ES: fase tal y como la devuelve la API.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private LunasEntity crearLuna(LunarPhaseDTO faseLunarAPI) {

		LunasEntity lunaParaDB = new LunasEntity();

		switch (faseLunarAPI.getMoonPhase()) {

			case NEW_MOON:
				lunaParaDB.setNueva(true);
				break;

			case FIRST_QUARTER:
				lunaParaDB.setCuartoCreciente(true);
				break;

			case FULLMOON:
				lunaParaDB.setLlena(true);
				break;

			case LAST_QUARTER:
				lunaParaDB.setCuartoMenguante(true);
				break;
		}

		LocalDateTime fecha = LocalDateTime.parse(faseLunarAPI.getDate());

		lunaParaDB.setYear(fecha.getYear());
		lunaParaDB.setDate(fecha);
		lunaParaDB.setSelecta(false);
		lunaParaDB.setInvertida(false);

		return lunaParaDB;
	}

	/**
	 * EN: Builds one historical-table row, with the date split into numeric fields so that
	 * years before 1 fit.
	 * ES: Construye una fila de la tabla histórica, con la fecha troceada en campos numéricos
	 * para que quepan los años anteriores al 1.
	 *
	 * @param faseLunarAPI EN: phase as returned by the API. / ES: fase tal y como la devuelve la API.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private AllFasesLunaresEntity crearFaseLunar(LunarPhaseDTO faseLunarAPI) {

		AllFasesLunaresEntity allFaseLunarParaDB = new AllFasesLunaresEntity();

		switch (faseLunarAPI.getMoonPhase()) {

			case NEW_MOON:
				allFaseLunarParaDB.setNueva(true);
				break;

			case FIRST_QUARTER:
				allFaseLunarParaDB.setCuartoCreciente(true);
				break;

			case FULLMOON:
				allFaseLunarParaDB.setLlena(true);
				break;

			case LAST_QUARTER:
				allFaseLunarParaDB.setCuartoMenguante(true);
				break;
		}

		FechasApi.Descompuesta descompuesta = FechasApi.descomponer(faseLunarAPI.getDate());

		allFaseLunarParaDB.setYear(descompuesta.getYear());
		allFaseLunarParaDB.setMonth(descompuesta.getMonth());
		allFaseLunarParaDB.setDay(descompuesta.getDay());
		allFaseLunarParaDB.setHour(descompuesta.getHour());
		allFaseLunarParaDB.setMinute(descompuesta.getMinute());
		allFaseLunarParaDB.setSecond(descompuesta.getSecond());

		return allFaseLunarParaDB;
	}


	/**
	 * EN: Single call to the OPALE API for the moon phases of one year.
	 * ES: Llamada única a la API de OPALE para las fases lunares de un año.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template. / ES: plantilla de URL.
	 * @return EN: the phases of that year, empty if the call fails. / ES: las fases de ese año, vacío si la llamada falla.
	 */
	public List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url){

		// https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}
		String urlParaLlamada = url.replace("{{YYYY}}", anyo);

		try {
			return this.getYLPDTO(urlParaLlamada);
		}
		catch (Exception e) {

			LOG.error("Error al llamar a YLP API", e);
			return new ArrayList<>();
		}
	}

	/**
	 * EN: Unwraps the API response and returns its data, or an empty list if any level of the
	 * structure is missing.
	 * ES: Desenvuelve la respuesta de la API y devuelve sus datos, o una lista vacía si falta
	 * algún nivel de la estructura.
	 *
	 * @param url EN: full URL to call. / ES: URL completa a la que llamar.
	 * @return EN: the phases found. / ES: las fases encontradas.
	 */
	private List<LunarPhaseDTO> getYLPDTO(String url){

		YLPDTO responseOPALEAPI = restTemplate.getForObject(url, YLPDTO.class);

		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			return responseOPALEAPI.getResponse().getData();
		}

		return new ArrayList<>();
	}
}
