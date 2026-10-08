package CCASolutions.Calendario.DTOs;

import java.util.List;

import CCASolutions.Calendario.Entities.AllEclipsesEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;

public class EclipsesParaDBDTO {
	
	private List<EclipsesEntity> eclipse;
	private List<AllEclipsesEntity> allEclipse;
	
	public List<EclipsesEntity> getEclipse() {
		return eclipse;
	}
	public void setEclipse(List<EclipsesEntity> eclipse) {
		this.eclipse = eclipse;
	}
	public List<AllEclipsesEntity> getAllEclipse() {
		return allEclipse;
	}
	public void setAllEclipse(List<AllEclipsesEntity> allEclipse) {
		this.allEclipse = allEclipse;
	}

}
