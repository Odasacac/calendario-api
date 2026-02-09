package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
			
			int anyoDelUltimoSOEGuardado = this.getAnyoDelUltimoSOEGuardado();
			
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
								break;
								
							case "VernalEquinox":
								soeParaDB.setEquinoccioPrimavera(true);
								break;
								
							case "SummerSolstice":
								soeParaDB.setSolsticioVerano(true);
								break;
								
							case "AutumnalEquinox":
								soeParaDB.setEquinoccioOtonyo(true);
								break;
						}
						
						soeParaDB.setYear(soeAPI.getDate().getYear());
						soeParaDB.setDate(soeAPI.getDate());
						
						try {
							
							this.saveOne(soeParaDB);
							
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
	
	
	
	public void saveOne (SolsticiosYEquinocciosEntity soeParaDB) {

		this.solsticiosYEquinocciosRepository.save(soeParaDB);
		
	}
	
	public List<SolsticiosYEquinocciosEntity> getSolsticiosYEquinocciosDesdeElMetono (LocalDateTime dateO, LocalDateTime dateLastMeton){
				
		return this.solsticiosYEquinocciosRepository.findByYearBetweenOrderByDateAsc(dateLastMeton.getYear(), dateO.getYear());
	}
	
	public int getAnyoDelUltimoSOEGuardado() {
		
		int anyoDelUltimoSOEGuardado = 0;
		
		SolsticiosYEquinocciosEntity ultimaSOEAlmacenado = this.solsticiosYEquinocciosRepository.findTopByOrderByDateDesc();
		
		if(ultimaSOEAlmacenado != null) {
			
			anyoDelUltimoSOEGuardado = ultimaSOEAlmacenado.getYear();
		}
		
		return anyoDelUltimoSOEGuardado;
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
	
	
	public List<SolsticiosYEquinocciosEntity> getLastAndNextSOEFrom (LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		List<SolsticiosYEquinocciosEntity> lastAndNextSOE = new ArrayList<>();
		
		SolsticiosYEquinocciosEntity lastSOE = new SolsticiosYEquinocciosEntity();		
		Long diasLastSOEConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastSOEDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		SolsticiosYEquinocciosEntity nextSOE = new SolsticiosYEquinocciosEntity();
		Long diasNextSOEConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasNextSOEDeDiferenciaConLaFechaO = Long.MAX_VALUE;

		boolean caeEnSOE = false;
		
		for(int i = 0; i < solsticiosYEquinocciosDesdeElMetono.size() && !caeEnSOE; i++) {			
			
			SolsticiosYEquinocciosEntity soe = solsticiosYEquinocciosDesdeElMetono.get(i);
		
			if(dateO.toLocalDate().isAfter(soe.getDate().toLocalDate())) {
				
				if(soe.getDate().getYear() == dateO.getYear() || soe.getDate().getYear() == dateO.getYear()-1) {
					
					diasLastSOEDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(soe.getDate(), dateO);
					
					if(diasLastSOEDeDiferenciaConLaFechaO < diasLastSOEConMenorDiferenciaConLaFechaO) {
						
						lastSOE = soe;					
						
						diasLastSOEConMenorDiferenciaConLaFechaO = diasLastSOEDeDiferenciaConLaFechaO;
					}	
				}
			}
			else if(dateO.toLocalDate().isBefore(soe.getDate().toLocalDate()) && soe.getDate().getYear() == dateO.getYear() ) {
				
				diasNextSOEDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(dateO, soe.getDate());
				
				if(diasNextSOEDeDiferenciaConLaFechaO < diasNextSOEConMenorDiferenciaConLaFechaO) {
					
					nextSOE = soe;
					
					diasNextSOEConMenorDiferenciaConLaFechaO = diasNextSOEDeDiferenciaConLaFechaO;
				}	
			}
			else if(dateO.toLocalDate().isEqual(soe.getDate().toLocalDate())) {
				
				lastSOE = soe;		
				nextSOE = soe;	
				caeEnSOE = true;
			}
		}		
				
		lastAndNextSOE.add(lastSOE);
		
		if(nextSOE.getDate() != null) {
			
			lastAndNextSOE.add(nextSOE);
		}
		
		return lastAndNextSOE;
		
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
