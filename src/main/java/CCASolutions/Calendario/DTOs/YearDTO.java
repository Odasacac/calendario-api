package CCASolutions.Calendario.DTOs;

/**
 * EN: The VAU year, which is the number of winter solstices gone by since the reference
 * meton. A date landing exactly on a winter solstice belongs to no year and gets a dash.
 * ES: El año VAU, que es el número de solsticios de invierno transcurridos desde el métono
 * de referencia. Una fecha que cae exactamente en un solsticio de invierno no pertenece a
 * ningún año y recibe un guion.
 */
public class YearDTO {
	
	private boolean esSolsticioDeInvierno;
	private String solsticiosDeInviernoSinceLastMetonIN;
	private int numberOfYear;
	
	public boolean isEsSolsticioDeInvierno() {
		return esSolsticioDeInvierno;
	}
	public void setEsSolsticioDeInvierno(boolean esSolsticioDeInvierno) {
		this.esSolsticioDeInvierno = esSolsticioDeInvierno;
	}
	public String getSolsticiosDeInviernoSinceLastMetonIN() {
		return solsticiosDeInviernoSinceLastMetonIN;
	}
	public void setSolsticiosDeInviernoSinceLastMetonIN(String solsticiosDeInviernoSinceLastMetonIN) {
		this.solsticiosDeInviernoSinceLastMetonIN = solsticiosDeInviernoSinceLastMetonIN;
	}
	public int getNumberOfYear() {
		return numberOfYear;
	}
	public void setNumberOfYear(int numberOfYear) {
		this.numberOfYear = numberOfYear;
	}
	
	

}
