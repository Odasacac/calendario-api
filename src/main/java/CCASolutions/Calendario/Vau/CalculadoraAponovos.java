package CCASolutions.Calendario.Vau;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.AponovosDTO;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: Aponovos: the selected new moons since the anchoring metono.
 *
 *     This was the single most expensive block of the whole read path, and the only
 *     reason the request had to load a centuries-long list of lunar phases. It made
 *     two full passes over roughly 29.000 hydrated entities to produce four small
 *     integers: how many aponovos had passed, which one we are in, how many new
 *     moons have passed since it, and which lunar month of the aponovo we are in.
 *
 *     All of that is now three indexed queries - two counts and one TOP-1 - with the
 *     exact same filters as the original loops:
 *
 *       - strictly after the day of the anchoring metono,
 *       - strictly before the requested day,
 *       - excluding the new moon that belongs to the anchoring metono itself.
 *
 * ES: Aponovos: las lunas nuevas selectas desde el metono ancla.
 *
 *     Este era el bloque mas caro de todo el camino de lectura, y la unica razon por la
 *     que la peticion tenia que cargar una lista de fases lunares de siglos. Hacia dos
 *     pasadas completas sobre unas 29.000 entidades hidratadas para producir cuatro
 *     enteros pequenos: cuantos aponovos habian pasado, en cual estamos, cuantas lunas
 *     nuevas han pasado desde el, y en que mes lunar del aponovo estamos.
 *
 *     Todo eso son ahora tres consultas sobre indice - dos conteos y un TOP-1 - con
 *     exactamente los mismos filtros que los bucles originales:
 *
 *       - estrictamente despues del dia del metono ancla,
 *       - estrictamente antes del dia consultado,
 *       - excluyendo la luna nueva que pertenece al propio metono ancla.
 * ==============================================================================
 */
@Component
public class CalculadoraAponovos {

	private final LunasRepository lunasRepository;

	public CalculadoraAponovos(LunasRepository lunasRepository) {
		this.lunasRepository = lunasRepository;
	}

	public AponovosDTO calcular(ContextoCosmico contexto) {

		AponovosDTO aponovos = new AponovosDTO();

		MetonsEntity ancla = contexto.getUltimoMetonoIApofasalRemoto();

		LocalDateTime desde = Fechas.despuesDelDia(ancla.getDate().toLocalDate());
		LocalDateTime hasta = Fechas.antesDelDia(contexto.getFecha());
		Long lunaDelAncla = ancla.getLunaId();

		if (!desde.isBefore(hasta)) {

			/*
			 * EN: The anchoring metono is the requested day or later: no aponovo has passed
			 *     yet, so we are in the first one and in its first lunar month.
			 * ES: El metono ancla es el dia consultado o posterior: todavia no ha pasado
			 *     ningun aponovo, asi que estamos en el primero y en su primer mes lunar.
			 */
			aponovos.setAponovosPasadosDesdeLastMetonoIAR(0);
			aponovos.setNumeroDeAponovo(1);
			aponovos.setLunasNuevasPasadasDesdeLastAponovo(0);
			aponovos.setMesAponoval(1);
			return aponovos;
		}

		// EN: How many aponovos are behind us, and therefore which one we are in.
		// ES: Cuantos aponovos quedan atras y, por tanto, en cual estamos.
		long aponovosPasados = this.lunasRepository.contarAponovos(desde, hasta, lunaDelAncla);
		aponovos.setAponovosPasadosDesdeLastMetonoIAR((int) aponovosPasados);
		aponovos.setNumeroDeAponovo((int) aponovosPasados + 1);

		// EN: How many new moons since the aponovo we are in, and therefore its month.
		// ES: Cuantas lunas nuevas desde el aponovo en el que estamos y, por tanto, su mes.
		long lunasNuevasDesdeElAponovo = 0;
		LunasEntity aponovoActual = this.lunasRepository.ultimoAponovo(desde, hasta, lunaDelAncla);

		if (aponovoActual != null) {
			lunasNuevasDesdeElAponovo = this.lunasRepository.countByNuevaTrueAndDateGreaterThanEqualAndDateLessThan(
					Fechas.despuesDelDia(aponovoActual.getDate().toLocalDate()), hasta);
		}

		aponovos.setLunasNuevasPasadasDesdeLastAponovo((int) lunasNuevasDesdeElAponovo);
		aponovos.setMesAponoval((int) lunasNuevasDesdeElAponovo + 1);

		return aponovos;
	}
}
