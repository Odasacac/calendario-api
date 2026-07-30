package CCASolutions.Calendario.Support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.FestividadesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;

/*
 * ==============================================================================
 * EN: In-memory catalog of the immutable tables.
 *
 *     Six of the tables of this database never change once the calendar has been
 *     generated: months (18 rows), weeks (6), days (10), festividades (18),
 *     eclipenos (~220) and metons (~1.800). The original code queried them again
 *     and again inside the request:
 *
 *       - months        : 1 to 3 queries per request (plus one more per recursion)
 *       - weeks / days  : 2 queries per request
 *       - festividades  : a findAll() per request
 *       - eclipenos     : a findAll() ordered by date per request
 *       - metons        : a range query per request
 *
 *     That is roughly 8 round trips to MySQL for data that is identical on every
 *     call. This catalog loads them once and serves them from memory, which
 *     removes those round trips entirely and also removes the entity hydration
 *     cost that came with them.
 *
 *     Loading is lazy on purpose: on a fresh installation the database is still
 *     empty when the application starts and only gets filled by /api/poblatedb,
 *     so the catalog is built on first use and can be dropped with invalidar()
 *     once the population job finishes.
 *
 *     Thread safety: the snapshot is published through a volatile field and every
 *     collection inside it is unmodifiable. The cached entities are detached JPA
 *     objects that nothing on the read path mutates, so sharing them between
 *     request threads is safe.
 *
 * ES: Catalogo en memoria de las tablas inmutables.
 *
 *     Seis de las tablas de esta base de datos no cambian nunca una vez generado el
 *     calendario: months (18 filas), weeks (6), days (10), festividades (18),
 *     eclipenos (~220) y metons (~1.800). El codigo original las consultaba una y
 *     otra vez dentro de la peticion:
 *
 *       - months        : de 1 a 3 consultas por peticion (mas una por recursion)
 *       - weeks / days  : 2 consultas por peticion
 *       - festividades  : un findAll() por peticion
 *       - eclipenos     : un findAll() ordenado por fecha por peticion
 *       - metons        : una consulta por rango por peticion
 *
 *     Eso son unos 8 viajes a MySQL para datos identicos en cada llamada. Este
 *     catalogo los carga una vez y los sirve desde memoria, lo que elimina esos
 *     viajes y tambien el coste de hidratacion de entidades que conllevaban.
 *
 *     La carga es diferida a proposito: en una instalacion nueva la base de datos
 *     esta vacia cuando arranca la aplicacion y solo se llena con /api/poblatedb,
 *     asi que el catalogo se construye en el primer uso y se puede descartar con
 *     invalidar() cuando termina el proceso de poblacion.
 *
 *     Seguridad entre hilos: la instantanea se publica mediante un campo volatile y
 *     todas las colecciones que contiene son inmodificables. Las entidades
 *     cacheadas son objetos JPA desacoplados que nadie muta en el camino de
 *     lectura, por lo que compartirlas entre hilos de peticion es seguro.
 * ==============================================================================
 */
@Component
public class CatalogoCalendario {

	private static final Logger log = LoggerFactory.getLogger(CatalogoCalendario.class);

	private final MonthsRepository monthsRepository;
	private final WeeksRepository weeksRepository;
	private final DaysRepository daysRepository;
	private final FestividadesRepository festividadesRepository;
	private final EclipenosRepository eclipenosRepository;
	private final MetonsRepository metonsRepository;

	/*
	 * EN: The published snapshot. Volatile so a thread that did not build it still
	 *     sees a fully constructed object.
	 * ES: La instantanea publicada. Volatile para que un hilo que no la haya
	 *     construido vea igualmente un objeto completamente inicializado.
	 */
	private volatile Datos datos;

	public CatalogoCalendario(MonthsRepository monthsRepository,
			WeeksRepository weeksRepository,
			DaysRepository daysRepository,
			FestividadesRepository festividadesRepository,
			EclipenosRepository eclipenosRepository,
			MetonsRepository metonsRepository) {
		this.monthsRepository = monthsRepository;
		this.weeksRepository = weeksRepository;
		this.daysRepository = daysRepository;
		this.festividadesRepository = festividadesRepository;
		this.eclipenosRepository = eclipenosRepository;
		this.metonsRepository = metonsRepository;
	}

	// =========================================================================
	// EN: PUBLIC LOOKUPS - all of them are plain map or list reads.
	// ES: CONSULTAS PUBLICAS - todas son simples lecturas de mapa o de lista.
	// =========================================================================

	/*
	 * EN: The VAU month for a given (season, month of the season, liminal) triplet.
	 *     Replaces MonthsRepository.findBySeasonAndMonthOfSeasonAndLiminal.
	 *     Returns null when the combination does not exist, exactly like the
	 *     derived query did.
	 * ES: El mes VAU para una terna (estacion, mes de la estacion, liminal).
	 *     Sustituye a MonthsRepository.findBySeasonAndMonthOfSeasonAndLiminal.
	 *     Devuelve null cuando la combinacion no existe, igual que hacia la
	 *     consulta derivada.
	 */
	public MonthsEntity mes(int season, int monthOfSeason, boolean liminal) {
		return datos().meses.get(claveMes(season, monthOfSeason, liminal));
	}

	/*
	 * EN: Name of the VAU week that holds the given week-of-month (1..5).
	 * ES: Nombre de la semana VAU que corresponde a la semana del mes (1..5).
	 */
	public String nombreSemana(int weekOfMonth) {
		return datos().nombresDeSemana.get(weekOfMonth);
	}

	/*
	 * EN: Name of the VAU day for the given day-of-week offset.
	 * ES: Nombre del dia VAU para el desplazamiento de dia de la semana dado.
	 */
	public String nombreDia(long dayOfWeek) {
		return datos().nombresDeDia.get((int) dayOfWeek);
	}

	/*
	 * EN: Display name of a festivity, by its code ("CE", "CMAR", "MSI", ...).
	 *     Replaces the linear search over festividadesRepository.findAll() that the
	 *     original code performed once per festivity and per request.
	 * ES: Nombre visible de una festividad, por su codigo ("CE", "CMAR", "MSI", ...).
	 *     Sustituye la busqueda lineal sobre festividadesRepository.findAll() que el
	 *     codigo original hacia una vez por festividad y por peticion.
	 */
	public String nombreFestividad(String code) {
		if (code == null) {
			return null;
		}
		return datos().nombresDeFestividad.get(code);
	}

	/*
	 * EN: All eclipenos, newest first. Same order the old
	 *     findAllByOrderByDateDesc() produced.
	 * ES: Todos los eclipenos, del mas reciente al mas antiguo. El mismo orden que
	 *     producia el antiguo findAllByOrderByDateDesc().
	 */
	public List<EclipenosEntity> eclipenosPorFechaDesc() {
		return datos().eclipenos;
	}

	/*
	 * EN: The metonos whose date falls in [desde, hasta], newest first. This is the
	 *     in-memory replacement for
	 *     MetonsRepository.findByDateBetweenOrderByDateDesc(desde, hasta) and it
	 *     returns exactly the same window: several calculators pick "the nearest
	 *     metono in either direction", so widening the window would change results.
	 * ES: Los metonos cuya fecha cae en [desde, hasta], del mas reciente al mas
	 *     antiguo. Es el sustituto en memoria de
	 *     MetonsRepository.findByDateBetweenOrderByDateDesc(desde, hasta) y devuelve
	 *     exactamente la misma ventana: varios calculadores eligen "el metono mas
	 *     cercano en cualquier direccion", asi que ampliar la ventana cambiaria los
	 *     resultados.
	 */
	public List<MetonsEntity> metonosEntre(LocalDateTime desde, LocalDateTime hasta) {
		return Rangos.entre(datos().metonos, MetonsEntity::getDate, desde, hasta);
	}

	/*
	 * EN: True when the catalog found no eclipenos, i.e. the calendar has not been
	 *     generated yet.
	 * ES: Cierto cuando el catalogo no encontro eclipenos, es decir, el calendario
	 *     todavia no se ha generado.
	 */
	public boolean vacio() {
		return datos().eclipenos.isEmpty();
	}

	/*
	 * EN: Drops the snapshot so the next read rebuilds it. Called after the
	 *     population job, which is the only thing that can change these tables.
	 * ES: Descarta la instantanea para que la siguiente lectura la reconstruya. Se
	 *     llama tras el proceso de poblacion, lo unico que puede cambiar estas tablas.
	 */
	public void invalidar() {
		this.datos = null;
		log.info("Catálogo en memoria invalidado; se recargará en la próxima lectura.");
	}

	// =========================================================================
	// EN: LOADING
	// ES: CARGA
	// =========================================================================

	/*
	 * EN: Returns the snapshot, building it on first use. Double-checked locking:
	 *     the common path is a single volatile read with no synchronisation.
	 * ES: Devuelve la instantanea, construyendola en el primer uso. Bloqueo con
	 *     doble comprobacion: el camino habitual es una unica lectura volatile sin
	 *     sincronizacion.
	 */
	private Datos datos() {
		Datos actuales = this.datos;
		if (actuales == null) {
			synchronized (this) {
				actuales = this.datos;
				if (actuales == null) {
					actuales = cargar();
					this.datos = actuales;
				}
			}
		}
		return actuales;
	}

	/*
	 * EN: Reads the six tables once. Runs in a read-only transaction so Hibernate
	 *     skips dirty checking and never tries to flush these entities back.
	 * ES: Lee las seis tablas una vez. Se ejecuta en una transaccion de solo lectura
	 *     para que Hibernate se salte la deteccion de cambios y nunca intente
	 *     volcar estas entidades de vuelta.
	 */
	@Transactional(readOnly = true)
	protected Datos cargar() {

		Datos cargados = new Datos();

		// EN: months -> keyed by (season, month of season, liminal).
		// ES: months -> indexado por (estacion, mes de la estacion, liminal).
		for (MonthsEntity mes : this.monthsRepository.findAll()) {
			cargados.meses.put(claveMes(valor(mes.getSeason()), valor(mes.getMonthOfSeason()), mes.isLiminal()), mes);
		}

		// EN: weeks / days -> only their names are ever used downstream.
		// ES: weeks / days -> aguas abajo solo se usan sus nombres.
		for (WeeksEntity semana : this.weeksRepository.findAll()) {
			cargados.nombresDeSemana.put(semana.getWeekOfMonth(), semana.getName());
		}
		for (DaysEntity dia : this.daysRepository.findAll()) {
			cargados.nombresDeDia.put(dia.getDayOfWeek(), dia.getName());
		}

		// EN: festividades -> code to display name.
		// ES: festividades -> codigo a nombre visible.
		for (FestividadesEntity festividad : this.festividadesRepository.findAll()) {
			cargados.nombresDeFestividad.put(festividad.getCode(), festividad.getNombre());
		}

		/*
		 * EN: eclipenos / metons -> full lists, newest first. Rows with a NULL date
		 *     are dropped because the SQL queries they replace ("date BETWEEN ...")
		 *     never matched them either, and keeping them would break the binary
		 *     search in Rangos.
		 * ES: eclipenos / metons -> listas completas, de la mas reciente a la mas
		 *     antigua. Las filas con fecha NULL se descartan porque las consultas SQL
		 *     que sustituyen ("date BETWEEN ...") tampoco las devolvian, y mantenerlas
		 *     romperia la busqueda binaria de Rangos.
		 */
		Sort porFechaDesc = Sort.by(Sort.Direction.DESC, "date");
		cargados.eclipenos = conFecha(this.eclipenosRepository.findAll(porFechaDesc), EclipenosEntity::getDate);
		cargados.metonos = conFecha(this.metonsRepository.findAll(porFechaDesc), MetonsEntity::getDate);

		cargados.sellar();

		log.info("Catálogo en memoria cargado: {} meses, {} semanas, {} días, {} festividades, "
				+ "{} eclípenos, {} métonos.",
				cargados.meses.size(), cargados.nombresDeSemana.size(), cargados.nombresDeDia.size(),
				cargados.nombresDeFestividad.size(), cargados.eclipenos.size(), cargados.metonos.size());

		return cargados;
	}

	/*
	 * EN: Copies the list into an unmodifiable one, dropping rows without a date.
	 * ES: Copia la lista a una inmodificable, descartando las filas sin fecha.
	 */
	private static <T> List<T> conFecha(List<T> origen, Function<T, LocalDateTime> fechaDe) {
		List<T> conFecha = new ArrayList<>(origen.size());
		for (T elemento : origen) {
			if (fechaDe.apply(elemento) != null) {
				conFecha.add(elemento);
			}
		}
		return Collections.unmodifiableList(conFecha);
	}

	/*
	 * EN: Packs the three month coordinates into a single int key so the lookup is
	 *     one hash of a primitive instead of building a composite object.
	 * ES: Empaqueta las tres coordenadas del mes en una unica clave int para que la
	 *     busqueda sea un hash de un primitivo en vez de construir un objeto compuesto.
	 */
	private static int claveMes(int season, int monthOfSeason, boolean liminal) {
		return (season * 1000) + (monthOfSeason * 2) + (liminal ? 1 : 0);
	}

	/*
	 * EN: months stores season / monthOfSeason as nullable Integers; treat null as 0,
	 *     which is how the derived query behaved for the hybrid months.
	 * ES: months guarda season / monthOfSeason como Integer nullable; se trata null
	 *     como 0, que es como se comportaba la consulta derivada para los meses hibridos.
	 */
	private static int valor(Integer posibleNulo) {
		return posibleNulo == null ? 0 : posibleNulo.intValue();
	}

	/*
	 * EN: The immutable snapshot itself.
	 * ES: La propia instantanea inmutable.
	 */
	protected static final class Datos {

		private Map<Integer, MonthsEntity> meses = new HashMap<>();
		private Map<Integer, String> nombresDeSemana = new HashMap<>();
		private Map<Integer, String> nombresDeDia = new HashMap<>();
		private Map<String, String> nombresDeFestividad = new HashMap<>();
		private List<EclipenosEntity> eclipenos = Collections.emptyList();
		private List<MetonsEntity> metonos = Collections.emptyList();

		/*
		 * EN: Freezes every map once loading is done, so the snapshot cannot be
		 *     mutated by accident after publication.
		 * ES: Congela cada mapa cuando termina la carga, para que la instantanea no
		 *     pueda mutarse por accidente despues de publicarse.
		 */
		void sellar() {
			this.meses = Collections.unmodifiableMap(this.meses);
			this.nombresDeSemana = Collections.unmodifiableMap(this.nombresDeSemana);
			this.nombresDeDia = Collections.unmodifiableMap(this.nombresDeDia);
			this.nombresDeFestividad = Collections.unmodifiableMap(this.nombresDeFestividad);
		}
	}
}
