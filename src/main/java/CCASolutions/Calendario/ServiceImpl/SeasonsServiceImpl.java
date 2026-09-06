package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.SeasonDTO;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Services.SeasonsService;
import CCASolutions.Calendario.Services.TablasReferenciaService;
import CCASolutions.Calendario.Utils.Vecindad;

/**
 * EN: Works out the VAU season of a date and which third of the season it falls in.
 * ES: Calcula la estación VAU de una fecha y en qué tramo de la estación cae.
 */
@Service
public class SeasonsServiceImpl implements SeasonsService{

	private static final Logger LOG = LoggerFactory.getLogger(SeasonsServiceImpl.class);

	@Autowired
	private SeasonsRepository seasonsRepository;

	@Autowired
	private TablasReferenciaService tablasReferenciaService;


	/**
	 * EN: Works out the season and its qualifier. A date falling exactly on a solstice or
	 * equinox belongs to no season and gets the placeholder one. Otherwise the season is the
	 * one opened by the previous solstice, and the qualifier depends on whether the date
	 * comes before, on, or after the midsison, the halfway point of that season.
	 * ES: Calcula la estación y su apellido. Una fecha que cae justo en un solsticio o
	 * equinoccio no pertenece a ninguna estación y recibe la de relleno. Si no, la estación
	 * es la que abre el solsticio anterior, y el apellido depende de si la fecha va antes,
	 * en, o después del midsison, el punto medio de esa estación.
	 *
	 * @param date    EN: date being consulted. / ES: fecha que se consulta.
	 * @param allSoes EN: solstices and equinoxes around that date. / ES: solsticios y equinoccios alrededor de esa fecha.
	 * @return EN: name and qualifier of the season. / ES: nombre y apellido de la estación.
	 */
	public SeasonDTO getVAUSeason(LocalDate date, List<SolsticiosYEquinocciosEntity> allSoes) {

		SeasonDTO seasonDTO = new SeasonDTO();

		Vecindad<SolsticiosYEquinocciosEntity> vecindad = Vecindad.de(allSoes, SolsticiosYEquinocciosEntity::getDate, date);

		int startingSeason = 0;

		SolsticiosYEquinocciosEntity lastSoe = vecindad.getAnterior();
		SolsticiosYEquinocciosEntity nextSoe = vecindad.getProximo();

		// El codigo partia de entidades vacias en vez de null, de modo que el control
		// != null nunca fallaba y se leia una fecha nula unas lineas mas abajo
		if (vecindad.getActual() == null && lastSoe != null && nextSoe != null) {

			startingSeason = lastSoe.getStartingSeason();

			LocalDate midSeasonDate = lastSoe.getDate()
					.plusSeconds(ChronoUnit.SECONDS.between(lastSoe.getDate(), nextSoe.getDate()) / 2)
					.toLocalDate();

			String surname = "";

			if (date.isBefore(midSeasonDate)) {

				seasonDTO.setPreMidsison(true);
				surname = "iniciante";
			}
			else if (date.isEqual(midSeasonDate)) {

				seasonDTO.setMidsisonDay(true);
				surname = lastSoe.getStartingSeason() == 2 ? "cenítica" : "cenítico";
			}
			else {

				seasonDTO.setPostMidsison(true);
				surname = "terminante";
			}

			seasonDTO.setSurname(surname);
		}

		SeasonsEntity estacion = this.tablasReferenciaService.getEstacion(startingSeason);

		if (estacion != null) {
			seasonDTO.setName(estacion.getName());
		}
		else {
			LOG.warn("No existe la estación número {} en la base de datos", startingSeason);
		}

		return seasonDTO;
	}


	/**
	 * EN: Inserts the five fixed season rows. Does nothing if the table already has rows.
	 * ES: Inserta las cinco filas fijas de estaciones. No hace nada si la tabla ya tiene filas.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateSeasons() {

		LOG.info("Actualizando las Estaciones.");

		if(this.seasonsRepository.count() > 0) {

			LOG.warn("Ya hay estaciones en la base de datos.");
			return "Error al actualizar las estaciones: ya hay estaciones en la base de datos.";
		}

		List<SeasonsEntity> seasonsParaDDB = new ArrayList<>();

		seasonsParaDDB.add(this.createSeason("-", 0));
		seasonsParaDDB.add(this.createSeason("Invierno", 1));
		seasonsParaDDB.add(this.createSeason("Primavera", 2));
		seasonsParaDDB.add(this.createSeason("Verano", 3));
		seasonsParaDDB.add(this.createSeason("Otoño", 4));

		this.seasonsRepository.saveAll(seasonsParaDDB);

		LOG.info("Estaciones actualizadas");

		return "Estaciones actualizadas correctamente.";
	}

	/**
	 * EN: Builds one season row in memory.
	 * ES: Construye en memoria una fila de estación.
	 *
	 * @param name            EN: name of the season. / ES: nombre de la estación.
	 * @param seasonOfTheYear EN: season number, 0 to 4. / ES: número de estación, del 0 al 4.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private SeasonsEntity createSeason(String name, int seasonOfTheYear) {

		SeasonsEntity newSeason = new SeasonsEntity();
		newSeason.setName(name);
		newSeason.setSeasonOfTheYear(seasonOfTheYear);

		return newSeason;
	}
}
