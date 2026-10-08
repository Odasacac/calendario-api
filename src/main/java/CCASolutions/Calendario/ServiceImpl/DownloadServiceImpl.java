package CCASolutions.Calendario.ServiceImpl;

import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;
import CCASolutions.Calendario.Services.DownloadService;

@Service
public class DownloadServiceImpl implements DownloadService {

	private final static String MANUAL_PATH = "Documentacion/Manual_Calendario.pdf";

	public byte[] getManual() {

		byte[] manual = null;

		ClassPathResource manualResource = new ClassPathResource(MANUAL_PATH);

		if(manualResource.exists()) {

			try (InputStream manualStream = manualResource.getInputStream()) {

				manual = manualStream.readAllBytes();
			}
			catch(Exception e) {

				System.out.println("Error al leer el manual: " + e);
			}
		}
		else {

			System.out.println("No se ha encontrado el manual en el classpath: " + MANUAL_PATH);
		}

		return manual;
	}

	public byte[] getCalendarForAYear(CalendarByYearDTO yearForCalendar) {

		return null;
	}

}
