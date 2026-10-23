package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroUsuarioDTO {

	@NotBlank @Size(max=60)
	private String nombre;
	@NotBlank @Size(max=60)
	private String apellido;
	@NotNull @Past
	private LocalDate fechaNacimiento;
	@NotBlank @Email @Size(max=254)
	private String correo;
	@NotNull @Pattern(regexp=PoliticaContrasenya.PATRON, message=PoliticaContrasenya.MENSAJE)
	private String contrasenya;

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
}
