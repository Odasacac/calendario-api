package CCASolutions.Calendario.DTOs;


public class FestividadesDTO {
	
	private FestividadDTO festividadProxima;
	
	private FestividadDTO festividadAnterior;

	
	public FestividadDTO getFestividadProxima() {
		return festividadProxima;
	}

	public void setFestividadProxima(FestividadDTO festividadProxima) {
		this.festividadProxima = festividadProxima;
	}

	public FestividadDTO getFestividadAnterior() {
		return festividadAnterior;
	}

	public void setFestividadAnterior(FestividadDTO festividadAnterior) {
		this.festividadAnterior = festividadAnterior;
	}	

}
