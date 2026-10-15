package CCASolutions.Calendario.DTOs;


import CCASolutions.Calendario.Entities.ApoperisLunaresEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

public class LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO {

	private FasesLunaresEntity lunaActual;
	private FasesLunaresEntity lunaProxima;
	private FasesLunaresEntity lunaAnterior;
	
	private ApoperisLunaresEntity apoperiActual;
	private ApoperisLunaresEntity apoperiProximo;
	private ApoperisLunaresEntity apoperiAnterior;
	
	private SolsticiosYEquinocciosEntity soeActual;
	private SolsticiosYEquinocciosEntity soeProximo;
	private SolsticiosYEquinocciosEntity soeAnterior;
	
	private MidsisonDTO midsisonActual;
	private MidsisonDTO midsisonProximo;
	private MidsisonDTO midsisonAnterior;
	
	private MetonsEntity metonoActual;
	private MetonsEntity metonoProximo;
	private MetonsEntity metonoAnterior;
	
	private EclipsesEntity eclipseActual;
	private EclipsesEntity eclipseProximo;
	private EclipsesEntity eclipseAnterior;
	
	private EclipenosEntity eclipenoActual;
	private EclipenosEntity eclipenoProximo;
	private EclipenosEntity eclipenoAnterior;
	
	
	
	
	
	
	public MidsisonDTO getMidsisonActual() {
		return midsisonActual;
	}
	public void setMidsisonActual(MidsisonDTO midsisonActual) {
		this.midsisonActual = midsisonActual;
	}
	public MidsisonDTO getMidsisonProximo() {
		return midsisonProximo;
	}
	public void setMidsisonProximo(MidsisonDTO midsisonProximo) {
		this.midsisonProximo = midsisonProximo;
	}
	public MidsisonDTO getMidsisonAnterior() {
		return midsisonAnterior;
	}
	public void setMidsisonAnterior(MidsisonDTO midsisonAnterior) {
		this.midsisonAnterior = midsisonAnterior;
	}
	public FasesLunaresEntity getLunaActual() {
		return lunaActual;
	}
	public void setLunaActual(FasesLunaresEntity lunaActual) {
		this.lunaActual = lunaActual;
	}
	public FasesLunaresEntity getLunaProxima() {
		return lunaProxima;
	}
	public void setLunaProxima(FasesLunaresEntity lunaProxima) {
		this.lunaProxima = lunaProxima;
	}
	public FasesLunaresEntity getLunaAnterior() {
		return lunaAnterior;
	}
	public void setLunaAnterior(FasesLunaresEntity lunaAnterior) {
		this.lunaAnterior = lunaAnterior;
	}
	public ApoperisLunaresEntity getApoperiActual() {
		return apoperiActual;
	}
	public void setApoperiActual(ApoperisLunaresEntity apoperiActual) {
		this.apoperiActual = apoperiActual;
	}
	public ApoperisLunaresEntity getApoperiProximo() {
		return apoperiProximo;
	}
	public void setApoperiProximo(ApoperisLunaresEntity apoperiProximo) {
		this.apoperiProximo = apoperiProximo;
	}
	public ApoperisLunaresEntity getApoperiAnterior() {
		return apoperiAnterior;
	}
	public void setApoperiAnterior(ApoperisLunaresEntity apoperiAnterior) {
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
