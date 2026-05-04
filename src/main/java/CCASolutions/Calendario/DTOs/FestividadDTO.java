package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class FestividadDTO {

	private String nombre;
	
	private LocalDateTime date;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
	
	
}
