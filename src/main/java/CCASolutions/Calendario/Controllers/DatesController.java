package CCASolutions.Calendario.Controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Services.DatesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/conversiontovau")
public class DatesController {

	@Autowired
	DatesService datesService;		
	
	@GetMapping("/selected")
	public ResponseEntity<DateDTO> getDateVAU(@RequestParam LocalDate date) {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();
		System.out.println("Fecha requerida: " + date);
		if(date.getYear() < 0 || 2099 < date.getYear()) {
			body.setFechaEncontrada(false);
			body.setMensaje("Error al obtener dateVAU: fecha fuera del rango: " + date);
			System.out.println(body.getMensaje());
		}
		else {
			
			try {			

				body = this.datesService.getDateVAUFromDateO(date);
				
				if(body == null) {
					status = HttpStatus.BAD_REQUEST;
				}
				System.out.println(date + " convertida con éxito");
				
			} catch (Exception e) {
				
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				body.setMensaje("Error:" + e);
				System.out.println(body.getMensaje());
			}
		}
		

		return new ResponseEntity<DateDTO>(body, status);
	}
	
	@GetMapping("/today")
	public ResponseEntity<DateDTO> getTodayVAU() {
		HttpStatus status = HttpStatus.OK;
		DateDTO body = new DateDTO();
		LocalDate today = LocalDate.now();
		System.out.println("Fecha hoy requerida: " + today);
		try {			

			body = this.datesService.getDateVAUFromDateO(today);
			System.out.println(today + " hoy convertida con éxito");
				
		} catch (Exception e) {
				
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			body.setMensaje("Error:" + e);
			System.out.println(body.getMensaje());
		}
		
		return new ResponseEntity<DateDTO>(body, status);
	}

}
