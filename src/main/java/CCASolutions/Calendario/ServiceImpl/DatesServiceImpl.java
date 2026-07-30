package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Vau.BuscadorCasalero;
import CCASolutions.Calendario.Vau.CalculadoraAponovos;
import CCASolutions.Calendario.Vau.CalculadoraCiclosVAU;
import CCASolutions.Calendario.Vau.CalculadoraEclipsesAbsolutos;
import CCASolutions.Calendario.Vau.CalculadoraEstadoLuna;
import CCASolutions.Calendario.Vau.CalculadoraEventosResenables;
import CCASolutions.Calendario.Vau.CalculadoraFestividades;
import CCASolutions.Calendario.Vau.CalculadoraUnidadesVAU;
import CCASolutions.Calendario.Vau.CargadorDatosCosmicos;
import CCASolutions.Calendario.Vau.ContextoCosmico;

/*
 * ==============================================================================
 * EN: Converts an ordinary (Gregorian) date into a VAU date.
 *
 *     This class used to be 2.612 lines long and held every rule of the calendar in
 *     one place. It is now only the orchestration: it loads the cosmic context once
 *     and hands it to one collaborator per block of the response. Each collaborator
 *     is documented on its own and can be read, tested and optimised in isolation.
 *
 *     Two annotations carry most of the remaining performance work:
 *
 *     @Transactional(readOnly = true)
 *         One connection and one Hibernate session for the whole conversion instead
 *         of one per repository call, and a session that skips dirty checking
 *         because it knows nothing will be written. On the old code path, which
 *         hydrated tens of thousands of entities per request, that check alone was
 *         significant.
 *
 *     @Cacheable
 *         The conversion is a pure function of the date: the underlying tables are
 *         a pre-generated ephemeris that only changes when /api/poblatedb is run.
 *         Repeated requests for the same day - which is what a calendar front end
 *         does all day long, starting with today - are served from memory without
 *         touching MySQL at all.
 *
 * ES: Convierte una fecha ordinaria (gregoriana) en una fecha VAU.
 *
 *     Esta clase tenia 2.612 lineas y concentraba todas las reglas del calendario en un
 *     solo sitio. Ahora es solo la orquestacion: carga el contexto cosmico una vez y lo
 *     entrega a un colaborador por bloque de la respuesta. Cada colaborador esta
 *     documentado por su cuenta y se puede leer, probar y optimizar por separado.
 *
 *     Dos anotaciones cargan con la mayor parte del trabajo de rendimiento restante:
 *
 *     @Transactional(readOnly = true)
 *         Una conexion y una sesion de Hibernate para toda la conversion en lugar de una
 *         por llamada al repositorio, y una sesion que se salta la deteccion de cambios
 *         porque sabe que no se va a escribir nada. En el camino de codigo antiguo, que
 *         hidrataba decenas de miles de entidades por peticion, esa comprobacion sola ya
 *         era significativa.
 *
 *     @Cacheable
 *         La conversion es una funcion pura de la fecha: las tablas subyacentes son unas
 *         efemerides pregeneradas que solo cambian cuando se ejecuta /api/poblatedb. Las
 *         peticiones repetidas del mismo dia - que es lo que hace todo el dia un front de
 *         calendario, empezando por hoy - se sirven desde memoria sin tocar MySQL.
 * ==============================================================================
 */
@Service
public class DatesServiceImpl implements DatesService {

	/*
	 * EN: Name of the cache of converted dates. DBServiceImpl clears it after
	 *     repopulating the database.
	 * ES: Nombre de la cache de fechas convertidas. DBServiceImpl la limpia despues de
	 *     repoblar la base de datos.
	 */
	public static final String CACHE_FECHAS_VAU = "fechasVAU";

	private final CargadorDatosCosmicos cargador;
	private final CalculadoraUnidadesVAU unidades;
	private final CalculadoraCiclosVAU ciclos;
	private final CalculadoraEclipsesAbsolutos eclipsesAbsolutos;
	private final CalculadoraAponovos aponovos;
	private final CalculadoraEstadoLuna estadoLuna;
	private final CalculadoraEventosResenables eventos;
	private final CalculadoraFestividades festividades;
	private final BuscadorCasalero casaleros;

	/*
	 * EN: Constructor injection instead of eleven @Autowired fields: the dependencies
	 *     are final, the class can be built in a test without a Spring context, and a
	 *     missing bean fails at startup rather than at the first request.
	 * ES: Inyeccion por constructor en lugar de once campos @Autowired: las dependencias
	 *     son finales, la clase se puede construir en un test sin contexto de Spring, y un
	 *     bean que falta falla al arrancar y no en la primera peticion.
	 */
	public DatesServiceImpl(CargadorDatosCosmicos cargador,
			CalculadoraUnidadesVAU unidades,
			CalculadoraCiclosVAU ciclos,
			CalculadoraEclipsesAbsolutos eclipsesAbsolutos,
			CalculadoraAponovos aponovos,
			CalculadoraEstadoLuna estadoLuna,
			CalculadoraEventosResenables eventos,
			CalculadoraFestividades festividades,
			BuscadorCasalero casaleros) {
		this.cargador = cargador;
		this.unidades = unidades;
		this.ciclos = ciclos;
		this.eclipsesAbsolutos = eclipsesAbsolutos;
		this.aponovos = aponovos;
		this.estadoLuna = estadoLuna;
		this.eventos = eventos;
		this.festividades = festividades;
		this.casaleros = casaleros;
	}

	/*
	 * EN: A date that cannot be converted still gets an answer: the response carries
	 *     the reason and the original date, and fechaEncontrada stays false.
	 * ES: Una fecha que no se puede convertir tambien recibe respuesta: la respuesta lleva
	 *     el motivo y la fecha original, y fechaEncontrada se queda en false.
	 */
	@Override
	@Cacheable(CACHE_FECHAS_VAU)
	@Transactional(readOnly = true)
	public DateDTO getDateVAUFromDateO(LocalDate date) {

		ContextoCosmico contexto = this.cargador.cargar(date);

		DateDTO fechaVAU;

		if (contexto.isValido()) {
			fechaVAU = convertir(contexto);
		} else {
			fechaVAU = new DateDTO();
			fechaVAU.setMensaje(contexto.getMensaje());
		}

		fechaVAU.setFechaO(String.valueOf(date));

		return fechaVAU;
	}

	/*
	 * EN: Fills the response block by block. The order matters in one place: the
	 *     absolute eclipse counters read the eclipeno and metono blocks, so they are
	 *     computed after them.
	 * ES: Rellena la respuesta bloque a bloque. El orden importa en un sitio: los
	 *     contadores de eclipses absolutos leen los bloques de eclipeno y metono, asi que
	 *     se calculan despues de ellos.
	 */
	private DateDTO convertir(ContextoCosmico contexto) {

		DateDTO fechaVAU = new DateDTO();

		// EN: Calendar units. ES: Unidades de calendario.
		fechaVAU.setYear(this.unidades.calcularAnyo(contexto));
		fechaVAU.setMonth(this.unidades.calcularMes(contexto));

		VAUWeekAndDayDTO semanaYDia = this.unidades.calcularSemanaYDia(contexto);
		fechaVAU.setWeek(semanaYDia.getWeek());
		fechaVAU.setDay(semanaYDia.getDay());

		// EN: Long cycles. ES: Ciclos largos.
		fechaVAU.setLastEclipenoSelecto(this.ciclos.calcularEclipenoSelecto(contexto));
		fechaVAU.setMetonoInvernalApofasalRemoto(this.ciclos.calcularMetonoInvernalApofasalRemoto(contexto));
		fechaVAU.setMetonoVAU(this.ciclos.calcularMetono(contexto));
		fechaVAU.setEclipenoVAU(this.ciclos.calcularEclipeno(contexto));

		// EN: Counters and lunar state. ES: Contadores y estado de la luna.
		fechaVAU.setAbsoluteEclipses(this.eclipsesAbsolutos.calcular(contexto, fechaVAU));
		fechaVAU.setCasalero(this.casaleros.buscar(contexto));
		fechaVAU.setEstadoLuna(this.estadoLuna.calcular(contexto));
		fechaVAU.setAponovos(this.aponovos.calcular(contexto));

		// EN: Human readable blocks. ES: Bloques legibles.
		fechaVAU.setNotableEvent(this.eventos.calcular(contexto));
		fechaVAU.setFestividades(this.festividades.calcular(contexto));

		fechaVAU.setFechaEncontrada(true);

		return fechaVAU;
	}
}
