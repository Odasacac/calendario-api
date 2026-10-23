package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;
import java.util.List;

/** Una lunación (de luna nueva a la víspera de la siguiente) organizada como el calendario VAU en PDF. */
public class LunacionDTO {

	private LocalDate inicio;
	private LocalDate fin;
	private List<DiaCalendarioDTO> dias;
	/** Normalmente uno; dos si la lunación cambia de mes fasal. */
	private List<MesLunacionDTO> meses;
	/** Estaciones que toca, en orden; más de una = mes híbrido. */
	private List<Integer> estaciones;
	/** Nombre de cada semana por su número (0 = Terra). */
	private List<String> semanas;
	/** Nombres de los 7 días de la semana VAU, de Luno a Saturino. */
	private List<String> diasSemana;
	private String anyo;
	/** Distinto de anyo si la lunación cruza el solsticio de invierno. */
	private String anyoFinal;
	private Integer metono;
	private String apellidoMetono;
	/** Solsticio de invierno dentro de la lunación, si lo hay. */
	private LocalDate cambioDeAnyo;
	/** Un día de la lunación anterior; null si queda fuera del rango navegable. */
	private LocalDate anterior;
	/** Un día de la lunación siguiente; null si queda fuera del rango navegable. */
	private LocalDate siguiente;

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
	public List<DiaCalendarioDTO> getDias() {
		return dias;
	}
	public void setDias(List<DiaCalendarioDTO> dias) {
		this.dias = dias;
	}
	public List<MesLunacionDTO> getMeses() {
		return meses;
	}
	public void setMeses(List<MesLunacionDTO> meses) {
		this.meses = meses;
	}
	public List<Integer> getEstaciones() {
		return estaciones;
	}
	public void setEstaciones(List<Integer> estaciones) {
		this.estaciones = estaciones;
	}
	public List<String> getSemanas() {
		return semanas;
	}
	public void setSemanas(List<String> semanas) {
		this.semanas = semanas;
	}
	public List<String> getDiasSemana() {
		return diasSemana;
	}
	public void setDiasSemana(List<String> diasSemana) {
		this.diasSemana = diasSemana;
	}
	public String getAnyo() {
		return anyo;
	}
	public void setAnyo(String anyo) {
		this.anyo = anyo;
	}
	public String getAnyoFinal() {
		return anyoFinal;
	}
	public void setAnyoFinal(String anyoFinal) {
		this.anyoFinal = anyoFinal;
	}
	public Integer getMetono() {
		return metono;
	}
	public void setMetono(Integer metono) {
		this.metono = metono;
	}
	public String getApellidoMetono() {
		return apellidoMetono;
	}
	public void setApellidoMetono(String apellidoMetono) {
		this.apellidoMetono = apellidoMetono;
	}
	public LocalDate getCambioDeAnyo() {
		return cambioDeAnyo;
	}
	public void setCambioDeAnyo(LocalDate cambioDeAnyo) {
		this.cambioDeAnyo = cambioDeAnyo;
	}
	public LocalDate getAnterior() {
		return anterior;
	}
	public void setAnterior(LocalDate anterior) {
		this.anterior = anterior;
	}
	public LocalDate getSiguiente() {
		return siguiente;
	}
	public void setSiguiente(LocalDate siguiente) {
		this.siguiente = siguiente;
	}
}
