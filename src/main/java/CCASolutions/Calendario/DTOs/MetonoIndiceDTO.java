package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

/** Métono invernal nuevo para el selector del calendario. */
public class MetonoIndiceDTO {

	private Long id;
	private int numero;
	private String apellido;
	/** Primer día del métono (el siguiente al solsticio en que cae). */
	private LocalDate inicio;
	/** Día del métono siguiente (solsticio), o null si no lo hay en los datos. */
	private LocalDate fin;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public LocalDate getInicio() {
		return inicio;
	}
	public void setInicio(LocalDate inicio) {
		this.inicio = inicio;
	}
	public LocalDate getFin() {
		return fin;
	}
	public void setFin(LocalDate fin) {
		this.fin = fin;
	}
}
