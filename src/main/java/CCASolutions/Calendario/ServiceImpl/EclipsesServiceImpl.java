package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.EclipsesParaDBDTO;
import CCASolutions.Calendario.DTOs.LEPYDTO;
import CCASolutions.Calendario.DTOs.LunarEclipseDTO;
import CCASolutions.Calendario.DTOs.SEPYDTO;
import CCASolutions.Calendario.DTOs.SolarEclipseDTO;
import CCASolutions.Calendario.Entities.AllEclipsesEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.AllEclipsesRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Services.DatosService;
import CCASolutions.Calendario.Services.EclipsesService;


@Service
public class EclipsesServiceImpl implements EclipsesService{
	
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private DatosService datosService;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private AllEclipsesRepository allEclipsesRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final static String TOTAL = "TotalEclipse";
	private final static String PARTIAL = "PartialEclipse";
	private final static String PENUMBRAL = "PenumbralEclipse";
	private final static String NON_CENTRAL_PARTIAL = "NonCentralPartialEclipse";
	private final static String CENTRAL_ANULAR = "CentralAnnularEclipse";
	private final static String CENTRAL_TOTAL = "CentralTotalEclipse";
	
	
	
	
	public AbsoluteEclipsesDTO getVAUAbsoluteEclipses(DateDTO dateVAU, List<EclipsesEntity> eclipsesAbsolutosDesdeLastEclipenoIN, LocalDate date, MetonsEntity lastMetonIN) {
		
		AbsoluteEclipsesDTO absoluteEclipses = new AbsoluteEclipsesDTO ();		
		
		int eclipsesNoParcialesDesdeLastEclipenoIN = 0;
		int eclipsesSolaresNoParcialesDesdeLastEclipenoIN = 0;
		int eclipsesLunaresNoParcialesDesdeLastEclipenoIN = 0;
		
		int eclipsesNoParcialesDesdeLastMetonIN = 0;
		int eclipsesSolaresNoParcialesDesdeLastMetonIN = 0;		
		int eclipsesLunaresNoParcialesDesdeLastMetonIN = 0;
		
		
		
		if(!dateVAU.getEclipenoVAU().isEclipenoINDay()) {
		
			
			List<EclipsesEntity> eclipsesSolaresNoParcialesDesdeLastEclipenoINList = new ArrayList<>();		
			List<EclipsesEntity> eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList = new ArrayList<>();
			
			int lunaresDesdeElUltimoMetonoIN =0;
			int solaresDesdeElUltimoMetonoIN =0;
			
			//Si estamos en el primer métono, hay que restarle 1 porque viene el propio del eclípeno
			if(dateVAU.getMetonoVAU().getMetonsIN().getMetonosINSinceLastEclipenoIN() == 0) {
				solaresDesdeElUltimoMetonoIN=-1; 
			}
			
			
			for (EclipsesEntity eclipse : eclipsesAbsolutosDesdeLastEclipenoIN){
				
				if(eclipse.getDate().toLocalDate().isBefore(date)) {
					if(eclipse.isDeSol()) {
						
						eclipsesSolaresNoParcialesDesdeLastEclipenoINList.add(eclipse);
						
						if(eclipse.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate()) || eclipse.getDate().toLocalDate().isEqual(lastMetonIN.getDate().toLocalDate())) {
							
							solaresDesdeElUltimoMetonoIN = solaresDesdeElUltimoMetonoIN+1;					
						}
						
					}
					else if (eclipse.isDeLuna()){
						
						eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList.add(eclipse);
						
						if(eclipse.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate()) || eclipse.getDate().toLocalDate().isEqual(lastMetonIN.getDate().toLocalDate())) {
							
							lunaresDesdeElUltimoMetonoIN = lunaresDesdeElUltimoMetonoIN+1;				
						}			
					}			
				}			
			}			
			
			if(solaresDesdeElUltimoMetonoIN==-1) {
				solaresDesdeElUltimoMetonoIN=0;
			}
			eclipsesSolaresNoParcialesDesdeLastEclipenoIN = eclipsesSolaresNoParcialesDesdeLastEclipenoINList.size();
			eclipsesLunaresNoParcialesDesdeLastEclipenoIN = eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList.size();
			eclipsesNoParcialesDesdeLastEclipenoIN = eclipsesSolaresNoParcialesDesdeLastEclipenoIN + eclipsesLunaresNoParcialesDesdeLastEclipenoIN;
			
			
			eclipsesSolaresNoParcialesDesdeLastMetonIN = solaresDesdeElUltimoMetonoIN;		
			eclipsesLunaresNoParcialesDesdeLastMetonIN = lunaresDesdeElUltimoMetonoIN;
			eclipsesNoParcialesDesdeLastMetonIN = eclipsesSolaresNoParcialesDesdeLastMetonIN + eclipsesLunaresNoParcialesDesdeLastMetonIN;
			
		}
		
		absoluteEclipses.setSolarSinceLastEclipenoIN(eclipsesSolaresNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setSolarSinceLastMetonoIN(eclipsesSolaresNoParcialesDesdeLastMetonIN);
		
		absoluteEclipses.setLunarSinceLastEclipenoIN(eclipsesLunaresNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setLunarSinceLastMetonoIN(eclipsesLunaresNoParcialesDesdeLastMetonIN);
		
		absoluteEclipses.setSinceLastEclipenoIN(eclipsesNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setSinceLastMetonoIN(eclipsesNoParcialesDesdeLastMetonIN);
		
		return absoluteEclipses;
	}
	
	
	
	public String poblateEclipsesFromOpale() {
		
		String resultado = "Eclipses actualizados sin problema.";
		
		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList(this.datosService.getApiLunarEclipses(), this.datosService.getApiSolarEclipses()));	
		List<EclipsesEntity> eclipses = this.eclipsesRepository.findAll();
		List<AllEclipsesEntity> allEclipses = this.allEclipsesRepository.findAll();
		
		List<EclipsesEntity> eclipsesParaDB = new ArrayList<>();
		List<AllEclipsesEntity> allEclipsesParaDB = new ArrayList<>();
		
		String apiEclipsesLunares = null;
		String apiEclipsesSolares = null;
		
		if(eclipses.isEmpty() && allEclipses.isEmpty()) {
			for (DatosEntity url : urls) 
			{
				if (this.datosService.getApiLunarEclipses().equals(url.getConcepto())) {
					
				    apiEclipsesLunares = url.getValor();

				} else if (this.datosService.getApiSolarEclipses().equals(url.getConcepto())) {
					
				    apiEclipsesSolares = url.getValor();
				}
			}
			
			if(apiEclipsesLunares != null && apiEclipsesSolares != null) {
				
				try {
					
					for (int i = -4700; i <= 2100; i++) {
										
						EclipsesParaDBDTO eclipsesLunares = this.actualizarEclipsesLunaresDelAnyo(String.valueOf(i), apiEclipsesLunares);
						EclipsesParaDBDTO eclipsesSolares = this.actualizarEclipsesSolaresDelAnyo(String.valueOf(i), apiEclipsesSolares);	
						
						if(eclipsesLunares.getEclipse() != null) {
							
							eclipsesParaDB.add(eclipsesLunares.getEclipse());
						}
						
						if(eclipsesSolares.getEclipse() != null) {
							
							eclipsesParaDB.add(eclipsesSolares.getEclipse());
						}
						
						if(eclipsesLunares.getAllEclipse() != null) {
							
							allEclipsesParaDB.add(eclipsesLunares.getAllEclipse());
						}
						
						if(eclipsesSolares.getAllEclipse() != null) {
							
							allEclipsesParaDB.add(eclipsesSolares.getAllEclipse());									
						}
						
						
						
																
					}
					
					System.out.println("Almacenando eclipses...");
					
					if(!eclipsesParaDB.isEmpty()) {
						
						this.eclipsesRepository.saveAll(eclipsesParaDB);
					}
					
					if(!allEclipsesParaDB.isEmpty()) {
						
						this.allEclipsesRepository.saveAll(allEclipsesParaDB);
					}
					
					System.out.println("Eclipses almacenados.");
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
	
	private EclipsesParaDBDTO actualizarEclipsesLunaresDelAnyo (String anyo, String url){
		
		EclipsesParaDBDTO eclipsesLunares = new EclipsesParaDBDTO();
		
		System.out.println("Actualizando los eclipses lunares del anyo: " + anyo);
		
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
						

						boolean esFechaInvalida = false;

						for (String fechaInvalida : this.datosService.getFechasInvalidas()) {

						    if (eclipseParaBD.getDate().toLocalDate().toString().equals(fechaInvalida)) {
						    	
						        esFechaInvalida = true;
						        break;
						    }
						}

						if (!esFechaInvalida) {
							eclipsesLunares.setEclipse(eclipseParaBD);
						}		
						
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
					
					LocalDateTime fecha = LocalDateTime.parse(eclipse.getDate());

					allEclipseParaDB.setYear(fecha.getYear());
					allEclipseParaDB.setMonth(fecha.getMonthValue());
					allEclipseParaDB.setDay(fecha.getDayOfMonth());
					allEclipseParaDB.setHour(fecha.getHour());
					allEclipseParaDB.setMinute(fecha.getMinute());
					allEclipseParaDB.setSecond(fecha.getSecond());
					
					eclipsesLunares.setAllEclipse(allEclipseParaDB);
					
					
					System.out.println("Actualizados los eclipses lunares del anyo: " + anyo);	
				}
			}
		}
		catch (Exception e) {
			
			System.out.println("Error al actualizar los eclipses lunares del anyo " + anyo  +": "+ e);
			e.printStackTrace();
		}
		
	
		
		return eclipsesLunares;
	}
	
	
	
	
	

	 private EclipsesParaDBDTO actualizarEclipsesSolaresDelAnyo (String anyo, String url){
		
		EclipsesParaDBDTO eclipsesSolares = new EclipsesParaDBDTO();
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
				
					boolean esFechaInvalida = false;

					for (String fechaInvalida : this.datosService.getFechasInvalidas()) {

					    if (eclipseParaBD.getDate().toLocalDate().toString().equals(fechaInvalida)) {
					    	
					        esFechaInvalida = true;
					        break;
					    }
					}

					if (!esFechaInvalida) {
						eclipsesSolares.setEclipse(eclipseParaBD);
					}		
				}
				
				AllEclipsesEntity allEclipseParaDB = new AllEclipsesEntity();
				allEclipseParaDB.setDeSol(true);
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
				
				LocalDateTime fecha = LocalDateTime.parse(eclipse.getDate());

				allEclipseParaDB.setYear(fecha.getYear());
				allEclipseParaDB.setMonth(fecha.getMonthValue());
				allEclipseParaDB.setDay(fecha.getDayOfMonth());
				allEclipseParaDB.setHour(fecha.getHour());
				allEclipseParaDB.setMinute(fecha.getMinute());
				allEclipseParaDB.setSecond(fecha.getSecond());
				
				eclipsesSolares.setAllEclipse(allEclipseParaDB);			
				System.out.println("Actualizados los eclipses solares del anyo: " + anyo);	
			}
		}
		catch (Exception e) {
			System.out.println("Error al actualizar los eclipses solares del anyo " + anyo  +": "+ e);
		}
		
		return eclipsesSolares;
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
