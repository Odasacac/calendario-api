package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.GSYEFDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class DatesServiceImpl implements DatesService {


	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private MetonsRepository metonsRepository;

	
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public DateDTO getDateVAUFromDateO(LocalDateTime dateO) {

		DateDTO dateVAU = new DateDTO();
		
		LocalDateTime dateLastMeton = this.getLastMetonDate(dateO);
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		

		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("ASYEF"));
	
		
		for (DatosEntity url : urls) 
		{
			switch (url.getConcepto()) {
			case "ASYEF":
				solsticiosYEquinocciosDesdeElMetono = this.getSolsticiosYEquinocciosDesdeElMetono(dateO, url.getValor());
		
				break;
			}
		}
		
		
		dateVAU.setYear(getVAUYear(dateO, dateLastMeton, solsticiosYEquinocciosDesdeElMetono));

		//dateVAU.setMonth(getVAUMonth(dateO, solsticiosYEquinoccios, moonPhases));

		//dateVAU.setWeek(getVauWeek(dateO, moonPhases));

		//dateVAU.setDay(getVAUDay(dateO, moonPhases));

		return dateVAU;
	}
	
	
	// MEETODOS PRIVADOS
	
	private String getVAUYear(LocalDateTime dateO, LocalDateTime dateLastMeton, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
	
		String vauYear = "-";

		System.out.println(dateO.toLocalDate());
		System.out.println(dateLastMeton.toLocalDate());
		
		
		if(solsticiosYEquinocciosDesdeElMetono != null || !dateO.toLocalDate().isEqual(dateLastMeton.toLocalDate())) {

			if(solsticiosYEquinocciosDesdeElMetono.size() == 0) {
				
				vauYear = "0";
			}
			else {
				
				int vauYearInt = 0;
	
				boolean esSolsticioDeInvierno = false;
				
				for(int i = 0; i < solsticiosYEquinocciosDesdeElMetono.size() && !esSolsticioDeInvierno; i++) {			
					
					FenomenoDTO fenomeno = solsticiosYEquinocciosDesdeElMetono.get(i);
					
					if("WinterSolstice".equals(fenomeno.getPhenomena())) {
						
						if(dateO.toLocalDate().isAfter(fenomeno.getDate().toLocalDate())) {
							
							vauYearInt = vauYearInt +1;
						}
						else if (dateO.toLocalDate().isEqual(fenomeno.getDate().toLocalDate())) {
							esSolsticioDeInvierno = true;
						}
					}
				}
				
				if(!esSolsticioDeInvierno) {
					vauYear = String.valueOf(vauYearInt);
				}
				
			}
		}
		
		return vauYear;
		
	}
	 
	private List<FenomenoDTO> getSYEF(String url){
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		
		GSYEFDTO responseGetSYEFAPI = restTemplate.getForObject(url, GSYEFDTO.class);
		
		if(responseGetSYEFAPI != null && responseGetSYEFAPI.getResponse() != null && responseGetSYEFAPI.getResponse().getData() != null) {
			solsticiosYEquinocciosDesdeElMetono = responseGetSYEFAPI.getResponse().getData();
		}
		
		return solsticiosYEquinocciosDesdeElMetono;
	}

	
	private List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetono (LocalDateTime dateO, String url){
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		
		LocalDateTime dateLastMeton = this.getLastMetonDate(dateO);
		
		if((dateO.getYear() - dateLastMeton.getYear()) > 0) {
			
			// https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}
			String urlParaLlamada = url.replace("{{YYYY}}", String.valueOf(dateLastMeton.getYear())).replace("{{NNNN}}", String.valueOf(dateO.getYear() - dateLastMeton.getYear()+1));
			
			try {
				solsticiosYEquinocciosDesdeElMetono = this.getSYEF(urlParaLlamada);
			}
			catch (Exception e) {
				System.out.println("Error al llamar a GASYEF API: " + e);
			}
			
		}
		
		return solsticiosYEquinocciosDesdeElMetono;
		
	}
	
	
	private LocalDateTime getLastMetonDate (LocalDateTime dateO) {
		LocalDateTime lastMeton = null;		
		
		try {
			Optional<MetonsEntity> lastMetonOpt= this.metonsRepository.findFirstByDateLessThanEqualAndNuevoTrueAndSolsticialTrueAndInicialTrueOrderByDateDesc(dateO);
			
			if(lastMetonOpt.isPresent()) {
				lastMeton = lastMetonOpt.get().getDate();
			}
		}
		catch (Exception e) {
			System.out.println("Error getting last meton: " + e);
		}
		
		return lastMeton;
	}


}







