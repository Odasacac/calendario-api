package CCASolutions.Calendario.Services;

import java.util.List;

import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;

/**
 * EN: Cached access to the reference tables (days, weeks, months, seasons and
 * festivities). They hold fewer than twenty rows each and never change unless the
 * database is repopulated, yet every date conversion used to query them between five and
 * eight times against MySQL.
 * ES: Acceso cacheado a las tablas de referencia (días, semanas, meses, estaciones y
 * festividades). Son tablas de menos de veinte filas que no cambian salvo que se
 * repueble la base de datos, pero cada conversión de fecha las consultaba entre
 * cinco y ocho veces contra MySQL.
 */
public interface TablasReferenciaService {

	/**
	 * EN: VAU week by its number within the month, 0 to 5.
	 * ES: Semana VAU por su número dentro del mes, del 0 al 5.
	 *
	 * @param weekOfMonth EN: week number. / ES: número de semana.
	 * @return EN: the week, or {@code null} if it does not exist. / ES: la semana, o {@code null} si no existe.
	 */
	WeeksEntity getSemanaPorNumero(int weekOfMonth);

	/**
	 * EN: VAU day by its number within the week, 0 to 9.
	 * ES: Día VAU por su número dentro de la semana, del 0 al 9.
	 *
	 * @param dayOfWeek EN: day number. / ES: número de día.
	 * @return EN: the day, or {@code null} if it does not exist. / ES: el día, o {@code null} si no existe.
	 */
	DaysEntity getDiaPorNumero(long dayOfWeek);

	/**
	 * EN: VAU month by season, position within the season and whether it is liminal.
	 * ES: Mes VAU por estación, posición dentro de la estación y si es liminal.
	 *
	 * @param season        EN: season, 1 to 4; 0 for the hybrid month. / ES: estación, del 1 al 4; 0 para el mes híbrido.
	 * @param monthOfSeason EN: position within the season; 0 for the hybrid month. / ES: posición dentro de la estación; 0 para el mes híbrido.
	 * @param liminal       EN: whether the liminal month is wanted. / ES: si se quiere el mes liminal.
	 * @return EN: the month, or {@code null} if it does not exist. / ES: el mes, o {@code null} si no existe.
	 */
	MonthsEntity getMes(int season, int monthOfSeason, boolean liminal);

	/**
	 * EN: VAU season by its number, 1 to 4; 0 is the placeholder season.
	 * ES: Estación VAU por su número, del 1 al 4; el 0 es la estación de relleno.
	 *
	 * @param seasonOfTheYear EN: season number. / ES: número de estación.
	 * @return EN: the season, or {@code null} if it does not exist. / ES: la estación, o {@code null} si no existe.
	 */
	SeasonsEntity getEstacion(int seasonOfTheYear);

	/**
	 * EN: Every festivity, to map codes onto names.
	 * ES: Todas las festividades, para traducir códigos a nombres.
	 *
	 * @return EN: the sixteen festivities. / ES: las dieciséis festividades.
	 */
	List<FestividadesEntity> getFestividades();

	/**
	 * EN: Empties the cache. Called after repopulating the database, so the reference
	 * tables are read again from MySQL.
	 * ES: Vacía la caché. Se llama después de repoblar la base de datos, para que las
	 * tablas de referencia se vuelvan a leer de MySQL.
	 */
	void limpiarCache();
}
