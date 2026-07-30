package CCASolutions.Calendario.Vau;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.ComportamientoLunaDTO;
import CCASolutions.Calendario.DTOs.EstadoLunaDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: Whether the Moon is currently approaching or receding, based on the last
 *     apogee or perigee.
 *
 *     The logic is unchanged; the loop now reads one epoch day per row instead of
 *     rebuilding a LocalDate two or three times, and the "no apogee found" case is
 *     reported instead of throwing a NullPointerException on a sentinel entity that
 *     had a null date.
 *
 * ES: Si la Luna se esta acercando o alejando, segun el ultimo apogeo o perigeo.
 *
 *     La logica no cambia; el bucle lee ahora un dia epoch por fila en lugar de
 *     reconstruir un LocalDate dos o tres veces, y el caso de "no se ha encontrado
 *     apogeo" se informa en lugar de reventar con NullPointerException sobre una
 *     entidad centinela que tenia la fecha a null.
 * ==============================================================================
 */
@Component
public class CalculadoraEstadoLuna {

	private static final Logger log = LoggerFactory.getLogger(CalculadoraEstadoLuna.class);

	public EstadoLunaDTO calcular(ContextoCosmico contexto) {

		EstadoLunaDTO estadoLuna = new EstadoLunaDTO();
		estadoLuna.setComportamientoLunaDTO(calcularComportamiento(contexto));
		return estadoLuna;
	}

	private ComportamientoLunaDTO calcularComportamiento(ContextoCosmico contexto) {

		ComportamientoLunaDTO comportamiento = new ComportamientoLunaDTO();

		final long diaDeLaFecha = contexto.getDiaEpoch();

		ApogeosYPerigeosLunaEntity apoperiMasCercano = null;
		long diasDesdeElApoperi = Long.MAX_VALUE;
		boolean esHoy = false;

		for (ApogeosYPerigeosLunaEntity apoperi : contexto.getApoperis()) {

			long diaDelApoperi = Fechas.diaEpoch(apoperi.getDate());

			if (diaDelApoperi == diaDeLaFecha) {

				/*
				 * EN: An apogee or perigee on the requested day always wins, and the last one
				 *     of that day is the one reported.
				 * ES: Un apogeo o perigeo en el dia consultado siempre gana, y el ultimo de
				 *     ese dia es el que se informa.
				 */
				esHoy = true;
				apoperiMasCercano = apoperi;

			} else if (!esHoy && diaDelApoperi < diaDeLaFecha) {

				long distancia = diaDeLaFecha - diaDelApoperi;
				if (distancia < diasDesdeElApoperi) {
					diasDesdeElApoperi = distancia;
					apoperiMasCercano = apoperi;
				}
			}
		}

		if (apoperiMasCercano == null) {
			log.warn("No se ha encontrado ningún apogeo o perigeo anterior o igual a {}.", contexto.getFecha());
			return comportamiento;
		}

		if (esHoy) {

			// EN: Today the Moon reaches one of the two ends of its orbit.
			// ES: Hoy la Luna alcanza uno de los dos extremos de su orbita.
			if (apoperiMasCercano.isEsApogeo()) {
				comportamiento.setDireccion("Ha alcanzado su punto más lejano");
			} else if (apoperiMasCercano.isEsPerigeo()) {
				comportamiento.setDireccion("Ha alcanzado su punto más cercano");
			}
			comportamiento.setDate(apoperiMasCercano.getDate());

		} else {

			/*
			 * EN: After an apogee the Moon is approaching; after a perigee it is receding.
			 * ES: Tras un apogeo la Luna se acerca; tras un perigeo se aleja.
			 */
			String accion = "";
			if (apoperiMasCercano.isEsApogeo()) {
				accion = "acercándose";
			} else if (apoperiMasCercano.isEsPerigeo()) {
				accion = "alejándose";
			}

			comportamiento.setDireccion("Lleva " + diasDesdeElApoperi + " " + Fechas.literalDias(diasDesdeElApoperi)
					+ " " + accion);
		}

		return comportamiento;
	}
}
