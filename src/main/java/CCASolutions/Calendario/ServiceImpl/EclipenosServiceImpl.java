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
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAll();
		List<MetonsEntity> metonos = this.metonsRepository.findAll();
		
		if(allEclipenos.isEmpty() && !metonos.isEmpty()) {
			try {
				
				for (MetonsEntity meton : metonos) {
					
					System.out.println("Actualizando los eclípenos del anyo: " + meton.getYear());
					List<EclipsesEntity> eclipses = this.eclipsesRepository.findByYear(meton.getYear());
					
					if(meton.isFasal()) {
						
						for(EclipsesEntity eclipse : eclipses) {
							
							long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipse.getDate(), meton.getDate()));
							
							if(segundosDeDiferencia <= 86164) {
								
								EclipenosEntity eclipeno = new EclipenosEntity();
								
								eclipeno.setDate(meton.getDate());
								eclipeno.setYear(meton.getYear());
								
								eclipeno.setInvernal(meton.isInvernal());
								eclipeno.setPrimaveral(meton.isPrimaveral());
								eclipeno.setEstival(meton.isEstival());
								eclipeno.setOtonyal(meton.isOtonyal());
								
								eclipeno.setLleno(meton.isLleno());
								eclipeno.setNuevo(meton.isNuevo());
								
								eclipeno.setEsAnular(eclipse.isEsAnular());
								eclipeno.setEsParcial(eclipse.isEsParcial());
								eclipeno.setEsTotal(eclipse.isEsTotal());
								eclipeno.setEsPenumbral(eclipse.isEsPenumbral());
								eclipeno.setEsHibrido(eclipse.isEsHibrido());	
								
								eclipeno.setEclipseId(eclipse.getId());
								eclipeno.setMetonoId(meton.getId());
								eclipeno.setInvertido(meton.isInvertido());
								eclipeno.setSelecto(meton.isSelecto());
														
								this.eclipenosRepository.save(eclipeno);
							}
						}
					}
					
				}										
			}
			catch (Exception e) {
				System.out.println("Error al evaluar los eclipenos: " + e);
					resultado = "Error al evaluar los eclipenos, revisar logs";
			}
				
		}
		else {
			if(!allEclipenos.isEmpty()) {
				System.out.println("Ya hay eclípenos en la base de datos.");
			}
			else if(!metonos.isEmpty()){
				System.out.println("No hay métonos en la base de datos.");
			}
			
			resultado = "Error al actualizar los eclípenos, checkear logs.";
		}

		

		return resultado;
	}

}
