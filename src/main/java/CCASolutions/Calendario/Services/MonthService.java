package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/**
 * EN: Manages the VAU months. Each season holds three ordinary months plus a hybrid one
 * spanning the change of season, and there is a liminal month for the stretch between the
 * winter solstice and the first new moon after it.
 * ES: Gestiona los meses VAU. Cada estación tiene tres meses corrientes más uno híbrido
 * que abarca el cambio de estación, y existe un mes liminal para el tramo entre el
 * solsticio de invierno y la primera luna nueva posterior.
 */
public interface MonthService {

	/**
	 * EN: Inserts the eighteen fixed month rows if the table is empty.
	 * ES: Inserta las dieciocho filas fijas de meses si la tabla está vacía.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateMonths();

	/**
	 * EN: Works out the VAU month of a date by counting the new moons elapsed since the
	 * previous solstice or equinox. A date falling on a new moon belongs to no month;
	 * dates in the stretch between the last new moon and the next solstice, or between a
	 * solstice and the first new moon, belong to the hybrid month.
	 * ES: Calcula el mes VAU de una fecha contando las lunas nuevas transcurridas desde el
	 * solsticio o equinoccio anterior. Una fecha que cae en luna nueva no pertenece a
	 * ningún mes; las fechas del tramo entre la última luna nueva y el siguiente solsticio,
	 * o entre un solsticio y la primera luna nueva, pertenecen al mes híbrido.
	 *
	 * @param date                                          EN: date being consulted. / ES: fecha que se consulta.
	 * @param soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas  EN: solstices and equinoxes around the date. / ES: solsticios y equinoccios alrededor de la fecha.
	 * @param lunasDesdeElAnyoAnteriorHastaElSiguiente       EN: moon phases around the date. / ES: fases lunares alrededor de la fecha.
	 * @return EN: name of the month and its qualifier. / ES: nombre del mes y su apellido.
	 */
	public abstract MonthDTO getVAUMonth (LocalDate date, List<SolsticiosYEquinocciosEntity> soesDesdeElAnyoAnteriorAlMetonoHastaUnAnyoMas, List<LunasEntity> lunasDesdeElAnyoAnteriorHastaElSiguiente);
}
