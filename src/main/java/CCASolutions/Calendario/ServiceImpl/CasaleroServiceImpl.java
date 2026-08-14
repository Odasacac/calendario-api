package CCASolutions.Calendario.ServiceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.CasaleroService;

@Service
public class CasaleroServiceImpl implements CasaleroService{

	@Autowired
	private CasalerosRepository casalerosRepository;
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	public CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN) {
		
		CasaleroDTO casaleroDTO = null;
		
		try {
			
			CasalerosEntity casaleroEntity = casalerosRepository.findByEclipenoId(lastEclipenoIN.getId());
			
			if(casaleroEntity != null) {
				
				casaleroDTO = new CasaleroDTO();
				casaleroDTO.setDateO(casaleroEntity.getDate().toLocalDate());
				
				String tipo = "";
				if(casaleroEntity.getMetonoId() != null) {
					
					Optional<MetonsEntity> metonoOpt = this.metonsRepository.findById(casaleroEntity.getMetonoId());
					
					if(metonoOpt.isPresent()) {
						
						MetonsEntity metono = metonoOpt.get();
						
						tipo="Metónico";
						
						casaleroDTO.setLleno(metono.isLleno());
						casaleroDTO.setNuevo(metono.isNuevo());
						casaleroDTO.setInvernal(metono.isInvernal());
						casaleroDTO.setPrimaveral(metono.isPrimaveral());
						casaleroDTO.setEstival(metono.isEstival());	
						casaleroDTO.setOtonyal(metono.isOtonyal());
						casaleroDTO.setNuevo(true);
					}								
				}
				else if (casaleroEntity.getEclipseId() != null){
					
					Optional<EclipsesEntity> eclipseOpt = this.eclipsesRepository.findById(casaleroEntity.getEclipseId());
					
					if(eclipseOpt.isPresent()) {
						
						EclipsesEntity eclipse = eclipseOpt.get();
						
						tipo="Eclipelar";
						casaleroDTO.setDeSol(eclipse.isDeSol());
						casaleroDTO.setDeLuna(eclipse.isDeLuna());
					}				
				}
				
				casaleroDTO.setTipo(tipo);
							
			}	
		}
		catch(Exception e) {
			
			System.out.println("Error al obtener el casalero: " + e.getMessage());
		}
		
		return casaleroDTO;		
	}
	
}
