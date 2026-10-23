package CCASolutions.Calendario.DTOs;

/**
 * Requisitos mínimos de contraseña, compartidos por registro, cambio y
 * recuperación. El máximo de 64 caracteres mantiene la contraseña por debajo
 * de los 72 bytes que BCrypt tiene en cuenta.
 */
public final class PoliticaContrasenya {

	public static final String PATRON = "^(?=.*\\p{L})(?=.*\\d)\\S{8,64}$";
	public static final String MENSAJE = "La contraseña debe tener entre 8 y 64 caracteres, sin espacios, con al menos una letra y un número.";

	private PoliticaContrasenya() {
	}
}
