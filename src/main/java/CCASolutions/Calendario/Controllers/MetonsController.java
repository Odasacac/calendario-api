package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.Services.MetonsService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class MetonsController {

	@Autowired
	private MetonsService metonsService;
	
	@GetMapping("/checkmetons")
	public ResponseEntity<String> getDateVAU(@RequestParam int since, @RequestParam int to) {
		HttpStatus status = HttpStatus.OK;
		String body = "Error al actualizar los metonos.";

		try {
			
			body = this.metonsService.checkMetonosSinceTo(since, to);
		} catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<String>(body, status);
	}

}
