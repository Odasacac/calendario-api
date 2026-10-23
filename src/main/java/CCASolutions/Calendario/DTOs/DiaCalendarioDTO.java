package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

/** Un día de la rejilla del calendario VAU, con lo justo para pintar su celda. */
public class DiaCalendarioDTO {

	private LocalDate fecha;
	/** Día de luna nueva: va en la fila Terra, fuera de la rejilla de semanas. */
	private boolean terra;
	/** 1 (Primana) a 5 (Limana); null en Terra o si no hay datos. */
	private Integer semana;
	/** 0 a 6. Limana reutiliza las primeras columnas. */
	private Integer columna;
	private String dia;
	private String apellidoDia;
	private String mes;
	/** 1 a 4; 0 el día del solsticio o equinoccio, que no pertenece a ninguna estación. */
	private int estacion;
	private String festividad;
	private String evento;
	/** Métono, eclípeno o eclipse (se resaltan como en el PDF). */
	private boolean eventoResenyable;
	private boolean eclipse;
	private boolean sinDatos;

	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public boolean isTerra() {
		return terra;
	}
	public void setTerra(boolean terra) {
		this.terra = terra;
	}
	public Integer getSemana() {
		return semana;
	}
	public void setSemana(Integer semana) {
		this.semana = semana;
	}
	public Integer getColumna() {
		return columna;
	}
	public void setColumna(Integer columna) {
		this.columna = columna;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getApellidoDia() {
		return apellidoDia;
	}
	public void setApellidoDia(String apellidoDia) {
		this.apellidoDia = apellidoDia;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public int getEstacion() {
		return estacion;
	}
	public void setEstacion(int estacion) {
		this.estacion = estacion;
	}
	public String getFestividad() {
		return festividad;
	}
	public void setFestividad(String festividad) {
		this.festividad = festividad;
	}
	public String getEvento() {
		return evento;
	}
	public void setEvento(String evento) {
		this.evento = evento;
	}
	public boolean isEventoResenyable() {
		return eventoResenyable;
	}
	public void setEventoResenyable(boolean eventoResenyable) {
		this.eventoResenyable = eventoResenyable;
	}
	public boolean isEclipse() {
		return eclipse;
	}
	public void setEclipse(boolean eclipse) {
		this.eclipse = eclipse;
	}
	public boolean isSinDatos() {
		return sinDatos;
	}
	public void setSinDatos(boolean sinDatos) {
		this.sinDatos = sinDatos;
	}
}
