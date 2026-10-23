package CCASolutions.Calendario.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EliminacionCuentaDTO {

	@NotBlank @Size(max=128)
	private String contrasenya;

	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
}
