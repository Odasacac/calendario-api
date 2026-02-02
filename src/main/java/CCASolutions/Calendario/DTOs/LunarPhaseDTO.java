package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class LunarPhaseDTO {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime date;
	
	private String moonPhase;
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getMoonPhase() {
		return moonPhase;
	}
	public void setMoonPhase(String fenomeno) {
		this.moonPhase = fenomeno;
	}
}
