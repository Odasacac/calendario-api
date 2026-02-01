package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FenomenoDTO {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime date;
	
	private String phenomena;
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public String getPhenomena() {
		return phenomena;
	}
	public void setPhenomena(String fenomeno) {
		this.phenomena = fenomeno;
	}
}
