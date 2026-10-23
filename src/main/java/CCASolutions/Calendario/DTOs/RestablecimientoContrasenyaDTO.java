package CCASolutions.Calendario.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RestablecimientoContrasenyaDTO {

	@NotBlank
	private String token;
	@NotNull @Pattern(regexp=PoliticaContrasenya.PATRON, message=PoliticaContrasenya.MENSAJE)
	private String nueva;
	@NotNull
	private String confirmacion;

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getNueva() {
		return nueva;
	}
	public void setNueva(String nueva) {
		this.nueva = nueva;
	}
	public String getConfirmacion() {
		return confirmacion;
	}
	public void setConfirmacion(String confirmacion) {
		this.confirmacion = confirmacion;
	}
}
