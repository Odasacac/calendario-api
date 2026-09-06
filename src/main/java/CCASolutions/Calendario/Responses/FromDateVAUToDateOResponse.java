package CCASolutions.Calendario.Responses;

import java.time.LocalDate;

/**
 * EN: Response of the reverse conversion, from a VAU date back into a Gregorian one: the
 * resulting date plus any remarks about how it was resolved.
 * ES: Respuesta de la conversión inversa, de una fecha VAU de vuelta a una gregoriana: la
 * fecha resultante más los comentarios sobre cómo se ha resuelto.
 */
public class FromDateVAUToDateOResponse {

	private LocalDate dateO;
	private String comentarios;
	
	public LocalDate getDateO() {
		return dateO;
	}
	public void setDateO(LocalDate dateO) {
		this.dateO = dateO;
	}
	public String getComentarios() {
		return comentarios;
	}
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	
}
