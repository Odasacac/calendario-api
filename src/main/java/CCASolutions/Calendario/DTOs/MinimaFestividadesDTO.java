package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

/**
 * EN: One festivity candidate while it is still being computed: its code, its date and its
 * distance in days from the date being consulted. A candidate with no date is one that could
 * not be worked out.
 * ES: Una festividad candidata mientras se está calculando: su código, su fecha y su
 * distancia en días respecto a la fecha consultada. Una candidata sin fecha es una que no se
 * ha podido calcular.
 */
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
