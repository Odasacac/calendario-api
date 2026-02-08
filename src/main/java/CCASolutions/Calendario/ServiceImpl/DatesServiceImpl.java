package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.MonthService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
public class DatesServiceImpl implements DatesService {


	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Autowired
	private MetonsService metonsService;
	
	@Autowired
	private MonthService monthsService;
	
	@Autowired
	private MonthsRepository monthsRepository;
	
	@Autowired
	private WeeksRepository weeksRepository;
	
	@Autowired
	private DaysRepository daysRepository;
	
	@Autowired
	private LunasService lunasService;
	
	public DateDTO getDateVAUFromDateO (LocalDateTime dateO, boolean future) {
		
		DateDTO dateVAU = new DateDTO();
		
		if(future) {
			
			dateVAU = this.getDateVAUFromDateOViaAPI(dateO);
		}
		else {
			
			dateVAU = this.getDateVAUFromDateOViaDB(dateO);
		}
		
		return dateVAU;
	}
	
	public boolean esFechaDeAnyoFuturo(LocalDateTime date) {
		
		boolean esFechaFutura = false;
		
		if(date.getYear() > (LocalDateTime.now().getYear())) {
			
			esFechaFutura = true;
		}	
		
		return esFechaFutura;
	}
	
	
	// ========================= METODOS PRIVADOS
	
	
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
	
	// ============ DB
	
	private DateDTO getDateVAUFromDateOViaDB (LocalDateTime dateO) {
		
		DateDTO dateVAU = new DateDTO();
		
		LocalDateTime dateLastMeton = this.metonsService.getLastMetonDate(dateO);
		
		if(dateLastMeton != null) {
			
			List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = this.solsticiosYEquinocciosService.getSolsticiosYEquinocciosDesdeElMetonoViaDB(dateO);
			List<LunarPhaseDTO> fasesLunaresDelAnyo = this.lunasService.getFasesLunaresDelAnyoViaDB(String.valueOf(dateO.getYear()));
			
			dateVAU.setMeton(String.valueOf(dateLastMeton.getYear()));
			dateVAU.setYear(getVAUYear(dateO, dateLastMeton, solsticiosYEquinocciosDesdeElMetono));
			//dateVAU.setMonth(this.getVAUMonthViaDB(dateO, fasesLunaresDelAnyo, solsticiosYEquinocciosDesdeElMetono));
			
			//VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDayViaDB(dateO, fasesLunaresDelAnyo, urls);
			//dateVAU.setWeek(vauWeekAndDay.getWeek());
			//dateVAU.setDay(vauWeekAndDay.getDay());
		}
		
		
		return dateVAU;
	}
	
	
	// ============ API
	

	private DateDTO getDateVAUFromDateOViaAPI(LocalDateTime dateO) {

		DateDTO dateVAU = new DateDTO();
		
		LocalDateTime dateLastMeton = this.metonsService.getLastMetonDate(dateO);
		
		if(dateLastMeton != null) {
			
			List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono = new ArrayList<>();
			List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();

			List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("ASYEF", "YLP"));
			
			for (DatosEntity url : urls) 
			{
				switch (url.getConcepto()) {
				
					case "ASYEF":
						
						solsticiosYEquinocciosDesdeElMetono = this.solsticiosYEquinocciosService.getSolsticiosYEquinocciosDesdeElMetonoViaAPI(dateO, dateLastMeton, url.getValor());
						break;
					
					case "YLP":
						
						fasesLunaresDelAnyo = this.lunasService.getFasesLunaresDelAnyoViaAPI(String.valueOf(dateO.getYear()), url.getValor());
						break;						
				}
			}
			
			dateVAU.setMeton(String.valueOf(dateLastMeton.getYear()));
			dateVAU.setYear(getVAUYear(dateO, dateLastMeton, solsticiosYEquinocciosDesdeElMetono));
			dateVAU.setMonth(getVAUMonthViaAPI(dateO, fasesLunaresDelAnyo, solsticiosYEquinocciosDesdeElMetono));
			
			VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDayViaAPI(dateO, fasesLunaresDelAnyo, urls);
			dateVAU.setWeek(vauWeekAndDay.getWeek());
			dateVAU.setDay(vauWeekAndDay.getDay());
		}
		

		return dateVAU;
	}
	
	
	
	
	private String getVAUMonthViaAPI(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo, List<FenomenoDTO> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauMonth = "-";
		
		if(!this.lunasService.esDateOLunaNueva(dateO, fasesLunaresDelAnyo)) {
			
			List<FenomenoDTO> lastAndNextSOE = this.solsticiosYEquinocciosService.getLastAndNextSOEFrom(dateO, solsticiosYEquinocciosDesdeElMetono);
			
			int season = 1;
			int monthOfSeason = 0;
			
			if(lastAndNextSOE.size()==2) {
				
				FenomenoDTO lastSOE = lastAndNextSOE.get(0);
				FenomenoDTO nextSOE = lastAndNextSOE.get(1);
				
				if(lastSOE.getDate().toLocalDate().isAfter(nextSOE.getDate().toLocalDate())) {
					
					lastSOE = lastAndNextSOE.get(1);
					nextSOE = lastAndNextSOE.get(0);
				}
				
				List<LunarPhaseDTO> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = this.lunasService.getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(fasesLunaresDelAnyo, lastSOE, nextSOE);

				season = this.monthsService.getSeasonDelMesHibridoSiLoEs(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, nextSOE, dateO);
	
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
					
					monthOfSeason = this.lunasService.lunasNuevasPasadasDesdeLastSOEHastaDateO(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, dateO);
				}
			}
	
			
			MonthsEntity vauMonthEntity = this.monthsRepository.findBySeasonAndMonthOfSeason(season, monthOfSeason);
				
			vauMonth = vauMonthEntity.getName();		
			
			
		}
		
		
		return vauMonth;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDayViaAPI(LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo, List<DatosEntity> urls) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		WeeksEntity vauWeek = new WeeksEntity();
		
		long dayWeekNumber = 0L;
		DaysEntity vauDay = new DaysEntity();
		
		LunarPhaseDTO lastNewMoon = this.getLastNewMoonForADateOViaAPI(dateO, fasesLunaresDelAnyo, urls);
		
		long diasDesdeLaLunaNueva = ChronoUnit.DAYS.between(lastNewMoon.getDate().toLocalDate(), dateO.toLocalDate());
		
		if (diasDesdeLaLunaNueva == 0) {
			
			vauWeek.setName("-");
			vauDay.setName("-");
		}
		else if (diasDesdeLaLunaNueva <= 7) {
			
			vauWeek = this.weeksRepository.findByWeekOfMonth("1");
			dayWeekNumber = diasDesdeLaLunaNueva;
			vauDay = this.daysRepository.findByDayOfWeek(String.valueOf(dayWeekNumber));
			
		} else if (diasDesdeLaLunaNueva <= 14) {
			
			vauWeek = this.weeksRepository.findByWeekOfMonth("2");
			dayWeekNumber = diasDesdeLaLunaNueva-7;
			vauDay = this.daysRepository.findByDayOfWeek(String.valueOf(dayWeekNumber));

		} else if (diasDesdeLaLunaNueva <= 21) {
			
			vauWeek = this.weeksRepository.findByWeekOfMonth("3");
			dayWeekNumber = diasDesdeLaLunaNueva-14;
			vauDay = this.daysRepository.findByDayOfWeek(String.valueOf(dayWeekNumber));

		} else if (diasDesdeLaLunaNueva <= 28) {
			
			vauWeek = this.weeksRepository.findByWeekOfMonth("4");
			dayWeekNumber = diasDesdeLaLunaNueva-21;
			vauDay = this.daysRepository.findByDayOfWeek(String.valueOf(dayWeekNumber));
		}
		else {
			vauWeek.setName("-");
			dayWeekNumber = diasDesdeLaLunaNueva-21;
			vauDay = this.daysRepository.findByDayOfWeek(String.valueOf(dayWeekNumber));
		}
		
		vauWeekAndDay.setWeek(vauWeek.getName());
		vauWeekAndDay.setDay(vauDay.getName());
		
		return vauWeekAndDay;
	}
	

	
	
	private LunarPhaseDTO getLastNewMoonForADateOViaAPI (LocalDateTime dateO, List<LunarPhaseDTO> fasesLunaresDelAnyo, List<DatosEntity> urls) {
		
		LunarPhaseDTO lastNewMoon = new LunarPhaseDTO();
		Long diasLastLPConMenorDiferenciaConLaFechaO = Long.MAX_VALUE;
		Long diasLastLPDeDiferenciaConLaFechaO = Long.MAX_VALUE;
		
		for(int i = 0; i<fasesLunaresDelAnyo.size(); i++) {
			
			LunarPhaseDTO faseLunar = fasesLunaresDelAnyo.get(i);

			if("NewMoon".equals(faseLunar.getMoonPhase()) && 
				(faseLunar.getDate().toLocalDate().isBefore(dateO.toLocalDate())|| faseLunar.getDate().toLocalDate().isEqual(dateO.toLocalDate()))) {
				
				diasLastLPDeDiferenciaConLaFechaO = ChronoUnit.DAYS.between(faseLunar.getDate(), dateO);
				
				if(diasLastLPDeDiferenciaConLaFechaO < diasLastLPConMenorDiferenciaConLaFechaO) {
					
					lastNewMoon.setDate(faseLunar.getDate());
					lastNewMoon.setMoonPhase(faseLunar.getMoonPhase());
					diasLastLPConMenorDiferenciaConLaFechaO = diasLastLPDeDiferenciaConLaFechaO;
				}	
			}	
		}
		
		if(lastNewMoon.getDate() == null) {

			for (DatosEntity url : urls) 
			{
				if ("YLP".equals(url.getConcepto())) {	
					String anyo = String.valueOf(dateO.minusYears(1).getYear());
					List<LunarPhaseDTO> fasesLunaresDelAnyoAnterior = this.lunasService.getFasesLunaresDelAnyoViaAPI(anyo, url.getValor());
					lastNewMoon = this.getLastNewMoonForADateOViaAPI(dateO, fasesLunaresDelAnyoAnterior, urls);
					break;	
				}
			}						
		}
		
		return lastNewMoon;
	}
	


	
}







