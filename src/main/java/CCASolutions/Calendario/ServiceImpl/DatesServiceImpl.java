package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Services.CasaleroService;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.Services.FestividadesService;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.NotableEventService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;
import CCASolutions.Calendario.Services.UtilsService;

@Service
public class DatesServiceImpl implements DatesService {	
	
	@Autowired
	private LunasService lunasService;
	
	@Autowired
	private NotableEventService notableEventService;
	
	@Autowired
	private FestividadesService festividadesService;
	
	@Autowired
	private CasaleroService casaleroService;
	
	@Autowired
	private EclipsesService eclipsesService;
	
	@Autowired
	private EclipenosService eclipenosService;
	
	@Autowired
	private MetonsService metonsService;
	
	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Autowired
	private UtilsService utilsService;
	
	
	
	public DateDTO getDateVAUFromDateO (LocalDate date) {
		
		DateDTO dateVAU = null;
		
		DatosCosmicosParaVAUDTO lunasSolsticiosEclipsesMetonosYEclipenos = this.utilsService.getDatosCosmicos(date);
		
		if(lunasSolsticiosEclipsesMetonosYEclipenos.isValido()) {
			dateVAU = this.getDateVAU(date, lunasSolsticiosEclipsesMetonosYEclipenos);
		}
		else {
			dateVAU = new DateDTO();
			dateVAU.setMensaje(lunasSolsticiosEclipsesMetonosYEclipenos.getMensaje());
		}
		dateVAU.setFechaO(String.valueOf(date));
		
		return dateVAU;
		
	}
	

	private DateDTO getDateVAU(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
		
		DateDTO dateVAU= new DateDTO();
		
		dateVAU.setYear(this.solsticiosYEquinocciosService.getVAUYear(datosCosmicosParaVAUDTO.getLastEclipenoIN(), date, datosCosmicosParaVAUDTO.getSoes(), datosCosmicosParaVAUDTO.getLastMetonIN()));					
		dateVAU.setMonth(this.lunasService.getVAUMonth(date, datosCosmicosParaVAUDTO.getSoes(), datosCosmicosParaVAUDTO.getLunas()));
		
		VAUWeekAndDayDTO vauWeekAndDay = this.lunasService.getVauWeekAndDay(date, datosCosmicosParaVAUDTO.getLunas());
		dateVAU.setWeek(vauWeekAndDay.getWeek());
		dateVAU.setDay(vauWeekAndDay.getDay());					
		dateVAU.setLastEclipenoSelecto(this.eclipenosService.getVAUEclipenoSelecto(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), date));
		dateVAU.setMetonoInvernalApofasalRemoto(this.metonsService.getMetonoInvernalApofasalRemoto(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), datosCosmicosParaVAUDTO.getMetons(), date));
		
		dateVAU.setMetonoVAU(this.metonsService.getVAUMeton(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto(), datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), datosCosmicosParaVAUDTO.getMetons(), date));
		
		dateVAU.setEclipenoVAU(this.eclipenosService.getVAUEclipeno(datosCosmicosParaVAUDTO.getEclipenos(), datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), date));			
		dateVAU.setAbsoluteEclipses(this.eclipsesService.getVAUAbsoluteEclipses(dateVAU, datosCosmicosParaVAUDTO.getEclipses(), date, datosCosmicosParaVAUDTO.getLastMetonIN()));
		dateVAU.setCasalero(this.casaleroService.getCasalero(datosCosmicosParaVAUDTO.getLastEclipenoIN()));
		dateVAU.setEstadoLuna(this.lunasService.getEstadoLuna(date, datosCosmicosParaVAUDTO.getApoperis()));	
		dateVAU.setAponovos(this.lunasService.getAponovos(date, datosCosmicosParaVAUDTO));
		
		dateVAU.setNotableEvent(this.notableEventService.getNotableEvent(date, datosCosmicosParaVAUDTO));		
		dateVAU.setFestividades(this.festividadesService.getFestividades(date, datosCosmicosParaVAUDTO));
		
		dateVAU.setFechaEncontrada(true);
		
		return dateVAU;
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	

	
	
	
	

	

	
	
	
	
	
}







