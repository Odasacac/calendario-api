package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.UtilsService;

/**
 * EN: Loads, in one go, every astronomical phenomenon the conversion of a date needs, so
 * that no other service has to touch the database.
 * ES: Carga de una sola vez todos los fenómenos astronómicos que necesita la conversión de
 * una fecha, de modo que ningún otro servicio tenga que tocar la base de datos.
 */
@Service
@Transactional(readOnly = true)
public class UtilsServiceImpl implements UtilsService {

	private static final Logger LOG = LoggerFactory.getLogger(UtilsServiceImpl.class);

	/**
	 * EN: Years on each side of the consulted date for which every moon phase is loaded.
	 * ES: Años a cada lado de la fecha consultada de los que se cargan todas las fases lunares.
	 */
	private static final int ANYOS_DE_VENTANA_LUNAR = 2;

	@Autowired
	private MetonsRepository metonsRepository;

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	@Autowired
	private LunasRepository lunasRepository;

	@Autowired
	private EclipenosRepository eclipenosRepository;

	@Autowired
	private EclipsesRepository eclipsesRepository;

	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;

	@Autowired
	private EclipenosService eclipenosService;

	@Autowired
	private MetonsService metonsService;

	@Autowired
	private MidsisonRepository midsisonRepository;


	/**
	 * EN: Gathers everything needed to convert a date, checking at each step that the data
	 * is there. The reference phenomena come first, because the ranges of the later queries
	 * depend on them: without a winter apofasal remote eclipeno there is no origin for the
	 * counters, and without a winter new meton there is no year.
	 * ES: Recoge todo lo necesario para convertir una fecha, comprobando en cada paso que los
	 * datos están. Los fenómenos de referencia van primero, porque de ellos dependen los
	 * rangos de las consultas siguientes: sin eclípeno invernal apofasal remoto no hay origen
	 * para los contadores, y sin métono invernal nuevo no hay año.
	 *
	 * @param date EN: date the data is gathered around. / ES: fecha alrededor de la cual se recogen los datos.
	 * @return EN: the data set; {@code isValido()} says whether it can be used. / ES: el conjunto de datos; {@code isValido()} dice si se puede usar.
	 */
	public DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date) {

		DatosCosmicosParaVAUDTO datos = new DatosCosmicosParaVAUDTO();
		LocalDateTime dateO = date.atTime(LocalTime.MAX);

		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAllByOrderByDateDesc();

		if (allEclipenos.isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no hay eclipenos");
		}

		datos.setEclipenos(allEclipenos);
		datos.setLastEclipenoIN(this.eclipenosService.getLastEclipenoIN(allEclipenos, date));
		datos.setLastEclipenoInvernalApofasalRemoto(this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(allEclipenos, date));

		if (datos.getLastEclipenoIN() == null) {
			return this.invalido(datos, "Error al obtener dateVAU: no se ha encontrado un eclípeno inicial nuevo anterior a la fecha proporcionada.");
		}

		if (datos.getLastEclipenoInvernalApofasalRemoto() == null) {
			return this.invalido(datos, "Error al obtener dateVAU: no se ha encontrado un eclípeno invernal apofasal remoto anterior a la fecha proporcionada.");
		}

		List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(
				datos.getLastEclipenoInvernalApofasalRemoto().getDate().minusYears(1), dateO.plusYears(1));

		if (allMetons.isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado métonos.");
		}

		datos.setMetons(allMetons);
		datos.setLastMetonIN(this.metonsService.getLastMetonINForDate(allMetons, date));
		datos.setLastMetonIApofasalRemoto(this.metonsService.getLastMetonIApofasalRemoto(allMetons, date));

		if (datos.getLastMetonIN() == null) {
			return this.invalido(datos, "Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
		}

		// El métono invernal apofasal remoto se usaba sin comprobar: como el buscador
		// devolvía una entidad vacía en vez de null, aquí saltaba un NPE al leer su fecha
		if (datos.getLastMetonIApofasalRemoto() == null) {
			return this.invalido(datos, "Error al obtener dateVAU: no se ha encontrado un métono invernal apofasal remoto anterior a la fecha proporcionada.");
		}

		datos.setLunas(this.getLunasNecesarias(datos.getLastMetonIApofasalRemoto().getDate().minusYears(1), dateO));
		datos.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(datos.getLastMetonIN().getDate().minusYears(1), dateO.plusYears(1)));
		datos.setEclipses(this.eclipsesRepository.findEclipsesAbsoluteQuery(datos.getLastEclipenoIN().getDate().toLocalDate().atStartOfDay(), dateO.plusYears(1)));
		datos.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetween(dateO.minusMonths(3), dateO.plusMonths(3)));
		datos.setMidsisons(this.midsisonRepository.findByDateBetween(dateO.minusMonths(3), dateO.plusMonths(3)));

		if (datos.getApoperis().isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado apoperis.");
		}

		if (datos.getSoes().isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado soes.");
		}

		if (datos.getLunas().isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado fases lunares.");
		}

		if (datos.getEclipses().isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado eclipses.");
		}

		if (datos.getMidsisons().isEmpty()) {
			return this.invalido(datos, "Error al obtener dateVAU: no se han encontrado midsisons.");
		}

		datos.setValido(true);

		return datos;
	}

	/**
	 * EN: Moon phases the conversion of a date actually needs.
	 * <p>
	 * EN: The whole interval from the winter apofasal remote meton (year 1433 for any present
	 * day date) up to a year past the consulted one used to be fetched: more than 70.000 rows
	 * hydrated as JPA entities on every request, and that was by far the biggest cost of the
	 * endpoint. Out of that interval only two things are needed: the phases of the two years
	 * surrounding the date, for the week, the day, the month, the notable event and the
	 * midsison; and the new moons at apogee (the aponovos) of the whole interval, to count the
	 * current aponovo and locate the closest change of aponovo. The union of both sets holds
	 * exactly the same rows the calculations used, so the result does not change.
	 * <p>
	 * ES: Fases lunares que necesita el cálculo de una fecha.
	 *
	 * Antes se traía el intervalo completo desde el métono invernal apofasal remoto
	 * (año 1433 para cualquier fecha actual) hasta un año después de la consultada:
	 * más de 70.000 filas hidratadas como entidades JPA en cada peticion, y ese era
	 * con diferencia el mayor coste del endpoint.
	 *
	 * De todo ese intervalo solo hacen falta dos cosas:
	 * <ul>
	 * <li>las fases de los dos años que rodean a la fecha, para semana, día, mes,
	 * evento notable y midsison;</li>
	 * <li>las lunas nuevas selectas (los aponovos) de todo el intervalo, para contar
	 * el aponovo actual y localizar el cambio de aponovo más próximo.</li>
	 * </ul>
	 * La unión de ambos conjuntos contiene exactamente las mismas filas que usaban
	 * los cálculos, así que el resultado no cambia.
	 *
	 * @param desde EN: start of the interval, one year before the reference meton. / ES: inicio del intervalo, un año antes del métono de referencia.
	 * @param dateO EN: end of the consulted day. / ES: final del día consultado.
	 * @return EN: the phases needed, in chronological order. / ES: las fases necesarias, en orden cronológico.
	 */
	private List<LunasEntity> getLunasNecesarias(LocalDateTime desde, LocalDateTime dateO) {

		LocalDateTime inicioVentana = dateO.minusYears(ANYOS_DE_VENTANA_LUNAR);

		// El limite superior se mantiene en un año, igual que la consulta original
		List<LunasEntity> lunas = new ArrayList<>(this.lunasRepository.findByDateBetween(inicioVentana, dateO.plusYears(1)));

		if (desde.isBefore(inicioVentana)) {

			// Los dos rangos se solapan en el extremo, asi que se descartan repetidos
			Set<Long> yaIncluidas = new HashSet<>();

			for (LunasEntity luna : lunas) {
				yaIncluidas.add(luna.getId());
			}

			for (LunasEntity aponovo : this.lunasRepository.findByDateBetweenAndNuevaTrueAndSelectaTrue(desde, inicioVentana)) {

				if (yaIncluidas.add(aponovo.getId())) {
					lunas.add(aponovo);
				}
			}
		}

		lunas.sort(Comparator.comparing(LunasEntity::getDate));

		return lunas;
	}

	/**
	 * EN: Marks the data set as unusable, records why, and returns it so the caller can bail
	 * out in a single line.
	 * ES: Marca el conjunto de datos como inservible, deja constancia del motivo y lo devuelve
	 * para que quien lo llama pueda abandonar en una sola línea.
	 *
	 * @param datos   EN: data set being built. / ES: conjunto de datos en construcción.
	 * @param mensaje EN: reason the conversion cannot go on. / ES: motivo por el que la conversión no puede seguir.
	 * @return EN: the same data set, flagged as invalid. / ES: el mismo conjunto de datos, marcado como inválido.
	 */
	private DatosCosmicosParaVAUDTO invalido(DatosCosmicosParaVAUDTO datos, String mensaje) {

		datos.setMensaje(mensaje);
		LOG.warn(mensaje);

		return datos;
	}
}
