package CCASolutions.Calendario.DTOs;

public class MetonoInvernalApofasalRemotoDTO {
	
	private boolean metonoInvernalApofasalRemotoDay;
	private String daysSinceCurrentMetonoInvernalApofasalRemoto;
	
	
	public boolean isMetonoInvernalApofasalRemotoDay() {
		return metonoInvernalApofasalRemotoDay;
	}
	public void setMetonoInvernalApofasalRemotoDay(boolean metonoInvernalApofasalRemotoDay) {
		this.metonoInvernalApofasalRemotoDay = metonoInvernalApofasalRemotoDay;
	}
	public String getDaysSinceCurrentMetonoInvernalApofasalRemoto() {
		return daysSinceCurrentMetonoInvernalApofasalRemoto;
	}
	public void setDaysSinceCurrentMetonoInvernalApofasalRemoto(String daysSinceCurrentMetonoInvernalApofasalRemoto) {
		this.daysSinceCurrentMetonoInvernalApofasalRemoto = daysSinceCurrentMetonoInvernalApofasalRemoto;
	}
}
