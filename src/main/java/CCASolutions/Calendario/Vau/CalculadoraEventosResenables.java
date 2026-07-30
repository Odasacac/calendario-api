package CCASolutions.Calendario.Vau;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.MidsisonDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: The notable event of the day, the previous one and the next one.
 *
 *     Conceptually this asks the same question of seven kinds of phenomenon: which
 *     one falls on the requested day, which is the closest before it and which the
 *     closest after it. The old code wrote that search out by hand six times, and
 *     each copy called entity.getDate().toLocalDate() up to four times per row.
 *
 *     Here the search lives in one place (Vecindario) and reads one epoch day per
 *     row. Metonos keep their own version because, unlike the rest, they collect
 *     every phenomenon tied at the same distance rather than just one.
 *
 * ES: El evento resenable del dia, el anterior y el siguiente.
 *
 *     Conceptualmente esto plantea la misma pregunta sobre siete tipos de fenomeno: cual
 *     cae en el dia consultado, cual es el mas cercano antes y cual el mas cercano
 *     despues. El codigo antiguo escribia esa busqueda a mano seis veces, y cada copia
 *     llamaba a entity.getDate().toLocalDate() hasta cuatro veces por fila.
 *
 *     Aqui la busqueda vive en un solo sitio (Vecindario) y lee un dia epoch por fila.
 *     Los metonos mantienen su propia version porque, a diferencia del resto, recogen
 *     todos los fenomenos empatados a la misma distancia y no solo uno.
 * ==============================================================================
 */
@Component
public class CalculadoraEventosResenables {

	public NotableEventDTO calcular(ContextoCosmico contexto) {

		NotableEventDTO eventos = new NotableEventDTO();

		final long diaDeLaFecha = contexto.getDiaEpoch();

		// ---------------------------------------------------------------------
		// EN: One pass per kind of phenomenon.
		// ES: Una pasada por tipo de fenomeno.
		// ---------------------------------------------------------------------
		Vecindario<ApogeosYPerigeosLunaEntity> apoperis = Vecindario.de(contexto.getApoperis(),
				ApogeosYPerigeosLunaEntity::getDate, diaDeLaFecha);
		Vecindario<LunasEntity> lunas = Vecindario.de(contexto.getLunas(), LunasEntity::getDate, diaDeLaFecha);
		Vecindario<SolsticiosYEquinocciosEntity> soes = Vecindario.de(contexto.getSoes(),
				SolsticiosYEquinocciosEntity::getDate, diaDeLaFecha);
		Vecindario<EclipsesEntity> eclipses = Vecindario.de(contexto.getEclipses(), EclipsesEntity::getDate,
				diaDeLaFecha);
		Vecindario<EclipenosEntity> eclipenos = Vecindario.de(contexto.getEclipenos(), EclipenosEntity::getDate,
				diaDeLaFecha);
		VecindarioDeMetonos metonos = VecindarioDeMetonos.de(contexto.getMetonos(), diaDeLaFecha);

		// ---------------------------------------------------------------------
		// EN: The midsison is not stored anywhere: it is the midpoint between the
		//     previous and the next solstice/equinox, so it only exists when both are
		//     known. At the very edge of the generated data one of them is missing,
		//     which used to end the request with a NullPointerException.
		// ES: El midsison no esta guardado en ningun sitio: es el punto medio entre el
		//     solsticio/equinoccio anterior y el siguiente, asi que solo existe cuando se
		//     conocen los dos. En el borde mismo de los datos generados falta uno de ellos,
		//     lo que antes terminaba la peticion con un NullPointerException.
		// ---------------------------------------------------------------------
		MidsisonDTO midsisonActual = null;
		MidsisonDTO midsisonAnterior = null;
		MidsisonDTO midsisonProximo = null;

		if (soes.anterior != null && soes.proximo != null) {

			LocalDateTime diaDelMidsison = Fechas.puntoMedio(soes.anterior.getDate(), soes.proximo.getDate());
			MidsisonDTO midsison = new MidsisonDTO();
			midsison.setDate(diaDelMidsison);
			midsison.setLastSoeSeason(soes.anterior.getStartingSeason());

			long diaEpochDelMidsison = Fechas.diaEpoch(diaDelMidsison);
			if (diaEpochDelMidsison < diaDeLaFecha) {
				midsisonAnterior = midsison;
			} else if (diaEpochDelMidsison == diaDeLaFecha) {
				midsisonActual = midsison;
			} else {
				midsisonProximo = midsison;
			}
		}

		// ---------------------------------------------------------------------
		// EN: Today's event needs no distance: whatever falls on the day is the event.
		// ES: El evento de hoy no necesita distancia: lo que cae en el dia es el evento.
		// ---------------------------------------------------------------------
		eventos.setToday(NombresDeEventos.nombrar(lunas.actual, soes.actual, primero(metonos.actuales),
				eclipses.actual, eclipenos.actual, apoperis.actual, midsisonActual));

		// ---------------------------------------------------------------------
		// EN: The previous and next events are whichever phenomenon is closest.
		// ES: Los eventos anterior y siguiente son el fenomeno que quede mas cerca.
		// ---------------------------------------------------------------------
		eventos.setPrevious(evento(contexto, lunas.anterior, soes.anterior, primero(metonos.anteriores),
				eclipses.anterior, eclipenos.anterior, apoperis.anterior, midsisonAnterior, true));

		eventos.setNext(evento(contexto, lunas.proximo, soes.proximo, primero(metonos.proximos), eclipses.proximo,
				eclipenos.proximo, apoperis.proximo, midsisonProximo, false));

		return eventos;
	}

	/*
	 * EN: Picks the closest of the seven candidates and renders it as
	 *     "<name> hace N días" or "<name> dentro de N días". A candidate only takes
	 *     part when it exists; the winner is the one whose distance equals the minimum,
	 *     and ties are resolved by the priority inside NombresDeEventos.
	 * ES: Elige el mas cercano de los siete candidatos y lo formatea como
	 *     "<nombre> hace N días" o "<nombre> dentro de N días". Un candidato solo participa
	 *     si existe; gana el que tiene distancia igual al minimo, y los empates los
	 *     resuelve la prioridad de NombresDeEventos.
	 */
	private String evento(ContextoCosmico contexto,
			LunasEntity luna,
			SolsticiosYEquinocciosEntity soe,
			MetonsEntity metono,
			EclipsesEntity eclipse,
			EclipenosEntity eclipeno,
			ApogeosYPerigeosLunaEntity apoperi,
			MidsisonDTO midsison,
			boolean haciaElPasado) {

		long diasLuna = distancia(contexto, luna == null ? null : luna.getDate(), haciaElPasado);
		long diasSoe = distancia(contexto, soe == null ? null : soe.getDate(), haciaElPasado);
		long diasMetono = distancia(contexto, metono == null ? null : metono.getDate(), haciaElPasado);
		long diasEclipse = distancia(contexto, eclipse == null ? null : eclipse.getDate(), haciaElPasado);
		long diasEclipeno = distancia(contexto, eclipeno == null ? null : eclipeno.getDate(), haciaElPasado);
		long diasApoperi = distancia(contexto, apoperi == null ? null : apoperi.getDate(), haciaElPasado);
		long diasMidsison = distancia(contexto, midsison == null ? null : midsison.getDate(), haciaElPasado);

		long minimo = Math.min(diasMidsison, Math.min(diasApoperi, Math.min(diasLuna,
				Math.min(diasSoe, Math.min(diasMetono, Math.min(diasEclipse, diasEclipeno))))));

		String nombre = NombresDeEventos.nombrar(
				diasLuna == minimo ? luna : null,
				diasSoe == minimo ? soe : null,
				diasMetono == minimo ? metono : null,
				diasEclipse == minimo ? eclipse : null,
				diasEclipeno == minimo ? eclipeno : null,
				diasApoperi == minimo ? apoperi : null,
				diasMidsison == minimo ? midsison : null);

		return nombre + (haciaElPasado ? " hace " : " dentro de ") + minimo + " " + Fechas.literalDias(minimo);
	}

	/*
	 * EN: Whole days between the phenomenon and the requested date, or Long.MAX_VALUE
	 *     when the phenomenon does not exist so that it never wins the minimum.
	 * ES: Dias completos entre el fenomeno y la fecha consultada, o Long.MAX_VALUE cuando
	 *     el fenomeno no existe, para que nunca gane el minimo.
	 */
	private long distancia(ContextoCosmico contexto, LocalDateTime fechaDelFenomeno, boolean haciaElPasado) {
		if (fechaDelFenomeno == null) {
			return Long.MAX_VALUE;
		}
		long diaDelFenomeno = Fechas.diaEpoch(fechaDelFenomeno);
		return haciaElPasado
				? contexto.getDiaEpoch() - diaDelFenomeno
				: diaDelFenomeno - contexto.getDiaEpoch();
	}

	private static MetonsEntity primero(List<MetonsEntity> metonos) {
		return metonos.isEmpty() ? null : metonos.get(0);
	}

	// =========================================================================
	// EN: TEMPORAL NEIGHBOURHOOD
	// ES: VECINDARIO TEMPORAL
	// =========================================================================

	/*
	 * EN: The phenomenon of the day, the closest one before it and the closest one
	 *     after it, found in a single pass. The tie-breaks reproduce the original
	 *     loops: for "today" the last row of the day wins, and for the neighbours the
	 *     first row at the minimum distance wins.
	 * ES: El fenomeno del dia, el mas cercano antes y el mas cercano despues, hallados en
	 *     una sola pasada. Los desempates reproducen los bucles originales: para "hoy"
	 *     gana la ultima fila del dia, y para los vecinos gana la primera fila a la
	 *     distancia minima.
	 */
	private static final class Vecindario<T> {

		private T actual;
		private T anterior;
		private T proximo;

		private static <T> Vecindario<T> de(List<T> fenomenos, Function<T, LocalDateTime> fechaDe,
				long diaDeLaFecha) {

			Vecindario<T> vecindario = new Vecindario<>();
			long diasAlAnterior = Long.MAX_VALUE;
			long diasAlProximo = Long.MAX_VALUE;

			for (T fenomeno : fenomenos) {

				long diaDelFenomeno = Fechas.diaEpoch(fechaDe.apply(fenomeno));

				if (diaDelFenomeno == diaDeLaFecha) {
					vecindario.actual = fenomeno;
				} else if (diaDelFenomeno < diaDeLaFecha) {
					long distancia = diaDeLaFecha - diaDelFenomeno;
					if (distancia < diasAlAnterior) {
						diasAlAnterior = distancia;
						vecindario.anterior = fenomeno;
					}
				} else {
					long distancia = diaDelFenomeno - diaDeLaFecha;
					if (distancia < diasAlProximo) {
						diasAlProximo = distancia;
						vecindario.proximo = fenomeno;
					}
				}
			}

			return vecindario;
		}
	}

	/*
	 * EN: Same idea for metonos, which can share a day or a distance: two metonos of
	 *     different families often fall together, and the original kept all of them.
	 * ES: La misma idea para los metonos, que pueden compartir dia o distancia: dos
	 *     metonos de familias distintas caen a menudo juntos, y el original los guardaba
	 *     todos.
	 */
	private static final class VecindarioDeMetonos {

		private List<MetonsEntity> actuales = new ArrayList<>(2);
		private List<MetonsEntity> anteriores = new ArrayList<>(2);
		private List<MetonsEntity> proximos = new ArrayList<>(2);

		private static VecindarioDeMetonos de(List<MetonsEntity> metonos, long diaDeLaFecha) {

			VecindarioDeMetonos vecindario = new VecindarioDeMetonos();
			long diasAlAnterior = Long.MAX_VALUE;
			long diasAlProximo = Long.MAX_VALUE;

			for (MetonsEntity metono : metonos) {

				long diaDelMetono = Fechas.diaEpoch(metono.getDate());

				if (diaDelMetono == diaDeLaFecha) {
					vecindario.actuales.add(metono);
				} else if (diaDelMetono < diaDeLaFecha) {
					long distancia = diaDeLaFecha - diaDelMetono;
					if (distancia < diasAlAnterior) {
						diasAlAnterior = distancia;
						vecindario.anteriores.clear();
						vecindario.anteriores.add(metono);
					} else if (distancia == diasAlAnterior) {
						vecindario.anteriores.add(metono);
					}
				} else {
					long distancia = diaDelMetono - diaDeLaFecha;
					if (distancia < diasAlProximo) {
						diasAlProximo = distancia;
						vecindario.proximos.clear();
						vecindario.proximos.add(metono);
					} else if (distancia == diasAlProximo) {
						vecindario.proximos.add(metono);
					}
				}
			}

			return vecindario;
		}
	}
}
