package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class MinimaFestividadesDTO {

	private String code;
	private long diasDeDiferenciaConDate;
	private LocalDateTime date;
	
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public long getDiasDeDiferenciaConDate() {
		return diasDeDiferenciaConDate;
	}
	public void setDiasDeDiferenciaConDate(long diasDeDiferenciaConDate) {
		this.diasDeDiferenciaConDate = diasDeDiferenciaConDate;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
