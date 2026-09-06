package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MidsisonEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MidsisonService;
import CCASolutions.Calendario.Utils.IndiceTemporal;

/**
 * EN: Builds the midsison table: the instant exactly halfway between one solstice or
 * equinox and the next, plus whichever lunar phenomenon coincides with it.
 * ES: Construye la tabla de midsisons: el instante exactamente equidistante entre un
 * solsticio o equinoccio y el siguiente, más el fenómeno lunar que coincida con él.
 */
@Service
public class MidsisonServiceImpl implements MidsisonService{

	private static final Logger LOG = LoggerFactory.getLogger(MidsisonServiceImpl.class);

	private static final long TOLERANCIA_EN_SEGUNDOS = IndiceTemporal.DIA_SIDERAL_EN_SEGUNDOS;

	@Autowired
	private MidsisonRepository midsisonRepository;

	@Autowired
	private LunasRepository lunasRepository;

	@Autowired
	private EclipsesRepository eclipsesRepository;

	@Autowired
	private ApogeosYPerigeosLunaRepository apoperisRepository;

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	/*
		Un midsison es el momento en el que hacia el lastSOE al nextSOE pasara el mismo tiempo.

		Siempre son referentes a la estacion pasada: Midsison invernal.

		Se coge un soe, se coge el siguiente soe a ese
	*/
	/**
	 * EN: Creates one midsison for every consecutive pair of solstices and equinoxes, then
	 * marks the moon phase, the apogee or perigee and the eclipse that fall within one
	 * sidereal day of each. A midsison matching both a moon phase and an apogee or perigee
	 * is apofasal.
	 * ES: Crea un midsison por cada pareja consecutiva de solsticios y equinoccios, y después
	 * marca la fase lunar, el apogeo o perigeo y el eclipse que caen dentro de un día sideral
	 * de cada uno. Un midsison que coincide a la vez con una fase lunar y con un apogeo o
	 * perigeo es apofasal.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateMidsison() {

		LOG.info("Iniciando poblar Midsison.");

		try {

			if (this.midsisonRepository.count() > 0) {

				LOG.warn("Ya hay midsisons en base de datos");
				return "Error al poblar los midsisons: ya hay midsisons en base de datos.";
			}

			// findAll() no garantiza ningún orden y aquí se emparejaba cada soe con el
			// siguiente por posición: sin ORDER BY los midsisons podían salir mal calculados
			List<SolsticiosYEquinocciosEntity> allSoesFromDB = this.solsticiosYEquinocciosRepository.findAllByOrderByDateAsc();

			if (allSoesFromDB.isEmpty()) {

				LOG.error("No hay soes en base de datos");
				return "Error al poblar los midsisons: no hay soes en base de datos.";
			}

			List<MidsisonEntity> midsisonsForDB = new ArrayList<>(allSoesFromDB.size());

			for (int i = 0; i < allSoesFromDB.size() - 1; i++) {

				midsisonsForDB.add(this.crearMidsison(allSoesFromDB.get(i), allSoesFromDB.get(i + 1)));
			}

			LOG.info("Midsisons calculados, ahora a ver si llenos o nuevos también.");

			List<LunasEntity> allLunasFromDB = this.lunasRepository.findAll();
			List<ApogeosYPerigeosLunaEntity> allApoperisFromDB = this.apoperisRepository.findAll();
			List<EclipsesEntity> allEclipsesFromDB = this.eclipsesRepository.findAll();

			// Antes se recorrían las tres tablas completas por cada midsison: unos 8.400
			// midsisons x 143.000 filas. Los índices ordenados dejan el cruce en O(n log m).
			IndiceTemporal<LunasEntity> indiceLunas = IndiceTemporal.de(allLunasFromDB, LunasEntity::getDate);
			IndiceTemporal<ApogeosYPerigeosLunaEntity> indiceApoperis = IndiceTemporal.de(allApoperisFromDB, ApogeosYPerigeosLunaEntity::getDate);
			IndiceTemporal<EclipsesEntity> indiceEclipses = IndiceTemporal.de(allEclipsesFromDB, EclipsesEntity::getDate);

			for (MidsisonEntity midsison : midsisonsForDB) {

				this.aplicarLuna(midsison, indiceLunas);
				this.aplicarApoperi(midsison, indiceApoperis);

				if (midsison.getApoperiId() != null && midsison.getLunaId() != null) {
					midsison.setApofasal(true);
				}

				if (midsison.isNuevo() || midsison.isLleno()) {
					this.aplicarEclipse(midsison, indiceEclipses);
				}
			}

			this.midsisonRepository.saveAll(midsisonsForDB);

			LOG.info("Poblate midsisons finalizado: {} midsisons", midsisonsForDB.size());

			if (allLunasFromDB.isEmpty() || allApoperisFromDB.isEmpty() || allEclipsesFromDB.isEmpty()) {
				return "Midsisons poblados parcialmente: no hay lunas, apoperis o eclipses en base de datos.";
			}

			return "Midsisons poblados correctamente.";
		}
		catch (Exception e) {

			LOG.error("No se ha podido poblar los midsisons", e);
			return "Error al poblar los midsisons, checkear logs.";
		}
	}

	/**
	 * EN: Builds one midsison from a pair of consecutive solstices or equinoxes. Its date is
	 * the first one plus half the seconds separating them, and it always takes its season
	 * from the earlier of the two.
	 * ES: Construye un midsison a partir de una pareja de solsticios o equinoccios
	 * consecutivos. Su fecha es la del primero más la mitad de los segundos que los separan,
	 * y siempre toma la estación del anterior de los dos.
	 *
	 * @param pastSoe EN: previous solstice or equinox. / ES: solsticio o equinoccio anterior.
	 * @param nextSoe EN: next solstice or equinox. / ES: solsticio o equinoccio siguiente.
	 * @return EN: the midsison, not yet persisted. / ES: el midsison, todavía sin persistir.
	 */
	private MidsisonEntity crearMidsison(SolsticiosYEquinocciosEntity pastSoe, SolsticiosYEquinocciosEntity nextSoe) {

		MidsisonEntity midsison = new MidsisonEntity();

		midsison.setPastSOEId(pastSoe.getId());
		midsison.setNextSOEId(nextSoe.getId());

		midsison.setDate(pastSoe.getDate().plusSeconds(ChronoUnit.SECONDS.between(pastSoe.getDate(), nextSoe.getDate()) / 2));

		midsison.setLastSoeInvernal(pastSoe.isSolsticioInvierno());
		midsison.setLastSoePrimaveral(pastSoe.isEquinoccioPrimavera());
		midsison.setLastSoeEstival(pastSoe.isSolsticioVerano());
		midsison.setLastSoeOtonyal(pastSoe.isEquinoccioOtonyo());

		return midsison;
	}

	/**
	 * EN: Marks the new or full moon falling within one sidereal day of the midsison. If
	 * several match, the last one wins, which is the historical behaviour.
	 * ES: Marca la luna nueva o llena que cae dentro de un día sideral del midsison. Si
	 * coinciden varias gana la última, que es el comportamiento histórico.
	 *
	 * @param midsison    EN: midsison being filled in. / ES: midsison que se está rellenando.
	 * @param indiceLunas EN: date-ordered index of the moon phases. / ES: índice de fases lunares ordenado por fecha.
	 */
	private void aplicarLuna(MidsisonEntity midsison, IndiceTemporal<LunasEntity> indiceLunas) {

		for (LunasEntity luna : indiceLunas.enVentana(midsison.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

			if (!luna.isNueva() && !luna.isLlena()) {
				continue;
			}

			midsison.setLunaId(luna.getId());
			midsison.setNuevo(luna.isNueva());
			midsison.setLleno(luna.isLlena());

			if (luna.isSelecta() || luna.isInvertida()) {
				midsison.setSelecto(luna.isSelecta());
				midsison.setInvertido(luna.isInvertida());
			}
		}
	}

	/**
	 * EN: Marks the apogee or perigee falling within one sidereal day of the midsison.
	 * ES: Marca el apogeo o perigeo que cae dentro de un día sideral del midsison.
	 *
	 * @param midsison       EN: midsison being filled in. / ES: midsison que se está rellenando.
	 * @param indiceApoperis EN: date-ordered index of the apogees and perigees. / ES: índice de apogeos y perigeos ordenado por fecha.
	 */
	private void aplicarApoperi(MidsisonEntity midsison, IndiceTemporal<ApogeosYPerigeosLunaEntity> indiceApoperis) {

		for (ApogeosYPerigeosLunaEntity apoperi : indiceApoperis.enVentana(midsison.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

			midsison.setAporico(apoperi.isEsApogeo());
			midsison.setPerico(apoperi.isEsPerigeo());
			midsison.setApoperiId(apoperi.getId());

			if (apoperi.isEsSelecto() || apoperi.isEsInvertido()) {
				midsison.setSelecto(apoperi.isEsSelecto());
				midsison.setInvertido(apoperi.isEsInvertido());
			}
		}
	}

	/**
	 * EN: Marks the absolute eclipse (any solar one, or a total lunar one) falling within one
	 * sidereal day of the midsison. Only checked for midsisons that already carry a new or
	 * full moon.
	 * ES: Marca el eclipse absoluto (cualquiera solar, o uno lunar total) que cae dentro de un
	 * día sideral del midsison. Sólo se comprueba en los midsisons que ya llevan luna nueva o
	 * llena.
	 *
	 * @param midsison       EN: midsison being filled in. / ES: midsison que se está rellenando.
	 * @param indiceEclipses EN: date-ordered index of the eclipses. / ES: índice de eclipses ordenado por fecha.
	 */
	private void aplicarEclipse(MidsisonEntity midsison, IndiceTemporal<EclipsesEntity> indiceEclipses) {

		for (EclipsesEntity eclipse : indiceEclipses.enVentana(midsison.getDate(), TOLERANCIA_EN_SEGUNDOS)) {

			if (eclipse.isDeSol() || (eclipse.isDeLuna() && eclipse.isEsTotal())) {

				midsison.setEclipse(true);
				midsison.setEclipseId(eclipse.getId());
			}
		}
	}
}
