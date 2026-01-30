package CCASolutions.Calendario.Controllers;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Services.DatesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DatesController {

	@Autowired
	DatesService datesService;
	
	
	@PostMapping("/conversion")
	public ResponseEntity <DateDTO> getDateVAU (LocalDateTime dateO){
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();
		
		try {
			body = this.datesService.getDateVAU(dateO);
		}
		catch (Exception e) {
			
		}
		
		return new ResponseEntity<DateDTO>(body, status);
	}
}
