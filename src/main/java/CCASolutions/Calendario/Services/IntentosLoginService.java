package CCASolutions.Calendario.Services;

public interface IntentosLoginService {

	public static final int MAX_INTENTOS = 3;
	public static final int MINUTOS_BLOQUEO = 15;

	/** Lanza DEMASIADOS_INTENTOS si la clave está bloqueada. */
	public abstract void comprobarBloqueo(String clave);

	/**
	 * Apunta un fallo y devuelve los intentos que quedan. Al llegar a 0 la clave
	 * queda bloqueada {@value #MINUTOS_BLOQUEO} minutos.
	 */
	public abstract int registrarFallo(String clave);

	public abstract void limpiar(String clave);

	public abstract void purgarCaducados();
}
