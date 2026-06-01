package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class ComportamientoLunaDTO {
	
	private String direccion;
	private LocalDateTime date;
	

	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}	
}
