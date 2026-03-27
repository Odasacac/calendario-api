package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.CasalerosService;

@Service
public class CasalerosServiceImpl implements CasalerosService {

	@Autowired 
	private CasalerosRepository casalerosRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	
	
	public String poblateCasaleros() {
			
		/*
			Un casalero siempre tiene dos apellido: es el año y el tipo
				
			Casalero X del año Y, ¿qué quiere decir?
				
			Y: El año en el ocurrió el eclípeno al que este Casalero hace referencia
			X: Nombre referente al fenómeno que ocurrirá primero despues de que haya ocurrido el eclípeno
				
			Metónico: Métono
			Eclipelar: Eclipse absoluto			
			
			
		*/
			
		System.out.println("Iniciando poblar Casaderos.");
			
		String resultado = "Todo ha salido mal";			
				
		try {
				
			List<EclipenosEntity> eclipenos = this.eclipenosRepository.findAll();
					
			if(!eclipenos.isEmpty()) {
					
				for(EclipenosEntity eclipeno : eclipenos) {
						
					System.out.println("Evaluando eclipeno año: " + eclipeno.getYear());
						
					CasalerosEntity casaleroParaDB = new CasalerosEntity();
						
					LocalDateTime eclipenoDate = eclipeno.getDate();
					
					MetonsEntity metono = this.getMetonoParaCasalero(eclipeno);	
					
					EclipsesEntity eclipseAbsoluto = this.getEclipseParaCasalero(eclipeno);

					if(metono != null && eclipseAbsoluto != null) {
						
						long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipeno.getDate(), eclipseAbsoluto.getDate()));
						
						if(segundosDeDiferencia <= 86164) {
							
							LocalDateTime nuevaFecha = eclipenoDate.plusSeconds(86164);
							eclipseAbsoluto = this.getEclipseParaCasaleroConFecha(eclipeno, nuevaFecha);
						}
							
						if(metono.getDate().isBefore(eclipseAbsoluto.getDate())) {
								
							casaleroParaDB.setMetonoId(metono.getId());
							casaleroParaDB.setDate(metono.getDate());

						}
						else if(eclipseAbsoluto.getDate().isBefore(metono.getDate())){
								
							casaleroParaDB.setEclipseId(eclipseAbsoluto.getId());
							casaleroParaDB.setDate(eclipseAbsoluto.getDate());
						}													
					}
					
					else {
						
						if(metono != null && eclipseAbsoluto == null) {
								
							casaleroParaDB.setMetonoId(metono.getId());	
							casaleroParaDB.setDate(metono.getDate());
						}
						else if (eclipseAbsoluto != null && metono == null) {
	
							long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipeno.getDate(), eclipseAbsoluto.getDate()));
							
							if(segundosDeDiferencia <= 86164) {
								
								LocalDateTime nuevaFecha = eclipenoDate.plusSeconds(86164);
								eclipseAbsoluto = this.getEclipseParaCasaleroConFecha(eclipeno, nuevaFecha);
							}
							casaleroParaDB.setEclipseId(eclipseAbsoluto.getId());
							casaleroParaDB.setDate(eclipseAbsoluto.getDate());
						}
					}
								
					if(casaleroParaDB.getDate() != null) {
						
						casaleroParaDB.setYear(casaleroParaDB.getDate().getYear());
						casaleroParaDB.setEclipenoId(eclipeno.getId());
						
						casalerosRepository.save(casaleroParaDB);
						System.out.println("Casalero almacenado, año: " + casaleroParaDB.getYear());
					}								
				}
				
				resultado = "Casaleros poblados correctamente";
						
			}
			else {
				resultado = "Eclípenos is empty";
			}
				
		}
		catch(Exception e) {
				
			resultado = "Error al acceder a la base de datos a recoger los eclipenos: " + e.getMessage();;
		}
			
		
		System.out.println("Poblate casaleros finalizado.");
			
			
		return resultado;
	}
	
	private EclipsesEntity getEclipseParaCasalero(EclipenosEntity eclipeno) {
		
		EclipsesEntity eclipse = null;
		
		if(Boolean.TRUE.equals(eclipeno.getLleno())) {
			eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeLunaIsTrueOrderByDateAsc(eclipeno.getDate());
		}
		else if (Boolean.TRUE.equals(eclipeno.getNuevo())) {
			eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeSolIsTrueOrderByDateAsc(eclipeno.getDate());
		}		
		
		return eclipse;
		
	}
	
	private EclipsesEntity getEclipseParaCasaleroConFecha(EclipenosEntity eclipeno, LocalDateTime fecha) {
		
		EclipsesEntity eclipse = null;
		
		if(Boolean.TRUE.equals(eclipeno.getLleno())) {
			eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeLunaIsTrueOrderByDateAsc(fecha);
		}
		else if (Boolean.TRUE.equals(eclipeno.getNuevo())) {
			eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseAndDeSolIsTrueOrderByDateAsc(fecha);
		}		
		
		return eclipse;
		
	}
	
	
	private MetonsEntity getMetonoParaCasalero(EclipenosEntity eclipeno) {
		
		MetonsEntity metono = null;
		
		if(Boolean.TRUE.equals(eclipeno.getInicial())) {
			
			if(Boolean.TRUE.equals(eclipeno.getNuevo())) {
				metono = this.metonsRepository.findFirstByDateAfterAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
			else if(Boolean.TRUE.equals(eclipeno.getLleno())) {
				metono = this.metonsRepository.findFirstByDateAfterAndInicialIsTrueAndLlenoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
		}
		else if(Boolean.TRUE.equals(eclipeno.getCuartal())) {
			
			if(Boolean.TRUE.equals(eclipeno.getNuevo())) {
				metono = this.metonsRepository.findFirstByDateAfterAndCuartalIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
			else if(Boolean.TRUE.equals(eclipeno.getLleno())) {
				metono = this.metonsRepository.findFirstByDateAfterAndCuartalIsTrueAndLlenoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
		}
		else if(Boolean.TRUE.equals(eclipeno.getBicuartal())) {
			
			if(Boolean.TRUE.equals(eclipeno.getNuevo())) {
				metono = this.metonsRepository.findFirstByDateAfterAndBicuartalIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
			else if(Boolean.TRUE.equals(eclipeno.getLleno())) {
				metono = this.metonsRepository.findFirstByDateAfterAndBicuartalIsTrueAndLlenoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
		}
		
		else if(Boolean.TRUE.equals(eclipeno.getTricuartal())) {
			
			if(Boolean.TRUE.equals(eclipeno.getNuevo())) {
				metono = this.metonsRepository.findFirstByDateAfterAndTricuartalIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
			else if(Boolean.TRUE.equals(eclipeno.getLleno())) {
				metono = this.metonsRepository.findFirstByDateAfterAndTricuartalIsTrueAndLlenoIsTrueOrderByDateAsc(eclipeno.getDate());			
			}
		}
		
		
		return metono;
	}
}
