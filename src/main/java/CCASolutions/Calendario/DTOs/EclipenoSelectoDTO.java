package CCASolutions.Calendario.DTOs;

public class EclipenoSelectoDTO {
	
	private boolean eclipenoINSelectoDay;
	private int yearOfCurrentEclipenoIN;
	

	
	public boolean isEclipenoINSelectoDay() {
		return eclipenoINSelectoDay;
	}
	public void setEclipenoINSelectoDay(boolean eclipenoINSelectoDay) {
		this.eclipenoINSelectoDay = eclipenoINSelectoDay;
	}
	public int getYearOfCurrentEclipenoIN() {
		return yearOfCurrentEclipenoIN;
	}
	public void setYearOfCurrentEclipenoIN(int yearOfCurrentEclipenoIN) {
		this.yearOfCurrentEclipenoIN = yearOfCurrentEclipenoIN;
	}

}
