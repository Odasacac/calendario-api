package CCASolutions.Calendario.Services;

/**
 * EN: Manages the {@code datos} table, the key/value store holding the OPALE API URLs
 * and the administrator password.
 * ES: Gestiona la tabla {@code datos}, el almacén de clave/valor donde están las URLs de
 * las APIs de OPALE y la contraseña de administrador.
 */
public interface DatosService {

	/**
	 * EN: Inserts the five OPALE API URLs. Only runs when the table holds nothing but the
	 * administrator password row.
	 * ES: Inserta las cinco URLs de las APIs de OPALE. Sólo se ejecuta cuando la tabla no
	 * contiene más que la fila de la contraseña de administrador.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateDatos();
}
