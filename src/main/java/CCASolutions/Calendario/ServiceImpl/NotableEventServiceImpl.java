package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
			MetonsEntity metonActual = null;
			EclipsesEntity eclipseActual = null;
			EclipenosEntity eclipenoActual = null;
			MidsisonDTO midsisonActual = null;
			
			LunasEntity lunaPasado = null;
			ApogeosYPerigeosLunaEntity apoperiPasado = null;
			SolsticiosYEquinocciosEntity soePasado = null;
			MetonsEntity metonPasado = null;
			EclipsesEntity eclipsePasado = null;
			EclipenosEntity eclipenoPasado = null;
			MidsisonDTO midsisonPasado = null;
			
			LunasEntity lunaFuturo = null;
			ApogeosYPerigeosLunaEntity apoperiFuturo = null;
			SolsticiosYEquinocciosEntity soeFuturo = null;
			MetonsEntity metonFuturo = null;
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
					metonActual = new MetonsEntity();
					metonActual =meton;
				}		
				
				else if(meton.getDate().toLocalDate().isBefore(dateO)) {
					
					long diasDeDiferenciaEntreMetonoPasadoYDate = ChronoUnit.DAYS.between(meton.getDate().toLocalDate(), dateO);
					
					if(diasDeDiferenciaEntreMetonoPasadoYDate < diasMinimosDeDiferenciaEntreMetonoPasadoYDate) {
						diasMinimosDeDiferenciaEntreMetonoPasadoYDate = diasDeDiferenciaEntreMetonoPasadoYDate;
						metonPasado = new MetonsEntity();
						metonPasado = meton;
					}
					else if(diasDeDiferenciaEntreMetonoPasadoYDate == diasMinimosDeDiferenciaEntreMetonoPasadoYDate) {
						metonPasado = new MetonsEntity();
						metonPasado = meton;
					}
				}	
				else if(meton.getDate().toLocalDate().isAfter(dateO)) {
					
					long diasDeDiferenciaEntreMetonoFuturoYDate = ChronoUnit.DAYS.between(dateO, meton.getDate().toLocalDate());
					
					if(diasDeDiferenciaEntreMetonoFuturoYDate < diasMinimosDeDiferenciaEntreMetonoFuturoYDate) {
						diasMinimosDeDiferenciaEntreMetonoFuturoYDate = diasDeDiferenciaEntreMetonoFuturoYDate;
						metonFuturo = new MetonsEntity();
						metonFuturo= meton;
					}
					else if(diasDeDiferenciaEntreMetonoFuturoYDate == diasMinimosDeDiferenciaEntreMetonoFuturoYDate) {
						metonFuturo = new MetonsEntity();
						metonFuturo = meton;
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
				
				boolean esFasal = false;
				boolean esApoperico = false;
				
				if(lunaPasado != null && Math.abs(ChronoUnit.SECONDS.between(lunaPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setNuevo(lunaPasado.isNueva());
					midsisonPasado.setLleno(lunaPasado.isLlena());
					midsisonPasado.setSelecto(lunaPasado.isSelecta());
					midsisonPasado.setInvertido(lunaPasado.isInvertida());
					esFasal = true;
				}
				else if(lunaActual != null && Math.abs(ChronoUnit.SECONDS.between(lunaActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setNuevo(lunaActual.isNueva());
					midsisonPasado.setLleno(lunaActual.isLlena());
					midsisonPasado.setSelecto(lunaActual.isSelecta());
					midsisonPasado.setInvertido(lunaActual.isInvertida());
					esFasal = true;
				}
				else if(lunaFuturo != null && Math.abs(ChronoUnit.SECONDS.between(lunaFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setNuevo(lunaFuturo.isNueva());
					midsisonPasado.setLleno(lunaFuturo.isLlena());
					midsisonPasado.setSelecto(lunaFuturo.isSelecta());
					midsisonPasado.setInvertido(lunaFuturo.isInvertida());
					esFasal = true;
				}
				
				if(apoperiPasado != null && Math.abs(ChronoUnit.SECONDS.between(apoperiPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setAporico(apoperiPasado.isEsApogeo());
					midsisonPasado.setPerico(apoperiPasado.isEsPerigeo());
					midsisonPasado.setSelecto(apoperiPasado.isEsSelecto());
					midsisonPasado.setInvertido(apoperiPasado.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiActual != null && Math.abs(ChronoUnit.SECONDS.between(apoperiActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setAporico(apoperiActual.isEsApogeo());
					midsisonPasado.setPerico(apoperiActual.isEsPerigeo());
					midsisonPasado.setSelecto(apoperiActual.isEsSelecto());
					midsisonPasado.setInvertido(apoperiActual.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiFuturo != null && Math.abs(ChronoUnit.SECONDS.between(apoperiFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonPasado.setAporico(apoperiFuturo.isEsApogeo());
					midsisonPasado.setPerico(apoperiFuturo.isEsPerigeo());
					midsisonPasado.setSelecto(apoperiFuturo.isEsSelecto());
					midsisonPasado.setInvertido(apoperiFuturo.isEsInvertido());
					esApoperico = true;
				}
				
				if(esFasal && esApoperico) {
					midsisonPasado.setApofasal(true);
				}
				
				
				if( (eclipsePasado != null && Math.abs(ChronoUnit.SECONDS.between(eclipsePasado.getDate(), diaDelMidsison)) <= 86164)
					|| (eclipseActual != null && Math.abs(ChronoUnit.SECONDS.between(eclipseActual.getDate(), diaDelMidsison)) <= 86164)
					|| (eclipseFuturo != null && Math.abs(ChronoUnit.SECONDS.between(eclipseFuturo.getDate(), diaDelMidsison)) <= 86164)) {
					midsisonPasado.setEclipse(true);
				}
				
			}
			else if(diaDelMidsison.toLocalDate().isEqual(dateO)) {
				
				midsisonActual = new MidsisonDTO();
				midsisonActual.setDate(diaDelMidsison);	
				midsisonActual.setLastSoeSeason(soePasado.getStartingSeason());
				
				boolean esFasal = false;
				boolean esApoperico = false;
				
				if(lunaPasado != null && Math.abs(ChronoUnit.SECONDS.between(lunaPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setNuevo(lunaPasado.isNueva());
					midsisonActual.setLleno(lunaPasado.isLlena());
					midsisonActual.setSelecto(lunaPasado.isSelecta());
					midsisonActual.setInvertido(lunaPasado.isInvertida());
					esFasal = true;
				}
				else if(lunaActual != null && Math.abs(ChronoUnit.SECONDS.between(lunaActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setNuevo(lunaActual.isNueva());
					midsisonActual.setLleno(lunaActual.isLlena());
					midsisonActual.setSelecto(lunaActual.isSelecta());
					midsisonActual.setInvertido(lunaActual.isInvertida());
					esFasal = true;
				}
				else if(lunaFuturo != null && Math.abs(ChronoUnit.SECONDS.between(lunaFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setNuevo(lunaFuturo.isNueva());
					midsisonActual.setLleno(lunaFuturo.isLlena());
					midsisonActual.setSelecto(lunaFuturo.isSelecta());
					midsisonActual.setInvertido(lunaFuturo.isInvertida());
					esFasal = true;
				}
				
				if(apoperiPasado != null && Math.abs(ChronoUnit.SECONDS.between(apoperiPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setAporico(apoperiPasado.isEsApogeo());
					midsisonActual.setPerico(apoperiPasado.isEsPerigeo());
					midsisonActual.setSelecto(apoperiPasado.isEsSelecto());
					midsisonActual.setInvertido(apoperiPasado.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiActual != null && Math.abs(ChronoUnit.SECONDS.between(apoperiActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setAporico(apoperiActual.isEsApogeo());
					midsisonActual.setPerico(apoperiActual.isEsPerigeo());
					midsisonActual.setSelecto(apoperiActual.isEsSelecto());
					midsisonActual.setInvertido(apoperiActual.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiFuturo != null && Math.abs(ChronoUnit.SECONDS.between(apoperiFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonActual.setAporico(apoperiFuturo.isEsApogeo());
					midsisonActual.setPerico(apoperiFuturo.isEsPerigeo());
					midsisonActual.setSelecto(apoperiFuturo.isEsSelecto());
					midsisonActual.setInvertido(apoperiFuturo.isEsInvertido());
					esApoperico = true;
				}
				
				if(esFasal && esApoperico) {
					midsisonActual.setApofasal(true);
				}
				
				if( (eclipsePasado != null && Math.abs(ChronoUnit.SECONDS.between(eclipsePasado.getDate(), diaDelMidsison)) <= 86164)
						|| (eclipseActual != null && Math.abs(ChronoUnit.SECONDS.between(eclipseActual.getDate(), diaDelMidsison)) <= 86164)
						|| (eclipseFuturo != null && Math.abs(ChronoUnit.SECONDS.between(eclipseFuturo.getDate(), diaDelMidsison)) <= 86164)) {
						midsisonActual.setEclipse(true);
					}
				
			}
			else if (diaDelMidsison.toLocalDate().isAfter(dateO)) {
				
				midsisonFuturo = new MidsisonDTO();
				midsisonFuturo.setDate(diaDelMidsison);	
				midsisonFuturo.setLastSoeSeason(soePasado.getStartingSeason());
				
				boolean esFasal = false;
				boolean esApoperico = false;
				
				if(lunaPasado != null && Math.abs(ChronoUnit.SECONDS.between(lunaPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setNuevo(lunaPasado.isNueva());
					midsisonFuturo.setLleno(lunaPasado.isLlena());
					midsisonFuturo.setSelecto(lunaPasado.isSelecta());
					midsisonFuturo.setInvertido(lunaPasado.isInvertida());
					esFasal = true;
				}
				else if(lunaActual != null && Math.abs(ChronoUnit.SECONDS.between(lunaActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setNuevo(lunaActual.isNueva());
					midsisonFuturo.setLleno(lunaActual.isLlena());
					midsisonFuturo.setSelecto(lunaActual.isSelecta());
					midsisonFuturo.setInvertido(lunaActual.isInvertida());
					esFasal = true;
				}
				else if(lunaFuturo != null && Math.abs(ChronoUnit.SECONDS.between(lunaFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setNuevo(lunaFuturo.isNueva());
					midsisonFuturo.setLleno(lunaFuturo.isLlena());
					midsisonFuturo.setSelecto(lunaFuturo.isSelecta());
					midsisonFuturo.setInvertido(lunaFuturo.isInvertida());
					esFasal = true;
				}
				
				if(apoperiPasado != null && Math.abs(ChronoUnit.SECONDS.between(apoperiPasado.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setAporico(apoperiPasado.isEsApogeo());
					midsisonFuturo.setPerico(apoperiPasado.isEsPerigeo());
					midsisonFuturo.setSelecto(apoperiPasado.isEsSelecto());
					midsisonFuturo.setInvertido(apoperiPasado.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiActual != null && Math.abs(ChronoUnit.SECONDS.between(apoperiActual.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setAporico(apoperiActual.isEsApogeo());
					midsisonFuturo.setPerico(apoperiActual.isEsPerigeo());
					midsisonFuturo.setSelecto(apoperiActual.isEsSelecto());
					midsisonFuturo.setInvertido(apoperiActual.isEsInvertido());
					esApoperico = true;
				}
				else if(apoperiFuturo != null && Math.abs(ChronoUnit.SECONDS.between(apoperiFuturo.getDate(), diaDelMidsison)) <= 86164) {
					midsisonFuturo.setAporico(apoperiFuturo.isEsApogeo());
					midsisonFuturo.setPerico(apoperiFuturo.isEsPerigeo());
					midsisonFuturo.setSelecto(apoperiFuturo.isEsSelecto());
					midsisonFuturo.setInvertido(apoperiFuturo.isEsInvertido());
					esApoperico = true;
				}
				
				if(esFasal && esApoperico) {
					midsisonFuturo.setApofasal(true);
				}
				
				if( (eclipsePasado != null && Math.abs(ChronoUnit.SECONDS.between(eclipsePasado.getDate(), diaDelMidsison)) <= 86164)
						|| (eclipseActual != null && Math.abs(ChronoUnit.SECONDS.between(eclipseActual.getDate(), diaDelMidsison)) <= 86164)
						|| (eclipseFuturo != null && Math.abs(ChronoUnit.SECONDS.between(eclipseFuturo.getDate(), diaDelMidsison)) <= 86164)) {
						midsisonFuturo.setEclipse(true);
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
		MetonsEntity metonParaMetodo = LunasSoesEclipsesMetonosYEclipenosActualesProximasFuturas.getMetonoActual();
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
				
			  
		long minDias = Math.min(diasEntreMidsisonYDate, Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaAnterior() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeAnterior() : null;
		MidsisonDTO midsisonParaMetodo = diasEntreMidsisonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonAnterior() : null;
		MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoAnterior() : null;
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
			  
		long minDias = Math.min(diasEntreMidsisonYDate, Math.min(diasEntreApoperiYDate, Math.min(diasEntreLunaYDate, Math.min(diasEntreSOEYDate, Math.min(diasEntreMetonYDate, Math.min(diasEntreEclipseYDate, diasEntreEclipenoYDate))))));
			    
		LunasEntity lunaParaMetodo = diasEntreLunaYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getLunaProxima() : null;
		SolsticiosYEquinocciosEntity soeParaMetodo = diasEntreSOEYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getSoeProximo() : null;
		MidsisonDTO midsisonParaMetodo = diasEntreMidsisonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMidsisonProximo() : null;
		MetonsEntity metonParaMetodo = diasEntreMetonYDate == minDias ? lunasSolsticiosEclipsesMetonosYEclipenos.getMetonoProximo() : null;
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
	
	private String getNotableEventName(LunasEntity luna, SolsticiosYEquinocciosEntity soe, MetonsEntity meton, EclipsesEntity eclipse, EclipenosEntity eclipeno, ApogeosYPerigeosLunaEntity apoperi, MidsisonDTO midsison) {
		
		String evento = "";
		
				
		
		if(eclipeno != null) {
		
			evento = this.getEclipenoName(eclipeno);									
		}
		else if (meton != null) {
						
			evento = this.getMetonoName(meton);				
		}
		else if(soe != null) {
						
			evento = this.getSoeName(soe);
		}
		else if (eclipse != null) {				
										
			evento = this.getEclipseName(eclipse);
		}	
		else if(midsison != null) {
				
			evento = this.getMidsisonName(midsison);
		}
		else if (luna != null) {

			evento = this.getLunaName(luna);
		}
		else if (apoperi != null) {
				
			evento = this.getApoperiName(apoperi);				
		}
			
		return evento;
	}
	
	private String getEclipenoName(EclipenosEntity eclipeno) {
		
		String name = "";
		
		if (eclipeno.isInvernal()) {
			
			name = name + "Eclípeno invernal ";
		}
		else if(eclipeno.isPrimaveral()) {
					
			name = name + "Eclípeno primaveral ";
		}
		else if (eclipeno.isEstival()) {
					
			name = name + "Eclípeno estival ";
		}
		else if (eclipeno.isOtonyal()) {
					
			name = name + "Eclípeno otoñal ";
		}
								
		if(eclipeno.isApofasal()) {
			
			name = name + "apofasal ";
			
			if(eclipeno.isSelecto() && eclipeno.isNuevo()) {
				name = name + "remoto";
			}
			else if(eclipeno.isSelecto() && eclipeno.isLleno()) {
				name = name + "brillante";
			}
			else if(eclipeno.isInvertido() && eclipeno.isNuevo()) {
				name = name + "velado";
			}
			else if(eclipeno.isInvertido() && eclipeno.isLleno()) {
				name = name + "tenue";
			}
		}
		
		else {
			
			if(eclipeno.isNuevo()) {
				
				name = name + " nuevo";
			}
			else if(eclipeno.isLleno()) {
						
				name = name + " lleno";
			}
			if(eclipeno.isSelecto()) {
				
				name = name + " selecto";
			}
			else if(eclipeno.isInvertido()) {
				
				name = name + " invertido";
			}									
		}			
		
		return name;
	}
	
	private String getMetonoName(MetonsEntity meton) {
		
		String name = "";
		
		if (meton.isInvernal()) {
			
			name = name + "Métono invernal";
		}
		else if(meton.isPrimaveral()) {
					
			name = name + "Métono primaveral";
		}
		else if (meton.isEstival()) {
					
			name = name + "Métono estival";
		}
		else if (meton.isOtonyal()) {
					
			name = name + "Métono otoñal";
		}
		
		if(meton.isSelecto()) {
			
			if(meton.isApofasal()) {
				name = name + " apofasal";
				
				if((meton.isFasal() && meton.isNuevo() || (meton.isApoperico() && meton.isAporico()))){
					name = name + " remoto";
				}
				else if((meton.isFasal() && meton.isLleno() || meton.isApoperico() && meton.isPerico())){
					name = name + " brillante";
				}	
			}
			else {
				if(meton.isFasal() && meton.isNuevo()){
					name = name + " nuevo";
				}
				else if(meton.isFasal() && meton.isLleno()){
					name = name + " lleno";
				} 
				else if(meton.isApoperico() && meton.isAporico()){
					name = name + " apórico";
				}
				else if(meton.isApoperico() && meton.isPerico()) {
					name = name + " périco";
				}
				
				name = name + " selecto";
			}
			
			
		}
		else if (meton.isInvertido()) {
			
			if(meton.isApofasal()) {
				name = name + " apofasal";
				
				if((meton.isFasal() && meton.isNuevo() || (meton.isApoperico() && meton.isPerico()))){
					name = name + " velado";
				}
				else if((meton.isFasal() && meton.isLleno() || meton.isApoperico() && meton.isAporico())){
					name = name + " tenue";
				}	
			}
			else {
				if(meton.isFasal() && meton.isNuevo()){
					name = name + " nuevo";
				}
				else if(meton.isFasal() && meton.isLleno()){
					name = name + " lleno";
				} 
				else if(meton.isApoperico() && meton.isAporico()){
					name = name + " apórico";
				}
				else if(meton.isApoperico() && meton.isPerico()) {
					name = name + " périco";
				}
				name = name + " invertido";
			}
			
		}
		else {
			
			if(meton.isFasal()) {
				
				if(meton.isNuevo()) {
					
					name = name + " nuevo";
				}
				else if(meton.isLleno()) {
							
					name = name + " lleno";
				}
			}
			else if (meton.isApoperico()) {
				
				if(meton.isAporico()) {
					
					name = name + " apórico";
				}
				else if(meton.isPerico()) {
							
					name = name + " périco";
				}
			}
		}			
		
		return name;
	}
	
	private String getSoeName(SolsticiosYEquinocciosEntity soe) {
		
		String name = "";
		
		if(soe.isSolsticioInvierno()) {
			
			name = name + "Solsticio de invierno";
		}
		else if(soe.isEquinoccioPrimavera()) {
					
			name = name + "Equinoccio de primavera";
		}
		else if(soe.isSolsticioVerano()) {
					
			name = name + "Solsticio de verano";
		}
		else if (soe.isEquinoccioOtonyo()) {
					
			name = name + "Equinoccio de otoño";
		}
		
		return name;
	}
	
	private String getEclipseName (EclipsesEntity eclipse) {
		
		String name = "";
		
		if(eclipse.isDeLuna()) {
					
			name =  "Eclipse de luna";
		}
		else if (eclipse.isDeSol()) {
					
			name = "Eclipse de sol";
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
				
		name = name + fase;
		
		return name;
	}
	
	private String getMidsisonName(MidsisonDTO midsison) {
		
		String name = "Midsison";
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
		
		
		if(midsison.isApofasal()) {
			midsisonLunar = " apofasal";
			if (midsison.isNuevo() && midsison.isAporico()) {
				midsisonLunar = midsisonLunar + " remoto";
			}
			else if(midsison.isNuevo() && midsison.isPerico()) {
				midsisonLunar = midsisonLunar + " velado";
			}
			else if(midsison.isLleno() && midsison.isAporico()) {
				midsisonLunar = midsisonLunar + " tenue";
			}
			else if(midsison.isLleno() && midsison.isPerico()) {
				midsisonLunar = midsisonLunar + " brillante";
			}
		}
		else {
			if (midsison.isNuevo()) {
				midsisonLunar = " nuevo";							
			}
			else if (midsison.isLleno()) {
				midsisonLunar = " lleno";
			}
			else if (midsison.isAporico()) {
				midsisonLunar = " apórico";
			}
			else if (midsison.isPerico()) {
				midsisonLunar = " périco";
			}
			
			if(midsison.isSelecto()) {
				midsisonLunar = midsisonLunar + " selecto";
			}
			else if(midsison.isInvertido()) {
				midsisonLunar = midsisonLunar + " invertido";
			}
		}
		
		if(midsison.isEclipse()) {
			midsisonLunar = midsisonLunar + " eclipsado";
		}
		
		
		name = name + midsisonApellido + midsisonLunar;
		return name;
	}
	
	private String getLunaName(LunasEntity luna) {
		
		String name = "";
		
		if(luna.isNueva() && luna.isSelecta()) {
			name = "Luna aponoval";
		}
		else {
			if (luna.isNueva()) {
				name = "Luna nueva";
			} 
			else if (luna.isCuartoCreciente()) {
				name = "Luna cuarto creciente";
			} 
			else if (luna.isLlena()) {
				name = "Luna llena";
			} 
			else if (luna.isCuartoMenguante()) {
				name = "Luna cuarto menguante";
			}
			

			if(luna.isSelecta()) {
				name = name + " selecta";
			}
			else if(luna.isInvertida()) {
				name = name + " invertida";
			}
		}
		
		return name;
	}
	
	private String getApoperiName(ApogeosYPerigeosLunaEntity apoperi) {
		
		String name = "";
		
		String estado = "";
		String especial = "";
		
		if(apoperi.isEsApogeo()) {
			estado = "distante";
		}
		else if (apoperi.isEsPerigeo()) {
			estado = "presente";
		}
		
		if(apoperi.isEsSelecto()) {
			especial = " selecto";
		}
		else if(apoperi.isEsInvertido()) {
			especial = " invertido";
		}
		
		name = "Luna " + estado + especial;
		
		return name;
	}
}
