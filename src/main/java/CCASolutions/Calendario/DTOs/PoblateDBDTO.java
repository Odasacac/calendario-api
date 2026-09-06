package CCASolutions.Calendario.DTOs;

public class PoblateDBDTO {

	private String password;
	private boolean poblarDesdeCero;
	private boolean vacia;
	private boolean llamadasAAPis;
	private boolean editar;
	private boolean poblar;
	
	public PoblateDBDTO(boolean poblarDesdeCero, boolean vacia) {
		
		this.setPoblarDesdeCero(poblarDesdeCero);
		this.setVacia(vacia);
		 if(poblarDesdeCero) {
			 this.llamadasAAPis = true;
			 this.editar=true;
			 this.poblar=true;
		 }
	}
	
	
	public boolean isVacia() {
		return vacia;
	}
	public void setVacia(boolean vacia) {
		this.vacia = vacia;
	}
	public boolean isPoblarDesdeCero() {
		return poblarDesdeCero;
	}
	public void setPoblarDesdeCero(boolean poblarDesdeCero) {
		this.poblarDesdeCero = poblarDesdeCero;
	}
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
