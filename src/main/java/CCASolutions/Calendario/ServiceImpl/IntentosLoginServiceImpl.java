package CCASolutions.Calendario.ServiceImpl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Services.IntentosLoginService;

/**
 * Contador de intentos fallidos en memoria. La clave es "u:{id}" para cuentas
 * existentes (así correo y código de usuario comparten contador) o "i:{texto}"
 * para identificadores que no existen, de modo que la respuesta no delata si
 * una cuenta existe. Se pierde al reiniciar el servidor.
 */
@Service
public class IntentosLoginServiceImpl implements IntentosLoginService {

	private static final Duration BLOQUEO = Duration.ofMinutes(MINUTOS_BLOQUEO);

	private record Registro(int fallos, Instant ultimoFallo, Instant bloqueadoHasta) {}

	private final Map<String, Registro> registros = new ConcurrentHashMap<>();

	@Autowired
	private Clock reloj;

	@Override
	public void comprobarBloqueo(String clave) {

		Registro registro = this.vigente(clave);
		if(registro != null && registro.bloqueadoHasta() != null) {

			long segundos = Duration.between(Instant.now(this.reloj), registro.bloqueadoHasta()).toSeconds();
			throw new UsuarioException(HttpStatus.TOO_MANY_REQUESTS, UsuarioException.DEMASIADOS_INTENTOS,
					"Demasiados intentos fallidos. Vuelve a intentarlo más tarde.")
					.conDetalle("minutosRestantes", Math.max(1, (segundos + 59) / 60));
		}
	}

	@Override
	public int registrarFallo(String clave) {

		Instant ahora = Instant.now(this.reloj);
		Registro registro = this.registros.compute(clave, (k, anterior) -> {

			int fallos = (this.caducado(anterior, ahora) ? 0 : anterior.fallos()) + 1;
			Instant bloqueadoHasta = fallos >= MAX_INTENTOS ? ahora.plus(BLOQUEO) : null;
			return new Registro(fallos, ahora, bloqueadoHasta);
		});

		return Math.max(0, MAX_INTENTOS - registro.fallos());
	}

	@Override
	public void limpiar(String clave) {
		this.registros.remove(clave);
	}

	@Override
	public void purgarCaducados() {

		Instant ahora = Instant.now(this.reloj);
		this.registros.entrySet().removeIf(entrada -> this.caducado(entrada.getValue(), ahora));
	}

	private Registro vigente(String clave) {

		Registro registro = this.registros.get(clave);
		if(this.caducado(registro, Instant.now(this.reloj))) {
			this.registros.remove(clave);
			return null;
		}
		return registro;
	}

	/** Un bloqueo vencido, o fallos sueltos de hace más de 15 minutos, se olvidan. */
	private boolean caducado(Registro registro, Instant ahora) {

		if(registro == null) {
			return true;
		}
		if(registro.bloqueadoHasta() != null) {
			return !ahora.isBefore(registro.bloqueadoHasta());
		}
		return !ahora.isBefore(registro.ultimoFallo().plus(BLOQUEO));
	}
}
