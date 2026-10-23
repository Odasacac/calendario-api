package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

/** Mes fasal de un año VAU para el selector; fecha es su primer día dentro del año. */
public class MesIndiceDTO {

	private String nombre;
	private String apellido;
	private LocalDate fecha;

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
}
