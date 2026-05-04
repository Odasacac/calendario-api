package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="festividades")
public class FestividadesEntity implements Serializable {

	private static final long serialVersionUID = -4528683727937150880L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String descripcion;
	
	private boolean esSolsticioVerano;
	
	private boolean esSolsticioInvierno;
	
	private boolean esEquinoccioPrimavera;
	
	private boolean esEquinoccioOtonyo;
	
	private boolean esLunaNueva;
	
	private boolean esMetono;
	
	private boolean esEclipeno;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isEsSolsticioVerano() {
		return esSolsticioVerano;
	}

	public void setEsSolsticioVerano(boolean esSolsticioVerano) {
		this.esSolsticioVerano = esSolsticioVerano;
	}

	public boolean isEsSolsticioInvierno() {
		return esSolsticioInvierno;
	}

	public void setEsSolsticioInvierno(boolean esSolsticioInvierno) {
		this.esSolsticioInvierno = esSolsticioInvierno;
	}

	public boolean isEsEquinoccioPrimavera() {
		return esEquinoccioPrimavera;
	}

	public void setEsEquinoccioPrimavera(boolean esEquinoccioPrimavera) {
		this.esEquinoccioPrimavera = esEquinoccioPrimavera;
	}

	public boolean isEsEquinoccioOtonyo() {
		return esEquinoccioOtonyo;
	}

	public void setEsEquinoccioOtonyo(boolean esEquinoccioOtonyo) {
		this.esEquinoccioOtonyo = esEquinoccioOtonyo;
	}

	public boolean isEsLunaNueva() {
		return esLunaNueva;
	}

	public void setEsLunaNueva(boolean esLunaNueva) {
		this.esLunaNueva = esLunaNueva;
	}

	public boolean isEsMetono() {
		return esMetono;
	}

	public void setEsMetono(boolean esMetono) {
		this.esMetono = esMetono;
	}

	public boolean isEsEclipeno() {
		return esEclipeno;
	}

	public void setEsEclipeno(boolean esEclipeno) {
		this.esEclipeno = esEclipeno;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
	
	
	
}
