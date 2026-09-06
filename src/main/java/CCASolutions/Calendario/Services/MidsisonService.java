package CCASolutions.Calendario.Services;

/**
 * EN: Manages the midsisons, the instants exactly halfway between one solstice or
 * equinox and the next.
 * ES: Gestiona los midsisons, los instantes exactamente equidistantes entre un solsticio
 * o equinoccio y el siguiente.
 */
public interface MidsisonService {

	/**
	 * EN: Computes one midsison for each consecutive pair of solstices and equinoxes and
	 * marks whether it coincides with a moon phase, an apogee or perigee, or an eclipse.
	 * ES: Calcula un midsison por cada pareja consecutiva de solsticios y equinoccios y
	 * marca si coincide con una fase lunar, con un apogeo o perigeo, o con un eclipse.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	public abstract String poblateMidsison();
}
