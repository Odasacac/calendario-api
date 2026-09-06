package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One eclipse in the historical table {@code all_eclipses}, with the date split into
 * numeric fields. Kept as a record only; the calendar uses the eclipses table.
 * ES: Un eclipse en la tabla histórica {@code all_eclipses}, con la fecha troceada en campos
 * numéricos. Se conserva sólo como registro; el calendario usa la tabla de eclipses.
 */
@Entity
@Table(name="all_eclipses")
public class AllEclipsesEntity implements Serializable{
	
	private static final long serialVersionUID = 5835651804759186343L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	private boolean deLuna;
	private boolean deSol;
	
	private boolean parcial;
	private boolean total;
	private boolean penumbral;
	private boolean hibrido;
	private boolean anular;
	
	
	
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
	public boolean isDeLuna() {
		return deLuna;
	}
	public void setDeLuna(boolean deLuna) {
		this.deLuna = deLuna;
	}
	public boolean isDeSol() {
		return deSol;
	}
	public void setDeSol(boolean deSol) {
		this.deSol = deSol;
	}
	public boolean isParcial() {
		return parcial;
	}
	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}
	public boolean isTotal() {
		return total;
	}
	public void setTotal(boolean total) {
		this.total = total;
	}
	public boolean isPenumbral() {
		return penumbral;
	}
	public void setPenumbral(boolean penumbral) {
		this.penumbral = penumbral;
	}
	public boolean isHibrido() {
		return hibrido;
	}
	public void setHibrido(boolean hibrido) {
		this.hibrido = hibrido;
	}
	public boolean isAnular() {
		return anular;
	}
	public void setAnular(boolean anular) {
		this.anular = anular;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
