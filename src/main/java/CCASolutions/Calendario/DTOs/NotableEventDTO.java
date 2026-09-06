package CCASolutions.Calendario.DTOs;

/**
 * EN: The three notable events around a date, already named and formatted: the one falling
 * on the date itself, the closest previous one and the closest upcoming one.
 * ES: Los tres eventos notables alrededor de una fecha, ya nombrados y formateados: el que
 * cae en la propia fecha, el anterior más cercano y el próximo más cercano.
 */
public class NotableEventDTO {

	private String today;
	private String next;
	private String previous;
	
	
	public String getToday() {
		return today;
	}
	public void setToday(String today) {
		this.today = today;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}	
	
}
