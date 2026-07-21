package CCASolutions.Calendario.DTOs;

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
