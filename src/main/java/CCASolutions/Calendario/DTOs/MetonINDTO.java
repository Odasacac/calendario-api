package CCASolutions.Calendario.DTOs;

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
