package CCASolutions.Calendario.DTOs;

public class YearDTO {
	
	private boolean esSolsticioDeInvierno;
	private int solsticiosDeInviernoSinceLastMetonIN;
	private int numberOfYear;
	
	public boolean isEsSolsticioDeInvierno() {
		return esSolsticioDeInvierno;
	}
	public void setEsSolsticioDeInvierno(boolean esSolsticioDeInvierno) {
		this.esSolsticioDeInvierno = esSolsticioDeInvierno;
	}
	public int getSolsticiosDeInviernoSinceLastMetonIN() {
		return solsticiosDeInviernoSinceLastMetonIN;
	}
	public void setSolsticiosDeInviernoSinceLastMetonIN(int solsticiosDeInviernoSinceLastMetonIN) {
		this.solsticiosDeInviernoSinceLastMetonIN = solsticiosDeInviernoSinceLastMetonIN;
	}
	public int getNumberOfYear() {
		return numberOfYear;
	}
	public void setNumberOfYear(int numberOfYear) {
		this.numberOfYear = numberOfYear;
	}
	
	

}
