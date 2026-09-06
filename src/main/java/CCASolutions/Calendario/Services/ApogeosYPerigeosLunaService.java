package CCASolutions.Calendario.Services;

/**
 * EN: Manages the apogees and perigees of the moon (the "apoperis"), the farthest and
 * closest points of its orbit.
 * ES: Gestiona los apogeos y perigeos de la luna (los "apoperis"), los puntos más lejano
 * y más cercano de su órbita.
 */
public interface ApogeosYPerigeosLunaService {

	/**
	 * EN: Downloads from the OPALE API every apogee and perigee between years 1000 and
	 * 2100 and stores them.
	 * ES: Descarga de la API de OPALE todos los apogeos y perigeos entre los años 1000 y
	 * 2100 y los almacena.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateApogeosFromOpale();

	/**
	 * EN: Pairs every apogee or perigee with the moon phase falling within the same
	 * sidereal day and marks both. A full moon at perigee or a new moon at apogee is
	 * "selecta"; a full moon at apogee or a new moon at perigee is "invertida".
	 * ES: Empareja cada apogeo o perigeo con la fase lunar que cae dentro del mismo día
	 * sideral y marca a ambos. Luna llena en perigeo o luna nueva en apogeo es "selecta";
	 * luna llena en apogeo o luna nueva en perigeo es "invertida".
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String updateLunasYApoperisConSelectoOInvertido();
}
