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
				

			/*
			 Lo primero es coger todos los eclípenos
			 */
			List<EclipenosEntity> eclipenos = this.eclipenosRepository.findAll();
					
			if(!eclipenos.isEmpty()) {
					
				/*
				 	Para cada uno de ellos, vamos a coger el evento que ocurrió primero despues de su fecha
					*/
					
				for(EclipenosEntity eclipeno : eclipenos) {
						
					System.out.println("Evaluando eclipeno año: " + eclipeno.getYear());
						
					CasalerosEntity casaleroParaDB = new CasalerosEntity();
						
					LocalDateTime eclipenoDate = eclipeno.getDate();
						
					MetonsEntity metono = this.metonsRepository.findFirstByDateAfterOrderByDateAsc(eclipenoDate);						
					EclipsesEntity eclipseAbsoluto = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(eclipenoDate);

					if(metono != null && eclipseAbsoluto != null) {
						
						long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipeno.getDate(), eclipseAbsoluto.getDate()));
						
						if(segundosDeDiferencia <= 86164) {
							
							eclipseAbsoluto = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(eclipenoDate.plusSeconds(86164));
						}
							
						if(metono.getDate().isBefore(eclipseAbsoluto.getDate())) {
								
							casaleroParaDB = this.createCasaleroMetonico(metono);

						}
						else if(eclipseAbsoluto.getDate().isBefore(metono.getDate())){
								
							casaleroParaDB = this.createCasaleroEclipelar(eclipseAbsoluto);
						}													
					}
					
					else {
						
						if(metono != null && eclipseAbsoluto == null) {
								
							casaleroParaDB = this.createCasaleroMetonico(metono);								
						}
						else if (eclipseAbsoluto != null && metono == null) {
	
							long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipeno.getDate(), eclipseAbsoluto.getDate()));
							
							if(segundosDeDiferencia <= 86164) {
								
								eclipseAbsoluto = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(eclipenoDate.plusSeconds(86164));
							}
							casaleroParaDB = this.createCasaleroEclipelar(eclipseAbsoluto);
						}
					}
												
					casaleroParaDB.setEclipenDate(eclipeno.getDate());
					casaleroParaDB.setEclipenYear(eclipeno.getYear());
					casaleroParaDB.setEclipenoId(eclipeno.getId());
					
					if(Boolean.TRUE.equals(eclipeno.getInicial()) && Boolean.TRUE.equals(eclipeno.getNuevo())) {

						casaleroParaDB.setEclipenoInicialNuevo(true);
					}
					
					casalerosRepository.save(casaleroParaDB);
					System.out.println("Casalero almacenado, año: " + casaleroParaDB.getYear());
						
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
		
		
	private CasalerosEntity createCasaleroMetonico (MetonsEntity metono) {
		
		CasalerosEntity casalero = new CasalerosEntity();
		
		casalero.setDate(metono.getDate());
		casalero.setYear(metono.getYear());		

		casalero.setMetonicoInicial(Boolean.TRUE.equals(metono.getInicial()));
		casalero.setMetonicoBicuartal(Boolean.TRUE.equals(metono.getBicuartal()));
		casalero.setMetonicoNuevo(Boolean.TRUE.equals(metono.getNuevo()));
		casalero.setMetonicoLleno(Boolean.TRUE.equals(metono.getLleno()));	
		casalero.setMetonicoCuartal(Boolean.TRUE.equals(metono.getCuartal()));
		casalero.setMetonicoTricuartal(Boolean.TRUE.equals(metono.getTricuartal()));
		
		casalero.setMetonico(true);
		casalero.setMetonoId(metono.getId());
					
		return casalero;		
	}
	
	private CasalerosEntity createCasaleroEclipelar (EclipsesEntity eclipse) {
		
		CasalerosEntity casalero = new CasalerosEntity();		
		
		casalero.setDate(eclipse.getDate());
		casalero.setYear(eclipse.getYear());
		
		if(eclipse.isDeSol()) {
			casalero.setEclipelarDeSol(true);
			
		}
		else if(eclipse.isDeLuna()) {
			casalero.setEclipelarDeLuna(true);
		}
		
		
		casalero.setEclipelar(true);
		casalero.setEclipseId(eclipse.getId());
		
		return casalero;
		
	}
}
