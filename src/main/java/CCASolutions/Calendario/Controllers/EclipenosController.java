package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.Services.EclipenosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class EclipenosController {
	
	@Autowired
	private EclipenosService eclipenosService;
	
	@GetMapping("/poblateeclipenos")
	public ResponseEntity<String> getDateVAU() {
		HttpStatus status = HttpStatus.OK;
		String body = "Error al actualizar los eclipenos.";

		try {
			
			body = this.eclipenosService.poblateEclipenos();
		} catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<String>(body, status);
	}
}
