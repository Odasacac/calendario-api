package CCASolutions.Calendario.DTOs;

/**
 * EN: Distance to the reference eclipeno, the winter apofasal remote one: the days elapsed
 * already formatted as text, and whether the date is that very day.
 * ES: Distancia al eclípeno de referencia, el invernal apofasal remoto: los días
 * transcurridos ya formateados como texto, y si la fecha es ese mismo día.
 */
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
