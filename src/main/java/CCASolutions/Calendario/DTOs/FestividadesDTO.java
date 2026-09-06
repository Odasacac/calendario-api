package CCASolutions.Calendario.DTOs;


/**
 * EN: The three festivities around a date, already formatted as text.
 * ES: Las tres festividades alrededor de una fecha, ya formateadas como texto.
 */
public class FestividadesDTO {
	
	private String festividadActual;
	
	private String festividadProxima;
	
	private String festividadAnterior;

	public String getFestividadActual() {
		return festividadActual;
	}

	public void setFestividadActual(String festividadActual) {
		this.festividadActual = festividadActual;
	}

	public String getFestividadProxima() {
		return festividadProxima;
	}

	public void setFestividadProxima(String festividadProxima) {
		this.festividadProxima = festividadProxima;
	}

	public String getFestividadAnterior() {
		return festividadAnterior;
	}

	public void setFestividadAnterior(String festividadAnterior) {
		this.festividadAnterior = festividadAnterior;
	}

	
	
}
