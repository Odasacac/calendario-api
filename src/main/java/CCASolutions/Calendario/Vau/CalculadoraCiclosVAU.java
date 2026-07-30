package CCASolutions.Calendario.Vau;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonIADTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MetonoInvernalApofasalRemotoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: The long VAU cycles: eclipenos and metonos.
 *
 *     All four calculations here follow the same shape: take the phenomena of a
 *     given kind between an anchor and the requested date, then use the most recent
 *     one and how many there are.
 *
 *     The old code built an ArrayList for each of them just to read element 0 and
 *     size(). Since the source lists are ordered newest first, element 0 is simply
 *     the first match found, so one pass that keeps a reference and a counter
 *     replaces the list. The pass can also stop as soon as the dates drop below the
 *     anchor, because from there on nothing can match.
 *
 * ES: Los ciclos VAU largos: eclipenos y metonos.
 *
 *     Los cuatro calculos de aqui tienen la misma forma: tomar los fenomenos de un
 *     tipo dado entre un ancla y la fecha consultada, y usar el mas reciente y cuantos
 *     hay.
 *
 *     El codigo antiguo construia un ArrayList para cada uno solo para leer el elemento
 *     0 y size(). Como las listas de origen estan ordenadas de la mas nueva a la mas
 *     antigua, el elemento 0 es simplemente la primera coincidencia encontrada, asi que
 *     una pasada que guarda una referencia y un contador sustituye a la lista. La pasada
 *     puede pararse ademas en cuanto las fechas caen por debajo del ancla, porque a
 *     partir de ahi nada puede coincidir.
 * ==============================================================================
 */
@Component
public class CalculadoraCiclosVAU {

	private static final Logger log = LoggerFactory.getLogger(CalculadoraCiclosVAU.class);

	private static final String APELLIDO_INVERTIDO = "(Invertido)";
	private static final String APELLIDO_SELECTO = "(Selecto)";

	// =========================================================================
	// EN: SELECTED ECLIPENO - how long ago the current era started.
	// ES: ECLIPENO SELECTO - cuanto hace que empezo la era actual.
	// =========================================================================

	public EclipenoSelectoDTO calcularEclipenoSelecto(ContextoCosmico contexto) {

		EclipenoSelectoDTO eclipenoSelecto = new EclipenoSelectoDTO();

		long diaDelEclipeno = Fechas.diaEpoch(contexto.getUltimoEclipenoInvernalApofasalRemoto().getDate());

		eclipenoSelecto.setDaysSinceCurrentEclipenoSelectoIN(
				"hace " + contexto.diasHastaLaFecha(diaDelEclipeno) + " días");
		eclipenoSelecto.setEclipenoINSelectoDay(diaDelEclipeno == contexto.getDiaEpoch());

		return eclipenoSelecto;
	}

	// =========================================================================
	// EN: ECLIPENO - winter new eclipenos since the last selected one.
	// ES: ECLIPENO - eclipenos invernales nuevos desde el ultimo selecto.
	// =========================================================================

	public EclipenoINDTO calcularEclipeno(ContextoCosmico contexto) {

		EclipenoINDTO eclipenoVAU = new EclipenoINDTO();

		final EclipenosEntity eclipenoSelecto = contexto.getUltimoEclipenoInvernalApofasalRemoto();
		final long diaDeLaFecha = contexto.getDiaEpoch();
		final long diaDelSelecto = Fechas.diaEpoch(eclipenoSelecto.getDate());

		/*
		 * EN: On the day of the selected eclipeno itself we are in no eclipeno yet.
		 * ES: El dia del propio eclipeno selecto todavia no estamos en ningun eclipeno.
		 */
		if (diaDelSelecto == diaDeLaFecha) {
			eclipenoVAU.setEclipenoINDay(true);
			eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(0);
			eclipenoVAU.setNumberOfEclipenoIN(0);
			eclipenoVAU.setYearOfCurrentEclipenoIN(eclipenoSelecto.getYear());
			return eclipenoVAU;
		}

		EclipenosEntity ultimoEclipenoIN = null;
		int eclipenosIN = 0;
		final LocalDateTime fechaDelSelecto = eclipenoSelecto.getDate();

		for (EclipenosEntity eclipeno : contexto.getEclipenos()) {

			// EN: Newest first: once we are before the anchor, nothing else can match.
			// ES: De mas nuevo a mas antiguo: pasado el ancla, ya nada puede coincidir.
			if (eclipeno.getDate().isBefore(fechaDelSelecto)) {
				break;
			}

			if (eclipeno.isInvernal() && eclipeno.isNuevo() && Fechas.diaEpoch(eclipeno.getDate()) <= diaDeLaFecha) {
				if (ultimoEclipenoIN == null) {
					ultimoEclipenoIN = eclipeno;
				}
				eclipenosIN++;
			}
		}

		if (ultimoEclipenoIN == null) {
			log.warn("No se ha encontrado ningún eclípeno invernal nuevo entre el eclípeno selecto y {}.",
					contexto.getFecha());
			return eclipenoVAU;
		}

		boolean esDiaDeEclipeno = Fechas.diaEpoch(ultimoEclipenoIN.getDate()) == diaDeLaFecha;
		eclipenoVAU.setYearOfCurrentEclipenoIN(ultimoEclipenoIN.getYear());
		eclipenoVAU.setEclipenoINDay(esDiaDeEclipeno);

		/*
		 * EN: One is subtracted because the count includes the eclipeno of the anchor
		 *     itself, and another one on the day of an eclipeno, because a cycle is not
		 *     added until its first day is over.
		 * ES: Se resta uno porque el conteo incluye el eclipeno del propio ancla, y otro
		 *     el dia de un eclipeno, porque no se suma un ciclo hasta que pasa su primer dia.
		 */
		int eclipenosDesdeElSelecto = eclipenosIN - 1;
		if (esDiaDeEclipeno) {
			eclipenosDesdeElSelecto--;
		}
		eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(eclipenosDesdeElSelecto);

		int numeroDeEclipeno = eclipenosDesdeElSelecto + 1;
		eclipenoVAU.setNumberOfEclipenoIN(numeroDeEclipeno);

		if (numeroDeEclipeno != 0 && !esDiaDeEclipeno && ultimoEclipenoIN.isApofasal()) {
			if (ultimoEclipenoIN.isInvertido()) {
				eclipenoVAU.setLastEclipenoSurname(APELLIDO_INVERTIDO);
			} else if (ultimoEclipenoIN.isSelecto()) {
				eclipenoVAU.setLastEclipenoSurname(APELLIDO_SELECTO);
			}
		}

		return eclipenoVAU;
	}

	// =========================================================================
	// EN: WINTER APOFASAL REMOTE METONO - the unit between eclipeno and metono.
	// ES: METONO INVERNAL APOFASAL REMOTO - la unidad entre eclipeno y metono.
	// =========================================================================

	public MetonoInvernalApofasalRemotoDTO calcularMetonoInvernalApofasalRemoto(ContextoCosmico contexto) {

		MetonoInvernalApofasalRemotoDTO metonoIAR = new MetonoInvernalApofasalRemotoDTO();

		final EclipenosEntity eclipenoSelecto = contexto.getUltimoEclipenoInvernalApofasalRemoto();
		final long diaDeLaFecha = contexto.getDiaEpoch();

		if (Fechas.diaEpoch(eclipenoSelecto.getDate()) == diaDeLaFecha) {
			metonoIAR.setMetonoInvernalApofasalRemotoDay(true);
			metonoIAR.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(0);
			metonoIAR.setNumberOfMetonoInvernalApofasalRemoto(0);
			metonoIAR.setYearOfCurrentMetonoInvernalApofasalRemoto(0);
			return metonoIAR;
		}

		MetonsEntity ultimoMetonoIAR = null;
		int metonosIAR = 0;
		final LocalDateTime fechaDelEclipeno = eclipenoSelecto.getDate();

		for (MetonsEntity metono : contexto.getMetonos()) {

			if (metono.getDate().isBefore(fechaDelEclipeno)) {
				break;
			}

			if (metono.isInvernal() && metono.isApofasal() && metono.isNuevo() && metono.isSelecto()
					&& Fechas.diaEpoch(metono.getDate()) <= diaDeLaFecha) {
				if (ultimoMetonoIAR == null) {
					ultimoMetonoIAR = metono;
				}
				metonosIAR++;
			}
		}

		if (ultimoMetonoIAR == null) {
			log.warn("No se ha encontrado ningún métono invernal apofasal remoto entre el eclípeno selecto y {}.",
					contexto.getFecha());
			return metonoIAR;
		}

		boolean esDiaDeMetono = Fechas.diaEpoch(ultimoMetonoIAR.getDate()) == diaDeLaFecha;
		metonoIAR.setYearOfCurrentMetonoInvernalApofasalRemoto(ultimoMetonoIAR.getYear());
		metonoIAR.setMetonoInvernalApofasalRemotoDay(esDiaDeMetono);

		int metonosDesdeElEclipeno = metonosIAR - 1;
		if (esDiaDeMetono) {
			metonosDesdeElEclipeno--;
		}
		metonoIAR.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(metonosDesdeElEclipeno);
		metonoIAR.setNumberOfMetonoInvernalApofasalRemoto(metonosDesdeElEclipeno + 1);

		return metonoIAR;
	}

	// =========================================================================
	// EN: METONO - winter new (fasal) and winter aporic metonos since the anchor.
	// ES: METONO - metonos invernales nuevos (fasales) y aporicos desde el ancla.
	// =========================================================================

	public MetonDTO calcularMetono(ContextoCosmico contexto) {

		MetonDTO metonoVAU = new MetonDTO();
		MetonINDTO metonoIN = new MetonINDTO();
		MetonIADTO metonoIA = new MetonIADTO();

		final MetonsEntity ancla = contexto.getUltimoMetonoIApofasalRemoto();
		final LocalDateTime fechaDelAncla = ancla.getDate();
		final long diaDeLaFecha = contexto.getDiaEpoch();
		final long diaDelAncla = Fechas.diaEpoch(fechaDelAncla);

		MetonsEntity ultimoNuevo = null;
		MetonsEntity ultimoAporico = null;
		int nuevos = 0;
		int aporicos = 0;

		for (MetonsEntity metono : contexto.getMetonos()) {

			if (metono.getDate().isBefore(fechaDelAncla)) {
				break;
			}

			if (!metono.isInvernal() || Fechas.diaEpoch(metono.getDate()) > diaDeLaFecha) {
				continue;
			}

			if (metono.isNuevo()) {
				if (ultimoNuevo == null) {
					ultimoNuevo = metono;
				}
				nuevos++;
			} else if (metono.isAporico()) {
				if (ultimoAporico == null) {
					ultimoAporico = metono;
				}
				aporicos++;
			}
		}

		if (ultimoNuevo == null || ultimoAporico == null) {
			log.warn("No se han encontrado métonos invernales nuevos y/o apóricos entre el métono ancla y {}.",
					contexto.getFecha());
			metonoVAU.setMetonsIN(metonoIN);
			metonoVAU.setMetonsIA(metonoIA);
			return metonoVAU;
		}

		boolean esDiaDeMetonoNuevo = Fechas.diaEpoch(ultimoNuevo.getDate()) == diaDeLaFecha;
		boolean esDiaDeMetonoAporico = Fechas.diaEpoch(ultimoAporico.getDate()) == diaDeLaFecha;
		boolean esDiaDelAncla = diaDelAncla == diaDeLaFecha;

		metonoIN.setYearOfCurrentMetonIN(ultimoNuevo.getYear());
		metonoIN.setMetonoINDay(esDiaDeMetonoNuevo);
		metonoIA.setYearOfCurrentMetonIA(ultimoAporico.getYear());
		metonoIA.setMetonoIADay(esDiaDeMetonoAporico);

		// EN: Same "minus the anchor, minus the day in progress" rule as the eclipenos.
		// ES: La misma regla de "menos el ancla, menos el dia en curso" que los eclipenos.
		int nuevosDesdeElAncla = nuevos - 1;
		if (esDiaDeMetonoNuevo && !esDiaDelAncla) {
			nuevosDesdeElAncla--;
		}
		metonoIN.setMetonosINSinceLastEclipenoIN(nuevosDesdeElAncla);

		int numeroDeMetonoNuevo = nuevosDesdeElAncla + 1;
		if (esDiaDelAncla) {
			numeroDeMetonoNuevo--;
		}
		metonoIN.setNumberOfMetonIN(numeroDeMetonoNuevo);

		int aporicosDesdeElAncla = aporicos - 1;
		if (esDiaDeMetonoAporico && !esDiaDelAncla) {
			aporicosDesdeElAncla--;
		}
		metonoIA.setMetonosIASinceLastEclipenoSelecto(aporicosDesdeElAncla);

		int numeroDeMetonoAporico = aporicosDesdeElAncla + 1;
		if (esDiaDelAncla) {
			numeroDeMetonoAporico--;
		}
		metonoIA.setNumberOfMetonIA(numeroDeMetonoAporico);

		if (numeroDeMetonoNuevo != 0 && !esDiaDeMetonoNuevo) {
			if (ultimoNuevo.isInvertido()) {
				metonoIN.setLastMetonSurname(APELLIDO_INVERTIDO);
			} else if (ultimoNuevo.isSelecto()) {
				metonoIN.setLastMetonSurname(APELLIDO_SELECTO);
			}
		}

		/*
		 * EN: Careful, this is intentional: the aporic metono's surname is gated on the
		 *     *new* metono's number, not on its own. That is what the original code did
		 *     and changing it would change the API response, so it is preserved.
		 * ES: Cuidado, esto es intencionado: el apellido del metono aporico depende del
		 *     numero del metono *nuevo*, no del suyo. Es lo que hacia el codigo original y
		 *     cambiarlo cambiaria la respuesta de la API, asi que se conserva.
		 */
		if (numeroDeMetonoNuevo != 0 && !esDiaDeMetonoAporico) {
			if (ultimoAporico.isInvertido()) {
				metonoIA.setLastMetonSurname(APELLIDO_INVERTIDO);
			} else if (ultimoAporico.isSelecto()) {
				metonoIA.setLastMetonSurname(APELLIDO_SELECTO);
			}
		}

		metonoVAU.setMetonsIN(metonoIN);
		metonoVAU.setMetonsIA(metonoIA);

		return metonoVAU;
	}
}
