package CCASolutions.Calendario.DTOs;

/**
 * EN: One moon phase as the OPALE API returns it: the instant as a raw string, because dates
 * before year 1 do not parse into a LocalDateTime, and the type of phase.
 * ES: Una fase lunar tal y como la devuelve la API de OPALE: el instante como cadena en
 * bruto, porque las fechas anteriores al año 1 no se parsean a LocalDateTime, y el tipo de
 * fase.
 */
public class LunarPhaseDTO {
	
	private String date;
	
	private String moonPhase;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMoonPhase() {
		return moonPhase;
	}
	public void setMoonPhase(String fenomeno) {
		this.moonPhase = fenomeno;
	}
}
