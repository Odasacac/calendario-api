package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

/** Año VAU dentro de un métono: del día siguiente a un solsticio de invierno al solsticio siguiente. */
public class AnyoIndiceDTO {

	private int numero;
	private LocalDate inicio;
	private LocalDate fin;

	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
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
