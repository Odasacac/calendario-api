package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.YearDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;


/**
 * EN: Manages the solstices and equinoxes (the "soes"), which mark the VAU seasons and
 * the change of year.
 * ES: Gestiona los solsticios y equinoccios (los "soes"), que marcan las estaciones VAU y
 * el cambio de año.
 */
public interface SolsticiosYEquinocciosService {

	/**
	 * EN: Downloads from the OPALE API every solstice and equinox from year -4700 to 2100
	 * and stores them.
	 * ES: Descarga de la API de OPALE todos los solsticios y equinoccios del año -4700 al
	 * 2100 y los almacena.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateSolsticiosYEquinocciosFromOpale();

	/**
	 * EN: Single call to the OPALE API asking for the solstices and equinoxes of one year.
	 * ES: Llamada única a la API de OPALE pidiendo los solsticios y equinoccios de un año.
	 *
	 * @param anyo EN: year to request. / ES: año que se solicita.
	 * @param url  EN: URL template carrying the year and count placeholders. / ES: plantilla de URL con los marcadores de año y de número de días.
	 * @return EN: the phenomena of that year, empty if the call fails. / ES: los fenómenos de ese año, vacío si la llamada falla.
	 */
	public abstract List<FenomenoDTO> getSolsticiosYEquinocciosDelAnyoViaAPI(String anyo, String url);

	/**
	 * EN: Works out the VAU year, which is the number of winter solstices gone by since
	 * the reference meton. A date falling exactly on a winter solstice belongs to no year.
	 * ES: Calcula el año VAU, que es el número de solsticios de invierno transcurridos
	 * desde el métono de referencia. Una fecha que cae justo en un solsticio de invierno no
	 * pertenece a ningún año.
	 *
	 * @param lastEclipenoIN                                EN: last winter new eclipeno. / ES: último eclípeno invernal nuevo.
	 * @param date                                          EN: date being consulted. / ES: fecha que se consulta.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas EN: solstices and equinoxes in range. / ES: solsticios y equinoccios del rango.
	 * @param lastMetonIN                                   EN: last winter new meton. / ES: último métono invernal nuevo.
	 * @return EN: the VAU year and whether the date is a winter solstice. / ES: el año VAU y si la fecha es solsticio de invierno.
	 */
	public abstract YearDTO getVAUYear(EclipenosEntity lastEclipenoIN, LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, MetonsEntity lastMetonIN);
}
