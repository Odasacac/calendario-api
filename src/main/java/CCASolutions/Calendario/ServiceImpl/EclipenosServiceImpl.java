package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.EclipenosService;

@Service
public class EclipenosServiceImpl implements EclipenosService{

	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	public String poblateEclipenos() {
		
		String resultado = "Eclipenos actualizados sin problema.";		

		try {
			
			List<MetonsEntity> metonos = this.metonsRepository.findAll();
			
			for (MetonsEntity meton : metonos) {
				
				System.out.println("Actualizando los eclípenos del anyo: " + meton.getYear());
				List<EclipsesEntity> eclipses = this.eclipsesRepository.findByYear(meton.getYear());
				
				for(EclipsesEntity eclipse : eclipses) {
					
					long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipse.getDate(), meton.getDate()));
					
					if(segundosDeDiferencia <= 86164) {
						
						EclipenosEntity eclipeno = new EclipenosEntity();
						
						eclipeno.setDate(meton.getDate());
						eclipeno.setYear(meton.getYear());
						eclipeno.setInicial(meton.getInicial());
						eclipeno.setCuartal(meton.getCuartal());
						eclipeno.setBicuartal(meton.getBicuartal());
						eclipeno.setTricuartal(meton.getTricuartal());
						eclipeno.setLleno(meton.getLleno());
						eclipeno.setNuevo(meton.getNuevo());
						
						eclipeno.setEsAnular(eclipse.isEsAnular());
						eclipeno.setEsParcial(eclipse.isEsParcial());
						eclipeno.setEsTotal(eclipse.isEsTotal());
						eclipeno.setEsPenumbral(eclipse.isEsPenumbral());
						eclipeno.setEsHibrido(eclipse.isEsHibrido());				
												
						this.eclipenosRepository.save(eclipeno);
					}
				}
			}										
		}
		catch (Exception e) {
			System.out.println("Error al evaluar los eclipenos: " + e);
				resultado = "Error al evaluar los eclipenos, revisar logs";
		}
			

		return resultado;
	}

}
