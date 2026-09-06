package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.DateVAUDTO;

/**
 * EN: Manages the VAU days: Terra, Luno, Sole, Merco, Venuro, Marto, Júpeno, Saturino,
 * Liminol and Caelumbra.
 * ES: Gestiona los días VAU: Terra, Luno, Sole, Merco, Venuro, Marto, Júpeno, Saturino,
 * Liminol y Caelumbra.
 */
public interface DaysService {

	/**
	 * EN: Inverse of the conversion: given a VAU week and day, returns how many days must
	 * be added to the new moon to land on that date.
	 * ES: Operación inversa de la conversión: dada una semana y un día VAU, devuelve cuántos
	 * días hay que sumar a la luna nueva para llegar a esa fecha.
	 *
	 * @param dateVAU EN: VAU date holding the week and day names. / ES: fecha VAU con los nombres de semana y día.
	 * @return EN: days to add, or 0 if the week or day is unknown. / ES: días a sumar, o 0 si la semana o el día no existen.
	 */
	public abstract long getDiasASumarALaLunaNueva(DateVAUDTO dateVAU);

	/**
	 * EN: Inserts the ten fixed day rows if the table is empty.
	 * ES: Inserta las diez filas fijas de días si la tabla está vacía.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateDays();
}
