package CCASolutions.Calendario.Services;


import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.DTOs.VAUWeekAndDayDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;


/**
 * EN: Everything the moon drives in the VAU calendar: the week and the day (which count
 * from the last new moon), the aponovos and the state of the moon.
 * ES: Todo lo que la luna determina en el calendario VAU: la semana y el día (que se
 * cuentan desde la última luna nueva), los aponovos y el estado de la luna.
 */
public interface LunasService {

	/**
	 * EN: Downloads every moon phase from year -4700 to 2100 from the OPALE API and stores
	 * them. Years after 1 go into the working table; the whole range goes into the
	 * historical table.
	 * ES: Descarga de la API de OPALE todas las fases lunares del año -4700 al 2100 y las
	 * almacena. Los años posteriores al 1 van a la tabla de trabajo; el rango completo va
	 * a la tabla histórica.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateLunasFromOpale();

	/**
	 * EN: Single call to the OPALE API asking for the moon phases of one year.
	 * ES: Llamada única a la API de OPALE pidiendo las fases lunares de un año.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template carrying the year placeholder. / ES: plantilla de URL con el marcador del año.
	 * @return EN: the phases of that year, empty if the call fails. / ES: las fases de ese año, vacío si la llamada falla.
	 */
	public abstract List<LunarPhaseDTO> getFasesLunaresDelAnyoViaAPI(String anyo, String url);

	/**
	 * EN: Counts the aponovos. An aponovo is a new moon at apogee ("selecta"); the DTO says
	 * how many have gone by since the reference meton and how many ordinary new moons have
	 * passed since the last one.
	 * ES: Cuenta los aponovos. Un aponovo es una luna nueva en apogeo ("selecta"); el DTO
	 * indica cuántos han pasado desde el métono de referencia y cuántas lunas nuevas
	 * corrientes han pasado desde el último.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: aponovo number and month within the aponovo. / ES: número de aponovo y mes dentro del aponovo.
	 */
	public abstract AponovosDTO getAponovos(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);

	/**
	 * EN: Describes whether the moon is drawing closer to or moving away from the Earth,
	 * and for how many days, based on the last apogee or perigee before the date.
	 * ES: Describe si la luna se está acercando o alejando de la Tierra, y desde hace
	 * cuántos días, a partir del último apogeo o perigeo anterior a la fecha.
	 *
	 * @param date        EN: date being consulted. / ES: fecha que se consulta.
	 * @param allApoperis EN: apogees and perigees around that date. / ES: apogeos y perigeos alrededor de esa fecha.
	 * @return EN: the described behaviour of the moon. / ES: el comportamiento descrito de la luna.
	 */
	public abstract EstadoLunaDTO getEstadoLuna(LocalDate date, List<ApogeosYPerigeosLunaEntity> allApoperis);

	/**
	 * EN: Works out the VAU week and day. Both count from the last new moon: days 0 to 7
	 * are the first week, 8 to 14 the second, and so on. A day that also falls on a
	 * solstice or equinox is marked as "desdoblado" (split).
	 * ES: Calcula la semana y el día VAU. Ambos se cuentan desde la última luna nueva: los
	 * días del 0 al 7 son la primera semana, del 8 al 14 la segunda, y así sucesivamente.
	 * Un día que además cae en solsticio o equinoccio se marca como "desdoblado".
	 *
	 * @param date                                         EN: date being consulted. / ES: fecha que se consulta.
	 * @param lunasNuevasDesdeElAnyoAnteriorHasElSiguiente  EN: moon phases around the date. / ES: fases lunares alrededor de la fecha.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas EN: solstices and equinoxes around the date. / ES: solsticios y equinoccios alrededor de la fecha.
	 * @return EN: names of the VAU week and day. / ES: nombres de la semana y el día VAU.
	 */
	public abstract VAUWeekAndDayDTO getVauWeekAndDay(LocalDate date, List<LunasEntity> lunasNuevasDesdeElAnyoAnteriorHasElSiguiente, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas);

}
