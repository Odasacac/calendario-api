package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import CCASolutions.Calendario.DTOs.AnyoIndiceDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MesIndiceDTO;
import CCASolutions.Calendario.DTOs.MetonoIndiceDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Others.LunacionVAU;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.FasesLunaresRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.CalendarioVAUService;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class CalendarioVAUServiceImpl implements CalendarioVAUService {

	/** Lunaciones guardadas en memoria (~2,5 años de navegación); al pasarse se olvidan las más antiguas. */
	static final int MAX_LUNACIONES_EN_CACHE = 400;
	private static final int MAX_DIAS_POR_ANYO = 400;

	@Autowired
	private DatesService datesService;

	@Autowired
	private FasesLunaresRepository fasesLunaresRepository;

	@Autowired
	private MetonsRepository metonsRepository;

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	@Autowired
	private SeasonsRepository seasonsRepository;

	@Autowired
	private WeeksRepository weeksRepository;

	@Autowired
	private DaysRepository daysRepository;

	/*
	 * Futuros en vez de valores: si llegan a la vez la petición de una lunación
	 * y su precarga, se calcula una sola vez.
	 */
	private final Map<LocalDate, CompletableFuture<LunacionDTO>> lunaciones = new ConcurrentHashMap<>();
	private final Queue<LocalDate> ordenLunaciones = new ConcurrentLinkedQueue<>();
	private final Map<LocalDate, List<MesIndiceDTO>> mesesPorAnyo = new ConcurrentHashMap<>();
	private volatile CompletableFuture<List<MetonoIndiceDTO>> metonos;
	private volatile LunacionVAU constructor;

	/** Prepara el índice de métonos y la lunación de hoy sin retrasar el arranque. */
	@EventListener(ApplicationReadyEvent.class)
	void precalentar() {

		CompletableFuture.runAsync(() -> {
			try {
				// Primero lo que verá el usuario al abrir el calendario.
				LocalDate hoy = LocalDate.now();
				if(!hoy.isBefore(FECHA_MINIMA) && !hoy.isAfter(FECHA_MAXIMA)) {
					this.getLunacion(hoy);
				}
				this.getMetonos();
				System.out.println("Calendario VAU precalentado.");
			}
			catch(Exception e) {
				System.out.println("No se ha podido precalentar el calendario VAU: " + e);
			}
		});
	}

	@Override
	public LunacionDTO getLunacion(LocalDate fecha) {

		validarRango(fecha);

		LocalDate inicio = this.terraDe(fecha);
		CompletableFuture<LunacionDTO> futuro = this.futuroDe(inicio);

		try {
			LunacionDTO lunacion = futuro.join();
			// Lo más probable es que el usuario pase al mes de al lado: se deja calculado.
			this.precargar(lunacion.getAnterior());
			this.precargar(lunacion.getSiguiente());
			return lunacion;
		}
		catch(CompletionException e) {
			// Un fallo no se queda en caché: el siguiente intento vuelve a calcular.
			this.lunaciones.remove(inicio, futuro);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido calcular la lunación.", e.getCause());
		}
	}

	@Override
	public List<MetonoIndiceDTO> getMetonos() {

		CompletableFuture<List<MetonoIndiceDTO>> actual = this.metonos;
		if(actual == null) {
			synchronized(this) {
				if(this.metonos == null) {
					this.metonos = CompletableFuture.supplyAsync(this::calcularMetonos);
				}
				actual = this.metonos;
			}
		}

		try {
			return actual.join();
		}
		catch(CompletionException e) {
			this.metonos = null;
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido calcular el índice de métonos.", e.getCause());
		}
	}

	@Override
	public List<AnyoIndiceDTO> getAnyos(Long idMetono) {

		MetonoIndiceDTO metono = this.getMetonos().stream()
				.filter(m -> m.getId().equals(idMetono))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe ese métono."));

		// El métono cae en un solsticio de invierno (S0); el año k va del día siguiente a Sk hasta Sk+1.
		LocalDate solsticioDelMetono = metono.getInicio().minusDays(1);
		LocalDate hasta = metono.getFin() != null ? metono.getFin() : solsticioDelMetono.plusYears(20);

		List<LocalDate> solsticios = new ArrayList<>();
		solsticios.add(solsticioDelMetono);
		this.solsticiosYEquinocciosRepository
				.findByDateAfterAndDateLessThanEqual(solsticioDelMetono.plusDays(1).atStartOfDay(), hasta.atTime(23, 59, 59))
				.stream()
				.filter(SolsticiosYEquinocciosEntity::isSolsticioInvierno)
				.map(s -> s.getDate().toLocalDate())
				.sorted()
				.forEach(solsticios::add);

		List<AnyoIndiceDTO> anyos = new ArrayList<>();
		for(int k = 0; k + 1 < solsticios.size(); k++) {

			AnyoIndiceDTO anyo = new AnyoIndiceDTO();
			anyo.setNumero(k);
			anyo.setInicio(solsticios.get(k).plusDays(1));
			anyo.setFin(solsticios.get(k + 1));

			if(!anyo.getFin().isBefore(FECHA_MINIMA) && !anyo.getInicio().isAfter(FECHA_MAXIMA)) {
				anyos.add(anyo);
			}
		}
		return anyos;
	}

	@Override
	public List<MesIndiceDTO> getMeses(LocalDate inicio, LocalDate fin) {

		validarRango(inicio);
		if(fin.isBefore(inicio) || ChronoUnit.DAYS.between(inicio, fin) > MAX_DIAS_POR_ANYO) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El intervalo no corresponde a un año VAU.");
		}

		return this.mesesPorAnyo.computeIfAbsent(inicio, clave -> this.calcularMeses(clave, fin.isAfter(FECHA_MAXIMA) ? FECHA_MAXIMA : fin));
	}

	// ------------------------------------------------------------------

	/** Día de la luna nueva que abre la lunación de la fecha (Terra). */
	LocalDate terraDe(LocalDate fecha) {

		FasesLunaresEntity luna = this.fasesLunaresRepository.findFirstByDateBeforeAndNuevaIsTrueOrderByDateDesc(fecha.plusDays(1).atStartOfDay());
		if(luna == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay datos lunares para esa fecha.");
		}
		return luna.getDate().toLocalDate();
	}

	/** Futuro de la lunación que empieza en Terra; lo crea (y lanza el cálculo) si no existe. */
	private CompletableFuture<LunacionDTO> futuroDe(LocalDate inicio) {

		CompletableFuture<LunacionDTO> futuro = this.lunaciones.computeIfAbsent(inicio, clave -> {
			this.ordenLunaciones.add(clave);
			return CompletableFuture.supplyAsync(() -> this.calcularLunacion(clave));
		});
		this.limitarCache();
		return futuro;
	}

	/** Cálculo en segundo plano, sin esperar ni propagar errores (ya se reintentará si hace falta). */
	private void precargar(LocalDate fecha) {

		if(fecha == null) {
			return;
		}
		CompletableFuture.runAsync(() -> {
			try {
				LocalDate inicio = this.terraDe(fecha);
				this.futuroDe(inicio).exceptionally(e -> {
					this.lunaciones.remove(inicio);
					return null;
				});
			}
			catch(Exception e) {
				/* sin datos lunares: no hay nada que precargar */
			}
		});
	}

	private LunacionDTO calcularLunacion(LocalDate inicio) {

		FasesLunaresEntity siguiente = this.fasesLunaresRepository.findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(inicio.plusDays(1).atStartOfDay());
		if(siguiente == null) {
			throw new IllegalStateException("No hay luna nueva después de " + inicio);
		}
		LocalDate fin = siguiente.getDate().toLocalDate().minusDays(1);

		List<DateDTO> dias = this.datesService.getDatesVAUFromRange(inicio, fin);
		LunacionDTO lunacion = this.constructor().construir(dias);

		LocalDate anterior = inicio.minusDays(1);
		LocalDate posterior = fin.plusDays(1);
		lunacion.setAnterior(anterior.isBefore(FECHA_MINIMA) ? null : anterior);
		lunacion.setSiguiente(posterior.isAfter(FECHA_MAXIMA) ? null : posterior);
		return lunacion;
	}

	private void limitarCache() {

		while(this.ordenLunaciones.size() > MAX_LUNACIONES_EN_CACHE) {
			LocalDate masAntigua = this.ordenLunaciones.poll();
			if(masAntigua != null) {
				this.lunaciones.remove(masAntigua);
			}
		}
	}

	/** Número y apellido de cada métono salen del propio cálculo VAU del día siguiente al métono. */
	private List<MetonoIndiceDTO> calcularMetonos() {

		List<MetonsEntity> todos = new ArrayList<>(this.metonsRepository.findAllByInvernalIsTrueAndNuevoIsTrue());
		todos.sort(Comparator.comparing(MetonsEntity::getDate));

		List<MetonoIndiceDTO> indice = new ArrayList<>();
		for(int i = 0; i < todos.size(); i++) {

			LocalDate dia = todos.get(i).getDate().toLocalDate();
			LocalDate siguiente = i + 1 < todos.size() ? todos.get(i + 1).getDate().toLocalDate() : null;
			// El métono termina el día de su sucesor: el que acaba justo en FECHA_MINIMA (21-12-1433) se queda fuera.
			boolean tocaElRango = !dia.isAfter(FECHA_MAXIMA) && (siguiente == null || siguiente.isAfter(FECHA_MINIMA));

			if(tocaElRango) {
				MetonoIndiceDTO metono = new MetonoIndiceDTO();
				metono.setId(todos.get(i).getId());
				metono.setInicio(dia.plusDays(1));
				metono.setFin(siguiente);
				indice.add(metono);
			}
		}

		Integer[] numeros = new Integer[indice.size()];
		IntStream.range(0, indice.size()).parallel().forEach(i -> {
			MetonoIndiceDTO metono = indice.get(i);
			try {
				DateDTO vau = this.datesService.getDateVAUFromDateO(metono.getInicio());
				if(vau.getMetonoVAU() != null && vau.getMetonoVAU().getMetonsIN() != null) {
					numeros[i] = vau.getMetonoVAU().getMetonsIN().getNumberOfMetonIN();
					metono.setApellido(vau.getMetonoVAU().getMetonsIN().getLastMetonSurname());
				}
			}
			catch(Exception e) {
				// Al principio de los datos el cálculo VAU no siempre es posible (faltan festividades previas).
				System.out.println("Sin datos VAU para el métono del " + metono.getInicio() + ": " + e);
			}
		});

		completarNumeros(numeros);
		for(int i = 0; i < indice.size(); i++) {
			indice.get(i).setNumero(numeros[i] == null ? 0 : numeros[i]);
		}
		return List.copyOf(indice);
	}

	/**
	 * Los métonos se numeran de uno en uno: un hueco (cálculo VAU imposible)
	 * se deduce del vecino conocido más cercano.
	 */
	static void completarNumeros(Integer[] numeros) {

		for(int i = numeros.length - 2; i >= 0; i--) {
			if(numeros[i] == null && numeros[i + 1] != null) {
				numeros[i] = numeros[i + 1] - 1;
			}
		}
		for(int i = 1; i < numeros.length; i++) {
			if(numeros[i] == null && numeros[i - 1] != null) {
				numeros[i] = numeros[i - 1] + 1;
			}
		}
	}

	private List<MesIndiceDTO> calcularMeses(LocalDate inicio, LocalDate fin) {

		List<DateDTO> dias = this.datesService.getDatesVAUFromRange(inicio, fin);
		String[] nombres = LunacionVAU.nombresDeMes(dias);

		List<MesIndiceDTO> meses = new ArrayList<>();
		for(int i = 0; i < dias.size(); i++) {

			if(nombres[i] == null) {
				continue;
			}

			MesIndiceDTO ultimo = meses.isEmpty() ? null : meses.get(meses.size() - 1);
			if(ultimo == null || !ultimo.getNombre().equals(nombres[i])) {
				ultimo = new MesIndiceDTO();
				ultimo.setNombre(nombres[i]);
				ultimo.setFecha(LocalDate.parse(dias.get(i).getFechaO()));
				meses.add(ultimo);
			}

			DateDTO dia = dias.get(i);
			String apellido = LunacionVAU.tieneDatos(dia) && !LunacionVAU.esTerra(dia) ? dia.getMonth().getSurname() : null;
			if(ultimo.getApellido() == null && apellido != null && !apellido.isBlank() && !LunacionVAU.SIN_VALOR.equals(apellido)) {
				ultimo.setApellido(apellido);
			}
		}
		return List.copyOf(meses);
	}

	/** Nombres de estaciones, semanas y días: tablas fijas, se leen una vez. */
	private LunacionVAU constructor() {

		LunacionVAU actual = this.constructor;
		if(actual != null) {
			return actual;
		}

		Map<String, Integer> estaciones = new HashMap<>();
		for(SeasonsEntity estacion : this.seasonsRepository.findAll()) {
			estaciones.put(estacion.getName(), estacion.getSeasonOfTheYear());
		}

		Map<String, Integer> semanasPorNombre = new HashMap<>();
		List<WeeksEntity> semanasOrdenadas = new ArrayList<>(this.weeksRepository.findAll());
		semanasOrdenadas.sort(Comparator.comparingInt(WeeksEntity::getWeekOfMonth));
		List<String> semanas = new ArrayList<>();
		for(WeeksEntity semana : semanasOrdenadas) {
			semanasPorNombre.put(semana.getName(), semana.getWeekOfMonth());
			semanas.add(semana.getWeekOfMonth() == 0 ? "Terra" : semana.getName());
		}

		Map<String, Integer> diasPorNombre = new HashMap<>();
		List<DaysEntity> diasOrdenados = new ArrayList<>(this.daysRepository.findAll());
		diasOrdenados.sort(Comparator.comparingInt(DaysEntity::getDayOfWeek));
		List<String> diasSemana = new ArrayList<>();
		for(DaysEntity dia : diasOrdenados) {
			diasPorNombre.put(dia.getName(), dia.getDayOfWeek());
			if(dia.getDayOfWeek() >= 1 && dia.getDayOfWeek() <= 7) {
				diasSemana.add(dia.getName());
			}
		}

		actual = new LunacionVAU(estaciones, semanasPorNombre, diasPorNombre, List.copyOf(semanas), List.copyOf(diasSemana));
		this.constructor = actual;
		return actual;
	}

	private static void validarRango(LocalDate fecha) {

		if(fecha.isBefore(FECHA_MINIMA) || fecha.isAfter(FECHA_MAXIMA)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La fecha debe estar entre " + FECHA_MINIMA + " y " + FECHA_MAXIMA + ".");
		}
	}
}
