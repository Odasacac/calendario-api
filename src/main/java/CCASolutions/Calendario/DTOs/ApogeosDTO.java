package CCASolutions.Calendario.DTOs;


/**
 * EN: One apogee or perigee as the OPALE API returns it: instant, kind and distance in
 * kilometres.
 * ES: Un apogeo o perigeo tal y como lo devuelve la API de OPALE: instante, tipo y distancia
 * en kilómetros.
 */
public class ApogeosDTO {

    private String date;
    private String phenomena;
    private double distance;

    public ApogeosDTO() {}

    public ApogeosDTO(String date, String phenomena, double distance) {
        this.date = date;
        this.phenomena = phenomena;
        this.distance = distance;
    }

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPhenomena() {
		return phenomena;
	}

	public void setPhenomena(String phenomena) {
		this.phenomena = phenomena;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
