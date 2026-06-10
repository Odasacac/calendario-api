package CCASolutions.Calendario.DTOs;

public class EclipenoINDTO {

	private boolean eclipenoINDay;
	private int yearOfCurrentEclipenoIN;
	private int eclipenosINSinceLastEclipenoINSelecto;
	private int numberOfEclipeno;
	

	
	public int getEclipenosINSinceLastEclipenoINSelecto() {
		return eclipenosINSinceLastEclipenoINSelecto;
	}
	public void setEclipenosINSinceLastEclipenoINSelecto(int eclipenosINSinceLastEclipenoINSelecto) {
		this.eclipenosINSinceLastEclipenoINSelecto = eclipenosINSinceLastEclipenoINSelecto;
	}
	public int getNumberOfEclipeno() {
		return numberOfEclipeno;
	}
	public void setNumberOfEclipeno(int numberOfEclipeno) {
		this.numberOfEclipeno = numberOfEclipeno;
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
