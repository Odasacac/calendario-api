package CCASolutions.Calendario.DTOs;

/**
 * EN: A VAU date expressed as plain values, used by the reverse conversion back into a
 * Gregorian date.
 * ES: Una fecha VAU expresada con valores simples, que usa la conversión inversa de vuelta a
 * una fecha gregoriana.
 */
public class DateVAUDTO {

	private int eclipenoIN;
	private int numberOfMetonoIN;
	private int numberOfYear;
	private String month;
	private String week;
	private String day;
	public int getEclipenoIN() {
		return eclipenoIN;
	}
	public void setEclipenoIN(int eclipenoIN) {
		this.eclipenoIN = eclipenoIN;
	}
	public int getNumberOfMetonoIN() {
		return numberOfMetonoIN;
	}
	public void setNumberOfMetonoIN(int numberOfMetonoIN) {
		this.numberOfMetonoIN = numberOfMetonoIN;
	}
	public int getNumberOfYear() {
		return numberOfYear;
	}
	public void setNumberOfYear(int numberOfYear) {
		this.numberOfYear = numberOfYear;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
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
