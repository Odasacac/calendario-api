package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
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
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public String poblateEclipses() {
		
		String resultado = "Eclipses actualizados sin problema.";
		
		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("LEPY", "SEPY"));	
		
		String apiEclipsesLunares = "";
		String apiEclipsesSolares = "";
		
		for (DatosEntity url : urls) 
		{
			switch (url.getConcepto()) {
			
				case "LEPY":					
					apiEclipsesLunares = url.getValor();
					break;
				
				case "SEPY":					
					apiEclipsesSolares = url.getValor();
					break;					
			}
		}
		
		if(apiEclipsesLunares != null && apiEclipsesSolares != null) {
			
			try {
				
				for (int i = 0; i <= 2100; i++) {
														
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
		}
		
		return resultado;

	}
	
	// PRIVATE METHODS
	
	private void actualizarEclipsesLunaresDelAnyo (String anyo, String url){
		
		System.out.println("Actualizando los eclipses solares del anyo: " + anyo);
		
		List<LunarEclipseDTO> eclipsesLunaresDelAnyo = this.getEclipsesLunaresDelAnyoViaAPI(anyo, url);
		
		if(!eclipsesLunaresDelAnyo.isEmpty()) {
			
			for(LunarEclipseDTO eclipse : eclipsesLunaresDelAnyo) {
				
				EclipsesEntity eclipseParaBD = new EclipsesEntity();
				eclipseParaBD.setDeLuna(true);
				eclipseParaBD.setDate(eclipse.getDate());
				eclipseParaBD.setYear(Integer.valueOf(anyo));
				
				switch(eclipse.getType()) {
				
					case "TotalEclipse":
						eclipseParaBD.setEsTotal(true);
						break;
						
					case "PartialEclipse":
						eclipseParaBD.setEsParcial(true);
						break;
						
					case "PenumbralEclipse":
						eclipseParaBD.setEsPenumbral(true);
						break;
				}
										
				this.eclipsesRepository.save(eclipseParaBD);
			}
		}
		
		
		System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
	}
	
	
	
	
	

	 private void actualizarEclipsesSolaresDelAnyo (String anyo, String url){
		
		System.out.println("Actualizando los eclipses solares del anyo: " + anyo);
		
		List<SolarEclipseDTO> eclipsesSolaresDelAnyo = this.getEclipsesSolaresDelAnyoViaAPI(anyo, url);
		
		for(SolarEclipseDTO eclipse : eclipsesSolaresDelAnyo) {
			
			EclipsesEntity eclipseParaBD = new EclipsesEntity();
			eclipseParaBD.setDeSol(true);
			eclipseParaBD.setDate(eclipse.getDate());
			eclipseParaBD.setYear(Integer.valueOf(anyo));
			
			switch(eclipse.getType()) {
			
				case "NonCentralPartialEclipse":
					eclipseParaBD.setEsParcial(true);
					break;
				
				case "CentralAnnularEclipse":
					eclipseParaBD.setEsAnular(true);
					break;
					
				case "CentralTotalEclipse":
					eclipseParaBD.setEsTotal(true);
					break;
			}

		
			this.eclipsesRepository.save(eclipseParaBD);
	
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

			        LunarEclipseDTO dto = new LunarEclipseDTO(eclipse.getEvents().getGreatest().getDate(), eclipse.getType());
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
	       
	    			SolarEclipseDTO dto = new SolarEclipseDTO(eclipse.getEvents().getGreatest().getDate(), eclipse.getType());

	    			eclipsesSolares.add(dto);
	    		}
	    	}
	    }

	    return eclipsesSolares;
	}


}
