package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;

/**
 * EN: Loads, in one go, every astronomical phenomenon the conversion of a date needs.
 * ES: Carga de una sola vez todos los fenómenos astronómicos que necesita la conversión
 * de una fecha.
 */
public interface UtilsService {

	/**
	 * EN: Reads from the database the moon phases, solstices and equinoxes, eclipses,
	 * apogees and perigees, metons, eclipenos and midsisons surrounding the given date,
	 * plus the reference phenomena the VAU counters hang from. Every later calculation
	 * works in memory over this result, without touching the database again.
	 * ES: Lee de la base de datos las fases lunares, solsticios y equinoccios, eclipses,
	 * apogeos y perigeos, métonos, eclípenos y midsisons que rodean a la fecha dada, junto
	 * con los fenómenos de referencia de los que cuelgan los contadores VAU. Todos los
	 * cálculos posteriores trabajan en memoria sobre este resultado, sin volver a la base
	 * de datos.
	 *
	 * @param date EN: date the data is gathered around. / ES: fecha alrededor de la cual se recogen los datos.
	 * @return EN: the data set; check {@code isValido()} before using it. / ES: el conjunto de datos; comprobar {@code isValido()} antes de usarlo.
	 */
	public abstract DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date);
}
