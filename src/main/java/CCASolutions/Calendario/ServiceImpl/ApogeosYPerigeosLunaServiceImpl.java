package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AGPDTO;
import CCASolutions.Calendario.DTOs.ApogeosDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Services.ApogeosYPerigeosLunaService;

@Service
public class ApogeosYPerigeosLunaServiceImpl implements ApogeosYPerigeosLunaService{

	@Autowired
	private DatosRepository datosRepository;	
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaServiceRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private static final String APOGEO = "MaximalDistance";
	private static final String PERIGEO = "MinimalDistance";
	private static final DateTimeFormatter FORMATTER_API_REQUEST =
	        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final DateTimeFormatter FORMATTER_API_RESPONSE =
	        new DateTimeFormatterBuilder()
	                .appendPattern("yyyy-MM-dd'T'HH:mm:")
	                .appendValue(ChronoField.SECOND_OF_MINUTE)
	                .optionalStart()
	                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
	                .optionalEnd()
	                .toFormatter();
	
	public String poblateApogeos() {
		
		String resultado = "Apogeos actualizados sin problema.";
		
		DatosEntity apiGetApogeosUrl = datosRepository.findByConcepto("APG");
		String apogeosUrl = apiGetApogeosUrl.getValor();
		
		List<ApogeosYPerigeosLunaEntity> apogeosYPerigeosExistentesEnBD = this.apogeosYPerigeosLunaServiceRepository.findAll();
		
		if(apogeosYPerigeosExistentesEnBD.isEmpty()) {
			
			List<ApogeosDTO> allApogeosAPI = this.getApogeosViaAPI(apogeosUrl);
			
			if(!allApogeosAPI.isEmpty()) {
				
				List<ApogeosYPerigeosLunaEntity> apogeosParaDB = new ArrayList<>();
				for(ApogeosDTO apogeo : allApogeosAPI) {
							
					ApogeosYPerigeosLunaEntity apogeoParaDB = new ApogeosYPerigeosLunaEntity();
					apogeoParaDB.setDate(LocalDateTime.parse(apogeo.getDate(), FORMATTER_API_RESPONSE));
						
					switch (apogeo.getPhenomena()) {
							
						case APOGEO:
							apogeoParaDB.setEsApogeo(true);
							break;
							
						case PERIGEO:
							apogeoParaDB.setEsPerigeo(true);
							break;
					}
					apogeoParaDB.setDistance(apogeo.getDistance());							
					
					apogeosParaDB.add(apogeoParaDB);
				}
				this.apogeosYPerigeosLunaServiceRepository.saveAll(apogeosParaDB);
				apogeosYPerigeosExistentesEnBD = apogeosParaDB;
				System.out.println("Apogeos almacenados en la BD.");
			}	
			else {
				System.out.println("No se han obtenido apogeos por la API.");				
				resultado="Error al actualizar los apogeos, chequear logs.";
			}
			
		}
		if (!apogeosYPerigeosExistentesEnBD.isEmpty()) {
			
			System.out.println("Comparando lunas con apogeos...");
			List<LunasEntity> allLunas = this.lunasRepository.findAll();
			
			if(!allLunas.isEmpty()) {
				
				for (ApogeosYPerigeosLunaEntity apoperi : apogeosYPerigeosExistentesEnBD)
				{
					for(LunasEntity luna : allLunas) {
						
						long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(luna.getDate(), apoperi.getDate()));
									
						if(segundosDeDiferencia <= 86164) {								
								
							if(luna.isLlena()) {
									
								if(apoperi.isEsApogeo()) {
									luna.setHibrida(true);
									luna.setTransicionada(true);
									this.lunasRepository.save(luna);
									System.out.println("Luna transicionada encontrada en " + luna.getDate());
								}
								else if(apoperi.isEsPerigeo()) {
									luna.setHibrida(true);
									luna.setSelecta(true);
									this.lunasRepository.save(luna);
									System.out.println("Luna selecta encontrada en " + luna.getDate());
								}								
							}
							else if (luna.isNueva()){
									
								if(apoperi.isEsPerigeo()) {
									luna.setHibrida(true);
									luna.setTransicionada(true);
									this.lunasRepository.save(luna);
									System.out.println("Luna transicionada encontrada en " + luna.getDate());
								}
								else if(apoperi.isEsApogeo()) {
									luna.setHibrida(true);
									luna.setSelecta(true);
									this.lunasRepository.save(luna);
									System.out.println("Luna selecta encontrada en " + luna.getDate());
								}
										
							}
						}			
					}			
				}
			}
		}
		else {
			System.out.println("No hay lunas en la base de datos.");				
			resultado="Error al actualizar las lunas con sus apogeos, chequear logs.";
		}
				
		
		return resultado;
	}



	private List<ApogeosDTO> getApogeosViaAPI(String url){
		
		List<ApogeosDTO> allApogeos = new ArrayList<>();
		
		// https://opale.imcce.fr/api/v1/phenomena/distances?date={{YYYY-MM-DD}}&nbd={{DDDD}}&bodies=399,301&calendar=gregorian		
		
		LocalDateTime fechaParaLlamada = LocalDateTime.parse("1000-01-01T00:00:00.000");

		LocalDateTime fechaTope = LocalDateTime.parse("2100-01-01T00:00:00.000");

		String urlConDias = url.replace("{{DDDD}}", "500");

		System.out.println("Haciendo llamada a API para apogeos");
		

		   try {
		    	
			   while (fechaParaLlamada.isBefore(fechaTope)) {

				   String fechaParaLlamadaFormateada = fechaParaLlamada.format(FORMATTER_API_REQUEST);				   
				  
				   String urlParaLlamada = urlConDias.replace("{{YYYY-MM-DD}}", fechaParaLlamadaFormateada);

				   System.out.println("Haciendo llamada para " + fechaParaLlamadaFormateada);
				   List<ApogeosDTO> apogeosFromAPI = this.getAPGDTO(urlParaLlamada);

				   LocalDateTime nuevaFechaParaLlamada = fechaParaLlamada;
		       
		        
				   for (ApogeosDTO apogeo : apogeosFromAPI) {
		        	
					   LocalDateTime apogeoDate = LocalDateTime.parse(apogeo.getDate(), FORMATTER_API_RESPONSE);
					   
					   if (apogeoDate.isAfter(nuevaFechaParaLlamada)) {
		                
						   nuevaFechaParaLlamada = apogeoDate;
					   }
				   }

				   allApogeos.addAll(apogeosFromAPI);

				   if (nuevaFechaParaLlamada.isAfter(fechaParaLlamada)) {
		        	
					   fechaParaLlamada = nuevaFechaParaLlamada.plusSeconds(1);
				   } 
				   else {
		        	
		            fechaParaLlamada = fechaParaLlamada.plusDays(50);
				   }
		 	
			   }	
			   
			   System.out.println("Fin de llamada a la API.");
		   } 
		   catch (Exception e) {
    	
			   System.out.println("Error al llamar a APG API: " + e);
		   }
		    
		   return allApogeos;
		
	}
	
	private List<ApogeosDTO> getAPGDTO(String url) {

	    AGPDTO responseOPALEAPI = restTemplate.getForObject(url, AGPDTO.class);

	    if (responseOPALEAPI != null &&
	        responseOPALEAPI.getResponse() != null &&
	        responseOPALEAPI.getResponse().getData() != null) {

	        return responseOPALEAPI.getResponse()
	                .getData()
	                .stream()
	                .map(item -> new ApogeosDTO(
	                        item.getDate(),
	                        item.getPhenomena(),
	                        item.getDistance()
	                ))
	                .toList();
	    }

	    return new ArrayList<>();
	}
}
