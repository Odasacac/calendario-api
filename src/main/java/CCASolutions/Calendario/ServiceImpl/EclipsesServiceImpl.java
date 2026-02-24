package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.EclipseDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.DTOs.SEPYDTO;
import CCASolutions.Calendario.DTOs.LEPYDTO;

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
		
		List<EclipseDTO> eclipsesLunaresDelAnyo = this.getEclipsesLunaresDelAnyoViaAPI(anyo, url);
		
		for(EclipseDTO eclipse : eclipsesLunaresDelAnyo) {
			
			EclipsesEntity eclipseParaBD = new EclipsesEntity();						
									
			this.eclipsesRepository.save(eclipseParaBD);
		}
		
		System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
	}
	
	private void actualizarEclipsesSolaresDelAnyo (String anyo, String url){
		
		System.out.println("Actualizando los eclipses solares del anyo: " + anyo);
		
		List<EclipseDTO> eclipsesSolaresDelAnyo = this.getEclipsesSolaresDelAnyoViaAPI(anyo, url);
		
		for(EclipseDTO eclipse : eclipsesSolaresDelAnyo) {
			
			EclipsesEntity eclipseParaBD = new EclipsesEntity();						
									
			this.eclipsesRepository.save(eclipseParaBD);
		}
		
		System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
	}
	
	private List<EclipseDTO> getEclipsesLunaresDelAnyoViaAPI(String anyo, String url) {

		List<EclipseDTO> eclipsesLunaresDelAnyo = new ArrayList<>();

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
	
	private List<EclipseDTO> getEclipsesSolaresDelAnyoViaAPI(String anyo, String url) {

		List<EclipseDTO> eclipsesSolaresDelAnyo = new ArrayList<>();

		// https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}
		String urlParaLlamadaAPISolar = url.replace("{{YYYY}}", anyo);
	
		try {

			eclipsesSolaresDelAnyo = this.getSEPYDTO(urlParaLlamadaAPISolar);
	
		} catch (Exception e) {
	
			System.out.println("Error al llamar a SEPY API: " + e);
		}
	
		    return eclipsesSolaresDelAnyo;
	}
	
	
	private List<EclipseDTO> getSEPYDTO(String url) {
		
		List<EclipseDTO> eclipsesSolares = new ArrayList<>();
		
		SEPYDTO responseFromOPALEAPI = restTemplate.getForObject(url, SEPYDTO.class);
		

		
		return eclipsesSolares;
	}

	private List<EclipseDTO> getLEPYDTO(String url) {
		
		List<EclipseDTO> eclipsesLunares = new ArrayList<>();
		
		 LEPYDTO apiResponse = restTemplate.getForObject(url, LEPYDTO.class);

		
		return eclipsesLunares;
	}
}
