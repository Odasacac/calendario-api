package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;

import CCASolutions.Calendario.DTOs.SeasonDTO;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/**
 * EN: Manages the four VAU seasons and which part of the season a date belongs to.
 * ES: Gestiona las cuatro estaciones VAU y a qué parte de la estación pertenece una fecha.
 */
public interface SeasonsService {

	/**
	 * EN: Inserts the five fixed season rows if the table is empty.
	 * ES: Inserta las cinco filas fijas de estaciones si la tabla está vacía.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateSeasons();

	/**
	 * EN: Works out the season a date belongs to and its qualifier: "iniciante" before the
	 * midsison, "cenítico" on the midsison itself and "terminante" after it. The midsison
	 * is the halfway point between the previous and the next solstice or equinox.
	 * ES: Calcula la estación a la que pertenece una fecha y su apellido: "iniciante" antes
	 * del midsison, "cenítico" el propio día del midsison y "terminante" después. El
	 * midsison es el punto medio entre el solsticio o equinoccio anterior y el siguiente.
	 *
	 * @param date    EN: date being consulted. / ES: fecha que se consulta.
	 * @param allSoes EN: solstices and equinoxes around that date. / ES: solsticios y equinoccios alrededor de esa fecha.
	 * @return EN: name and qualifier of the season. / ES: nombre y apellido de la estación.
	 */
	public abstract SeasonDTO getVAUSeason(LocalDate date, List<SolsticiosYEquinocciosEntity> allSoes);
}
