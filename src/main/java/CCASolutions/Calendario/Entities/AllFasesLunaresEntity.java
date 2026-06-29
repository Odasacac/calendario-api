package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="all_faseslunares")
public class AllFasesLunaresEntity implements Serializable {

	private static final long serialVersionUID = -233937609496062129L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private boolean nueva;
	private boolean cuartoCreciente;
	private boolean llena;
	private boolean cuartoMenguante;
	
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isNueva() {
		return nueva;
	}
	public void setNueva(boolean nueva) {
		this.nueva = nueva;
	}
	public boolean isCuartoCreciente() {
		return cuartoCreciente;
	}
	public void setCuartoCreciente(boolean cuartoCreciente) {
		this.cuartoCreciente = cuartoCreciente;
	}
	public boolean isLlena() {
		return llena;
	}
	public void setLlena(boolean llena) {
		this.llena = llena;
	}
	public boolean isCuartoMenguante() {
		return cuartoMenguante;
	}
	public void setCuartoMenguante(boolean cuartoMenguante) {
		this.cuartoMenguante = cuartoMenguante;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
