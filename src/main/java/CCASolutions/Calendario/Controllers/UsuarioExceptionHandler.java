package CCASolutions.Calendario.Controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import CCASolutions.Calendario.DTOs.ErrorUsuarioDTO;
import CCASolutions.Calendario.Exceptions.UsuarioException;

/** Respuestas de error con forma {codigo, mensaje, detalles}. Solo afecta a UsuarioController. */
@RestControllerAdvice(assignableTypes = UsuarioController.class)
public class UsuarioExceptionHandler {

	public static final String VALIDACION = "VALIDACION";
	public static final String ERROR_INTERNO = "ERROR_INTERNO";

	@ExceptionHandler(UsuarioException.class)
	public ResponseEntity<ErrorUsuarioDTO> usuario(UsuarioException e) {
		return respuesta(e.getStatus(), e.getCodigo(), e.getMessage(), e.getDetalles());
	}

	/** Detalles = campo -> mensaje, para que el front marque cada campo. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorUsuarioDTO> validacion(MethodArgumentNotValidException e) {

		Map<String, Object> campos = new LinkedHashMap<>();
		e.getBindingResult().getFieldErrors().forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
		return respuesta(HttpStatus.BAD_REQUEST, VALIDACION, "Hay campos con valores no válidos.", campos);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorUsuarioDTO> cuerpoIlegible(HttpMessageNotReadableException e) {
		return respuesta(HttpStatus.BAD_REQUEST, VALIDACION, "La petición no tiene un formato válido.", Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorUsuarioDTO> inesperado(Exception e) {

		// El detalle se queda en el log: al cliente no se le dan pistas internas.
		System.out.println("Error inesperado en /api/usuarios: " + e);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNO, "Ha ocurrido un error inesperado.", Map.of());
	}

	private static ResponseEntity<ErrorUsuarioDTO> respuesta(HttpStatus status, String codigo, String mensaje, Map<String, Object> detalles) {

		ErrorUsuarioDTO cuerpo = new ErrorUsuarioDTO();
		cuerpo.setCodigo(codigo);
		cuerpo.setMensaje(mensaje);
		cuerpo.setDetalles(detalles);
		return new ResponseEntity<ErrorUsuarioDTO>(cuerpo, status);
	}
}
