package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.AllFasesLunaresEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Repositories.AllFasesLunaresRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Services.LunasService;

@Service
public class LunasServiceImpl implements LunasService {
	
	@Autowired
	private DatosRepository datosRepository;	
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private AllFasesLunaresRepository allFasesLunaresRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	
	// METODOS PUBLICOS
	

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


	public LunasEntity getPrimeraLunaNuevaAnteriorAFecha(List<LunasEntity> lunasNuevasDesdeAnyoMinimoAAnyoMaximo, LocalDate fecha) {
		
		LunasEntity primeraLunaNuevaAnteriorAFecha = new LunasEntity();
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		for(LunasEntity luna :lunasNuevasDesdeAnyoMinimoAAnyoMaximo) {
				
			if(luna.getDate().toLocalDate().isBefore(fecha)) {
					
				long diasDeDiferenciaEntreLastSOEYLuna = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), fecha);
					
				if(diasDeDiferenciaEntreLastSOEYLuna < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYLuna;
					primeraLunaNuevaAnteriorAFecha = luna;
				}

			}
		}
		return primeraLunaNuevaAnteriorAFecha;
	}

	public LunasEntity getPrimeraLunaNuevaPosteriorAFecha(List<LunasEntity> lunasDesdeAnyoMinimoAAnyoMaximo, LocalDate fecha) {
		
		LunasEntity primeraLunaNuevaAnteriorAFecha = new LunasEntity();
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		for(LunasEntity luna :lunasDesdeAnyoMinimoAAnyoMaximo) {
				
			if(luna.isNueva() && luna.getDate().toLocalDate().isAfter(fecha)) {
					
				long diasDeDiferenciaEntreLastSOEYLuna = ChronoUnit.DAYS.between(fecha, luna.getDate().toLocalDate());
					
				if(diasDeDiferenciaEntreLastSOEYLuna < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYLuna;
					primeraLunaNuevaAnteriorAFecha = luna;
				}

			}
		}
		return primeraLunaNuevaAnteriorAFecha;
	}

	
	
	public String poblateLunas() {
		
		String resultado = "Lunas actualizadas sin problema.";
		
		DatosEntity apiGetLunasUrl = datosRepository.findByConcepto("YLP");
		
		List<LunasEntity> allLunas = this.lunasRepository.findAll();
		
		if(apiGetLunasUrl != null && allLunas.isEmpty()) {	
			
			for (int i = -4700; i < 2100; i++) {
				
				System.out.println("Actualizando lunas del anyo: " + i);
				
				try {
					List<LunarPhaseDTO> fasesLunaresDelAnyo = this.getFasesLunaresDelAnyoViaAPI(String.valueOf(i), apiGetLunasUrl.getValor());
					
					if(!fasesLunaresDelAnyo.isEmpty()) {
						
						for(LunarPhaseDTO faseLunarAPI : fasesLunaresDelAnyo) {
							
							if(i > 0) {
								
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
								
								lunaParaDB.setYear(LocalDateTime.parse(faseLunarAPI.getDate()).getYear());
								lunaParaDB.setDate(LocalDateTime.parse(faseLunarAPI.getDate()));									
								lunaParaDB.setSelecta(false);
								lunaParaDB.setInvertida(false);
								
								this.lunasRepository.save(lunaParaDB);;
							}
		
							AllFasesLunaresEntity allFaseLunarParaDB = new AllFasesLunaresEntity();
							
							switch (faseLunarAPI.getMoonPhase()){
							
								case "NewMoon":
									allFaseLunarParaDB.setNueva(true);
									break;
								
								case "FirstQuarter":
									allFaseLunarParaDB.setCuartoCreciente(true);
									break;
								
								case "FullMoon":
									allFaseLunarParaDB.setLlena(true);
									break;
								
								case "LastQuarter":
									allFaseLunarParaDB.setCuartoMenguante(true);
									break;
							}
							

							String[] parts = String.valueOf(faseLunarAPI.getDate()).split("T");
							String[] dateParts = parts[0].split("-");
							String[] timeParts = parts[1].split(":");

							if(String.valueOf(faseLunarAPI.getDate()).startsWith("-")) {
								allFaseLunarParaDB.setYear(Integer.parseInt("-" + dateParts[1]));
								allFaseLunarParaDB.setMonth(Integer.parseInt(dateParts[2]));
								allFaseLunarParaDB.setDay(Integer.parseInt(dateParts[3]));
							}
							else {
								allFaseLunarParaDB.setYear(Integer.parseInt(dateParts[0]));
								allFaseLunarParaDB.setMonth(Integer.parseInt(dateParts[1]));
								allFaseLunarParaDB.setDay(Integer.parseInt(dateParts[2]));
							}
							

							allFaseLunarParaDB.setHour(Integer.parseInt(timeParts[0]));
							allFaseLunarParaDB.setMinute(Integer.parseInt(timeParts[1]));
							allFaseLunarParaDB.setSecond(Integer.parseInt(timeParts[2]));
							
							this.allFasesLunaresRepository.save(allFaseLunarParaDB);
						}					
						
						
						System.out.println("Actualizadas las lunas del anyo: " + i);
					}					
					else {
					
						System.out.println("No se han obtenido lunas de la API.");
						resultado = "Error al actualizar lunas, checkear logs.";
					}					
				}
				catch(Exception e) {
					System.out.println("Error al actualizar lunas del anyo " + i  +": "+ e);
					resultado = "Error al actualizar lunas, checkear logs.";
				}
				
			}
		}	
		
		else {
			
			if(apiGetLunasUrl == null) {
				
				System.out.println("La URL de la API para obtener las lunas es nula.");
			}
			else if(!allLunas.isEmpty()) {
				
				System.out.println("Ya hay lunas en la base de datos.");
			}
			
			resultado = "Error al actualizar lunas, checkear logs.";
		}
		
		return resultado;
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
	

	
	private List<LunarPhaseDTO> getYLPDTO(String url){
		
		List<LunarPhaseDTO> fenomenos = new ArrayList<>();
		
		YLPDTO responseOPALEAPI = restTemplate.getForObject(url, YLPDTO.class);
		
		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			fenomenos = responseOPALEAPI.getResponse().getData();
		}
		
		return fenomenos;
	}





}
