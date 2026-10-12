package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;
import java.util.List;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class DatosCosmicosRangoDTO {

	private List<EclipenosEntity> eclipenos;
	private List<MetonsEntity> metons;
	private List<LunasEntity> lunas;
	private List<SolsticiosYEquinocciosEntity> soes;
	private List<EclipsesEntity> eclipses;
	private List<ApogeosYPerigeosLunaEntity> apoperis;

	private LocalDateTime metonsDesde;
	private LocalDateTime lunasDesde;
	private LocalDateTime soesDesde;
	private LocalDateTime eclipsesDesde;
	private LocalDateTime apoperisDesde;

	private boolean valido;


	public List<EclipenosEntity> getEclipenos() {
		return eclipenos;
	}
	public void setEclipenos(List<EclipenosEntity> eclipenos) {
		this.eclipenos = eclipenos;
	}
	public List<MetonsEntity> getMetons() {
		return metons;
	}
	public void setMetons(List<MetonsEntity> metons) {
		this.metons = metons;
	}
	public List<LunasEntity> getLunas() {
		return lunas;
	}
	public void setLunas(List<LunasEntity> lunas) {
		this.lunas = lunas;
	}
	public List<SolsticiosYEquinocciosEntity> getSoes() {
		return soes;
	}
	public void setSoes(List<SolsticiosYEquinocciosEntity> soes) {
		this.soes = soes;
	}
	public List<EclipsesEntity> getEclipses() {
		return eclipses;
	}
	public void setEclipses(List<EclipsesEntity> eclipses) {
		this.eclipses = eclipses;
	}
	public List<ApogeosYPerigeosLunaEntity> getApoperis() {
		return apoperis;
	}
	public void setApoperis(List<ApogeosYPerigeosLunaEntity> apoperis) {
		this.apoperis = apoperis;
	}
	public LocalDateTime getMetonsDesde() {
		return metonsDesde;
	}
	public void setMetonsDesde(LocalDateTime metonsDesde) {
		this.metonsDesde = metonsDesde;
	}
	public LocalDateTime getLunasDesde() {
		return lunasDesde;
	}
	public void setLunasDesde(LocalDateTime lunasDesde) {
		this.lunasDesde = lunasDesde;
	}
	public LocalDateTime getSoesDesde() {
		return soesDesde;
	}
	public void setSoesDesde(LocalDateTime soesDesde) {
		this.soesDesde = soesDesde;
	}
	public LocalDateTime getEclipsesDesde() {
		return eclipsesDesde;
	}
	public void setEclipsesDesde(LocalDateTime eclipsesDesde) {
		this.eclipsesDesde = eclipsesDesde;
	}
	public LocalDateTime getApoperisDesde() {
		return apoperisDesde;
	}
	public void setApoperisDesde(LocalDateTime apoperisDesde) {
		this.apoperisDesde = apoperisDesde;
	}
	public boolean isValido() {
		return valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
}
