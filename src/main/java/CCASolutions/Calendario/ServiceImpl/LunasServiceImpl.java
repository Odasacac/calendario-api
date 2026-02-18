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
	
	
	// METODOS PUBLICOS
	

	public LunasEntity getPrimeraLunaNuevaAnteriorAFecha(List<LunasEntity> lunasDesdeAnyoMinimoAAnyoMaximo, LocalDate fecha) {
		
		LunasEntity primeraLunaNuevaAnteriorAFecha = new LunasEntity();
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		for(LunasEntity luna :lunasDesdeAnyoMinimoAAnyoMaximo) {
				
			if(luna.isNueva() && luna.getDate().toLocalDate().isBefore(fecha)) {
					
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
		
		if(apiGetLunasUrl != null) {	

			int anyoDeLaUltimaLunaGuardada = this.lunasRepository.findTopByOrderByDateDesc().getYear();
			
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
								
								this.lunasRepository.save(lunaParaDB);
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
	
	// METODOS PRIVADOS
	
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
	

	
	private List<LunarPhaseDTO> getYLPDTO(String url){
		
		List<LunarPhaseDTO> fenomenos = new ArrayList<>();
		
		YLPDTO responseOPALEAPI = restTemplate.getForObject(url, YLPDTO.class);
		
		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			fenomenos = responseOPALEAPI.getResponse().getData();
		}
		
		return fenomenos;
	}





}
