package CCASolutions.Calendario.DTOs;

public class EclipenoSelectoDTO {
	
	private boolean eclipenoINSelectoDay;
	private String daysSinceCurrentEclipenoSelectoIN;
	

	
	public boolean isEclipenoINSelectoDay() {
		return eclipenoINSelectoDay;
	}
	public void setEclipenoINSelectoDay(boolean eclipenoINSelectoDay) {
		this.eclipenoINSelectoDay = eclipenoINSelectoDay;
	}
	public String getDaysSinceCurrentEclipenoSelectoIN() {
		return daysSinceCurrentEclipenoSelectoIN;
	}
	public void setDaysSinceCurrentEclipenoSelectoIN(String daysSinceCurrentEclipenoSelectoIN) {
		this.daysSinceCurrentEclipenoSelectoIN = daysSinceCurrentEclipenoSelectoIN;
	}


}
