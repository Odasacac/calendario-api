package CCASolutions.Calendario.DTOs;

public class MetonIADTO {
	
	private boolean metonoIADay;
	private int metonosIASinceLastEclipenoIA;
	private int yearOfCurrentMetonIA;
	private int numberOfMetonIA;
	private String lastMetonSurname;
	
	
	
	public boolean isMetonoIADay() {
		return metonoIADay;
	}
	public void setMetonoIADay(boolean metonoIADay) {
		this.metonoIADay = metonoIADay;
	}
	public int getMetonosIASinceLastEclipenoIA() {
		return metonosIASinceLastEclipenoIA;
	}
	public void setMetonosIASinceLastEclipenoIA(int metonosIASinceLastEclipenoIA) {
		this.metonosIASinceLastEclipenoIA = metonosIASinceLastEclipenoIA;
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
