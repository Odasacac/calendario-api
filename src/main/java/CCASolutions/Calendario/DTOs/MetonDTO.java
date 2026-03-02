package CCASolutions.Calendario.DTOs;

public class MetonDTO {

	private boolean esMetonoIN;
	private int metonosINPasadosDesdeLastEclipenoIN;
	private int yearOfTheActualMetonIN;
	
	
	
	public int getMetonosINPasadosDesdeLastEclipenoIN() {
		return metonosINPasadosDesdeLastEclipenoIN;
	}
	public void setMetonosINPasadosDesdeLastEclipenoIN(int metonosINPasadosDesdeLastEclipenoIN) {
		this.metonosINPasadosDesdeLastEclipenoIN = metonosINPasadosDesdeLastEclipenoIN;
	}
	public boolean isEsMetonoIN() {
		return esMetonoIN;
	}
	public void setEsMetonoIN(boolean esMetono) {
		this.esMetonoIN = esMetono;
	}
	public int getYearOfTheActualMetonIN() {
		return yearOfTheActualMetonIN;
	}
	public void setYearOfTheActualMetonIN(int yearOfTheActualMetonIN) {
		this.yearOfTheActualMetonIN = yearOfTheActualMetonIN;
	}

}
