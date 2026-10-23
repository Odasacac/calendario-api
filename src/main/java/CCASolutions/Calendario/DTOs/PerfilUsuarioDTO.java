package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;
import CCASolutions.Calendario.Enums.RolUsuarioEnum;

public class PerfilUsuarioDTO {

	private String codigoUsuario;
	private String nombre;
	private String apellido;
	private LocalDate fechaNacimiento;
	private boolean esMayorDeEdad;
	private String correo;
	private EstadoUsuarioEnum estado;
	private RolUsuarioEnum rol;
	private LocalDateTime fechaCreacion;
	private LocalDateTime fechaLastLogin;

	public String getCodigoUsuario() {
		return codigoUsuario;
	}
	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}
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
	public boolean isEsMayorDeEdad() {
		return esMayorDeEdad;
	}
	public void setEsMayorDeEdad(boolean esMayorDeEdad) {
		this.esMayorDeEdad = esMayorDeEdad;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public EstadoUsuarioEnum getEstado() {
		return estado;
	}
	public void setEstado(EstadoUsuarioEnum estado) {
		this.estado = estado;
	}
	public RolUsuarioEnum getRol() {
		return rol;
	}
	public void setRol(RolUsuarioEnum rol) {
		this.rol = rol;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public LocalDateTime getFechaLastLogin() {
		return fechaLastLogin;
	}
	public void setFechaLastLogin(LocalDateTime fechaLastLogin) {
		this.fechaLastLogin = fechaLastLogin;
	}
}
