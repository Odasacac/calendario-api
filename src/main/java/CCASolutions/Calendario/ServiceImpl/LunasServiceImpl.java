package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Services.LunasService;

@Service
public class LunasServiceImpl implements LunasService {
	
	@Autowired
	private DatosRepository datosRepository;	
	
	@Autowired
	private LunasRepository lunasRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public String poblateLunas() {
		
		String resultado = "Lunas actualizadas sin problema.";
		
		DatosEntity apiGetLunasUrl = datosRepository.findByConcepto("YLP");
		
		if(apiGetLunasUrl != null) {	

			int anyoDeLaUltimaLunaGuardada = this.getAnyoDeLaUltimaLunaGuardada();
			
			int anyoParaLaApi = anyoDeLaUltimaLunaGuardada+1;		
			
			for (int i = anyoParaLaApi; i <= 2100; i++) {
				
				System.out.println("Actualizando lunas del anyo: " + i);
				
				List<LunarPhaseDTO> fasesLunaresDelAnyo = this.getFasesLunaresDelAnyoViaAPI(String.valueOf(i), apiGetLunasUrl.getValor());
				
				if(!fasesLunaresDelAnyo.isEmpty()) {
					
					for(LunarPhaseDTO faseLunarAPI : fasesLunaresDelAnyo) {
									
							LunasEntity lunaParaDB = new LunasEntity();
							
							switch (faseLunarAPI.getMoonPhase()){
							
								case "NewMoon":
									lunaParaDB.setNueva(true);
									break;
									
								case "FirstQuarter":
									lunaParaDB.setCuartoCreciente(true);
									break;
									
								case "FullMoon":
									lunaParaDB.setLlena(true);
									break;
									
								case "LastQuarter":
									lunaParaDB.setCuartoMenguante(true);
									break;
							}
							
							lunaParaDB.setYear(faseLunarAPI.getDate().getYear());
							lunaParaDB.setDate(faseLunarAPI.getDate());					

							try {
								
								this.saveOne(lunaParaDB);
							}
							catch (Exception e)	{
									
								System.out.println("Error al almacenar luna: " + e);
								resultado = "Error al actualizar lunas, checkear logs.";
							}		
					}
					
					System.out.println("Actualizadas las lunas del anyo: " + i);
				}					
				else {
				
					System.out.println("No se han obtenido lunas de la API.");
					resultado = "Error al actualizar lunas, checkear logs.";
				}	
				
			}
		}	
		
		else {
			
			System.out.println("La URL de la API para obtener las lunas es nula.");
			resultado = "Error al actualizar lunas, checkear logs.";
		}
		
		return resultado;
	}
	
	public void saveOne(LunasEntity lunaParaDB) {
		
		this.lunasRepository.save(lunaParaDB);
	}

	
	public int getAnyoDeLaUltimaLunaGuardada() {
	
		int anyoDeLaUltimaLunaGuardada = 0;
		
		LunasEntity ultimaLunaAlmacenada = this.lunasRepository.findTopByOrderByDateDesc();
		
		if(ultimaLunaAlmacenada != null) {
			
			anyoDeLaUltimaLunaGuardada = ultimaLunaAlmacenada.getYear();
		}
		
		return anyoDeLaUltimaLunaGuardada;
	}
	
	public List<LunarPhaseDTO> getFasesLunaresDelAnyoViaDB(String anyo){
		
		List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();
		
		return fasesLunaresDelAnyo;
	}
	
	
	public List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url){
		
		List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();
		
		// https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}
		
		String urlParaLlamada = url.replace("{{YYYY}}", anyo);

		try {
			
			fasesLunaresDelAnyo = this.getYLPDTO(urlParaLlamada);
		}
		catch (Exception e) {
			
			System.out.println("Error al llamar a YLP API: " + e);
		}
				
		return fasesLunaresDelAnyo;
	}
	
	public boolean esDateOLunaNueva(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo) {
		
		boolean esLunaNueva = false;
		
		for(int i = 0; i < fasesLunaresDelAnyo.size() && !esLunaNueva; i++) {			
				
			LunarPhaseDTO fenomeno = fasesLunaresDelAnyo.get(i);
				
			if("NewMoon".equals(fenomeno.getMoonPhase()) && dateO.toLocalDate().isEqual(fenomeno.getDate().toLocalDate())) {
				
				esLunaNueva = true;
			}
			
		}

		
		return esLunaNueva;
	}
	
	public int lunasNuevasPasadasDesdeLastSOEHastaDateO (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, LocalDateTime dateO){
		
		int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
		
		for(LunarPhaseDTO lunaNueva : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {				
			
			if(dateO.toLocalDate().isAfter(lunaNueva.getDate().toLocalDate())) {
					
				lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO +1;
			}

		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaDateO;
	}
	
	public List<LunarPhaseDTO> getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(List<LunarPhaseDTO> fasesLunaresDelAnyo, FenomenoDTO lastSOE, FenomenoDTO nextSOE) {
		
		List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = new ArrayList<>();
		
		for(LunarPhaseDTO faseLunar :fasesLunaresDelAnyo) {
			
			if(faseLunar.getDate().isAfter(lastSOE.getDate()) && faseLunar.getDate().isBefore(nextSOE.getDate()) && "NewMoon".equals(faseLunar.getMoonPhase())) {
				
				lunasNuevasPasadasDesdeLastSOEHastaNextSOE.add(faseLunar);
			}
			
		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaNextSOE;
		
	}
	
	public List<LunasEntity> getLunasEntityFromLunarPhaseDTO(List<LunarPhaseDTO> lunarPhases){
		
		List<LunasEntity> lunasEntity = new ArrayList <>();
		
		return lunasEntity;
	}
	
	
	// PRIVATE METHODS
	
	private List<LunarPhaseDTO> getYLPDTO(String url){
		
		List<LunarPhaseDTO> fenomenos = new ArrayList<>();
		
		YLPDTO responseOPALEAPI = restTemplate.getForObject(url, YLPDTO.class);
		
		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			fenomenos = responseOPALEAPI.getResponse().getData();
		}
		
		return fenomenos;
	}



}
