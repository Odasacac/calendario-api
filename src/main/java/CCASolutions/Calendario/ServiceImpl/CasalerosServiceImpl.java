package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.CasaleroDTO;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.CasalerosService;
import CCASolutions.Calendario.Utils.IndiceTemporal;

/**
 * EN: Manages the casaleros, the names given to each eclipeno according to the first
 * phenomenon that follows it.
 * ES: Gestiona los casaleros, los nombres que recibe cada eclípeno según el primer fenómeno
 * que lo sigue.
 */
@Service
@Transactional(readOnly = true)
public class CasalerosServiceImpl implements CasalerosService {

	private static final Logger LOG = LoggerFactory.getLogger(CasalerosServiceImpl.class);

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	@Autowired
	private CasalerosRepository casalerosRepository;

	@Autowired
	private EclipenosRepository eclipenosRepository;

	@Autowired
	private EclipsesRepository eclipsesRepository;

	@Autowired
	private MetonsRepository metonsRepository;


	/**
	 * EN: Reads the casalero of an eclipeno and fills in its details from whichever of the
	 * two possible sources it points to: a meton makes it "Metónico", an eclipse makes it
	 * "Eclipelar".
	 * ES: Lee el casalero de un eclípeno y rellena sus detalles a partir de la fuente a la que
	 * apunte: un métono lo hace "Metónico", un eclipse lo hace "Eclipelar".
	 *
	 * @param lastEclipenoIN EN: eclipeno whose casalero is wanted. / ES: eclípeno del que se quiere el casalero.
	 * @return EN: the casalero, or {@code null} if it has none or cannot be read. / ES: el casalero, o {@code null} si no tiene o no se puede leer.
	 */
	public CasaleroDTO getCasalero(EclipenosEntity lastEclipenoIN) {

		CasaleroDTO casaleroDTO = null;

		try {

			CasalerosEntity casaleroEntity = casalerosRepository.findByEclipenoId(lastEclipenoIN.getId());

			if(casaleroEntity == null) {
				return null;
			}

			casaleroDTO = new CasaleroDTO();
			casaleroDTO.setDateO(casaleroEntity.getDate().toLocalDate());

			String tipo = "";

			if(casaleroEntity.getMetonoId() != null) {

				Optional<MetonsEntity> metonoOpt = this.metonsRepository.findById(casaleroEntity.getMetonoId());

				if(metonoOpt.isPresent()) {

					MetonsEntity metono = metonoOpt.get();

					tipo = "Metónico";

					casaleroDTO.setLleno(metono.isLleno());
					// setNuevo(true) sobrescribia inmediatamente este valor
					casaleroDTO.setNuevo(metono.isNuevo());
					casaleroDTO.setInvernal(metono.isInvernal());
					casaleroDTO.setPrimaveral(metono.isPrimaveral());
					casaleroDTO.setEstival(metono.isEstival());
					casaleroDTO.setOtonyal(metono.isOtonyal());
				}
			}
			else if (casaleroEntity.getEclipseId() != null){

				Optional<EclipsesEntity> eclipseOpt = this.eclipsesRepository.findById(casaleroEntity.getEclipseId());

				if(eclipseOpt.isPresent()) {

					EclipsesEntity eclipse = eclipseOpt.get();

					tipo = "Eclipelar";
					casaleroDTO.setDeSol(eclipse.isDeSol());
					casaleroDTO.setDeLuna(eclipse.isDeLuna());
				}
			}

			casaleroDTO.setTipo(tipo);
		}
		catch(Exception e) {

			LOG.error("Error al obtener el casalero", e);
		}

		return casaleroDTO;
	}


	/*
		Un casalero siempre tiene dos apellido: es el año y el tipo

		Casalero X del año Y, ¿qué quiere decir?

		Y: El año en el ocurrió el eclípeno al que este Casalero hace referencia
		X: Nombre referente al fenómeno que ocurrirá primero despues de que haya ocurrido el eclípeno

		Metónico: Métono
		Eclipelar: Eclipse absoluto
	*/
	/**
	 * EN: Creates one casalero per eclipeno. Loads the metons and the absolute eclipses once
	 * and resolves each "first one after" by binary search, instead of hitting the database
	 * two or three times per eclipeno.
	 * ES: Crea un casalero por cada eclípeno. Carga los métonos y los eclipses absolutos una
	 * sola vez y resuelve cada "primero posterior a" por bisección, en lugar de ir a la base
	 * de datos dos o tres veces por eclípeno.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateCasaleros() {

		LOG.info("Iniciando poblar Casaleros.");

		try {

			if (this.casalerosRepository.count() > 0) {

				LOG.warn("Ya hay casaleros en la base de datos.");
				return "Error al actualizar los casaleros: ya hay casaleros en la base de datos.";
			}

			List<EclipenosEntity> eclipenos = this.eclipenosRepository.findAll();

			if (eclipenos.isEmpty()) {

				LOG.error("No hay eclípenos en la base de datos.");
				return "Error al actualizar los casaleros: no hay eclípenos en la base de datos.";
			}

			// Antes se hacian dos o tres SELECT por eclipeno. Las dos tablas se cargan
			// una sola vez y las busquedas "primero posterior a" se resuelven por
			// biseccion sobre el indice ordenado.
			IndiceTemporal<MetonsEntity> indiceMetonos = IndiceTemporal.de(this.metonsRepository.findAll(), MetonsEntity::getDate);

			List<EclipsesEntity> eclipsesAbsolutos = new ArrayList<>();

			for (EclipsesEntity eclipse : this.eclipsesRepository.findAll()) {

				if (!eclipse.isEsParcial() && !eclipse.isEsPenumbral()) {
					eclipsesAbsolutos.add(eclipse);
				}
			}

			IndiceTemporal<EclipsesEntity> indiceEclipses = IndiceTemporal.de(eclipsesAbsolutos, EclipsesEntity::getDate);

			List<CasalerosEntity> casalerosParaDB = new ArrayList<>();

			for (EclipenosEntity eclipeno : eclipenos) {

				LOG.debug("Evaluando eclipeno año: {}", eclipeno.getYear());

				CasalerosEntity casaleroParaDB = this.crearCasalero(eclipeno, indiceMetonos, indiceEclipses);

				if (casaleroParaDB != null) {

					casalerosParaDB.add(casaleroParaDB);
				}
			}

			this.casalerosRepository.saveAll(casalerosParaDB);

			LOG.info("Poblate casaleros finalizado: {} casaleros", casalerosParaDB.size());

			return "Casaleros poblados correctamente.";
		}
		catch (Exception e) {

			LOG.error("Error al poblar los casaleros", e);
			return "Error al actualizar los casaleros, checkear logs.";
		}
	}

	/**
	 * EN: Decides which phenomenon names one eclipeno: the first meton or the first absolute
	 * eclipse after it, whichever comes first. The eclipse that triggered the eclipeno itself
	 * does not count, so if the first one found is within a sidereal day the search moves on
	 * to the next.
	 * ES: Decide qué fenómeno da nombre a un eclípeno: el primer métono o el primer eclipse
	 * absoluto posterior, el que llegue antes. El eclipse que disparó el propio eclípeno no
	 * cuenta, así que si el primero encontrado está dentro de un día sideral la búsqueda pasa
	 * al siguiente.
	 *
	 * @param eclipeno        EN: eclipeno being named. / ES: eclípeno al que se pone nombre.
	 * @param indiceMetonos   EN: date-ordered index of the metons. / ES: índice de métonos ordenado por fecha.
	 * @param indiceEclipses  EN: date-ordered index of the absolute eclipses. / ES: índice de eclipses absolutos ordenado por fecha.
	 * @return EN: the casalero, or {@code null} if there is no later phenomenon. / ES: el casalero, o {@code null} si no hay ningún fenómeno posterior.
	 */
	private CasalerosEntity crearCasalero(EclipenosEntity eclipeno,
			IndiceTemporal<MetonsEntity> indiceMetonos,
			IndiceTemporal<EclipsesEntity> indiceEclipses) {

		LocalDateTime eclipenoDate = eclipeno.getDate();

		MetonsEntity metono = indiceMetonos.primeroDespuesDe(eclipenoDate);
		EclipsesEntity eclipseAbsoluto = indiceEclipses.primeroDespuesDe(eclipenoDate);

		// El eclipse que dispara el propio eclipeno no cuenta: se busca el siguiente
		if (eclipseAbsoluto != null
				&& Math.abs(ChronoUnit.SECONDS.between(eclipenoDate, eclipseAbsoluto.getDate())) <= TOLERANCIA_EN_SEGUNDOS) {

			eclipseAbsoluto = indiceEclipses.primeroDespuesDe(eclipenoDate.plusSeconds(TOLERANCIA_EN_SEGUNDOS));
		}

		CasalerosEntity casaleroParaDB = new CasalerosEntity();

		if (metono != null && eclipseAbsoluto != null) {

			if (metono.getDate().isBefore(eclipseAbsoluto.getDate())) {

				casaleroParaDB.setMetonoId(metono.getId());
				casaleroParaDB.setDate(metono.getDate());
			}
			else if (eclipseAbsoluto.getDate().isBefore(metono.getDate())) {

				casaleroParaDB.setEclipseId(eclipseAbsoluto.getId());
				casaleroParaDB.setDate(eclipseAbsoluto.getDate());
			}
		}
		else if (metono != null) {

			casaleroParaDB.setMetonoId(metono.getId());
			casaleroParaDB.setDate(metono.getDate());
		}
		else if (eclipseAbsoluto != null) {

			casaleroParaDB.setEclipseId(eclipseAbsoluto.getId());
			casaleroParaDB.setDate(eclipseAbsoluto.getDate());
		}

		if (casaleroParaDB.getDate() == null) {
			return null;
		}

		casaleroParaDB.setYear(casaleroParaDB.getDate().getYear());
		casaleroParaDB.setEclipenoId(eclipeno.getId());

		return casaleroParaDB;
	}
}
