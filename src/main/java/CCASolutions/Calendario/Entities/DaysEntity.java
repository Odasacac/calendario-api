package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One VAU day, table {@code days}: ten fixed rows, from Terra (position 0, the day of
 * the new moon) to Caelumbra (position 9).
 * ES: Un día VAU, tabla {@code days}: diez filas fijas, de Terra (posición 0, el día de la
 * luna nueva) a Caelumbra (posición 9).
 */
@Entity
@Table(name="days")
public class DaysEntity implements Serializable {

	private static final long serialVersionUID = -6526611219028500391L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private int dayOfWeek;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	

}
