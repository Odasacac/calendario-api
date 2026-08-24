package CCASolutions.Calendario.DTOs;

import java.util.List;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MidsisonEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class DatosCosmicosParaVAUDTO {

	private List<SolsticiosYEquinocciosEntity> soes;
	private List<LunasEntity> lunas;
	private List<EclipsesEntity> eclipses;
	private List<EclipenosEntity> eclipenos;
	private List<MetonsEntity> metons;
	private List<ApogeosYPerigeosLunaEntity> apoperis;
	private List<MidsisonEntity> midsisons;
	private EclipenosEntity lastEclipenoIN;
	private EclipenosEntity lastEclipenoInvernalApofasalRemoto;
	private MetonsEntity lastMetonIN;
	private MetonsEntity lastMetonIApofasalRemoto;
	private boolean valido;
	private String mensaje;
	

	
	public List<MidsisonEntity> getMidsisons() {
		return midsisons;
	}
	public void setMidsisons(List<MidsisonEntity> midsisons) {
		this.midsisons = midsisons;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public boolean isValido() {
		return valido;
	}
	public void setValido(boolean valido) {
		this.valido = valido;
	}
	public MetonsEntity getLastMetonIApofasalRemoto() {
		return lastMetonIApofasalRemoto;
	}
	public void setLastMetonIApofasalRemoto(MetonsEntity lastMetonIApofasalRemoto) {
		this.lastMetonIApofasalRemoto = lastMetonIApofasalRemoto;
	}
	public EclipenosEntity getLastEclipenoInvernalApofasalRemoto() {
		return lastEclipenoInvernalApofasalRemoto;
	}
	public void setLastEclipenoInvernalApofasalRemoto(EclipenosEntity lastEclipenoInvernalApofasalRemoto) {
		this.lastEclipenoInvernalApofasalRemoto = lastEclipenoInvernalApofasalRemoto;
	}
	public List<ApogeosYPerigeosLunaEntity> getApoperis() {
		return apoperis;
	}
	public void setApoperis(List<ApogeosYPerigeosLunaEntity> apoperis) {
		this.apoperis = apoperis;
	}
	public EclipenosEntity getLastEclipenoIN() {
		return lastEclipenoIN;
	}
	public void setLastEclipenoIN(EclipenosEntity lastEclipenoIN) {
		this.lastEclipenoIN = lastEclipenoIN;
	}
	public MetonsEntity getLastMetonIN() {
		return lastMetonIN;
	}
	public void setLastMetonIN(MetonsEntity lastMetonIN) {
		this.lastMetonIN = lastMetonIN;
	}
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
