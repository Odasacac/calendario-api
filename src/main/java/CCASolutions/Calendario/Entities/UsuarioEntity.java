package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;
import CCASolutions.Calendario.Enums.MotivoSuspensionEnum;
import CCASolutions.Calendario.Enums.RolUsuarioEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="usuarios")
public class UsuarioEntity implements Serializable {

	private static final long serialVersionUID = -4280367530987316521L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(nullable=false, length=60)
	private String nombre;

	@Column(nullable=false, length=60)
	private String apellido;

	@Column(nullable=false)
	private LocalDate fechaNacimiento;

	@Column(nullable=false)
	private boolean esMayorDeEdad;

	/** Siempre en minúsculas: la unicidad no distingue mayúsculas. */
	@Column(nullable=false, unique=true, length=254)
	private String correo;

	/** Hash BCrypt, nunca la contraseña en claro. */
	@Column(nullable=false, length=72)
	private String contrasenya;

	@Column(nullable=false, unique=true, length=80)
	private String codigoUsuario;

	@Enumerated(EnumType.STRING)
	@Column(nullable=false, length=20)
	private EstadoUsuarioEnum estado;

	/** USER por defecto; ADMIN solo se asigna a mano. */
	@Enumerated(EnumType.STRING)
	@Column(nullable=false, length=20)
	private RolUsuarioEnum rol = RolUsuarioEnum.USER;

	@Column(nullable=false)
	private LocalDateTime fechaCreacion;

	private LocalDateTime fechaLastLogin;
	private LocalDateTime fechaEliminacion;
	private LocalDateTime fechaSuspension;

	@Enumerated(EnumType.STRING)
	@Column(length=30)
	private MotivoSuspensionEnum motivoSuspension;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
	public String getCodigoUsuario() {
		return codigoUsuario;
	}
	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
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
	public LocalDateTime getFechaEliminacion() {
		return fechaEliminacion;
	}
	public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}
	public LocalDateTime getFechaSuspension() {
		return fechaSuspension;
	}
	public void setFechaSuspension(LocalDateTime fechaSuspension) {
		this.fechaSuspension = fechaSuspension;
	}
	public MotivoSuspensionEnum getMotivoSuspension() {
		return motivoSuspension;
	}
	public void setMotivoSuspension(MotivoSuspensionEnum motivoSuspension) {
		this.motivoSuspension = motivoSuspension;
	}
}
