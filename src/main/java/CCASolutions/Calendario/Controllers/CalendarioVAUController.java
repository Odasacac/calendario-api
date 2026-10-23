package CCASolutions.Calendario.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.AnyoIndiceDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MesIndiceDTO;
import CCASolutions.Calendario.DTOs.MetonoIndiceDTO;
import CCASolutions.Calendario.Services.CalendarioVAUService;

/**
 * Calendario VAU por lunaciones. Público, como el resto de la conversión de
 * fechas. Los datos no cambian, así que el navegador puede guardarlos un día.
 */
@RestController
@RequestMapping("/api/calendario")
public class CalendarioVAUController {

	private static final CacheControl UN_DIA = CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic();

	@Autowired
	private CalendarioVAUService calendarioVAUService;

	@GetMapping("/lunacion")
	public ResponseEntity<LunacionDTO> getLunacion(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return ResponseEntity.ok().cacheControl(UN_DIA).body(this.calendarioVAUService.getLunacion(fecha));
	}

	@GetMapping("/metonos")
	public ResponseEntity<List<MetonoIndiceDTO>> getMetonos() {
		return ResponseEntity.ok().cacheControl(UN_DIA).body(this.calendarioVAUService.getMetonos());
	}

	@GetMapping("/metonos/{id}/anyos")
	public ResponseEntity<List<AnyoIndiceDTO>> getAnyos(@PathVariable Long id) {
		return ResponseEntity.ok().cacheControl(UN_DIA).body(this.calendarioVAUService.getAnyos(id));
	}

	@GetMapping("/meses")
	public ResponseEntity<List<MesIndiceDTO>> getMeses(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
		return ResponseEntity.ok().cacheControl(UN_DIA).body(this.calendarioVAUService.getMeses(inicio, fin));
	}
}
