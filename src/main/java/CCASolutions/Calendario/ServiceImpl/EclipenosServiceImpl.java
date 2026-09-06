package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Utils.IndiceTemporal;

/**
 * EN: Manages the eclipenos, the rarest cycle of the calendar and the origin every other
 * VAU counter hangs from.
 * ES: Gestiona los eclípenos, el ciclo más excepcional del calendario y el origen del que
 * cuelgan todos los demás contadores VAU.
 */
@Service
public class EclipenosServiceImpl implements EclipenosService{

	private static final Logger LOG = LoggerFactory.getLogger(EclipenosServiceImpl.class);

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	@Autowired
	private MetonsRepository metonsRepository;

	@Autowired
	private EclipenosRepository eclipenosRepository;

	@Autowired
	private EclipsesRepository eclipsesRepository;

	/**
	 * EN: Most recent winter new eclipeno with an annular or total eclipse, on or before the
	 * date.
	 * ES: Eclípeno invernal nuevo más reciente con eclipse anular o total, en la fecha dada o
	 * anterior.
	 *
	 * @param allEclipenos EN: eclipenos to search. / ES: eclípenos donde buscar.
	 * @param date         EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the eclipeno, or {@code null} if there is none. / ES: el eclípeno, o {@code null} si no hay ninguno.
	 */
	public EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date) {

		return this.getUltimoEclipenoHasta(allEclipenos, date,
				eclipeno -> eclipeno.isInvernal() && eclipeno.isNuevo() && (eclipeno.isEsAnular() || eclipeno.isEsTotal()));
	}


	/*
		InvernalApofasalRemoto

		Invernal = solsticio de invierno
		Apofasal = luna y apoperi ambos a menos de un dia sideral
		Remoto = Luna nueva y apogeo
	*/
	/**
	 * EN: Most recent winter apofasal remote eclipeno on or before the date: a winter
	 * solstice with a new moon at apogee within a sidereal day, and an annular or total
	 * eclipse on top. It is the origin of every VAU count.
	 * ES: Eclípeno invernal apofasal remoto más reciente en la fecha dada o anterior: un
	 * solsticio de invierno con luna nueva en apogeo dentro de un día sideral, y encima un
	 * eclipse anular o total. Es el origen de todas las cuentas VAU.
	 *
	 * @param allEclipenos EN: eclipenos to search. / ES: eclípenos donde buscar.
	 * @param date         EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @return EN: the eclipeno, or {@code null} if there is none. / ES: el eclípeno, o {@code null} si no hay ninguno.
	 */
	public EclipenosEntity getLastEclipenoInvernalApofasalRemoto(List<EclipenosEntity> allEclipenos, LocalDate date) {

		return this.getUltimoEclipenoHasta(allEclipenos, date,
				eclipeno -> eclipeno.isInvernal() && eclipeno.isNuevo() && eclipeno.isApofasal() && eclipeno.isSelecto()
						&& (eclipeno.isEsAnular() || eclipeno.isEsTotal()));
	}

	/**
	 * EN: Shared search for both of the above: walks the list once and keeps the latest
	 * eclipeno on or before the date that satisfies the given filter.
	 * ES: Búsqueda común para los dos anteriores: recorre la lista una vez y se queda con el
	 * eclípeno más tardío, en la fecha o anterior, que cumpla el filtro dado.
	 *
	 * @param allEclipenos EN: eclipenos to search; may be {@code null}. / ES: eclípenos donde buscar; admite {@code null}.
	 * @param date         EN: upper bound, inclusive. / ES: cota superior, incluida.
	 * @param filtro       EN: condition the eclipeno must satisfy. / ES: condición que debe cumplir el eclípeno.
	 * @return EN: the eclipeno, or {@code null} if none matches. / ES: el eclípeno, o {@code null} si ninguno encaja.
	 */
	private EclipenosEntity getUltimoEclipenoHasta(List<EclipenosEntity> allEclipenos, LocalDate date, Predicate<EclipenosEntity> filtro) {

		EclipenosEntity encontrado = null;
		long diaMasReciente = Long.MIN_VALUE;
		long diaReferencia = date.toEpochDay();

		if (allEclipenos == null) {
			return null;
		}

		for (EclipenosEntity eclipeno : allEclipenos) {

			if (eclipeno.getDate() == null || !filtro.test(eclipeno)) {
				continue;
			}

			long dia = eclipeno.getDate().toLocalDate().toEpochDay();

			if (dia <= diaReferencia && dia > diaMasReciente) {

				diaMasReciente = dia;
				encontrado = eclipeno;
			}
		}

		return encontrado;
	}

	/**
	 * EN: Days elapsed since the reference eclipeno, already formatted as text, and whether
	 * the date is that very day.
	 * ES: Días transcurridos desde el eclípeno de referencia, ya formateados como texto, y si
	 * la fecha es ese mismo día.
	 *
	 * @param lastEclipenoSelecto EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param date                EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: elapsed days and the same-day flag. / ES: los días transcurridos y la marca de mismo día.
	 */
	public EclipenoSelectoDTO getVAUEclipenoSelecto(EclipenosEntity lastEclipenoSelecto, LocalDate date) {

		EclipenoSelectoDTO eclipenoSelectoVAU = new EclipenoSelectoDTO();

		LocalDate fechaEclipeno = lastEclipenoSelecto.getDate().toLocalDate();

		eclipenoSelectoVAU.setDaysSinceCurrentEclipenoSelectoIN("hace " + ChronoUnit.DAYS.between(fechaEclipeno, date) + " días");
		eclipenoSelectoVAU.setEclipenoINSelectoDay(fechaEclipeno.isEqual(date));

		return eclipenoSelectoVAU;
	}

	/**
	 * EN: Counts the winter new eclipenos between the reference eclipeno and the date. The
	 * eclipeno list arrives most-recent first, so element zero is the current eclipeno. The
	 * day of an eclipeno belongs to no eclipeno, hence the extra subtraction.
	 * ES: Cuenta los eclípenos invernales nuevos entre el eclípeno de referencia y la fecha.
	 * La lista de eclípenos llega del más reciente al más antiguo, así que el elemento cero es
	 * el eclípeno actual. El día de un eclípeno no pertenece a ningún eclípeno, de ahí la
	 * resta adicional.
	 *
	 * @param allEclipenos        EN: every eclipeno, most recent first. / ES: todos los eclípenos, del más reciente al más antiguo.
	 * @param lastEclipenoSelecto EN: reference eclipeno. / ES: eclípeno de referencia.
	 * @param date                EN: date being consulted. / ES: fecha que se consulta.
	 * @return EN: the eclipeno counter and its qualifier. / ES: el contador de eclípeno y su apellido.
	 */
	public EclipenoINDTO getVAUEclipeno(List<EclipenosEntity> allEclipenos, EclipenosEntity lastEclipenoSelecto, LocalDate date) {

		EclipenoINDTO eclipenoVAU = new EclipenoINDTO();

		LocalDate fechaEclipenoSelecto = lastEclipenoSelecto.getDate().toLocalDate();

		if(fechaEclipenoSelecto.isEqual(date)){

			eclipenoVAU.setEclipenoINDay(true);
			eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(0);
			eclipenoVAU.setNumberOfEclipenoIN(0);
			eclipenoVAU.setYearOfCurrentEclipenoIN(lastEclipenoSelecto.getYear());

			return eclipenoVAU;
		}

		List<EclipenosEntity> eclipenosIN = new ArrayList<>();

		for(EclipenosEntity eclipeno : allEclipenos) {

			if(eclipeno.isInvernal() && eclipeno.isNuevo()
					&& !eclipeno.getDate().isBefore(lastEclipenoSelecto.getDate())
					&& !eclipeno.getDate().toLocalDate().isAfter(date)) {

				eclipenosIN.add(eclipeno);
			}
		}

		// Se leia el elemento 0 sin comprobar la lista
		if (eclipenosIN.isEmpty()) {

			LOG.warn("No hay eclípenos invernales nuevos entre {} y {}", fechaEclipenoSelecto, date);
			return eclipenoVAU;
		}

		EclipenosEntity eclipenoActual = eclipenosIN.get(0);

		eclipenoVAU.setYearOfCurrentEclipenoIN(eclipenoActual.getYear());
		eclipenoVAU.setEclipenoINDay(eclipenoActual.getDate().toLocalDate().isEqual(date));

		int eclipenosDesdeElLastEclipenSelecto = eclipenosIN.size() - 1; // -1 porque incluye el del eclipeno

		// No se suma un eclipeno hasta que pase el dia del eclipeno, pero si es el dia de eclipeno no se resta, que se ha restado antes
		if(eclipenoVAU.isEclipenoINDay()) {

			eclipenosDesdeElLastEclipenSelecto = eclipenosDesdeElLastEclipenSelecto - 1;
		}

		eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(eclipenosDesdeElLastEclipenSelecto);

		int yearOfTheEclipeno = eclipenosDesdeElLastEclipenSelecto + 1;

		eclipenoVAU.setNumberOfEclipenoIN(yearOfTheEclipeno);

		if(yearOfTheEclipeno != 0 && !eclipenoVAU.isEclipenoINDay() && eclipenoActual.isApofasal()) {

			if(eclipenoActual.isInvertido()) {
				eclipenoVAU.setLastEclipenoSurname("(Invertido)");
			}
			else if(eclipenoActual.isSelecto()) {
				eclipenoVAU.setLastEclipenoSurname("(Selecto)");
			}
		}

		return eclipenoVAU;
	}


	/**
	 * EN: Creates the eclipenos: for every fasal meton, one eclipeno per eclipse of the same
	 * year falling within one sidereal day of it. The eclipses are read once and grouped by
	 * year, instead of one query per meton.
	 * ES: Crea los eclípenos: por cada métono fasal, un eclípeno por cada eclipse del mismo año
	 * que caiga dentro de un día sideral. Los eclipses se leen una vez y se agrupan por año, en
	 * lugar de lanzar una consulta por métono.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateEclipenos() {

		LOG.info("Iniciando actualizar eclipenos");

		if (this.eclipenosRepository.count() > 0) {

			LOG.warn("Ya hay eclípenos en la base de datos.");
			return "Error al actualizar los eclípenos: ya hay eclípenos en la base de datos.";
		}

		List<MetonsEntity> metonos = this.metonsRepository.findAll();

		// El mensaje de error estaba invertido: se avisaba de que no habia metonos
		// justo cuando si los habia
		if (metonos.isEmpty()) {

			LOG.error("No hay métonos en la base de datos.");
			return "Error al actualizar los eclípenos: no hay métonos en la base de datos.";
		}

		try {

			// Antes se lanzaba un findByYear por cada metono: unas 1.750 consultas.
			// Ahora se traen los eclipses una sola vez y se agrupan en memoria.
			Map<Integer, List<EclipsesEntity>> eclipsesPorAnyo = new HashMap<>();

			for (EclipsesEntity eclipse : this.eclipsesRepository.findAll()) {

				eclipsesPorAnyo.computeIfAbsent(eclipse.getYear(), anyo -> new ArrayList<>()).add(eclipse);
			}

			List<EclipenosEntity> eclipenosParaDB = new ArrayList<>();

			for (MetonsEntity meton : metonos) {

				if (!meton.isFasal()) {
					continue;
				}

				List<EclipsesEntity> eclipsesDelAnyo = eclipsesPorAnyo.getOrDefault(meton.getYear(), List.of());

				for (EclipsesEntity eclipse : eclipsesDelAnyo) {

					if (Math.abs(ChronoUnit.SECONDS.between(eclipse.getDate(), meton.getDate())) <= TOLERANCIA_EN_SEGUNDOS) {

						LOG.debug("Actualizando los eclípenos del anyo: {}", meton.getYear());
						eclipenosParaDB.add(this.crearEclipeno(meton, eclipse));
					}
				}
			}

			this.eclipenosRepository.saveAll(eclipenosParaDB);

			LOG.info("Eclípenos actualizados: {}", eclipenosParaDB.size());

			return "Eclipenos actualizados sin problema.";
		}
		catch (Exception e) {

			LOG.error("Error al evaluar los eclipenos", e);
			return "Error al evaluar los eclipenos, revisar logs";
		}
	}

	/**
	 * EN: Builds one eclipeno by combining a meton with the eclipse coinciding with it: the
	 * seasonal and lunar traits come from the meton, the eclipse type from the eclipse.
	 * ES: Construye un eclípeno combinando un métono con el eclipse que coincide con él: los
	 * rasgos estacionales y lunares vienen del métono, y el tipo de eclipse del eclipse.
	 *
	 * @param meton   EN: meton the eclipeno is built on. / ES: métono sobre el que se construye el eclípeno.
	 * @param eclipse EN: eclipse coinciding with it. / ES: eclipse que coincide con él.
	 * @return EN: the eclipeno, not yet persisted. / ES: el eclípeno, todavía sin persistir.
	 */
	private EclipenosEntity crearEclipeno(MetonsEntity meton, EclipsesEntity eclipse) {

		EclipenosEntity eclipeno = new EclipenosEntity();

		eclipeno.setDate(meton.getDate());
		eclipeno.setYear(meton.getYear());

		eclipeno.setInvernal(meton.isInvernal());
		eclipeno.setPrimaveral(meton.isPrimaveral());
		eclipeno.setEstival(meton.isEstival());
		eclipeno.setOtonyal(meton.isOtonyal());

		eclipeno.setLleno(meton.isLleno());
		eclipeno.setNuevo(meton.isNuevo());

		eclipeno.setEsAnular(eclipse.isEsAnular());
		eclipeno.setEsParcial(eclipse.isEsParcial());
		eclipeno.setEsTotal(eclipse.isEsTotal());
		eclipeno.setEsPenumbral(eclipse.isEsPenumbral());
		eclipeno.setEsHibrido(eclipse.isEsHibrido());

		eclipeno.setEclipseId(eclipse.getId());
		eclipeno.setMetonoId(meton.getId());
		eclipeno.setInvertido(meton.isInvertido());
		eclipeno.setSelecto(meton.isSelecto());

		eclipeno.setApofasal(meton.isApofasal());

		return eclipeno;
	}
}
