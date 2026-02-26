package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DateDTOFromDB;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
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
	
	// METODOS PUBLICOS 

	public FromDateVAUToDateOResponse getDateOFromDateVAU(DateDTOFromDB dateVAU) {

		FromDateVAUToDateOResponse fromDateVAUToDateOResponse = new FromDateVAUToDateOResponse();
		LocalDate dateO = null;
		String response = "";

		// Lo primero es obtener el soe correspondiente a ese mes
		// Para ello necesitamos el año y el monthOfSeason, que basicamente es el numero de LN que han pasado		
		
		int anyoDelSoe = dateVAU.getMeton().getYear() + dateVAU.getYear()+1;
		
		if(dateVAU.getMonth().isLiminal() || (dateVAU.getMonth().getSeason() == 1 && dateVAU.getMonth().getMonthOfSeason() != 0)) {
			
			anyoDelSoe=anyoDelSoe-1;	
		}
		
		SolsticiosYEquinocciosEntity soe = this.solsticiosYEquinocciosRepository.findByYearAndStartingSeason(anyoDelSoe, dateVAU.getMonth().getSeason());
		
		if(soe != null) {
			
			LunasEntity lunaCorrespondiente = new LunasEntity();

			if(dateVAU.getMonth().getHibrid()) {
				
				// Si es hibrido, hay que coger la luna nueva anterior al soe y contar desde ahi
				
				lunaCorrespondiente = lunasRepository.findTopByDateLessThanOrderByDateDesc(soe.getDate());				
			}
			else {		
				
				// Ya con el SOE, selecciona la luna nueva a partir de la cual se cuentan los dias

				List<LunasEntity> lunasAPartirDelSoe = this.lunasRepository.findTop3ByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(soe.getDate());			
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
		
		LocalDateTime dateO = date.atStartOfDay();		
		
	
		// Lo primero es la fecha del último eclipeno que haya ocurrido hasta la fecha a consultar
		EclipenosEntity lastEclipeno = this.eclipenosRepository.findTopByDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrDateLessThanEqualAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrueOrderByDateDesc(dateO, dateO);
		
		if(lastEclipeno != null) {
			
			// Una vez tenemos este eclipeno, hay que contar cuantos metonos han ocurrido
			List<MetonsEntity> metons = this.metonsRepository.findByDateBetweenAndInicialIsTrueAndNuevoIsTrueOrderByDateDesc(lastEclipeno.getDate(), dateO);
			
			
			if(metons != null) {

				// Con esto, lo primero es obtener todos los solsticios y equinoccios ocurridos entre el último métono y la fecha a consultar mas un año			
				List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas = this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(metons.get(0).getDate().minusYears(1), dateO.plusYears(1));
				
				// Y todas las lunas, desde un año antes hasta un año despues de la fecha a consultar			
				List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente = this.lunasRepository.findByDateBetweenAndNuevaTrue(dateO.minusYears(1), dateO.plusYears(1));
			
				
				if(soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.isEmpty() || lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.isEmpty()) {
					
					System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas.");
				}
				else {
					
					dateVAU = new DateDTO();
					
					// Lo primero es obtener el añoVAU				
					dateVAU.setYear(this.getVAUYear(dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, metons.get(0)));
					
					// Luego el mesVau
					dateVAU.setMonth(this.getVAUMonth(dateO, soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasNuevasDesdeElAnyoAnteriorHasElSiguiente));
					
					// Despues, la semana y el dia				
					VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(dateO, lunasNuevasDesdeElAnyoAnteriorHasElSiguiente);
					dateVAU.setWeek(vauWeekAndDay.getWeek());
					dateVAU.setDay(vauWeekAndDay.getDay());
					
					// Indicamos el metono
					dateVAU.setMeton(getVAUMeton(metons));
					
					// E indicamos el eclipeno
					dateVAU.setEclipeno(String.valueOf(lastEclipeno.getYear()));
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
	
	public DateDTOFromDB getDateDTOFromDB(DateDTO dateVAU) {

		DateDTOFromDB dateVAUDTOFromDB = new DateDTOFromDB();
		
		if(Integer.valueOf(dateVAU.getEclipeno()) >= 0) {
			
			EclipenosEntity eclipeno = this.eclipenosRepository.findTopByYearAndInicialIsTrueAndNuevoIsTrueAndEsAnularIsTrueOrYearAndInicialIsTrueAndNuevoIsTrueAndEsTotalIsTrue(Integer.valueOf(dateVAU.getEclipeno()), Integer.valueOf(dateVAU.getEclipeno()));
			
			if(eclipeno != null) {
				
				if(dateVAU.getMeton().getNumberOfMeton() >= 0) {
					
					List<MetonsEntity> metons = this.metonsRepository.findByYearGreaterThanEqualAndInicialIsTrueAndNuevoIsTrueOrderByDateAsc(eclipeno.getYear());			
													
					if(metons != null) {						
	
						MetonsEntity meton = metons.get(Integer.valueOf(dateVAU.getMeton().getNumberOfMeton()));
						
						dateVAUDTOFromDB.setMeton(meton);
						
						MetonsEntity nextMeton = this.metonsRepository.findFirstByYearGreaterThanAndInicialIsTrueAndNuevoIsTrueOrderByYearAsc(meton.getYear());
						
						if(nextMeton != null && (nextMeton.getYear() - meton.getYear() >= Integer.valueOf(dateVAU.getYear()))) {
							
							dateVAUDTOFromDB.setYear(Integer.valueOf(dateVAU.getYear()));
							MonthsEntity vauMonth = this.monthsRepository.findByName(dateVAU.getMonth().getName()); 
							
							if(vauMonth != null) {
								
								dateVAUDTOFromDB.setMonth(vauMonth);
								
								WeeksEntity vauWeek = this.weeksRepository.findByName(dateVAU.getWeek());
								
								if(vauWeek != null) {
									
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
									
									dateVAUDTOFromDB.setComentarios("No se ha encontrado la semana " + dateVAU.getWeek() + " en la base de datos.");
								}
							}
							else {
								
								dateVAUDTOFromDB.setComentarios("No se ha encontrado el mes " + dateVAU.getMonth() + " en la base de datos.");
							}	
						}						
						else {
							
							dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getYear() + " está fuera del rango para este métono.");
						}						
					}
					else {
						
						dateVAUDTOFromDB.setComentarios("No se tienen registros de métonos a parti del año " + Integer.valueOf(dateVAU.getEclipeno() + "."));
					}
				}
				else {
					
					dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getMeton() + " para un metono no es válido.");
				}
			}
			else {
				
				dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getEclipeno() + " para un eclipeno no es válido.");
			}
			
			
		}
		else {
			dateVAUDTOFromDB.setComentarios("El año " + dateVAU.getEclipeno() + " para un eclipeno no es válido.");
		}
	
		
				
		return dateVAUDTOFromDB;
	}


	
	
	// ========================= METODOS PRIVADOS
	
	
	private MetonDTO getVAUMeton (List<MetonsEntity> metons) {
		
		MetonDTO meton = new MetonDTO();
		meton.setNumberOfMeton(metons.size()-1);
		meton.setYearOfTheMeton(metons.get(0).getYear());		
		return meton;
	}

	private String getVAUYear(LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMeton) {
		
		String vauYear = "-";
		
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
				else if (soe.getDate().toLocalDate().isBefore(dateO.toLocalDate()) && soe.getDate().toLocalDate().isAfter(lastMeton.getDate().toLocalDate())){
					
					year=year+1;
				}
			}
			
		}
		
		if(caeEnSolsticioDeInvierno) {
			
			vauYear = "No pertenece a ningún año, es el día del solsticio de invierno del año " + Integer.valueOf(year)+1 + ".";
		}
		else {
			vauYear = String.valueOf(year);
		}
		
		return vauYear;
		
	}
	
	
	
	private MonthDTO getVAUMonth (LocalDateTime dateO, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente) {
		
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
			for(int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.size(); i++) {
				
				LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.get(i);
				
	
				if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
						
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
							
						if(lunasNuevasPasadasDesdeLastSOEHastaDateO == 0 && lastSOE.isSolsticioInvierno()) {
								
							vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(lastSOE.getStartingSeason(), lunasNuevasPasadasDesdeLastSOEHastaDateO, true);								
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
				
			if (caeEnLunaNueva) {
				
				month.setLunaNueva(true);
				month.setName("No pertenece a ningún mes, es el día de la luna nueva de " +vauMonth.getName() + ".");
			}
			else {
				month.setName(vauMonth.getName());
			}		
		}
		else {
			
			System.out.println("Error, no se han encontrado nextSOE y/o lastSOE.");
		}
		

		return month;
	}
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDateTime dateO, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		String weekVauString = "-";
		String dayVauString = "-";
		
		// Lo primero es seleccionar la luna nueva mas reciente, si cae en luna llena, no hay dias ni semanas
		
		LunasEntity lastLN = new LunasEntity();
		long diasDesdeLaLunaNueva = Long.MAX_VALUE;
		boolean caeEnLunaNueva = false;
		for (int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.size() && !caeEnLunaNueva; i++) {
			
			LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.get(i);
			

			if(luna.getDate().toLocalDate().isEqual(dateO.toLocalDate())) {
					
				caeEnLunaNueva = true;
			}
			else if (luna.getDate().toLocalDate().isBefore(dateO.toLocalDate())) {
					
				long diasDeDiferenciaEntreLNYDateO = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), dateO.toLocalDate());
					
				if(diasDeDiferenciaEntreLNYDateO < diasDesdeLaLunaNueva) {
						
					lastLN=luna;
					diasDesdeLaLunaNueva = diasDeDiferenciaEntreLNYDateO;						
				}
			}
		}
			
		if(!caeEnLunaNueva && lastLN != null) {
			
			// Con la luna llena más reciente y con los días que los separan, ya lo tenemos
			
			if (diasDesdeLaLunaNueva <= 7) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("1").getName();
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



	
}







