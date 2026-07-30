package CCASolutions.Calendario.Vau;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.AbsoluteEclipsesDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: How many eclipses have happened since the current eclipeno and since the
 *     current metono.
 *
 *     This block is pure counting, and the old implementation did it the expensive
 *     way: it walked the ~1.300 eclipses that had been hydrated for the request and
 *     appended them to two ArrayLists whose only use was .size().
 *
 *     Four indexed COUNT queries replace all of it. They are only issued when the
 *     counters are actually needed - on the day of an eclipeno every counter is
 *     zero by definition, so no query is sent at all.
 *
 * ES: Cuantos eclipses han ocurrido desde el eclipeno actual y desde el metono actual.
 *
 *     Este bloque es puro conteo, y la implementacion antigua lo hacia por la via cara:
 *     recorria los ~1.300 eclipses que se habian hidratado para la peticion y los
 *     anadia a dos ArrayList cuyo unico uso era .size().
 *
 *     Cuatro consultas COUNT sobre indice lo sustituyen todo. Solo se lanzan cuando los
 *     contadores hacen falta de verdad: el dia de un eclipeno todos los contadores son
 *     cero por definicion, asi que no se envia ninguna consulta.
 * ==============================================================================
 */
@Component
public class CalculadoraEclipsesAbsolutos {

	private final EclipsesRepository eclipsesRepository;

	public CalculadoraEclipsesAbsolutos(EclipsesRepository eclipsesRepository) {
		this.eclipsesRepository = eclipsesRepository;
	}

	/*
	 * EN: Only partial and penumbral eclipses are excluded, which is exactly what the
	 *     repository counters already filter out.
	 * ES: Solo se excluyen los eclipses parciales y penumbrales, que es precisamente lo
	 *     que ya filtran los contadores del repositorio.
	 */
	public AbsoluteEclipsesDTO calcular(ContextoCosmico contexto, DateDTO fechaVAU) {

		AbsoluteEclipsesDTO absolutos = new AbsoluteEclipsesDTO();

		long solaresDesdeElEclipeno = 0;
		long lunaresDesdeElEclipeno = 0;
		long solaresDesdeElMetono = 0;
		long lunaresDesdeElMetono = 0;

		if (!fechaVAU.getEclipenoVAU().isEclipenoINDay()) {

			/*
			 * EN: The window starts on the day of the current eclipeno and ends the day
			 *     before the requested date, exactly like the old in-memory filter.
			 * ES: La ventana empieza el dia del eclipeno actual y termina el dia anterior a
			 *     la fecha consultada, exactamente como el filtro en memoria antiguo.
			 */
			LocalDateTime desdeElEclipeno = Fechas
					.inicioDelDia(contexto.getUltimoEclipenoIN().getDate().toLocalDate());
			LocalDateTime hasta = Fechas.antesDelDia(contexto.getFecha());

			// EN: The metono window is the intersection of both anchors.
			// ES: La ventana del metono es la interseccion de ambas anclas.
			LocalDateTime desdeElMetono = Fechas.inicioDelDia(contexto.getUltimoMetonoIN().getDate().toLocalDate());
			if (desdeElMetono.isBefore(desdeElEclipeno)) {
				desdeElMetono = desdeElEclipeno;
			}

			if (hasta.isAfter(desdeElEclipeno)) {

				solaresDesdeElEclipeno = this.eclipsesRepository
						.countByEsParcialFalseAndEsPenumbralFalseAndDeSolTrueAndDateGreaterThanEqualAndDateLessThan(
								desdeElEclipeno, hasta);
				lunaresDesdeElEclipeno = this.eclipsesRepository
						.countByEsParcialFalseAndEsPenumbralFalseAndDeLunaTrueAndDateGreaterThanEqualAndDateLessThan(
								desdeElEclipeno, hasta);

				if (hasta.isAfter(desdeElMetono)) {
					solaresDesdeElMetono = this.eclipsesRepository
							.countByEsParcialFalseAndEsPenumbralFalseAndDeSolTrueAndDateGreaterThanEqualAndDateLessThan(
									desdeElMetono, hasta);
					lunaresDesdeElMetono = this.eclipsesRepository
							.countByEsParcialFalseAndEsPenumbralFalseAndDeLunaTrueAndDateGreaterThanEqualAndDateLessThan(
									desdeElMetono, hasta);
				}
			}

			/*
			 * EN: Inside the very first metono of a cycle one solar eclipse belongs to the
			 *     eclipeno itself and must not be counted twice.
			 * ES: Dentro del primer metono de un ciclo, un eclipse solar pertenece al propio
			 *     eclipeno y no debe contarse dos veces.
			 */
			if (fechaVAU.getMetonoVAU().getMetonsIN().getMetonosINSinceLastEclipenoIN() == 0
					&& solaresDesdeElMetono > 0) {
				solaresDesdeElMetono--;
			}
		}

		absolutos.setSolarSinceLastEclipenoIN((int) solaresDesdeElEclipeno);
		absolutos.setSolarSinceLastMetonoIN((int) solaresDesdeElMetono);

		absolutos.setLunarSinceLastEclipenoIN((int) lunaresDesdeElEclipeno);
		absolutos.setLunarSinceLastMetonoIN((int) lunaresDesdeElMetono);

		absolutos.setSinceLastEclipenoIN((int) (solaresDesdeElEclipeno + lunaresDesdeElEclipeno));
		absolutos.setSinceLastMetonoIN((int) (solaresDesdeElMetono + lunaresDesdeElMetono));

		return absolutos;
	}
}
