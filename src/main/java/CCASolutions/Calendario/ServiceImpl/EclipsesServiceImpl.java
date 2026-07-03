package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.Entities.AllEclipsesEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Repositories.AllEclipsesRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.DTOs.SEPYDTO;
import CCASolutions.Calendario.DTOs.LEPYDTO;
import CCASolutions.Calendario.DTOs.LunarEclipseDTO;
import CCASolutions.Calendario.DTOs.SolarEclipseDTO;

@Service
public class EclipsesServiceImpl implements EclipsesService{
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private AllEclipsesRepository allEclipsesRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final static String API_LUNAR_ECLIPSES = "LEPY";
	private final static String API_SOLAR_ECLIPSES = "SEPY";
	
	private final static String TOTAL = "TotalEclipse";
	private final static String PARTIAL = "PartialEclipse";
	private final static String PENUMBRAL = "PenumbralEclipse";
	private final static String NON_CENTRAL_PARTIAL = "NonCentralPartialEclipse";
	private final static String CENTRAL_ANULAR = "CentralAnnularEclipse";
	private final static String CENTRAL_TOTAL = "CentralTotalEclipse";
	
	public String poblateEclipses() {
		
		String resultado = "Eclipses actualizados sin problema.";
		
		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList(API_LUNAR_ECLIPSES, API_SOLAR_ECLIPSES));	
		List<EclipsesEntity> allEclipses = this.eclipsesRepository.findAll();
		
		String apiEclipsesLunares = "";
		String apiEclipsesSolares = "";
		
		if(allEclipses.isEmpty()) {
			for (DatosEntity url : urls) 
			{
				switch (url.getConcepto()) {
				
					case API_LUNAR_ECLIPSES:					
						apiEclipsesLunares = url.getValor();
						break;
					
					case API_SOLAR_ECLIPSES:					
						apiEclipsesSolares = url.getValor();
						break;					
				}
			}
			
			if(apiEclipsesLunares != null && apiEclipsesSolares != null) {
				
				try {
					
					for (int i = -4700; i <= 2100; i++) {
															
						this.actualizarEclipsesLunaresDelAnyo(String.valueOf(i), apiEclipsesLunares);
						
						this.actualizarEclipsesSolaresDelAnyo(String.valueOf(i), apiEclipsesSolares);				
					
					}
				}
				catch (Exception e)
				{
					System.out.println("Error al evaluar los eclipses: " + e);
					resultado = "Error al evaluar los eclipses, revisar logs";
				}
				
			}
			else {
				System.out.println("La URL de la API para obtener los eclipses es nula.");
				resultado = "Error al evaluar los eclipses: la URL de la API para obtener los eclipses es nula.";
			}
		}
		else {
			System.out.println("Ya hay eclipses en la base de datos.");
			resultado = "Error al actualizar los eclipses: ya hay eclipses en la base de datos.";
		}
			
		
		
		return resultado;

	}
	
	// PRIVATE METHODS
	
	private void actualizarEclipsesLunaresDelAnyo (String anyo, String url){
		
		System.out.println("Actualizando los eclipses solares del anyo: " + anyo);
		
		try {
			
			List<LunarEclipseDTO> eclipsesLunaresDelAnyo = this.getEclipsesLunaresDelAnyoViaAPI(anyo, url);
			
			if(!eclipsesLunaresDelAnyo.isEmpty()) {
				
				for(LunarEclipseDTO eclipse : eclipsesLunaresDelAnyo) {
					
					if(Integer.valueOf(anyo) > 0) {
						
						EclipsesEntity eclipseParaBD = new EclipsesEntity();
						eclipseParaBD.setDeLuna(true);
						eclipseParaBD.setDate(LocalDateTime.parse(eclipse.getDate()));
						eclipseParaBD.setYear(Integer.valueOf(anyo));
						
						switch(eclipse.getType()) {
						
							case TOTAL:
								eclipseParaBD.setEsTotal(true);
								break;
								
							case PARTIAL:
								eclipseParaBD.setEsParcial(true);
								break;
								
							case PENUMBRAL:
								eclipseParaBD.setEsPenumbral(true);
								break;
						}
						
						this.eclipsesRepository.save(eclipseParaBD);
					}
					
					
					AllEclipsesEntity allEclipseParaDB = new AllEclipsesEntity();
					allEclipseParaDB.setDeLuna(true);
					switch(eclipse.getType()) {
					
						case TOTAL:
							allEclipseParaDB.setTotal(true);
							break;
						
						case PARTIAL:
							allEclipseParaDB.setParcial(true);
							break;
						
						case PENUMBRAL:
							allEclipseParaDB.setPenumbral(true);
							break;
					}
					
					String[] parts = String.valueOf(eclipse.getDate()).split("T");
					String[] dateParts = parts[0].split("-");
					String[] timeParts = parts[1].split(":");

					if(String.valueOf(eclipse.getDate()).startsWith("-")) {
						allEclipseParaDB.setYear(Integer.parseInt("-" + dateParts[1]));
						allEclipseParaDB.setMonth(Integer.parseInt(dateParts[2]));
						allEclipseParaDB.setDay(Integer.parseInt(dateParts[3]));
					}
					else {
						allEclipseParaDB.setYear(Integer.parseInt(dateParts[0]));
						allEclipseParaDB.setMonth(Integer.parseInt(dateParts[1]));
						allEclipseParaDB.setDay(Integer.parseInt(dateParts[2]));
					}
					

					allEclipseParaDB.setHour(Integer.parseInt(timeParts[0]));
					allEclipseParaDB.setMinute(Integer.parseInt(timeParts[1]));
					allEclipseParaDB.setSecond(Integer.parseInt(timeParts[2]));
					
					this.allEclipsesRepository.save(allEclipseParaDB);
					
				}
			}
		}
		catch (Exception e) {
			
			System.out.println("Error al actualizar los eclipses lunares del anyo " + anyo  +": "+ e);
		}
		
		
		
		System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
	}
	
	
	
	
	

	 private void actualizarEclipsesSolaresDelAnyo (String anyo, String url){
		
		System.out.println("Actualizando los eclipses solares del anyo: " + anyo);
		
		try {
			List<SolarEclipseDTO> eclipsesSolaresDelAnyo = this.getEclipsesSolaresDelAnyoViaAPI(anyo, url);
			
			for(SolarEclipseDTO eclipse : eclipsesSolaresDelAnyo) {
				
				if(Integer.valueOf(anyo) > 0) {
					EclipsesEntity eclipseParaBD = new EclipsesEntity();
					eclipseParaBD.setDeSol(true);
					eclipseParaBD.setDate(LocalDateTime.parse(eclipse.getDate()));
					eclipseParaBD.setYear(Integer.valueOf(anyo));
					
					switch(eclipse.getType()) {
					
						case NON_CENTRAL_PARTIAL:
							eclipseParaBD.setEsParcial(true);
							break;
						
						case CENTRAL_ANULAR:
							eclipseParaBD.setEsAnular(true);
							break;
							
						case CENTRAL_TOTAL:
							eclipseParaBD.setEsTotal(true);
							break;
					}
				
					this.eclipsesRepository.save(eclipseParaBD);
				}
				
				AllEclipsesEntity allEclipseParaDB = new AllEclipsesEntity();
				allEclipseParaDB.setDeLuna(true);
				switch(eclipse.getType()) {
				
					case NON_CENTRAL_PARTIAL:
						allEclipseParaDB.setParcial(true);
						break;
				
					case CENTRAL_ANULAR:
						allEclipseParaDB.setAnular(true);
						break;
					
					case CENTRAL_TOTAL:
						allEclipseParaDB.setTotal(true);
						break;
					}
				
				String[] parts = String.valueOf(eclipse.getDate()).split("T");
				String[] dateParts = parts[0].split("-");
				String[] timeParts = parts[1].split(":");

				if(String.valueOf(eclipse.getDate()).startsWith("-")) {
					allEclipseParaDB.setYear(Integer.parseInt("-" + dateParts[1]));
					allEclipseParaDB.setMonth(Integer.parseInt(dateParts[2]));
					allEclipseParaDB.setDay(Integer.parseInt(dateParts[3]));
				}
				else {
					allEclipseParaDB.setYear(Integer.parseInt(dateParts[0]));
					allEclipseParaDB.setMonth(Integer.parseInt(dateParts[1]));
					allEclipseParaDB.setDay(Integer.parseInt(dateParts[2]));
				}
				

				allEclipseParaDB.setHour(Integer.parseInt(timeParts[0]));
				allEclipseParaDB.setMinute(Integer.parseInt(timeParts[1]));
				allEclipseParaDB.setSecond(Integer.parseInt(timeParts[2]));
				
				this.allEclipsesRepository.save(allEclipseParaDB);
				
		
			}
		}
		catch (Exception e) {
			System.out.println("Error al actualizar los eclipses solares del anyo " + anyo  +": "+ e);
		}
		
		
		System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
	}
	
	private List<SolarEclipseDTO> getEclipsesSolaresDelAnyoViaAPI(String anyo, String url) {

		List<SolarEclipseDTO> eclipsesSolaresDelAnyo = new ArrayList<>();

		// https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}
		String urlParaLlamadaAPISolar = url.replace("{{YYYY}}", anyo);
	
		try {

			eclipsesSolaresDelAnyo = this.getSEPYDTO(urlParaLlamadaAPISolar);
	
		} catch (Exception e) {
	
			System.out.println("Error al llamar a SEPY API: " + e);
		}
	
		    return eclipsesSolaresDelAnyo;
	}
	
	private List<LunarEclipseDTO> getEclipsesLunaresDelAnyoViaAPI(String anyo, String url) {

		List<LunarEclipseDTO> eclipsesLunaresDelAnyo = new ArrayList<>();

		// https://opale.imcce.fr/api/v1/phenomena/eclipses/301/{{YYYY}}
		String urlParaLlamadaAPILunar = url.replace("{{YYYY}}", anyo);		
	
		try {
			
			eclipsesLunaresDelAnyo = this.getLEPYDTO(urlParaLlamadaAPILunar);
		} 
		catch (Exception e) {
	
			System.out.println("Error al llamar a LEPY API: " + e);
		}
	
		    return eclipsesLunaresDelAnyo;
	}
	
	private List<LunarEclipseDTO> getLEPYDTO(String url) {
		
		List<LunarEclipseDTO> eclipsesLunares = new ArrayList<>();
		
		 LEPYDTO apiResponse = restTemplate.getForObject(url, LEPYDTO.class);

		 if(apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getLunareclipse() != null) {
			 
			 for (LEPYDTO.LunarEclipse eclipse : apiResponse.getResponse().getLunareclipse()) {

				 if (eclipse.getEvents() != null && eclipse.getEvents().getGreatest() != null && eclipse.getEvents().getGreatest().getDate() != null) {

			        LunarEclipseDTO dto = new LunarEclipseDTO(String.valueOf(eclipse.getEvents().getGreatest().getDate()), eclipse.getType());
			        eclipsesLunares.add(dto);
			    }
			 }
		 }	
		 
		return eclipsesLunares;
	}
	
	
	
	private List<SolarEclipseDTO> getSEPYDTO(String url) {

		List<SolarEclipseDTO> eclipsesSolares = new ArrayList<>();

		SEPYDTO apiResponse = restTemplate.getForObject(url, SEPYDTO.class);

	    if (apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getData() != null) {
	 
	    	for (SEPYDTO.SolarEclipse eclipse : apiResponse.getResponse().getData()) {

	    		if (eclipse.getEvents() != null && eclipse.getEvents().getGreatest() != null && eclipse.getEvents().getGreatest().getDate() != null) {
	       
	    			SolarEclipseDTO dto = new SolarEclipseDTO(String.valueOf(eclipse.getEvents().getGreatest().getDate()), eclipse.getType());

	    			eclipsesSolares.add(dto);
	    		}
	    	}
	    }

	    return eclipsesSolares;
	}


}
