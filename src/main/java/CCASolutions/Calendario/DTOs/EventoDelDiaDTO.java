package CCASolutions.Calendario.DTOs;

import CCASolutions.Calendario.Entities.ApoperisLunaresEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/**
 * Fenómenos que caen en un día concreto y el nombre del evento reseñable que
 * resulta de ellos (vacío si ese día no hay ninguno).
 */
public class EventoDelDiaDTO {

	private String nombre;
	private FasesLunaresEntity luna;
	private ApoperisLunaresEntity apoperi;
	private SolsticiosYEquinocciosEntity soe;
	private MetonsEntity meton;
	private EclipsesEntity eclipse;
	private EclipenosEntity eclipeno;
	private boolean midsison;

	public boolean hayEvento() {
		return nombre != null && !nombre.isBlank();
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public FasesLunaresEntity getLuna() {
		return luna;
	}
	public void setLuna(FasesLunaresEntity luna) {
		this.luna = luna;
	}
	public ApoperisLunaresEntity getApoperi() {
		return apoperi;
	}
	public void setApoperi(ApoperisLunaresEntity apoperi) {
		this.apoperi = apoperi;
	}
	public SolsticiosYEquinocciosEntity getSoe() {
		return soe;
	}
	public void setSoe(SolsticiosYEquinocciosEntity soe) {
		this.soe = soe;
	}
	public MetonsEntity getMeton() {
		return meton;
	}
	public void setMeton(MetonsEntity meton) {
		this.meton = meton;
	}
	public EclipsesEntity getEclipse() {
		return eclipse;
	}
	public void setEclipse(EclipsesEntity eclipse) {
		this.eclipse = eclipse;
	}
	public EclipenosEntity getEclipeno() {
		return eclipeno;
	}
	public void setEclipeno(EclipenosEntity eclipeno) {
		this.eclipeno = eclipeno;
	}
	public boolean isMidsison() {
		return midsison;
	}
	public void setMidsison(boolean midsison) {
		this.midsison = midsison;
	}
}
