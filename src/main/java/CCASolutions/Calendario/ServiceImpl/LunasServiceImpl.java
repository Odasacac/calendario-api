package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.DTOs.ComportamientoLunaDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.AllFasesLunaresEntity;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.AllFasesLunaresRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.LunasService;

@Service
public class LunasServiceImpl implements LunasService {
	
	@Autowired
	private DatosRepository datosRepository;	
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private MonthsRepository monthsRepository;
	
	@Autowired
	private WeeksRepository weeksRepository;
	
	@Autowired
	private DaysRepository daysRepository;
	
	@Autowired
	private AllFasesLunaresRepository allFasesLunaresRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private final static String API_LUNAR_FASES = "YLP";
	
	private final static String NEW_MOON = "NewMoon";
	private final static String FIRST_QUARTER = "FirstQuarter";
	private final static String FULLMOON = "FullMoon";
	private final static String LAST_QUARTER = "LastQuarter";
	
	
	// METODOS PUBLICOS
	

	public MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente) {
		
		MonthDTO month = new MonthDTO();
		
		List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente = new ArrayList<>();
		List<LunasEntity> lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente = new ArrayList<>();
		
		for(LunasEntity luna : lunasDesdeElAnyoAnteriorHastaElSiguiente) {
			if(luna.isNueva()) {
				lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.add(luna);
			}
			else if(luna.isLlena()) {
				lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.add(luna);
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
			
			if(soe.getDate().toLocalDate().isEqual(date)) {
				caeEnSOE = true;
				lastSOE = soe;
				nextSOE = soe;
			}
			else if(soe.getDate().toLocalDate().isBefore(date)) {
				
				long diasDeDiferenciaEntreLastSOEYFecha = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreLastSOEYFecha < diasMinimosDeDiferenciaConLastSOE) {
					diasMinimosDeDiferenciaConLastSOE = diasDeDiferenciaEntreLastSOEYFecha;
					lastSOE = soe;
				}
				
			}
			else if(soe.getDate().toLocalDate().isAfter(date)) {
				
				long diasDeDiferenciaEntreNextSOEYFecha = ChronoUnit.DAYS.between(date, soe.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreNextSOEYFecha < diasMinimosDeDiferenciaConNextSOE) {
					diasMinimosDeDiferenciaConNextSOE = diasDeDiferenciaEntreNextSOEYFecha;
					nextSOE = soe;
				}
			}			
		}
		
		if(lastSOE != null && nextSOE != null) {
			// Luego, coger las lunas nuevas que se encuentran entre ambos lastSOE y nextSOE
			// Si cae en Luna nueva, ya tenemos el mes
			
			LunasEntity lunaNuevaAnteriorMasCercanaALaFecha = new LunasEntity();
			LunasEntity lunaNuevaPosteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
			Long numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = Long.MAX_VALUE;	
			Long numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate = Long.MAX_VALUE;	
			
			List<LunasEntity> lunasNuevasEntreLastSOEYNextSOE = new ArrayList<>();
			boolean caeEnLunaNueva = false;
			String surname = "";
			for(int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.size(); i++) {
				
				LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHastaElSiguiente.get(i);
	
				if(luna.getDate().toLocalDate().isEqual(date)) {
						
					lunasNuevasEntreLastSOEYNextSOE.add(luna);	
					caeEnLunaNueva = true;
					
					if(luna.isSelecta()) {
						surname = "selecto";
					}
					else if(luna.isInvertida()) {
						surname = "invertido";
					}
						
				}
				else if(!luna.getDate().toLocalDate().isBefore(lastSOE.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(nextSOE.getDate().toLocalDate())){							
							
					lunasNuevasEntreLastSOEYNextSOE.add(luna);						
				}


				if(luna.getDate().toLocalDate().isBefore(date)) {
					
					long diasDeDiferenciaEntreLNAnteriorYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
					
					if(diasDeDiferenciaEntreLNAnteriorYDate < numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate) {
						
						numeroMinimoDeDiasEntreLunaNuevaAnteriorYDate = diasDeDiferenciaEntreLNAnteriorYDate;
						lunaNuevaAnteriorMasCercanaALaFecha = luna;
					}			
				}
				else if(luna.getDate().toLocalDate().isAfter(date)) {
				
					long diasDeDiferenciaEntreLNPosteriorYDate = ChronoUnit.DAYS.between( date, luna.getDate().toLocalDate());
					
					if(diasDeDiferenciaEntreLNPosteriorYDate < numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate) {
						
						numeroMinimoDeDiasEntreLunaNuevaSiguienteYDate = diasDeDiferenciaEntreLNPosteriorYDate;
						lunaNuevaPosteriorMasCercanaALaFecha = luna;
					}		
				}
			}
			
			LunasEntity lunaLlenaAnteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
			LunasEntity lunaLlenaPosteriorMasCercanaALaFecha = new LunasEntity(); // Ya tendra utilidad
			Long numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate = Long.MAX_VALUE;	
			Long numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate = Long.MAX_VALUE;	
			boolean caeEnLunaLlena = false;  // Ya tendra utilidad
			
			for(int i = 0; i<lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.size(); i++) {
				
				LunasEntity luna = lunasLlenasDesdeElAnyoAnteriorHastaElSiguiente.get(i);
				
				if(luna.getDate().toLocalDate().isBefore(date)) {
					
					long diasDeDiferenciaEntreLLAnteriorYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
					
					if(diasDeDiferenciaEntreLLAnteriorYDate < numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate) {
						
						numeroMinimoDeDiasEntreLunaLlenaAnteriorYDate = diasDeDiferenciaEntreLLAnteriorYDate;
						lunaLlenaAnteriorMasCercanaALaFecha = luna;
					}		
				}
				else if(luna.getDate().toLocalDate().isAfter(date)) {
					long diasDeDiferenciaEntreLLPosteriorYDate = ChronoUnit.DAYS.between(date, luna.getDate().toLocalDate());
					
					if(diasDeDiferenciaEntreLLPosteriorYDate < numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate) {
						
						numeroMinimoDeDiasEntreLunaLlenaSiguienteYDate = diasDeDiferenciaEntreLLPosteriorYDate;
						lunaLlenaPosteriorMasCercanaALaFecha = luna;
					}		
				}
				else if(luna.getDate().toLocalDate().isEqual(date)) {
					caeEnLunaLlena = true;
				}
				
			}
			
			
			MonthsEntity vauMonth = new MonthsEntity();
			// Si cae en soe, pertenece al mes hibrido de ese soe.
			// A no ser que sea luna nueva, en ese caso seria el mes siguiente
			if(caeEnSOE) {

				if(caeEnLunaNueva) {
					
					// Basicamente si hay un metono (da igual el tipo)
					MonthDTO monthIfLN = getVAUMonth(date.plusDays(1), soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasDesdeElAnyoAnteriorHastaElSiguiente);
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
						
					if(date.isAfter(luna.getDate().toLocalDate())) {
							
						lunasNuevasPasadasDesdeLastSOEHastaDateO = lunasNuevasPasadasDesdeLastSOEHastaDateO+1;						
					}
				}
					
				
					
				if(lastLNBeforeNextSOE != null || firstLNAfterLastSOE != null) {
					
					// Si la fecha a consultar esta entre la ultima luna y el nextSOE, pertenece al mes hibrido de ese soe.
					if(date.isAfter(lastLNBeforeNextSOE.getDate().toLocalDate()) && date.isBefore(nextSOE.getDate().toLocalDate())) {
		
						vauMonth = this.monthsRepository.findBySeasonAndMonthOfSeasonAndLiminal(nextSOE.getStartingSeason(), 0, false);

					}
					// Si la fecha a consultar esta entre el lastSOE y la primera luna, pertenece al mes hibrido de ese soe.
					// Pero si el lastSOE es solsticio de invierno y no ha pasado ninguna luna nueva, es Oterno Liminal
					// A no ser que sea luna nueva, que en ese caso será Prierno
					else if (date.isBefore(firstLNAfterLastSOE.getDate().toLocalDate()) && date.isAfter(lastSOE.getDate().toLocalDate())) {						

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
							
							MonthDTO monthIfLN = getVAUMonth(date.plusDays(1), soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, lunasDesdeElAnyoAnteriorHastaElSiguiente);
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
			
			
			// Una parte de un mes tiene apellido cuando su luna nueva es selecta o invertida

		
			if(caeEnLunaNueva) {
				month.setSurname(surname);
			}
			else {
			
				if(lunaNuevaAnteriorMasCercanaALaFecha.isSelecta()) {
					month.setSurname("selecto");
				}
				else if(lunaNuevaAnteriorMasCercanaALaFecha.isInvertida()) {
					month.setSurname("invertido");
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
	
	
	
	public VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		String weekVauString = null;
		String dayVauString = null;
		
		// Lo primero es seleccionar la luna nueva mas reciente, si cae en luna llena, no hay dias ni semanas
		
		LunasEntity lastLN = new LunasEntity();
		long diasDesdeLaLunaNueva = Long.MAX_VALUE;
		boolean caeEnLunaNueva = false;
		for (int i = 0; i<lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.size() && !caeEnLunaNueva; i++) {
			
			LunasEntity luna = lunasNuevasDesdeElAnyoAnteriorHasElSiguiente.get(i);
			
			if(luna.isNueva()) {
				
				if(luna.getDate().toLocalDate().isEqual(date)) {
					
					caeEnLunaNueva = true;
					diasDesdeLaLunaNueva=0;
				}
				else if (luna.getDate().toLocalDate().isBefore(date)) {
						
					long diasDeDiferenciaEntreLNYDateO = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
						
					if(diasDeDiferenciaEntreLNYDateO < diasDesdeLaLunaNueva) {
							
						lastLN=luna;
						diasDesdeLaLunaNueva = diasDeDiferenciaEntreLNYDateO;						
					}
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
	
		
	public AponovosDTO getAponovos(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
		
		AponovosDTO aponovosDTO = new AponovosDTO();
		
		List<LunasEntity> lunasSelectasDesdeLastMIARHastaDate = new ArrayList<>();
		LunasEntity lunaNSmasCercanaADate = null;
		long diasMinimosEntreDateYLNS = Long.MAX_VALUE;
		for(LunasEntity luna : datosCosmicosParaVAUDTO.getLunas()) {
	
			if(luna.isNueva() 
				&& luna.isSelecta() 
				&& luna.getDate().toLocalDate().isBefore(date)
				&& luna.getDate().toLocalDate().isAfter(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate().toLocalDate()) 
				&& !luna.getId().equals(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getLunaId())) {
				
				lunasSelectasDesdeLastMIARHastaDate.add(luna);
				
				long diasEntreDateYLNS = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date);
				if(diasEntreDateYLNS < diasMinimosEntreDateYLNS) {
					diasMinimosEntreDateYLNS = diasEntreDateYLNS;
					lunaNSmasCercanaADate=luna;
				}
			}
		}
		
		aponovosDTO.setAponovosPasadosDesdeLastMetonoIAR(lunasSelectasDesdeLastMIARHastaDate.size());
		aponovosDTO.setNumeroDeAponovo(aponovosDTO.getAponovosPasadosDesdeLastMetonoIAR()+1);
		
		int lunasNuevasDesdeLastLNSHastaDate = 0;
		
		if(lunaNSmasCercanaADate != null) {
			for(LunasEntity luna : datosCosmicosParaVAUDTO.getLunas()) {
				
				if(luna.isNueva() && luna.getDate().toLocalDate().isBefore(date) && luna.getDate().toLocalDate().isAfter(lunaNSmasCercanaADate.getDate().toLocalDate())) {
					lunasNuevasDesdeLastLNSHastaDate = lunasNuevasDesdeLastLNSHastaDate+1;
				}
			}
		}
		
		
		
		aponovosDTO.setLunasNuevasPasadasDesdeLastAponovo(lunasNuevasDesdeLastLNSHastaDate);
		aponovosDTO.setMesAponoval(aponovosDTO.getLunasNuevasPasadasDesdeLastAponovo()+1);
		
		return aponovosDTO;
	}
	
	public EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis) {
		
		EstadoLunaDTO estadoLuna = new EstadoLunaDTO();
		
		estadoLuna.setComportamientoLunaDTO(this.getComportamientoLuna(date, allApoperis));		
		
		return estadoLuna;
	}
	
	private ComportamientoLunaDTO getComportamientoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis) {
		
		ComportamientoLunaDTO comportamientoLuna = new ComportamientoLunaDTO();
		
		long diasMinimosEntreDateYApoperi = Long.MAX_VALUE;
		ApogeosYPerigeosLunaEntity apoperiMasCercanoADate = new ApogeosYPerigeosLunaEntity();
		
		for(ApogeosYPerigeosLunaEntity apoperi : allApoperis) {
			
			if(apoperi.getDate().toLocalDate().isBefore(date)) {
				
				long diasEntreDateYApoperi = ChronoUnit.DAYS.between(apoperi.getDate().toLocalDate(), date);
				
				if(diasEntreDateYApoperi < diasMinimosEntreDateYApoperi) {
					
					diasMinimosEntreDateYApoperi = diasEntreDateYApoperi;
					apoperiMasCercanoADate=apoperi;
				}
			}
			else if(apoperi.getDate().toLocalDate().isEqual(date)) {
				diasMinimosEntreDateYApoperi = Long.MIN_VALUE;
				apoperiMasCercanoADate=apoperi;
			}
		}
		
		if(apoperiMasCercanoADate.getDate().toLocalDate().isBefore(date)) {
			String accion = "";
			
			if(apoperiMasCercanoADate.isEsApogeo()){
				accion ="acercándose";
			}
			else if(apoperiMasCercanoADate.isEsPerigeo()) {
				accion = "alejándose";
			}

			String dias = "días";
					
			if(diasMinimosEntreDateYApoperi == 1) {
				dias = "día";
			}
							
			comportamientoLuna.setDireccion("Lleva " + diasMinimosEntreDateYApoperi + " " + dias + " " + accion);
		}
		else if(apoperiMasCercanoADate.getDate().toLocalDate().isEqual(date)){
			
			if(apoperiMasCercanoADate.isEsApogeo()){
				comportamientoLuna.setDireccion("Ha alcanzado su punto más lejano");
			}
			else if(apoperiMasCercanoADate.isEsPerigeo()) {
				comportamientoLuna.setDireccion("Ha alcanzado su punto más cercano");
			}
			comportamientoLuna.setDate(apoperiMasCercanoADate.getDate());
		}
			
		
	
		return comportamientoLuna;
	}
	
	
	
	public String poblateLunasFromOpale() {
		
		String resultado = "Lunas actualizadas sin problema.";
		
		DatosEntity apiGetLunasUrl = datosRepository.findByConcepto(API_LUNAR_FASES);
		
		List<LunasEntity> allLunas = this.lunasRepository.findAll();
		
		if(apiGetLunasUrl != null && allLunas.isEmpty()) {	
			
			for (int i = -4700; i < 2100; i++) {
				
				System.out.println("Actualizando lunas del anyo: " + i);
				
				try {
					List<LunarPhaseDTO> fasesLunaresDelAnyo = this.getFasesLunaresDelAnyoViaAPI(String.valueOf(i), apiGetLunasUrl.getValor());
					
					if(!fasesLunaresDelAnyo.isEmpty()) {
						
						for(LunarPhaseDTO faseLunarAPI : fasesLunaresDelAnyo) {
							
							if(i > 0) {
								
								LunasEntity lunaParaDB = new LunasEntity();
								
								switch (faseLunarAPI.getMoonPhase()){
								
									case NEW_MOON:
										lunaParaDB.setNueva(true);
										break;
										
									case FIRST_QUARTER:
										lunaParaDB.setCuartoCreciente(true);
										break;
										
									case FULLMOON:
										lunaParaDB.setLlena(true);
										break;
										
									case LAST_QUARTER:
										lunaParaDB.setCuartoMenguante(true);
										break;
								}
								
								lunaParaDB.setYear(LocalDateTime.parse(faseLunarAPI.getDate()).getYear());
								lunaParaDB.setDate(LocalDateTime.parse(faseLunarAPI.getDate()));									
								lunaParaDB.setSelecta(false);
								lunaParaDB.setInvertida(false);
								
								this.lunasRepository.save(lunaParaDB);;
							}
		
							AllFasesLunaresEntity allFaseLunarParaDB = new AllFasesLunaresEntity();
							
							switch (faseLunarAPI.getMoonPhase()){
							
								case NEW_MOON:
									allFaseLunarParaDB.setNueva(true);
									break;
								
								case FIRST_QUARTER:
									allFaseLunarParaDB.setCuartoCreciente(true);
									break;
								
								case FULLMOON:
									allFaseLunarParaDB.setLlena(true);
									break;
								
								case LAST_QUARTER:
									allFaseLunarParaDB.setCuartoMenguante(true);
									break;
							}
							

							String[] parts = String.valueOf(faseLunarAPI.getDate()).split("T");
							String[] dateParts = parts[0].split("-");
							String[] timeParts = parts[1].split(":");

							if(String.valueOf(faseLunarAPI.getDate()).startsWith("-")) {
								allFaseLunarParaDB.setYear(Integer.parseInt("-" + dateParts[1]));
								allFaseLunarParaDB.setMonth(Integer.parseInt(dateParts[2]));
								allFaseLunarParaDB.setDay(Integer.parseInt(dateParts[3]));
							}
							else {
								allFaseLunarParaDB.setYear(Integer.parseInt(dateParts[0]));
								allFaseLunarParaDB.setMonth(Integer.parseInt(dateParts[1]));
								allFaseLunarParaDB.setDay(Integer.parseInt(dateParts[2]));
							}
							

							allFaseLunarParaDB.setHour(Integer.parseInt(timeParts[0]));
							allFaseLunarParaDB.setMinute(Integer.parseInt(timeParts[1]));
							allFaseLunarParaDB.setSecond(Integer.parseInt(timeParts[2]));
							
							this.allFasesLunaresRepository.save(allFaseLunarParaDB);
						}					
						
						
						System.out.println("Actualizadas las lunas del anyo: " + i);
					}					
					else {
					
						System.out.println("No se han obtenido lunas de la API.");
						resultado = "Error al actualizar lunas: no se han obtenido lunas de la API.";
					}					
				}
				catch(Exception e) {
					System.out.println("Error al actualizar lunas del anyo " + i  +": "+ e);
					resultado = "Error al actualizar lunas, checkear logs.";
				}
				
			}
		}	
		
		else {
			
			if(apiGetLunasUrl == null) {
				
				System.out.println("La URL de la API para obtener las lunas es nula.");
				resultado = "Error al actualizar lunas: la URL de la API para obtener las lunas es nula.";
			}
			else if(!allLunas.isEmpty()) {
				
				System.out.println("Ya hay lunas en la base de datos.");
				resultado = "Error al actualizar lunas: ya hay lunas en la base de datos.";
			}
			
			
		}
		
		return resultado;
	}	

	
	public List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url){
		
		List<LunarPhaseDTO> fasesLunaresDelAnyo = new ArrayList<>();
		
		// https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}
		
		String urlParaLlamada = url.replace("{{YYYY}}", anyo);

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





}
