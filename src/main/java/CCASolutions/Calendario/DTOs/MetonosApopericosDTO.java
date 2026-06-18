package CCASolutions.Calendario.DTOs;

public class MetonosApopericosDTO {
	
	private boolean metonoApopericoIADay;
	private int metonoApopericosIASinceLastEclipenoIN;
	private int yearOfCurrentMetonIA;
	private int numberOfMetonApoperico;
	private String lastMetonApopericoSurname;
	public boolean isMetonoApopericoIADay() {
		return metonoApopericoIADay;
	}
	public void setMetonoApopericoIADay(boolean metonoApopericoIADay) {
		this.metonoApopericoIADay = metonoApopericoIADay;
	}
	public int getMetonoApopericosIASinceLastEclipenoIN() {
		return metonoApopericosIASinceLastEclipenoIN;
	}
	public void setMetonoApopericosIASinceLastEclipenoIN(int metonoApopericosIASinceLastEclipenoIN) {
		this.metonoApopericosIASinceLastEclipenoIN = metonoApopericosIASinceLastEclipenoIN;
	}
	public int getYearOfCurrentMetonIA() {
		return yearOfCurrentMetonIA;
	}
	public void setYearOfCurrentMetonIA(int yearOfCurrentMetonIA) {
		this.yearOfCurrentMetonIA = yearOfCurrentMetonIA;
	}
	public int getNumberOfMetonApoperico() {
		return numberOfMetonApoperico;
	}
	public void setNumberOfMetonApoperico(int numberOfMetonApoperico) {
		this.numberOfMetonApoperico = numberOfMetonApoperico;
	}
	public String getLastMetonApopericoSurname() {
		return lastMetonApopericoSurname;
	}
	public void setLastMetonApopericoSurname(String lastMetonApopericoSurname) {
		this.lastMetonApopericoSurname = lastMetonApopericoSurname;
	}
	
	
}
