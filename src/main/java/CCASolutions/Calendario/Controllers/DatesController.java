package CCASolutions.Calendario.Controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.Services.DatesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DatesController {

	@Autowired
	DatesService datesService;		
	
	@GetMapping("/conversiontovau")
	public ResponseEntity<DateDTO> getDateVAU(@RequestParam LocalDateTime dateO) {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();

		if(dateO.getYear() < 0 || 2099 < dateO.getYear()) {
			
			status = HttpStatus.BAD_REQUEST;
		}
		else {
			
			try {			

				body = this.datesService.getDateVAUFromDateO(dateO);
				
			} catch (Exception e) {
				
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				System.out.println(e);
			}
		}
		

		return new ResponseEntity<DateDTO>(body, status);
	}

	@PostMapping("/conversiontoofficial")
	public ResponseEntity<LocalDateTime> getDateO(@RequestBody DateDTO dateVAU) {
		HttpStatus status = HttpStatus.OK;
		LocalDateTime body = LocalDateTime.now();

		DateDTOFromDB dateDTOFromDB = this.datesService.getDateDTOFromDB(dateVAU);
		
		if(dateDTOFromDB.isValid()) {
			
			try {			

				body = this.datesService.getDateOFromDateVAU(dateDTOFromDB);
					
			} catch (Exception e) {
					
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				System.out.println(e);
			}	
		}
			
		else {
			
			status = HttpStatus.BAD_REQUEST;
		}
					
		

		return new ResponseEntity<LocalDateTime>(body, status);
	}
	
	
	
	@GetMapping("/nowtovau")
	public ResponseEntity<DateDTO> getTodayVAU() {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();

		LocalDateTime dateO = LocalDateTime.now();

		try {
			
			body = this.datesService.getDateVAUFromDateO(dateO);			
		} 
		catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<DateDTO>(body, status);
	}
}
