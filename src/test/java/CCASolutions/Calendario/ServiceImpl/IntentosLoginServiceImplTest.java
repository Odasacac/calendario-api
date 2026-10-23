package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import CCASolutions.Calendario.Exceptions.UsuarioException;

class IntentosLoginServiceImplTest {

	/** Reloj que se puede adelantar a mano. */
	private static class RelojMovil extends Clock {
		Instant ahora = Instant.parse("2026-10-23T10:00:00Z");
		void avanzar(Duration d) { this.ahora = this.ahora.plus(d); }
		@Override public ZoneId getZone() { return ZoneOffset.UTC; }
		@Override public Clock withZone(ZoneId zone) { return this; }
		@Override public Instant instant() { return this.ahora; }
	}

	private RelojMovil reloj;
	private IntentosLoginServiceImpl servicio;

	@BeforeEach
	void preparar() {
		this.reloj = new RelojMovil();
		this.servicio = new IntentosLoginServiceImpl();
		ReflectionTestUtils.setField(this.servicio, "reloj", this.reloj);
	}

	@Test
	void bloqueaAlTercerFallo() {

		assertEquals(2, this.servicio.registrarFallo("u:1"));
		assertEquals(1, this.servicio.registrarFallo("u:1"));
		assertDoesNotThrow(() -> this.servicio.comprobarBloqueo("u:1"));
		assertEquals(0, this.servicio.registrarFallo("u:1"));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.comprobarBloqueo("u:1"));
		assertEquals(UsuarioException.DEMASIADOS_INTENTOS, e.getCodigo());
		assertEquals(15L, e.getDetalles().get("minutosRestantes"));
	}

	@Test
	void elBloqueoCaducaALos15Minutos() {

		for(int i = 0; i < 3; i++) {
			this.servicio.registrarFallo("u:1");
		}
		this.reloj.avanzar(Duration.ofMinutes(15));

		assertDoesNotThrow(() -> this.servicio.comprobarBloqueo("u:1"));
		assertEquals(2, this.servicio.registrarFallo("u:1"));
	}

	@Test
	void unLoginCorrectoReiniciaElContador() {

		this.servicio.registrarFallo("u:1");
		this.servicio.registrarFallo("u:1");
		this.servicio.limpiar("u:1");

		assertEquals(2, this.servicio.registrarFallo("u:1"));
	}

	@Test
	void lasClavesSonIndependientes() {

		for(int i = 0; i < 3; i++) {
			this.servicio.registrarFallo("u:1");
		}
		assertDoesNotThrow(() -> this.servicio.comprobarBloqueo("u:2"));
	}
}
