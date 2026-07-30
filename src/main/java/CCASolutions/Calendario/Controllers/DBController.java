package CCASolutions.Calendario.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/*
 * ==============================================================================
 * EN: Administrative endpoint that (re)generates the calendar tables.
 *
 *     This is a long job - it can run for hours when it has to call the external
 *     ephemeris API - and it is guarded by the administrator password stored in the
 *     "datos" table.
 *
 *     Changes: a logger instead of System.out (an exception printed with
 *     System.out.println(e) loses its stack trace, which is exactly what you need
 *     when a three hour job fails), the nested try/catch flattened, and constructor
 *     injection.
 *
 * ES: Endpoint administrativo que (re)genera las tablas del calendario.
 *
 *     Es un trabajo largo - puede durar horas cuando tiene que llamar a la API externa de
 *     efemerides - y esta protegido por la contrasena de administrador guardada en la
 *     tabla "datos".
 *
 *     Cambios: un logger en lugar de System.out (una excepcion impresa con
 *     System.out.println(e) pierde su traza, que es justo lo que necesitas cuando falla un
 *     trabajo de tres horas), el try/catch anidado aplanado, e inyeccion por constructor.
 * ==============================================================================
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DBController {

	private static final Logger log = LoggerFactory.getLogger(DBController.class);

	/*
	 * EN: Key under which the administrator password hash lives in the "datos" table.
	 * ES: Clave bajo la que vive el hash de la contrasena de administrador en "datos".
	 */
	private static final String CONCEPTO_PASSWORD = "PW";

	private final BCryptPasswordEncoder encoder;
	private final DBService dbService;
	private final DatosRepository datosRepository;

	public DBController(BCryptPasswordEncoder encoder, DBService dbService, DatosRepository datosRepository) {
		this.encoder = encoder;
		this.dbService = dbService;
		this.datosRepository = datosRepository;
	}

	@PostMapping("/poblatedb")
	public ResponseEntity<String> poblateDB(@RequestBody PoblateDBDTO poblateDBDTO) {

		try {

			DatosEntity passwordEnBD = this.datosRepository.findByConcepto(CONCEPTO_PASSWORD);

			if (passwordEnBD == null) {
				log.error("No se ha encontrado la contraseña de administrador en la base de datos.");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error al actualizar la base de datos.");
			}

			if (!this.encoder.matches(poblateDBDTO.getPassword(), passwordEnBD.getValor())) {
				log.warn("Intento de poblar la base de datos con una contraseña incorrecta.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("No tienes permisos para realizar esta acción.");
			}

			// EN: Can take hours when the external ephemeris API is involved.
			// ES: Puede tardar horas cuando entra en juego la API externa de efemerides.
			log.info("Iniciando la población de la base de datos.");
			String resultado = this.dbService.poblateDB();
			log.info("Población de la base de datos terminada.");

			return ResponseEntity.ok(resultado);

		} catch (Exception e) {

			log.error("Error al poblar la base de datos", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al actualizar la base de datos.");
		}
	}
}
