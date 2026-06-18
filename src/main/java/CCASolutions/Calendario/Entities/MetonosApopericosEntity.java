package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="metonos_apopericos")
public class MetonosApopericosEntity implements Serializable {

	/*	 
	 	Un metono apoperico es la conjuncion soe con apoperi, siendo la fecha siempre referente al soe
	 
	 */

	private static final long serialVersionUID = 1570735644012843654L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime date;
	private int year;
	
	private Long apoperiId;
	private Long soeId;
	
	private boolean invernal;
	private boolean primaveral;
	private boolean estival;
	private boolean otonyal;
	
	private boolean apogeo;
	private boolean perigeo;
	
	private boolean selecto; // Perigeo en luna llena o apogeo en luna nueva
	private boolean invertido; // Apogeo en luna llena o perigeo en luna nueva
	
	
	public Long getApoperiId() {
		return apoperiId;
	}
	public void setApoperiId(Long apoperiId) {
		this.apoperiId = apoperiId;
	}
	public Long getSoeId() {
		return soeId;
	}
	public void setSoeId(Long soeId) {
		this.soeId = soeId;
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
	public boolean isApogeo() {
		return apogeo;
	}
	public void setApogeo(boolean apogeo) {
		this.apogeo = apogeo;
	}
	public boolean isPerigeo() {
		return perigeo;
	}
	public void setPerigeo(boolean perigeo) {
		this.perigeo = perigeo;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
