package CCASolutions.Calendario.DTOs;

/**
 * EN: The two winter meton cycles of a VAU date: the new ones (IN, solstice with a new
 * moon) and the aporic ones (IA, solstice with an apogee).
 * ES: Los dos ciclos de métonos invernales de una fecha VAU: los nuevos (IN, solsticio con
 * luna nueva) y los apóricos (IA, solsticio con apogeo).
 */
public class MetonDTO {

	private MetonINDTO metonsIN;
	private MetonIADTO metonsIA;
	
	public MetonINDTO getMetonsIN() {
		return metonsIN;
	}
	public void setMetonsIN(MetonINDTO metonsIN) {
		this.metonsIN = metonsIN;
	}
	public MetonIADTO getMetonsIA() {
		return metonsIA;
	}
	public void setMetonsIA(MetonIADTO metonsIA) {
		this.metonsIA = metonsIA;
	}
}
