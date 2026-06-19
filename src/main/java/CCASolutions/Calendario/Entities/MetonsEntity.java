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
	private int year;
	
	private boolean fasal;
	private boolean apoperico;
	
	private boolean invernal;
	private boolean primaveral;
	private boolean estival;
	private boolean otonyal;
	
	private boolean nuevo;
	private boolean lleno;
	
	private boolean perico;
	private boolean aporico;
	
	private boolean selecto;
	private boolean invertido;
	
	private long lunaId;
	private Long apoperiId;
	private long soeId;
	
	
	
	
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
	public boolean isFasal() {
		return fasal;
	}
	public void setFasal(boolean fasal) {
		this.fasal = fasal;
	}
	public boolean isApoperico() {
		return apoperico;
	}
	public void setApoperico(boolean apoperico) {
		this.apoperico = apoperico;
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
	public boolean isPerico() {
		return perico;
	}
	public void setPerico(boolean perico) {
		this.perico = perico;
	}
	public boolean isAporico() {
		return aporico;
	}
	public void setAporico(boolean aporico) {
		this.aporico = aporico;
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
	public long getLunaId() {
		return lunaId;
	}
	public void setLunaId(long lunaId) {
		this.lunaId = lunaId;
	}
	public Long getApoperiId() {
		return apoperiId;
	}
	public void setApoperiId(Long apoperiId) {
		this.apoperiId = apoperiId;
	}
	public long getSoeId() {
		return soeId;
	}
	public void setSoeId(long soeId) {
		this.soeId = soeId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
		
}
