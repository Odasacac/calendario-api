package CCASolutions.Calendario.Services;

/**
 * EN: Manages the VAU weeks: Primana, Segana, Terana, Curana and Limana.
 * ES: Gestiona las semanas VAU: Primana, Segana, Terana, Curana y Limana.
 */
public interface WeeksService {

	/**
	 * EN: Inserts the six fixed week rows if the table is empty.
	 * ES: Inserta las seis filas fijas de semanas si la tabla está vacía.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateWeeks();
}
