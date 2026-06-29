package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="apo_peri_lunas")
public class ApogeosYPerigeosLunaEntity implements Serializable {


	private static final long serialVersionUID = 4648719388199771942L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;
	private int year;
	
	private boolean esApogeo;
	private boolean esPerigeo;
	private double distance;
	
	private boolean esSelecto;
	private boolean esInvertido;
	private Long lunaId;
	
	
	
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Long getLunaId() {
		return lunaId;
	}
	public void setLunaId(Long lunaId) {
		this.lunaId = lunaId;
	}
	public boolean isEsSelecto() {
		return esSelecto;
	}
	public void setEsSelecto(boolean esSelecto) {
		this.esSelecto = esSelecto;
	}
	public boolean isEsInvertido() {
		return esInvertido;
	}
	public void setEsInvertido(boolean esInvertido) {
		this.esInvertido = esInvertido;
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
	public boolean isEsApogeo() {
		return esApogeo;
	}
	public void setEsApogeo(boolean esApogeo) {
		this.esApogeo = esApogeo;
	}
	public boolean isEsPerigeo() {
		return esPerigeo;
	}
	public void setEsPerigeo(boolean esPerigeo) {
		this.esPerigeo = esPerigeo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	
}
