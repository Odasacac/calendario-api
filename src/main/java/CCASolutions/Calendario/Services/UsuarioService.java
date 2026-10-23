package CCASolutions.Calendario.Services;

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
import CCASolutions.Calendario.Entities.UsuarioEntity;

public interface UsuarioService {

	/** Crea la cuenta y deja al usuario con la sesión iniciada. */
	public abstract SesionDTO registrar(RegistroUsuarioDTO registro);

	/** Login por correo o por código de usuario. */
	public abstract SesionDTO login(LoginDTO login);

	/** Paso 1 de la recuperación: comprueba que código de usuario y correo son de la misma cuenta. */
	public abstract TokenRecuperacionDTO verificarRecuperacion(VerificacionRecuperacionDTO verificacion);

	/** Paso 2 de la recuperación: fija la nueva contraseña. */
	public abstract void restablecerContrasenya(RestablecimientoContrasenyaDTO restablecimiento);

	public abstract PerfilUsuarioDTO getPerfil(Long idUsuario);

	/**
	 * Usuario dueño de la sesión, siempre que pueda seguir usando la cuenta
	 * (activo o inactivo); si no, lanza SESION_NO_VALIDA.
	 */
	public abstract UsuarioEntity getUsuarioConSesion(Long idUsuario);

	/** Cambiar nombre, apellido o fecha de nacimiento recalcula el código de usuario. */
	public abstract PerfilUsuarioDTO editarPerfil(Long idUsuario, EdicionPerfilDTO edicion);

	public abstract void cambiarContrasenya(Long idUsuario, CambioContrasenyaDTO cambio);

	/** Suspensión voluntaria: se levanta con el siguiente login antes de que el cron la elimine. */
	public abstract void suspenderCuenta(Long idUsuario);

	public abstract void eliminarCuenta(Long idUsuario, EliminacionCuentaDTO eliminacion);

	/**
	 * Revisión diaria de estados: suspendidos de más de 30 días pasan a
	 * eliminados; sin login en más de un año, a suspendidos; en más de 30 días,
	 * a inactivos. También recalcula la mayoría de edad.
	 *
	 * @return número de usuarios modificados
	 */
	public abstract int mantenimientoDiario();
}
