package CCASolutions.Calendario.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * EN: Administration endpoint that populates the database from the OPALE APIs and from
 * the calculations derived from them. Protected by a single password stored hashed in
 * the {@code datos} table.
 * ES: Endpoint de administración que puebla la base de datos a partir de las APIs de
 * OPALE y de los cálculos que se derivan de ellas. Se protege con una única contraseña
 * almacenada cifrada en la tabla {@code datos}.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DBController {

	private static final Logger LOG = LoggerFactory.getLogger(DBController.class);

	private static final String CONCEPTO_PASSWORD = "PW";

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private DBService dbService;

	@Autowired
	private DatosRepository datosRepository;

	/**
	 * EN: Checks the administrator password and, if it matches, launches the population
	 * process with the phases requested in the body. Answers with the report of what each
	 * phase did.
	 * ES: Comprueba la contraseña de administrador y, si coincide, lanza el proceso de
	 * poblado con las fases que pida el cuerpo de la petición. Responde con el informe de
	 * lo que ha hecho cada fase.
	 *
	 * @param poblateDBDTO EN: password plus the flags choosing which phases to run. / ES: contraseña y las banderas que eligen qué fases ejecutar.
	 * @return EN: 200 with the report, 401 on a wrong password, 500 on failure. / ES: 200 con el informe, 401 si la contraseña no es correcta, 500 si falla.
	 */
	@PostMapping("/poblatedb")
	public ResponseEntity<String> poblateDB(@RequestBody PoblateDBDTO poblateDBDTO) {

		try {

			DatosEntity dbPassword = this.datosRepository.findByConcepto(CONCEPTO_PASSWORD);

			if(dbPassword == null) {

				LOG.error("No se ha encontrado la PW en la BD.");
				return new ResponseEntity<>("Error al actualizar la base de datos.", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if(!encoder.matches(poblateDBDTO.getPassword(), dbPassword.getValor())) {

				LOG.warn("Intento de poblar la base de datos con contraseña incorrecta.");
				return new ResponseEntity<>("No tienes permisos para realizar esta acción.", HttpStatus.UNAUTHORIZED);
			}

			return new ResponseEntity<>(this.dbService.poblateDB(poblateDBDTO), HttpStatus.OK);
		}
		catch (Exception e) {

			LOG.error("Error al poblar la base de datos", e);
			return new ResponseEntity<>("Error al actualizar la base de datos.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
