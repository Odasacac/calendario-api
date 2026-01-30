package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="metons")
public class MetonsEntity implements Serializable {

	private static final long serialVersionUID = -310747303333504293L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;
	
	private Boolean solsticial;
	
	private Boolean equinoccial;
	
	private Boolean inicial;
	
	private Boolean bicuartal;
	
	private Boolean cuartal;
	
	private Boolean Tricuartal;
	
	private Boolean nuevo;
	
	private Boolean lleno;

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

	public Boolean getSolsticial() {
		return solsticial;
	}

	public void setSolsticial(Boolean solsticial) {
		this.solsticial = solsticial;
	}

	public Boolean getEquinoccial() {
		return equinoccial;
	}

	public void setEquinoccial(Boolean equinoccial) {
		this.equinoccial = equinoccial;
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

	public void setCuartal(Boolean cuarta) {
		this.cuartal = cuarta;
	}

	public Boolean getTricuartal() {
		return Tricuartal;
	}

	public void setTricuartal(Boolean tricuartal) {
		Tricuartal = tricuartal;
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
