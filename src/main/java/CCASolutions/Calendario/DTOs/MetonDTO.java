package CCASolutions.Calendario.DTOs;

public class MetonDTO {

	private boolean metonoINDay;
	private int metonosINSinceLastEclipenoIN;
	private int yearOfCurrentMetonIN;
	private int numberOfMeton;
	private String lastMetonSurname;
	
	

	public String getLastMetonSurname() {
		return lastMetonSurname;
	}
	public void setLastMetonSurname(String lastMetonSurname) {
		this.lastMetonSurname = lastMetonSurname;
	}
	public int getNumberOfMeton() {
		return numberOfMeton;
	}
	public void setNumberOfMeton(int numberOfMeton) {
		this.numberOfMeton = numberOfMeton;
	}
	public int getMetonosINSinceLastEclipenoIN() {
		return metonosINSinceLastEclipenoIN;
	}
	public void setMetonosINSinceLastEclipenoIN(int metonosINSinceLastEclipenoIN) {
		this.metonosINSinceLastEclipenoIN = metonosINSinceLastEclipenoIN;
	}
	public boolean isMetonoINDay() {
		return metonoINDay;
	}
	public void setMetonoINDay(boolean metonoINDay) {
		this.metonoINDay = metonoINDay;
	}
	public int getYearOfCurrentMetonIN() {
		return yearOfCurrentMetonIN;
	}
	public void setYearOfCurrentMetonIN(int yearOfCurrentMetonIN) {
		this.yearOfCurrentMetonIN = yearOfCurrentMetonIN;
	}


}
