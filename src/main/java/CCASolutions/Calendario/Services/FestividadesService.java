package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.FestividadesDTO;

/**
 * EN: Manages the sixteen VAU festivities, each tied to an astronomical phenomenon:
 * change of eclipeno, change of meton, change of year, welcoming of spring, midsisons,
 * change of aponovo and so on.
 * ES: Gestiona las dieciséis festividades VAU, cada una ligada a un fenómeno astronómico:
 * cambio de eclípeno, cambio de métono, cambio de año, bienvenida de la primavera,
 * midsisons, cambio de aponovo, etcétera.
 */
public interface FestividadesService {

	/**
	 * EN: Inserts the sixteen fixed festivity rows if the table is empty.
	 * ES: Inserta las dieciséis filas fijas de festividades si la tabla está vacía.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateFestividades();

	/**
	 * EN: Works out today's festivity, the previous one and the next one. Festivities
	 * falling on the same day are resolved by relevance, and the presence of an eclipeno
	 * or a meton cancels the lesser festivities around it.
	 * ES: Calcula la festividad de hoy, la anterior y la próxima. Las festividades que caen
	 * el mismo día se resuelven por relevancia, y la presencia de un eclípeno o de un
	 * métono anula las festividades menores de su entorno.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: the three festivities, already formatted as text. / ES: las tres festividades, ya formateadas como texto.
	 */
	public abstract FestividadesDTO getFestividades(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
}
