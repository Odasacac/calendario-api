package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;

/**
 * EN: Manages the casaleros. A casalero names an eclipeno after the first phenomenon that
 * happens once the eclipeno has passed: "Metónico" if it is a meton, "Eclipelar" if it is
 * an absolute eclipse.
 * ES: Gestiona los casaleros. Un casalero da nombre a un eclípeno según el primer fenómeno
 * que ocurre una vez pasado ese eclípeno: "Metónico" si es un métono, "Eclipelar" si es un
 * eclipse absoluto.
 */
public interface CasalerosService {

	/**
	 * EN: Creates one casalero per eclipeno, resolving which phenomenon comes first after
	 * it. The eclipse that triggered the eclipeno itself does not count.
	 * ES: Crea un casalero por cada eclípeno, resolviendo qué fenómeno llega antes después
	 * de él. El eclipse que disparó el propio eclípeno no cuenta.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateCasaleros();

	/**
	 * EN: Reads the casalero of the given eclipeno and fills in its details from the meton
	 * or the eclipse it points to.
	 * ES: Lee el casalero del eclípeno dado y rellena sus detalles a partir del métono o del
	 * eclipse al que apunta.
	 *
	 * @param lastEclipenoIN EN: eclipeno whose casalero is wanted. / ES: eclípeno del que se quiere el casalero.
	 * @return EN: the casalero, or {@code null} if the eclipeno has none. / ES: el casalero, o {@code null} si el eclípeno no tiene ninguno.
	 */
	public abstract CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN);
}
