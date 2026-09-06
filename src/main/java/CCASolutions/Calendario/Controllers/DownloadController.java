package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	
	@GetMapping("/getpdf")
	public ResponseEntity<byte[]> getPDF() {
		
		HttpStatus status = HttpStatus.OK;
		byte[] body = new byte[0];
		
		try {
			
			body = this.downloadService.getPDF();
			if(body == null) {
				status = HttpStatus.BAD_REQUEST;
			}
			
		}
		catch(Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}
		
		return new ResponseEntity<byte[]>(body, status);
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
