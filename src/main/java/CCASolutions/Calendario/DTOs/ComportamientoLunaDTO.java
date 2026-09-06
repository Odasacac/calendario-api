package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

/**
 * EN: How the moon is behaving: whether it has just reached its farthest or closest point,
 * or how many days it has been drawing closer or moving away, already worded for display.
 * ES: Cómo se está comportando la luna: si acaba de alcanzar su punto más lejano o más
 * cercano, o cuántos días lleva acercándose o alejándose, ya redactado para mostrarlo.
 */
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
