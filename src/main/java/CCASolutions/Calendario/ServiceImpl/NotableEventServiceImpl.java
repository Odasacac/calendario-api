package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO;
import CCASolutions.Calendario.DTOs.MidsisonDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Services.NotableEventService;

@Service
public class NotableEventServiceImpl implements NotableEventService {

	
	public NotableEventDTO getNotableEvent(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
	
		NotableEventDTO notableEventDTO = new NotableEventDTO();
		
		LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosPPPFecha = this.getFenomenosPPPFecha(date, datosCosmicosParaVAUDTO);
		
		notableEventDTO.setToday(this.getEventoActual(date, fenomenosPPPFecha));
		notableEventDTO.setPrevious(this.getEventoPasado(date, fenomenosPPPFecha));
		notableEventDTO.setNext(this.getEventoProximo(date, fenomenosPPPFecha));
		
		return notableEventDTO;		
	}
	
	private LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO getFenomenosPPPFecha(LocalDate dateO, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
		
		LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO fenomenosParaEventosDTO = new LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO();
			
			LunasEntity lunaActual = null;
			ApogeosYPerigeosLunaEntity apoperiActual = null;
			SolsticiosYEquinocciosEntity soeActual = null;
			List<MetonsEntity> metonActual = new ArrayList<>();
			EclipsesEntity eclipseActual = null;
			EclipenosEntity eclipenoActual = null;
			MidsisonDTO midsisonActual = null;
			
			LunasEntity lunaPasado = null;
			ApogeosYPerigeosLunaEntity apoperiPasado = null;
			SolsticiosYEquinocciosEntity soePasado = null;
			List<MetonsEntity> metonPasado = new ArrayList<>();
			EclipsesEntity eclipsePasado = null;
			EclipenosEntity eclipenoPasado = null;
			MidsisonDTO midsisonPasado = null;
			
			LunasEntity lunaFuturo = null;
			ApogeosYPerigeosLunaEntity apoperiFuturo = null;
			SolsticiosYEquinocciosEntity soeFuturo = null;
			List<MetonsEntity> metonFuturo = new ArrayList<>();
			EclipsesEntity eclipseFuturo = null;
			EclipenosEntity eclipenoFuturo = null;	
			MidsisonDTO midsisonFuturo = null;
			
			long diasMinimosDeDiferenciaEntreApoperiFuturaYDate = Long.MAX_VALUE;
			long diasMinimosDeDiferenciaEntreApoperiPasadaYDate = Long.MAX_VALUE;
			for(ApogeosYPerigeosLunaEntity apoperi : datosCosmicosParaVAUDTO.getApoperis()) {
				
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
			for(LunasEntity luna : datosCosmicosParaVAUDTO.getLunas()) {
				
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
			for(SolsticiosYEquinocciosEntity soe : datosCosmicosParaVAUDTO.getSoes()) {
				
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
			for(MetonsEntity meton : datosCosmicosParaVAUDTO.getMetons()) {
				if(meton.getDate().toLocalDate().isEqual(dateO)) {
					metonActual.add(meton);
				}		
				
				else if(meton.getDate().toLocalDate().isBefore(dateO)) {
					
					long diasDeDiferenciaEntreMetonoPasadoYDate = ChronoUnit.DAYS.between(meton.getDate().toLocalDate(), dateO);
					
					if(diasDeDiferenciaEntreMetonoPasadoYDate < diasMinimosDeDiferenciaEntreMetonoPasadoYDate) {
						diasMinimosDeDiferenciaEntreMetonoPasadoYDate = diasDeDiferenciaEntreMetonoPasadoYDate;
						metonPasado = new ArrayList<>();
						metonPasado.add(meton);
					}
					else if(diasDeDiferenciaEntreMetonoPasadoYDate == diasMinimosDeDiferenciaEntreMetonoPasadoYDate) {
						metonPasado.add(meton);
					}
				}	
				else if(meton.getDate().toLocalDate().isAfter(dateO)) {
					
					long diasDeDiferenciaEntreMetonoFuturoYDate = ChronoUnit.DAYS.between(dateO, meton.getDate().toLocalDate());
					
					if(diasDeDiferenciaEntreMetonoFuturoYDate < diasMinimosDeDiferenciaEntreMetonoFuturoYDate) {
						diasMinimosDeDiferenciaEntreMetonoFuturoYDate = diasDeDiferenciaEntreMetonoFuturoYDate;
						metonFuturo= new ArrayList<>();
						metonFuturo.add(meton);
					}
					else if(diasDeDiferenciaEntreMetonoFuturoYDate == diasMinimosDeDiferenciaEntreMetonoFuturoYDate) {
						metonFuturo.add(meton);
					}
				}
			}
			
			
			long diasMinimosDeDiferenciaEntreEclipsePasadoYDate =Long.MAX_VALUE;
			long diasMinimosDeDiferenciaEntreEclipseFuturoYDate =Long.MAX_VALUE;
			for(EclipsesEntity eclipse : datosCosmicosParaVAUDTO.getEclipses()) {
				
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
			for(EclipenosEntity eclipeno : datosCosmicosParaVAUDTO.getEclipenos()) {
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
			

			LocalDateTime diaDelMidsison = soePasado.getDate().plusSeconds((ChronoUnit.SECONDS.between(soePasado.getDate(), soeFuturo.getDate()))/2);
			
			if(diaDelMidsison.toLocalDate().isBefore(dateO)) {
				midsisonPasado = new MidsisonDTO();
				midsisonPasado.setDate(diaDelMidsison);
				midsisonPasado.setLastSoeSeason(soePasado.getStartingSeason());
			}
			else if(diaDelMidsison.toLocalDate().isEqual(dateO)) {
				midsisonActual = new MidsisonDTO();
				midsisonActual.setDate(diaDelMidsison);	
				midsisonActual.setLastSoeSeason(soePasado.getStartingSeason());
			}
			else if (diaDelMidsison.toLocalDate().isAfter(dateO)) {
				midsisonFuturo = new MidsisonDTO();
				midsisonFuturo.setDate(diaDelMidsison);	
				midsisonFuturo.setLastSoeSeason(soePasado.getStartingSeason());
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
			
			fenomenosParaEventosDTO.setMidsisonActual(midsisonActual);
			fenomenosParaEventosDTO.setMidsisonAnterior(midsisonPasado);
			fenomenosParaEventosDTO.setMidsisonProximo(midsisonFuturo);
			
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
		
	private String getEventoActual(LocalDate dateO, LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturasDTO LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas) {
		
		String eventoActual = "";	
		
		LunasEntity lunaParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getLunaActual();
		SolsticiosYEquinocciosEntity soeParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getSoeActual();
		List<MetonsEntity> metonParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getMetonoActual();
		EclipsesEntity eclipseParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getEclipseActual();
		EclipenosEntity eclipenoParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getEclipenoActual();
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getApoperiActual();
		MidsisonDTO midsisonParaMetono = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getMidsisonActual();
	
		eventoActual = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo, midsisonParaMetono);
		
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
		Long diasEntreMidsisonYDate = Long.MAX_VALUE;
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior() != null) {
			diasEntreLunaYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior().getDate().toLocalDate(), dateO);
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior() != null) {
			diasEntreSOEYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior().getDate().toLocalDate(), dateO);
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonAnterior() != null) {
			diasEntreMidsisonYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonAnterior().getDate().toLocalDate(), dateO);	    
		} 
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior() != null) {
			diasEntreMetonYDate = ChronoUnit.DAYS.between(lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior().get(0).getDate().toLocalDate(), dateO);
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
				
			  
		long minDias = Math.min(diasEntreMidsisonYDate, Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior() : null;
		MidsisonDTO midsisonParaMetodo = diasEntreMidsisonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonAnterior() : null;
		List<MetonsEntity> metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior() : new ArrayList<>();
		EclipsesEntity eclipseParaMetodo = diasEntreEclipseYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseAnterior() : null;
		EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoAnterior() : null;
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = diasEntreApoperiYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiAnterior() : null;
			    
		String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo, midsisonParaMetodo);
			    
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
		Long diasEntreMidsisonYDate = Long.MAX_VALUE;
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima() != null) {
			diasEntreLunaYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima().getDate().toLocalDate());
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo() != null) {
			diasEntreSOEYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo().getDate().toLocalDate());
		}
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonProximo() != null) {
			diasEntreMidsisonYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonProximo().getDate().toLocalDate());	    
		} 
		
		if(!lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo().isEmpty()) {
			diasEntreMetonYDate = ChronoUnit.DAYS.between(dateO, lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo().get(0).getDate().toLocalDate());
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
			  
		long minDias = Math.min(diasEntreMidsisonYDate, Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo() : null;
		MidsisonDTO midsisonParaMetodo = diasEntreMidsisonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonProximo() : null;
		List<MetonsEntity> metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo() : new ArrayList<>();
		EclipsesEntity eclipseParaMetodo = diasEntreEclipseYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipseProximo() : null;
		EclipenosEntity eclipenoParaMetodo = diasEntreEclipenoYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getEclipenoProximo() : null;
		ApogeosYPerigeosLunaEntity apoperiParaMetodo = diasEntreApoperiYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getApoperiProximo() : null;
			    
		String nombreDelEvento = this.getNotableEventName(lunaParaMetodo, soeParaMetodo, metonParaMetodo, eclipseParaMetodo, eclipenoParaMetodo, apoperiParaMetodo, midsisonParaMetodo);
			    
		String dias = " días";
		if(minDias == 1) {
			 dias = " día";
			}
			
		eventoFuturo = nombreDelEvento +" dentro de "+ minDias + dias;		
	
		return eventoFuturo;
	}
	
	private String getNotableEventName(LunasEntity luna, SolsticiosYEquinocciosEntity soe, List<MetonsEntity> meton, EclipsesEntity eclipse, EclipenosEntity eclipeno, ApogeosYPerigeosLunaEntity apoperi, MidsisonDTO midsison) {
		
		String evento = "";
		
				
		if(luna != null || soe!= null || !meton.isEmpty() || eclipse!= null || eclipeno!= null || apoperi != null || midsison!= null) {
					
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
										
				if(eclipeno.isApofasal()) {
					
					evento = evento + "apofasal ";
					
					if(eclipeno.isSelecto() && eclipeno.isNuevo()) {
						evento = evento + "remoto";
					}
					else if(eclipeno.isSelecto() && eclipeno.isLleno()) {
						evento = evento + "brillante";
					}
					else if(eclipeno.isInvertido() && eclipeno.isNuevo()) {
						evento = evento + "velado";
					}
					else if(eclipeno.isInvertido() && eclipeno.isLleno()) {
						evento = evento + "tenue";
					}
				}
				
				else {
					if(eclipeno.isNuevo()) {
						
						evento = evento + " nuevo";
					}
					else if(eclipeno.isLleno()) {
								
						evento = evento + " lleno";
					}
					if(eclipeno.isSelecto()) {
						
						evento = evento + " selecto";
					}
					else if(eclipeno.isInvertido()) {
						
						evento = evento + " invertido";
					}									
				}			
			}
			else if (!meton.isEmpty()) {
						
				if (meton.get(0).isInvernal()) {
							
					evento = evento + "Métono invernal";
				}
				else if(meton.get(0).isPrimaveral()) {
							
					evento = evento + "Métono primaveral";
				}
				else if (meton.get(0).isEstival()) {
							
					evento = evento + "Métono estival";
				}
				else if (meton.get(0).isOtonyal()) {
							
					evento = evento + "Métono otoñal";
				}
				
				if(meton.get(0).isSelecto()) {
					
					if(meton.get(0).isApofasal()) {
						evento = evento + " apofasal";
						
						if((meton.get(0).isFasal() && meton.get(0).isNuevo() || (meton.get(0).isApoperico() && meton.get(0).isAporico()))){
							evento = evento + " remoto";
						}
						else if((meton.get(0).isFasal() && meton.get(0).isLleno() || meton.get(0).isApoperico() && meton.get(0).isPerico())){
							evento = evento + " brillante";
						}	
					}
					else {
						if(meton.get(0).isFasal() && meton.get(0).isNuevo()){
							evento = evento + " nuevo";
						}
						else if(meton.get(0).isFasal() && meton.get(0).isLleno()){
							evento = evento + " lleno";
						} 
						else if(meton.get(0).isApoperico() && meton.get(0).isAporico()){
							evento = evento + " apórico";
						}
						else if(meton.get(0).isApoperico() && meton.get(0).isPerico()) {
							evento = evento + " périco";
						}
						
						evento = evento + " selecto";
					}
					
					
				}
				else if (meton.get(0).isInvertido()) {
					
					if(meton.get(0).isApofasal()) {
						evento = evento + " apofasal";
						
						if((meton.get(0).isFasal() && meton.get(0).isNuevo() || (meton.get(0).isApoperico() && meton.get(0).isPerico()))){
							evento = evento + " velado";
						}
						else if((meton.get(0).isFasal() && meton.get(0).isLleno() || meton.get(0).isApoperico() && meton.get(0).isAporico())){
							evento = evento + " tenue";
						}	
					}
					else {
						if(meton.get(0).isFasal() && meton.get(0).isNuevo()){
							evento = evento + " nuevo";
						}
						else if(meton.get(0).isFasal() && meton.get(0).isLleno()){
							evento = evento + " lleno";
						} 
						else if(meton.get(0).isApoperico() && meton.get(0).isAporico()){
							evento = evento + " apórico";
						}
						else if(meton.get(0).isApoperico() && meton.get(0).isPerico()) {
							evento = evento + " périco";
						}
						evento = evento + " invertido";
					}
					
				}
				else {
					
					if(meton.get(0).isFasal()) {
						
						if(meton.get(0).isNuevo()) {
							
							evento = evento + " nuevo";
						}
						else if(meton.get(0).isLleno()) {
									
							evento = evento + " lleno";
						}
					}
					else if (meton.get(0).isApoperico()) {
						
						if(meton.get(0).isAporico()) {
							
							evento = evento + " apórico";
						}
						else if(meton.get(0).isPerico()) {
									
							evento = evento + " périco";
						}
					}
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
			else if(midsison != null) {
				
				evento = "Midsison";
				String midsisonApellido = "";
				String midsisonLunar = "";
				
				switch(midsison.getLastSoeSeason()) {
				
					case 1:
						midsisonApellido = " invernal";
						break;
						
					case 2:
						midsisonApellido = " primaveral";
						break;
						
					case 3:
						midsisonApellido = " estival";
						break;
						
					case 4: 
						midsisonApellido = " otoñal";
						break;
				}
				
				if(luna != null) {
					if (luna.isNueva()) {
						if (!luna.isSelecta()){
							midsisonLunar = " nuevo";
						}
						else {
							midsisonLunar = " aponoval";
						}
					}
					else if (luna.isLlena()) {
						midsisonLunar = " lleno";
					}
				}
				evento = evento + midsisonApellido + midsisonLunar;
			}
			else if (luna != null) {

				if(luna.isNueva() && luna.isSelecta()) {
					evento = "Luna aponoval";
				}
				else {
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
			}
			else if (apoperi != null) {
				
				String estado = "";
				
				if(apoperi.isEsApogeo()) {
					estado = "distante";
				}
				else if (apoperi.isEsPerigeo()) {
					estado = "presente";
				}
				evento = evento + "Luna " + estado;
				
			}
			
		}

		return evento;
	}
}
