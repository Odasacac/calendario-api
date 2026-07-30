package CCASolutions.Calendario.Vau;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Support.CatalogoCalendario;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: Gathers the cosmic phenomena needed to convert one date.
 *
 *     This is where the old implementation spent most of its time. It used to:
 *
 *       - load all ~220 eclipenos and scan them twice, in full, with a
 *         ChronoUnit.DAYS.between() call per row;
 *       - load every metono between the anchoring eclipeno and the request;
 *       - load every lunar phase from one year before the anchoring metono, which
 *         can sit centuries back, up to a year after the request: about 29.000
 *         entities per request;
 *       - load every visible eclipse since the anchoring eclipeno: about 1.300 more.
 *
 *     Now:
 *
 *       - the eclipenos and metonos come from the in-memory catalog, and because
 *         they are stored newest first, the "last X before the date" searches stop
 *         at the first match instead of scanning everything;
 *       - the lunar and eclipse lists are bounded windows around the requested date.
 *
 *     Why the bounded windows are equivalent: every consumer of these two lists
 *     looks for a phenomenon *near* the requested date (the previous new moon, the
 *     next eclipse, the phases between two solstices, the full moon before the
 *     autumn equinox...). The only consumers that needed the long history were the
 *     aponovo counters and the absolute eclipse counters, and those are now exact
 *     COUNT queries in CalculadoraAponovos and CalculadoraEclipsesAbsolutos. The
 *     windows are also clamped to the old query bounds, so they are strict subsets
 *     of what the old code saw: nothing new can enter a "nearest" comparison.
 *
 * ES: Reune los fenomenos cosmicos necesarios para convertir una fecha.
 *
 *     Aqui es donde la implementacion antigua gastaba la mayor parte del tiempo. Antes:
 *
 *       - cargaba los ~220 eclipenos y los recorria dos veces, enteros, con una llamada
 *         a ChronoUnit.DAYS.between() por fila;
 *       - cargaba todos los metonos entre el eclipeno ancla y la peticion;
 *       - cargaba todas las fases lunares desde un ano antes del metono ancla, que puede
 *         estar siglos atras, hasta un ano despues de la peticion: unas 29.000 entidades
 *         por peticion;
 *       - cargaba todos los eclipses visibles desde el eclipeno ancla: unas 1.300 mas.
 *
 *     Ahora:
 *
 *       - los eclipenos y metonos vienen del catalogo en memoria, y como estan guardados
 *         del mas nuevo al mas antiguo, las busquedas de "el ultimo X antes de la fecha"
 *         paran en la primera coincidencia en vez de recorrerlo todo;
 *       - las listas de lunas y eclipses son ventanas acotadas alrededor de la fecha.
 *
 *     Por que las ventanas acotadas son equivalentes: todos los consumidores de esas dos
 *     listas buscan un fenomeno *cercano* a la fecha consultada (la luna nueva anterior,
 *     el siguiente eclipse, las fases entre dos solsticios, la luna llena anterior al
 *     equinoccio de otono...). Los unicos consumidores que necesitaban el historico largo
 *     eran los contadores de aponovos y de eclipses absolutos, y ahora son consultas
 *     COUNT exactas en CalculadoraAponovos y CalculadoraEclipsesAbsolutos. Ademas las
 *     ventanas se recortan a los limites de las consultas antiguas, asi que son
 *     subconjuntos estrictos de lo que veia el codigo antiguo: nada nuevo puede entrar en
 *     una comparacion de "el mas cercano".
 * ==============================================================================
 */
@Component
public class CargadorDatosCosmicos {

	private static final Logger log = LoggerFactory.getLogger(CargadorDatosCosmicos.class);

	/*
	 * EN: Half-width of the lunar and eclipse windows, in days. Wide enough for the
	 *     furthest "nearest phenomenon" any calculator asks for (the first new moon
	 *     after the nearest winter solstice, which can be about 215 days away) with a
	 *     comfortable margin, and still only about 130 lunar rows instead of 29.000.
	 * ES: Semiancho de las ventanas de lunas y eclipses, en dias. Suficiente para el
	 *     "fenomeno mas cercano" mas lejano que pide cualquier calculador (la primera
	 *     luna nueva tras el solsticio de invierno mas cercano, que puede estar a unos
	 *     215 dias) con un margen holgado, y aun asi solo unas 130 filas de lunas en
	 *     lugar de 29.000.
	 */
	private static final long DIAS_DE_MARGEN = 500L;

	private final CatalogoCalendario catalogo;
	private final SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	private final LunasRepository lunasRepository;
	private final EclipsesRepository eclipsesRepository;
	private final ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;

	public CargadorDatosCosmicos(CatalogoCalendario catalogo,
			SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository,
			LunasRepository lunasRepository,
			EclipsesRepository eclipsesRepository,
			ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository) {
		this.catalogo = catalogo;
		this.solsticiosYEquinocciosRepository = solsticiosYEquinocciosRepository;
		this.lunasRepository = lunasRepository;
		this.eclipsesRepository = eclipsesRepository;
		this.apogeosYPerigeosLunaRepository = apogeosYPerigeosLunaRepository;
	}

	/*
	 * EN: Builds the context for a date, or an invalid context carrying the reason.
	 *     The order of the checks is the same the old code used, so the message a
	 *     client gets for an unconvertible date does not change.
	 * ES: Construye el contexto de una fecha, o un contexto invalido que lleva el
	 *     motivo. El orden de las comprobaciones es el mismo que usaba el codigo
	 *     antiguo, asi que el mensaje que recibe un cliente para una fecha no
	 *     convertible no cambia.
	 */
	public ContextoCosmico cargar(LocalDate fecha) {

		ContextoCosmico contexto = new ContextoCosmico(fecha);

		// EN: Upper bound of the old query windows: one year after the end of the day.
		// ES: Limite superior de las ventanas antiguas: un ano tras el fin del dia.
		LocalDateTime finDelDia = Fechas.finDelDia(fecha);
		LocalDateTime unAnyoDespues = finDelDia.plusYears(1);

		// ---------------------------------------------------------------------
		// EN: 1. Eclipenos - the outermost VAU unit, read from the catalog.
		// ES: 1. Eclipenos - la unidad VAU mas externa, leida del catalogo.
		// ---------------------------------------------------------------------
		List<EclipenosEntity> eclipenos = this.catalogo.eclipenosPorFechaDesc();
		if (eclipenos.isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no hay eclipenos");
		}
		contexto.setEclipenos(eclipenos);

		EclipenosEntity ultimoEclipenoIN = ultimoEclipenoInicialNuevo(eclipenos, fecha);
		if (ultimoEclipenoIN == null) {
			return invalido(contexto, "Error al obtener dateVAU: no se ha encontrado un eclípeno inicial nuevo "
					+ "anterior a la fecha proporcionada.");
		}
		contexto.setUltimoEclipenoIN(ultimoEclipenoIN);

		EclipenosEntity ultimoEclipenoIAR = ultimoEclipenoInvernalApofasalRemoto(eclipenos, fecha);
		if (ultimoEclipenoIAR == null) {
			return invalido(contexto, "Error al obtener dateVAU: no se ha encontrado un eclípeno invernal apofasal "
					+ "remoto anterior a la fecha proporcionada.");
		}
		contexto.setUltimoEclipenoInvernalApofasalRemoto(ultimoEclipenoIAR);

		// ---------------------------------------------------------------------
		// EN: 2. Metonos - same window the old SQL query asked for, sliced out of
		//     the catalog with a binary search instead of a database round trip.
		// ES: 2. Metonos - la misma ventana que pedia la consulta SQL antigua, recortada
		//     del catalogo con una busqueda binaria en vez de un viaje a la base de datos.
		// ---------------------------------------------------------------------
		List<MetonsEntity> metonos = this.catalogo.metonosEntre(ultimoEclipenoIAR.getDate().minusYears(1),
				unAnyoDespues);
		if (metonos.isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no se han encontrado métonos.");
		}
		contexto.setMetonos(metonos);

		MetonsEntity ultimoMetonoIN = ultimoMetonoInicialNuevo(metonos, fecha);
		MetonsEntity ultimoMetonoIAR = ultimoMetonoInvernalApofasalRemoto(metonos, fecha);

		/*
		 * EN: The old code asked "if (lastMetonIN != null)" but its finder returned an
		 *     empty entity instead of null, so the guard never fired and a missing
		 *     metono surfaced as a NullPointerException a few lines later. Checking the
		 *     two anchors for real produces the message the guard was written for.
		 * ES: El codigo antiguo preguntaba "if (lastMetonIN != null)" pero su buscador
		 *     devolvia una entidad vacia en lugar de null, asi que la guarda nunca
		 *     saltaba y un metono ausente afloraba como NullPointerException unas lineas
		 *     mas abajo. Comprobar de verdad las dos anclas produce el mensaje para el
		 *     que se escribio la guarda.
		 */
		if (ultimoMetonoIN == null || ultimoMetonoIAR == null) {
			return invalido(contexto, "Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha "
					+ "proporcionada.");
		}
		contexto.setUltimoMetonoIN(ultimoMetonoIN);
		contexto.setUltimoMetonoIApofasalRemoto(ultimoMetonoIAR);

		// ---------------------------------------------------------------------
		// EN: 3. The phenomena that still come from the database. Every window is
		//     clamped to the bounds the old queries used, so the retained rows are a
		//     subset of what the old code saw.
		// ES: 3. Los fenomenos que siguen viniendo de la base de datos. Cada ventana se
		//     recorta a los limites que usaban las consultas antiguas, asi que las filas
		//     conservadas son un subconjunto de lo que veia el codigo antiguo.
		// ---------------------------------------------------------------------
		LocalDateTime margenAtras = fecha.minusDays(DIAS_DE_MARGEN).atStartOfDay();

		contexto.setLunas(this.lunasRepository.findByDateBetweenOrderByDateAsc(
				masReciente(ultimoMetonoIAR.getDate().minusYears(1), margenAtras), unAnyoDespues));

		contexto.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqualOrderByDateAsc(
				ultimoMetonoIN.getDate().minusYears(1), unAnyoDespues));

		contexto.setEclipses(this.eclipsesRepository.findByEsParcialFalseAndEsPenumbralFalseAndDateBetweenOrderByDateAsc(
				masReciente(Fechas.inicioDelDia(ultimoEclipenoIN.getDate().toLocalDate()), margenAtras),
				unAnyoDespues));

		contexto.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetweenOrderByDateAsc(
				finDelDia.minusMonths(3), finDelDia.plusMonths(3)));

		// ---------------------------------------------------------------------
		// EN: 4. Same emptiness checks, same order, same messages as before.
		// ES: 4. Las mismas comprobaciones de vacio, el mismo orden y los mismos
		//     mensajes que antes.
		// ---------------------------------------------------------------------
		if (contexto.getApoperis().isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no se han encontrado apoperis.");
		}
		if (contexto.getSoes().isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no se han encontrado soes.");
		}
		if (contexto.getLunas().isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no se han encontrado fases lunares.");
		}
		if (contexto.getEclipses().isEmpty()) {
			return invalido(contexto, "Error al obtener dateVAU: no se han encontrado eclipses.");
		}

		contexto.setValido(true);
		return contexto;
	}

	// =========================================================================
	// EN: ANCHOR SEARCHES
	//     All four lists are ordered newest first, so the first row that satisfies
	//     the filter is already the nearest one before the date: the loop can stop
	//     there. The old code scanned every row and computed a day distance for each
	//     one just to end up with the same answer.
	// ES: BUSQUEDAS DE ANCLAS
	//     Las cuatro listas estan ordenadas de la mas nueva a la mas antigua, asi que
	//     la primera fila que cumple el filtro ya es la mas cercana antes de la fecha:
	//     el bucle puede parar ahi. El codigo antiguo recorria todas las filas y
	//     calculaba una distancia en dias para cada una solo para llegar a la misma
	//     respuesta.
	// =========================================================================

	/*
	 * EN: Last "eclipeno inicial nuevo": winter, new, and either annular or total.
	 * ES: Ultimo "eclipeno inicial nuevo": invernal, nuevo, y anular o total.
	 */
	private EclipenosEntity ultimoEclipenoInicialNuevo(List<EclipenosEntity> eclipenosDesc, LocalDate fecha) {
		long fechaDiaEpoch = fecha.toEpochDay();
		for (EclipenosEntity eclipeno : eclipenosDesc) {
			if (Fechas.diaEpoch(eclipeno.getDate()) <= fechaDiaEpoch
					&& eclipeno.isInvernal()
					&& eclipeno.isNuevo()
					&& (eclipeno.isEsAnular() || eclipeno.isEsTotal())) {
				return eclipeno;
			}
		}
		return null;
	}

	/*
	 * EN: Last "eclipeno invernal apofasal remoto":
	 *       invernal = winter solstice
	 *       apofasal = moon and apogee/perigee less than a sidereal day apart
	 *       remoto   = new moon and apogee
	 * ES: Ultimo "eclipeno invernal apofasal remoto":
	 *       invernal = solsticio de invierno
	 *       apofasal = luna y apoperi ambos a menos de un dia sideral
	 *       remoto   = luna nueva y apogeo
	 */
	private EclipenosEntity ultimoEclipenoInvernalApofasalRemoto(List<EclipenosEntity> eclipenosDesc,
			LocalDate fecha) {
		long fechaDiaEpoch = fecha.toEpochDay();
		for (EclipenosEntity eclipeno : eclipenosDesc) {
			if (Fechas.diaEpoch(eclipeno.getDate()) <= fechaDiaEpoch
					&& eclipeno.isInvernal()
					&& eclipeno.isNuevo()
					&& eclipeno.isApofasal()
					&& eclipeno.isSelecto()
					&& (eclipeno.isEsAnular() || eclipeno.isEsTotal())) {
				return eclipeno;
			}
		}
		return null;
	}

	/*
	 * EN: Last winter new metono before or on the requested date.
	 * ES: Ultimo metono invernal nuevo anterior o igual a la fecha consultada.
	 */
	private MetonsEntity ultimoMetonoInicialNuevo(List<MetonsEntity> metonosDesc, LocalDate fecha) {
		long fechaDiaEpoch = fecha.toEpochDay();
		for (MetonsEntity metono : metonosDesc) {
			if (Fechas.diaEpoch(metono.getDate()) <= fechaDiaEpoch && metono.isInvernal() && metono.isNuevo()) {
				return metono;
			}
		}
		return null;
	}

	/*
	 * EN: Last winter apofasal remote metono before or on the requested date.
	 * ES: Ultimo metono invernal apofasal remoto anterior o igual a la fecha consultada.
	 */
	private MetonsEntity ultimoMetonoInvernalApofasalRemoto(List<MetonsEntity> metonosDesc, LocalDate fecha) {
		long fechaDiaEpoch = fecha.toEpochDay();
		for (MetonsEntity metono : metonosDesc) {
			if (Fechas.diaEpoch(metono.getDate()) <= fechaDiaEpoch
					&& metono.isInvernal()
					&& metono.isApofasal()
					&& metono.isSelecto()
					&& metono.isNuevo()) {
				return metono;
			}
		}
		return null;
	}

	// =========================================================================
	// EN: HELPERS
	// ES: AUXILIARES
	// =========================================================================

	/*
	 * EN: The later of two instants, used to clamp a window's lower bound.
	 * ES: El mas tardio de dos instantes, para recortar el limite inferior de una ventana.
	 */
	private static LocalDateTime masReciente(LocalDateTime uno, LocalDateTime otro) {
		return uno.isAfter(otro) ? uno : otro;
	}

	/*
	 * EN: Marks the context as unconvertible and logs the reason once. The old code
	 *     printed these to System.out, which serialises every request thread on the
	 *     console lock; a logger does not.
	 * ES: Marca el contexto como no convertible y registra el motivo una vez. El codigo
	 *     antiguo los imprimia por System.out, que serializa todos los hilos de peticion
	 *     en el cerrojo de la consola; un logger no.
	 */
	private ContextoCosmico invalido(ContextoCosmico contexto, String mensaje) {
		contexto.setMensaje(mensaje);
		log.debug("{}", mensaje);
		return contexto;
	}
}
