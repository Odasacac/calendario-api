package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="eclipses")
public class EclipsesEntity implements Serializable {

	private static final long serialVersionUID = -5445158608884441554L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime date;
	
	private int year;
	
	private boolean deLuna;
	
	private boolean deSol;
	
	private boolean esTotal;
	
	private boolean esParcial;
	
	private boolean esAnular;
	
	private boolean esHibrido;
		
	private boolean esPenumbral;

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

	public boolean isEsTotal() {
		return esTotal;
	}

	public void setEsTotal(boolean esTotal) {
		this.esTotal = esTotal;
	}

	public boolean isEsParcial() {
		return esParcial;
	}

	public void setEsParcial(boolean esParcial) {
		this.esParcial = esParcial;
	}

	public boolean isEsAnular() {
		return esAnular;
	}

	public void setEsAnular(boolean esAnular) {
		this.esAnular = esAnular;
	}

	public boolean isEsHibrido() {
		return esHibrido;
	}

	public void setEsHibrido(boolean esHibrido) {
		this.esHibrido = esHibrido;
	}

	public boolean isEsPenumbral() {
		return esPenumbral;
	}

	public void setEsPenumbral(boolean esPenumbral) {
		this.esPenumbral = esPenumbral;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
