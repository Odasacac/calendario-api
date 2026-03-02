package CCASolutions.Calendario.DTOs;

public class YearDTO {
	private boolean esSolsticioDeInvierno;
	private int solsticiosDeInviernoPasadosDesdeLastMetonIN;
	
	public boolean isEsSolsticioDeInvierno() {
		return esSolsticioDeInvierno;
	}
	public void setEsSolsticioDeInvierno(boolean esSolsticioDeInvierno) {
		this.esSolsticioDeInvierno = esSolsticioDeInvierno;
	}
	public int getSolsticiosDeInviernoPasadosDesdeLastMetonIN() {
		return solsticiosDeInviernoPasadosDesdeLastMetonIN;
	}
	public void setSolsticiosDeInviernoPasadosDesdeLastMetonIN(int solsticiosDeInviernoPasadosDesdeLastMetonIN) {
		this.solsticiosDeInviernoPasadosDesdeLastMetonIN = solsticiosDeInviernoPasadosDesdeLastMetonIN;
	}

	
}
