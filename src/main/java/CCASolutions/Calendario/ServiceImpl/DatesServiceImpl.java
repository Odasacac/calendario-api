package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.EclipenoDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
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
	
	// METODOS PUBLICOS 

	public DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU) {

		DateDTOFromDB dateVAUDTOFromDB = new DateDTOFromDB();
		
		if(dateVAU.getEclipenoIN().getYearOfCurrentEclipenoIN() >= 0) {
			
			EclipenosEntity eclipeno = this.eclipenosRepository.findTopByYearAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrYearAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrue(dateVAU.getEclipenoIN().getYearOfCurrentEclipenoIN(), dateVAU.getEclipenoIN().getYearOfCurrentEclipenoIN());
			
			if(eclipeno != null) {
				
				dateVAUDTOFromDB.setEclipeno(eclipeno);
				dateVAUDTOFromDB.setEsEclipeno(dateVAU.getEclipenoIN().isEclipenoINDay()); //Esto deberia calcularse, no obtenerse del DTO
				
				if(dateVAU.getMetonoIN().getMetonosINSinceLastEclipenoIN() >= 0) {
					
					List<MetonsEntity> metons = this.metonsRepository.findByYearGreaterThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getYear());			
													
					if(metons != null) {						
	
						int numeroDeMetono = Integer.valueOf(dateVAU.getMetonoIN().getMetonosINSinceLastEclipenoIN());
						if(dateVAU.getMetonoIN().isMetonoINDay() && !dateVAU.getEclipenoIN().isEclipenoINDay()) { //Esto deberia calcularse, no obtenerse del DTO
							numeroDeMetono = numeroDeMetono+1;
						}

						MetonsEntity meton = metons.get(numeroDeMetono);
						
						dateVAUDTOFromDB.setMeton(meton);
						dateVAUDTOFromDB.setEsMetono(dateVAU.getMetonoIN().isMetonoINDay()); //Esto deberia calcularse, no obtenerse del DTO
						
						MetonsEntity nextMeton = this.metonsRepository.findFirstByYearGreaterThanAndInicialIsTrueAndNuevoIsTrueOrderByYearAsc(meton.getYear());
						
						if(nextMeton != null && (nextMeton.getYear() - meton.getYear() >= dateVAU.getYear().getSolsticiosDeInviernoSinceLastMetonIN())) {
							
							dateVAUDTOFromDB.setYear(dateVAU.getYear().getSolsticiosDeInviernoSinceLastMetonIN());
							MonthsEntity vauMonth = this.monthsRepository.findByName(dateVAU.getMonth().getName()); 
							
							if(vauMonth != null) {
								
								dateVAUDTOFromDB.setMonth(vauMonth);
								
								WeeksEntity vauWeek = this.weeksRepository.findByName(dateVAU.getWeek());
								
								if(vauWeek != null) {
									
									if(vauWeek.getWeekOfMonth() != 5) {
										
										dateVAUDTOFromDB.setWeek(vauWeek);
										DaysEntity vauDay = this.daysRepository.findByName(dateVAU.getDay());
										
										if(vauDay != null) {
											
											dateVAUDTOFromDB.setDay(vauDay);							
											dateVAUDTOFromDB.setValid(true);									
										}
										else {
											
											dateVAUDTOFromDB.setComentarios("No se ha encontrado el día " + dateVAU.getDay() + " en la base de datos.");
										}
									}
									else {
										dateVAUDTOFromDB.setComentarios("La búsqueda por días liminales no está implementada.");
									}									
								}
								else {
									
									dateVAUDTOFromDB.setComentarios("No se ha encontrado la semana " + dateVAU.getWeek() + " en la base de datos.");
								}
							}
							else {
								
								dateVAUDTOFromDB.setComentarios("No se ha encontrado el mes " + dateVAU.getMonth().getName() + " en la base de datos.");
							}	
						}						
						else {
							
							dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getYear().getSolsticiosDeInviernoSinceLastMetonIN() + " está fuera del rango para este métono.");
						}						
					}
					else {
						
						dateVAUDTOFromDB.setComentarios("No se tienen registros de métonos a partir del año " + Integer.valueOf(dateVAU.getEclipenoIN() + "."));
					}
				}
				else {
					
					dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getMetonoIN().getYearOfCurrentMetonIN() + " para un metono no es válido.");
				}
			}
			else {
				
				dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getEclipenoIN() + " para un eclipeno no es válido.");
			}
			
			
		}
		else {
			dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getEclipenoIN() + " para un eclipeno no es válido.");
		}
	
		
				
		return dateVAUDTOFromDB;
	}


	
	
	
	public FromDateVAUToDateOResponse getDateOFromDateVAU(DateDTOFromDB dateVAU) {

		FromDateVAUToDateOResponse fromDateVAUToDateOResponse = new FromDateVAUToDateOResponse();
		LocalDate dateO = null;
		String response = "";

		// Lo primero es obtener el soe correspondiente a ese mes
		// Para ello necesitamos el año y el monthOfSeason, que basicamente es el numero de LN que han pasado		
		
		int anyoDelSoe = dateVAU.getMeton().getYear() + dateVAU.getYear()+1;
		
		if(dateVAU.getMonth().isLiminal() || (dateVAU.getMonth().getSeason() == 1 && dateVAU.getMonth().getMonthOfSeason() != 0) || dateVAU.isEsMetono() || dateVAU.isEsEclipeno()) {
			
			anyoDelSoe=anyoDelSoe-1;	
		}
		
		SolsticiosYEquinocciosEntity soe = this.solsticiosYEquinocciosRepository.findByYearAndStartingSeason(anyoDelSoe, dateVAU.getMonth().getSeason());
		
		if(soe != null) {
			
			LunasEntity lunaCorrespondiente = new LunasEntity();

			if(dateVAU.isEsMetono()) {
				
				// Si ha sido metono, hay que coger la luna nueva del metono
				LunasEntity lunaDelMetono = this.lunasRepository.findByDateBetweenAndNuevaTrue(soe.getDate().minusDays(1), soe.getDate().plusDays(1)).get(0);
				
				
				if(lunaDelMetono.getDate().toLocalDate().isAfter(soe.getDate().toLocalDate())) {
					//Si la luna del metono es despues del solsticio, la luna correspondiente es la de Oterno
					lunaCorrespondiente = lunasRepository.findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(soe.getDate());
				}
				else {
					// Si la luna del metono es antes del solsticio, es esa luna
					lunaCorrespondiente = lunaDelMetono;
				}
				
				

			}
			else if (dateVAU.getMonth().getHibrid()) {
				
				// Si es hibrido, hay que coger la luna nueva anterior al soe y contar desde ahi
				lunaCorrespondiente = lunasRepository.findTopByDateLessThanAndNuevaIsTrueOrderByDateDesc(soe.getDate());
								
			}
			else {		
				
				// Ya con el SOE, selecciona la luna nueva a partir de la cual se cuentan los dias
				LocalDateTime fechaParaGetLunas = soe.getDate().toLocalDate().atStartOfDay();
				List<LunasEntity> lunasAPartirDelSoe = this.lunasRepository.findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(fechaParaGetLunas);			
				lunaCorrespondiente = lunasAPartirDelSoe.get(dateVAU.getMonth().getMonthOfSeason()-1);
			}
					
			dateO = lunaCorrespondiente.getDate().toLocalDate().plusDays(this.daysService.getDiasASumarALaLunaNueva(dateVAU));		
		}
		else {
			
			response ="Error, no existe un SOE correspondiente al año: " + anyoDelSoe;
		}
		
		fromDateVAUToDateOResponse.setDateO(dateO);
		fromDateVAUToDateOResponse.setComentarios(response);
		
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
					dateVAU.setYear(this.getVAUYear(dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, metonsIN.get(0)));
					
					// Luego el mesVau
					dateVAU.setMonth(this.getVAUMonth(dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente));
					
					// Despues, la semana y el dia				
					VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(dateO, lunasNuevasDesdeElAnyoAnteriorHastaElAnyoSiguiente);
					dateVAU.setWeek(vauWeekAndDay.getWeek());
					dateVAU.setDay(vauWeekAndDay.getDay());
					
					// Indicamos el metono
					dateVAU.setMetonoIN(getVAUMeton(metonsIN, date));
					
					// Indicamos el eclipeno
					dateVAU.setEclipenoIN(this.getVAUEclipeno(lastEclipenoIN, date));
					
					// Indicamos los eclipses totales/anulares ocurridos
					dateVAU.setAbsoluteEclipses(this.getVAUAbsoluteEclipses(dateVAU, eclipsesNoParcialesNiPenumbralesDesdeLastEclipenoIN, date, metonsIN.get(0)));
					
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
	
	private MetonDTO getVAUMeton (List<MetonsEntity> metonsIN, LocalDate dateO) {
		
		MetonDTO meton = new MetonDTO();
		meton.setYearOfCurrentMetonIN(metonsIN.get(0).getYear());
		meton.setMetonoINDay(metonsIN.get(0).getDate().toLocalDate().isEqual(dateO));
		
		int metonosDesdeElLastEclipen = (metonsIN.size()-1); // -1 porque incluye el del eclipeno
		
		// Si es el dia del metono, pero no el día del eclipeno, hay que restarle 1 porque no se cuenta el metono hasta que se ha pasado
		if(meton.isMetonoINDay() && metonsIN.size()>1) {
			metonosDesdeElLastEclipen = metonosDesdeElLastEclipen-1;
		}
		
		meton.setMetonosINSinceLastEclipenoIN(metonosDesdeElLastEclipen);
		
		return meton;
	}

	private YearDTO getVAUYear(LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN) {
		
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
			if(caeEnSOE) {

				vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), 0, false);

			}
			else {
					
				// Si no, hay que calcular cuantas lunas nuevas han pasado desde el lastSOE hasta la fecha a consultar
					
				int lunasNuevasPasadasDesdeLastSOEHastaDateO = 0;
				long diasMinimosDeDiferenciaLunaNuevaConNextSOE = Long.MAX_VALUE;
				LunasEntity lastLNBeforeNextSOE = null;
					
				for(LunasEntity luna : lunasNuevasEntreLastSOEYNextSOE) {
						
					long diasDeDiferenciaEntreNextSOEYLN = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), nextSOE.getDate().toLocalDate());
						
					if(diasDeDiferenciaEntreNextSOEYLN < diasMinimosDeDiferenciaLunaNuevaConNextSOE) {
							
						lastLNBeforeNextSOE=luna;
						diasMinimosDeDiferenciaLunaNuevaConNextSOE = diasDeDiferenciaEntreNextSOEYLN;
							
					}
						
					if(dateO.toLocalDate().isAfter(luna.getDate().toLocalDate())) {
							
						lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO+1;						
					}
				}
					
				// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
					
				if(lastLNBeforeNextSOE != null) {
						
					if(dateO.toLocalDate().isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && dateO.toLocalDate().isBefore(nextSOE.getDate().toLocalDate())) {
		
						vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(nextSOE.getStartingSeason(), 0, false);

					}
					else {
							
						//Si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
						// A no ser que ese año haya habido metono, en ese caso es Priverno
							
						if(lunasNuevasPasadasDesdeLastSOEHastaDateO == 0 && lastSOE.isSolsticioInvierno()) {
							
							MetonsEntity meton = this.metonsRepository.findByYearAndInicialIsTrueAndNuevoIsTrue(dateO.getYear());
							
							if(meton != null) {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO+1, false);
							}
							else {
								
								vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);
							}
															
						}
				
						else {
								
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, false);
						}
							
							
					}	
				}
				else {
					System.out.println("Error, no hay lastLNBeforeNextSOE.");
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
		String weekVauString = "0";
		String dayVauString = "0";
		
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
				
				if(caeEnLunaNueva) {
								
					weekVauString = this.weeksRepository.findByWeekOfMonth("0").getName();
				}
				else {
					
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
					
					evento = evento + "Starting ";
				}
				else if(eclipeno.getCuartal()) {
					
					evento = evento + "Quarter ";
				}
				else if (eclipeno.getBicuartal()) {
					
					evento = evento + "Biquarter ";
				}
				else if (eclipeno.getTricuartal()) {
					
					evento = evento + "Triquarter ";
				}
				
				if(eclipeno.getNuevo()) {
					
					evento = evento + "new eclipen.";
				}
				else if(eclipeno.getLleno()) {
					
					evento = evento + "full eclipen.";
				}
			}
			else if (meton != null) {
				
				if (meton.getInicial()) {
					
					evento = evento + "Starting ";
				}
				else if(meton.getCuartal()) {
					
					evento = evento + "Quarter ";
				}
				else if (meton.getBicuartal()) {
					
					evento = evento + "Biquarter ";
				}
				else if (meton.getTricuartal()) {
					
					evento = evento + "Triquarter ";
				}
				
				if(meton.getNuevo()) {
					
					evento = evento + "new meton.";
				}
				else if(meton.getLleno()) {
					
					evento = evento + "full meton.";
				}
				
			}
			else if(soe != null) {
				
				if(soe.isSolsticioInvierno()) {
					
					evento = evento + "Winter Solstice.";
				}
				else if(soe.isEquinoccioPrimavera()) {
					
					evento = evento + "Spring Equinox.";
				}
				else if(soe.isSolsticioVerano()) {
					
					evento = evento + "Summer Solstice.";
				}
				else if (soe.isEquinoccioOtonyo()) {
					
					evento = evento + "Autumn Equinox.";
				}
			}
			else if (eclipse != null) {				
								
				String tipo = "";
				if(eclipse.isDeLuna()) {
					
					tipo =  "lunar eclipse.";
				}
				else if (eclipse.isDeSol()) {
					
					tipo = "solar eclipse.";
				}
				
				String fase = "";
				if(eclipse.isEsAnular()) {
					fase = "Annular ";
				}
				else if (eclipse.isEsHibrido()) {
					fase = "Hybrid ";
				}
				else if (eclipse.isEsParcial()) {
					fase = "Partial ";
				}
				else if (eclipse.isEsPenumbral()) {
					fase = "Penumbral ";
				}
				else if (eclipse.isEsTotal()) {
					fase = "Total ";
				}		
				
				evento = evento + fase + tipo;
			}			
			else if (luna != null) {

			    if (luna.isNueva()) {
			        evento = evento + "New Moon.";
			    } 
			    else if (luna.isCuartoCreciente()) {
			        evento = evento + "First Quarter Moon.";
			    } 
			    else if (luna.isLlena()) {
			        evento = evento + "Full Moon.";
			    } 
			    else if (luna.isCuartoMenguante()) {
			        evento = evento + "Last Quarter Moon.";
			    }
			}
		}
		
		return evento;
	}



	
}







