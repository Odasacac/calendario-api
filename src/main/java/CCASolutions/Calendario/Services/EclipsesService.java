package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;

/**
 * EN: Manages solar and lunar eclipses.
 * ES: Gestiona los eclipses solares y lunares.
 */
public interface EclipsesService {

	/**
	 * EN: Downloads from the OPALE API every solar and lunar eclipse from year -4700 to
	 * 2100 and stores them.
	 * ES: Descarga de la API de OPALE todos los eclipses solares y lunares del año -4700 al
	 * 2100 y los almacena.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateEclipsesFromOpale();

	/**
	 * EN: Counts the absolute eclipses (solar ones, and total lunar ones) that have gone by
	 * since the reference eclipeno and since the last winter new meton, split into solar,
	 * lunar and total.
	 * ES: Cuenta los eclipses absolutos (los solares, y los lunares totales) que han pasado
	 * desde el eclípeno de referencia y desde el último métono invernal nuevo, desglosados
	 * en solares, lunares y totales.
	 *
	 * @param dateVAU                              EN: VAU date being built; its eclipeno and meton counters are already filled in. / ES: fecha VAU en construcción; sus contadores de eclípeno y métono ya están rellenos.
	 * @param eclipsesAbsolutosDesdeLastEclipenoIN EN: absolute eclipses in range. / ES: eclipses absolutos del rango.
	 * @param date                                 EN: date being consulted. / ES: fecha que se consulta.
	 * @param lastMetonIN                          EN: last winter new meton. / ES: último métono invernal nuevo.
	 * @return EN: the six eclipse counters. / ES: los seis contadores de eclipses.
	 */
	public abstract AbsoluteEclipsesDTO getVAUAbsoluteEclipses(DateDTO dateVAU, List<EclipsesEntity> eclipsesAbsolutosDesdeLastEclipenoIN, LocalDate date, MetonsEntity lastMetonIN);
}
