package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Services.CasalerosService;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.EclipsesService;
import CCASolutions.Calendario.Services.FestividadesService;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.MonthService;
import CCASolutions.Calendario.Services.NotableEventService;
import CCASolutions.Calendario.Services.SeasonsService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;
import CCASolutions.Calendario.Services.UtilsService;

/**
 * EN: Entry point of the conversion. Loads every phenomenon once and then hands it to each
 * specialised service, so a whole VAU date is built with a single trip to the database.
 * ES: Punto de entrada de la conversión. Carga todos los fenómenos una sola vez y se los
 * pasa a cada servicio especializado, de modo que una fecha VAU completa se construye con
 * un único viaje a la base de datos.
 */
@Service
@Transactional(readOnly = true)
public class DatesServiceImpl implements DatesService {

	@Autowired
	private LunasService lunasService;

	@Autowired
	private NotableEventService notableEventService;

	@Autowired
	private FestividadesService festividadesService;

	@Autowired
	private CasalerosService casalerosService;

	@Autowired
	private EclipsesService eclipsesService;

	@Autowired
	private EclipenosService eclipenosService;

	@Autowired
	private MetonsService metonsService;

	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;

	@Autowired
	private MonthService monthService;

	@Autowired
	private UtilsService utilsService;

	@Autowired
	private SeasonsService seasonsService;


	/**
	 * EN: Converts a Gregorian date into its VAU equivalent. First gathers the astronomical
	 * data around the date; if the data is not complete enough, returns a DTO carrying the
	 * reason instead of a conversion.
	 * ES: Convierte una fecha gregoriana en su equivalente VAU. Primero recoge los datos
	 * astronómicos del entorno de la fecha; si esos datos no son suficientes, devuelve un DTO
	 * con el motivo en lugar de una conversión.
	 *
	 * @param date EN: Gregorian date to convert. / ES: fecha gregoriana a convertir.
	 * @return EN: the VAU date, always with the original date filled in. / ES: la fecha VAU, siempre con la fecha original rellena.
	 */
	public DateDTO getDateVAUFromDateO (LocalDate date) {

		DateDTO dateVAU = new DateDTO();

		DatosCosmicosParaVAUDTO datosCosmicos = this.utilsService.getDatosCosmicos(date);

		if(datosCosmicos.isValido()) {

			dateVAU = this.getDateVAU(date, datosCosmicos);
		}
		else {

			dateVAU.setMensaje(datosCosmicos.getMensaje());
		}

		dateVAU.setFechaO(String.valueOf(date));

		return dateVAU;
	}


	/**
	 * EN: Fills in every piece of a VAU date, in order. The order matters: the absolute
	 * eclipses read the eclipeno and meton counters that were computed just above them.
	 * ES: Rellena, en orden, cada pieza de una fecha VAU. El orden importa: los eclipses
	 * absolutos leen los contadores de eclípeno y de métono que se han calculado justo antes.
	 *
	 * @param date                    EN: date being converted. / ES: fecha que se está convirtiendo.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: the complete VAU date. / ES: la fecha VAU completa.
	 */
	private DateDTO getDateVAU(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		DateDTO dateVAU = new DateDTO();

		dateVAU.setYear(this.solsticiosYEquinocciosService.getVAUYear(datosCosmicosParaVAUDTO.getLastEclipenoIN(), date, datosCosmicosParaVAUDTO.getSoes(), datosCosmicosParaVAUDTO.getLastMetonIN()));
		dateVAU.setSeason(this.seasonsService.getVAUSeason(date, datosCosmicosParaVAUDTO.getSoes()));
		dateVAU.setMonth(this.monthService.getVAUMonth(date, datosCosmicosParaVAUDTO.getSoes(), datosCosmicosParaVAUDTO.getLunas()));

		VAUWeekAndDayDTO vauWeekAndDay = this.lunasService.getVauWeekAndDay(date, datosCosmicosParaVAUDTO.getLunas(), datosCosmicosParaVAUDTO.getSoes());
		dateVAU.setWeek(vauWeekAndDay.getWeek());
		dateVAU.setDay(vauWeekAndDay.getDay());

		dateVAU.setMetonoVAU(this.metonsService.getVAUMeton(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto(), datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), datosCosmicosParaVAUDTO.getMetons(), date));
		dateVAU.setEclipenoVAU(this.eclipenosService.getVAUEclipeno(datosCosmicosParaVAUDTO.getEclipenos(), datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), date));
		dateVAU.setLastEclipenoSelecto(this.eclipenosService.getVAUEclipenoSelecto(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), date));
		dateVAU.setMetonoInvernalApofasalRemoto(this.metonsService.getMetonoInvernalApofasalRemoto(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto(), datosCosmicosParaVAUDTO.getMetons(), date));
		dateVAU.setAbsoluteEclipses(this.eclipsesService.getVAUAbsoluteEclipses(dateVAU, datosCosmicosParaVAUDTO.getEclipses(), date, datosCosmicosParaVAUDTO.getLastMetonIN()));

		dateVAU.setEstadoLuna(this.lunasService.getEstadoLuna(date, datosCosmicosParaVAUDTO.getApoperis()));
		dateVAU.setAponovos(this.lunasService.getAponovos(date, datosCosmicosParaVAUDTO));
		dateVAU.setCasalero(this.casalerosService.getCasalero(datosCosmicosParaVAUDTO.getLastEclipenoIN()));

		dateVAU.setNotableEvent(this.notableEventService.getNotableEvent(date, datosCosmicosParaVAUDTO));
		dateVAU.setFestividades(this.festividadesService.getFestividades(date, datosCosmicosParaVAUDTO));

		dateVAU.setFechaEncontrada(true);

		return dateVAU;
	}
}
