package CCASolutions.Calendario.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CambioContrasenyaDTO {

	@NotBlank @Size(max=128)
	private String actual;
	@NotNull @Pattern(regexp=PoliticaContrasenya.PATRON, message=PoliticaContrasenya.MENSAJE)
	private String nueva;

	public String getActual() {
		return actual;
	}
	public void setActual(String actual) {
		this.actual = actual;
	}
	public String getNueva() {
		return nueva;
	}
	public void setNueva(String nueva) {
		this.nueva = nueva;
	}
}
