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
import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.FestividadesDTO;
import CCASolutions.Calendario.DTOs.LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO;
import CCASolutions.Calendario.DTOs.LunasSolsticiosEclipsesMetonosYEclipenosDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonIADTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MinimaFestividadesDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.DTOs.ComportamientoLunaDTO;
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
import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.FestividadesRepository;
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
	
	@Autowired
	private FestividadesRepository festividadesRepository;

	
	// METODOS PUBLICOS 
	
	
	public DateDTO getDateVAUFromDateO (LocalDate date) {
		
		DateDTO dateVAU = null;
		LocalDateTime dateO = date.atTime(LocalTime.MAX);	
		
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAllByOrderByDateDesc();
		
		if(!allEclipenos.isEmpty()) {
			LunasSolsticiosEclipsesMetonosYEclipenosDTO lunasSolsticiosEclipsesMetonosYEclipenos = new LunasSolsticiosEclipsesMetonosYEclipenosDTO();
			lunasSolsticiosEclipsesMetonosYEclipenos.setEclipenos(allEclipenos);
			lunasSolsticiosEclipsesMetonosYEclipenos.setLastEclipenoIN(this.getLastEclipenoIN(allEclipenos, date));
			lunasSolsticiosEclipsesMetonosYEclipenos.setLastEclipenoINSelecto(this.getLastEclipenoINSelecto(allEclipenos, date));
			
			if(lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN() != null || lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoINSelecto() != null) {
				
				List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN().getDate(), dateO.plusYears(1));
	
				
				if(!allMetons.isEmpty()) {
					
					lunasSolsticiosEclipsesMetonosYEclipenos.setMetons(allMetons);
					lunasSolsticiosEclipsesMetonosYEclipenos.setLastMetonIN(this.getLastMetonINForDate(allMetons, date));
					
					if(lunasSolsticiosEclipsesMetonosYEclipenos.getLastMetonIN() != null) {									
						
						lunasSolsticiosEclipsesMetonosYEclipenos.setLunas(this.lunasRepository.findByDateBetween(dateO.minusYears(1), dateO.plusYears(1)));
						lunasSolsticiosEclipsesMetonosYEclipenos.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(lunasSolsticiosEclipsesMetonosYEclipenos.getLastMetonIN().getDate().minusYears(1), dateO.plusYears(1)));
						lunasSolsticiosEclipsesMetonosYEclipenos.setEclipses(this.eclipsesRepository.findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN().getDate().toLocalDate().atStartOfDay(), dateO.plusYears(1)));
						lunasSolsticiosEclipsesMetonosYEclipenos.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetween(dateO.minusMonths(3), dateO.plusMonths(3)));
						
						if(lunasSolsticiosEclipsesMetonosYEclipenos.getSoes().isEmpty() || lunasSolsticiosEclipsesMetonosYEclipenos.getLunas().isEmpty() || lunasSolsticiosEclipsesMetonosYEclipenos.getEclipses().isEmpty()) {
							
							System.out.println("Error al obtener dateVAU: no se han encontrado solsticios/equinoccios/lunas/eclipses.");
						}
						else {					
	
							dateVAU = this.getDateVAU(date, lunasSolsticiosEclipsesMetonosYEclipenos);									
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
				if(lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN() == null) {
					System.out.println("Error al obtener dateVAU: no se ha encontrado un eclípeno IN anterior a la fecha proporcionada.");
				}
				else if (lunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoINSelecto() == null) {
					System.out.println("Error al obtener dateVAU: no se ha encontrado un eclípeno selecto anterior a la fecha proporcionada.");
				}
				
			}
		}
		else {
			System.out.println("Error al obtener dateVAU: no hay eclipenos");
		}
		
		
		return dateVAU;
		
	}
	

	
	// ========================= METODOS PRIVADOS
	
	private DateDTO getDateVAU(LocalDate date, LunasSolsticiosEclipsesMetonosYEclipenosDTO allLunasSolsticiosEclipsesMetonosYEclipenos) {
		
		DateDTO dateVAU= new DateDTO();
		
		dateVAU.setYear(this.getVAUYear(allLunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN(), date, allLunasSolsticiosEclipsesMetonosYEclipenos.getSoes(), allLunasSolsticiosEclipsesMetonosYEclipenos.getLastMetonIN()));					
		dateVAU.setMonth(this.getVAUMonth(date, allLunasSolsticiosEclipsesMetonosYEclipenos.getSoes(), allLunasSolsticiosEclipsesMetonosYEclipenos.getLunas()));
		
		VAUWeekAndDayDTO vauWeekAndDay = this.getVauWeekAndDay(date, allLunasSolsticiosEclipsesMetonosYEclipenos.getLunas());
		dateVAU.setWeek(vauWeekAndDay.getWeek());
		dateVAU.setDay(vauWeekAndDay.getDay());					

		dateVAU.setLastEclipenoSelecto(this.getVAUEclipenoSelecto(allLunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoINSelecto(), date));
		
		dateVAU.setMetonoVAU(this.getVAUMeton(allLunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN(), allLunasSolsticiosEclipsesMetonosYEclipenos.getMetons(), date));
		
		dateVAU.setEclipenoVAU(this.getVAUEclipeno(allLunasSolsticiosEclipsesMetonosYEclipenos.getEclipenos(), allLunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoINSelecto(), date));			
		dateVAU.setAbsoluteEclipses(this.getVAUAbsoluteEclipses(dateVAU, allLunasSolsticiosEclipsesMetonosYEclipenos.getEclipses(), date, allLunasSolsticiosEclipsesMetonosYEclipenos.getLastMetonIN()));
		dateVAU.setCasalero(this.getCasalero(allLunasSolsticiosEclipsesMetonosYEclipenos.getLastEclipenoIN()));
		dateVAU.setEstadoLuna(this.getEstadoLuna(date, allLunasSolsticiosEclipsesMetonosYEclipenos.getApoperis()));	
		
		dateVAU.setNotableEvent(this.getNotableEvent(date, allLunasSolsticiosEclipsesMetonosYEclipenos));		
		dateVAU.setFestividades(this.getFestividades(date, allLunasSolsticiosEclipsesMetonosYEclipenos));
		
		return dateVAU;
	}
	
	
	private NotableEventDTO getNotableEvent(LocalDate date, LunasSolsticiosEclipsesMetonosYEclipenosDTO allLunasSolsticiosEclipsesMetonosYEclipenos) {
	
		NotableEventDTO notableEventDTO = new NotableEventDTO();
		
		LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosPPPFecha = this.getFenomenosPPPFecha(date, allLunasSolsticiosEclipsesMetonosYEclipenos);
		
		notableEventDTO.setToday(this.getEventoActual(date, fenomenosPPPFecha));
		notableEventDTO.setPrevious(this.getEventoPasado(date, fenomenosPPPFecha));
		notableEventDTO.setNext(this.getEventoProximo(date, fenomenosPPPFecha));
		
		return notableEventDTO;		
	}
	
	
	private FestividadesDTO getFestividades(LocalDate date, LunasSolsticiosEclipsesMetonosYEclipenosDTO allLunasSolsticiosEclipsesMetonosYEclipenos) {
		
		FestividadesDTO festividades = new FestividadesDTO();
		
		List<MinimaFestividadesDTO> festividadesObtenidasDTO = this.getFestividadesDesdeFecha(date, allLunasSolsticiosEclipsesMetonosYEclipenos);
		List<FestividadesEntity> festividadesEntities = this.festividadesRepository.findAll();
		
		List<MinimaFestividadesDTO> festividadesActuales = new ArrayList<>();
		List<MinimaFestividadesDTO> festividadesPasadas = new ArrayList<>();
		List<MinimaFestividadesDTO> festividadesFuturas = new ArrayList<>();
		
		for(MinimaFestividadesDTO festividad : festividadesObtenidasDTO) {
			
			if(festividad.getDiasDeDiferenciaConDate() == 0) {
				
				festividadesActuales.add(festividad);
			}
			else if(festividad.getDate().toLocalDate().isAfter(date)) {
				
				festividadesFuturas.add(festividad);
			}
			else if(festividad.getDate().toLocalDate().isBefore(date)) {
				
				festividadesPasadas.add(festividad);
			}
		}
		
		festividades.setFestividadActual(this.getFestividadActual(festividadesEntities, festividadesActuales));
		festividades.setFestividadAnterior(this.getFestividadAnterior(festividadesEntities, festividadesPasadas));
		festividades.setFestividadProxima(this.getFestividadProxima(festividadesEntities, festividadesFuturas));		
		
		return festividades;		
	}		
	
	private String getFestividadActual(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesActuales) {
		
		String festividadActual = "";
		
		if(!festividadesActuales.isEmpty()) {
			
			if(festividadesActuales.size()==1) {
				
				for(FestividadesEntity entity : festividadesEntities) {
					
					if(entity.getCode().equals(festividadesActuales.get(0).getCode())) {
					
						festividadActual = entity.getNombre();
					}	
				}
			}
			else {
				
				String codeCECMCA = "";
				
				for(MinimaFestividadesDTO festividad : festividadesActuales) {
					
					switch (festividad.getCode()) {
					
						case "CE":
							
							codeCECMCA = festividad.getCode();
							break;
								
						case "CM":
							
							if(!codeCECMCA.equals("CM")) {
								
								codeCECMCA = festividad.getCode();
							}
							break;
						
						case "CA":
							
							if(!codeCECMCA.equals("CE") && !codeCECMCA.equals("CM")) {
								
								codeCECMCA = festividad.getCode();
							}
							break;
					}
				}
				
				for(FestividadesEntity entity : festividadesEntities) {
					
					if(entity.getCode().equals(codeCECMCA)) {
					
						festividadActual = entity.getNombre();
					}	
				}
			}
		}	
		
		
		
		return festividadActual;
	}
	
	private String getFestividadAnterior(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesPasadas) {
		
		MinimaFestividadesDTO festividadMasCercanaDTO = this.getFestividadParaGetName(festividadesPasadas);
		String festividadAnterior = this.getFestividadName(festividadMasCercanaDTO, festividadesEntities).replace("{{TTTT}}", "hace");		
		
		return festividadAnterior;
	}

	private String getFestividadProxima(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesFuturas) {
	
		MinimaFestividadesDTO festividadMasCercanaDTO = this.getFestividadParaGetName(festividadesFuturas);
		String festividadProxima = this.getFestividadName(festividadMasCercanaDTO, festividadesEntities).replace("{{TTTT}}", "dentro de");		
		
		return festividadProxima;
	}
	
	private String getFestividadName(MinimaFestividadesDTO festividadMasCercanaDTO, List<FestividadesEntity> festividadesEntities) {
		
		String name = "";
		String dias = "días";
		
		for(FestividadesEntity entity : festividadesEntities) {
				
			if(entity.getCode().equals(festividadMasCercanaDTO.getCode())) {
					
				if(festividadMasCercanaDTO.getDiasDeDiferenciaConDate() == 1) {
					dias = "día";
				}
				name = entity.getNombre() + " {{TTTT}} " + festividadMasCercanaDTO.getDiasDeDiferenciaConDate() + " " + dias;
			}		
		}

		return name;
	}
	
	private MinimaFestividadesDTO getFestividadParaGetName(List<MinimaFestividadesDTO> festividadesFuturas) {
		
		MinimaFestividadesDTO festividadParaGetName = new MinimaFestividadesDTO();
		
		long diasMinimosEntreDateYFestividad = Long.MAX_VALUE;
		
		for(MinimaFestividadesDTO festividad : festividadesFuturas) {
			
			if(festividad.getDiasDeDiferenciaConDate() < diasMinimosEntreDateYFestividad) {
				
				diasMinimosEntreDateYFestividad = festividad.getDiasDeDiferenciaConDate();
				festividadParaGetName = festividad;				
				
			}
		}
		
		return festividadParaGetName;
	}
	
	private List<MinimaFestividadesDTO> getFestividadesDesdeFecha(LocalDate date, LunasSolsticiosEclipsesMetonosYEclipenosDTO allLunasSolsticiosEclipsesMetonosYEclipenos) {
		
		List<MinimaFestividadesDTO> festividadesObtenidasDTO = new ArrayList<>();

		
		// 1 - Cambio de eclipeno
		MinimaFestividadesDTO cambioDeEclipeno = new MinimaFestividadesDTO();
		cambioDeEclipeno.setCode("CE");		
		long diasMinimosDeDiferenciaEntreCEYDate = Long.MAX_VALUE;
		boolean esHoyCE = false;
		
		for(int i = 0; i<allLunasSolsticiosEclipsesMetonosYEclipenos.getEclipenos().size(); i++) {
			
			EclipenosEntity eclipeno = allLunasSolsticiosEclipsesMetonosYEclipenos.getEclipenos().get(i);
			
			if(eclipeno.isInvernal() && eclipeno.isNuevo()) {
				
				if(eclipeno.getDate().toLocalDate().isEqual(date)) {
					cambioDeEclipeno.setDate(eclipeno.getDate());
					cambioDeEclipeno.setDiasDeDiferenciaConDate(0);
					esHoyCE=true;
				}
				else if(!esHoyCE) {
					
					long diasDeDiferenciaEntrCEYDate = Math.abs(ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date));
					
					if(diasDeDiferenciaEntrCEYDate < diasMinimosDeDiferenciaEntreCEYDate) {
						diasMinimosDeDiferenciaEntreCEYDate = diasDeDiferenciaEntrCEYDate;
						cambioDeEclipeno.setDate(eclipeno.getDate());
						cambioDeEclipeno.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCEYDate);
					}
				}
			}
		}
		
		festividadesObtenidasDTO.add(cambioDeEclipeno);
		
		
		
		
		
		// 2 - Cambio de metono
		MinimaFestividadesDTO cambioDeMetono = new MinimaFestividadesDTO();
		cambioDeMetono.setCode("CM");		
		long diasMinimosDeDiferenciaEntreCMYDate = Long.MAX_VALUE;
		boolean esHoyCM = false;
		
		for(int i = 0; i<allLunasSolsticiosEclipsesMetonosYEclipenos.getMetons().size(); i++) {
			
			MetonsEntity metono = allLunasSolsticiosEclipsesMetonosYEclipenos.getMetons().get(i);
			
			if(metono.isInvernal() && metono.isNuevo()) {
				
				if(metono.getDate().toLocalDate().isEqual(date)) {
					cambioDeMetono.setDate(metono.getDate());
					cambioDeMetono.setDiasDeDiferenciaConDate(0);
					esHoyCM=true;
				}
				else if(!esHoyCM) {
					
					long diasDeDiferenciaEntrCMYDate = Math.abs(ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date));
					
					if(diasDeDiferenciaEntrCMYDate < diasMinimosDeDiferenciaEntreCMYDate) {
						diasMinimosDeDiferenciaEntreCMYDate = diasDeDiferenciaEntrCMYDate;
						cambioDeMetono.setDate(metono.getDate());
						cambioDeMetono.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCMYDate);
					}
				}
			}
		}
		
		festividadesObtenidasDTO.add(cambioDeMetono);
		
		
		
		// 3 - Cambio de año, Bienvenida a la Primavera, Mitad de año y Bienvenida del otoño
		MinimaFestividadesDTO cambioDeAnyo = new MinimaFestividadesDTO();
		cambioDeAnyo.setCode("CA");		
		long diasMinimosDeDiferenciaEntreCAYDate = Long.MAX_VALUE;
		boolean esHoyCA = false;
		
		MinimaFestividadesDTO bienvenidaPrimavera = new MinimaFestividadesDTO();
		bienvenidaPrimavera.setCode("BP");		
		long diasMinimosDeDiferenciaEntreBPYDate = Long.MAX_VALUE;
		boolean esHoyBP =false;
		
		MinimaFestividadesDTO pasoOtonyo = new MinimaFestividadesDTO();
		pasoOtonyo.setCode("PO");		
		long diasMinimosDeDiferenciaEntrePOYDate = Long.MAX_VALUE;
		boolean esHoyBO =false;
		
		MinimaFestividadesDTO mitadAnyo = new MinimaFestividadesDTO();
		mitadAnyo.setCode("MA");		
		long diasMinimosDeDiferenciaEntreMAYDate = Long.MAX_VALUE;
		boolean esHoyMA = false;
		
		SolsticiosYEquinocciosEntity sIMasCercano = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity sVMasCercano = new SolsticiosYEquinocciosEntity(); // Tendra utilidad cuando haya festividades con luna en verano
		SolsticiosYEquinocciosEntity eOMasCercano = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity ePMasCercano = new SolsticiosYEquinocciosEntity(); // Tendra utilidad cuando haya festividades con luna en primavera
		
		
		for(int j = 0; j<allLunasSolsticiosEclipsesMetonosYEclipenos.getSoes().size(); j++) {
			
			SolsticiosYEquinocciosEntity soe = allLunasSolsticiosEclipsesMetonosYEclipenos.getSoes().get(j);
			
			if(soe.getDate().toLocalDate().isEqual(date)) {
				
				if(soe.isSolsticioInvierno()) {
					
					cambioDeAnyo.setDate(soe.getDate());
					cambioDeAnyo.setDiasDeDiferenciaConDate(0);
					sIMasCercano=soe;
					esHoyCA = true;
				}
				else if(soe.isEquinoccioPrimavera()) {
					
					bienvenidaPrimavera.setDate(soe.getDate());
					bienvenidaPrimavera.setDiasDeDiferenciaConDate(0);
					ePMasCercano=soe;
					esHoyBP = true;
				}
				else if(soe.isSolsticioVerano()) {
					
					mitadAnyo.setDate(soe.getDate());
					mitadAnyo.setDiasDeDiferenciaConDate(0);
					sVMasCercano=soe;
					esHoyMA = true;
				}
				else if(soe.isEquinoccioOtonyo()) {
					
					pasoOtonyo.setDate(soe.getDate());
					pasoOtonyo.setDiasDeDiferenciaConDate(0);
					eOMasCercano=soe;
					esHoyBO = true;
				}
				
			}
			else {
					
				if(soe.isSolsticioInvierno() && !esHoyCA) {
					
					long diasDeDiferenciaEntreCAYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreCAYDate < diasMinimosDeDiferenciaEntreCAYDate) {
						
						diasMinimosDeDiferenciaEntreCAYDate = diasDeDiferenciaEntreCAYDate;
						cambioDeAnyo.setDate(soe.getDate());
						cambioDeAnyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCAYDate);
						sIMasCercano=soe;
					}
				}
					
				else if(soe.isEquinoccioPrimavera() && !esHoyBP) {
						
					long diasDeDiferenciaEntreBPYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreBPYDate < diasMinimosDeDiferenciaEntreBPYDate) {
						
						diasMinimosDeDiferenciaEntreBPYDate = diasDeDiferenciaEntreBPYDate;
						bienvenidaPrimavera.setDate(soe.getDate());
						bienvenidaPrimavera.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreBPYDate);
						ePMasCercano=soe;
					}
				}
				else if(soe.isSolsticioVerano() && !esHoyMA) {
						
					long diasDeDiferenciaEntreMAYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreMAYDate < diasMinimosDeDiferenciaEntreMAYDate) {
						
						diasMinimosDeDiferenciaEntreMAYDate = diasDeDiferenciaEntreMAYDate;
						mitadAnyo.setDate(soe.getDate());
						mitadAnyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreMAYDate);
						sVMasCercano=soe;
					}
				}
			
				else if(soe.isEquinoccioOtonyo() && !esHoyBO) {
						
					long diasDeDiferenciaEntrePOYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntrePOYDate < diasMinimosDeDiferenciaEntrePOYDate) {
						
						diasMinimosDeDiferenciaEntrePOYDate = diasDeDiferenciaEntrePOYDate;
						pasoOtonyo.setDate(soe.getDate());
						pasoOtonyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntrePOYDate);
						eOMasCercano=soe;
					}
				}
			}
		}
		
		festividadesObtenidasDTO.add(cambioDeAnyo);
		festividadesObtenidasDTO.add(bienvenidaPrimavera);
		festividadesObtenidasDTO.add(mitadAnyo);
		festividadesObtenidasDTO.add(pasoOtonyo);
		
		
		// 4 - Inicio del primer mes del año, despedida del verano y despedida del año
		
		MinimaFestividadesDTO inicioPrimerMesAnyo = new MinimaFestividadesDTO();
		inicioPrimerMesAnyo.setCode("IA");		
		long diasMinimosDeDiferenciaEntreLunaYSI = Long.MAX_VALUE;

		
		MinimaFestividadesDTO despedidaVerano = new MinimaFestividadesDTO();
		despedidaVerano.setCode("DV");		
		long diasMinimosDeDiferenciaEntreDVYLuna = Long.MAX_VALUE;
		
		
		MinimaFestividadesDTO despedidaAnyo = new MinimaFestividadesDTO();
		despedidaAnyo.setCode("DA");		
		long diasMinimosDeDiferenciaEntreDAYLuna = Long.MAX_VALUE;

		
		for(LunasEntity luna : allLunasSolsticiosEclipsesMetonosYEclipenos.getLunas()) {
			
	
			if(luna.isNueva()) {
				
				if(sIMasCercano.getDate().toLocalDate().isBefore(luna.getDate().toLocalDate())) {
								
					long diasDeDiferenciaEntreLunaYSI = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreLunaYSI < diasMinimosDeDiferenciaEntreLunaYSI) {
							
						diasMinimosDeDiferenciaEntreLunaYSI = diasDeDiferenciaEntreLunaYSI;
						inicioPrimerMesAnyo.setDate(luna.getDate());
						inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(date, luna.getDate().toLocalDate())));
									
					}
				}
				
			}
			else if (luna.isLlena()) {
				
				SolsticiosYEquinocciosEntity soeMasCercanoALaLuna = new SolsticiosYEquinocciosEntity();
				
				long diasMinimosDeDiferenciaEntreSoeYLuna = Long.MAX_VALUE;
				for(SolsticiosYEquinocciosEntity soe : allLunasSolsticiosEclipsesMetonosYEclipenos.getSoes()) {
					
					long diasDeDiferenciaEntreSoeYLuna = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), luna.getDate().toLocalDate()));
					
					if(diasDeDiferenciaEntreSoeYLuna < diasMinimosDeDiferenciaEntreSoeYLuna) {
						
						diasMinimosDeDiferenciaEntreSoeYLuna = diasDeDiferenciaEntreSoeYLuna;
						soeMasCercanoALaLuna=soe;
					}					
				}
				
				
				if(soeMasCercanoALaLuna.getStartingSeason()==4 && soeMasCercanoALaLuna.getDate().toLocalDate().equals(eOMasCercano.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(eOMasCercano.getDate().toLocalDate())) {
					
					long diasDeDiferenciaEntreDVYLuna = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreDVYLuna < diasMinimosDeDiferenciaEntreDVYLuna) {
							
						diasMinimosDeDiferenciaEntreDVYLuna = diasDeDiferenciaEntreDVYLuna;
						despedidaVerano.setDate(luna.getDate());
						despedidaVerano.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date)));		
					}		
				}
				else if(soeMasCercanoALaLuna.getStartingSeason()==1 && soeMasCercanoALaLuna.getDate().toLocalDate().equals(sIMasCercano.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(sIMasCercano.getDate().toLocalDate())) {
					

					long diasDeDiferenciaEntreDAYLuna = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreDAYLuna < diasMinimosDeDiferenciaEntreDAYLuna) {
							
						diasMinimosDeDiferenciaEntreDAYLuna = diasDeDiferenciaEntreDAYLuna;
						despedidaAnyo.setDate(luna.getDate());
						despedidaAnyo.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date)));
						
					}			
				}				
			}
		}
		
		// Si hay un eclipeno no hay festividad de inicio del primer mes ni de cambio de metono
		// Y si hay un metono, no hay inicio del primer mes
		if(cambioDeEclipeno.getDiasDeDiferenciaConDate() < 100) { 
			
			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
			cambioDeMetono.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
		}
		else if(cambioDeMetono.getDiasDeDiferenciaConDate() < 100) {
			
			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
		}
		
		
		festividadesObtenidasDTO.add(inicioPrimerMesAnyo);
		festividadesObtenidasDTO.add(despedidaVerano);
		festividadesObtenidasDTO.add(despedidaAnyo);
		
		
		
		
		return festividadesObtenidasDTO;
	}
	
	
	private String getEventoActual(LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas) {
		
		String eventoActual = "";	
		
		LunasEntity lunaParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getLunaActual();
		SolsticiosYEquinocciosEntity soeParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getSoeActual();
		MetonsEntity metonParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getMetonoActual();
		EclipsesEntity eclipseParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getEclipseActual();
		EclipenosEntity eclipenoParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getEclipenoActual();
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getApoperiActual();
	
		eventoActual = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo);
		
		return eventoActual;
	}
	
	private String getEventoPasado(LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO lunasSolsticiosEclipsesMetonosYEclipenos) {
		
		String eventoPasado = "";				
		
		Long diasEntreLunaYDate = Long.MAX_VALUE;
		Long diasEntreSOEYDate = Long.MAX_VALUE;
		Long diasEntreMetonYDate = Long.MAX_VALUE;
		Long diasEntreEclipseYDate = Long.MAX_VALUE;
		Long diasEntreEclipenoYDate = Long.MAX_VALUE;
		Long diasEntreApoperiYDate = Long.MAX_VALUE;
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior() != null) {
			diasEntreLunaYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior().getDate().toLocalDate(), dateO);
		}
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior() != null) {
			diasEntreSOEYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior().getDate().toLocalDate(), dateO);
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior() != null) {
			diasEntreMetonYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior().getDate().toLocalDate(), dateO);
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseAnterior() != null) {
			diasEntreEclipseYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseAnterior().getDate().toLocalDate(), dateO);
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoAnterior() != null) {
			diasEntreEclipenoYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoAnterior().getDate().toLocalDate(), dateO);	    
		} 
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiAnterior() != null) {
			diasEntreApoperiYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiAnterior().getDate().toLocalDate(), dateO);	    
		} 
				
			  
		long minDias = Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate)))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior() : null;
		MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior() : null;
		EclipsesEntity eclipseParaMetodo = diasEntreEclipseYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseAnterior() : null;
		EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoAnterior() : null;
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = diasEntreApoperiYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiAnterior() : null;
			    
		String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo);
			    
		String dias = " días";
		if(minDias == 1) {
			 dias = " día";
		}
			
		eventoPasado = nombreDelEvento +" hace "+ minDias + dias;	

		return eventoPasado;
	}
	
	
	private String getEventoProximo (LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO lunasSolsticiosEclipsesMetonosYEclipenos) {
		
		String eventoFuturo = "";		
		
		Long diasEntreLunaYDate = Long.MAX_VALUE;
		Long diasEntreSOEYDate = Long.MAX_VALUE;
		Long diasEntreMetonYDate = Long.MAX_VALUE;
		Long diasEntreEclipseYDate = Long.MAX_VALUE;
		Long diasEntreEclipenoYDate = Long.MAX_VALUE;
		Long diasEntreApoperiYDate = Long.MAX_VALUE;
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima() != null) {
			diasEntreLunaYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima().getDate().toLocalDate());
		}
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo() != null) {
			diasEntreSOEYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo().getDate().toLocalDate());
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo() != null) {
			diasEntreMetonYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo().getDate().toLocalDate());
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseProximo() != null) {
			diasEntreEclipseYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseProximo().getDate().toLocalDate());
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoProximo() != null) {
			diasEntreEclipenoYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoProximo().getDate().toLocalDate());	
		} 

		if(lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiAnterior() != null) {
			diasEntreApoperiYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiProximo().getDate().toLocalDate());	    
		} 
			  
		long minDias = Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate)))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo() : null;
		MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo() : null;
		EclipsesEntity eclipseParaMetodo = diasEntreEclipseYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseProximo() : null;
		EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoProximo() : null;
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = diasEntreApoperiYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiProximo() : null;
			    
		String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo);
			    
		String dias = " días";
		if(minDias == 1) {
			 dias = " día";
			}
			
		eventoFuturo = nombreDelEvento +" dentro de "+ minDias + dias;		
	
		return eventoFuturo;
	}
	
	
	
	private EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis) {
		
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
	
	private CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN) {
		
		CasaleroDTO casaleroDTO = null;
		
		try {
			
			CasalerosEntity casaleroEntity = casalerosRepository.findByEclipenoId(lastEclipenoIN.getId());
			
			if(casaleroEntity != null) {
				
				casaleroDTO = new CasaleroDTO();
				casaleroDTO.setDateO(casaleroEntity.getDate().toLocalDate());
				
				String tipo = "";
				if(casaleroEntity.getMetonoId() != null) {
					
					Optional<MetonsEntity> metonoOpt = this.metonsRepository.findById(casaleroEntity.getMetonoId());
					
					if(metonoOpt.isPresent()) {
						
						MetonsEntity metono = metonoOpt.get();
						
						tipo="Metónico";
						
						casaleroDTO.setLleno(metono.isLleno());
						casaleroDTO.setNuevo(metono.isNuevo());
						casaleroDTO.setInvernal(metono.isInvernal());
						casaleroDTO.setPrimaveral(metono.isPrimaveral());
						casaleroDTO.setEstival(metono.isEstival());	
						casaleroDTO.setOtonyal(metono.isOtonyal());
						casaleroDTO.setNuevo(true);
					}								
				}
				else if (casaleroEntity.getEclipseId() != null){
					
					Optional<EclipsesEntity> eclipseOpt = this.eclipsesRepository.findById(casaleroEntity.getEclipseId());
					
					if(eclipseOpt.isPresent()) {
						
						EclipsesEntity eclipse = eclipseOpt.get();
						
						tipo="Eclipelar";
						casaleroDTO.setDeSol(eclipse.isDeSol());
						casaleroDTO.setDeLuna(eclipse.isDeLuna());
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
		
		
		
		if(!dateVAU.getEclipenoVAU().isEclipenoINDay()) {
		
			
			List<EclipsesEntity> eclipsesSolaresNoParcialesDesdeLastEclipenoINList = new ArrayList<>();		
			List<EclipsesEntity> eclipsesLunaresNoParcialesNiPenumbralesDesdeLastEclipenoINList = new ArrayList<>();
			
			int lunaresDesdeElUltimoMetonoIN =0;
			int solaresDesdeElUltimoMetonoIN =0;
			
			//Si estamos en el primer métono, hay que restarle 1 porque viene el propio del eclípeno
			if(dateVAU.getMetonoVAU().getMetonsIN().getMetonosINSinceLastEclipenoIN() == 0) {
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
	
	private EclipenoSelectoDTO getVAUEclipenoSelecto(EclipenosEntity lastEclipenoSelecto, LocalDate date) {
		
		EclipenoSelectoDTO eclipenoSelectoVAU = new EclipenoSelectoDTO();
		
		eclipenoSelectoVAU.setDaysSinceCurrentEclipenoSelectoIN("hace " + ChronoUnit.DAYS.between(lastEclipenoSelecto.getDate().toLocalDate(), date) + " días");
		eclipenoSelectoVAU.setEclipenoINSelectoDay(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date));
		
		
		return eclipenoSelectoVAU;
	}

	private EclipenoINDTO getVAUEclipeno(List<EclipenosEntity> allEclipenos, EclipenosEntity lastEclipenoSelecto, LocalDate date) {
		
		EclipenoINDTO eclipenoVAU = new EclipenoINDTO();
		
		
		if(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)){
			
			eclipenoVAU.setEclipenoINDay(true);
			eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(0);
			eclipenoVAU.setNumberOfEclipenoIN(0);
			eclipenoVAU.setYearOfCurrentEclipenoIN(lastEclipenoSelecto.getYear());
			
		}
		else {
			
			List<EclipenosEntity> eclipenosIN = new ArrayList<>();
			
			for(EclipenosEntity eclipeno : allEclipenos) {
				
				if(eclipeno.isInvernal() && eclipeno.isNuevo() && !eclipeno.getDate().isBefore(lastEclipenoSelecto.getDate()) && !eclipeno.getDate().toLocalDate().isAfter(date)) {
					eclipenosIN.add(eclipeno);
				}
			}
			
			eclipenoVAU.setYearOfCurrentEclipenoIN(eclipenosIN.get(0).getYear());
			eclipenoVAU.setEclipenoINDay(eclipenosIN.get(0).getDate().toLocalDate().isEqual(date));
			
			int eclipenosDesdeElLastEclipenSelecto = (eclipenosIN.size()-1); // -1 porque incluye el del eclipeno
			
			// No se suma un eclipeno hasta que pase el dia del eclipeno, pero si es el dia de eclipeno no se resta, que se ha restado antes
			
			if(eclipenoVAU.isEclipenoINDay() && !lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)) {
				
				eclipenosDesdeElLastEclipenSelecto = eclipenosDesdeElLastEclipenSelecto-1;
			}
			
			eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(eclipenosDesdeElLastEclipenSelecto);
			int yearOfTheEclipeno = eclipenosDesdeElLastEclipenSelecto +1;
			
			if(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)) { //Si es el dia del eclipeno selecto, no estamos en ningun eclipeno
				yearOfTheEclipeno= yearOfTheEclipeno-1;
			}
			eclipenoVAU.setNumberOfEclipenoIN(yearOfTheEclipeno);
			
			if(eclipenosIN.get(0).isInvertido() && yearOfTheEclipeno != 0 && !eclipenoVAU.isEclipenoINDay()) {
				eclipenoVAU.setLastEclipenoSurname("(Invertido)");
			}
			else if(eclipenosIN.get(0).isSelecto() && yearOfTheEclipeno != 0 && !eclipenoVAU.isEclipenoINDay()) {
				eclipenoVAU.setLastEclipenoSurname("(Selecto)");
			}
		}
		
		return eclipenoVAU;
	}
	
	private MetonDTO getVAUMeton (EclipenosEntity lastEclipenoIN, List<MetonsEntity> metons, LocalDate date) {
		
		MetonDTO metonVAU = new MetonDTO();
		
		MetonINDTO metonINDTO = new MetonINDTO();
		MetonIADTO metonIADTO = new MetonIADTO();
		
		List<MetonsEntity> metonsIN = new ArrayList<>();
		List<MetonsEntity> metonsIA = new ArrayList<>();
		
		for(MetonsEntity meton : metons) {
			
			if(meton.isInvernal() && !meton.getDate().toLocalDate().isAfter(date)) {
				if(meton.isNuevo()) {
					metonsIN.add(meton);
				}
				else if(meton.isAporico()) {
					metonsIA.add(meton);
				}		
			}
		}
		
		metonINDTO.setYearOfCurrentMetonIN(metonsIN.get(0).getYear());
		metonIADTO.setYearOfCurrentMetonIA(metonsIA.get(0).getYear());
		
		metonINDTO.setMetonoINDay(metonsIN.get(0).getDate().toLocalDate().isEqual(date));
		metonIADTO.setMetonoIADay(metonsIA.get(0).getDate().toLocalDate().isEqual(date));
		
		int metonosDesdeElLastEclipen = (metonsIN.size()-1); // -1 porque incluye el del eclipeno
		
		// No se suma un metono hasta que pase el dia del metono, pero si es el dia de eclipeno no se resta, que se ha restado antes
		
		if(metonINDTO.isMetonoINDay() && !lastEclipenoIN.getDate().toLocalDate().isEqual(date)) {
			
			metonosDesdeElLastEclipen = metonosDesdeElLastEclipen-1;
		}
		
		metonINDTO.setMetonosINSinceLastEclipenoIN(metonosDesdeElLastEclipen);
		int yearOfTheMeton = metonosDesdeElLastEclipen +1;
		
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(date)) { //Si es el dia del eclipeno, no estamos en ningun metono
			yearOfTheMeton= yearOfTheMeton-1;
		}
		metonINDTO.setNumberOfMetonIN(yearOfTheMeton);
		
		if(metonsIN.get(0).isInvertido() && yearOfTheMeton != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonsIN.get(0).isSelecto() && yearOfTheMeton != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Selecto)");
		}
		
		
		if(metonsIA.get(0).isInvertido() && yearOfTheMeton != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonsIA.get(0).isSelecto() && yearOfTheMeton != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Selecto)");
		}
		
		
		metonVAU.setMetonsIN(metonINDTO);
		metonVAU.setMetonsIA(metonIADTO);
		
		return metonVAU;
	}

	private YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN) {
		
		YearDTO vauYear = new YearDTO();
		
		boolean caeEnSolsticioDeInvierno=false;
		
		// Hay que contar cuantos solsticios de invierno han pasado desde el métono hasta la fecha a consultar
		// Si la fecha a consultar cae en solsticio de invierno, no corresponde a ningún añoVau
		
		int year = 0;
		
		for(int i = 0; i<soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.size() && !caeEnSolsticioDeInvierno; i++) {
			
			SolsticiosYEquinocciosEntity soe = soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas.get(i);
			
			if(soe.isSolsticioInvierno()) {				
		
				if(soe.getDate().toLocalDate().isEqual(date)) {
						
					caeEnSolsticioDeInvierno=true;
				}
				else if (soe.getDate().toLocalDate().isBefore(date) && soe.getDate().toLocalDate().isAfter(lastMetonIN.getDate().toLocalDate())){
					
					year=year+1;
				}
			}
			
		}
		
		
		vauYear.setEsSolsticioDeInvierno(caeEnSolsticioDeInvierno);	
		vauYear.setSolsticiosDeInviernoSinceLastMetonIN(year);	
		
		int numberOfYear = year +1;
		if(lastEclipenoIN.getDate().toLocalDate().isEqual(date) || lastMetonIN.getDate().toLocalDate().isEqual(date)) {
			
			numberOfYear = numberOfYear-1;
		}
		vauYear.setNumberOfYear(numberOfYear);
	
		return vauYear;
		
	}
	
	
	
	private MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente) {
		
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
			LunasEntity lunaNuevaPosteriorMasCercanaALaFecha = new LunasEntity();
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
			boolean caeEnLunaLlena = false;
			
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
	
	
	
	private VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente) {
		
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
	

	
	private String getNotableEventName(LunasEntity luna, SolsticiosYEquinocciosEntity soe, MetonsEntity meton, EclipsesEntity eclipse, EclipenosEntity eclipeno, ApogeosYPerigeosLunaEntity apoperi) {
		
		String evento = "";
		
				
		if(luna != null || soe!= null || meton!= null || eclipse!= null || eclipeno!= null || apoperi != null) {
					
			evento = "";
					
			if(eclipeno != null) {
						
				if (eclipeno.isInvernal()) {
							
					evento = evento + "Eclípeno invernal ";
				}
				else if(eclipeno.isPrimaveral()) {
							
					evento = evento + "Eclípeno primaveral ";
				}
				else if (eclipeno.isEstival()) {
							
					evento = evento + "Eclípeno estival ";
				}
				else if (eclipeno.isOtonyal()) {
							
					evento = evento + "Eclípeno otoñal ";
				}
						
				if(eclipeno.isNuevo()) {
							
					evento = evento + "nuevo";
				}
				else if(eclipeno.isLleno()) {
							
					evento = evento + "lleno";
				}
				
				
				if(eclipeno.isSelecto()) {
					evento = evento + " selecto";
				}
				else if(eclipeno.isInvertido()) {
					evento = evento + " invertido";
				}

			}
			else if (meton != null) {
						
				if (meton.isInvernal()) {
							
					evento = evento + "Métono invernal ";
				}
				else if(meton.isPrimaveral()) {
							
					evento = evento + "Métono primaveral ";
				}
				else if (meton.isEstival()) {
							
					evento = evento + "Métono estival ";
				}
				else if (meton.isOtonyal()) {
							
					evento = evento + "Métono otoñal ";
				}
						
				if(meton.isNuevo()) {
							
					evento = evento + "nuevo";
				}
				else if(meton.isLleno()) {
							
					evento = evento + "lleno";
				}
				

				if(meton.isSelecto()) {
					evento = evento + " selecto";
				}
				else if (meton.isInvertido()) {
					evento = evento + " invertido";
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
				

				if(luna.isSelecta()) {
					evento = evento + " selecta";
				}
				else if(luna.isInvertida()) {
					evento = evento + " invertida";
				}

			}
			else if (apoperi != null) {
				
				String estado = "";
				
				if(apoperi.isEsApogeo()) {
					estado = "durmiente";
				}
				else if (apoperi.isEsPerigeo()) {
					estado = "presente";
				}
				evento = evento + "Luna " + estado;
				
			}
			
		}

		return evento;
	}
	
	private EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date) {
		
		EclipenosEntity lastEclipenoIN = null;
		
		long diasMinimosDeDiferenciaEntreEclipenoYDate =Long.MAX_VALUE;		
		for(EclipenosEntity eclipeno : allEclipenos) {
					
			if(!eclipeno.getDate().toLocalDate().isAfter(date) && eclipeno.isInvernal() && eclipeno.isNuevo() && (eclipeno.isEsAnular() || eclipeno.isEsTotal())) {	
				
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
	
	
	private EclipenosEntity getLastEclipenoINSelecto(List<EclipenosEntity> allEclipenos, LocalDate date) {
		
		EclipenosEntity lastEclipenoINSelecto = null;
		
		long diasMinimosDeDiferenciaEntreEclipenoYDate =Long.MAX_VALUE;		
		for(EclipenosEntity eclipeno : allEclipenos) {
					
			if(!eclipeno.getDate().toLocalDate().isAfter(date) && eclipeno.isInvernal() && eclipeno.isNuevo() && (eclipeno.isEsAnular() || eclipeno.isEsTotal() && eclipeno.isSelecto())) {	
				
				long diasDeDiferenciaEntreEclipenoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreEclipenoYDate < diasMinimosDeDiferenciaEntreEclipenoYDate) {
					lastEclipenoINSelecto = new EclipenosEntity();
					diasMinimosDeDiferenciaEntreEclipenoYDate = diasDeDiferenciaEntreEclipenoYDate;
					lastEclipenoINSelecto = eclipeno;
				}
			}
		}
		
		return lastEclipenoINSelecto;
	}
	
	
	private MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date) {
		
		MetonsEntity lastMetonINForDate = new MetonsEntity();
		
		long diasMinimosDeDiferenciaEntreMetonoYDate =Long.MAX_VALUE;
		
		for(MetonsEntity metono : allMetons) {									
			
			if(!metono.getDate().toLocalDate().isAfter(date) && metono.isInvernal() && metono.isNuevo()) {
				
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
	
private LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO getFenomenosPPPFecha(LocalDate dateO, LunasSolsticiosEclipsesMetonosYEclipenosDTO lunasSolsticiosEclipsesMetonosYEclipenos) {
		
	LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosParaEventosDTO = new LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO();
		
		LunasEntity lunaActual = null;
		ApogeosYPerigeosLunaEntity apoperiActual = null;
		SolsticiosYEquinocciosEntity soeActual = null;
		MetonsEntity metonActual = null;
		EclipsesEntity eclipseActual = null;
		EclipenosEntity eclipenoActual = null;
		
		LunasEntity lunaPasado = null;
		ApogeosYPerigeosLunaEntity apoperiPasado = null;
		SolsticiosYEquinocciosEntity soePasado = null;
		MetonsEntity metonPasado = null;
		EclipsesEntity eclipsePasado = null;
		EclipenosEntity eclipenoPasado = null;	
		
		LunasEntity lunaFuturo = null;
		ApogeosYPerigeosLunaEntity apoperiFuturo = null;
		SolsticiosYEquinocciosEntity soeFuturo = null;
		MetonsEntity metonFuturo = null;
		EclipsesEntity eclipseFuturo = null;
		EclipenosEntity eclipenoFuturo = null;	
		
		
		long diasMinimosDeDiferenciaEntreApoperiFuturaYDate = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreApoperiPasadaYDate = Long.MAX_VALUE;
		for(ApogeosYPerigeosLunaEntity apoperi : lunasSolsticiosEclipsesMetonosYEclipenos.getApoperis()) {
			
			if (apoperi.getDate().toLocalDate().isEqual(dateO)) {
				apoperiActual=apoperi;
			}			
			else if (apoperi.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreApoperiPasadaYDate = ChronoUnit.DAYS.between(apoperi.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreApoperiPasadaYDate < diasMinimosDeDiferenciaEntreApoperiPasadaYDate) {
					diasMinimosDeDiferenciaEntreApoperiPasadaYDate = diasDeDiferenciaEntreApoperiPasadaYDate;
					apoperiPasado=apoperi;
				}
			}			
			else if(apoperi.getDate().toLocalDate().isAfter(dateO)) {
					
				long diasDeDiferenciaEntreApoperiFuturaYDate = ChronoUnit.DAYS.between(dateO, apoperi.getDate().toLocalDate());
					
				if(diasDeDiferenciaEntreApoperiFuturaYDate < diasMinimosDeDiferenciaEntreApoperiFuturaYDate) {
					diasMinimosDeDiferenciaEntreApoperiFuturaYDate = diasDeDiferenciaEntreApoperiFuturaYDate;
					apoperiFuturo=apoperi;		
				}
			}
		}
		
		long diasMinimosDeDiferenciaEntreLunaFuturaYDate = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreLunaPasadaYDate = Long.MAX_VALUE;
		for(LunasEntity luna : lunasSolsticiosEclipsesMetonosYEclipenos.getLunas()) {
			
			if (luna.getDate().toLocalDate().isEqual(dateO)) {
				lunaActual=luna;
			}			
			else if (luna.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreLunaPasadaYDate = ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreLunaPasadaYDate < diasMinimosDeDiferenciaEntreLunaPasadaYDate) {
					diasMinimosDeDiferenciaEntreLunaPasadaYDate = diasDeDiferenciaEntreLunaPasadaYDate;
					lunaPasado=luna;
				}
			}			
			else if(luna.getDate().toLocalDate().isAfter(dateO)) {
					
				long diasDeDiferenciaEntreLunaFuturaYDate = ChronoUnit.DAYS.between(dateO, luna.getDate().toLocalDate());
					
				if(diasDeDiferenciaEntreLunaFuturaYDate < diasMinimosDeDiferenciaEntreLunaFuturaYDate) {
					diasMinimosDeDiferenciaEntreLunaFuturaYDate = diasDeDiferenciaEntreLunaFuturaYDate;
					lunaFuturo=luna;		
				}
			}
		}
	
	
		long diasMinimosDeDiferenciaEntreSoePasadoYDate =Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreSoeFuturoYDate =Long.MAX_VALUE;
		for(SolsticiosYEquinocciosEntity soe : lunasSolsticiosEclipsesMetonosYEclipenos.getSoes()) {
			
			if(soe.getDate().toLocalDate().isEqual(dateO)) {
				soeActual=soe;
			}	
			else if(soe.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreSoePasadoYDate = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreSoePasadoYDate < diasMinimosDeDiferenciaEntreSoePasadoYDate) {
					diasMinimosDeDiferenciaEntreSoePasadoYDate = diasDeDiferenciaEntreSoePasadoYDate;
					soePasado=soe;
				}
			}
			else if(soe.getDate().toLocalDate().isAfter(dateO)) {
					
				long diasDeDiferenciaEntreSoeFuturoYDate = ChronoUnit.DAYS.between(dateO, soe.getDate().toLocalDate());
					
				if(diasDeDiferenciaEntreSoeFuturoYDate < diasMinimosDeDiferenciaEntreSoeFuturoYDate) {
					diasMinimosDeDiferenciaEntreSoeFuturoYDate = diasDeDiferenciaEntreSoeFuturoYDate;
					soeFuturo=soe;
					
				}
			}
		}
		
		long diasMinimosDeDiferenciaEntreMetonoPasadoYDate =Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreMetonoFuturoYDate =Long.MAX_VALUE;
		for(MetonsEntity meton : lunasSolsticiosEclipsesMetonosYEclipenos.getMetons()) {
			if(meton.getDate().toLocalDate().isEqual(dateO)) {
				metonActual=meton;
			}		
			
			else if(meton.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreMetonoPasadoYDate = ChronoUnit.DAYS.between(meton.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreMetonoPasadoYDate < diasMinimosDeDiferenciaEntreMetonoPasadoYDate) {
					diasMinimosDeDiferenciaEntreMetonoPasadoYDate = diasDeDiferenciaEntreMetonoPasadoYDate;
					metonPasado=meton;
				}
			}	
			else if(meton.getDate().toLocalDate().isAfter(dateO)) {
				
				long diasDeDiferenciaEntreMetonoFuturoYDate = ChronoUnit.DAYS.between(dateO, meton.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreMetonoFuturoYDate < diasMinimosDeDiferenciaEntreMetonoFuturoYDate) {
					diasMinimosDeDiferenciaEntreMetonoFuturoYDate = diasDeDiferenciaEntreMetonoFuturoYDate;
					metonFuturo=meton;
				}
			}
		}
		
		
		long diasMinimosDeDiferenciaEntreEclipsePasadoYDate =Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreEclipseFuturoYDate =Long.MAX_VALUE;
		for(EclipsesEntity eclipse : lunasSolsticiosEclipsesMetonosYEclipenos.getEclipses()) {
			
			if(eclipse.getDate().toLocalDate().isEqual(dateO)) {
				eclipseActual=eclipse;
			}
			else if(eclipse.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreEclipsePasadoYDate = ChronoUnit.DAYS.between(eclipse.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreEclipsePasadoYDate < diasMinimosDeDiferenciaEntreEclipsePasadoYDate) {
					diasMinimosDeDiferenciaEntreEclipsePasadoYDate = diasDeDiferenciaEntreEclipsePasadoYDate;
					eclipsePasado=eclipse;
				}
			}
			else if(eclipse.getDate().toLocalDate().isAfter(dateO)) {
				
				long diasDeDiferenciaEntreEclipseFuturoYDate = ChronoUnit.DAYS.between(dateO, eclipse.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreEclipseFuturoYDate < diasMinimosDeDiferenciaEntreEclipseFuturoYDate) {
					diasMinimosDeDiferenciaEntreEclipseFuturoYDate = diasDeDiferenciaEntreEclipseFuturoYDate;
					eclipseFuturo=eclipse;
				}
			}
		}
		
		
		long diasMinimosDeDiferenciaEntreEclipenoPasadoYDate =Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreEclipenoFuturoYDate =Long.MAX_VALUE;
		for(EclipenosEntity eclipeno : lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenos()) {
			if(eclipeno.getDate().toLocalDate().isEqual(dateO)) {
				eclipenoActual=eclipeno;
			}
			else if(eclipeno.getDate().toLocalDate().isBefore(dateO)) {
				
				long diasDeDiferenciaEntreEclipenoPasadoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), dateO);
				
				if(diasDeDiferenciaEntreEclipenoPasadoYDate < diasMinimosDeDiferenciaEntreEclipenoPasadoYDate) {
					diasMinimosDeDiferenciaEntreEclipenoPasadoYDate = diasDeDiferenciaEntreEclipenoPasadoYDate;
					eclipenoPasado=eclipeno;
				}
			}
			else if(eclipeno.getDate().toLocalDate().isAfter(dateO)) {
				
				long diasDeDiferenciaEntreEclipenoFuturoYDate = ChronoUnit.DAYS.between(dateO, eclipeno.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreEclipenoFuturoYDate < diasMinimosDeDiferenciaEntreEclipenoFuturoYDate) {
					diasMinimosDeDiferenciaEntreEclipenoFuturoYDate = diasDeDiferenciaEntreEclipenoFuturoYDate;
					eclipenoFuturo=eclipeno;
				}
			}
		}
		
		
		fenomenosParaEventosDTO.setLunaActual(lunaActual);
		fenomenosParaEventosDTO.setLunaAnterior(lunaPasado);
		fenomenosParaEventosDTO.setLunaProxima(lunaFuturo);
		
		fenomenosParaEventosDTO.setApoperiActual(apoperiActual);
		fenomenosParaEventosDTO.setApoperiAnterior(apoperiPasado);
		fenomenosParaEventosDTO.setApoperiProximo(apoperiFuturo);
		
		fenomenosParaEventosDTO.setSoeActual(soeActual);
		fenomenosParaEventosDTO.setSoeAnterior(soePasado);
		fenomenosParaEventosDTO.setSoeProximo(soeFuturo);
		
		fenomenosParaEventosDTO.setMetonoActual(metonActual);
		fenomenosParaEventosDTO.setMetonoAnterior(metonPasado);
		fenomenosParaEventosDTO.setMetonoProximo(metonFuturo);

		fenomenosParaEventosDTO.setEclipseActual(eclipseActual);
		fenomenosParaEventosDTO.setEclipseAnterior(eclipsePasado);
		fenomenosParaEventosDTO.setEclipseProximo(eclipseFuturo);

		fenomenosParaEventosDTO.setEclipenoActual(eclipenoActual);
		fenomenosParaEventosDTO.setEclipenoAnterior(eclipenoPasado);
		fenomenosParaEventosDTO.setEclipenoProximo(eclipenoFuturo);
		

		return fenomenosParaEventosDTO;
	}
	
	
}







