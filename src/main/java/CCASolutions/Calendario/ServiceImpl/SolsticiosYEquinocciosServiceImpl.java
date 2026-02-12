package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.GASYEFDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
public class SolsticiosYEquinocciosServiceImpl implements SolsticiosYEquinocciosService{

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	
	@Autowired
	private DatosRepository datosRepository;
	
	
	private final RestTemplate restTemplate = new RestTemplate();
	

	public String poblateSolsticiosYEquinoccios() {
		
		String resultado = "Solsticios y equinoccios actualizados sin problema";
		
		DatosEntity apiGetSYEUrl = datosRepository.findByConcepto("ASYEF");
		
		if(apiGetSYEUrl != null) {	
			
			int anyoDelUltimoSOEGuardado = this.solsticiosYEquinocciosRepository.findTopByOrderByDateDesc().getYear();
			
			int anyoParaLaApi = anyoDelUltimoSOEGuardado+1;
			
			
			
			for (int i = anyoParaLaApi; i <= 2100; i++) {
				
				System.out.println("Actualizando los solsticios y equinoccios del anyo: " + i);
				
				List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = this.getSolsticiosYEquinocciosDelAnyoViaAPI(String.valueOf(i), apiGetSYEUrl.getValor());
				
				if(!solsticiosYEquinocciosDelAnyo.isEmpty()) {
					
					for(FenomenoDTO soeAPI : solsticiosYEquinocciosDelAnyo) {
						
						SolsticiosYEquinocciosEntity soeParaDB = new SolsticiosYEquinocciosEntity();
						
						switch (soeAPI.getPhenomena()) {
						
							case "WinterSolstice":
								soeParaDB.setSolsticioInvierno(true);
								soeParaDB.setStartingSeason(1);
								break;
								
							case "VernalEquinox":
								soeParaDB.setEquinoccioPrimavera(true);
								soeParaDB.setStartingSeason(2);
								break;
								
							case "SummerSolstice":
								soeParaDB.setSolsticioVerano(true);
								soeParaDB.setStartingSeason(3);
								break;
								
							case "AutumnalEquinox":
								soeParaDB.setEquinoccioOtonyo(true);
								soeParaDB.setStartingSeason(4);
								break;
						}
						
						soeParaDB.setYear(soeAPI.getDate().getYear());
						soeParaDB.setDate(soeAPI.getDate());
						
						try {
							
							this.solsticiosYEquinocciosRepository.save(soeParaDB);
							
						}
						catch (Exception e)	{
								
							System.out.println("Error al almacenar solsticio o equinoccio: " + e);
							resultado = "Error al actualizar solsticios y equinoccios, checkear logs.";
						}
						
					}
					
					System.out.println("Actualizados los solsticios y equinoccios del anyo: " + i);
												
				}
				else {
					
					System.out.println("No se han obtenido solsticios ni equinoccios de la API.");
					resultado = "Error al actualizar solsticios y equinoccios, checkear logs.";
				}	
				
			}				
		}
		else {
			
			System.out.println("La URL de la API para obtener los solsticios y equinoccios es nula.");
		}
		
		return resultado;
	}
	
	


	
	public List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url) {	
			
		List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = new ArrayList<>();			
			
		// https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}
		String urlParaLlamada = url.replace("{{YYYY}}", anyo).replace("{{NNNN}}", "1");
				
		try {
				
			solsticiosYEquinocciosDelAnyo = this.getGASYEFDTO(urlParaLlamada);
		}
		catch (Exception e) {
				
			System.out.println("Error al llamar a GASYEF API: " + e);
		}				
					
		return solsticiosYEquinocciosDelAnyo;
			
	}
	
	
	
	// PRIVATE METHODS
	
	private List<FenomenoDTO> getGASYEFDTO(String url){
		
		List<FenomenoDTO> fenomenos = new ArrayList<>();
		
		GASYEFDTO responseOPALEAPI = restTemplate.getForObject(url, GASYEFDTO.class);
		
		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			fenomenos = responseOPALEAPI.getResponse().getData();
		}
		
		return fenomenos;
	}


}
