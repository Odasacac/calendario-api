package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DateDTO;

/**
 * EN: Orchestrates the conversion of a Gregorian date into a full VAU date.
 * ES: Orquesta la conversión de una fecha gregoriana en una fecha VAU completa.
 */
public interface DatesService {

	/**
	 * EN: Converts a Gregorian date ("dateO", the original date) into its VAU equivalent:
	 * year, season, month, week, day, meton, eclipeno, absolute eclipses, moon state,
	 * aponovos, casalero, notable event and festivities.
	 * ES: Convierte una fecha gregoriana ("dateO", la fecha original) en su equivalente VAU:
	 * año, estación, mes, semana, día, métono, eclípeno, eclipses absolutos, estado de la
	 * luna, aponovos, casalero, evento notable y festividades.
	 *
	 * @param date EN: Gregorian date to convert. / ES: fecha gregoriana a convertir.
	 * @return EN: the VAU date, or a DTO carrying the reason it could not be computed. / ES: la fecha VAU, o un DTO con el motivo por el que no se ha podido calcular.
	 */
	public abstract DateDTO getDateVAUFromDateO (LocalDate date);

}
