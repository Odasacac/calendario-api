package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.EclipenoDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunasSolsticiosEclipsesDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DatesService;

@Service
public class DatesServiceImpl implements DatesService {	
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository; 
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private MonthsRepository monthsRepository;
	
	@Autowired
	private WeeksRepository weeksRepository;
	
	@Autowired
	private DaysRepository daysRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private CasalerosRepository casalerosRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;
	
	// METODOS PUBLICOS 
	
	
	public DateDTO getDateVAUFromDateO (LocalDate date) {
		
		DateDTO dateVAU = null;
		LocalDateTime dateO = date.atTime(LocalTime.MAX);	
		
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAll();
		
		if(!allEclipenos.isEmpty()) {
			
			EclipenosEntity lastEclipenoIN = this.getLastEclipenoIN(allEclipenos, date);		
			
			if(lastEclipenoIN != null) {
				
				List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(lastEclipenoIN.getDate(), dateO.plusYears(1));
				
				if(!allMetons.isEmpty()) {
					
					MetonsEntity lastMetonINForDate = this.getLastMetonINForDate(allMetons, date);
					
					if(lastMetonINForDate != null) {
									
						LunasSolsticiosEclipsesDTO lunasSolsticiosEclipses = new LunasSolsticiosEclipsesDTO();
						lunasSolsticiosEclipses.setLunas(this.lunasRepository.findByDateBetween(dateO.minusYears(1), dateO.plusYears(1)));
						lunasSolsticiosEclipses.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(lastMetonINForDate.getDate().minusYears(1), dateO.plusYears(1)));
						lunasSolsticiosEclipses.setEclipses(this.eclipsesRepository.findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(lastEclipenoIN.getDate().toLocalDate().atStartOfDay(), dateO.plusYears(1)));
						
						if(lunasSolsticiosEclipses.getSoes().isEmpty() || lunasSolsticiosEclipses.getLunas().isEmpty() || lunasSolsticiosEclipses.getEclipses().isEmpty()) {
							
							System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas/eclipses.");
						}
						else {					
	
							dateVAU = new DateDTO();
									
							dateVAU.setYear(this.getVAUYear(lastEclipenoIN, dateO, lunasSolsticiosEclipses.getSoes(), lastMetonINForDate));					
							dateVAU.setMonth(this.getVAUMonth(dateO, lunasSolsticiosEclipses.getSoes(), lunasSolsticiosEclipses.getLunas()));
							
							VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(dateO, lunasSolsticiosEclipses.getLunas());
							dateVAU.setWeek(vauWeekAndDay.getWeek());
							dateVAU.setDay(vauWeekAndDay.getDay());					

							dateVAU.setMetonoIN(getVAUMeton(lastEclipenoIN, allMetons, date));
							dateVAU.setEclipenoIN(this.getVAUEclipeno(lastEclipenoIN, date));			
							dateVAU.setAbsoluteEclipses(this.getVAUAbsoluteEclipses(dateVAU, lunasSolsticiosEclipses.getEclipses(), date, lastMetonINForDate));
							dateVAU.setCasalero(this.getCasalero(lastEclipenoIN.getId()));
							dateVAU.setNotableEvent(this.getNotableEvents(date, lunasSolsticiosEclipses));
							dateVAU.setEstadoLuna(this.getEstadoLuna(date));				
						}
					}
					else {
						System.out.println("Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
					}				
				}
				else {
					System.out.println("Error al obtener dateVAU: no se han encontrado métonos.");
				}
			}
			else {
				System.out.println("Error al obtener dateVAU: no se ha encontrado un eclípeno anterior a la fecha proporcionada.");
			}
		}
		else {
			System.out.println("Error al obtener dateVAU: no hay eclipenos");
		}
		
		
		return dateVAU;
		
	}
	

	
	// ========================= METODOS PRIVADOS
	
	
	private NotableEventDTO getNotableEvents(LocalDate dateO, LunasSolsticiosEclipsesDTO lunasSolsticiosEclipses) {
		
		NotableEventDTO notableEventDTO = new NotableEventDTO();

		notableEventDTO.setToday(this.getEventoActual(dateO, lunasSolsticiosEclipses));
		notableEventDTO.setPrevious(this.getEventoPasado(dateO, lunasSolsticiosEclipses));
		notableEventDTO.setNext(this.getEventoProximo(dateO, lunasSolsticiosEclipses));

		return notableEventDTO;
	}
	
	private String getEventoActual(LocalDate dateO, LunasSolsticiosEclipsesDTO lunasSolsticiosEclipses) {
		
		String eventoActual = "";		
	
		//eventoActual = this.getNotableEventName(luna, soe, meton, eclipse, eclipeno);		
		
		return eventoActual;
	}
	
	private String getEventoPasado(LocalDate dateO, LunasSolsticiosEclipsesDTO lunasSolsticiosEclipses) {
		
		String eventoPasado = "";
		
		LocalDateTime startOfDay = dateO.atStartOfDay();
		
		LunasEntity luna = this.lunasRepository.findFirstByDateBeforeOrderByDateDesc(startOfDay);
		SolsticiosYEquinocciosEntity soe = this.solsticiosYEquinocciosRepository.findFirstByDateBeforeOrderByDateDesc(startOfDay);
		MetonsEntity meton = this.metonsRepository.findFirstByDateBeforeOrderByDateDesc(startOfDay);
		EclipsesEntity eclipse = this.eclipsesRepository.findFirstByDateBeforeOrderByDateDesc(startOfDay);
		EclipenosEntity eclipeno = this.eclipenosRepository.findFirstByDateBeforeOrderByDateDesc(startOfDay);	
		
		
		Long diasEntreLunaYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), dateO);
		Long diasEntreSOEYDate = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), dateO);
		Long diasEntreMetonYDate = ChronoUnit.DAYS.between(meton.getDate().toLocalDate(), dateO);
		Long diasEntreEclipseYDate = ChronoUnit.DAYS.between(eclipse.getDate().toLocalDate(), dateO);
		Long diasEntreEclipenoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), dateO);	    
		  
		long minDias = Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))));
		    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? luna : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? soe : null;
		MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? meton : null;
		EclipsesEntity eclipseParaMetodo = diasEntreEclipseYDate == minDias ? eclipse : null;
		EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? eclipeno : null;
		    
		String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo);
		    
		String dias = " días";
		if(minDias == 1) {
		  dias = " día";
		 }
		
		eventoPasado = nombreDelEvento +" hace "+ minDias + dias;
		

		return eventoPasado;
	}
	
	
	private String getEventoProximo (LocalDate dateO, LunasSolsticiosEclipsesDTO lunasSolsticiosEclipses) {
		
		String eventoFuturo = "";
		
		LocalDateTime endOfDay = dateO.plusDays(1).atStartOfDay();
		
		LunasEntity luna = this.lunasRepository.findFirstByDateAfterOrderByDateAsc(endOfDay);
	    SolsticiosYEquinocciosEntity soeFuturo = this.solsticiosYEquinocciosRepository.findFirstByDateAfterOrderByDateAsc(endOfDay);
	    MetonsEntity meton = this.metonsRepository.findFirstByDateAfterOrderByDateAsc(endOfDay);
	    EclipsesEntity eclipse = this.eclipsesRepository.findFirstByDateAfterOrderByDateAsc(endOfDay);
	    EclipenosEntity eclipeno = this.eclipenosRepository.findFirstByDateAfterOrderByDateAsc(endOfDay);

	    
		
		Long diasEntreLunaYDate = ChronoUnit.DAYS.between(dateO, luna.getDate().toLocalDate());
	    Long diasEntreSOEYDate = ChronoUnit.DAYS.between(dateO, soeFuturo.getDate().toLocalDate());
	    Long diasEntreMetonYDate = ChronoUnit.DAYS.between(dateO, meton.getDate().toLocalDate());
	    Long diasEntreEclipseYDate = ChronoUnit.DAYS.between(dateO, eclipse.getDate().toLocalDate());
	    Long diasEntreEclipenoYDate = ChronoUnit.DAYS.between(dateO, eclipeno.getDate().toLocalDate());	    
	  
	    long minDias = Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))));
	    
	    LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? luna : null;
	    SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? soeFuturo : null;
	    MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? meton : null;
	    EclipsesEntity eclipseParaMetodo = null;
	    EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? eclipeno : null;
	    
	    String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo);
	    
	    String dias = " días";
	    if(minDias == 1) {
	    	dias = " día";
	    }
	    	
	    eventoFuturo = nombreDelEvento+" dentro de "+ minDias + dias;
		   
		return eventoFuturo;
	}
	
	private EstadoLunaDTO getEstadoLuna(LocalDate date) {
		
		EstadoLunaDTO estadoLuna = new EstadoLunaDTO();
		
		estadoLuna.setDireccion(this.getLunaDireccion(date));		
		
		return estadoLuna;
	}
	
	private String getLunaDireccion(LocalDate date) {
		
		String direccion = "";
		
		ApogeosYPerigeosLunaEntity apoperiMasCercanoADate = this.apogeosYPerigeosLunaRepository.findTopByDateLessThanEqualOrderByDateDesc(date.atTime(LocalTime.MAX));
		
		if(apoperiMasCercanoADate != null) {
			
			if(apoperiMasCercanoADate.getDate().toLocalDate().isEqual(date)) {
				
				if(apoperiMasCercanoADate.isEsApogeo()) {
					direccion = "Durmiente";
				}
				else if(apoperiMasCercanoADate.isEsPerigeo()){
					direccion = "Presente";
				}
			}
			else if(apoperiMasCercanoADate.isEsApogeo()) {
				direccion = "Acercándose";
			}
			else if(apoperiMasCercanoADate.isEsPerigeo()) {
				direccion = "Alejándose";
			}	
		}		
		
		return direccion;
	}
	
	private CasaleroDTO getCasalero(Long lastEclipenoINId) {
		
		CasaleroDTO casaleroDTO = null;
		
		try {
			
			CasalerosEntity casaleroEntity = casalerosRepository.findByEclipenoId(lastEclipenoINId);
			
			if(casaleroEntity != null) {
				
				casaleroDTO = new CasaleroDTO();
				casaleroDTO.setDateO(casaleroEntity.getDate().toLocalDate());
				
				String tipo = "";
				if(casaleroEntity.getMetonoId() != null) {
					
					Optional<MetonsEntity> metonoOpt = this.metonsRepository.findById(casaleroEntity.getMetonoId());
					
					if(metonoOpt.isPresent()) {
						
						MetonsEntity metono = metonoOpt.get();
						
						tipo="Metónico";
						
						casaleroDTO.setLleno(Boolean.TRUE.equals(metono.getLleno()));
						casaleroDTO.setNuevo(Boolean.TRUE.equals(metono.getNuevo()));
						casaleroDTO.setInicial(Boolean.TRUE.equals(metono.getInicial()));
						casaleroDTO.setBicuartal(Boolean.TRUE.equals(metono.getBicuartal()));	
						casaleroDTO.setCuartal(Boolean.TRUE.equals(metono.getCuartal()));
						casaleroDTO.setTricuartal(Boolean.TRUE.equals(metono.getTricuartal()));
						casaleroDTO.setNuevo(true);
					}								
				}
				else if (casaleroEntity.getEclipseId() != null){
					
					Optional<EclipsesEntity> eclipseOpt = this.eclipsesRepository.findById(casaleroEntity.getEclipseId());
					
					if(eclipseOpt.isPresent()) {
						
						EclipsesEntity eclipse = eclipseOpt.get();
						
						tipo="Eclipelar";
						casaleroDTO.setDeSol(Boolean.TRUE.equals(eclipse.isDeSol()));
						casaleroDTO.setDeLuna(Boolean.TRUE.equals(eclipse.isDeLuna()));
					}				
				}
				
				casaleroDTO.setTipo(tipo);
							
			}	
		}
		catch(Exception e) {
			
			System.out.println("Error al obtener el casalero: " + e.getMessage());
		}
		
		return casaleroDTO;		
	}
	
	private AbsoluteEclipsesDTO getVAUAbsoluteEclipses(DateDTO dateVAU, List<EclipsesEntity> eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN, LocalDate date, MetonsEntity lastMetonIN) {
		
		AbsoluteEclipsesDTO absoluteEclipses = new AbsoluteEclipsesDTO ();		
		
		int eclipsesNoParcialesDesdeLastEclipenoIN = 0;
		int eclipsesSolaresNoParcialesDesdeLastEclipenoIN = 0;
		int eclipsesLunaresNoParcialesDesdeLastEclipenoIN = 0;
		
		int eclipsesNoParcialesDesdeLastMetonIN = 0;
		int eclipsesSolaresNoParcialesDesdeLastMetonIN = 0;		
		int eclipsesLunaresNoParcialesDesdeLastMetonIN = 0;
		
		
		
		if(!dateVAU.getEclipenoIN().isEclipenoINDay()) {
		
			
			List<EclipsesEntity> eclipsesSolaresNoParcialesDesdeLastEclipenoINList = new ArrayList<>();		
			List<EclipsesEntity> eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList = new ArrayList<>();
			
			int lunaresDesdeElUltimoMetonoIN =0;
			int solaresDesdeElUltimoMetonoIN =0;
			
			//Si estamos en el primer métono, hay que restarle 1 porque viene el propio del eclípeno
			if(dateVAU.getMetonoIN().getMetonosINSinceLastEclipenoIN() == 0) {
				solaresDesdeElUltimoMetonoIN=-1; 
			}
			
			
			for (EclipsesEntity eclipse : eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN){
				
				if(eclipse.isDeSol()) {
					
					eclipsesSolaresNoParcialesDesdeLastEclipenoINList.add(eclipse);
					
					if(eclipse.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate()) || eclipse.getDate().toLocalDate().isEqual(lastMetonIN.getDate().toLocalDate())) {
						
						solaresDesdeElUltimoMetonoIN = solaresDesdeElUltimoMetonoIN+1;					
					}
					
				}
				else if (eclipse.isDeLuna()){
					
					eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList.add(eclipse);
					
					if(eclipse.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate()) || eclipse.getDate().toLocalDate().isEqual(lastMetonIN.getDate().toLocalDate())) {
						
						lunaresDesdeElUltimoMetonoIN = lunaresDesdeElUltimoMetonoIN+1;				
					}			
				}						
			}			
			
			eclipsesSolaresNoParcialesDesdeLastEclipenoIN = eclipsesSolaresNoParcialesDesdeLastEclipenoINList.size()-1;
			eclipsesLunaresNoParcialesDesdeLastEclipenoIN = eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList.size();
			eclipsesNoParcialesDesdeLastEclipenoIN = eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN.size()-1;
			
			
			eclipsesSolaresNoParcialesDesdeLastMetonIN = solaresDesdeElUltimoMetonoIN;		
			eclipsesLunaresNoParcialesDesdeLastMetonIN = lunaresDesdeElUltimoMetonoIN;
			eclipsesNoParcialesDesdeLastMetonIN = eclipsesSolaresNoParcialesDesdeLastMetonIN + eclipsesLunaresNoParcialesDesdeLastMetonIN;
			
		}
		
		absoluteEclipses.setSolarSinceLastEclipenoIN(eclipsesSolaresNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setSolarSinceLastMetonoIN(eclipsesSolaresNoParcialesDesdeLastMetonIN);
		
		absoluteEclipses.setLunarSinceLastEclipenoIN(eclipsesLunaresNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setLunarSinceLastMetonoIN(eclipsesLunaresNoParcialesDesdeLastMetonIN);
		
		absoluteEclipses.setSinceLastEclipenoIN(eclipsesNoParcialesDesdeLastEclipenoIN);
		absoluteEclipses.setSinceLastMetonoIN(eclipsesNoParcialesDesdeLastMetonIN);
		
		return absoluteEclipses;
	}

	private EclipenoDTO getVAUEclipeno(EclipenosEntity lastEclipenoIN, LocalDate date) {
		
		EclipenoDTO eclipeno = new EclipenoDTO();
		eclipeno.setYearOfCurrentEclipenoIN(lastEclipenoIN.getYear());
		eclipeno.setEclipenoINDay(lastEclipenoIN.getDate().toLocalDate().isEqual(date));
		
		return eclipeno;
	}
	
	private MetonDTO getVAUMeton (EclipenosEntity lastEclipenoIN, List<MetonsEntity> metons, LocalDate dateO) {
		
		MetonDTO metonIN = new MetonDTO();
		
		List<MetonsEntity> metonsIN = new ArrayList<>();
		
		for(MetonsEntity meton : metons) {
			
			if(meton.getInicial() && meton.getNuevo()) {
				metonsIN.add(meton);
			}
		}
		
		metonIN.setYearOfCurrentMetonIN(metonsIN.get(0).getYear());
		metonIN.setMetonoINDay(metonsIN.get(0).getDate().toLocalDate().isEqual(dateO));
		
		int metonosDesdeElLastEclipen = (metonsIN.size()-1); // -1 porque incluye el del eclipeno
		
		// No se suma un metono hasta que pase el dia del metono, pero si es el dia de eclipeno no se resta, que se ha restado antes
		
		if(metonIN.isMetonoINDay() && !lastEclipenoIN.getDate().toLocalDate().isEqual(dateO)) {
			
			metonosDesdeElLastEclipen = metonosDesdeElLastEclipen-1;
		}
		
		metonIN.setMetonosINSinceLastEclipenoIN(metonosDesdeElLastEclipen);
		int yearOfTheMeton = metonosDesdeElLastEclipen +1;
		
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(dateO)) { //Si es el dia del eclipeno, no estamos en ningun metono
			yearOfTheMeton= yearOfTheMeton-1;
		}
		metonIN.setNumberOfMeton(yearOfTheMeton);
		
		return metonIN;
	}

	private YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN) {
		
		YearDTO vauYear = new YearDTO();
		
		boolean caeEnSolsticioDeInvierno=false;
		
		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		
		int year = 0;
		
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSolsticioDeInvierno; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.isSolsticioInvierno()) {				
		
				if(soe.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
						
					caeEnSolsticioDeInvierno=true;
				}
				else if (soe.getDate().toLocalDate().isBefore(dateO.toLocalDate()) && soe.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate())){
					
					year=year+1;
				}
			}
			
		}
		
		
		vauYear.setEsSolsticioDeInvierno(caeEnSolsticioDeInvierno);	
		vauYear.setSolsticiosDeInviernoSinceLastMetonIN(year);	
		
		int numberOfYear = year +1;
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
			
			numberOfYear = numberOfYear-1;
		}
		vauYear.setNumberOfYear(numberOfYear);
	
		return vauYear;
		
	}
	
	
	
	private MonthDTO getVAUMonth (LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente) {
		
		MonthDTO month = new MonthDTO();
		
		List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente = new ArrayList<>();
		
		for(LunasEntity luna : lunasDesdeElAnyoAnteriorHastaElSiguiente) {
			if(luna.isNueva()) {
				lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.add(luna);
			}
		}
		
		// Lo primero es coger los solsticios y equinoccios mas cercanos a la fecha a consultar
		SolsticiosYEquinocciosEntity lastSOE = null;
		SolsticiosYEquinocciosEntity nextSOE = null;
		
		long diasMinimosDeDiferenciaConLastSOE = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaConNextSOE = Long.MAX_VALUE;
		
		// Si cae en SOE, ya tenemos el mes
		boolean caeEnSOE = false;
		
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSOE; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
				caeEnSOE = true;
				lastSOE = soe;
				nextSOE = soe;
			}
			else if(soe.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
				
				long diasDeDiferenciaEntreLastSOEYFecha = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), dateO.toLocalDate());
				
				if(diasDeDiferenciaEntreLastSOEYFecha < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYFecha;
					lastSOE = soe;
				}
				
			}
			else if(soe.getDate().toLocalDate().isAfter(dateO.toLocalDate())) {
				
				long diasDeDiferenciaEntreNextSOEYFecha = ChronoUnit.DAYS.between(dateO.toLocalDate(), soe.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreNextSOEYFecha < diasMinimosDeDiferenciaConNextSOE) {
					diasMinimosDeDiferenciaConNextSOE = diasDeDiferenciaEntreNextSOEYFecha;
					nextSOE = soe;
				}
			}			
		}
		
		if(lastSOE != null && nextSOE != null) {
			// Luego, coger las lunas nuevas que se encuentran entre ambos lastSOE y nextSOE
			// Si cae en Luna nueva, ya tenemos el mes
			
			
			List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE = new ArrayList<>();
			boolean caeEnLunaNueva = false;
			for(int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.size(); i++) {
				
				LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.get(i);
	
				if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate() )) {
						
					lunasNuevasEntreLastSOEYNextSOE.add(luna);	
					caeEnLunaNueva = true;	
						
				}
				else if(luna.getDate().toLocalDate().isAfter(lastSOE.getDate().toLocalDate()) || luna.getDate().toLocalDate().isEqual(lastSOE.getDate().toLocalDate())) {
							
					if(luna.getDate().toLocalDate().isBefore(nextSOE.getDate().toLocalDate())) {							
							
						lunasNuevasEntreLastSOEYNextSOE.add(luna);					
					}	
				}
				
				
			}
			
			MonthsEntity vauMonth = new MonthsEntity();
			// Si cae en soe, pertenece al mes hibrido de ese soe.
			// A no ser que sea luna nueva, en ese caso seria el mes siguiente
			if(caeEnSOE) {

				if(caeEnLunaNueva) {
					
					// Basicamente si hay un metono (da igual el tipo)
					MonthDTO monthIfLN = getVAUMonth(dateO.plusDays(1), soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente);
					vauMonth.setName(monthIfLN.getName());
					
				}
				else {
					vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);
				}
				

			}
			else{
					
				// Si no cae en SOE, hay que calcular cuantas lunas nuevas han pasado desde el lastSOE hasta la fecha a consultar
				// Tambien obtenemos la luna nueva anterior al nextSOE y la luna nueva posterior al lastSOE
				int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
				
				long diasMinimosDeDiferenciaLunaNuevaConNextSOE = Long.MAX_VALUE;
				LunasEntity lastLNBeforeNextSOE = null;
				
				long diasMinimosDeDiferenciaLunaNuevaConLastSOE = Long.MAX_VALUE;
				LunasEntity firstLNAfterLastSOE = null;
					
				for(LunasEntity luna : lunasNuevasEntreLastSOEYNextSOE) {
						
					long diasDeDiferenciaEntreNextSOEYLN = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), nextSOE.getDate().toLocalDate());
					long diasDeDiferenciaEntreLastSOEYLN = ChronoUnit.DAYS.between(lastSOE.getDate().toLocalDate(), luna.getDate().toLocalDate());
						
					if(diasDeDiferenciaEntreNextSOEYLN < diasMinimosDeDiferenciaLunaNuevaConNextSOE) {
							
						lastLNBeforeNextSOE=luna;
						diasMinimosDeDiferenciaLunaNuevaConNextSOE = diasDeDiferenciaEntreNextSOEYLN;
							
					}
					
					if(diasDeDiferenciaEntreLastSOEYLN < diasMinimosDeDiferenciaLunaNuevaConLastSOE) {
						
						firstLNAfterLastSOE=luna;
						diasMinimosDeDiferenciaLunaNuevaConLastSOE = diasDeDiferenciaEntreLastSOEYLN;
							
					}
						
					if(dateO.toLocalDate().isAfter(luna.getDate().toLocalDate())) {
							
						lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO+1;						
					}
				}
					
				
					
				if(lastLNBeforeNextSOE != null || firstLNAfterLastSOE != null) {
					
					// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
					if(dateO.toLocalDate().isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && dateO.toLocalDate().isBefore(nextSOE.getDate().toLocalDate())) {
		
						vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(nextSOE.getStartingSeason(), 0, false);

					}
					// Si la fecha a consultar esta entre el lastSOE y la primera luna, pertenece al mes hibrido de ese soe.
					// Pero si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
					// A no ser que sea luna nueva, que en ese caso será Prierno
					else if (dateO.toLocalDate().isBefore(firstLNAfterLastSOE.getDate().toLocalDate()) && dateO.toLocalDate().isAfter(lastSOE.getDate().toLocalDate())) {						

						if(lastSOE.isSolsticioInvierno()) {						
	
							if(caeEnLunaNueva) {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO+1, false);
							}
							else {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);
							}
						}
						else {
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);
						}
								
					
					}
					else {											
						// Situacion normal: sabemos cuantas lunas han pasado, y sabemos el soe que es
						// Pero si es luna nueva, ha de indicarse el mes siguiente, es decir, coger el mes de un día mas
							
						if (caeEnLunaNueva) {
							
							MonthDTO monthIfLN = getVAUMonth(dateO.plusDays(1), soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente);
							vauMonth.setName(monthIfLN.getName());
						}
						else {
							
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, false);
						}				
					}															
				}
				else {
					System.out.println("Error, no hay lastLNBeforeNextSOE o firstLNAfterLastSOE.");
				}
									
			}			

			month.setNewMoon(caeEnLunaNueva);	
			month.setName(vauMonth.getName());
				
		}
		else {
			
			System.out.println("Error, no se han encontrado nextSOE y/o lastSOE.");
		}
		

		return month;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		String weekVauString = null;
		String dayVauString = null;
		
		// Lo primero es seleccionar la luna nueva mas reciente, si cae en luna llena, no hay dias ni semanas
		
		LunasEntity lastLN = new LunasEntity();
		long diasDesdeLaLunaNueva = Long.MAX_VALUE;
		boolean caeEnLunaNueva = false;
		for (int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.size() && !caeEnLunaNueva; i++) {
			
			LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.get(i);
			

			if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
					
				caeEnLunaNueva = true;
				diasDesdeLaLunaNueva=0;
			}
			else if (luna.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
					
				long diasDeDiferenciaEntreLNYDateO = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), dateO.toLocalDate());
					
				if(diasDeDiferenciaEntreLNYDateO < diasDesdeLaLunaNueva) {
						
					lastLN=luna;
					diasDesdeLaLunaNueva = diasDeDiferenciaEntreLNYDateO;						
				}
			}
		}
			
		if(lastLN != null) {
			
			// Con la luna llena más reciente y con los días que los separan, ya lo tenemos
			
			if (diasDesdeLaLunaNueva <= 7) {
				
				if(!caeEnLunaNueva) {							
							
					weekVauString = this.weeksRepository.findByWeekOfMonth("1").getName();
				}
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva).getName();
				
			} else if (diasDesdeLaLunaNueva <= 14) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("2").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-7).getName();

			} else if (diasDesdeLaLunaNueva <= 21) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("3").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-14).getName();

			} else if (diasDesdeLaLunaNueva <= 28) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("4").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-21).getName();
			}
			else {
				weekVauString = this.weeksRepository.findByWeekOfMonth("5").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-21).getName();
			}
		}
		
		vauWeekAndDay.setWeek(weekVauString);
		vauWeekAndDay.setDay(dayVauString);
		
		return vauWeekAndDay;
	}
	

	
	private String getNotableEventName(LunasEntity luna, SolsticiosYEquinocciosEntity soe, MetonsEntity meton, EclipsesEntity eclipse, EclipenosEntity eclipeno) {
		
		String evento = "";
		
				
		if(luna != null || soe!= null || meton!= null || eclipse!= null || eclipeno!= null) {
					
			evento = "";
					
			if(eclipeno != null) {
						
				if (Boolean.TRUE.equals(eclipeno.getInicial())) {
							
					evento = evento + "Eclípeno inicial ";
				}
				else if(Boolean.TRUE.equals(eclipeno.getCuartal())) {
							
					evento = evento + "Eclípeno cuartal ";
				}
				else if (Boolean.TRUE.equals(eclipeno.getBicuartal())) {
							
					evento = evento + "Eclípeno bicuartal ";
				}
				else if (Boolean.TRUE.equals(eclipeno.getTricuartal())) {
							
					evento = evento + "Eclípeno tricuartal ";
				}
						
				if(Boolean.TRUE.equals(eclipeno.getNuevo())) {
							
					evento = evento + "nuevo";
				}
				else if(Boolean.TRUE.equals(eclipeno.getLleno())) {
							
					evento = evento + "lleno";
				}
				
				if(Boolean.TRUE.equals(eclipeno.getHibrido())) {
					if(Boolean.TRUE.equals(eclipeno.getSelecto())) {
						evento = evento + " selecto";
					}
					else if(Boolean.TRUE.equals(eclipeno.getTransicionado())) {
						evento = evento + " transicionado";
					}
				}
			}
			else if (meton != null) {
						
				if (Boolean.TRUE.equals(meton.getInicial())) {
							
					evento = evento + "Métono inicial ";
				}
				else if(Boolean.TRUE.equals(meton.getCuartal())) {
							
					evento = evento + "Métono cuartal ";
				}
				else if (Boolean.TRUE.equals(meton.getBicuartal())) {
							
					evento = evento + "Métono bicuartal ";
				}
				else if (Boolean.TRUE.equals(meton.getTricuartal())) {
							
					evento = evento + "Métono tricuartal ";
				}
						
				if(Boolean.TRUE.equals(meton.getNuevo())) {
							
					evento = evento + "nuevo";
				}
				else if(Boolean.TRUE.equals(meton.getLleno())) {
							
					evento = evento + "lleno";
				}
				
				if(Boolean.TRUE.equals(meton.getHibrido())) {
					if(Boolean.TRUE.equals(meton.getSelecto())) {
						evento = evento + " selecto";
					}
					else if (Boolean.TRUE.equals(meton.getTransicionado())) {
						evento = evento + " transicionado";
					}
				}
						
			}
			else if(soe != null) {
						
				if(Boolean.TRUE.equals(soe.isSolsticioInvierno())) {
							
					evento = evento + "Solsticio de invierno";
				}
				else if(Boolean.TRUE.equals(soe.isEquinoccioPrimavera())) {
							
					evento = evento + "Equinoccio de primavera";
				}
				else if(Boolean.TRUE.equals(soe.isSolsticioVerano())) {
							
					evento = evento + "Solsticio de verano";
				}
				else if (Boolean.TRUE.equals(soe.isEquinoccioOtonyo())) {
							
					evento = evento + "Equinoccio de otoño";
				}
				
			}
			else if (eclipse != null) {				
										
				String tipo = "";
				
				if(Boolean.TRUE.equals(eclipse.isDeLuna())) {
							
					tipo =  "Eclipse de luna";
				}
				else if (Boolean.TRUE.equals(eclipse.isDeSol())) {
							
					tipo = "Eclipse de sol";
				}
						
				String fase = "";
				
				if(Boolean.TRUE.equals(eclipse.isEsAnular())) {
					fase = " anular";
				}
				else if (Boolean.TRUE.equals(eclipse.isEsHibrido())) {
					fase = " híbrido";
				}
				else if (Boolean.TRUE.equals(eclipse.isEsParcial())) {
					fase = " parcial";
				}
				else if (Boolean.TRUE.equals(eclipse.isEsPenumbral())) {
					fase = " penumbral";
				}
				else if (Boolean.TRUE.equals(eclipse.isEsTotal())) {
					fase = " total";
				}		
						
				evento = evento + tipo + fase;
			}			
			else if (luna != null) {

				if (Boolean.TRUE.equals(luna.isNueva())) {
					evento = evento + "Luna nueva";
				} 
				else if (Boolean.TRUE.equals(luna.isCuartoCreciente())) {
					evento = evento + "Luna cuarto creciente";
				} 
				else if (Boolean.TRUE.equals(luna.isLlena())) {
					 evento = evento + "Luna llena";
				} 
				else if (Boolean.TRUE.equals(luna.isCuartoMenguante())) {
					  evento = evento + "Luna cuarto menguante";
				}
				
				if(luna.isHibrida()) {
					if(luna.isSelecta()) {
						evento = evento + " selecta";
					}
					else if(luna.isTransicionada()) {
						evento = evento + " transicionada";
					}
				}
			}
		}

		return evento;
	}
	
	private EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date) {
		
		EclipenosEntity lastEclipenoIN = null;
		
		long diasMinimosDeDiferenciaEntreEclipenoYDate =Long.MAX_VALUE;		
		for(EclipenosEntity eclipeno : allEclipenos) {
					
			if(!eclipeno.getDate().toLocalDate().isAfter(date) && eclipeno.getInicial() && eclipeno.getNuevo() && (eclipeno.isEsAnular() || eclipeno.isEsTotal())) {	
				
				long diasDeDiferenciaEntreEclipenoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreEclipenoYDate < diasMinimosDeDiferenciaEntreEclipenoYDate) {
					lastEclipenoIN = new EclipenosEntity();
					diasMinimosDeDiferenciaEntreEclipenoYDate = diasDeDiferenciaEntreEclipenoYDate;
					lastEclipenoIN = eclipeno;
				}
			}
		}
		
		return lastEclipenoIN;
	}
	
	private MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date) {
		
		MetonsEntity lastMetonINForDate = new MetonsEntity();
		
		long diasMinimosDeDiferenciaEntreMetonoYDate =Long.MAX_VALUE;
		
		for(MetonsEntity metono : allMetons) {									
			
			if(!metono.getDate().toLocalDate().isAfter(date) && metono.getInicial() && metono.getNuevo()) {
				
				long diasDeDiferenciaEntreMetonoYDate = ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreMetonoYDate < diasMinimosDeDiferenciaEntreMetonoYDate) {
					lastMetonINForDate = new MetonsEntity();
					diasMinimosDeDiferenciaEntreMetonoYDate = diasDeDiferenciaEntreMetonoYDate;
					lastMetonINForDate = metono;
				}
			}
		}
		return lastMetonINForDate;
	}
	
}







