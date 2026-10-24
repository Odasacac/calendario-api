package CCASolutions.Calendario.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.FormularioAcontecimientoDTO;
import CCASolutions.Calendario.DTOs.PaginaDTO;
import CCASolutions.Calendario.DTOs.AcontecimientoDestacadoDTO;
import CCASolutions.Calendario.Services.AcontecimientoDestacadoService;
import jakarta.validation.Valid;

/**
 * Acontecimientos destacados del usuario con sesión. Vive bajo /api/usuarios/me, así
 * que hereda la exigencia de JWT y el CORS restringido de SeguridadConfig.
 */
@RestController
@RequestMapping("/api/usuarios/me/acontecimientos")
public class AcontecimientoDestacadoController {

	@Autowired
	private AcontecimientoDestacadoService acontecimientoDestacadoService;

	/** Paginado: pagina desde 0 y tamanyo entre 5 y 50 (fuera de ese rango se ajusta). */
	@GetMapping
	public ResponseEntity<PaginaDTO<AcontecimientoDestacadoDTO>> listar(@AuthenticationPrincipal Jwt jwt,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamanyo) {
		return ResponseEntity.ok(this.acontecimientoDestacadoService.listar(idUsuario(jwt), pagina, tamanyo));
	}

	@GetMapping("/fechas")
	public ResponseEntity<List<LocalDate>> fechas(@AuthenticationPrincipal Jwt jwt) {
		return ResponseEntity.ok(this.acontecimientoDestacadoService.fechas(idUsuario(jwt)));
	}

	/** 200 con el acontecimiento de ese día, o 204 si no hay ninguno. */
	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<AcontecimientoDestacadoDTO> buscarPorFecha(@AuthenticationPrincipal Jwt jwt,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

		return this.acontecimientoDestacadoService.buscarPorFecha(idUsuario(jwt), fecha)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}

	@PostMapping
	public ResponseEntity<AcontecimientoDestacadoDTO> crear(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody FormularioAcontecimientoDTO formulario) {
		return new ResponseEntity<AcontecimientoDestacadoDTO>(this.acontecimientoDestacadoService.crear(idUsuario(jwt), formulario), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AcontecimientoDestacadoDTO> modificar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
			@Valid @RequestBody FormularioAcontecimientoDTO formulario) {
		return ResponseEntity.ok(this.acontecimientoDestacadoService.modificar(idUsuario(jwt), id, formulario));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		this.acontecimientoDestacadoService.eliminar(idUsuario(jwt), id);
		return ResponseEntity.noContent().build();
	}

	private static Long idUsuario(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}
}
