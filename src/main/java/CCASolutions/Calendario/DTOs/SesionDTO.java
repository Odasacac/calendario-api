package CCASolutions.Calendario.DTOs;

import java.time.Instant;

public class SesionDTO {

	private String token;
	private Instant expiraEn;
	/** true si el login ha levantado una suspensión del propio usuario. */
	private boolean reactivada;
	private PerfilUsuarioDTO usuario;

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Instant getExpiraEn() {
		return expiraEn;
	}
	public void setExpiraEn(Instant expiraEn) {
		this.expiraEn = expiraEn;
	}
	public boolean isReactivada() {
		return reactivada;
	}
	public void setReactivada(boolean reactivada) {
		this.reactivada = reactivada;
	}
	public PerfilUsuarioDTO getUsuario() {
		return usuario;
	}
	public void setUsuario(PerfilUsuarioDTO usuario) {
		this.usuario = usuario;
	}
}
