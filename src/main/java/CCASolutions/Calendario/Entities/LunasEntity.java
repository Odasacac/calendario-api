package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One moon phase, table {@code lunas}: new moon, first quarter, full moon or last
 * quarter, from year 1 to 2100. The selecta and invertida flags are set later, when the
 * phases are paired with the apogees and perigees: a full moon at perigee or a new moon at
 * apogee is selecta, and the opposite combinations are invertida.
 * ES: Una fase lunar, tabla {@code lunas}: luna nueva, cuarto creciente, luna llena o cuarto
 * menguante, del año 1 al 2100. Las banderas selecta e invertida se ponen más tarde, al
 * emparejar las fases con los apogeos y perigeos: luna llena en perigeo o luna nueva en
 * apogeo es selecta, y las combinaciones contrarias son invertida.
 */
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
	
	private boolean selecta;
	private boolean invertida;
	private Long apoperiId;

	
	public Long getApoperiId() {
		return apoperiId;
	}
	public void setApoperiId(Long apoperiId) {
		this.apoperiId = apoperiId;
	}
	public boolean isSelecta() {
		return selecta;
	}
	public void setSelecta(boolean selecta) {
		this.selecta = selecta;
	}

	public boolean isInvertida() {
		return invertida;
	}
	public void setInvertida(boolean invertida) {
		this.invertida = invertida;
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
