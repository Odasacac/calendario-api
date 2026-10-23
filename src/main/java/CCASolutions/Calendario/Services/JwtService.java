package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.SesionDTO;
import CCASolutions.Calendario.Entities.UsuarioEntity;

public interface JwtService {

	public static final String CLAIM_TIPO = "tipo";
	public static final String CLAIM_ROL = "rol";
	public static final String TIPO_SESION = "sesion";
	public static final String TIPO_RECUPERACION = "recuperacion";

	/** Sesión con token y caducidad (el perfil lo rellena quien la pida). */
	public abstract SesionDTO crearSesion(UsuarioEntity usuario);

	/**
	 * Token de un solo uso para cambiar la contraseña: lleva una huella del hash
	 * actual, así que deja de valer en cuanto la contraseña cambia.
	 */
	public abstract String crearTokenRecuperacion(UsuarioEntity usuario);

	/** Usuario dueño del token de recuperación; si no es válido lanza TOKEN_RECUPERACION_INVALIDO. */
	public abstract UsuarioEntity validarTokenRecuperacion(String token);
}
