package CCASolutions.Calendario.DTOs;

import java.util.List;

import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class LunasSolsticiosEclipsesMetonosYEclipenosDTO {

	List<SolsticiosYEquinocciosEntity> soes;
	List<LunasEntity> lunas;
	List<EclipsesEntity> eclipses;
	List<EclipenosEntity> eclipenos;
	List<MetonsEntity> metons;
	
	
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
	public List<SolsticiosYEquinocciosEntity> getSoes() {
		return soes;
	}
	public void setSoes(List<SolsticiosYEquinocciosEntity> soes) {
		this.soes = soes;
	}
	public List<LunasEntity> getLunas() {
		return lunas;
	}
	public void setLunas(List<LunasEntity> lunas) {
		this.lunas = lunas;
	}
	public List<EclipsesEntity> getEclipses() {
		return eclipses;
	}
	public void setEclipses(List<EclipsesEntity> eclipses) {
		this.eclipses = eclipses;
	}
	
	
}
