package CCASolutions.Calendario.Services;

public interface CodigoUsuarioService {

	/**
	 * Primer código libre de la secuencia inicial+apellido, inicial+apellido+año,
	 * inicial+apellido+año_1, _2... (p. ej. ccasado, ccasado1993, ccasado1993_1).
	 *
	 * @param idExcluido usuario cuyo código actual no cuenta como ocupado (al
	 *                   editar el perfil); null al registrar.
	 */
	public abstract String generar(String nombre, String apellido, int anyoNacimiento, Long idExcluido);

	/** Minúsculas, sin tildes ni caracteres fuera de [a-z0-9]. */
	public abstract String limpiar(String texto);
}
