package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.FractionalSeconds;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Acontecimiento que un usuario apunta en un día del calendario. Como máximo uno por usuario y día. */
@Entity
@Table(name="acontecimientos_destacados", uniqueConstraints=@UniqueConstraint(name="uk_acontecimiento_usuario_fecha", columnNames={"usuario_id", "fecha"}))
public class AcontecimientoDestacadoEntity implements Serializable {

	private static final long serialVersionUID = 3147295520384461197L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="usuario_id", nullable=false)
	private UsuarioEntity usuario;

	@Column(nullable=false)
	private LocalDate fecha;

	@Column(nullable=false, length=1000)
	private String descripcion;

	@Column(nullable=false)
	@FractionalSeconds(0)
	private LocalDateTime fechaCreacion;

	@Column(nullable=false)
	@FractionalSeconds(0)
	private LocalDateTime fechaModificacion;

	/*
	 * Evento reseñable de ese día, calculado al crear o modificar. Los ids
	 * apuntan a los fenómenos que caen ese día y solo se rellenan si hay evento.
	 */
	@Column(nullable=false)
	private boolean notableEvent;

	@Column(length=200)
	private String notableEventType;

	private Long faseLunarId;
	private Long apoperiId;
	private Long soeId;
	private Long metonoId;
	private Long eclipseId;
	private Long eclipenoId;
	private Long midsisonId;
	private Long casaleroId;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UsuarioEntity getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioEntity usuario) {
		this.usuario = usuario;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	public boolean isNotableEvent() {
		return notableEvent;
	}
	public void setNotableEvent(boolean notableEvent) {
		this.notableEvent = notableEvent;
	}
	public String getNotableEventType() {
		return notableEventType;
	}
	public void setNotableEventType(String notableEventType) {
		this.notableEventType = notableEventType;
	}
	public Long getFaseLunarId() {
		return faseLunarId;
	}
	public void setFaseLunarId(Long faseLunarId) {
		this.faseLunarId = faseLunarId;
	}
	public Long getApoperiId() {
		return apoperiId;
	}
	public void setApoperiId(Long apoperiId) {
		this.apoperiId = apoperiId;
	}
	public Long getSoeId() {
		return soeId;
	}
	public void setSoeId(Long soeId) {
		this.soeId = soeId;
	}
	public Long getMetonoId() {
		return metonoId;
	}
	public void setMetonoId(Long metonoId) {
		this.metonoId = metonoId;
	}
	public Long getEclipseId() {
		return eclipseId;
	}
	public void setEclipseId(Long eclipseId) {
		this.eclipseId = eclipseId;
	}
	public Long getEclipenoId() {
		return eclipenoId;
	}
	public void setEclipenoId(Long eclipenoId) {
		this.eclipenoId = eclipenoId;
	}
	public Long getMidsisonId() {
		return midsisonId;
	}
	public void setMidsisonId(Long midsisonId) {
		this.midsisonId = midsisonId;
	}
	public Long getCasaleroId() {
		return casaleroId;
	}
	public void setCasaleroId(Long casaleroId) {
		this.casaleroId = casaleroId;
	}
}
