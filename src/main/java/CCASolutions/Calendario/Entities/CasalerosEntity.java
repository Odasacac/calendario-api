package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One casalero, table {@code casaleros}: the name an eclipeno gets from the first
 * phenomenon happening after it, either a meton or an absolute eclipse. There is exactly one
 * per eclipeno.
 * ES: Un casalero, tabla {@code casaleros}: el nombre que recibe un eclípeno del primer
 * fenómeno que ocurre después de él, sea un métono o un eclipse absoluto. Hay exactamente
 * uno por eclípeno.
 */
@Entity
@Table(name="casaleros")
public class CasalerosEntity implements Serializable {
	
	/*
	 
	 ¿Qué fenómeno ocurrirá primero después de un eclípeno?
	 
	 	¿Un métono o un eclipse absoluto? Eso es un Casalero
			
	 */
	
	private static final long serialVersionUID = -8157953109064013194L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)	
	private Long id;
	
	private LocalDateTime date;
	private int year;
	private Long eclipenoId;
	private Long metonoId;
	private Long eclipseId;
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Long getEclipenoId() {
		return eclipenoId;
	}
	public void setEclipenoId(Long eclipenoId) {
		this.eclipenoId = eclipenoId;
	}
	public Long getMetonoId() {
		return metonoId;
	}
	public void setMetonoId(Long metonoId) {
		this.metonoId = metonoId;
	}
	public Long getEclipseId() {
		return eclipseId;
	}
	public void setEclipseId(Long eclipseId) {
		this.eclipseId = eclipseId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
