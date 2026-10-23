package CCASolutions.Calendario.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.CambioContrasenyaDTO;
import CCASolutions.Calendario.DTOs.EdicionPerfilDTO;
import CCASolutions.Calendario.DTOs.EliminacionCuentaDTO;
import CCASolutions.Calendario.DTOs.LoginDTO;
import CCASolutions.Calendario.DTOs.PerfilUsuarioDTO;
import CCASolutions.Calendario.DTOs.RegistroUsuarioDTO;
import CCASolutions.Calendario.DTOs.RestablecimientoContrasenyaDTO;
import CCASolutions.Calendario.DTOs.SesionDTO;
import CCASolutions.Calendario.DTOs.TokenRecuperacionDTO;
import CCASolutions.Calendario.DTOs.VerificacionRecuperacionDTO;
import CCASolutions.Calendario.Services.UsuarioService;
import jakarta.validation.Valid;

/**
 * Sin @CrossOrigin: el CORS de /api/usuarios lo define SeguridadConfig con
 * una lista cerrada de orígenes. Los errores los traduce UsuarioExceptionHandler.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/registro")
	public ResponseEntity<SesionDTO> registrar(@Valid @RequestBody RegistroUsuarioDTO registro) {
		return new ResponseEntity<SesionDTO>(this.usuarioService.registrar(registro), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<SesionDTO> login(@Valid @RequestBody LoginDTO login) {
		return ResponseEntity.ok(this.usuarioService.login(login));
	}

	@PostMapping("/recuperacion/verificar")
	public ResponseEntity<TokenRecuperacionDTO> verificarRecuperacion(@Valid @RequestBody VerificacionRecuperacionDTO verificacion) {
		return ResponseEntity.ok(this.usuarioService.verificarRecuperacion(verificacion));
	}

	@PostMapping("/recuperacion/restablecer")
	public ResponseEntity<Void> restablecerContrasenya(@Valid @RequestBody RestablecimientoContrasenyaDTO restablecimiento) {
		this.usuarioService.restablecerContrasenya(restablecimiento);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public ResponseEntity<PerfilUsuarioDTO> getPerfil(@AuthenticationPrincipal Jwt jwt) {
		return ResponseEntity.ok(this.usuarioService.getPerfil(idUsuario(jwt)));
	}

	@PutMapping("/me")
	public ResponseEntity<PerfilUsuarioDTO> editarPerfil(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody EdicionPerfilDTO edicion) {
		return ResponseEntity.ok(this.usuarioService.editarPerfil(idUsuario(jwt), edicion));
	}

	@PutMapping("/me/contrasenya")
	public ResponseEntity<Void> cambiarContrasenya(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CambioContrasenyaDTO cambio) {
		this.usuarioService.cambiarContrasenya(idUsuario(jwt), cambio);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/me/suspender")
	public ResponseEntity<Void> suspenderCuenta(@AuthenticationPrincipal Jwt jwt) {
		this.usuarioService.suspenderCuenta(idUsuario(jwt));
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/me/eliminar")
	public ResponseEntity<Void> eliminarCuenta(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody EliminacionCuentaDTO eliminacion) {
		this.usuarioService.eliminarCuenta(idUsuario(jwt), eliminacion);
		return ResponseEntity.noContent().build();
	}

	private static Long idUsuario(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}
}
