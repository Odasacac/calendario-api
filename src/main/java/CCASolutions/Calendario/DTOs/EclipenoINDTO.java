package CCASolutions.Calendario.DTOs;

/**
 * EN: Position within the eclipeno cycle, the longest one in the calendar: which eclipeno
 * the date belongs to, how many have gone by since the reference one, the year the current
 * one happened, and its qualifier when it is apofasal.
 * ES: Posición dentro del ciclo de eclípenos, el más largo del calendario: a qué eclípeno
 * pertenece la fecha, cuántos han pasado desde el de referencia, el año en que ocurrió el
 * actual, y su apellido cuando es apofasal.
 */
public class EclipenoINDTO {

	private boolean eclipenoINDay;
	private int yearOfCurrentEclipenoIN;
	private int eclipenosINSinceLastEclipenoINSelecto;
	private int numberOfEclipenoIN;
	private String lastEclipenoSurname;
	

	
	public String getLastEclipenoSurname() {
		return lastEclipenoSurname;
	}
	public void setLastEclipenoSurname(String lastEclipenoSurname) {
		this.lastEclipenoSurname = lastEclipenoSurname;
	}
	public int getEclipenosINSinceLastEclipenoINSelecto() {
		return eclipenosINSinceLastEclipenoINSelecto;
	}
	public void setEclipenosINSinceLastEclipenoINSelecto(int eclipenosINSinceLastEclipenoINSelecto) {
		this.eclipenosINSinceLastEclipenoINSelecto = eclipenosINSinceLastEclipenoINSelecto;
	}
	public int getNumberOfEclipenoIN() {
		return numberOfEclipenoIN;
	}
	public void setNumberOfEclipenoIN(int numberOfEclipenoIN) {
		this.numberOfEclipenoIN = numberOfEclipenoIN;
	}
	public boolean isEclipenoINDay() {
		return eclipenoINDay;
	}
	public void setEclipenoINDay(boolean eclipenoINDay) {
		this.eclipenoINDay = eclipenoINDay;
	}
	public int getYearOfCurrentEclipenoIN() {
		return yearOfCurrentEclipenoIN;
	}
	public void setYearOfCurrentEclipenoIN(int yearOfCurrentEclipenoIN) {
		this.yearOfCurrentEclipenoIN = yearOfCurrentEclipenoIN;
	}

	
}
