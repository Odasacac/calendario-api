package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Services.MonthService;
import CCASolutions.Calendario.Services.TablasReferenciaService;

/**
 * EN: Works out the VAU month of a date and manages the fixed month table.
 * ES: Calcula el mes VAU de una fecha y gestiona la tabla fija de meses.
 */
@Service
public class MonthServiceImpl implements MonthService{

	private static final Logger LOG = LoggerFactory.getLogger(MonthServiceImpl.class);

	@Autowired
	private MonthsRepository monthsRepository;

	@Autowired
	private TablasReferenciaService tablasReferenciaService;

	/**
	 * EN: Works out the VAU month. First finds the solstice or equinox on either side of the
	 * date, then the new moons falling between them. A date landing on a new moon belongs to
	 * no month; a date landing on a solstice belongs to the hybrid month of that season;
	 * otherwise the month is given by how many new moons have gone by since the solstice.
	 * The qualifier comes from the last new moon before the date.
	 * ES: Calcula el mes VAU. Primero localiza el solsticio o equinoccio a cada lado de la
	 * fecha, y después las lunas nuevas que caen entre ambos. Una fecha que cae en luna nueva
	 * no pertenece a ningún mes; una que cae en solsticio pertenece al mes híbrido de esa
	 * estación; en el resto de casos el mes lo da cuántas lunas nuevas han pasado desde el
	 * solsticio. El apellido lo aporta la última luna nueva anterior a la fecha.
	 *
	 * @param date                                          EN: date being consulted. / ES: fecha que se consulta.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas EN: solstices and equinoxes around the date. / ES: solsticios y equinoccios alrededor de la fecha.
	 * @param lunasDesdeElAnyoAnteriorHastaElSiguiente      EN: moon phases around the date. / ES: fases lunares alrededor de la fecha.
	 * @return EN: name and qualifier of the month. / ES: nombre y apellido del mes.
	 */
	public MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente) {

		MonthDTO month = new MonthDTO();

		// Lo primero es coger los solsticios y equinoccios mas cercanos a la fecha a consultar
		SolsticiosYEquinocciosEntity lastSOE = null;
		SolsticiosYEquinocciosEntity nextSOE = null;

		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaConNextSOE = Long.MAX_VALUE;

		// Si cae en SOE, ya tenemos el mes
		boolean caeEnSOE = false;

		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSOE; i++) {

			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			LocalDate fechaSoe = soe.getDate().toLocalDate();

			if(fechaSoe.isEqual(date)) {

				caeEnSOE = true;
				lastSOE = soe;
				nextSOE = soe;
			}
			else if(fechaSoe.isBefore(date)) {

				long diasDeDiferenciaEntreLastSOEYFecha = ChronoUnit.DAYS.between(fechaSoe, date);

				if(diasDeDiferenciaEntreLastSOEYFecha < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYFecha;
					lastSOE = soe;
				}
			}
			else {

				long diasDeDiferenciaEntreNextSOEYFecha = ChronoUnit.DAYS.between(date, fechaSoe);

				if(diasDeDiferenciaEntreNextSOEYFecha < diasMinimosDeDiferenciaConNextSOE) {
					diasMinimosDeDiferenciaConNextSOE = diasDeDiferenciaEntreNextSOEYFecha;
					nextSOE = soe;
				}
			}
		}

		if(lastSOE == null || nextSOE == null) {

			LOG.warn("Error, no se han encontrado nextSOE y/o lastSOE para {}.", date);
			return month;
		}

		// Luego, coger las lunas nuevas que se encuentran entre ambos lastSOE y nextSOE
		// Si cae en Luna nueva, ya tenemos el mes
		LocalDate fechaLastSOE = lastSOE.getDate().toLocalDate();
		LocalDate fechaNextSOE = nextSOE.getDate().toLocalDate();

		LunasEntity lunaNuevaAnteriorMasCercanaALaFecha = null;
		long numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = Long.MAX_VALUE;

		List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE = new ArrayList<>();
		boolean caeEnLunaNueva = false;

		// Antes se recorrian las lunas tres veces: una para separar nuevas de llenas,
		// otra para las nuevas y otra para las llenas (esta ultima sin usarse para nada)
		for(LunasEntity luna : lunasDesdeElAnyoAnteriorHastaElSiguiente) {

			if(!luna.isNueva()) {
				continue;
			}

			LocalDate fechaLuna = luna.getDate().toLocalDate();

			if(fechaLuna.isEqual(date)) {

				lunasNuevasEntreLastSOEYNextSOE.add(luna);
				caeEnLunaNueva = true;
			}
			else if(!fechaLuna.isBefore(fechaLastSOE) && fechaLuna.isBefore(fechaNextSOE)){

				lunasNuevasEntreLastSOEYNextSOE.add(luna);
			}

			if(fechaLuna.isBefore(date)) {

				long diasDeDiferenciaEntreLNAnteriorYDate = ChronoUnit.DAYS.between(fechaLuna, date);

				if(diasDeDiferenciaEntreLNAnteriorYDate < numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate) {

					numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = diasDeDiferenciaEntreLNAnteriorYDate;
					lunaNuevaAnteriorMasCercanaALaFecha = luna;
				}
			}
		}

		month.setNewMoon(caeEnLunaNueva);

		if(caeEnLunaNueva) {

			month.setName("-");
			return month;
		}

		MonthsEntity vauMonth;

		// Si cae en soe, pertenece al mes hibrido de ese soe.
		if(caeEnSOE) {

			vauMonth = this.tablasReferenciaService.getMes(lastSOE.getStartingSeason(), 0, false);
		}
		else {

			vauMonth = this.getMesEntreSolsticios(date, lastSOE, nextSOE, lunasNuevasEntreLastSOEYNextSOE);
		}

		if(vauMonth == null) {
			return month;
		}

		month.setName(vauMonth.getName());

		if(lunaNuevaAnteriorMasCercanaALaFecha != null) {

			if(lunaNuevaAnteriorMasCercanaALaFecha.isSelecta()) {
				month.setSurname("selecto");
			}
			else if(lunaNuevaAnteriorMasCercanaALaFecha.isInvertida()) {
				month.setSurname("invertido");
			}
		}

		return month;
	}

	/**
	 * EN: Resolves the month for a date that falls neither on a solstice nor on a new moon.
	 * The two edges of the season are special: between the last new moon and the next
	 * solstice the month is the hybrid one of the season about to open, and between a
	 * solstice and the first new moon after it the month is the hybrid one of the season
	 * that just opened, except after the winter solstice, where it is the liminal month.
	 * ES: Resuelve el mes de una fecha que no cae ni en solsticio ni en luna nueva. Los dos
	 * bordes de la estación son especiales: entre la última luna nueva y el siguiente
	 * solsticio el mes es el híbrido de la estación que va a abrirse, y entre un solsticio y
	 * la primera luna nueva posterior el mes es el híbrido de la estación recién abierta,
	 * salvo tras el solsticio de invierno, donde es el mes liminal.
	 *
	 * @param date                             EN: date being consulted. / ES: fecha que se consulta.
	 * @param lastSOE                          EN: previous solstice or equinox. / ES: solsticio o equinoccio anterior.
	 * @param nextSOE                          EN: next solstice or equinox. / ES: solsticio o equinoccio siguiente.
	 * @param lunasNuevasEntreLastSOEYNextSOE  EN: new moons between the two. / ES: lunas nuevas entre ambos.
	 * @return EN: the month, or {@code null} if it cannot be resolved. / ES: el mes, o {@code null} si no se puede resolver.
	 */
	private MonthsEntity getMesEntreSolsticios(LocalDate date, SolsticiosYEquinocciosEntity lastSOE, SolsticiosYEquinocciosEntity nextSOE, List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE) {

		// Si no cae en SOE, hay que calcular cuantas lunas nuevas han pasado desde el lastSOE hasta la fecha a consultar
		// Tambien obtenemos la luna nueva anterior al nextSOE y la luna nueva posterior al lastSOE
		LocalDate fechaLastSOE = lastSOE.getDate().toLocalDate();
		LocalDate fechaNextSOE = nextSOE.getDate().toLocalDate();

		int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;

		long diasMinimosDeDiferenciaLunaNuevaConNextSOE = Long.MAX_VALUE;
		LunasEntity lastLNBeforeNextSOE = null;

		long diasMinimosDeDiferenciaLunaNuevaConLastSOE = Long.MAX_VALUE;
		LunasEntity firstLNAfterLastSOE = null;

		for(LunasEntity luna : lunasNuevasEntreLastSOEYNextSOE) {

			LocalDate fechaLuna = luna.getDate().toLocalDate();

			long diasDeDiferenciaEntreNextSOEYLN = ChronoUnit.DAYS.between(fechaLuna, fechaNextSOE);
			long diasDeDiferenciaEntreLastSOEYLN = ChronoUnit.DAYS.between(fechaLastSOE, fechaLuna);

			if(diasDeDiferenciaEntreNextSOEYLN < diasMinimosDeDiferenciaLunaNuevaConNextSOE) {

				lastLNBeforeNextSOE = luna;
				diasMinimosDeDiferenciaLunaNuevaConNextSOE = diasDeDiferenciaEntreNextSOEYLN;
			}

			if(diasDeDiferenciaEntreLastSOEYLN < diasMinimosDeDiferenciaLunaNuevaConLastSOE) {

				firstLNAfterLastSOE = luna;
				diasMinimosDeDiferenciaLunaNuevaConLastSOE = diasDeDiferenciaEntreLastSOEYLN;
			}

			if(date.isAfter(fechaLuna)) {
				lunasNuevasPasadasDesdeLastSOEHastaDateO++;
			}
		}

		// El control era un OR y luego se leian las dos referencias: bastaba con que una
		// de las dos fuese null para provocar un NullPointerException
		if(lastLNBeforeNextSOE == null || firstLNAfterLastSOE == null) {

			LOG.warn("Error, no hay lastLNBeforeNextSOE o firstLNAfterLastSOE para {}.", date);
			return null;
		}

		// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
		if(date.isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && date.isBefore(fechaNextSOE)) {

			return this.tablasReferenciaService.getMes(nextSOE.getStartingSeason(), 0, false);
		}

		// Si la fecha a consultar esta entre el lastSOE y la primera luna, pertenece al mes hibrido de ese soe.
		// Pero si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
		if (date.isBefore(firstLNAfterLastSOE.getDate().toLocalDate()) && date.isAfter(fechaLastSOE)) {

			if(lastSOE.isSolsticioInvierno()) {

				return this.tablasReferenciaService.getMes(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);
			}

			return this.tablasReferenciaService.getMes(lastSOE.getStartingSeason(), 0, false);
		}

		return this.tablasReferenciaService.getMes(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, false);
	}


	/**
	 * EN: Inserts the eighteen fixed month rows. Does nothing if the table already has rows.
	 * ES: Inserta las dieciocho filas fijas de meses. No hace nada si la tabla ya tiene filas.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateMonths() {

		LOG.info("Actualizando los Meses.");

		if(this.monthsRepository.count() > 0) {

			LOG.warn("Ya hay meses en la base de datos.");
			return "Error al actualizar los meses: ya hay meses en la base de datos.";
		}

		List<MonthsEntity> monthsParaDDB = new ArrayList<>();

		monthsParaDDB.add(this.createMonth("Prierno", false, 1, 1, false));
		monthsParaDDB.add(this.createMonth("Seguerno", false, 2, 1, false));
		monthsParaDDB.add(this.createMonth("Terno", false, 3, 1, false));
		monthsParaDDB.add(this.createMonth("Pinera", false, 1, 2, false));
		monthsParaDDB.add(this.createMonth("Seguera", false, 2, 2, false));
		monthsParaDDB.add(this.createMonth("Tera", false, 3, 2, false));
		monthsParaDDB.add(this.createMonth("Prano", false, 1, 3, false));
		monthsParaDDB.add(this.createMonth("Segano", false, 2, 3, false));
		monthsParaDDB.add(this.createMonth("Tano", false, 3, 3, false));
		monthsParaDDB.add(this.createMonth("Pridor", false, 1, 4, false));
		monthsParaDDB.add(this.createMonth("Sedor", false, 2, 4, false));
		monthsParaDDB.add(this.createMonth("Tor", false, 3, 4, false));
		monthsParaDDB.add(this.createMonth("Invera", true, 0, 2, false));
		monthsParaDDB.add(this.createMonth("Primano", true, 0, 3, false));
		monthsParaDDB.add(this.createMonth("Verdor", true, 0, 4, false));
		monthsParaDDB.add(this.createMonth("Oterno", true, 0, 1, false));
		monthsParaDDB.add(this.createMonth("Oterno liminal", true, 0, 1, true));
		monthsParaDDB.add(this.createMonth("Nomon", false, 0, 0, false));

		this.monthsRepository.saveAll(monthsParaDDB);

		LOG.info("Meses actualizados");

		return "Meses actualizados correctamente.";
	}

	/**
	 * EN: Builds one month row in memory.
	 * ES: Construye en memoria una fila de mes.
	 *
	 * @param name          EN: name of the month. / ES: nombre del mes.
	 * @param hibrid        EN: whether it spans a change of season. / ES: si abarca un cambio de estación.
	 * @param monthOfSeason EN: position within the season. / ES: posición dentro de la estación.
	 * @param season        EN: season it belongs to. / ES: estación a la que pertenece.
	 * @param liminal       EN: whether it is the liminal month. / ES: si es el mes liminal.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private MonthsEntity createMonth(String name, boolean hibrid, int monthOfSeason, int season, boolean liminal) {

		MonthsEntity newMonth = new MonthsEntity();
		newMonth.setName(name);
		newMonth.setHibrid(hibrid);
		newMonth.setMonthOfSeason(monthOfSeason);
		newMonth.setSeason(season);
		newMonth.setLiminal(liminal);

		return newMonth;
	}
}
