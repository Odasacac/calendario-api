package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonIADTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MetonoInvernalApofasalRemotoDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Utils.IndiceTemporal;


/**
 * EN: Manages the metons and the VAU counters built on them.
 * ES: Gestiona los métonos y los contadores VAU que se construyen sobre ellos.
 */
@Service
public class MetonsServiceImpl implements MetonsService {

	private static final Logger LOG = LoggerFactory.getLogger(MetonsServiceImpl.class);

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	/**
	 * EN: Search window for the closest candidate in whole days. Two full days guarantee that
	 * any element one calendar day away or less falls inside the window, and that is the only
	 * kind that can end up satisfying the sidereal day tolerance.
	 * ES: Ventana de búsqueda para el candidato más próximo en días naturales. Dos días
	 * completos garantizan que cualquier elemento a distancia de un día natural o
	 * menos entra en la ventana, que es la única que puede acabar cumpliendo la
	 * tolerancia de un día sideral.
	 */
	private static final long VENTANA_EN_SEGUNDOS = 2 * 86400L;

	@Autowired
	private MetonsRepository metonsRepository;

	@Autowired
	private LunasRepository lunasRepository;

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;

	/**
	 * EN: Most recent winter apofasal remote meton on or before the date: winter solstice with
	 * a new moon at apogee, all within one sidereal day. It is the reference the VAU meton
	 * counters hang from.
	 * ES: Métono invernal apofasal remoto más reciente en la fecha o anterior: solsticio de
	 * invierno con luna nueva en apogeo, todo dentro de un día sideral. Es la referencia de la
	 * que cuelgan los contadores de métono VAU.
	 *
	 * @param allMetons EN: metons to search. / ES: métonos donde buscar.
	 * @param date      EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the meton, or {@code null} if there is none. It used to return an empty
	 *         entity, so the {@code != null} checks in the callers never fired and the NPE
	 *         showed up later, when its date was read. / ES: el métono, o {@code null} si no
	 *         hay ninguno. Antes devolvía una entidad vacía, con lo que los controles
	 *         {@code != null} de quien lo llamaba nunca saltaban y el NPE aparecía más
	 *         adelante al leer su fecha.
	 */
	public MetonsEntity getLastMetonIApofasalRemoto(List<MetonsEntity> allMetons, LocalDate date) {

		return this.getUltimoMetonoHasta(allMetons, date, metono -> metono.isInvernal() && metono.isApofasal() && metono.isSelecto() && metono.isNuevo());
	}


	/**
	 * EN: Most recent winter new meton on or before the date: winter solstice coinciding with
	 * a new moon. It is what marks the start of the current VAU year.
	 * ES: Métono invernal nuevo más reciente en la fecha o anterior: solsticio de invierno que
	 * coincide con luna nueva. Es lo que marca el comienzo del año VAU actual.
	 *
	 * @param allMetons EN: metons to search. / ES: métonos donde buscar.
	 * @param date      EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the meton, or {@code null} if there is none. / ES: el métono, o {@code null} si no hay ninguno.
	 */
	public MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date) {

		return this.getUltimoMetonoHasta(allMetons, date, metono -> metono.isInvernal() && metono.isNuevo());
	}

	/**
	 * EN: Shared search for the two lookups above: walks the list once and keeps the latest
	 * meton on or before the date that satisfies the given filter.
	 * ES: Búsqueda común para las dos consultas anteriores: recorre la lista una vez y se queda
	 * con el métono más tardío, en la fecha o anterior, que cumpla el filtro dado.
	 *
	 * @param allMetons EN: metons to search; may be {@code null}. / ES: métonos donde buscar; admite {@code null}.
	 * @param date      EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @param filtro    EN: condition the meton must satisfy. / ES: condición que debe cumplir el métono.
	 * @return EN: the meton, or {@code null} if none matches. / ES: el métono, o {@code null} si ninguno encaja.
	 */
	private MetonsEntity getUltimoMetonoHasta(List<MetonsEntity> allMetons, LocalDate date, java.util.function.Predicate<MetonsEntity> filtro) {

		MetonsEntity encontrado = null;
		long diaMasReciente = Long.MIN_VALUE;
		long diaReferencia = date.toEpochDay();

		if (allMetons == null) {
			return null;
		}

		for (MetonsEntity metono : allMetons) {

			if (metono.getDate() == null || !filtro.test(metono)) {
				continue;
			}

			long dia = metono.getDate().toLocalDate().toEpochDay();

			if (dia <= diaReferencia && dia > diaMasReciente) {

				diaMasReciente = dia;
				encontrado = metono;
			}
		}

		return encontrado;
	}


	/**
	 * EN: Counts the winter apofasal remote metons between the reference eclipeno and the
	 * date. It is the longest cycle below the eclipeno itself, so on most dates the counter
	 * sits at one.
	 * ES: Cuenta los métonos invernales apofasales remotos entre el eclípeno de referencia y la
	 * fecha. Es el ciclo más largo por debajo del propio eclípeno, así que en la mayoría de
	 * fechas el contador se queda en uno.
	 *
	 * @param lastEclipenoInvernalApofasalRemoto EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param allMetons                          EN: metons in range, most recent first. / ES: métonos del rango, del más reciente al más antiguo.
	 * @param date                               EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the counter for this cycle. / ES: el contador de este ciclo.
	 */
	public MetonoInvernalApofasalRemotoDTO getMetonoInvernalApofasalRemoto(EclipenosEntity lastEclipenoInvernalApofasalRemoto, List<MetonsEntity> allMetons, LocalDate date) {

		MetonoInvernalApofasalRemotoDTO metonoInvernalApofasalRemotoDTO = new MetonoInvernalApofasalRemotoDTO();

		LocalDate fechaEclipeno = lastEclipenoInvernalApofasalRemoto.getDate().toLocalDate();

		if(fechaEclipeno.isEqual(date)){

			metonoInvernalApofasalRemotoDTO.setMetonoInvernalApofasalRemotoDay(true);
			metonoInvernalApofasalRemotoDTO.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(0);
			metonoInvernalApofasalRemotoDTO.setNumberOfMetonoInvernalApofasalRemoto(0);
			metonoInvernalApofasalRemotoDTO.setYearOfCurrentMetonoInvernalApofasalRemoto(0);

			return metonoInvernalApofasalRemotoDTO;
		}

		List<MetonsEntity> metonosInvernalesApofasalesRemotos = new ArrayList<>();

		for(MetonsEntity metono : allMetons) {

			if(metono.isInvernal() && metono.isApofasal() && metono.isNuevo() && metono.isSelecto()
					&& !metono.getDate().isBefore(lastEclipenoInvernalApofasalRemoto.getDate())
					&& !metono.getDate().toLocalDate().isAfter(date)) {

				metonosInvernalesApofasalesRemotos.add(metono);
			}
		}

		// Se accedia al elemento 0 sin comprobar la lista: con una fecha sin metonos
		// posteriores al eclipeno saltaba un IndexOutOfBoundsException
		if (metonosInvernalesApofasalesRemotos.isEmpty()) {

			LOG.warn("No hay metonos invernales apofasales remotos entre {} y {}", fechaEclipeno, date);
			return metonoInvernalApofasalRemotoDTO;
		}

		MetonsEntity masReciente = metonosInvernalesApofasalesRemotos.get(0);

		metonoInvernalApofasalRemotoDTO.setYearOfCurrentMetonoInvernalApofasalRemoto(masReciente.getYear());
		metonoInvernalApofasalRemotoDTO.setMetonoInvernalApofasalRemotoDay(masReciente.getDate().toLocalDate().isEqual(date));

		int metonosIARDesdeElLastEclipenSelecto = metonosInvernalesApofasalesRemotos.size() - 1; // -1 porque incluye el del eclipeno

		// No se suma un eclipeno hasta que pase el dia del eclipeno, pero si es el dia de eclipeno no se resta, que se ha restado antes
		if(metonoInvernalApofasalRemotoDTO.isMetonoInvernalApofasalRemotoDay()) {

			metonosIARDesdeElLastEclipenSelecto = metonosIARDesdeElLastEclipenSelecto - 1;
		}

		metonoInvernalApofasalRemotoDTO.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(metonosIARDesdeElLastEclipenSelecto);
		metonoInvernalApofasalRemotoDTO.setNumberOfMetonoInvernalApofasalRemoto(metonosIARDesdeElLastEclipenSelecto + 1);

		return metonoInvernalApofasalRemotoDTO;
	}


	/**
	 * EN: Counts the two winter meton cycles since the reference meton: the new ones (IN,
	 * solstice with a new moon) and the aporic ones (IA, solstice with an apogee). Both lists
	 * arrive most-recent first, so element zero is the current meton of each cycle. The day of
	 * a meton belongs to no meton, which is what the extra subtractions are for.
	 * ES: Cuenta los dos ciclos de métonos invernales desde el métono de referencia: los nuevos
	 * (IN, solsticio con luna nueva) y los apóricos (IA, solsticio con apogeo). Ambas listas
	 * llegan del más reciente al más antiguo, así que el elemento cero es el métono actual de
	 * cada ciclo. El día de un métono no pertenece a ningún métono, y para eso están las restas
	 * adicionales.
	 *
	 * @param lastMetonIApofasalRemoto EN: reference meton the counts hang from. / ES: métono de referencia del que cuelgan las cuentas.
	 * @param lastEclipenoINSelecto    EN: reference eclipeno; kept for signature compatibility. / ES: eclípeno de referencia; se mantiene por compatibilidad de firma.
	 * @param metons                   EN: metons in range, most recent first. / ES: métonos del rango, del más reciente al más antiguo.
	 * @param date                     EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the IN and IA counters with their qualifiers. / ES: los contadores IN e IA con sus apellidos.
	 */
	public MetonDTO getVAUMeton (MetonsEntity lastMetonIApofasalRemoto, EclipenosEntity lastEclipenoINSelecto, List<MetonsEntity> metons, LocalDate date) {

		MetonDTO metonVAU = new MetonDTO();

		MetonINDTO metonINDTO = new MetonINDTO();
		MetonIADTO metonIADTO = new MetonIADTO();

		metonVAU.setMetonsIN(metonINDTO);
		metonVAU.setMetonsIA(metonIADTO);

		List<MetonsEntity> metonsIN = new ArrayList<>();
		List<MetonsEntity> metonsIA = new ArrayList<>();

		LocalDateTime fechaMetonoIAR = lastMetonIApofasalRemoto.getDate();

		for(MetonsEntity meton : metons) {

			if(meton.isInvernal() && !meton.getDate().toLocalDate().isAfter(date) && !meton.getDate().isBefore(fechaMetonoIAR)) {

				if(meton.isNuevo()) {
					metonsIN.add(meton);
				}
				else if(meton.isAporico()) {
					metonsIA.add(meton);
				}
			}
		}

		// Ambas listas se leian por indice 0 sin comprobarlas antes
		if (metonsIN.isEmpty() || metonsIA.isEmpty()) {

			LOG.warn("No hay metonos invernales nuevos y/o aporicos entre {} y {}", fechaMetonoIAR.toLocalDate(), date);
			return metonVAU;
		}

		boolean esDiaDelMetonoIAR = fechaMetonoIAR.toLocalDate().isEqual(date);

		MetonsEntity metonINActual = metonsIN.get(0);
		MetonsEntity metonIAActual = metonsIA.get(0);

		metonINDTO.setYearOfCurrentMetonIN(metonINActual.getYear());
		metonIADTO.setYearOfCurrentMetonIA(metonIAActual.getYear());

		metonINDTO.setMetonoINDay(metonINActual.getDate().toLocalDate().isEqual(date));
		metonIADTO.setMetonoIADay(metonIAActual.getDate().toLocalDate().isEqual(date));

		int metonosINDesdeElLastEclipen = metonsIN.size() - 1; // -1 porque incluye el del eclipeno

		// No se suma un metono hasta que pase el dia del metono, pero si es el dia de eclipeno no se resta, que se ha restado antes
		if(metonINDTO.isMetonoINDay() && !esDiaDelMetonoIAR) {

			metonosINDesdeElLastEclipen = metonosINDesdeElLastEclipen - 1;
		}

		metonINDTO.setMetonosINSinceLastEclipenoIN(metonosINDesdeElLastEclipen);
		int yearOfTheMetonIN = metonosINDesdeElLastEclipen + 1;

		if(esDiaDelMetonoIAR) { //Si es el dia del metonoIAR, no estamos en ningun metono
			yearOfTheMetonIN = yearOfTheMetonIN - 1;
		}

		metonINDTO.setNumberOfMetonIN(yearOfTheMetonIN);

		int metonosIADesdeElMetonIApofasalRemoto = metonsIA.size() - 1;

		if(metonIADTO.isMetonoIADay() && !esDiaDelMetonoIAR) {

			metonosIADesdeElMetonIApofasalRemoto = metonosIADesdeElMetonIApofasalRemoto - 1;
		}

		metonIADTO.setMetonosIASinceLastEclipenoSelecto(metonosIADesdeElMetonIApofasalRemoto);
		int yearOfTheMetonIA = metonosIADesdeElMetonIApofasalRemoto + 1;

		if(esDiaDelMetonoIAR) {
			yearOfTheMetonIA = yearOfTheMetonIA - 1;
		}

		metonIADTO.setNumberOfMetonIA(yearOfTheMetonIA);

		if(metonINActual.isInvertido() && yearOfTheMetonIN != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonINActual.isSelecto() && yearOfTheMetonIN != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Selecto)");
		}

		// El apellido del metono aporico se decidia mirando yearOfTheMetonIN
		if(metonIAActual.isInvertido() && yearOfTheMetonIA != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonIAActual.isSelecto() && yearOfTheMetonIA != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Selecto)");
		}

		return metonVAU;
	}

	/**
	 * EN: Creates the metons: for every solstice and equinox, one meton per moon phase and one
	 * per apogee or perigee falling within one sidereal day of it. Afterwards it marks the
	 * apofasal ones, which are those where both coincide at once.
	 * ES: Crea los métonos: por cada solsticio y equinoccio, un métono por cada fase lunar y
	 * otro por cada apogeo o perigeo que caiga dentro de un día sideral. Después marca los
	 * apofasales, que son aquellos en los que coinciden ambos a la vez.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateMetonos() {

		String resultado = "Metonos checkeados sin problema.";

		LOG.info("Iniciando evaluacion de metonos.");

		if (this.metonsRepository.count() > 0) {

			LOG.warn("Ya hay metonos en la BBDD");
			return "Error a la hora de actualizar los metonos: ya hay metonos en la base de datos.";
		}

		List<LunasEntity> allLunas = this.lunasRepository.findAll();
		List<ApogeosYPerigeosLunaEntity> allApoperis = this.apogeosYPerigeosLunaRepository.findAll();
		List<SolsticiosYEquinocciosEntity> allSoes = this.solsticiosYEquinocciosRepository.findAll();

		if (allLunas.isEmpty()) {

			LOG.error("No hay lunas en la base de datos.");
			return "Error al chequear metonos: no hay lunas en la base de datos.";
		}

		if (allSoes.isEmpty()) {

			LOG.error("No hay soes en la base de datos.");
			return "Error al chequear metonos: no hay soes en la base de datos.";
		}

		// Antes se cruzaba cada soe contra la tabla entera de lunas y de apoperis:
		// del orden de 8.400 x 133.000 comparaciones. Con los indices ordenados solo
		// se recorren las filas que caen dentro del dia sideral de cada soe.
		IndiceTemporal<LunasEntity> indiceLunas = IndiceTemporal.de(allLunas, LunasEntity::getDate);
		IndiceTemporal<ApogeosYPerigeosLunaEntity> indiceApoperis = IndiceTemporal.de(allApoperis, ApogeosYPerigeosLunaEntity::getDate);

		List<MetonsEntity> metonosParaDB = new ArrayList<>();

		for (SolsticiosYEquinocciosEntity soe : allSoes) {

			for (LunasEntity luna : indiceLunas.enVentana(soe.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

				if (luna.isNueva() || luna.isLlena()) {

					metonosParaDB.add(this.crearMetonoFasal(soe, luna));
					LOG.debug("Nuevo métono fasal encontrado: {}", soe.getDate().toLocalDate());
				}
			}

			for (ApogeosYPerigeosLunaEntity apoperi : indiceApoperis.enVentana(soe.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

				if (apoperi.isEsApogeo() || apoperi.isEsPerigeo()) {

					metonosParaDB.add(this.crearMetonoApoperico(soe, apoperi));
					LOG.debug("Nuevo métono apopérico encontrado: {}", soe.getDate().toLocalDate());
				}
			}
		}

		LOG.info("Actualizando métonos apofasales");

		this.marcarApofasales(metonosParaDB, allLunas, allApoperis, indiceLunas, indiceApoperis);

		this.metonsRepository.saveAll(metonosParaDB);

		LOG.info("Evaluacion de métonos finalizada: {} métonos", metonosParaDB.size());

		return resultado;
	}

	/**
	 * EN: Builds a fasal meton: a solstice or equinox coinciding with a moon phase. It takes
	 * its date and season from the solstice, and its lunar traits from the phase.
	 * ES: Construye un métono fasal: un solsticio o equinoccio que coincide con una fase lunar.
	 * Toma la fecha y la estación del solsticio, y los rasgos lunares de la fase.
	 *
	 * @param soe  EN: solstice or equinox. / ES: solsticio o equinoccio.
	 * @param luna EN: moon phase coinciding with it. / ES: fase lunar que coincide con él.
	 * @return EN: the meton, not yet persisted. / ES: el métono, todavía sin persistir.
	 */
	private MetonsEntity crearMetonoFasal(SolsticiosYEquinocciosEntity soe, LunasEntity luna) {

		MetonsEntity nuevoMetono = new MetonsEntity();

		nuevoMetono.setFasal(true);

		nuevoMetono.setLunaId(luna.getId());
		nuevoMetono.setSelecto(luna.isSelecta());
		nuevoMetono.setInvertido(luna.isInvertida());
		nuevoMetono.setNuevo(luna.isNueva());
		nuevoMetono.setLleno(luna.isLlena());

		this.copiarDatosDelSoe(nuevoMetono, soe);

		return nuevoMetono;
	}

	/**
	 * EN: Builds an apoperico meton: a solstice or equinox coinciding with an apogee or a
	 * perigee.
	 * ES: Construye un métono apopérico: un solsticio o equinoccio que coincide con un apogeo o
	 * un perigeo.
	 *
	 * @param soe     EN: solstice or equinox. / ES: solsticio o equinoccio.
	 * @param apoperi EN: apogee or perigee coinciding with it. / ES: apogeo o perigeo que coincide con él.
	 * @return EN: the meton, not yet persisted. / ES: el métono, todavía sin persistir.
	 */
	private MetonsEntity crearMetonoApoperico(SolsticiosYEquinocciosEntity soe, ApogeosYPerigeosLunaEntity apoperi) {

		MetonsEntity nuevoMetono = new MetonsEntity();

		nuevoMetono.setApoperico(true);

		nuevoMetono.setApoperiId(apoperi.getId());
		nuevoMetono.setSelecto(apoperi.isEsSelecto());
		nuevoMetono.setInvertido(apoperi.isEsInvertido());
		nuevoMetono.setPerico(apoperi.isEsPerigeo());
		nuevoMetono.setAporico(apoperi.isEsApogeo());

		this.copiarDatosDelSoe(nuevoMetono, soe);

		return nuevoMetono;
	}

	/**
	 * EN: Copies onto the meton the fields it inherits from its solstice or equinox: date,
	 * year and season.
	 * ES: Copia al métono los campos que hereda de su solsticio o equinoccio: fecha, año y
	 * estación.
	 *
	 * @param metono EN: meton being built. / ES: métono que se está construyendo.
	 * @param soe    EN: solstice or equinox it comes from. / ES: solsticio o equinoccio del que procede.
	 */
	private void copiarDatosDelSoe(MetonsEntity metono, SolsticiosYEquinocciosEntity soe) {

		metono.setSoeId(soe.getId());
		metono.setYear(soe.getYear());
		metono.setDate(soe.getDate());

		metono.setInvernal(soe.isSolsticioInvierno());
		metono.setPrimaveral(soe.isEquinoccioPrimavera());
		metono.setEstival(soe.isSolsticioVerano());
		metono.setOtonyal(soe.isEquinoccioOtonyo());
	}

	/**
	 * EN: Marks the apofasal metons. A meton is apofasal when the moon phase, the apogee or
	 * perigee and the meton itself all fall within the same sidereal day. Only metons already
	 * flagged selecto or invertido are worth checking, because that flag is what says the
	 * phenomenon has a lunar counterpart nearby.
	 * ES: Marca los métonos apofasales. Un métono es apofasal cuando la luna, el apoperi y el
	 * propio métono caen todos dentro del mismo día sideral. Sólo merece la pena comprobar los
	 * métonos ya marcados como selecto o invertido, porque esa marca es la que indica que el
	 * fenómeno tiene una contraparte lunar cerca.
	 *
	 * @param metonos        EN: metons to inspect. / ES: métonos que se inspeccionan.
	 * @param allLunas       EN: every moon phase. / ES: todas las fases lunares.
	 * @param allApoperis    EN: every apogee and perigee. / ES: todos los apogeos y perigeos.
	 * @param indiceLunas    EN: date-ordered index of the moon phases. / ES: índice de fases lunares ordenado por fecha.
	 * @param indiceApoperis EN: date-ordered index of the apogees and perigees. / ES: índice de apogeos y perigeos ordenado por fecha.
	 */
	private void marcarApofasales(List<MetonsEntity> metonos,
			List<LunasEntity> allLunas,
			List<ApogeosYPerigeosLunaEntity> allApoperis,
			IndiceTemporal<LunasEntity> indiceLunas,
			IndiceTemporal<ApogeosYPerigeosLunaEntity> indiceApoperis) {

		Map<Long, LunasEntity> lunasPorId = new HashMap<>();

		for (LunasEntity luna : allLunas) {
			lunasPorId.put(luna.getId(), luna);
		}

		Map<Long, ApogeosYPerigeosLunaEntity> apoperisPorId = new HashMap<>();

		for (ApogeosYPerigeosLunaEntity apoperi : allApoperis) {
			apoperisPorId.put(apoperi.getId(), apoperi);
		}

		for (MetonsEntity meton : metonos) {

			if (!meton.isSelecto() && !meton.isInvertido()) {
				continue;
			}

			if (meton.getLunaId() != null) {

				LunasEntity luna = lunasPorId.get(meton.getLunaId());

				if (luna == null) {
					continue;
				}

				ApogeosYPerigeosLunaEntity apoperiMasCercano = this.masCercanoEnDias(
						indiceApoperis.enVentana(luna.getDate(), VENTANA_EN_SEGUNDOS), luna.getDate(), ApogeosYPerigeosLunaEntity::getDate);

				if (apoperiMasCercano != null
						&& this.dentroDeTolerancia(apoperiMasCercano.getDate(), meton.getDate())
						&& this.dentroDeTolerancia(luna.getDate(), meton.getDate())) {

					meton.setApofasal(true);
					LOG.debug("Nuevo métono apofasal encontrado: {}", meton.getDate().toLocalDate());
				}
			}
			else if (meton.getApoperiId() != null) {

				ApogeosYPerigeosLunaEntity apoperi = apoperisPorId.get(meton.getApoperiId());

				if (apoperi == null) {
					continue;
				}

				LunasEntity lunaMasCercana = null;
				long mejorDistancia = Long.MAX_VALUE;

				for (LunasEntity luna : indiceLunas.enVentana(apoperi.getDate(), VENTANA_EN_SEGUNDOS)) {

					if (!luna.isNueva() && !luna.isLlena()) {
						continue;
					}

					long distancia = Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), apoperi.getDate().toLocalDate()));

					if (distancia < mejorDistancia) {
						mejorDistancia = distancia;
						lunaMasCercana = luna;
					}
				}

				if (lunaMasCercana != null
						&& this.dentroDeTolerancia(lunaMasCercana.getDate(), meton.getDate())
						&& this.dentroDeTolerancia(apoperi.getDate(), meton.getDate())
						&& this.dentroDeTolerancia(apoperi.getDate(), lunaMasCercana.getDate())) {

					meton.setApofasal(true);
					LOG.debug("Nuevo métono apofasal encontrado: {}", meton.getDate().toLocalDate());
				}
			}
		}
	}

	/**
	 * EN: Closest candidate in whole days to a reference date, scanning in chronological
	 * order so that ties go to the earlier one.
	 * ES: Candidato más cercano en días naturales a una fecha de referencia, recorriendo en
	 * orden cronológico para que los empates se resuelvan a favor del más antiguo.
	 *
	 * @param candidatos EN: candidates to compare. / ES: candidatos a comparar.
	 * @param referencia EN: instant to measure against. / ES: instante contra el que se mide.
	 * @param fecha      EN: how to read the date of each candidate. / ES: cómo obtener la fecha de cada candidato.
	 * @return EN: the closest one, or {@code null} if there are no candidates. / ES: el más cercano, o {@code null} si no hay candidatos.
	 */
	private <T> T masCercanoEnDias(List<T> candidatos, LocalDateTime referencia, Function<T, LocalDateTime> fecha) {

		T masCercano = null;
		long mejorDistancia = Long.MAX_VALUE;
		LocalDate diaReferencia = referencia.toLocalDate();

		for (T candidato : candidatos) {

			long distancia = Math.abs(ChronoUnit.DAYS.between(fecha.apply(candidato).toLocalDate(), diaReferencia));

			if (distancia < mejorDistancia) {
				mejorDistancia = distancia;
				masCercano = candidato;
			}
		}

		return masCercano;
	}

	/**
	 * EN: Whether two instants are within one sidereal day of each other, which is the
	 * tolerance the whole domain uses to decide that two phenomena coincide.
	 * ES: Si dos instantes están a menos de un día sideral el uno del otro, que es la
	 * tolerancia que usa todo el dominio para decidir que dos fenómenos coinciden.
	 *
	 * @param uno  EN: first instant. / ES: primer instante.
	 * @param otro EN: second instant. / ES: segundo instante.
	 * @return EN: {@code true} if they coincide. / ES: {@code true} si coinciden.
	 */
	private boolean dentroDeTolerancia(LocalDateTime uno, LocalDateTime otro) {

		return Math.abs(ChronoUnit.SECONDS.between(uno, otro)) <= TOLERANCIA_EN_SEGUNDOS;
	}
}
