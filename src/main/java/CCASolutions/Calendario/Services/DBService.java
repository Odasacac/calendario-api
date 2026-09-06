package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;

/**
 * EN: Runs the whole database population process in the right order.
 * ES: Ejecuta el proceso completo de poblado de la base de datos en el orden correcto.
 */
public interface DBService {

	/**
	 * EN: Chains every population phase (base data, moon phases, apogees and perigees,
	 * solstices, eclipses, midsisons, metons, eclipenos, casaleros and reference tables)
	 * according to the flags received, and returns a report of what each phase did.
	 * ES: Encadena todas las fases del poblado (datos base, fases lunares, apogeos y
	 * perigeos, solsticios, eclipses, midsisons, métonos, eclípenos, casaleros y tablas de
	 * referencia) según las banderas recibidas, y devuelve un informe de lo que ha hecho
	 * cada fase.
	 *
	 * @param poblateDBDTO EN: flags choosing which phases to run. / ES: banderas que eligen qué fases se ejecutan.
	 * @return EN: a multi-line report, one line per phase. / ES: un informe de varias líneas, una por fase.
	 */
	public abstract String poblateDB(PoblateDBDTO poblateDBDTO);
}
