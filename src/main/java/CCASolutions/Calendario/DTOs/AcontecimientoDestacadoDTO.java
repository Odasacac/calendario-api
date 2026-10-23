package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AcontecimientoDestacadoDTO {

	private Long id;
	private LocalDate fecha;
	private String descripcion;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaModificacion;
	/** true si ese día hay un evento reseñable del calendario; su nombre va en notableEventType. */
	private boolean notableEvent;
	private String notableEventType;

	public boolean isNotableEvent() {
		return notableEvent;
	}
	public void setNotableEvent(boolean notableEvent) {
		this.notableEvent = notableEvent;
	}
	public String getNotableEventType() {
		return notableEventType;
	}
	public void setNotableEventType(String notableEventType) {
		this.notableEventType = notableEventType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
}
