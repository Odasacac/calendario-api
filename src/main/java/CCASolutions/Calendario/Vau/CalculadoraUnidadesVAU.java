package CCASolutions.Calendario.Vau;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Support.CatalogoCalendario;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: The short VAU units: year, month, week and day.
 *
 *     Same rules as before, but:
 *       - every "entity.getDate().toLocalDate()" inside a loop became one epoch-day
 *         long, read once per iteration instead of two or three times;
 *       - the month calculation no longer pre-splits the phase list into two new
 *         ArrayLists, and the pass over full moons was removed because none of its
 *         results were ever read;
 *       - the month, week and day names come from the in-memory catalog instead of
 *         one database round trip each.
 *
 * ES: Las unidades VAU cortas: ano, mes, semana y dia.
 *
 *     Las mismas reglas que antes, pero:
 *       - cada "entity.getDate().toLocalDate()" dentro de un bucle pasa a ser un long
 *         de dia epoch, leido una vez por iteracion en vez de dos o tres;
 *       - el calculo del mes ya no parte la lista de fases en dos ArrayList nuevos, y
 *         se ha eliminado la pasada sobre las lunas llenas porque ninguno de sus
 *         resultados se leia;
 *       - los nombres de mes, semana y dia vienen del catalogo en memoria en lugar de
 *         un viaje a la base de datos cada uno.
 * ==============================================================================
 */
@Component
public class CalculadoraUnidadesVAU {

	private static final Logger log = LoggerFactory.getLogger(CalculadoraUnidadesVAU.class);

	/*
	 * EN: Surnames a month part inherits from its new moon.
	 * ES: Apellidos que una parte de mes hereda de su luna nueva.
	 */
	private static final String APELLIDO_SELECTO = "selecto";
	private static final String APELLIDO_INVERTIDO = "invertido";

	private final CatalogoCalendario catalogo;

	public CalculadoraUnidadesVAU(CatalogoCalendario catalogo) {
		this.catalogo = catalogo;
	}

	// =========================================================================
	// EN: YEAR
	// ES: ANO
	// =========================================================================

	/*
	 * EN: The VAU year counts the winter solstices that passed between the anchoring
	 *     metono and the requested date. A date that falls exactly on a winter
	 *     solstice belongs to no VAU year.
	 * ES: El ano VAU cuenta los solsticios de invierno que han pasado desde el metono
	 *     ancla hasta la fecha consultada. Una fecha que cae exactamente en un solsticio
	 *     de invierno no corresponde a ningun ano VAU.
	 */
	public YearDTO calcularAnyo(ContextoCosmico contexto) {

		YearDTO anyoVAU = new YearDTO();

		final long diaDeLaFecha = contexto.getDiaEpoch();
		final long diaDelMetono = Fechas.diaEpoch(contexto.getUltimoMetonoIN().getDate());

		boolean caeEnSolsticioDeInvierno = false;
		int solsticios = 0;

		List<SolsticiosYEquinocciosEntity> soes = contexto.getSoes();
		for (int i = 0; i < soes.size() && !caeEnSolsticioDeInvierno; i++) {

			SolsticiosYEquinocciosEntity soe = soes.get(i);

			if (soe.isSolsticioInvierno()) {

				long diaDelSoe = Fechas.diaEpoch(soe.getDate());

				if (diaDelSoe == diaDeLaFecha) {
					caeEnSolsticioDeInvierno = true;
				} else if (diaDelSoe < diaDeLaFecha && diaDelSoe > diaDelMetono) {
					solsticios++;
				}
			}
		}

		anyoVAU.setEsSolsticioDeInvierno(caeEnSolsticioDeInvierno);
		anyoVAU.setSolsticiosDeInviernoSinceLastMetonIN(solsticios);

		/*
		 * EN: On the day of the anchoring eclipeno or metono the year has not started yet.
		 * ES: El dia del eclipeno o del metono ancla el ano todavia no ha empezado.
		 */
		int numeroDeAnyo = solsticios + 1;
		if (Fechas.diaEpoch(contexto.getUltimoEclipenoIN().getDate()) == diaDeLaFecha
				|| diaDelMetono == diaDeLaFecha) {
			numeroDeAnyo--;
		}
		anyoVAU.setNumberOfYear(numeroDeAnyo);

		return anyoVAU;
	}

	// =========================================================================
	// EN: MONTH
	// ES: MES
	// =========================================================================

	/*
	 * EN: Entry point for the month of the requested date.
	 * ES: Punto de entrada para el mes de la fecha consultada.
	 */
	public MonthDTO calcularMes(ContextoCosmico contexto) {
		return calcularMes(contexto.getFecha(), contexto.getSoes(), contexto.getLunas());
	}

	/*
	 * EN: A VAU month is delimited by the new moons inside a season, plus a "hybrid"
	 *     month around each solstice or equinox. The date is placed by finding the
	 *     surrounding solstice/equinox pair and counting the new moons in between.
	 *
	 *     When the date falls on a new moon the month is the *next* one, which the
	 *     original code resolved by asking for the month of the following day. That
	 *     recursion is kept, and it is now cheap: it walks two short in-memory lists
	 *     and hits no database at all, where before it repeated the whole scan plus
	 *     another month query.
	 *
	 * ES: Un mes VAU esta delimitado por las lunas nuevas dentro de una estacion, mas un
	 *     mes "hibrido" alrededor de cada solsticio o equinoccio. La fecha se situa
	 *     buscando la pareja de solsticio/equinoccio que la rodea y contando las lunas
	 *     nuevas intermedias.
	 *
	 *     Cuando la fecha cae en luna nueva el mes es el *siguiente*, algo que el codigo
	 *     original resolvia preguntando por el mes del dia siguiente. Se mantiene esa
	 *     recursion, y ahora es barata: recorre dos listas cortas en memoria y no toca la
	 *     base de datos, mientras que antes repetia todo el recorrido mas otra consulta
	 *     de mes.
	 */
	private MonthDTO calcularMes(LocalDate fecha, List<SolsticiosYEquinocciosEntity> soes, List<LunasEntity> lunas) {

		MonthDTO mes = new MonthDTO();
		final long diaDeLaFecha = fecha.toEpochDay();

		// ---------------------------------------------------------------------
		// EN: 1. The solstice/equinox before and after the date. Falling exactly on
		//     one of them already settles the month, so the loop stops there.
		// ES: 1. El solsticio/equinoccio anterior y el posterior a la fecha. Caer
		//     exactamente en uno de ellos ya decide el mes, asi que el bucle para ahi.
		// ---------------------------------------------------------------------
		SolsticiosYEquinocciosEntity soeAnterior = null;
		SolsticiosYEquinocciosEntity soeSiguiente = null;
		long diasHastaSoeAnterior = Long.MAX_VALUE;
		long diasHastaSoeSiguiente = Long.MAX_VALUE;
		boolean caeEnSoe = false;

		for (int i = 0; i < soes.size() && !caeEnSoe; i++) {

			SolsticiosYEquinocciosEntity soe = soes.get(i);
			long diaDelSoe = Fechas.diaEpoch(soe.getDate());

			if (diaDelSoe == diaDeLaFecha) {
				caeEnSoe = true;
				soeAnterior = soe;
				soeSiguiente = soe;
			} else if (diaDelSoe < diaDeLaFecha) {
				long distancia = diaDeLaFecha - diaDelSoe;
				if (distancia < diasHastaSoeAnterior) {
					diasHastaSoeAnterior = distancia;
					soeAnterior = soe;
				}
			} else {
				long distancia = diaDelSoe - diaDeLaFecha;
				if (distancia < diasHastaSoeSiguiente) {
					diasHastaSoeSiguiente = distancia;
					soeSiguiente = soe;
				}
			}
		}

		if (soeAnterior == null || soeSiguiente == null) {
			log.warn("No se han encontrado el solsticio/equinoccio anterior y/o el siguiente para {}.", fecha);
			return mes;
		}

		final long diaDelSoeAnterior = Fechas.diaEpoch(soeAnterior.getDate());
		final long diaDelSoeSiguiente = Fechas.diaEpoch(soeSiguiente.getDate());

		// ---------------------------------------------------------------------
		// EN: 2. One pass over the phases: the new moons of the current season, the
		//     nearest previous new moon (it lends its surname to the month) and
		//     whether the date itself is a new moon.
		// ES: 2. Una sola pasada por las fases: las lunas nuevas de la estacion actual,
		//     la luna nueva anterior mas cercana (que presta su apellido al mes) y si la
		//     propia fecha es luna nueva.
		// ---------------------------------------------------------------------
		long diaDePrimeraLunaNuevaDeLaEstacion = Long.MAX_VALUE;
		long diaDeUltimaLunaNuevaDeLaEstacion = Long.MIN_VALUE;
		int lunasNuevasPasadasDesdeSoeAnterior = 0;

		LunasEntity lunaNuevaAnteriorMasCercana = null;
		long diasHastaLunaNuevaAnterior = Long.MAX_VALUE;

		boolean caeEnLunaNueva = false;
		String apellidoDeLaLunaDelDia = "";

		for (LunasEntity luna : lunas) {

			if (!luna.isNueva()) {
				continue;
			}

			long diaDeLaLuna = Fechas.diaEpoch(luna.getDate());
			boolean dentroDeLaEstacion = false;

			if (diaDeLaLuna == diaDeLaFecha) {

				caeEnLunaNueva = true;
				dentroDeLaEstacion = true;

				if (luna.isSelecta()) {
					apellidoDeLaLunaDelDia = APELLIDO_SELECTO;
				} else if (luna.isInvertida()) {
					apellidoDeLaLunaDelDia = APELLIDO_INVERTIDO;
				}

			} else if (diaDeLaLuna >= diaDelSoeAnterior && diaDeLaLuna < diaDelSoeSiguiente) {
				dentroDeLaEstacion = true;
			}

			/*
			 * EN: The new moons of the season are only needed to find the first one after
			 *     the previous solstice, the last one before the next solstice and how
			 *     many of them the date has already left behind. Tracking those three
			 *     while walking avoids building an intermediate list.
			 * ES: Las lunas nuevas de la estacion solo se necesitan para hallar la primera
			 *     tras el solsticio anterior, la ultima antes del siguiente y cuantas ha
			 *     dejado atras la fecha. Llevar esos tres datos durante el recorrido evita
			 *     construir una lista intermedia.
			 */
			if (dentroDeLaEstacion) {
				if (diaDeLaLuna < diaDePrimeraLunaNuevaDeLaEstacion) {
					diaDePrimeraLunaNuevaDeLaEstacion = diaDeLaLuna;
				}
				if (diaDeLaLuna > diaDeUltimaLunaNuevaDeLaEstacion) {
					diaDeUltimaLunaNuevaDeLaEstacion = diaDeLaLuna;
				}
				if (diaDeLaFecha > diaDeLaLuna) {
					lunasNuevasPasadasDesdeSoeAnterior++;
				}
			}

			if (diaDeLaLuna < diaDeLaFecha) {
				long distancia = diaDeLaFecha - diaDeLaLuna;
				if (distancia < diasHastaLunaNuevaAnterior) {
					diasHastaLunaNuevaAnterior = distancia;
					lunaNuevaAnteriorMasCercana = luna;
				}
			}
		}

		// ---------------------------------------------------------------------
		// EN: 3. Which month the date belongs to.
		// ES: 3. A que mes pertenece la fecha.
		// ---------------------------------------------------------------------
		String nombreDelMes = null;

		if (caeEnSoe) {

			/*
			 * EN: On a solstice or equinox the date belongs to that solstice's hybrid
			 *     month - unless it is also a new moon, in which case the next month
			 *     already started.
			 * ES: En un solsticio o equinoccio la fecha pertenece al mes hibrido de ese
			 *     solsticio - salvo que sea tambien luna nueva, en cuyo caso el mes
			 *     siguiente ya ha empezado.
			 */
			nombreDelMes = caeEnLunaNueva
					? calcularMes(fecha.plusDays(1), soes, lunas).getName()
					: nombreDelMes(soeAnterior.getStartingSeason(), 0, false);

		} else if (diaDeUltimaLunaNuevaDeLaEstacion == Long.MIN_VALUE) {

			log.warn("No hay luna nueva entre el solsticio/equinoccio anterior y el siguiente para {}.", fecha);

		} else if (diaDeLaFecha > diaDeUltimaLunaNuevaDeLaEstacion && diaDeLaFecha < diaDelSoeSiguiente) {

			// EN: Between the last new moon and the next solstice: its hybrid month.
			// ES: Entre la ultima luna nueva y el siguiente solsticio: su mes hibrido.
			nombreDelMes = nombreDelMes(soeSiguiente.getStartingSeason(), 0, false);

		} else if (diaDeLaFecha < diaDePrimeraLunaNuevaDeLaEstacion && diaDeLaFecha > diaDelSoeAnterior) {

			/*
			 * EN: Between the previous solstice and the first new moon: its hybrid month.
			 *     After a winter solstice with no new moon yet it is "Oterno liminal"
			 *     instead, and "Prierno" if the date is itself a new moon.
			 * ES: Entre el solsticio anterior y la primera luna nueva: su mes hibrido.
			 *     Tras un solsticio de invierno en el que aun no ha habido luna nueva es
			 *     "Oterno liminal", y "Prierno" si la fecha es luna nueva.
			 */
			if (soeAnterior.isSolsticioInvierno()) {
				nombreDelMes = caeEnLunaNueva
						? nombreDelMes(soeAnterior.getStartingSeason(), lunasNuevasPasadasDesdeSoeAnterior + 1, false)
						: nombreDelMes(soeAnterior.getStartingSeason(), lunasNuevasPasadasDesdeSoeAnterior, true);
			} else {
				nombreDelMes = nombreDelMes(soeAnterior.getStartingSeason(), 0, false);
			}

		} else {

			/*
			 * EN: Normal case: the season is known and so is the number of new moons that
			 *     already passed. On a new moon the next month is reported.
			 * ES: Caso normal: se conoce la estacion y cuantas lunas nuevas han pasado. En
			 *     luna nueva se informa del mes siguiente.
			 */
			nombreDelMes = caeEnLunaNueva
					? calcularMes(fecha.plusDays(1), soes, lunas).getName()
					: nombreDelMes(soeAnterior.getStartingSeason(), lunasNuevasPasadasDesdeSoeAnterior, false);
		}

		// ---------------------------------------------------------------------
		// EN: 4. A month part takes a surname when its new moon is selected or inverted.
		// ES: 4. Una parte de mes toma apellido cuando su luna nueva es selecta o invertida.
		// ---------------------------------------------------------------------
		if (caeEnLunaNueva) {
			mes.setSurname(apellidoDeLaLunaDelDia);
		} else if (lunaNuevaAnteriorMasCercana != null) {
			if (lunaNuevaAnteriorMasCercana.isSelecta()) {
				mes.setSurname(APELLIDO_SELECTO);
			} else if (lunaNuevaAnteriorMasCercana.isInvertida()) {
				mes.setSurname(APELLIDO_INVERTIDO);
			}
		}

		mes.setNewMoon(caeEnLunaNueva);
		mes.setName(nombreDelMes);

		return mes;
	}

	/*
	 * EN: Month name from the catalog. A missing combination used to blow up with a
	 *     NullPointerException; now it is logged and the name is left empty.
	 * ES: Nombre del mes desde el catalogo. Una combinacion inexistente reventaba antes
	 *     con NullPointerException; ahora se registra y el nombre se deja vacio.
	 */
	private String nombreDelMes(int season, int monthOfSeason, boolean liminal) {
		MonthsEntity mes = this.catalogo.mes(season, monthOfSeason, liminal);
		if (mes == null) {
			log.warn("No existe el mes VAU (estación={}, mesDeLaEstación={}, liminal={}).", season, monthOfSeason,
					liminal);
			return null;
		}
		return mes.getName();
	}

	// =========================================================================
	// EN: WEEK AND DAY
	// ES: SEMANA Y DIA
	// =========================================================================

	/*
	 * EN: Weeks and days are counted from the most recent new moon. On a new moon
	 *     itself there is no week, only the day zero of the cycle.
	 * ES: Las semanas y los dias se cuentan desde la luna nueva mas reciente. En la
	 *     propia luna nueva no hay semana, solo el dia cero del ciclo.
	 */
	public VAUWeekAndDayDTO calcularSemanaYDia(ContextoCosmico contexto) {

		VAUWeekAndDayDTO semanaYDia = new VAUWeekAndDayDTO();

		final long diaDeLaFecha = contexto.getDiaEpoch();
		final List<LunasEntity> lunas = contexto.getLunas();

		long diasDesdeLaLunaNueva = Long.MAX_VALUE;
		boolean caeEnLunaNueva = false;

		for (int i = 0; i < lunas.size() && !caeEnLunaNueva; i++) {

			LunasEntity luna = lunas.get(i);
			if (!luna.isNueva()) {
				continue;
			}

			long diaDeLaLuna = Fechas.diaEpoch(luna.getDate());

			if (diaDeLaLuna == diaDeLaFecha) {
				caeEnLunaNueva = true;
				diasDesdeLaLunaNueva = 0;
			} else if (diaDeLaLuna < diaDeLaFecha) {
				long distancia = diaDeLaFecha - diaDeLaLuna;
				if (distancia < diasDesdeLaLunaNueva) {
					diasDesdeLaLunaNueva = distancia;
				}
			}
		}

		if (diasDesdeLaLunaNueva == Long.MAX_VALUE) {
			log.warn("No se ha encontrado ninguna luna nueva anterior a {}.", contexto.getFecha());
			return semanaYDia;
		}

		/*
		 * EN: Four seven-day weeks plus a fifth that absorbs the tail of the lunar
		 *     month. The day inside the fifth week keeps counting from day 21, exactly
		 *     as the original did.
		 * ES: Cuatro semanas de siete dias mas una quinta que absorbe la cola del mes
		 *     lunar. El dia dentro de la quinta semana sigue contando desde el dia 21,
		 *     exactamente como hacia el original.
		 */
		int semanaDelMes;
		long diaDeLaSemana;

		if (diasDesdeLaLunaNueva <= 7) {
			semanaDelMes = caeEnLunaNueva ? 0 : 1;
			diaDeLaSemana = diasDesdeLaLunaNueva;
		} else if (diasDesdeLaLunaNueva <= 14) {
			semanaDelMes = 2;
			diaDeLaSemana = diasDesdeLaLunaNueva - 7;
		} else if (diasDesdeLaLunaNueva <= 21) {
			semanaDelMes = 3;
			diaDeLaSemana = diasDesdeLaLunaNueva - 14;
		} else if (diasDesdeLaLunaNueva <= 28) {
			semanaDelMes = 4;
			diaDeLaSemana = diasDesdeLaLunaNueva - 21;
		} else {
			semanaDelMes = 5;
			diaDeLaSemana = diasDesdeLaLunaNueva - 21;
		}

		// EN: Week 0 means "on the new moon": the response carries no week name.
		// ES: La semana 0 significa "en la luna nueva": la respuesta no lleva nombre de semana.
		if (semanaDelMes > 0) {
			semanaYDia.setWeek(this.catalogo.nombreSemana(semanaDelMes));
		}
		semanaYDia.setDay(this.catalogo.nombreDia(diaDeLaSemana));

		return semanaYDia;
	}
}
