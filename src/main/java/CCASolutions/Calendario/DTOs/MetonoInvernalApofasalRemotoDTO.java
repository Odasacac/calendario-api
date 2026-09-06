package CCASolutions.Calendario.DTOs;

/**
 * EN: Position within the cycle of winter apofasal remote metons: a winter solstice with a
 * new moon at apogee. It is the longest cycle below the eclipeno itself.
 * ES: Posición dentro del ciclo de métonos invernales apofasales remotos: un solsticio de
 * invierno con luna nueva en apogeo. Es el ciclo más largo por debajo del propio eclípeno.
 */
public class MetonoInvernalApofasalRemotoDTO {
	
	private boolean metonoInvernalApofasalRemotoDay;
	private int yearOfCurrentMetonoInvernalApofasalRemoto;
	private int metonosInvernalApofasalRemotoSinceLastEclipenoINSelecto;
	private int numberOfMetonoInvernalApofasalRemoto;
	
	public boolean isMetonoInvernalApofasalRemotoDay() {
		return metonoInvernalApofasalRemotoDay;
	}
	public void setMetonoInvernalApofasalRemotoDay(boolean metonoInvernalApofasalRemotoDay) {
		this.metonoInvernalApofasalRemotoDay = metonoInvernalApofasalRemotoDay;
	}
	public int getYearOfCurrentMetonoInvernalApofasalRemoto() {
		return yearOfCurrentMetonoInvernalApofasalRemoto;
	}
	public void setYearOfCurrentMetonoInvernalApofasalRemoto(int yearOfCurrentMetonoInvernalApofasalRemoto) {
		this.yearOfCurrentMetonoInvernalApofasalRemoto = yearOfCurrentMetonoInvernalApofasalRemoto;
	}
	public int getMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto() {
		return metonosInvernalApofasalRemotoSinceLastEclipenoINSelecto;
	}
	public void setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(
			int metonosInvernalApofasalRemotoSinceLastEclipenoINSelecto) {
		this.metonosInvernalApofasalRemotoSinceLastEclipenoINSelecto = metonosInvernalApofasalRemotoSinceLastEclipenoINSelecto;
	}
	public int getNumberOfMetonoInvernalApofasalRemoto() {
		return numberOfMetonoInvernalApofasalRemoto;
	}
	public void setNumberOfMetonoInvernalApofasalRemoto(int numberOfMetonoInvernalApofasalRemoto) {
		this.numberOfMetonoInvernalApofasalRemoto = numberOfMetonoInvernalApofasalRemoto;
	}
	

}
