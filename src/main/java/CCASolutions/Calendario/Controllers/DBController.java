package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Services.DBService;
import CCASolutions.Calendario.Services.DatosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DBController {
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private DBService dbService;
	
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private DatosService datosService;

	@PostMapping("/poblatedb")
	public ResponseEntity<String> poblateDB(@RequestBody PoblateDBDTO poblateDBDTO) {
		HttpStatus status = HttpStatus.OK;
		String body = "Error al actualizar la base de datos.";
		
		if(poblateDBDTO.isPoblarDesdeCero()) {
			body = "No se puede poblar desde cero por petición.";
			status = HttpStatus.FORBIDDEN;
		}
		else {
			
			try {
				
				DatosEntity dbPassword = this.datosRepository.findByConcepto(this.datosService.getPWCode());
				
				if(dbPassword != null) {
					
					if(encoder.matches(poblateDBDTO.getPassword(), dbPassword.getValor())) {
						
						try {
							
							body = this.dbService.poblateDB(poblateDBDTO); 
						}
						catch(Exception e) {
							
							status = HttpStatus.INTERNAL_SERVER_ERROR;
							System.out.println(e);
						}
					}
					else {
						
						body = "No tienes permisos para realizar esta acción.";
						status = HttpStatus.UNAUTHORIZED;
					}
					
				}			
				else {
					
					System.out.println("No se ha encontrado la PW en la BD.");
					status = HttpStatus.INTERNAL_SERVER_ERROR;
				}		
			} 
			catch (Exception e) {
			
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				System.out.println(e);
			}
		}

		return new ResponseEntity<String>(body, status);
	}

}
