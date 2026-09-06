package CCASolutions.Calendario.DTOs;

/**
 * EN: Position within the aponovo cycle. An aponovo is a new moon at apogee: the DTO says
 * how many have gone by since the reference meton and how many ordinary new moons have
 * passed since the last one, which gives the month within the aponovo.
 * ES: Posición dentro del ciclo de aponovos. Un aponovo es una luna nueva en apogeo: el DTO
 * indica cuántos han pasado desde el métono de referencia y cuántas lunas nuevas corrientes
 * han pasado desde el último, lo que da el mes dentro del aponovo.
 */
public class AponovosDTO {

	private int aponovosPasadosDesdeLastMetonoIAR;
	private int numeroDeAponovo;
	private int lunasNuevasPasadasDesdeLastAponovo;
	private int mesAponoval;
	
	
	public int getLunasNuevasPasadasDesdeLastAponovo() {
		return lunasNuevasPasadasDesdeLastAponovo;
	}
	public void setLunasNuevasPasadasDesdeLastAponovo(int lunasNuevasPasadosDesdeLastAponovo) {
		this.lunasNuevasPasadasDesdeLastAponovo = lunasNuevasPasadosDesdeLastAponovo;
	}
	public int getMesAponoval() {
		return mesAponoval;
	}
	public void setMesAponoval(int mesAponoval) {
		this.mesAponoval = mesAponoval;
	}
	public int getAponovosPasadosDesdeLastMetonoIAR() {
		return aponovosPasadosDesdeLastMetonoIAR;
	}
	public void setAponovosPasadosDesdeLastMetonoIAR(int aponovosPasadosDesdeLastMetonoIAR) {
		this.aponovosPasadosDesdeLastMetonoIAR = aponovosPasadosDesdeLastMetonoIAR;
	}
	public int getNumeroDeAponovo() {
		return numeroDeAponovo;
	}
	public void setNumeroDeAponovo(int numeroDeAponovo) {
		this.numeroDeAponovo = numeroDeAponovo;
	}

}
