package CCASolutions.Calendario.DTOs;

/**
 * EN: Identifies one VAU year for the calendar download: the year of its eclipeno, its meton
 * number and its year number.
 * ES: Identifica un año VAU para la descarga del calendario: el año de su eclípeno, su número
 * de métono y su número de año.
 */
public class CalendarByYearDTO {

	private int yearOfEclipenoIN;
	private int numberOfMetonoIN;
	private int numberOfYear;
	
	
	public int getYearOfEclipenoIN() {
		return yearOfEclipenoIN;
	}
	public void setYearOfEclipenoIN(int yearOfEclipenoIN) {
		this.yearOfEclipenoIN = yearOfEclipenoIN;
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
}
