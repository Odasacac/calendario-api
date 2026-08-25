package CCASolutions.Calendario.DTOs;

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
