package CCASolutions.Calendario.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.FestividadesRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.TablasReferenciaService;

/**
 * EN: Cached access to the reference tables (days, weeks, months, seasons and
 * festivities). They hold fewer than twenty rows each and only change when the database is
 * repopulated, yet every date conversion used to query them between five and eight times.
 * ES: Acceso cacheado a las tablas de referencia (días, semanas, meses, estaciones y
 * festividades). Son tablas de menos de veinte filas que sólo cambian al repoblar la base
 * de datos, pero cada conversión de fecha las consultaba entre cinco y ocho veces.
 */
@Service
@Transactional(readOnly = true)
public class TablasReferenciaServiceImpl implements TablasReferenciaService {

	static final String CACHE_SEMANAS = "semanas";
	static final String CACHE_DIAS = "dias";
	static final String CACHE_MESES = "meses";
	static final String CACHE_ESTACIONES = "estaciones";
	static final String CACHE_FESTIVIDADES = "festividades";

	@Autowired
	private WeeksRepository weeksRepository;

	@Autowired
	private DaysRepository daysRepository;

	@Autowired
	private MonthsRepository monthsRepository;

	@Autowired
	private SeasonsRepository seasonsRepository;

	@Autowired
	private FestividadesRepository festividadesRepository;

	/**
	 * EN: VAU week by its number within the month. Cached, so it only reaches MySQL once.
	 * ES: Semana VAU por su número dentro del mes. Cacheada, así que sólo llega a MySQL una vez.
	 *
	 * @param weekOfMonth EN: week number, 0 to 5. / ES: número de semana, del 0 al 5.
	 * @return EN: the week, or {@code null} if it does not exist. / ES: la semana, o {@code null} si no existe.
	 */
	@Override
	@Cacheable(CACHE_SEMANAS)
	public WeeksEntity getSemanaPorNumero(int weekOfMonth) {
		return this.weeksRepository.findByWeekOfMonth(weekOfMonth);
	}

	/**
	 * EN: VAU day by its number within the week. Takes a {@code long} because the caller
	 * counts days elapsed since the new moon; the value always fits in an int.
	 * ES: Día VAU por su número dentro de la semana. Recibe un {@code long} porque quien lo
	 * llama cuenta días transcurridos desde la luna nueva; el valor siempre cabe en un int.
	 *
	 * @param dayOfWeek EN: day number, 0 to 9. / ES: número de día, del 0 al 9.
	 * @return EN: the day, or {@code null} if it does not exist. / ES: el día, o {@code null} si no existe.
	 */
	@Override
	@Cacheable(CACHE_DIAS)
	public DaysEntity getDiaPorNumero(long dayOfWeek) {
		return this.daysRepository.findByDayOfWeek((int) dayOfWeek);
	}

	/**
	 * EN: VAU month by season, position within the season and liminal flag.
	 * ES: Mes VAU por estación, posición dentro de la estación y bandera liminal.
	 *
	 * @param season        EN: season, 1 to 4. / ES: estación, del 1 al 4.
	 * @param monthOfSeason EN: position within the season; 0 is the hybrid month. / ES: posición dentro de la estación; 0 es el mes híbrido.
	 * @param liminal       EN: whether the liminal month is wanted. / ES: si se quiere el mes liminal.
	 * @return EN: the month, or {@code null} if it does not exist. / ES: el mes, o {@code null} si no existe.
	 */
	@Override
	@Cacheable(CACHE_MESES)
	public MonthsEntity getMes(int season, int monthOfSeason, boolean liminal) {
		return this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(season, monthOfSeason, liminal);
	}

	/**
	 * EN: VAU season by its number.
	 * ES: Estación VAU por su número.
	 *
	 * @param seasonOfTheYear EN: season number, 0 to 4. / ES: número de estación, del 0 al 4.
	 * @return EN: the season, or {@code null} if it does not exist. / ES: la estación, o {@code null} si no existe.
	 */
	@Override
	@Cacheable(CACHE_ESTACIONES)
	public SeasonsEntity getEstacion(int seasonOfTheYear) {
		return this.seasonsRepository.findBySeasonOfTheYear(seasonOfTheYear);
	}

	/**
	 * EN: Every festivity, read once and cached, so the sixteen rows are not fetched again
	 * on each date conversion.
	 * ES: Todas las festividades, leídas una vez y cacheadas, para no volver a traer las
	 * dieciséis filas en cada conversión de fecha.
	 *
	 * @return EN: the sixteen festivities. / ES: las dieciséis festividades.
	 */
	@Override
	@Cacheable(CACHE_FESTIVIDADES)
	public List<FestividadesEntity> getFestividades() {
		return this.festividadesRepository.findAll();
	}

	/**
	 * EN: Empties the five caches. Called once the database has been repopulated, so the
	 * next conversion reads the fresh rows.
	 * ES: Vacía las cinco cachés. Se llama una vez repoblada la base de datos, para que la
	 * siguiente conversión lea las filas nuevas.
	 */
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = CACHE_SEMANAS, allEntries = true),
			@CacheEvict(cacheNames = CACHE_DIAS, allEntries = true),
			@CacheEvict(cacheNames = CACHE_MESES, allEntries = true),
			@CacheEvict(cacheNames = CACHE_ESTACIONES, allEntries = true),
			@CacheEvict(cacheNames = CACHE_FESTIVIDADES, allEntries = true) })
	public void limpiarCache() {
		// El vaciado lo hace la anotación; se invoca tras repoblar la base de datos.
	}
}
