package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.Services.EclipsesService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class EclipsesController {

	@Autowired
	private EclipsesService eclipsesService;
	
	@GetMapping("/poblateeclipses")
	public ResponseEntity<String> poblateEclipses() {
		HttpStatus status = HttpStatus.OK;
		String body = "Error al actualizar los eclipses.";

		try {
			
			body = this.eclipsesService.poblateEclipses();
		} 
		catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<String>(body, status);
	}
}
