package CCASolutions.Calendario.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;
import CCASolutions.Calendario.Services.DownloadService;

/*
 * ==============================================================================
 * EN: Download endpoints. Both are still stubs: DownloadServiceImpl returns null for
 *     the PDF and for the yearly calendar, so these answer 200 with an empty body.
 *     They are left in place because they are part of the published API surface.
 *
 *     Changes: a logger instead of System.out, the result of the service is actually
 *     returned instead of being discarded, and /getcalendar now takes its DTO from
 *     the request body. As a @RequestParam on a complex type it could not bind at all,
 *     so the endpoint could only ever fail.
 *
 * ES: Endpoints de descarga. Los dos siguen siendo esbozos: DownloadServiceImpl devuelve
 *     null para el PDF y para el calendario anual, asi que estos responden 200 con cuerpo
 *     vacio. Se mantienen porque forman parte de la superficie publicada de la API.
 *
 *     Cambios: un logger en lugar de System.out, el resultado del servicio se devuelve de
 *     verdad en lugar de descartarse, y /getcalendar toma ahora su DTO del cuerpo de la
 *     peticion. Como @RequestParam sobre un tipo complejo no podia enlazarse, el endpoint
 *     solo podia fallar.
 * ==============================================================================
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class DownloadController {

	private static final Logger log = LoggerFactory.getLogger(DownloadController.class);

	private static final byte[] SIN_CONTENIDO = new byte[0];

	private final DownloadService downloadService;

	public DownloadController(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	@GetMapping("/getpdf")
	public ResponseEntity<byte[]> getPDF() {

		try {
			byte[] pdf = this.downloadService.getPDF();
			return ResponseEntity.ok(pdf == null ? SIN_CONTENIDO : pdf);

		} catch (Exception e) {
			log.error("Error al generar el PDF", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SIN_CONTENIDO);
		}
	}

	@PostMapping("/getcalendar")
	public ResponseEntity<byte[]> getCalendar(@RequestBody CalendarByYearDTO yearForCalendar) {

		try {
			byte[] calendario = this.downloadService.getCalendarForAYear(yearForCalendar);
			return ResponseEntity.ok(calendario == null ? SIN_CONTENIDO : calendario);

		} catch (Exception e) {
			log.error("Error al generar el calendario del año solicitado", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SIN_CONTENIDO);
		}
	}
}
