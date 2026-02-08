package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class SolsticiosYEquinocciosController {

	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@GetMapping("/poblatesoes")
	public ResponseEntity<String> getDateVAU() {
		HttpStatus status = HttpStatus.OK;
		String body = "Error al actualizar los solsticios y equinoccios.";

		try {
			
			body = this.solsticiosYEquinocciosService.poblateSolsticiosYEquinoccios();
					
		} catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			System.out.println(e);
		}

		return new ResponseEntity<String>(body, status);
	}
}
