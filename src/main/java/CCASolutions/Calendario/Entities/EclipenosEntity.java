package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="eclipenos")
public class EclipenosEntity implements Serializable{

	private static final long serialVersionUID = 7526229837280596257L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime date;
	
	private int year;
	
	private Boolean inicial;
	
	private Boolean bicuartal;
	
	private Boolean cuartal;
	
	private Boolean tricuartal;
	
	private Boolean nuevo;
	
	private Boolean lleno;

	private boolean esTotal;
	
	private boolean esParcial;
	
	private boolean esAnular;
	
	private boolean esHibrido;
		
	private boolean esPenumbral;
	
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

	public Boolean getInicial() {
		return inicial;
	}

	public void setInicial(Boolean inicial) {
		this.inicial = inicial;
	}

	public Boolean getBicuartal() {
		return bicuartal;
	}

	public void setBicuartal(Boolean bicuartal) {
		this.bicuartal = bicuartal;
	}

	public Boolean getCuartal() {
		return cuartal;
	}

	public void setCuartal(Boolean cuartal) {
		this.cuartal = cuartal;
	}

	public Boolean getTricuartal() {
		return tricuartal;
	}

	public void setTricuartal(Boolean tricuartal) {
		this.tricuartal = tricuartal;
	}

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}

	public Boolean getLleno() {
		return lleno;
	}

	public void setLleno(Boolean lleno) {
		this.lleno = lleno;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
