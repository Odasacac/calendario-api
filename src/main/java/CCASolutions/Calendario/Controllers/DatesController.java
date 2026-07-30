package CCASolutions.Calendario.Controllers;

import java.time.Duration;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Services.DatesService;

/*
 * ==============================================================================
 * EN: Conversion endpoints from an ordinary date to a VAU date.
 *
 *     What changed:
 *
 *       - System.out.println gave way to a logger. Every println takes a lock on the
 *         console and writes synchronously, so under concurrent load the request
 *         threads were queueing behind stdout; and there were three of them per
 *         request.
 *       - Successful responses now carry a Cache-Control header. The conversion of a
 *         given day never changes, so browsers and proxies can keep it instead of
 *         asking again.
 *       - Constructor injection, and the response is built without allocating a
 *         DateDTO that is then thrown away.
 *
 * ES: Endpoints de conversion de una fecha ordinaria a una fecha VAU.
 *
 *     Que ha cambiado:
 *
 *       - System.out.println da paso a un logger. Cada println toma un cerrojo sobre la
 *         consola y escribe de forma sincrona, asi que bajo carga concurrente los hilos
 *         de peticion hacian cola detras de stdout; y habia tres por peticion.
 *       - Las respuestas correctas llevan ahora una cabecera Cache-Control. La conversion
 *         de un dia dado no cambia nunca, asi que navegadores y proxies pueden guardarla
 *         en lugar de volver a preguntar.
 *       - Inyeccion por constructor, y la respuesta se construye sin crear un DateDTO que
 *         luego se descarta.
 * ==============================================================================
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/conversiontovau")
public class DatesController {

	private static final Logger log = LoggerFactory.getLogger(DatesController.class);

	/*
	 * EN: Range of years the generated ephemeris covers.
	 * ES: Rango de anos que cubren las efemerides generadas.
	 */
	private static final int PRIMER_ANYO = 0;
	private static final int ULTIMO_ANYO = 2099;

	/*
	 * EN: A specific day never changes, so it can be cached for a long time. "Today"
	 *     changes at midnight, so it is only cached for a few minutes.
	 * ES: Un dia concreto no cambia nunca, asi que se puede cachear mucho tiempo. "Hoy"
	 *     cambia a medianoche, asi que solo se cachea unos minutos.
	 */
	private static final CacheControl CACHE_DE_UN_DIA_CONCRETO = CacheControl
			.maxAge(Duration.ofHours(12)).cachePublic();
	private static final CacheControl CACHE_DE_HOY = CacheControl
			.maxAge(Duration.ofMinutes(5)).cachePublic();

	private final DatesService datesService;

	public DatesController(DatesService datesService) {
		this.datesService = datesService;
	}

	/*
	 * EN: Converts the requested date. Dates outside the generated range are answered
	 *     with 200 and an explanatory message, which is what the clients already expect.
	 * ES: Convierte la fecha solicitada. Las fechas fuera del rango generado se responden
	 *     con 200 y un mensaje explicativo, que es lo que ya esperan los clientes.
	 */
	@GetMapping("/selected")
	public ResponseEntity<DateDTO> getDateVAU(@RequestParam LocalDate date) {

		log.info("Fecha requerida: {}", date);

		if (date.getYear() < PRIMER_ANYO || ULTIMO_ANYO < date.getYear()) {

			DateDTO fueraDeRango = new DateDTO();
			fueraDeRango.setFechaEncontrada(false);
			fueraDeRango.setMensaje("Error al obtener dateVAU: fecha fuera del rango: " + date);
			fueraDeRango.setFechaO(String.valueOf(date));

			log.info("{}", fueraDeRango.getMensaje());

			return ResponseEntity.ok(fueraDeRango);
		}

		try {

			DateDTO fechaVAU = this.datesService.getDateVAUFromDateO(date);
			log.info("{} convertida con éxito", date);

			return ResponseEntity.ok().cacheControl(CACHE_DE_UN_DIA_CONCRETO).body(fechaVAU);

		} catch (Exception e) {

			log.error("Error al convertir la fecha {}", date, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(deError(date, e));
		}
	}

	/*
	 * EN: Converts today's date.
	 * ES: Convierte la fecha de hoy.
	 */
	@GetMapping("/today")
	public ResponseEntity<DateDTO> getTodayVAU() {

		LocalDate hoy = LocalDate.now();
		log.info("Fecha hoy requerida: {}", hoy);

		try {

			DateDTO fechaVAU = this.datesService.getDateVAUFromDateO(hoy);
			log.info("{} hoy convertida con éxito", hoy);

			return ResponseEntity.ok().cacheControl(CACHE_DE_HOY).body(fechaVAU);

		} catch (Exception e) {

			log.error("Error al convertir la fecha de hoy {}", hoy, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(deError(hoy, e));
		}
	}

	/*
	 * EN: Error body. The message keeps the original "Error:" + exception shape so
	 *     existing clients do not have to change.
	 * ES: Cuerpo de error. El mensaje conserva la forma original "Error:" + excepcion para
	 *     que los clientes existentes no tengan que cambiar.
	 */
	private static DateDTO deError(LocalDate fecha, Exception e) {
		DateDTO error = new DateDTO();
		error.setMensaje("Error:" + e);
		error.setFechaO(String.valueOf(fecha));
		return error;
	}
}
