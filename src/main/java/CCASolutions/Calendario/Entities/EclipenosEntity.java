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
	
	private boolean invernal;
	private boolean primaveral;
	private boolean estival;
	private boolean otonyal;
	
	private boolean nuevo;
	private boolean lleno;

	private boolean selecto;
	private boolean invertido;
	
	private boolean esTotal;
	private boolean esParcial;
	private boolean esAnular;
	private boolean esHibrido;
	private boolean esPenumbral;

	private long metonoId;
	private long eclipseId;
	
	
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
	public boolean isInvernal() {
		return invernal;
	}
	public void setInvernal(boolean invernal) {
		this.invernal = invernal;
	}
	public boolean isPrimaveral() {
		return primaveral;
	}
	public void setPrimaveral(boolean primaveral) {
		this.primaveral = primaveral;
	}
	public boolean isEstival() {
		return estival;
	}
	public void setEstival(boolean estival) {
		this.estival = estival;
	}
	public boolean isOtonyal() {
		return otonyal;
	}
	public void setOtonyal(boolean otonyal) {
		this.otonyal = otonyal;
	}
	public boolean isNuevo() {
		return nuevo;
	}
	public void setNuevo(boolean nuevo) {
		this.nuevo = nuevo;
	}
	public boolean isLleno() {
		return lleno;
	}
	public void setLleno(boolean lleno) {
		this.lleno = lleno;
	}
	public boolean isSelecto() {
		return selecto;
	}
	public void setSelecto(boolean selecto) {
		this.selecto = selecto;
	}
	public boolean isInvertido() {
		return invertido;
	}
	public void setInvertido(boolean invertido) {
		this.invertido = invertido;
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
	public long getMetonoId() {
		return metonoId;
	}
	public void setMetonoId(long metonoId) {
		this.metonoId = metonoId;
	}
	public long getEclipseId() {
		return eclipseId;
	}
	public void setEclipseId(long eclipseId) {
		this.eclipseId = eclipseId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
