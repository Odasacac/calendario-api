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
import CCASolutions.Calendario.DTOs.DateVAUDTO;
import CCASolutions.Calendario.DTOs.EclipenoDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Responses.FromDateVAUToDateOResponse;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.DaysService;

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
	private DaysService daysService;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private CasalerosRepository casalerosRepository;
	
	// METODOS PUBLICOS 
	
	
	
	public FromDateVAUToDateOResponse getDateOFromDateVAU(DateVAUDTO dateVAU) {

		FromDateVAUToDateOResponse fromDateVAUToDateOResponse = new FromDateVAUToDateOResponse();
		LocalDate dateO = null;
		String response = "";
		
		// Lo primero es obtener el año
		// Para ello, sabiendo el año del eclipeno se obtiene ese eclipeno y el siguiente
		
		List <EclipenosEntity> eclipenos = this.eclipenosRepository.findTop2ByYearGreaterThanEqualAndNuevoIsTrueAndInicialIsTrueOrderByYearAsc(dateVAU.getEclipenoIN());
		
		// Teniendo en cuenta estos eclipenos, obtenemos los metonos que hay entre ellos, includos los años limite
		
		List <MetonsEntity> metonos= new ArrayList<>();
		int metonosYearFrom = eclipenos.get(0).getYear();
		if(eclipenos.size() > 1) {
			
			metonos = this.metonsRepository.findByYearBetweenAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(metonosYearFrom, eclipenos.get(1).getYear());
			
		}
		else {
			
			metonos = this.metonsRepository.findByYearGreaterThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(metonosYearFrom);
		}
				
		
		// Comprobamos que no haya mas metonos en la peticion que los reales
		
		if(dateVAU.getNumberOfMetonoIN() < metonos.size()) {
			
			// Y ahora seleccionamos el metono correspondiente			
			MetonsEntity metono = metonos.get(dateVAU.getNumberOfMetonoIN()-1);
			
			int anyoDelSoe = metono.getYear() + dateVAU.getNumberOfYear();
			
			// Ahora cogemos el mes de la BBDD
			
			MonthsEntity mes = this.monthsRepository.findByName(dateVAU.getMonth());
			
			if(mes.isLiminal() || (mes.getSeason() == 1 && mes.getMonthOfSeason() != 0)) {
				
				anyoDelSoe=anyoDelSoe-1;	
			}
			
			// Y con eso tenemos el soe correspondiente
			SolsticiosYEquinocciosEntity soe = this.solsticiosYEquinocciosRepository.findByYearAndStartingSeason(anyoDelSoe, mes.getSeason());
			
			if(soe != null) {
				
				LunasEntity lunaCorrespondiente = new LunasEntity();

				if (mes.getHibrid()) {
					
					// Si es hibrido, hay que coger la luna nueva anterior al soe y contar desde ahi
					lunaCorrespondiente = lunasRepository.findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(soe.getDate());
									
				}
				else {		
					
					// Ya con el SOE, selecciona la luna nueva a partir de la cual se cuentan los dias
					LocalDateTime fechaParaGetLunas = soe.getDate().toLocalDate().atStartOfDay();
					List<LunasEntity> lunasAPartirDelSoe = this.lunasRepository.findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(fechaParaGetLunas);			
					lunaCorrespondiente = lunasAPartirDelSoe.get(mes.getMonthOfSeason()-1);
				}
						
				dateO = lunaCorrespondiente.getDate().toLocalDate().plusDays(this.daysService.getDiasASumarALaLunaNueva(dateVAU));		
			}
			else {
				
				response ="Error, no existe un SOE correspondiente al año: " + anyoDelSoe;
			}
			
			fromDateVAUToDateOResponse.setDateO(dateO);
			fromDateVAUToDateOResponse.setComentarios(response);
			
		}
		else {
			
			fromDateVAUToDateOResponse.setComentarios("El número de métonos indicados (" + dateVAU.getNumberOfMetonoIN() + ") se excede de los que existen en el eclípeno de " + dateVAU.getEclipenoIN() + ", que son " + metonos.size() + ".");
		}
		
		return fromDateVAUToDateOResponse;
	
	}

	
	public DateDTO getDateVAUFromDateO (LocalDate date) {
		
		DateDTO dateVAU = null;
		LocalDateTime dateO = date.atTime(LocalTime.MAX);	
		
	
		// Lo primero es la fecha del último eclipeno que haya ocurrido hasta la fecha a consultar
		EclipenosEntity lastEclipenoIN = this.eclipenosRepository.findTopByDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrueOrderByDateDesc(dateO, dateO);
		
		if(lastEclipenoIN != null) {
			
			// Una vez tenemos este eclipeno, hay que contar cuantos metonos han ocurrido
			List<MetonsEntity> metonsIN = this.metonsRepository.findByDateBetweenAndInicialIsTrueAndNuevoIsTrueOrderByDateDesc(lastEclipenoIN.getDate(), dateO);
			
			
			if(metonsIN != null) {

				// Con esto, lo primero es obtener todos los solsticios y equinoccios ocurridos entre el último métono y la fecha a consultar mas un año			
				List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas = this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(metonsIN.get(0).getDate().minusYears(1), dateO.plusYears(1));
				
				// Todas las lunas, desde un año antes hasta un año despues de la fecha a consultar			
				List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente = this.lunasRepository.findByDateBetweenAndNuevaTrue(dateO.minusYears(1), dateO.plusYears(1));
				
				// Y todos los eclipses totales desde el lastEclipenoIN
				List<EclipsesEntity> eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN = this.eclipsesRepository.findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(lastEclipenoIN.getDate().toLocalDate().atStartOfDay(), dateO);
			
				
				if(soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.isEmpty() || lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente.isEmpty()) {
					
					System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas.");
				}
				else {
					
					dateVAU = new DateDTO();
					
					// Lo primero es obtener el añoVAU				
					dateVAU.setYear(this.getVAUYear(lastEclipenoIN, dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, metonsIN.get(0)));
					
					// Luego el mesVau
					dateVAU.setMonth(this.getVAUMonth(dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente));
					
					// Despues, la semana y el dia				
					VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(dateO, lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente);
					dateVAU.setWeek(vauWeekAndDay.getWeek());
					dateVAU.setDay(vauWeekAndDay.getDay());
					
					// Indicamos el metono
					dateVAU.setMetonoIN(getVAUMeton(lastEclipenoIN, metonsIN, date));
					
					// Indicamos el eclipeno
					dateVAU.setEclipenoIN(this.getVAUEclipeno(lastEclipenoIN, date));
					
					// Indicamos los eclipses totales/anulares ocurridos
					dateVAU.setAbsoluteEclipses(this.getVAUAbsoluteEclipses(dateVAU, eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN, date, metonsIN.get(0)));
					
					// Incluimos Casalero si lo hay
					dateVAU.setCasalero(this.getCasalero(lastEclipenoIN.getId()));
					
					// Y finalmente, indicamos si hay algun tipo de evento reseñable
					dateVAU.setNotableEvent(this.getNotableEvent(date));
				}
				
			}
			else {
				System.out.println("Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
			}
		}
		else {
			System.out.println("Error al obtener dateVAU: no se ha encontrado un eclípeno anterior a la fecha proporcionada.");
		}
		
		
		return dateVAU;
		
	}
	

	
	// ========================= METODOS PRIVADOS
	
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
						casaleroDTO.setBicuartal(Boolean.TRUE.equals(metono.getInicial()));	
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
	
	private MetonDTO getVAUMeton (EclipenosEntity lastEclipenoIN, List<MetonsEntity> metonsIN, LocalDate dateO) {
		
		MetonDTO meton = new MetonDTO();
		meton.setYearOfCurrentMetonIN(metonsIN.get(0).getYear());
		meton.setMetonoINDay(metonsIN.get(0).getDate().toLocalDate().isEqual(dateO));
		
		int metonosDesdeElLastEclipen = (metonsIN.size()-1); // -1 porque incluye el del eclipeno
		
		// No se suma un metono hasta que pase el dia del metono, pero si es el dia de eclipeno no se resta, que se ha restado antes
		
		if(meton.isMetonoINDay() && !lastEclipenoIN.getDate().toLocalDate().isEqual(dateO)) {
			
			metonosDesdeElLastEclipen = metonosDesdeElLastEclipen-1;
		}
		
		meton.setMetonosINSinceLastEclipenoIN(metonosDesdeElLastEclipen);
		int yearOfTheMeton = metonosDesdeElLastEclipen +1;
		
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(dateO)) { //Si es el dia del eclipeno, no estamos en ningun metono
			yearOfTheMeton= yearOfTheMeton-1;
		}
		meton.setNumberOfMeton(yearOfTheMeton);
		
		return meton;
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
	
	
	
	private MonthDTO getVAUMonth (LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente) {
		
		MonthDTO month = new MonthDTO();
		
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
				
	
				if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
						
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
	
	
	private String getNotableEvent(LocalDate date) {
		
		String evento = null;
		
		// Los eventos reseñables son lunas, soes, metonos, eclipses y eclipenos
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
		
		LunasEntity luna = this.lunasRepository.findByDateBetween(startOfDay, endOfDay);		
		SolsticiosYEquinocciosEntity soe = this.solsticiosYEquinocciosRepository.findByDateBetween(startOfDay, endOfDay);
		MetonsEntity meton = this.metonsRepository.findByDateBetween(startOfDay, endOfDay);
		EclipsesEntity eclipse = this.eclipsesRepository.findByDateBetween(startOfDay, endOfDay);
		EclipenosEntity eclipeno = this.eclipenosRepository.findByDateBetween(startOfDay, endOfDay);
		
		if(luna != null || soe!= null || meton!= null || eclipse!= null || eclipeno!= null) {
			
			evento = "";
			
			if(eclipeno != null) {
				
				if (eclipeno.getInicial()) {
					
					evento = evento + "Eclípeno inicial ";
				}
				else if(eclipeno.getCuartal()) {
					
					evento = evento + "Eclípeno cuartal ";
				}
				else if (eclipeno.getBicuartal()) {
					
					evento = evento + "Eclípeno bicuartal ";
				}
				else if (eclipeno.getTricuartal()) {
					
					evento = evento + "Eclípeno tricuartal ";
				}
				
				if(eclipeno.getNuevo()) {
					
					evento = evento + "nuevo";
				}
				else if(eclipeno.getLleno()) {
					
					evento = evento + "lleno";
				}
			}
			else if (meton != null) {
				
				if (meton.getInicial()) {
					
					evento = evento + "Métono inicial ";
				}
				else if(meton.getCuartal()) {
					
					evento = evento + "Métono cuartal ";
				}
				else if (meton.getBicuartal()) {
					
					evento = evento + "Métono bicuartal ";
				}
				else if (meton.getTricuartal()) {
					
					evento = evento + "Métono tricuartal ";
				}
				
				if(meton.getNuevo()) {
					
					evento = evento + "nuevo";
				}
				else if(meton.getLleno()) {
					
					evento = evento + "lleno";
				}
				
			}
			else if(soe != null) {
				
				if(soe.isSolsticioInvierno()) {
					
					evento = evento + "Solsticio de invierno";
				}
				else if(soe.isEquinoccioPrimavera()) {
					
					evento = evento + "Equinoccio de primavera";
				}
				else if(soe.isSolsticioVerano()) {
					
					evento = evento + "Solsticio de verano";
				}
				else if (soe.isEquinoccioOtonyo()) {
					
					evento = evento + "Equinoccio de otoño";
				}
			}
			else if (eclipse != null) {				
								
				String tipo = "";
				if(eclipse.isDeLuna()) {
					
					tipo =  "Eclipse de luna";
				}
				else if (eclipse.isDeSol()) {
					
					tipo = "Eclipse de sol";
				}
				
				String fase = "";
				if(eclipse.isEsAnular()) {
					fase = " anular";
				}
				else if (eclipse.isEsHibrido()) {
					fase = " híbrido";
				}
				else if (eclipse.isEsParcial()) {
					fase = " parcial";
				}
				else if (eclipse.isEsPenumbral()) {
					fase = " penumbral";
				}
				else if (eclipse.isEsTotal()) {
					fase = " total";
				}		
				
				evento = evento + tipo + fase;
			}			
			else if (luna != null) {

			    if (luna.isNueva()) {
			        evento = evento + "Luna nueva";
			    } 
			    else if (luna.isCuartoCreciente()) {
			        evento = evento + "Luna cuarto creciente";
			    } 
			    else if (luna.isLlena()) {
			        evento = evento + "Luna llena";
			    } 
			    else if (luna.isCuartoMenguante()) {
			        evento = evento + "Luna cuarto menguante";
			    }
			}
		}
		
		return evento;
	}



	
}







