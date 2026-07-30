package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/*
 * EN: Lunar phases (~104.000 rows). Every read of this table filters by "date",
 *     so without these indexes MySQL had to scan the whole table on each request.
 *     The composite indexes cover the two hot filters: "new moon in a range" and
 *     "selected new moon in a range" (used to count aponovos).
 *
 * ES: Fases lunares (~104.000 filas). Todas las lecturas de esta tabla filtran por
 *     "date", por lo que sin estos indices MySQL tenia que recorrer la tabla completa
 *     en cada peticion. Los indices compuestos cubren los dos filtros calientes:
 *     "luna nueva en un rango" y "luna nueva selecta en un rango" (para contar aponovos).
 */
@Entity
@Table(name="lunas", indexes = {
		@Index(name="idx_lunas_date", columnList="date"),
		@Index(name="idx_lunas_nueva_date", columnList="nueva,date"),
		@Index(name="idx_lunas_nueva_selecta_date", columnList="nueva,selecta,date")
})
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
