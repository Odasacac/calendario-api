package CCASolutions.Calendario.DTOs;

/**
 * EN: State of the moon on a date. For now it only wraps its behaviour with respect to the
 * distance from the Earth.
 * ES: Estado de la luna en una fecha. Por ahora sólo envuelve su comportamiento respecto a
 * la distancia a la Tierra.
 */
public class EstadoLunaDTO {

	private ComportamientoLunaDTO comportamientoLunaDTO;

	public ComportamientoLunaDTO getComportamientoLunaDTO() {
		return comportamientoLunaDTO;
	}

	public void setComportamientoLunaDTO(ComportamientoLunaDTO comportamientoLunaDTO) {
		this.comportamientoLunaDTO = comportamientoLunaDTO;
	}
}
