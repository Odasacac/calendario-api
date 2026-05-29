package CCASolutions.Calendario.DTOs;

import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO {

	private LunasEntity lunaActual;
	private LunasEntity lunaProxima;
	private LunasEntity lunaAnterior;
	
	private SolsticiosYEquinocciosEntity soeActual;
	private SolsticiosYEquinocciosEntity soeProximo;
	private SolsticiosYEquinocciosEntity soeAnterior;
	
	private MetonsEntity metonoActual;
	private MetonsEntity metonoProximo;
	private MetonsEntity metonoAnterior;
	
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
	public MetonsEntity getMetonoActual() {
		return metonoActual;
	}
	public void setMetonoActual(MetonsEntity metonoActual) {
		this.metonoActual = metonoActual;
	}
	public MetonsEntity getMetonoProximo() {
		return metonoProximo;
	}
	public void setMetonoProximo(MetonsEntity metonoProximo) {
		this.metonoProximo = metonoProximo;
	}
	public MetonsEntity getMetonoAnterior() {
		return metonoAnterior;
	}
	public void setMetonoAnterior(MetonsEntity metonoAnterior) {
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
