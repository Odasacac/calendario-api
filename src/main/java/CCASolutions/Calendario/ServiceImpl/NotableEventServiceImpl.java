package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO;
import CCASolutions.Calendario.DTOs.MidsisonDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Services.NotableEventService;
import CCASolutions.Calendario.Utils.IndiceTemporal;
import CCASolutions.Calendario.Utils.Vecindad;

/**
 * EN: Works out which astronomical event stands out around a date, and names it.
 * ES: Averigua qué evento astronómico destaca alrededor de una fecha, y le pone nombre.
 */
@Service
public class NotableEventServiceImpl implements NotableEventService {

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	/**
	 * EN: Returns the event of the date itself, the closest previous one and the closest
	 * upcoming one, each already named.
	 * ES: Devuelve el evento de la propia fecha, el anterior más cercano y el próximo más
	 * cercano, cada uno ya con su nombre.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: the three notable events. / ES: los tres eventos notables.
	 */
	public NotableEventDTO getNotableEvent(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		NotableEventDTO notableEventDTO = new NotableEventDTO();

		LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosPPPFecha = this.getFenomenosPPPFecha(date, datosCosmicosParaVAUDTO);

		notableEventDTO.setToday(this.getEventoActual(fenomenosPPPFecha));
		notableEventDTO.setPrevious(this.getEventoPasado(date, fenomenosPPPFecha));
		notableEventDTO.setNext(this.getEventoProximo(date, fenomenosPPPFecha));

		return notableEventDTO;
	}

	/**
	 * EN: Locates the closest phenomenon of each kind on all three sides of the date, and
	 * computes the midsison. Every list is walked exactly once.
	 * ES: Localiza el fenómeno más cercano de cada tipo por los tres lados de la fecha, y
	 * calcula el midsison. Cada lista se recorre exactamente una vez.
	 *
	 * @param dateO                   EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: the twenty-one slots, three per kind of phenomenon. / ES: los veintiún huecos, tres por cada tipo de fenómeno.
	 */
	private LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO getFenomenosPPPFecha(LocalDate dateO, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosParaEventosDTO = new LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO();

		Vecindad<ApogeosYPerigeosLunaEntity> apoperis = Vecindad.de(datosCosmicosParaVAUDTO.getApoperis(), ApogeosYPerigeosLunaEntity::getDate, dateO);
		Vecindad<LunasEntity> lunas = Vecindad.de(datosCosmicosParaVAUDTO.getLunas(), LunasEntity::getDate, dateO);
		Vecindad<SolsticiosYEquinocciosEntity> soes = Vecindad.de(datosCosmicosParaVAUDTO.getSoes(), SolsticiosYEquinocciosEntity::getDate, dateO);
		Vecindad<EclipsesEntity> eclipses = Vecindad.de(datosCosmicosParaVAUDTO.getEclipses(), EclipsesEntity::getDate, dateO);
		Vecindad<EclipenosEntity> eclipenos = Vecindad.de(datosCosmicosParaVAUDTO.getEclipenos(), EclipenosEntity::getDate, dateO);

		// El metono fasal y el apoperico comparten la fecha del soe: ante un empate en
		// distancia el comportamiento historico es quedarse con el ultimo de la lista.
		Vecindad<MetonsEntity> metons = Vecindad.deUltimoEnEmpate(datosCosmicosParaVAUDTO.getMetons(), MetonsEntity::getDate, dateO);

		fenomenosParaEventosDTO.setLunaActual(lunas.getActual());
		fenomenosParaEventosDTO.setLunaAnterior(lunas.getAnterior());
		fenomenosParaEventosDTO.setLunaProxima(lunas.getProximo());

		fenomenosParaEventosDTO.setApoperiActual(apoperis.getActual());
		fenomenosParaEventosDTO.setApoperiAnterior(apoperis.getAnterior());
		fenomenosParaEventosDTO.setApoperiProximo(apoperis.getProximo());

		fenomenosParaEventosDTO.setSoeActual(soes.getActual());
		fenomenosParaEventosDTO.setSoeAnterior(soes.getAnterior());
		fenomenosParaEventosDTO.setSoeProximo(soes.getProximo());

		fenomenosParaEventosDTO.setMetonoActual(metons.getActual());
		fenomenosParaEventosDTO.setMetonoAnterior(metons.getAnterior());
		fenomenosParaEventosDTO.setMetonoProximo(metons.getProximo());

		fenomenosParaEventosDTO.setEclipseActual(eclipses.getActual());
		fenomenosParaEventosDTO.setEclipseAnterior(eclipses.getAnterior());
		fenomenosParaEventosDTO.setEclipseProximo(eclipses.getProximo());

		fenomenosParaEventosDTO.setEclipenoActual(eclipenos.getActual());
		fenomenosParaEventosDTO.setEclipenoAnterior(eclipenos.getAnterior());
		fenomenosParaEventosDTO.setEclipenoProximo(eclipenos.getProximo());

		this.rellenarMidsison(dateO, fenomenosParaEventosDTO, soes, lunas, apoperis, eclipses);

		return fenomenosParaEventosDTO;
	}

	/**
	 * EN: The midsison is the instant equidistant between the previous solstice or equinox and
	 * the next one. It is computed once and dropped into whichever slot (past, current or
	 * future) it belongs to with respect to the date being consulted.
	 * ES: El midsison es el instante equidistante entre el soe anterior y el siguiente.
	 * Se calcula una sola vez y se coloca en la ranura (pasado, actual o futuro) que
	 * le corresponda respecto a la fecha consultada.
	 *
	 * @param dateO    EN: date being consulted. / ES: fecha que se consulta.
	 * @param destino  EN: DTO the midsison is written into. / ES: DTO en el que se escribe el midsison.
	 * @param soes     EN: neighbourhood of solstices and equinoxes. / ES: vecindad de solsticios y equinoccios.
	 * @param lunas    EN: neighbourhood of moon phases. / ES: vecindad de fases lunares.
	 * @param apoperis EN: neighbourhood of apogees and perigees. / ES: vecindad de apogeos y perigeos.
	 * @param eclipses EN: neighbourhood of eclipses. / ES: vecindad de eclipses.
	 */
	private void rellenarMidsison(LocalDate dateO,
			LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO destino,
			Vecindad<SolsticiosYEquinocciosEntity> soes,
			Vecindad<LunasEntity> lunas,
			Vecindad<ApogeosYPerigeosLunaEntity> apoperis,
			Vecindad<EclipsesEntity> eclipses) {

		SolsticiosYEquinocciosEntity soePasado = soes.getAnterior();
		SolsticiosYEquinocciosEntity soeFuturo = soes.getProximo();

		if (soePasado == null || soeFuturo == null) {
			// Sin soe a un lado y a otro no hay estacion que partir por la mitad.
			return;
		}

		LocalDateTime diaDelMidsison = soePasado.getDate()
				.plusSeconds(ChronoUnit.SECONDS.between(soePasado.getDate(), soeFuturo.getDate()) / 2);

		MidsisonDTO midsison = this.construirMidsison(diaDelMidsison, soePasado, lunas, apoperis, eclipses);

		LocalDate diaNatural = diaDelMidsison.toLocalDate();

		if (diaNatural.isBefore(dateO)) {
			destino.setMidsisonAnterior(midsison);
		}
		else if (diaNatural.isEqual(dateO)) {
			destino.setMidsisonActual(midsison);
		}
		else {
			destino.setMidsisonProximo(midsison);
		}
	}

	/**
	 * EN: Builds the midsison and marks the moon phase, the apogee or perigee and the eclipse
	 * that coincide with it. A midsison matching both a moon phase and an apogee or perigee is
	 * apofasal.
	 * ES: Construye el midsison y marca la fase lunar, el apogeo o perigeo y el eclipse que
	 * coinciden con él. Un midsison que coincide a la vez con una fase lunar y con un apogeo o
	 * perigeo es apofasal.
	 *
	 * @param diaDelMidsison EN: instant of the midsison. / ES: instante del midsison.
	 * @param soePasado      EN: previous solstice, which lends it its season. / ES: solsticio anterior, que le presta su estación.
	 * @param lunas          EN: neighbourhood of moon phases. / ES: vecindad de fases lunares.
	 * @param apoperis       EN: neighbourhood of apogees and perigees. / ES: vecindad de apogeos y perigeos.
	 * @param eclipses       EN: neighbourhood of eclipses. / ES: vecindad de eclipses.
	 * @return EN: the midsison, fully described. / ES: el midsison, completamente descrito.
	 */
	private MidsisonDTO construirMidsison(LocalDateTime diaDelMidsison,
			SolsticiosYEquinocciosEntity soePasado,
			Vecindad<LunasEntity> lunas,
			Vecindad<ApogeosYPerigeosLunaEntity> apoperis,
			Vecindad<EclipsesEntity> eclipses) {

		MidsisonDTO midsison = new MidsisonDTO();
		midsison.setDate(diaDelMidsison);
		midsison.setLastSoeSeason(soePasado.getStartingSeason());

		LunasEntity luna = this.primeroEnTolerancia(diaDelMidsison, lunas.getAnterior(), lunas.getActual(), lunas.getProximo(), LunasEntity::getDate);

		if (luna != null) {
			midsison.setNuevo(luna.isNueva());
			midsison.setLleno(luna.isLlena());
			midsison.setSelecto(luna.isSelecta());
			midsison.setInvertido(luna.isInvertida());
		}

		ApogeosYPerigeosLunaEntity apoperi = this.primeroEnTolerancia(diaDelMidsison, apoperis.getAnterior(), apoperis.getActual(), apoperis.getProximo(), ApogeosYPerigeosLunaEntity::getDate);

		if (apoperi != null) {
			midsison.setAporico(apoperi.isEsApogeo());
			midsison.setPerico(apoperi.isEsPerigeo());
			midsison.setSelecto(apoperi.isEsSelecto());
			midsison.setInvertido(apoperi.isEsInvertido());
		}

		if (luna != null && apoperi != null) {
			midsison.setApofasal(true);
		}

		if (this.primeroEnTolerancia(diaDelMidsison, eclipses.getAnterior(), eclipses.getActual(), eclipses.getProximo(), EclipsesEntity::getDate) != null) {
			midsison.setEclipse(true);
		}

		return midsison;
	}

	/**
	 * EN: Returns the first of the three candidates (previous, current, next) falling within
	 * one sidereal day of the midsison, keeping that order of preference.
	 * ES: Devuelve el primero de los tres candidatos (anterior, actual, próximo) que caiga
	 * dentro de un día sideral del midsison, respetando ese orden de preferencia.
	 *
	 * @param referencia EN: instant of the midsison. / ES: instante del midsison.
	 * @param anterior   EN: previous candidate. / ES: candidato anterior.
	 * @param actual     EN: candidate falling on the same day. / ES: candidato del mismo día.
	 * @param proximo    EN: next candidate. / ES: candidato siguiente.
	 * @param fecha      EN: how to read the date of a candidate. / ES: cómo obtener la fecha de un candidato.
	 * @return EN: the chosen candidate, or {@code null} if none is close enough. / ES: el candidato elegido, o {@code null} si ninguno está lo bastante cerca.
	 */
	private <T> T primeroEnTolerancia(LocalDateTime referencia, T anterior, T actual, T proximo, Function<T, LocalDateTime> fecha) {

		if (this.dentroDeTolerancia(referencia, anterior, fecha)) {
			return anterior;
		}

		if (this.dentroDeTolerancia(referencia, actual, fecha)) {
			return actual;
		}

		if (this.dentroDeTolerancia(referencia, proximo, fecha)) {
			return proximo;
		}

		return null;
	}

	/**
	 * EN: Whether a candidate falls within one sidereal day of the reference instant.
	 * ES: Si un candidato cae dentro de un día sideral del instante de referencia.
	 *
	 * @param referencia EN: instant to measure against. / ES: instante contra el que se mide.
	 * @param candidato  EN: candidate; may be {@code null}. / ES: candidato; admite {@code null}.
	 * @param fecha      EN: how to read the date of the candidate. / ES: cómo obtener la fecha del candidato.
	 * @return EN: {@code true} if it is within tolerance. / ES: {@code true} si está dentro de la tolerancia.
	 */
	private <T> boolean dentroDeTolerancia(LocalDateTime referencia, T candidato, Function<T, LocalDateTime> fecha) {

		if (candidato == null) {
			return false;
		}

		LocalDateTime instante = fecha.apply(candidato);

		return instante != null && Math.abs(ChronoUnit.SECONDS.between(instante, referencia)) <= TOLERANCIA_EN_SEGUNDOS;
	}

	/**
	 * EN: Event falling on the date itself, if any.
	 * ES: Evento que cae en la propia fecha, si lo hay.
	 *
	 * @param fenomenos EN: phenomena around the date. / ES: fenómenos alrededor de la fecha.
	 * @return EN: name of the event, or an empty string. / ES: nombre del evento, o cadena vacía.
	 */
	private String getEventoActual(LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenos) {

		return this.getNotableEventName(fenomenos.getLunaActual(), fenomenos.getSoeActual(), fenomenos.getMetonoActual(),
				fenomenos.getEclipseActual(), fenomenos.getEclipenoActual(), fenomenos.getApoperiActual(),
				fenomenos.getMidsisonActual());
	}

	/**
	 * EN: Closest event before the date, together with how many days ago it happened. Among
	 * phenomena tied at the same distance, the most exceptional one wins.
	 * ES: Evento más cercano anterior a la fecha, junto con hace cuántos días ocurrió. Entre
	 * fenómenos empatados a la misma distancia gana el más excepcional.
	 *
	 * @param dateO     EN: date being consulted. / ES: fecha que se consulta.
	 * @param fenomenos EN: phenomena around the date. / ES: fenómenos alrededor de la fecha.
	 * @return EN: the formatted text, or an empty string if there is nothing before. / ES: el texto formateado, o cadena vacía si no hay nada antes.
	 */
	private String getEventoPasado(LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenos) {

		long diasEntreLunaYDate = this.diasEntre(fenomenos.getLunaAnterior() == null ? null : fenomenos.getLunaAnterior().getDate(), dateO, true);
		long diasEntreSOEYDate = this.diasEntre(fenomenos.getSoeAnterior() == null ? null : fenomenos.getSoeAnterior().getDate(), dateO, true);
		long diasEntreMidsisonYDate = this.diasEntre(fenomenos.getMidsisonAnterior() == null ? null : fenomenos.getMidsisonAnterior().getDate(), dateO, true);
		long diasEntreMetonYDate = this.diasEntre(fenomenos.getMetonoAnterior() == null ? null : fenomenos.getMetonoAnterior().getDate(), dateO, true);
		long diasEntreEclipseYDate = this.diasEntre(fenomenos.getEclipseAnterior() == null ? null : fenomenos.getEclipseAnterior().getDate(), dateO, true);
		long diasEntreEclipenoYDate = this.diasEntre(fenomenos.getEclipenoAnterior() == null ? null : fenomenos.getEclipenoAnterior().getDate(), dateO, true);
		long diasEntreApoperiYDate = this.diasEntre(fenomenos.getApoperiAnterior() == null ? null : fenomenos.getApoperiAnterior().getDate(), dateO, true);

		long minDias = this.minimo(diasEntreMidsisonYDate, diasEntreApoperiYDate, diasEntreLunaYDate, diasEntreSOEYDate,
				diasEntreMetonYDate, diasEntreEclipseYDate, diasEntreEclipenoYDate);

		if (minDias == Long.MAX_VALUE) {
			return "";
		}

		String nombreDelEvento = this.getNotableEventName(
				diasEntreLunaYDate == minDias ? fenomenos.getLunaAnterior() : null,
				diasEntreSOEYDate == minDias ? fenomenos.getSoeAnterior() : null,
				diasEntreMetonYDate == minDias ? fenomenos.getMetonoAnterior() : null,
				diasEntreEclipseYDate == minDias ? fenomenos.getEclipseAnterior() : null,
				diasEntreEclipenoYDate == minDias ? fenomenos.getEclipenoAnterior() : null,
				diasEntreApoperiYDate == minDias ? fenomenos.getApoperiAnterior() : null,
				diasEntreMidsisonYDate == minDias ? fenomenos.getMidsisonAnterior() : null);

		return nombreDelEvento + " hace " + minDias + (minDias == 1 ? " día" : " días");
	}

	/**
	 * EN: Closest event after the date, together with how many days remain.
	 * ES: Evento más cercano posterior a la fecha, junto con cuántos días faltan.
	 *
	 * @param dateO     EN: date being consulted. / ES: fecha que se consulta.
	 * @param fenomenos EN: phenomena around the date. / ES: fenómenos alrededor de la fecha.
	 * @return EN: the formatted text, or an empty string if there is nothing after. / ES: el texto formateado, o cadena vacía si no hay nada después.
	 */
	private String getEventoProximo(LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenos) {

		long diasEntreLunaYDate = this.diasEntre(fenomenos.getLunaProxima() == null ? null : fenomenos.getLunaProxima().getDate(), dateO, false);
		long diasEntreSOEYDate = this.diasEntre(fenomenos.getSoeProximo() == null ? null : fenomenos.getSoeProximo().getDate(), dateO, false);
		long diasEntreMidsisonYDate = this.diasEntre(fenomenos.getMidsisonProximo() == null ? null : fenomenos.getMidsisonProximo().getDate(), dateO, false);
		long diasEntreMetonYDate = this.diasEntre(fenomenos.getMetonoProximo() == null ? null : fenomenos.getMetonoProximo().getDate(), dateO, false);
		long diasEntreEclipseYDate = this.diasEntre(fenomenos.getEclipseProximo() == null ? null : fenomenos.getEclipseProximo().getDate(), dateO, false);
		long diasEntreEclipenoYDate = this.diasEntre(fenomenos.getEclipenoProximo() == null ? null : fenomenos.getEclipenoProximo().getDate(), dateO, false);
		long diasEntreApoperiYDate = this.diasEntre(fenomenos.getApoperiProximo() == null ? null : fenomenos.getApoperiProximo().getDate(), dateO, false);

		long minDias = this.minimo(diasEntreMidsisonYDate, diasEntreApoperiYDate, diasEntreLunaYDate, diasEntreSOEYDate,
				diasEntreMetonYDate, diasEntreEclipseYDate, diasEntreEclipenoYDate);

		if (minDias == Long.MAX_VALUE) {
			return "";
		}

		String nombreDelEvento = this.getNotableEventName(
				diasEntreLunaYDate == minDias ? fenomenos.getLunaProxima() : null,
				diasEntreSOEYDate == minDias ? fenomenos.getSoeProximo() : null,
				diasEntreMetonYDate == minDias ? fenomenos.getMetonoProximo() : null,
				diasEntreEclipseYDate == minDias ? fenomenos.getEclipseProximo() : null,
				diasEntreEclipenoYDate == minDias ? fenomenos.getEclipenoProximo() : null,
				diasEntreApoperiYDate == minDias ? fenomenos.getApoperiProximo() : null,
				diasEntreMidsisonYDate == minDias ? fenomenos.getMidsisonProximo() : null);

		return nombreDelEvento + " dentro de " + minDias + (minDias == 1 ? " día" : " días");
	}

	/**
	 * EN: Distance in whole days between an instant and the reference date, in the requested
	 * direction. Returns {@code Long.MAX_VALUE} when there is no instant, so it never wins a
	 * minimum.
	 * ES: Distancia en días naturales entre un instante y la fecha de referencia, en el sentido
	 * pedido. Devuelve {@code Long.MAX_VALUE} cuando no hay instante, de modo que nunca gana un
	 * mínimo.
	 *
	 * @param instante   EN: instant to measure; may be {@code null}. / ES: instante que se mide; admite {@code null}.
	 * @param referencia EN: reference date. / ES: fecha de referencia.
	 * @param haciaAtras EN: {@code true} to measure towards the past. / ES: {@code true} para medir hacia el pasado.
	 * @return EN: distance in days. / ES: distancia en días.
	 */
	private long diasEntre(LocalDateTime instante, LocalDate referencia, boolean haciaAtras) {

		if (instante == null) {
			return Long.MAX_VALUE;
		}

		LocalDate dia = instante.toLocalDate();

		return haciaAtras ? ChronoUnit.DAYS.between(dia, referencia) : ChronoUnit.DAYS.between(referencia, dia);
	}

	/**
	 * EN: Smallest of the given values.
	 * ES: El menor de los valores dados.
	 *
	 * @param valores EN: values to compare. / ES: valores a comparar.
	 * @return EN: the minimum, or {@code Long.MAX_VALUE} if there are none. / ES: el mínimo, o {@code Long.MAX_VALUE} si no hay ninguno.
	 */
	private long minimo(long... valores) {

		long minimo = Long.MAX_VALUE;

		for (long valor : valores) {

			if (valor < minimo) {
				minimo = valor;
			}
		}

		return minimo;
	}

	/**
	 * EN: Names the event, choosing among the phenomena that tie at the same distance. The
	 * order is from rarest to most frequent: eclipeno, meton, solstice or equinox, eclipse,
	 * midsison, moon phase and finally apogee or perigee.
	 * ES: Pone nombre al evento, eligiendo entre los fenómenos que empatan a la misma distancia.
	 * El orden va del más excepcional al más frecuente: eclípeno, métono, solsticio o
	 * equinoccio, eclipse, midsison, fase lunar y por último apogeo o perigeo.
	 *
	 * @param luna     EN: candidate moon phase, or {@code null}. / ES: fase lunar candidata, o {@code null}.
	 * @param soe      EN: candidate solstice or equinox, or {@code null}. / ES: solsticio o equinoccio candidato, o {@code null}.
	 * @param meton    EN: candidate meton, or {@code null}. / ES: métono candidato, o {@code null}.
	 * @param eclipse  EN: candidate eclipse, or {@code null}. / ES: eclipse candidato, o {@code null}.
	 * @param eclipeno EN: candidate eclipeno, or {@code null}. / ES: eclípeno candidato, o {@code null}.
	 * @param apoperi  EN: candidate apogee or perigee, or {@code null}. / ES: apogeo o perigeo candidato, o {@code null}.
	 * @param midsison EN: candidate midsison, or {@code null}. / ES: midsison candidato, o {@code null}.
	 * @return EN: the name of the event, or an empty string. / ES: el nombre del evento, o cadena vacía.
	 */
	private String getNotableEventName(LunasEntity luna, SolsticiosYEquinocciosEntity soe, MetonsEntity meton, EclipsesEntity eclipse, EclipenosEntity eclipeno, ApogeosYPerigeosLunaEntity apoperi, MidsisonDTO midsison) {

		String evento = "";

		if(eclipeno != null) {

			evento = this.getEclipenoName(eclipeno);
		}
		else if (meton != null) {

			evento = this.getMetonoName(meton);
		}
		else if(soe != null) {

			evento = this.getSoeName(soe);
		}
		else if (eclipse != null) {

			evento = this.getEclipseName(eclipse);
		}
		else if(midsison != null) {

			evento = this.getMidsisonName(midsison);
		}
		else if (luna != null) {

			evento = this.getLunaName(luna);
		}
		else if (apoperi != null) {

			evento = this.getApoperiName(apoperi);
		}

		return evento;
	}

	/**
	 * EN: Name of an eclipeno: its season, then whether it is apofasal, and finally the
	 * qualifier given by the moon phase and the lunar distance.
	 * ES: Nombre de un eclípeno: su estación, después si es apofasal, y por último el apellido
	 * que le dan la fase lunar y la distancia lunar.
	 *
	 * @param eclipeno EN: eclipeno to name. / ES: eclípeno al que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getEclipenoName(EclipenosEntity eclipeno) {

		StringBuilder name = new StringBuilder();

		if (eclipeno.isInvernal()) {
			name.append("Eclípeno invernal ");
		}
		else if(eclipeno.isPrimaveral()) {
			name.append("Eclípeno primaveral ");
		}
		else if (eclipeno.isEstival()) {
			name.append("Eclípeno estival ");
		}
		else if (eclipeno.isOtonyal()) {
			name.append("Eclípeno otoñal ");
		}

		if(eclipeno.isApofasal()) {

			name.append("apofasal ");

			if(eclipeno.isSelecto() && eclipeno.isNuevo()) {
				name.append("remoto");
			}
			else if(eclipeno.isSelecto() && eclipeno.isLleno()) {
				name.append("brillante");
			}
			else if(eclipeno.isInvertido() && eclipeno.isNuevo()) {
				name.append("velado");
			}
			else if(eclipeno.isInvertido() && eclipeno.isLleno()) {
				name.append("tenue");
			}
		}
		else {

			if(eclipeno.isNuevo()) {
				name.append(" nuevo");
			}
			else if(eclipeno.isLleno()) {
				name.append(" lleno");
			}

			if(eclipeno.isSelecto()) {
				name.append(" selecto");
			}
			else if(eclipeno.isInvertido()) {
				name.append(" invertido");
			}
		}

		return name.toString();
	}

	/**
	 * EN: Name of a meton, built the same way as an eclipeno.
	 * ES: Nombre de un métono, construido igual que el de un eclípeno.
	 *
	 * @param meton EN: meton to name. / ES: métono al que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getMetonoName(MetonsEntity meton) {

		StringBuilder name = new StringBuilder();

		if (meton.isInvernal()) {
			name.append("Métono invernal");
		}
		else if(meton.isPrimaveral()) {
			name.append("Métono primaveral");
		}
		else if (meton.isEstival()) {
			name.append("Métono estival");
		}
		else if (meton.isOtonyal()) {
			name.append("Métono otoñal");
		}

		if(meton.isSelecto()) {

			if(meton.isApofasal()) {

				name.append(" apofasal");

				if((meton.isFasal() && meton.isNuevo() || (meton.isApoperico() && meton.isAporico()))){
					name.append(" remoto");
				}
				else if((meton.isFasal() && meton.isLleno() || meton.isApoperico() && meton.isPerico())){
					name.append(" brillante");
				}
			}
			else {

				name.append(this.getSufijoFaseMetono(meton));
				name.append(" selecto");
			}
		}
		else if (meton.isInvertido()) {

			if(meton.isApofasal()) {

				name.append(" apofasal");

				if((meton.isFasal() && meton.isNuevo() || (meton.isApoperico() && meton.isPerico()))){
					name.append(" velado");
				}
				else if((meton.isFasal() && meton.isLleno() || meton.isApoperico() && meton.isAporico())){
					name.append(" tenue");
				}
			}
			else {

				name.append(this.getSufijoFaseMetono(meton));
				name.append(" invertido");
			}
		}
		else {

			if(meton.isFasal()) {

				if(meton.isNuevo()) {
					name.append(" nuevo");
				}
				else if(meton.isLleno()) {
					name.append(" lleno");
				}
			}
			else if (meton.isApoperico()) {

				if(meton.isAporico()) {
					name.append(" apórico");
				}
				else if(meton.isPerico()) {
					name.append(" périco");
				}
			}
		}

		return name.toString();
	}

	/**
	 * EN: Phase suffix of a meton: new, full, aporic or peric, depending on whether it is fasal
	 * or apoperico.
	 * ES: Sufijo de fase de un métono: nuevo, lleno, apórico o périco, según sea fasal o
	 * apopérico.
	 *
	 * @param meton EN: meton to describe. / ES: métono que se describe.
	 * @return EN: the suffix, or an empty string. / ES: el sufijo, o cadena vacía.
	 */
	private String getSufijoFaseMetono(MetonsEntity meton) {

		if(meton.isFasal() && meton.isNuevo()){
			return " nuevo";
		}

		if(meton.isFasal() && meton.isLleno()){
			return " lleno";
		}

		if(meton.isApoperico() && meton.isAporico()){
			return " apórico";
		}

		if(meton.isApoperico() && meton.isPerico()) {
			return " périco";
		}

		return "";
	}

	/**
	 * EN: Name of a solstice or equinox.
	 * ES: Nombre de un solsticio o equinoccio.
	 *
	 * @param soe EN: phenomenon to name. / ES: fenómeno al que se pone nombre.
	 * @return EN: its name, or an empty string. / ES: su nombre, o cadena vacía.
	 */
	private String getSoeName(SolsticiosYEquinocciosEntity soe) {

		if(soe.isSolsticioInvierno()) {
			return "Solsticio de invierno";
		}

		if(soe.isEquinoccioPrimavera()) {
			return "Equinoccio de primavera";
		}

		if(soe.isSolsticioVerano()) {
			return "Solsticio de verano";
		}

		if (soe.isEquinoccioOtonyo()) {
			return "Equinoccio de otoño";
		}

		return "";
	}

	/**
	 * EN: Name of an eclipse: solar or lunar, plus its type.
	 * ES: Nombre de un eclipse: solar o lunar, más su tipo.
	 *
	 * @param eclipse EN: eclipse to name. / ES: eclipse al que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getEclipseName (EclipsesEntity eclipse) {

		String name = "";

		if(eclipse.isDeLuna()) {
			name = "Eclipse de luna";
		}
		else if (eclipse.isDeSol()) {
			name = "Eclipse de sol";
		}

		String fase = "";

		if(eclipse.isEsAnular()) {
			fase = " anular";
		}
		else if (eclipse.isEsHibrido()) {
			fase = " híbrido";
		}
		else if (eclipse.isEsParcial()) {
			fase = " parcial";
		}
		else if (eclipse.isEsPenumbral()) {
			fase = " penumbral";
		}
		else if (eclipse.isEsTotal()) {
			fase = " total";
		}

		return name + fase;
	}

	/**
	 * EN: Name of a midsison: the season it closes, its lunar qualifier and, if there is one,
	 * the eclipse.
	 * ES: Nombre de un midsison: la estación que cierra, su apellido lunar y, si lo hay, el
	 * eclipse.
	 *
	 * @param midsison EN: midsison to name. / ES: midsison al que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getMidsisonName(MidsisonDTO midsison) {

		String midsisonApellido = "";
		String midsisonLunar = "";

		switch(midsison.getLastSoeSeason()) {

			case 1:
				midsisonApellido = " invernal";
				break;

			case 2:
				midsisonApellido = " primaveral";
				break;

			case 3:
				midsisonApellido = " estival";
				break;

			case 4:
				midsisonApellido = " otoñal";
				break;
		}

		if(midsison.isApofasal()) {

			midsisonLunar = " apofasal";

			if (midsison.isNuevo() && midsison.isAporico()) {
				midsisonLunar = midsisonLunar + " remoto";
			}
			else if(midsison.isNuevo() && midsison.isPerico()) {
				midsisonLunar = midsisonLunar + " velado";
			}
			else if(midsison.isLleno() && midsison.isAporico()) {
				midsisonLunar = midsisonLunar + " tenue";
			}
			else if(midsison.isLleno() && midsison.isPerico()) {
				midsisonLunar = midsisonLunar + " brillante";
			}
		}
		else {

			if (midsison.isNuevo()) {
				midsisonLunar = " nuevo";
			}
			else if (midsison.isLleno()) {
				midsisonLunar = " lleno";
			}
			else if (midsison.isAporico()) {
				midsisonLunar = " apórico";
			}
			else if (midsison.isPerico()) {
				midsisonLunar = " périco";
			}

			if(midsison.isSelecto()) {
				midsisonLunar = midsisonLunar + " selecto";
			}
			else if(midsison.isInvertido()) {
				midsisonLunar = midsisonLunar + " invertido";
			}
		}

		if(midsison.isEclipse()) {
			midsisonLunar = midsisonLunar + " eclipsado";
		}

		return "Midsison" + midsisonApellido + midsisonLunar;
	}

	/**
	 * EN: Name of a moon phase. A new moon at apogee has a name of its own, the aponoval moon.
	 * ES: Nombre de una fase lunar. Una luna nueva en apogeo tiene nombre propio, la luna
	 * aponoval.
	 *
	 * @param luna EN: phase to name. / ES: fase a la que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getLunaName(LunasEntity luna) {

		if(luna.isNueva() && luna.isSelecta()) {
			return "Luna aponoval";
		}

		String name = "";

		if (luna.isNueva()) {
			name = "Luna nueva";
		}
		else if (luna.isCuartoCreciente()) {
			name = "Luna cuarto creciente";
		}
		else if (luna.isLlena()) {
			name = "Luna llena";
		}
		else if (luna.isCuartoMenguante()) {
			name = "Luna cuarto menguante";
		}

		if(luna.isSelecta()) {
			name = name + " selecta";
		}
		else if(luna.isInvertida()) {
			name = name + " invertida";
		}

		return name;
	}

	/**
	 * EN: Name of an apogee or perigee: a distant moon at apogee, a present moon at perigee.
	 * ES: Nombre de un apogeo o perigeo: luna distante en el apogeo, luna presente en el
	 * perigeo.
	 *
	 * @param apoperi EN: phenomenon to name. / ES: fenómeno al que se pone nombre.
	 * @return EN: its full name. / ES: su nombre completo.
	 */
	private String getApoperiName(ApogeosYPerigeosLunaEntity apoperi) {

		String estado = "";
		String especial = "";

		if(apoperi.isEsApogeo()) {
			estado = "distante";
		}
		else if (apoperi.isEsPerigeo()) {
			estado = "presente";
		}

		if(apoperi.isEsSelecto()) {
			especial = " selecto";
		}
		else if(apoperi.isEsInvertido()) {
			especial = " invertido";
		}

		return "Luna " + estado + especial;
	}
}
