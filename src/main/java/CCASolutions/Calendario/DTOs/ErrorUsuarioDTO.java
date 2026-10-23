package CCASolutions.Calendario.DTOs;

import java.util.Map;

public class ErrorUsuarioDTO {

	private String codigo;
	private String mensaje;
	private Map<String, Object> detalles;

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Map<String, Object> getDetalles() {
		return detalles;
	}
	public void setDetalles(Map<String, Object> detalles) {
		this.detalles = detalles;
	}
}
