package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.LunasEntity;

/*
 * ==============================================================================
 * EN: Lunar phases (~104.000 rows), the biggest table of the read path.
 *
 *     The old read path issued a single findByDateBetween() spanning from one year
 *     before the last "metono invernal apofasal remoto" up to one year after the
 *     requested date. Because that anchor can sit centuries in the past, the query
 *     regularly hydrated ~29.000 entities per request just so a handful of loops
 *     could count some of them. It also had no ORDER BY, so the ordering depended
 *     on whatever MySQL happened to return.
 *
 *     It is replaced by:
 *       - one bounded window around the requested date, for the calculations that
 *         only look at nearby phases (month, week, day, notable events, festivities);
 *       - explicit COUNT and TOP-1 queries for the aggregates that genuinely need
 *         the full history (the aponovo counters and the nearest aponovo).
 *
 *     The counts are computed by MySQL over an index instead of in Java over a
 *     hydrated list, so the long history costs a few microseconds instead of tens
 *     of milliseconds and a few megabytes of garbage.
 *
 * ES: Fases lunares (~104.000 filas), la tabla mas grande del camino de lectura.
 *
 *     El camino de lectura antiguo lanzaba un unico findByDateBetween() que abarcaba
 *     desde un ano antes del ultimo "metono invernal apofasal remoto" hasta un ano
 *     despues de la fecha consultada. Como ese ancla puede estar siglos en el pasado,
 *     la consulta hidrataba habitualmente ~29.000 entidades por peticion solo para que
 *     unos pocos bucles contaran algunas de ellas. Ademas no tenia ORDER BY, por lo
 *     que el orden dependia de lo que MySQL devolviera.
 *
 *     Se sustituye por:
 *       - una ventana acotada alrededor de la fecha consultada, para los calculos que
 *         solo miran fases cercanas (mes, semana, dia, eventos resenables, festividades);
 *       - consultas COUNT y TOP-1 explicitas para los agregados que realmente
 *         necesitan el historico completo (los contadores de aponovos y el aponovo
 *         mas cercano).
 *
 *     Los conteos los calcula MySQL sobre un indice en vez de Java sobre una lista
 *     hidratada, asi que el historico largo cuesta unos microsegundos en lugar de
 *     decenas de milisegundos y varios megabytes de basura.
 * ==============================================================================
 */
public interface LunasRepository extends JpaRepository<LunasEntity, Long> {

	/*
	 * EN: The bounded window of phases around the requested date, oldest first.
	 *     The explicit ordering makes the "nearest phase" tie-breaks deterministic.
	 * ES: La ventana acotada de fases alrededor de la fecha consultada, de la mas
	 *     antigua a la mas nueva. El orden explicito hace deterministas los desempates
	 *     de "fase mas cercana".
	 */
	List<LunasEntity> findByDateBetweenOrderByDateAsc(LocalDateTime desde, LocalDateTime hasta);

	/*
	 * EN: How many aponovos (selected new moons) happened in [desde, hasta).
	 *     "idExcluida" drops the new moon that belongs to the anchoring metono, which
	 *     must not be counted as an aponovo of its own cycle; a null id excludes nothing.
	 * ES: Cuantos aponovos (lunas nuevas selectas) ocurrieron en [desde, hasta).
	 *     "idExcluida" descarta la luna nueva que pertenece al metono ancla, que no debe
	 *     contarse como aponovo de su propio ciclo; un id nulo no excluye nada.
	 */
	@Query("""
			SELECT COUNT(l)
			  FROM LunasEntity l
			 WHERE l.nueva = TRUE
			   AND l.selecta = TRUE
			   AND l.date >= :desde
			   AND l.date < :hasta
			   AND (:idExcluida IS NULL OR l.id <> :idExcluida)
			""")
	long contarAponovos(@Param("desde") LocalDateTime desde,
			@Param("hasta") LocalDateTime hasta,
			@Param("idExcluida") Long idExcluida);

	/*
	 * EN: The most recent aponovo inside [desde, hasta), i.e. the one the requested
	 *     date currently belongs to. Same filter as contarAponovos so both agree.
	 * ES: El aponovo mas reciente dentro de [desde, hasta), es decir, aquel al que
	 *     pertenece la fecha consultada. Mismo filtro que contarAponovos para que
	 *     ambos coincidan.
	 */
	@Query("""
			SELECT l
			  FROM LunasEntity l
			 WHERE l.nueva = TRUE
			   AND l.selecta = TRUE
			   AND l.date >= :desde
			   AND l.date < :hasta
			   AND (:idExcluida IS NULL OR l.id <> :idExcluida)
			 ORDER BY l.date DESC
			 LIMIT 1
			""")
	LunasEntity ultimoAponovo(@Param("desde") LocalDateTime desde,
			@Param("hasta") LocalDateTime hasta,
			@Param("idExcluida") Long idExcluida);

	/*
	 * EN: How many new moons (of any kind) happened in [desde, hasta). Used to derive
	 *     the month inside the current aponovo.
	 * ES: Cuantas lunas nuevas (de cualquier tipo) ocurrieron en [desde, hasta). Sirve
	 *     para derivar el mes dentro del aponovo actual.
	 */
	long countByNuevaTrueAndDateGreaterThanEqualAndDateLessThan(LocalDateTime desde, LocalDateTime hasta);

	/*
	 * EN: Nearest aponovo at or before the requested day, and nearest one after it.
	 *     Aponovos are rare (roughly one every 18 months), so they cannot be relied on
	 *     to appear inside the bounded window and need their own indexed TOP-1 lookups.
	 * ES: Aponovo mas cercano en el dia consultado o antes, y el mas cercano despues.
	 *     Los aponovos son escasos (aproximadamente uno cada 18 meses), asi que no se
	 *     puede contar con que aparezcan en la ventana acotada y necesitan sus propias
	 *     busquedas TOP-1 sobre indice.
	 */
	LunasEntity findFirstByNuevaTrueAndSelectaTrueAndDateBetweenOrderByDateDesc(LocalDateTime desde,
			LocalDateTime hasta);

	LunasEntity findFirstByNuevaTrueAndSelectaTrueAndDateBetweenOrderByDateAsc(LocalDateTime desde,
			LocalDateTime hasta);
}
