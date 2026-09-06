package CCASolutions.Calendario.DTOs;

/**
 * EN: The VAU week and day of a date, both counted from the last new moon.
 * ES: La semana y el día VAU de una fecha, ambos contados desde la última luna nueva.
 */
public class VAUWeekAndDayDTO {
	
	private String week;
	private String day;

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
}
