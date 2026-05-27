package CCASolutions.Calendario.DTOs;


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
