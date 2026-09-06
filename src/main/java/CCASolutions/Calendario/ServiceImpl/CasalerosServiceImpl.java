package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
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
	
	
	
	public String poblateCasaleros() {
			
		/*
			Un casalero siempre tiene dos apellido: es el año y el tipo
				
			Casalero X del año Y, ¿qué quiere decir?
				
			Y: El año en el ocurrió el eclípeno al que este Casalero hace referencia
			X: Nombre referente al fenómeno que ocurrirá primero despues de que haya ocurrido el eclípeno
				
			Metónico: Métono
			Eclipelar: Eclipse absoluto			
			
			
		*/
			
		System.out.println("Iniciando poblar Casaleros.");
			
		String resultado = "Todo ha salido mal";
		try {
			List<CasalerosEntity> allCasaleros = this.casalerosRepository.findAll();
			List<EclipenosEntity> eclipenos = this.eclipenosRepository.findAll();
			
			if(allCasaleros.isEmpty() && !eclipenos.isEmpty()) {
				try {				
													
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
						
					resultado = "Casaleros poblados correctamente.";
								
				}			
				catch(Exception e) {
					System.out.println("Error al acceder a la base de datos a recoger los eclipenos: " + e.getMessage());
					resultado = "Error al actualizar los casaleros, checkear logs.";
				}
			}
			else {
				
				if(!allCasaleros.isEmpty()) {
					System.out.println("Ya hay casaleros en la base de datos.");
					resultado = "Error al actualizar los casaleros: ya hay casaleros en la base de datos.";
				}
				else if(eclipenos.isEmpty()){
					System.out.println("No hay eclípenos en la base de datos.");
					resultado = "Error al actualizar los casaleros: no hay eclípenos en la base de datos.";
				}		
			}
						
		}
		catch (Exception e) {
			System.out.println("No se ha podido conectar a la base de datos: " + e.getMessage());
			resultado = "Error al actualizar los casaleros, checkear logs.";
		}
		
		
		
		System.out.println("Poblate casaleros finalizado.");
			
			
		return resultado;
	}
	
	private EclipsesEntity getEclipseParaCasalero(EclipenosEntity eclipeno) {
		
		EclipsesEntity eclipse = null;
		
		eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(eclipeno.getDate());		
		
		return eclipse;
		
	}
	
	private EclipsesEntity getEclipseParaCasaleroConFecha(EclipenosEntity eclipeno, LocalDateTime fecha) {
		
		EclipsesEntity eclipse = null;

		eclipse = this.eclipsesRepository.findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(fecha);
		
		return eclipse;
		
	}
	
	
	private MetonsEntity getMetonoParaCasalero(EclipenosEntity eclipeno) {
		
		MetonsEntity metono = null;
		
		metono = this.metonsRepository.findFirstByDateAfterOrderByDateAsc(eclipeno.getDate());			
		
		return metono;
	}
}
