package CCASolutions.Calendario.DTOs;

/**
 * EN: Position within the cycle of winter aporic metons, the solstices coinciding with an
 * apogee. Same shape as the new meton counter.
 * ES: Posición dentro del ciclo de métonos invernales apóricos, los solsticios que coinciden
 * con un apogeo. Misma forma que el contador de métonos nuevos.
 */
public class MetonIADTO {
	
	private boolean metonoIADay;
	private int metonosIASinceLastEclipenoSelecto;
	private int yearOfCurrentMetonIA;
	private int numberOfMetonIA;
	private String lastMetonSurname;
	
	
	
	public boolean isMetonoIADay() {
		return metonoIADay;
	}
	public void setMetonoIADay(boolean metonoIADay) {
		this.metonoIADay = metonoIADay;
	}
	public int getMetonosIASinceLastEclipenoSelecto() {
		return metonosIASinceLastEclipenoSelecto;
	}
	public void setMetonosIASinceLastEclipenoSelecto(int metonosIASinceLastEclipenoSelecto) {
		this.metonosIASinceLastEclipenoSelecto = metonosIASinceLastEclipenoSelecto;
	}
	public int getYearOfCurrentMetonIA() {
		return yearOfCurrentMetonIA;
	}
	public void setYearOfCurrentMetonIA(int yearOfCurrentMetonIA) {
		this.yearOfCurrentMetonIA = yearOfCurrentMetonIA;
	}

	public int getNumberOfMetonIA() {
		return numberOfMetonIA;
	}
	public void setNumberOfMetonIA(int numberOfMetonIA) {
		this.numberOfMetonIA = numberOfMetonIA;
	}
	public String getLastMetonSurname() {
		return lastMetonSurname;
	}
	public void setLastMetonSurname(String lastMetonSurname) {
		this.lastMetonSurname = lastMetonSurname;
	}
	
	
}
