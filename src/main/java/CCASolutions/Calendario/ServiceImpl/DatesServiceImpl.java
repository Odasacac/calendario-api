package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.GASYEFDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class DatesServiceImpl implements DatesService {


	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private MonthsRepository monthsRepository;

	
	private final RestTemplate restTemplate = new RestTemplate();


	@Override
	public DateDTO getDateVAUFromDateO(LocalDateTime dateO) {

		DateDTO dateVAU = new DateDTO();
		
		LocalDateTime dateLastMeton = this.getLastMetonDate(dateO);
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();
		

		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("ASYEF", "YLP"));
	
		
		for (DatosEntity url : urls) 
		{
			switch (url.getConcepto()) {
			
				case "ASYEF":
					
					solsticiosYEquinocciosDesdeElMetono = this.getSolsticiosYEquinocciosDesdeElMetono(dateO, url.getValor());
					break;
				
				case "YLP":
					
					fasesLunaresDelAnyo = this.getFasesLunaresDelAnyo(dateO, url.getValor());
					break;
					
			}
		}
		
		
		dateVAU.setYear(getVAUYear(dateO, dateLastMeton, solsticiosYEquinocciosDesdeElMetono));

		dateVAU.setMonth(getVAUMonth(dateO, fasesLunaresDelAnyo, solsticiosYEquinocciosDesdeElMetono));

		//dateVAU.setWeek(getVauWeek(dateO, moonPhases));

		//dateVAU.setDay(getVAUDay(dateO, moonPhases));

		return dateVAU;
	}
	
	
	// METODOS PRIVADOS
	

	private String getVAUYear(LocalDateTime dateO, LocalDateTime dateLastMeton, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
	
		String vauYear = "-";
		
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
	
	
	private String getVAUMonth(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauMonth = "-";
		
		if(!this.esLunaNueva(dateO, fasesLunaresDelAnyo)) {
			
			List<FenomenoDTO> lastAndNextSOE = this.getLastAndNextSOEFrom(dateO, solsticiosYEquinocciosDesdeElMetono);
			
			int season = 1;
			int monthOfSeason = 0;
			
			if(lastAndNextSOE.size()==2) {
				
				FenomenoDTO lastSOE = lastAndNextSOE.get(0);
				FenomenoDTO nextSOE = lastAndNextSOE.get(1);
				
				if(lastSOE.getDate().toLocalDate().isAfter(nextSOE.getDate().toLocalDate())) {
					
					lastSOE = lastAndNextSOE.get(1);
					nextSOE = lastAndNextSOE.get(0);
				}
				
				List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = this.getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(fasesLunaresDelAnyo, lastSOE, nextSOE);

				season = this.getSeasonDelMesHibridoSiLoEs(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, nextSOE, dateO);
	
				if(season == 0) {
					
					switch (lastSOE.getPhenomena()) {
					
						case "WinterSolstice":
							season=1;
							break;
						
						case "VernalEquinox":
							season=2;
							break;
						
						case "SummerSolstice":
							season=3;
							break;
						
						case "AutumnalEquinox":
							season=4;
							break;
					}
					
					monthOfSeason = this.lunasNuevasPasadasDesdeLastSOEHastaDateO(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, dateO);
				}
			}
	
			
			MonthsEntity vauMonthEntity = this.monthsRepository.findBySeasonAndMonthOfSeason(season, monthOfSeason);
				
			vauMonth = vauMonthEntity.getName();		
			
			
		}
		
		
		return vauMonth;
	}
	
	private int lunasNuevasPasadasDesdeLastSOEHastaDateO (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, LocalDateTime dateO){
		
		int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
		
		for(LunarPhaseDTO lunaNueva : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {				
			
			if(dateO.toLocalDate().isAfter(lunaNueva.getDate().toLocalDate())) {
					
				lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO +1;
			}

		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaDateO;
	}
	
	private int getSeasonDelMesHibridoSiLoEs (List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE, FenomenoDTO lastSOE, FenomenoDTO nextSOE, LocalDateTime dateO) {

		int season = 0;
		
		LunarPhaseDTO lastLP = new LunarPhaseDTO();
		Long diasLastLPConMenorDiferenciaConLaFechaO = 1000L;
		Long diasLastLPDeDiferenciaConLaFechaO = 1000L;
		
		LunarPhaseDTO nextLP = new LunarPhaseDTO();
		Long diasNextLPConMenorDiferenciaConLaFechaO = 1000L;
		Long diasNextLPDeDiferenciaConLaFechaO = 1000L;
		
		for(LunarPhaseDTO luna : lunasNuevasPasadasDesdeLastSOEHastaNextSOE) {
			
			if(luna.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
				
				diasLastLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(luna.getDate(), dateO);
				
				if(diasLastLPDeDiferenciaConLaFechaO < diasLastLPConMenorDiferenciaConLaFechaO) {
					
					lastLP.setDate(luna.getDate());
					lastLP.setMoonPhase(luna.getMoonPhase());
					diasLastLPConMenorDiferenciaConLaFechaO = diasLastLPDeDiferenciaConLaFechaO;

				}	
			}
			else if(luna.getDate().toLocalDate().isAfter(dateO.toLocalDate())) {
				
				diasNextLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(dateO, luna.getDate());
				
				if(diasNextLPDeDiferenciaConLaFechaO < diasNextLPConMenorDiferenciaConLaFechaO) {
					
					nextLP.setDate(luna.getDate());
					nextLP.setMoonPhase(luna.getMoonPhase());
					diasNextLPConMenorDiferenciaConLaFechaO = diasNextLPDeDiferenciaConLaFechaO;

				}	
			}
		}
		
		if(lastLP.getDate() == null) {
			
			switch (lastSOE.getPhenomena()) {
			
				case "VernalEquinox":
					season = 2;
					break;
				case "SummerSolstice":
					season = 3;
					break;
				case "AutumnalEquinox":
					season = 4;
					break;
				case "WinterSolstice":
					season = 1;
					break;
			}
		}
		else if (nextLP.getDate() == null) {
			
			switch (nextSOE.getPhenomena()) {
			
				case "VernalEquinox":
					season = 2;
					break;
				case "SummerSolstice":
					season = 3;
					break;
				case "AutumnalEquinox":
					season = 4;
					break;
				case "WinterSolstice":
					season = 1;
					break;
			}
		}
		
		return season;
	}
	
	
	private List<LunarPhaseDTO> getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(List<LunarPhaseDTO> fasesLunaresDelAnyo, FenomenoDTO lastSOE, FenomenoDTO nextSOE) {
		
		List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = new ArrayList<>();
		
		for(LunarPhaseDTO faseLunar :fasesLunaresDelAnyo) {
			
			if(faseLunar.getDate().isAfter(lastSOE.getDate()) && faseLunar.getDate().isBefore(nextSOE.getDate()) && "NewMoon".equals(faseLunar.getMoonPhase())) {
				
				lunasNuevasPasadasDesdeLastSOEHastaNextSOE.add(faseLunar);
			}
			
		}
		
		return lunasNuevasPasadasDesdeLastSOEHastaNextSOE;
		
	}
	
	private List<FenomenoDTO> getLastAndNextSOEFrom (LocalDateTime dateO, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
		
		List<FenomenoDTO> lastAndNextSOE = new ArrayList<>();
		
		FenomenoDTO lastSOE = new FenomenoDTO();		
		Long diasLastSOEConMenorDiferenciaConLaFechaO = 1000L;
		Long diasLastSOEDeDiferenciaConLaFechaO = 1000L;
		
		FenomenoDTO nextSOE = new FenomenoDTO();
		Long diasNextSOEConMenorDiferenciaConLaFechaO = 1000L;
		Long diasNextSOEDeDiferenciaConLaFechaO = 1000L;

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
	


	private boolean esLunaNueva(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo) {
		
		boolean esLunaNueva = false;
		
		for(int i = 0; i < fasesLunaresDelAnyo.size() && !esLunaNueva; i++) {			
				
			LunarPhaseDTO fenomeno = fasesLunaresDelAnyo.get(i);
				
			if("NewMoon".equals(fenomeno.getMoonPhase()) && dateO.toLocalDate().isEqual(fenomeno.getDate().toLocalDate())) {
				
				esLunaNueva = true;
			}
			
		}

		
		return esLunaNueva;
	}
	
	private List<LunarPhaseDTO> getFasesLunaresDelAnyo(LocalDateTime dateO, String url){
		
		List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();
		
		// https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{XXXX}}
		
		String urlParaLlamada = url.replace("{{YYYY}}", String.valueOf(dateO.getYear()));
		

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
	

	
	private List<FenomenoDTO> getSolsticiosYEquinocciosDesdeElMetono (LocalDateTime dateO, String url){
		
		List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
		
		LocalDateTime dateLastMeton = this.getLastMetonDate(dateO);
		
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
	
	 
	private List<FenomenoDTO> getGASYEFDTO(String url){
		
		List<FenomenoDTO> fenomenos = new ArrayList<>();
		
		GASYEFDTO responseOPALEAPI = restTemplate.getForObject(url, GASYEFDTO.class);
		
		if(responseOPALEAPI != null && responseOPALEAPI.getResponse() != null && responseOPALEAPI.getResponse().getData() != null) {
			fenomenos = responseOPALEAPI.getResponse().getData();
		}
		
		return fenomenos;
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







