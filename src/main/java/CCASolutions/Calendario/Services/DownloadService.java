package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;

/**
 * EN: Generates downloadable documents from the calendar. Not implemented yet: both
 * methods currently return {@code null} and the controller answers 501.
 * ES: Genera documentos descargables del calendario. Todavía sin implementar: ambos
 * métodos devuelven {@code null} por ahora y el controlador responde 501.
 */
public interface DownloadService {

	/**
	 * EN: Builds the general PDF of the calendar.
	 * ES: Construye el PDF general del calendario.
	 *
	 * @return EN: bytes of the document, or {@code null} while it is not implemented. / ES: los bytes del documento, o {@code null} mientras no esté implementado.
	 */
	public abstract byte[] getPDF();

	/**
	 * EN: Builds the calendar of one specific VAU year.
	 * ES: Construye el calendario de un año VAU concreto.
	 *
	 * @param yearForCalendar EN: identifiers of the requested VAU year. / ES: identificadores del año VAU solicitado.
	 * @return EN: bytes of the document, or {@code null} while it is not implemented. / ES: los bytes del documento, o {@code null} mientras no esté implementado.
	 */
	public abstract byte[] getCalendarForAYear(CalendarByYearDTO yearForCalendar);

}
