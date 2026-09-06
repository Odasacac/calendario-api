package CCASolutions.Calendario.DTOs;


/**
 * EN: One solstice or equinox as the OPALE API returns it: instant and kind of phenomenon.
 * ES: Un solsticio o equinoccio tal y como lo devuelve la API de OPALE: instante y tipo de
 * fenómeno.
 */
public class FenomenoDTO {
	
	private String date;
	
	private String phenomena;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPhenomena() {
		return phenomena;
	}
	public void setPhenomena(String fenomeno) {
		this.phenomena = fenomeno;
	}
}
