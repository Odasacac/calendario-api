package CCASolutions.Calendario.DTOs;

import java.util.List;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO {

	private LunasEntity lunaActual;
	private LunasEntity lunaProxima;
	private LunasEntity lunaAnterior;
	
	private ApogeosYPerigeosLunaEntity apoperiActual;
	private ApogeosYPerigeosLunaEntity apoperiProximo;
	private ApogeosYPerigeosLunaEntity apoperiAnterior;
	
	private SolsticiosYEquinocciosEntity soeActual;
	private SolsticiosYEquinocciosEntity soeProximo;
	private SolsticiosYEquinocciosEntity soeAnterior;
	
	private List<MetonsEntity> metonoActual;
	private List<MetonsEntity> metonoProximo;
	private List<MetonsEntity> metonoAnterior;
	
	private EclipsesEntity eclipseActual;
	private EclipsesEntity eclipseProximo;
	private EclipsesEntity eclipseAnterior;
	
	private EclipenosEntity eclipenoActual;
	private EclipenosEntity eclipenoProximo;
	private EclipenosEntity eclipenoAnterior;
	
	public LunasEntity getLunaActual() {
		return lunaActual;
	}
	public void setLunaActual(LunasEntity lunaActual) {
		this.lunaActual = lunaActual;
	}
	public LunasEntity getLunaProxima() {
		return lunaProxima;
	}
	public void setLunaProxima(LunasEntity lunaProxima) {
		this.lunaProxima = lunaProxima;
	}
	public LunasEntity getLunaAnterior() {
		return lunaAnterior;
	}
	public void setLunaAnterior(LunasEntity lunaAnterior) {
		this.lunaAnterior = lunaAnterior;
	}
	public ApogeosYPerigeosLunaEntity getApoperiActual() {
		return apoperiActual;
	}
	public void setApoperiActual(ApogeosYPerigeosLunaEntity apoperiActual) {
		this.apoperiActual = apoperiActual;
	}
	public ApogeosYPerigeosLunaEntity getApoperiProximo() {
		return apoperiProximo;
	}
	public void setApoperiProximo(ApogeosYPerigeosLunaEntity apoperiProximo) {
		this.apoperiProximo = apoperiProximo;
	}
	public ApogeosYPerigeosLunaEntity getApoperiAnterior() {
		return apoperiAnterior;
	}
	public void setApoperiAnterior(ApogeosYPerigeosLunaEntity apoperiAnterior) {
		this.apoperiAnterior = apoperiAnterior;
	}
	public SolsticiosYEquinocciosEntity getSoeActual() {
		return soeActual;
	}
	public void setSoeActual(SolsticiosYEquinocciosEntity soeActual) {
		this.soeActual = soeActual;
	}
	public SolsticiosYEquinocciosEntity getSoeProximo() {
		return soeProximo;
	}
	public void setSoeProximo(SolsticiosYEquinocciosEntity soeProximo) {
		this.soeProximo = soeProximo;
	}
	public SolsticiosYEquinocciosEntity getSoeAnterior() {
		return soeAnterior;
	}
	public void setSoeAnterior(SolsticiosYEquinocciosEntity soeAnterior) {
		this.soeAnterior = soeAnterior;
	}
	public List<MetonsEntity> getMetonoActual() {
		return metonoActual;
	}
	public void setMetonoActual(List<MetonsEntity> metonoActual) {
		this.metonoActual = metonoActual;
	}
	public List<MetonsEntity> getMetonoProximo() {
		return metonoProximo;
	}
	public void setMetonoProximo(List<MetonsEntity> metonoProximo) {
		this.metonoProximo = metonoProximo;
	}
	public List<MetonsEntity> getMetonoAnterior() {
		return metonoAnterior;
	}
	public void setMetonoAnterior(List<MetonsEntity> metonoAnterior) {
		this.metonoAnterior = metonoAnterior;
	}
	public EclipsesEntity getEclipseActual() {
		return eclipseActual;
	}
	public void setEclipseActual(EclipsesEntity eclipseActual) {
		this.eclipseActual = eclipseActual;
	}
	public EclipsesEntity getEclipseProximo() {
		return eclipseProximo;
	}
	public void setEclipseProximo(EclipsesEntity eclipseProximo) {
		this.eclipseProximo = eclipseProximo;
	}
	public EclipsesEntity getEclipseAnterior() {
		return eclipseAnterior;
	}
	public void setEclipseAnterior(EclipsesEntity eclipseAnterior) {
		this.eclipseAnterior = eclipseAnterior;
	}
	public EclipenosEntity getEclipenoActual() {
		return eclipenoActual;
	}
	public void setEclipenoActual(EclipenosEntity eclipenoActual) {
		this.eclipenoActual = eclipenoActual;
	}
	public EclipenosEntity getEclipenoProximo() {
		return eclipenoProximo;
	}
	public void setEclipenoProximo(EclipenosEntity eclipenoProximo) {
		this.eclipenoProximo = eclipenoProximo;
	}
	public EclipenosEntity getEclipenoAnterior() {
		return eclipenoAnterior;
	}
	public void setEclipenoAnterior(EclipenosEntity eclipenoAnterior) {
		this.eclipenoAnterior = eclipenoAnterior;
	}	
}
