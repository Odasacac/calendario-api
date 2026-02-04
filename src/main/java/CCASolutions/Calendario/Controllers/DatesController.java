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
import CCASolutions.Calendario.DTOs.DateWithEntitiesDTO;
import CCASolutions.Calendario.Services.DatesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DatesController {

	@Autowired
	DatesService datesService;
	
	@PostMapping("/conversiontoofficial")
	public ResponseEntity<LocalDateTime> getOfficialDate(@RequestBody DateDTO date) {
		
		HttpStatus status = HttpStatus.OK;
		LocalDateTime body = LocalDateTime.now();
		
		DateWithEntitiesDTO dateDB = this.datesService.validateDTO(date);

		if(dateDB.isValidated()) {
			
			try {
				
				body = this.datesService.getDateOFromDateVAU(date);
			} 
			catch (Exception e) {
				
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				System.out.println(e);
			}
		}
		else {
			
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<LocalDateTime>(body, status);
	}

	
	
	
	@GetMapping("/conversiontovau")
	public ResponseEntity<DateDTO> getDateVAU(@RequestParam LocalDateTime date) {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();

		try {
			body = this.datesService.getDateVAUFromDateO(date);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<DateDTO>(body, status);
	}

	
	
	
	@GetMapping("/nowtovau")
	public ResponseEntity<DateDTO> getTodayVAU() {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();

		LocalDateTime dateO = LocalDateTime.now();

		try {
			body = this.datesService.getDateVAUFromDateO(dateO);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<DateDTO>(body, status);
	}
}
