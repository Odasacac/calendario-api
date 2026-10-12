package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.Services.DownloadService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DownloadController {

	@Autowired
	private DownloadService downloadService;

	private final static String MANUAL_FILENAME = "Manual_Calendario.pdf";
	private final static String CALENDAR_FILENAME_PREFIX = "Calendario_VAU_";

	@GetMapping("/getpdf")
	@CrossOrigin(origins = "*", exposedHeaders = HttpHeaders.CONTENT_DISPOSITION)
	public ResponseEntity<byte[]> getManual() {

		HttpStatus status = HttpStatus.OK;
		HttpHeaders headers = new HttpHeaders();
		byte[] body = new byte[0];

		try {

			body = this.downloadService.getManual();
			if(body == null) {
				status = HttpStatus.NOT_FOUND;
				body = new byte[0];
			}
			else {
				headers.setContentType(MediaType.APPLICATION_PDF);
				headers.setContentDisposition(ContentDisposition.attachment().filename(MANUAL_FILENAME).build());
				headers.setContentLength(body.length);
			}

		}
		catch(Exception e) {

			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<byte[]>(body, headers, status);
	}

	@GetMapping("/getcalendar")
	@CrossOrigin(origins = "*", exposedHeaders = HttpHeaders.CONTENT_DISPOSITION)
	public ResponseEntity<byte[]> getDateVAU(@RequestParam int yearForCalendar) {

		HttpStatus status = HttpStatus.OK;
		HttpHeaders headers = new HttpHeaders();
		byte[] body = new byte[0];

		if(yearForCalendar > 1433 && yearForCalendar < 2100) {

			try {

				body = this.downloadService.getCalendarForAYear(yearForCalendar);
				if(body == null) {
					status = HttpStatus.NOT_FOUND;
					body = new byte[0];
				}
				else {
					headers.setContentType(MediaType.APPLICATION_PDF);
					headers.setContentDisposition(ContentDisposition.attachment().filename(CALENDAR_FILENAME_PREFIX + yearForCalendar + ".pdf").build());
					headers.setContentLength(body.length);
				}
			}
			catch(Exception e) {

				status = HttpStatus.INTERNAL_SERVER_ERROR;
				body = new byte[0];
				System.out.println(e);
			}
		}
		else {

			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<byte[]>(body, headers, status);
	}

}
