package CCASolutions.Calendario.DTOs;

/**
 * EN: Position within the cycle of winter new metons: which one the date belongs to, how
 * many have gone by since the reference meton, the year the current one happened, and its
 * qualifier when it is selecto or invertido.
 * ES: Posición dentro del ciclo de métonos invernales nuevos: a cuál pertenece la fecha,
 * cuántos han pasado desde el métono de referencia, el año en que ocurrió el actual, y su
 * apellido cuando es selecto o invertido.
 */
public class MetonINDTO {
	
	private boolean metonoINDay;
	private int metonosINSinceLastEclipenoIN;
	private int yearOfCurrentMetonIN;
	private int numberOfMetonIN;
	private String lastMetonSurname;
	public boolean isMetonoINDay() {
		return metonoINDay;
	}
	public void setMetonoINDay(boolean metonoINDay) {
		this.metonoINDay = metonoINDay;
	}
	public int getMetonosINSinceLastEclipenoIN() {
		return metonosINSinceLastEclipenoIN;
	}
	public void setMetonosINSinceLastEclipenoIN(int metonosINSinceLastEclipenoIN) {
		this.metonosINSinceLastEclipenoIN = metonosINSinceLastEclipenoIN;
	}
	public int getYearOfCurrentMetonIN() {
		return yearOfCurrentMetonIN;
	}
	public void setYearOfCurrentMetonIN(int yearOfCurrentMetonIN) {
		this.yearOfCurrentMetonIN = yearOfCurrentMetonIN;
	}

	public int getNumberOfMetonIN() {
		return numberOfMetonIN;
	}
	public void setNumberOfMetonIN(int numberOfMetonIN) {
		this.numberOfMetonIN = numberOfMetonIN;
	}
	public String getLastMetonSurname() {
		return lastMetonSurname;
	}
	public void setLastMetonSurname(String lastMetonSurname) {
		this.lastMetonSurname = lastMetonSurname;
	}
	
	
}
