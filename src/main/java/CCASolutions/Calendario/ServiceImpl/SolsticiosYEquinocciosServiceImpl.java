package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.GASYEFDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.AllSoEsEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.AllSoEsRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
public class SolsticiosYEquinocciosServiceImpl implements SolsticiosYEquinocciosService{

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private AllSoEsRepository allSoEsRepository;
	
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final static String API_SOES = "ASYEF";
	
	private final static String SI = "WinterSolstice";
	private final static String EP = "VernalEquinox";
	private final static String SV = "SummerSolstice";
	private final static String EO = "AutumnalEquinox";
	
	

	public YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN) {
		
		YearDTO vauYear = new YearDTO();
		
		boolean caeEnSolsticioDeInvierno=false;
		
		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		
		int year = 0;
		
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSolsticioDeInvierno; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.isSolsticioInvierno()) {				
		
				if(soe.getDate().toLocalDate().isEqual(date)) {
						
					caeEnSolsticioDeInvierno=true;
				}
				else if (soe.getDate().toLocalDate().isBefore(date) && soe.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate())){
					
					year=year+1;
				}
			}
			
		}
		
		
		vauYear.setEsSolsticioDeInvierno(caeEnSolsticioDeInvierno);
		if(caeEnSolsticioDeInvierno){
			vauYear.setSolsticiosDeInviernoSinceLastMetonIN("-");	
		}
		else {
			vauYear.setSolsticiosDeInviernoSinceLastMetonIN(String.valueOf(year));	
		}

		
		int numberOfYear = year +1;
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(date) || lastMetonIN.getDate().toLocalDate().isEqual(date)) {
			
			numberOfYear = numberOfYear-1;
		}
		vauYear.setNumberOfYear(numberOfYear);
	
		return vauYear;
		
	}
	
	

	public String poblateSolsticiosYEquinocciosFromOpale() {
		
		String resultado = "Solsticios y equinoccios actualizados sin problema";
		
		DatosEntity apiGetSYEUrl = datosRepository.findByConcepto(API_SOES);
		
		List<SolsticiosYEquinocciosEntity> allSoes = this.solsticiosYEquinocciosRepository.findAll();
		
		if(apiGetSYEUrl != null && allSoes.isEmpty()) {	
			
			for (int i = -4700; i < 2100; i++) {
				
				System.out.println("Actualizando los solsticios y equinoccios del anyo: " + i);
				
				try {
					List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = this.getSolsticiosYEquinocciosDelAnyoViaAPI(String.valueOf(i), apiGetSYEUrl.getValor());
					
					if(!solsticiosYEquinocciosDelAnyo.isEmpty()) {
						
						for(FenomenoDTO soeAPI : solsticiosYEquinocciosDelAnyo) {
							
							if(i > 0) {
								
								SolsticiosYEquinocciosEntity soeParaDB = new SolsticiosYEquinocciosEntity();
							
								switch (soeAPI.getPhenomena()) {
							
									case SI:
										soeParaDB.setSolsticioInvierno(true);
										soeParaDB.setStartingSeason(1);
										break;
									
									case EP:
										soeParaDB.setEquinoccioPrimavera(true);
										soeParaDB.setStartingSeason(2);
										break;
									
									case SV:
										soeParaDB.setSolsticioVerano(true);
										soeParaDB.setStartingSeason(3);
										break;
									
									case EO:
										soeParaDB.setEquinoccioOtonyo(true);
										soeParaDB.setStartingSeason(4);
										break;
								}
							
								soeParaDB.setYear(LocalDateTime.parse(soeAPI.getDate()).getYear());
								soeParaDB.setDate(LocalDateTime.parse(soeAPI.getDate()));
								
								this.solsticiosYEquinocciosRepository.save(soeParaDB);
							}
							
							AllSoEsEntity allSoEsParaDB = new AllSoEsEntity();
							
							switch (soeAPI.getPhenomena()) {
							
								case SI:
									allSoEsParaDB.setSolsticioInvierno(true);
									break;
							
								case EP:
									allSoEsParaDB.setEquinoccioPrimavera(true);
									break;
							
								case SV:
									allSoEsParaDB.setSolsticioVerano(true);
									break;
							
								case EO:
									allSoEsParaDB.setEquinoccioOtonyo(true);
									break;
							}
							
							String[] parts = String.valueOf(soeAPI.getDate()).split("T");
							String[] dateParts = parts[0].split("-");
							String[] timeParts = parts[1].split(":");

							if(String.valueOf(soeAPI.getDate()).startsWith("-")) {
								allSoEsParaDB.setYear(Integer.parseInt("-" + dateParts[1]));
								allSoEsParaDB.setMonth(Integer.parseInt(dateParts[2]));
								allSoEsParaDB.setDay(Integer.parseInt(dateParts[3]));
							}
							else {
								allSoEsParaDB.setYear(Integer.parseInt(dateParts[0]));
								allSoEsParaDB.setMonth(Integer.parseInt(dateParts[1]));
								allSoEsParaDB.setDay(Integer.parseInt(dateParts[2]));
							}
							

							allSoEsParaDB.setHour(Integer.parseInt(timeParts[0]));
							allSoEsParaDB.setMinute(Integer.parseInt(timeParts[1]));
							allSoEsParaDB.setSecond(Integer.parseInt(timeParts[2]));
										
							this.allSoEsRepository.save(allSoEsParaDB);
						}						
						
						System.out.println("Actualizados los solsticios y equinoccios del anyo: " + i);
													
					}
					else {
						
						System.out.println("No se han obtenido solsticios ni equinoccios de la API.");
						resultado = "Error al actualizar solsticios y equinoccios: no se han obtenido solsticios ni equinoccios de la API.";
					}	
				}
				catch (Exception e) {
					System.out.println("Error al actualizar solsticios y equinoccios del anyo " + i  +": "+ e);
					resultado = "Error al actualizar solsticios y equinoccios, checkear logs.";
				}
				
				
			}				
		}
		else {
			
			if(apiGetSYEUrl == null) {
				System.out.println("La URL de la API para obtener los soes es nula.");
				resultado = "Error al actualizar los solsticios y equinoccios: la URL de la API para obtener los soes es nula..";
			}
			else if(!allSoes.isEmpty()) {
				System.out.println("Ya hay soes en la base de datos.");
				resultado = "Error al actualizar los solsticios y equinoccios: ya hay soes en la base de datos.";
			}
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
