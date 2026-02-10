package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
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
	
	
	public LunasEntity getNewMoonBeforeADate(LocalDateTime date) {
		
		return this.lunasRepository.findFirstByDateBeforeAndNuevaTrueOrderByDateDesc(date);
	}
	
	
	public LunasEntity getNewMoonFromSOEAndMonthOfSeason(SolsticiosYEquinocciosEntity lastSOE, int monthOfSeason) {
		
		LunasEntity newMoon = new LunasEntity();
		
		List<LunasEntity> lunasNuevasPasadas = this.lunasRepository.findTop4ByDateAfterAndNuevaIsTrueOrderByDateAsc(lastSOE.getDate());
		
		if(!lunasNuevasPasadas.isEmpty()) {
			newMoon = lunasNuevasPasadas.get(monthOfSeason-1);
		}
		
		return newMoon;
	}
	
	public LunasEntity getLastNewMoonForADateO(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		LunasEntity lastNewMoon = new LunasEntity();
		Long diasLastLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		for(int i = 0; i<fasesLunaresDelAnyo.size(); i++) {
			
			LunasEntity faseLunar = fasesLunaresDelAnyo.get(i);

			if(faseLunar.isNueva() && 
				(faseLunar.getDate().toLocalDate().isBefore(dateO.toLocalDate()) || faseLunar.getDate().toLocalDate().isEqual(dateO.toLocalDate()))) {
				
				diasLastLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(faseLunar.getDate(), dateO);
				
				if(diasLastLPDeDiferenciaConLaFechaO < diasLastLPConMenorDiferenciaConLaFechaO) {
					
					lastNewMoon.setDate(faseLunar.getDate());
					lastNewMoon.setNueva(faseLunar.isNueva());;
					diasLastLPConMenorDiferenciaConLaFechaO = diasLastLPDeDiferenciaConLaFechaO;
				}	
			}	
		}
		
		if(lastNewMoon.getDate() == null) {
	
			List<LunasEntity> fasesLunaresDelAnyoAnterior = this.getFasesLunaresDelAnyo(String.valueOf(dateO.minusYears(1).getYear()));
			lastNewMoon = this.getLastNewMoonForADateO(dateO, fasesLunaresDelAnyoAnterior);
					
		}
		
		return lastNewMoon;
	}
	
	
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
	
	public List<LunasEntity> getFasesLunaresDelAnyo(String anyo){

		return this.lunasRepository.findByYearOrderByDateAsc(Integer.valueOf(anyo));
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
	
	public boolean esDateOLunaNueva(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		boolean esLunaNueva = false;
		
		for(int i = 0; i < fasesLunaresDelAnyo.size() && !esLunaNueva; i++) {			
				
			LunasEntity luna = fasesLunaresDelAnyo.get(i);
				
			if(luna.isNueva() && dateO.toLocalDate().isEqual(luna.getDate().toLocalDate())) {
				
				esLunaNueva = true;
			}
			
		}

		
		return esLunaNueva;
	}
	
	public int getLunasNuevasPasadasDesdeLastSOEHastaDateO (List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, SolsticiosYEquinocciosEntity lastSOE, LocalDateTime dateO){
		
		int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
		
		for(LunasEntity lunaNueva : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {				
			
			if(dateO.toLocalDate().isAfter(lunaNueva.getDate().toLocalDate())) {
					
				lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO +1;
			}

		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaDateO;
	}
	
	public List<LunasEntity> getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(List<LunasEntity> fasesLunaresDelAnyo, SolsticiosYEquinocciosEntity lastSOE, SolsticiosYEquinocciosEntity nextSOE) {
		
		List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = new ArrayList<>();
		
		for(LunasEntity faseLunar :fasesLunaresDelAnyo) {
			
			if(faseLunar.getDate().isAfter(lastSOE.getDate()) && faseLunar.getDate().isBefore(nextSOE.getDate()) && faseLunar.isNueva()) {
				
				lunasNuevasPasadasDesdeLastSOEHastaNextSOE.add(faseLunar);
			}
			
		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaNextSOE;
		
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
