package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * EN: One VAU month, table {@code months}: eighteen fixed rows. Each season has three
 * ordinary months plus a hybrid one spanning the change of season; there is also a liminal
 * month for the stretch after the winter solstice, and Nomon, the placeholder.
 * ES: Un mes VAU, tabla {@code months}: dieciocho filas fijas. Cada estación tiene tres meses
 * corrientes más uno híbrido que abarca el cambio de estación; existe además un mes liminal
 * para el tramo posterior al solsticio de invierno, y Nomon, el de relleno.
 */
@Entity
@Table(name="months")
public class MonthsEntity implements Serializable {

	private static final long serialVersionUID = -3474625787442383891L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Boolean hibrid;
	
	private Integer season;
	
	private Integer monthOfSeason;
	
	private boolean liminal;
	
	


	public boolean isLiminal() {
		return liminal;
	}

	public void setLiminal(boolean liminal) {
		this.liminal = liminal;
	}

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

	public Boolean getHibrid() {
		return hibrid;
	}

	public void setHibrid(Boolean hibrid) {
		this.hibrid = hibrid;
	}

	public Integer getSeason() {
		return season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public Integer getMonthOfSeason() {
		return monthOfSeason;
	}

	public void setMonthOfSeason(Integer monthOfSeason) {
		this.monthOfSeason = monthOfSeason;
	}
	
	
}
