package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
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
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Autowired
	private MetonsService metonsService;
	
	@Autowired
	private MonthService monthsService;
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private MonthsRepository monthsRepository;
	
	@Autowired
	private WeeksRepository weeksRepository;
	
	@Autowired
	private DaysRepository daysRepository;
	
	@Autowired
	private LunasService lunasService;
	

	public LocalDateTime getDateOFromDateVAU(DateDTOFromDB dateVAU) {

		LocalDateTime dateO = LocalDateTime.now();
		
		
		
		
		return dateO;
	}

	
	public DateDTO getDateVAUFromDateO (LocalDateTime dateO) {
		
		DateDTO dateVAU = new DateDTO();
		
		LocalDateTime dateLastMeton = this.metonsService.getLastMetonDate(dateO);
		
		if(dateLastMeton != null) {
			
			List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono = this.solsticiosYEquinocciosService.getSolsticiosYEquinocciosDesdeElMetono(dateO, dateLastMeton);
			List<LunasEntity> fasesLunaresDelAnyo = this.lunasService.getFasesLunaresDelAnyo(String.valueOf(dateO.getYear()));
			
			if(!solsticiosYEquinocciosDesdeElMetono.isEmpty() && !fasesLunaresDelAnyo.isEmpty()) {
								dateVAU.setMeton(String.valueOf(dateLastMeton.getYear()));
				dateVAU.setYear(getVAUYear(dateO, dateLastMeton, solsticiosYEquinocciosDesdeElMetono));
				dateVAU.setMonth(this.getVAUMonth(dateO, fasesLunaresDelAnyo, solsticiosYEquinocciosDesdeElMetono));
				
				VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(dateO, fasesLunaresDelAnyo);
				dateVAU.setWeek(vauWeekAndDay.getWeek());
				dateVAU.setDay(vauWeekAndDay.getDay());
			}

		}		
		
		return dateVAU;
		
	}
	

	
	
	// ========================= METODOS PRIVADOS
	
	
	private String getVAUYear(LocalDateTime dateO, LocalDateTime dateLastMeton, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauYear = "-";
		
		if(solsticiosYEquinocciosDesdeElMetono != null && !dateO.toLocalDate().isEqual(dateLastMeton.toLocalDate())) {

			if(solsticiosYEquinocciosDesdeElMetono.size() == 0) {
				
				vauYear = "0";
			}
			else {
				
				int vauYearInt = 0;
	
				boolean esSolsticioDeInvierno = false;
				
				for(int i = 0; i < solsticiosYEquinocciosDesdeElMetono.size() && !esSolsticioDeInvierno; i++) {			
					
					SolsticiosYEquinocciosEntity soe = solsticiosYEquinocciosDesdeElMetono.get(i);
					
					if(soe.isSolsticioInvierno()) {
						
						if(dateO.toLocalDate().isAfter(soe.getDate().toLocalDate())) {
							
							vauYearInt = vauYearInt +1;
						}
						else if (dateO.toLocalDate().isEqual(soe.getDate().toLocalDate())) {
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
	
	
	
	private String getVAUMonth(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo, List<SolsticiosYEquinocciosEntity> solsticiosYEquinocciosDesdeElMetono) {
		
		String vauMonth = "-";
		
		if(!this.lunasService.esDateOLunaNueva(dateO, fasesLunaresDelAnyo)) {
			
			List<SolsticiosYEquinocciosEntity> lastAndNextSOE = this.solsticiosYEquinocciosService.getLastAndNextSOEFrom(dateO, solsticiosYEquinocciosDesdeElMetono);
			
			int season = 1;
			int monthOfSeason = 0;
			
			if(lastAndNextSOE.size()==2) {
				
				SolsticiosYEquinocciosEntity lastSOE = lastAndNextSOE.get(0);
				SolsticiosYEquinocciosEntity nextSOE = lastAndNextSOE.get(1);
				
				if(lastSOE.getDate().toLocalDate().isAfter(nextSOE.getDate().toLocalDate())) {
					
					lastSOE = lastAndNextSOE.get(1);
					nextSOE = lastAndNextSOE.get(0);
				}
				
				List<LunasEntity> lunasNuevasPasadasDesdeLastSOEHastaNextSOE = this.lunasService.getLunasNuevasPasadasDesdeLastSOEHastaNextSOE(fasesLunaresDelAnyo, lastSOE, nextSOE);

				season = this.monthsService.getSeasonDelMesHibridoSiLoEs(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, nextSOE, dateO);
	
				if(season == 0) {
					
					if(lastSOE.isSolsticioInvierno()) {
						
						season = 1;
					}
					else if(lastSOE.isEquinoccioPrimavera()) {
						
						season = 2;
					}
					else if(lastSOE.isSolsticioVerano()) {
						
						season = 3;
					}
					else if(lastSOE.isEquinoccioOtonyo()) {
						
						season = 4;
					}
					
					monthOfSeason = this.lunasService.getLunasNuevasPasadasDesdeLastSOEHastaDateO(lunasNuevasPasadasDesdeLastSOEHastaNextSOE, lastSOE, dateO);
				}
			}
	
			
			MonthsEntity vauMonthEntity = this.monthsRepository.findBySeasonAndMonthOfSeason(season, monthOfSeason);
				
			if(vauMonthEntity != null) {
				
				vauMonth = vauMonthEntity.getName();
			}
				
			
		}
		
		
		return vauMonth;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> fasesLunaresDelAnyo) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		WeeksEntity vauWeek = new WeeksEntity();
		vauWeek.setName("-");
		
		long dayWeekNumber = 0L;
		DaysEntity vauDay = new DaysEntity();
		vauDay.setName("-");
		
		LunasEntity lastNewMoon = this.lunasService.getLastNewMoonForADateO(dateO, fasesLunaresDelAnyo);	
		
		if(lastNewMoon != null) {
			
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
		}
		
		
		
		vauWeekAndDay.setWeek(vauWeek.getName());
		vauWeekAndDay.setDay(vauDay.getName());
		
		return vauWeekAndDay;
	}


	public DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU) {

		DateDTOFromDB dateVAUDTOFromDB = new DateDTOFromDB();
		
		try {
			
			MetonsEntity lastMeton = this.metonsRepository.findByNuevoTrueAndInicialTrueAndYear(Integer.valueOf(dateVAU.getMeton()));
			MetonsEntity nextMeton = this.metonsService.getNextMetonDateByYear(Integer.valueOf(dateVAU.getMeton()));			
			boolean anyoCuadra = (Integer.valueOf(nextMeton.getYear()) - Integer.valueOf(lastMeton.getYear())) > Integer.valueOf(dateVAU.getYear());			
			MonthsEntity month = this.monthsRepository.findByName(dateVAU.getMonth());
			WeeksEntity week = this.weeksRepository.findByName(dateVAU.getWeek());
			DaysEntity day = this.daysRepository.findByName(dateVAU.getDay());
			
			if(lastMeton != null && month != null && week != null && day != null && anyoCuadra) {
				
				dateVAUDTOFromDB.setValid(true);
				dateVAUDTOFromDB.setMeton(lastMeton);
				dateVAUDTOFromDB.setYear(Integer.valueOf(dateVAU.getYear()));
				dateVAUDTOFromDB.setMonth(month);
				dateVAUDTOFromDB.setWeek(week);
				dateVAUDTOFromDB.setDay(day);
			}
			
		}
		catch(Exception e) {
			
			System.out.println("Error al obtener el dateVAUDTO de la base de datos: " + e);
		}
		
		
		return dateVAUDTOFromDB;
	}



	
}







