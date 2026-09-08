package CCASolutions.Calendario.DTOs;

import CCASolutions.Calendario.Entities.AllEclipsesEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;

public class EclipsesParaDBDTO {
	
	private EclipsesEntity eclipse;
	private AllEclipsesEntity allEclipse;
	
	
	public EclipsesEntity getEclipse() {
		return eclipse;
	}
	public void setEclipse(EclipsesEntity eclipse) {
		this.eclipse = eclipse;
	}
	public AllEclipsesEntity getAllEclipse() {
		return allEclipse;
	}
	public void setAllEclipse(AllEclipsesEntity allEclipse) {
		this.allEclipse = allEclipse;
	}
}
