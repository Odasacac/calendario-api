package CCASolutions.Calendario.Exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

/**
 * Error de negocio de la gestión de usuarios. El `codigo` es estable y es lo
 * que el front usa para mostrar el mensaje traducido; `mensaje` es solo una
 * ayuda en castellano.
 */
public class UsuarioException extends RuntimeException {

	private static final long serialVersionUID = 6043218850932713448L;

	public static final String CORREO_EXISTENTE = "CORREO_EXISTENTE";
	public static final String CREDENCIALES_INVALIDAS = "CREDENCIALES_INVALIDAS";
	public static final String DEMASIADOS_INTENTOS = "DEMASIADOS_INTENTOS";
	public static final String CUENTA_SUSPENDIDA = "CUENTA_SUSPENDIDA";
	public static final String CUENTA_ELIMINADA = "CUENTA_ELIMINADA";
	public static final String DATOS_RECUPERACION_INVALIDOS = "DATOS_RECUPERACION_INVALIDOS";
	public static final String TOKEN_RECUPERACION_INVALIDO = "TOKEN_RECUPERACION_INVALIDO";
	public static final String CONTRASENYAS_NO_COINCIDEN = "CONTRASENYAS_NO_COINCIDEN";
	public static final String CONTRASENYA_ACTUAL_INCORRECTA = "CONTRASENYA_ACTUAL_INCORRECTA";
	public static final String CONTRASENYA_REPETIDA = "CONTRASENYA_REPETIDA";
	public static final String FECHA_NACIMIENTO_INVALIDA = "FECHA_NACIMIENTO_INVALIDA";
	public static final String SESION_NO_VALIDA = "SESION_NO_VALIDA";

	private final HttpStatus status;
	private final String codigo;
	private final Map<String, Object> detalles = new LinkedHashMap<>();

	public UsuarioException(HttpStatus status, String codigo, String mensaje) {
		super(mensaje);
		this.status = status;
		this.codigo = codigo;
	}

	public UsuarioException conDetalle(String clave, Object valor) {
		this.detalles.put(clave, valor);
		return this;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCodigo() {
		return codigo;
	}

	public Map<String, Object> getDetalles() {
		return detalles;
	}
}
