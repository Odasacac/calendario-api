package CCASolutions.Calendario.DTOs;

/**
 * EN: Request body of the population endpoint: the administrator password plus the flags
 * choosing which phases to run. {@code llamadasAAPis} enables the downloads from OPALE,
 * {@code editar} the pairing of moon phases with apogees and perigees, and {@code poblar}
 * everything derived from them.
 * ES: Cuerpo de la petición del endpoint de poblado: la contraseña de administrador más las
 * banderas que eligen qué fases se ejecutan. {@code llamadasAAPis} habilita las descargas de
 * OPALE, {@code editar} el emparejamiento de fases lunares con apogeos y perigeos, y
 * {@code poblar} todo lo que se deriva de ello.
 */
public class PoblateDBDTO {

	private String password;
	private boolean llamadasAAPis;
	private boolean editar;
	private boolean poblar;
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isLlamadasAAPis() {
		return llamadasAAPis;
	}
	public void setLlamadasAAPis(boolean llamadasAAPis) {
		this.llamadasAAPis = llamadasAAPis;
	}
	public boolean isEditar() {
		return editar;
	}
	public void setEditar(boolean editar) {
		this.editar = editar;
	}
	public boolean isPoblar() {
		return poblar;
	}
	public void setPoblar(boolean poblar) {
		this.poblar = poblar;
	}	
}
