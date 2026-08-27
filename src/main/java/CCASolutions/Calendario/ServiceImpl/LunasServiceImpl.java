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
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.DTOs.YLPDTO;
import CCASolutions.Calendario.Entities.AllFasesLunaresEntity;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.AllFasesLunaresRepository;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.LunasService;

@Service
public class LunasServiceImpl implements LunasService {
	
	@Autowired
	private DatosRepository datosRepository;	
	
	@Autowired
	private LunasRepository lunasRepository;
	
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
	
	
	public VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas) {
		
		VAUWeekAndDayDTO vauWeekAndDay = new VAUWeekAndDayDTO();
		String weekVauString = null;
		String dayVauString = null;
		
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
			
			if (diasDesdeLaLunaNueva <= 7) {
				
				if(!caeEnLunaNueva) {							
							
					weekVauString = this.weeksRepository.findByWeekOfMonth("1").getName();
				}
				else {
					weekVauString = this.weeksRepository.findByWeekOfMonth("0").getName();
				}
				
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva).getName();
				
			} 
			else if (diasDesdeLaLunaNueva <= 14) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("2").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-7).getName();

			} 
			else if (diasDesdeLaLunaNueva <= 21) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("3").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-14).getName();

			} 
			else if (diasDesdeLaLunaNueva <= 28) {
				
				weekVauString = this.weeksRepository.findByWeekOfMonth("4").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-21).getName();
			}
			else {
				weekVauString = this.weeksRepository.findByWeekOfMonth("5").getName();
				dayVauString = this.daysRepository.findByDayOfWeek(diasDesdeLaLunaNueva-21).getName();
			}
		}
		
		vauWeekAndDay.setWeek(weekVauString);
		
		String dayParaDTO = dayVauString;
		
		boolean caeEnSoe = false;
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSoe; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.getDate().toLocalDate().isEqual(date)) {
				caeEnSoe = true;
			}
		}
		
		if(caeEnSoe && !caeEnLunaNueva) {
			
			dayParaDTO = dayParaDTO + " desdoblado";
					
		}
		vauWeekAndDay.setDay(dayParaDTO);
		
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
