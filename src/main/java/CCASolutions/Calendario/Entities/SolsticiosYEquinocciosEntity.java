package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One solstice or equinox, table {@code sye}: four per year from year 1 to 2100. The
 * startingSeason field says which season it opens: 1 winter, 2 spring, 3 summer, 4 autumn.
 * ES: Un solsticio o equinoccio, tabla {@code sye}: cuatro por año del 1 al 2100. El campo
 * startingSeason indica qué estación abre: 1 invierno, 2 primavera, 3 verano, 4 otoño.
 */
@Entity
@Table(name="sye")
public class SolsticiosYEquinocciosEntity implements Serializable {

	private static final long serialVersionUID = 2651548553460197414L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime date;
	
	private int year;
	
	private boolean solsticioInvierno;
	private boolean solsticioVerano;
	private boolean equinoccioPrimavera;
	private boolean equinoccioOtonyo;
	private int startingSeason;
	
	public int getStartingSeason() {
		return startingSeason;
	}
	public void setStartingSeason(int season) {
		this.startingSeason = season;
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
	public boolean isSolsticioInvierno() {
		return solsticioInvierno;
	}
	public void setSolsticioInvierno(boolean solsticioInvierno) {
		this.solsticioInvierno = solsticioInvierno;
	}
	public boolean isSolsticioVerano() {
		return solsticioVerano;
	}
	public void setSolsticioVerano(boolean solsticioVerano) {
		this.solsticioVerano = solsticioVerano;
	}
	public boolean isEquinoccioPrimavera() {
		return equinoccioPrimavera;
	}
	public void setEquinoccioPrimavera(boolean equinoccioPrimavera) {
		this.equinoccioPrimavera = equinoccioPrimavera;
	}
	public boolean isEquinoccioOtonyo() {
		return equinoccioOtonyo;
	}
	public void setEquinoccioOtonyo(boolean equinoccioOtonyo) {
		this.equinoccioOtonyo = equinoccioOtonyo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
