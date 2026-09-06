package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One solstice or equinox in the historical table {@code all_soes}, with the date split
 * into numeric fields. Kept as a record only; the calendar uses the sye table.
 * ES: Un solsticio o equinoccio en la tabla histórica {@code all_soes}, con la fecha troceada
 * en campos numéricos. Se conserva sólo como registro; el calendario usa la tabla sye.
 */
@Entity
@Table(name="all_soes")
public class AllSoEsEntity implements Serializable{

	private static final long serialVersionUID = -3204942105460324006L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	private boolean solsticioInvierno;
	private boolean equinoccioPrimavera;
	private boolean solsticioVerano;
	private boolean equinoccioOtonyo;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public boolean isSolsticioInvierno() {
		return solsticioInvierno;
	}
	public void setSolsticioInvierno(boolean solsticioInvierno) {
		this.solsticioInvierno = solsticioInvierno;
	}
	public boolean isEquinoccioPrimavera() {
		return equinoccioPrimavera;
	}
	public void setEquinoccioPrimavera(boolean equinoccioPrimavera) {
		this.equinoccioPrimavera = equinoccioPrimavera;
	}
	public boolean isSolsticioVerano() {
		return solsticioVerano;
	}
	public void setSolsticioVerano(boolean solsticioVerano) {
		this.solsticioVerano = solsticioVerano;
	}
	public boolean isEquinoccioOtonyo() {
		return equinoccioOtonyo;
	}
	public void setEquinoccioOtonyo(boolean equinoccioOtonyo) {
		this.equinoccioOtonyo = equinoccioOtonyo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
