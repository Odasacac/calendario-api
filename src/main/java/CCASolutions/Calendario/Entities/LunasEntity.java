package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="lunas")
public class LunasEntity implements Serializable {

	private static final long serialVersionUID = -7995525595322744836L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime date;
	
	private int year;	
	private boolean nueva;
	private boolean cuartoCreciente;
	private boolean llena;
	private boolean cuartoMenguante;
	
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
}
