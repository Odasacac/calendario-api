package CCASolutions.Calendario.Services;

import java.time.LocalDate;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;

/**
 * EN: Works out which astronomical event stands out around a given date.
 * ES: Averigua qué evento astronómico destaca alrededor de una fecha dada.
 */
public interface NotableEventService {

	/**
	 * EN: Returns the event falling on the date itself, the closest previous one and the
	 * closest upcoming one, each already named. When several phenomena tie on the same
	 * day the most exceptional one wins: eclipeno, meton, solstice or equinox, eclipse,
	 * midsison, moon phase and finally apogee or perigee.
	 * ES: Devuelve el evento que cae en la propia fecha, el anterior más cercano y el
	 * próximo más cercano, cada uno ya con su nombre. Cuando varios fenómenos empatan el
	 * mismo día gana el más excepcional: eclípeno, métono, solsticio o equinoccio,
	 * eclipse, midsison, fase lunar y por último apogeo o perigeo.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: today's, the previous and the next notable event. / ES: el evento notable de hoy, el anterior y el siguiente.
	 */
	public abstract NotableEventDTO getNotableEvent(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO);
}
