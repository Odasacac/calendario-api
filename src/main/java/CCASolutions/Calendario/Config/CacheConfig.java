package CCASolutions.Calendario.Config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import CCASolutions.Calendario.ServiceImpl.DatesServiceImpl;

/*
 * ==============================================================================
 * EN: Cache of converted dates.
 *
 *     Converting a date is a pure function of that date: the tables it reads are a
 *     pre-generated ephemeris covering the year 0 to 2099, and they only change when
 *     /api/poblatedb is run. That makes the result perfectly cacheable, and caching
 *     it is the difference between a few milliseconds and nothing at all for the
 *     access pattern a calendar actually has - the same handful of days, over and
 *     over, starting with today.
 *
 *     Caffeine is used rather than Spring's default in-memory cache because the
 *     default one is unbounded: with 767.000 convertible dates an unbounded map is a
 *     slow memory leak. This one is bounded by entry count and evicts the least
 *     recently used entries, so the memory it can hold is known in advance.
 *
 *     There is deliberately no time-to-live: the data is immutable between
 *     repopulations, and DBServiceImpl clears the cache explicitly when it changes.
 *
 * ES: Cache de fechas convertidas.
 *
 *     Convertir una fecha es una funcion pura de esa fecha: las tablas que lee son unas
 *     efemerides pregeneradas que cubren del ano 0 al 2099, y solo cambian cuando se
 *     ejecuta /api/poblatedb. Eso hace el resultado perfectamente cacheable, y cachearlo
 *     es la diferencia entre unos milisegundos y nada en absoluto para el patron de acceso
 *     que tiene realmente un calendario: los mismos pocos dias, una y otra vez, empezando
 *     por hoy.
 *
 *     Se usa Caffeine en lugar de la cache en memoria por defecto de Spring porque la de
 *     por defecto no tiene limite: con 767.000 fechas convertibles, un mapa sin limite es
 *     una fuga de memoria lenta. Esta esta acotada por numero de entradas y expulsa las
 *     menos usadas recientemente, asi que la memoria que puede ocupar se conoce de antemano.
 *
 *     A proposito no hay tiempo de vida: los datos son inmutables entre repoblaciones, y
 *     DBServiceImpl limpia la cache explicitamente cuando cambian.
 * ==============================================================================
 */
@Configuration
@EnableCaching
public class CacheConfig {

	/*
	 * EN: Maximum number of converted dates kept in memory. Each entry is a small
	 *     object graph, so a few thousand entries cost single-digit megabytes.
	 * ES: Numero maximo de fechas convertidas que se mantienen en memoria. Cada entrada
	 *     es un grafo de objetos pequeno, asi que unos miles de entradas cuestan
	 *     megabytes de una sola cifra.
	 */
	@Value("${calendario.cache.max-fechas:5000}")
	private long maximoDeFechas;

	/*
	 * EN: How long an entry may sit unused before it is dropped. This is not a
	 *     correctness bound - the data does not go stale - just a way of returning
	 *     memory after a burst of unusual dates.
	 * ES: Cuanto puede estar una entrada sin usarse antes de descartarse. No es un limite
	 *     de correccion - los datos no caducan - sino una forma de devolver memoria tras
	 *     una rafaga de fechas poco habituales.
	 */
	@Value("${calendario.cache.horas-sin-uso:24}")
	private long horasSinUso;

	@Bean
	CacheManager cacheManager() {

		CaffeineCacheManager gestor = new CaffeineCacheManager(DatesServiceImpl.CACHE_FECHAS_VAU);

		gestor.setCaffeine(Caffeine.newBuilder()
				.maximumSize(this.maximoDeFechas)
				.expireAfterAccess(this.horasSinUso, TimeUnit.HOURS)
				.recordStats());

		return gestor;
	}
}
