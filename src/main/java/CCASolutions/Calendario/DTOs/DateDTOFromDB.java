package CCASolutions.Calendario.DTOs;

import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;

public class DateDTOFromDB {

	private MetonsEntity meton;
	private int year;
	private MonthsEntity month;
	private WeeksEntity week;
	private DaysEntity day;
	private boolean isValid;
	private String comentarios;
	
	
	
	public String getComentarios() {
		return comentarios;
	}
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public MetonsEntity getMeton() {
		return meton;
	}
	public void setMeton(MetonsEntity meton) {
		this.meton = meton;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public MonthsEntity getMonth() {
		return month;
	}
	public void setMonth(MonthsEntity month) {
		this.month = month;
	}
	public WeeksEntity getWeek() {
		return week;
	}
	public void setWeek(WeeksEntity week) {
		this.week = week;
	}
	public DaysEntity getDay() {
		return day;
	}
	public void setDay(DaysEntity day) {
		this.day = day;
	}
	
	
}
