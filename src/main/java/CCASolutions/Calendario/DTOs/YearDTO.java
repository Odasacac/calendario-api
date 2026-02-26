package CCASolutions.Calendario.DTOs;

public class YearDTO {
	private boolean esSolsticioDeInvierno;
	private int year;
	
	public boolean isEsSolsticioDeInvierno() {
		return esSolsticioDeInvierno;
	}
	public void setEsSolsticioDeInvierno(boolean esSolsticioDeInvierno) {
		this.esSolsticioDeInvierno = esSolsticioDeInvierno;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
	
}
