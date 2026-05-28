package CCASolutions.Calendario.DTOs;

import java.util.List;

import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class LunasSolsticiosEclipsesDTO {

	List<SolsticiosYEquinocciosEntity> soes;
	List<LunasEntity> lunas;
	List<EclipsesEntity> eclipses;
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
