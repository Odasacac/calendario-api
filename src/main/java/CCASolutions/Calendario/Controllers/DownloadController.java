package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;
import CCASolutions.Calendario.Services.DownloadService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DownloadController {
	
	@Autowired
	private DownloadService downloadService;

	private final static String MANUAL_FILENAME = "Manual_Calendario.pdf";

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
	
	@PostMapping("/getcalendar")
	public ResponseEntity<byte[]> getDateVAU(@RequestParam CalendarByYearDTO yearForCalendar) {
		
		HttpStatus status = HttpStatus.OK;
		byte[] body = new byte[0];
		
		try {
			
			this.downloadService.getCalendarForAYear(yearForCalendar);
		}
		catch(Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}		
		
		return new ResponseEntity<byte[]>(body, status);
	}

}
