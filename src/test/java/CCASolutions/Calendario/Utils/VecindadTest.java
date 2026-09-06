package CCASolutions.Calendario.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Vecindad reemplaza los bucles "diasMinimosDeDiferenciaEntreXPasadoYDate" que
 * estaban repetidos por todo el proyecto, así que se contrasta contra una copia
 * literal de aquella lógica.
 */
class VecindadTest {

	private static final LocalDate REFERENCIA = LocalDate.of(2026, 9, 6);

	private LocalDateTime enDia(long dias) {
		return REFERENCIA.plusDays(dias).atTime(12, 0);
	}

	/** Reproduce el bucle original: primero gana en caso de empate. */
	private LocalDateTime[] comoAntes(List<LocalDateTime> elementos, LocalDate dateO) {

		LocalDateTime actual = null;
		LocalDateTime pasado = null;
		LocalDateTime futuro = null;
		long minPasado = Long.MAX_VALUE;
		long minFuturo = Long.MAX_VALUE;

		for (LocalDateTime elemento : elementos) {

			if (elemento.toLocalDate().isEqual(dateO)) {
				actual = elemento;
			}
			else if (elemento.toLocalDate().isBefore(dateO)) {

				long distancia = ChronoUnit.DAYS.between(elemento.toLocalDate(), dateO);

				if (distancia < minPasado) {
					minPasado = distancia;
					pasado = elemento;
				}
			}
			else {

				long distancia = ChronoUnit.DAYS.between(dateO, elemento.toLocalDate());

				if (distancia < minFuturo) {
					minFuturo = distancia;
					futuro = elemento;
				}
			}
		}

		return new LocalDateTime[] { actual, pasado, futuro };
	}

	@Test
	void coincideConElBucleOriginal() {

		Random random = new Random(1433L);

		for (int caso = 0; caso < 300; caso++) {

			List<LocalDateTime> elementos = new ArrayList<>();

			for (int i = 0; i < 120; i++) {
				elementos.add(this.enDia(random.nextInt(2000) - 1000));
			}

			LocalDateTime[] esperado = this.comoAntes(elementos, REFERENCIA);
			Vecindad<LocalDateTime> vecindad = Vecindad.de(elementos, fecha -> fecha, REFERENCIA);

			assertEquals(esperado[0], vecindad.getActual());
			assertEquals(esperado[1], vecindad.getAnterior());
			assertEquals(esperado[2], vecindad.getProximo());
		}
	}

	@Test
	void calculaLasDistanciasEnDias() {

		List<LocalDateTime> elementos = List.of(this.enDia(-10), this.enDia(-3), this.enDia(4), this.enDia(40));

		Vecindad<LocalDateTime> vecindad = Vecindad.de(elementos, fecha -> fecha, REFERENCIA);

		assertEquals(3, vecindad.getDiasHastaAnterior());
		assertEquals(4, vecindad.getDiasHastaProximo());
		assertNull(vecindad.getActual());
	}

	@Test
	void elEmpateSeResuelveSegunLaVariantePedida() {

		LocalDateTime primero = REFERENCIA.minusDays(5).atTime(1, 0);
		LocalDateTime segundo = REFERENCIA.minusDays(5).atTime(23, 0);

		List<LocalDateTime> elementos = List.of(primero, segundo);

		assertEquals(primero, Vecindad.de(elementos, fecha -> fecha, REFERENCIA).getAnterior());
		assertEquals(segundo, Vecindad.deUltimoEnEmpate(elementos, fecha -> fecha, REFERENCIA).getAnterior());
	}

	@Test
	void aguantaListasNulasYVacias() {

		Vecindad<LocalDateTime> nula = Vecindad.de(null, fecha -> fecha, REFERENCIA);

		assertNull(nula.getActual());
		assertNull(nula.getAnterior());
		assertNull(nula.getProximo());
		assertEquals(Long.MAX_VALUE, nula.getDiasHastaAnterior());
		assertEquals(Long.MAX_VALUE, nula.getDiasHastaProximo());
	}
}
