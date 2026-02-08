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
	

	public String updateSolsticiosYEquinoccios() {
		
		String resultado = "Solsticios y equinoccios actualizados sin problema";
		
		DatosEntity apiGetSYEUrl = datosRepository.findByConcepto("ASYEF");
		
		if(apiGetSYEUrl != null) {	
			
			int anyoDelUltimoSOEGuardado = this.getAnyoDelUltimoSOEGuardado();
			
			int anyoParaLaApi = anyoDelUltimoSOEGuardado+1;
			
			
			
			while (anyoParaLaApi <= LocalDateTime.now().getYear()) {
				
				System.out.println("Actualizando los solsticios y equinoccios del anyo: " + anyoParaLaApi);
				
				List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = this.getSolsticiosYEquinocciosDelAnyoViaAPI(String.valueOf(anyoParaLaApi), apiGetSYEUrl.getValor());
				
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
					
					System.out.println("Actualizados los solsticios y equinoccios del anyo: " + anyoParaLaApi);
												
				}
				else {
					
					System.out.println("No se han obtenido solsticios ni equinoccios de la API.");
					resultado = "Error al actualizar solsticios y equinoccios, checkear logs.";
				}	
				anyoParaLaApi = anyoParaLaApi +1;
				
			}				
		}
		else {
			
			System.out.println("La URL de la API para obtener los solsticios y equinoccios es nula.");
		}
		
		return resultado;
	}
	
	public List<SolsticiosYEquinocciosEntity> getSolsticiosYEquinocciosEntityFromFenomenoDTO(List<FenomenoDTO> fenomenoDTO){
		
		List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosEntity = new ArrayList<>();
		
		return solsticiosYEquinocciosEntity;
	}

	
	
	public void saveOne (SolsticiosYEquinocciosEntity soeParaDB) {

		this.solsticiosYEquinocciosRepository.save(soeParaDB);
		
	}
	
	public List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetonoViaDB (LocalDateTime dateO){
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetonoViaDB = new ArrayList<>();
		
		return solsticiosYEquinocciosDesdeElMetonoViaDB;
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
	
	
	public List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetonoViaAPI (LocalDateTime dateO, LocalDateTime dateLastMeton, String url){
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		
		if((dateO.getYear() - dateLastMeton.getYear()) > 0) {
			
			// https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}
			String urlParaLlamada = url.replace("{{YYYY}}", String.valueOf(dateLastMeton.getYear())).replace("{{NNNN}}", String.valueOf(dateO.getYear() - dateLastMeton.getYear()+1));
			
			try {
				solsticiosYEquinocciosDesdeElMetono = this.getGASYEFDTO(urlParaLlamada);
			}
			catch (Exception e) {
				System.out.println("Error al llamar a GASYEF API: " + e);
			}
			
		}
		
		return solsticiosYEquinocciosDesdeElMetono;
		
	}
	
	public List<FenomenoDTO> getLastAndNextSOEFrom (LocalDateTime dateO, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
		
		List<FenomenoDTO> lastAndNextSOE = new ArrayList<>();
		
		FenomenoDTO lastSOE = new FenomenoDTO();		
		Long diasLastSOEConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastSOEDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		FenomenoDTO nextSOE = new FenomenoDTO();
		Long diasNextSOEConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasNextSOEDeDiferenciaConLaFechaO = Long.MAX_VALUE;

		boolean caeEnSOE = false;
		
		for(int i = 0; i < solsticiosYEquinocciosDesdeElMetono.size() && !caeEnSOE; i++) {			
			
			FenomenoDTO fenomeno = solsticiosYEquinocciosDesdeElMetono.get(i);
		
			if(dateO.toLocalDate().isAfter(fenomeno.getDate().toLocalDate())) {
				
				if(fenomeno.getDate().getYear() == dateO.getYear() || fenomeno.getDate().getYear() == dateO.getYear()-1) {
					
					diasLastSOEDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(fenomeno.getDate(), dateO);
					
					if(diasLastSOEDeDiferenciaConLaFechaO < diasLastSOEConMenorDiferenciaConLaFechaO) {
						
						lastSOE.setDate(fenomeno.getDate());
						lastSOE.setPhenomena(fenomeno.getPhenomena());
						diasLastSOEConMenorDiferenciaConLaFechaO = diasLastSOEDeDiferenciaConLaFechaO;
					}	
				}
			}
			else if(dateO.toLocalDate().isBefore(fenomeno.getDate().toLocalDate()) && fenomeno.getDate().getYear() == dateO.getYear() ) {
				
				diasNextSOEDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(dateO, fenomeno.getDate());
				
				if(diasNextSOEDeDiferenciaConLaFechaO < diasNextSOEConMenorDiferenciaConLaFechaO) {
					
					nextSOE.setDate(fenomeno.getDate());
					nextSOE.setPhenomena(fenomeno.getPhenomena());
					diasNextSOEConMenorDiferenciaConLaFechaO = diasNextSOEDeDiferenciaConLaFechaO;
				}	
			}
			else if(dateO.toLocalDate().isEqual(fenomeno.getDate().toLocalDate())) {
				
				lastSOE.setDate(fenomeno.getDate());
				lastSOE.setPhenomena(fenomeno.getPhenomena());
				nextSOE.setDate(fenomeno.getDate());
				nextSOE.setPhenomena(fenomeno.getPhenomena());
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
