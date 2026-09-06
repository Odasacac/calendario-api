package CCASolutions.Calendario.ServiceImpl;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;
import CCASolutions.Calendario.Services.DownloadService;

/**
 * EN: Placeholder implementation of the document generation. Both methods return
 * {@code null} on purpose, and the controller turns that into a 501 response.
 * ES: Implementación de relleno de la generación de documentos. Los dos métodos devuelven
 * {@code null} a propósito, y el controlador lo convierte en una respuesta 501.
 */
@Service
public class DownloadServiceImpl implements DownloadService {

	/**
	 * EN: Would build the general PDF of the calendar. Not implemented yet.
	 * ES: Construiría el PDF general del calendario. Todavía sin implementar.
	 *
	 * @return EN: always {@code null} for the time being. / ES: siempre {@code null} por ahora.
	 */
	public byte[] getPDF() {

		return null;
	}

	/**
	 * EN: Would build the calendar of one specific VAU year. Not implemented yet.
	 * ES: Construiría el calendario de un año VAU concreto. Todavía sin implementar.
	 *
	 * @param yearForCalendar EN: identifiers of the requested VAU year. / ES: identificadores del año VAU solicitado.
	 * @return EN: always {@code null} for the time being. / ES: siempre {@code null} por ahora.
	 */
	public byte[] getCalendarForAYear(CalendarByYearDTO yearForCalendar) {

		return null;
	}

}
