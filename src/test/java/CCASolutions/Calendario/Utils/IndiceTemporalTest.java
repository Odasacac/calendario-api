package CCASolutions.Calendario.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * El índice sustituye a los bucles anidados que cruzaban tablas completas, así que
 * se compara contra la búsqueda por fuerza bruta que hacía el código original.
 */
class IndiceTemporalTest {

	private static final LocalDateTime ORIGEN = LocalDateTime.of(2000, 1, 1, 0, 0);

	private LocalDateTime enSegundo(long segundo) {
		return ORIGEN.plusSeconds(segundo);
	}

	private List<LocalDateTime> fuerzaBruta(List<LocalDateTime> elementos, LocalDateTime centro, long tolerancia) {

		List<LocalDateTime> esperados = new ArrayList<>();

		for (LocalDateTime elemento : elementos) {

			if (Math.abs(java.time.temporal.ChronoUnit.SECONDS.between(elemento, centro)) <= tolerancia) {
				esperados.add(elemento);
			}
		}

		Collections.sort(esperados);

		return esperados;
	}

	@Test
	void laVentanaCoincideConLaBusquedaLineal() {

		Random random = new Random(20260906L);

		List<LocalDateTime> elementos = new ArrayList<>();

		for (int i = 0; i < 2000; i++) {
			elementos.add(this.enSegundo(random.nextInt(5_000_000)));
		}

		IndiceTemporal<LocalDateTime> indice = IndiceTemporal.de(elementos, fecha -> fecha);

		for (int i = 0; i < 500; i++) {

			LocalDateTime centro = this.enSegundo(random.nextInt(5_000_000));
			long tolerancia = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

			assertEquals(this.fuerzaBruta(elementos, centro, tolerancia), indice.enVentana(centro, tolerancia),
					"La ventana debe devolver exactamente los mismos elementos que el recorrido lineal");
		}
	}

	@Test
	void laVentanaIncluyeLosExtremos() {

		List<LocalDateTime> elementos = List.of(this.enSegundo(0), this.enSegundo(100), this.enSegundo(200), this.enSegundo(201));

		IndiceTemporal<LocalDateTime> indice = IndiceTemporal.de(elementos, fecha -> fecha);

		assertEquals(List.of(this.enSegundo(0), this.enSegundo(100), this.enSegundo(200)), indice.enVentana(this.enSegundo(100), 100));
	}

	@Test
	void laVentanaDevuelveTodosLosRepetidos() {

		List<LocalDateTime> elementos = List.of(this.enSegundo(50), this.enSegundo(50), this.enSegundo(50), this.enSegundo(500));

		IndiceTemporal<LocalDateTime> indice = IndiceTemporal.de(elementos, fecha -> fecha);

		assertEquals(3, indice.enVentana(this.enSegundo(50), 10).size());
	}

	@Test
	void elPrimeroDespuesDeEsEstricto() {

		List<LocalDateTime> elementos = List.of(this.enSegundo(10), this.enSegundo(20), this.enSegundo(20), this.enSegundo(30));

		IndiceTemporal<LocalDateTime> indice = IndiceTemporal.de(elementos, fecha -> fecha);

		assertEquals(this.enSegundo(30), indice.primeroDespuesDe(this.enSegundo(20)));
		assertEquals(this.enSegundo(10), indice.primeroDespuesDe(this.enSegundo(5)));
		assertNull(indice.primeroDespuesDe(this.enSegundo(30)));
	}

	@Test
	void aguantaListasVaciasYFechasNulas() {

		IndiceTemporal<LocalDateTime> vacio = IndiceTemporal.de(new ArrayList<>(), fecha -> fecha);

		assertEquals(List.of(), vacio.enVentana(ORIGEN, 100));
		assertNull(vacio.primeroDespuesDe(ORIGEN));

		List<LocalDateTime> conNulos = new ArrayList<>();
		conNulos.add(null);
		conNulos.add(this.enSegundo(10));

		IndiceTemporal<LocalDateTime> indice = IndiceTemporal.de(conNulos, fecha -> fecha);

		assertEquals(List.of(this.enSegundo(10)), indice.enVentana(this.enSegundo(10), 5));
	}
}
