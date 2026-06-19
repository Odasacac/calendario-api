package CCASolutions.Calendario.DTOs;

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
