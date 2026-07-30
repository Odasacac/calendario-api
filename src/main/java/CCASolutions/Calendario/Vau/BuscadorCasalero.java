package CCASolutions.Calendario.Vau;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;

/*
 * ==============================================================================
 * EN: The "casalero" attached to the current eclipeno: the phenomenon - metonic or
 *     eclipelar - that opened it.
 *
 *     Two primary-key lookups at most, so this was never a bottleneck. What changed:
 *     the failure is logged instead of printed to System.out, and the broad
 *     catch(Exception) around the whole method is gone in favour of returning null
 *     when the casalero simply does not exist, which is a normal outcome rather than
 *     an error.
 *
 * ES: El "casalero" asociado al eclipeno actual: el fenomeno - metonico o eclipelar -
 *     que lo abrio.
 *
 *     Como maximo dos busquedas por clave primaria, asi que nunca fue un cuello de
 *     botella. Lo que cambia: el fallo se registra en el log en lugar de imprimirse por
 *     System.out, y desaparece el catch(Exception) que envolvia todo el metodo a favor
 *     de devolver null cuando el casalero simplemente no existe, que es un resultado
 *     normal y no un error.
 * ==============================================================================
 */
@Component
public class BuscadorCasalero {

	private static final Logger log = LoggerFactory.getLogger(BuscadorCasalero.class);

	private static final String TIPO_METONICO = "Metónico";
	private static final String TIPO_ECLIPELAR = "Eclipelar";

	private final CasalerosRepository casalerosRepository;
	private final MetonsRepository metonsRepository;
	private final EclipsesRepository eclipsesRepository;

	public BuscadorCasalero(CasalerosRepository casalerosRepository,
			MetonsRepository metonsRepository,
			EclipsesRepository eclipsesRepository) {
		this.casalerosRepository = casalerosRepository;
		this.metonsRepository = metonsRepository;
		this.eclipsesRepository = eclipsesRepository;
	}

	public CasaleroDTO buscar(ContextoCosmico contexto) {

		CasalerosEntity casalero = this.casalerosRepository
				.findByEclipenoId(contexto.getUltimoEclipenoIN().getId());

		if (casalero == null || casalero.getDate() == null) {
			log.debug("El eclípeno {} no tiene casalero.", contexto.getUltimoEclipenoIN().getId());
			return null;
		}

		CasaleroDTO casaleroDTO = new CasaleroDTO();
		casaleroDTO.setDateO(casalero.getDate().toLocalDate());
		casaleroDTO.setTipo("");

		if (casalero.getMetonoId() != null) {

			// EN: Metonic casalero: it inherits the season and the phase of its metono.
			// ES: Casalero metonico: hereda la estacion y la fase de su metono.
			Optional<MetonsEntity> metono = this.metonsRepository.findById(casalero.getMetonoId());

			if (metono.isPresent()) {
				MetonsEntity encontrado = metono.get();
				casaleroDTO.setTipo(TIPO_METONICO);
				casaleroDTO.setLleno(encontrado.isLleno());
				casaleroDTO.setInvernal(encontrado.isInvernal());
				casaleroDTO.setPrimaveral(encontrado.isPrimaveral());
				casaleroDTO.setEstival(encontrado.isEstival());
				casaleroDTO.setOtonyal(encontrado.isOtonyal());

				/*
				 * EN: Careful, this is intentional: the original set "nuevo" from the metono
				 *     and then forced it to true, so a metonic casalero always reports
				 *     nuevo=true. Preserved to keep the API response identical.
				 * ES: Cuidado, esto es intencionado: el original asignaba "nuevo" desde el
				 *     metono y luego lo forzaba a true, asi que un casalero metonico siempre
				 *     informa nuevo=true. Se conserva para mantener identica la respuesta.
				 */
				casaleroDTO.setNuevo(true);
			}

		} else if (casalero.getEclipseId() != null) {

			// EN: Eclipelar casalero: it only tells solar from lunar.
			// ES: Casalero eclipelar: solo distingue solar de lunar.
			Optional<EclipsesEntity> eclipse = this.eclipsesRepository.findById(casalero.getEclipseId());

			if (eclipse.isPresent()) {
				EclipsesEntity encontrado = eclipse.get();
				casaleroDTO.setTipo(TIPO_ECLIPELAR);
				casaleroDTO.setDeSol(encontrado.isDeSol());
				casaleroDTO.setDeLuna(encontrado.isDeLuna());
			}
		}

		return casaleroDTO;
	}
}
