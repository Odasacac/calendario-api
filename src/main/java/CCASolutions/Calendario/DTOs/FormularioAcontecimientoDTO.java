package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos que el usuario rellena al crear o modificar un acontecimiento destacado. */
public class FormularioAcontecimientoDTO {

	@NotNull
	private LocalDate fecha;
	@NotBlank @Size(max=1000)
	private String descripcion;

	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
