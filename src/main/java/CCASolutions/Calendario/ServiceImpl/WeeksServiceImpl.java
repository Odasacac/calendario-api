package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.WeeksService;

/**
 * EN: Manages the VAU weeks table.
 * ES: Gestiona la tabla de semanas VAU.
 */
@Service
public class WeeksServiceImpl implements WeeksService{

	private static final Logger LOG = LoggerFactory.getLogger(WeeksServiceImpl.class);

	@Autowired
	private WeeksRepository weeksRepository;

	/**
	 * EN: Inserts the six fixed week rows: the placeholder plus Primana, Segana, Terana,
	 * Curana and Limana. Does nothing if the table already has rows.
	 * ES: Inserta las seis filas fijas de semanas: la de relleno más Primana, Segana,
	 * Terana, Curana y Limana. No hace nada si la tabla ya tiene filas.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateWeeks() {

		LOG.info("Actualizando las Semanas.");

		if(this.weeksRepository.count() > 0) {

			LOG.warn("Ya hay semanas en la base de datos.");
			return "Error al actualizar las semanas: ya hay semanas en la base de datos.";
		}

		List<WeeksEntity> weeksParaDDB = new ArrayList<>();

		weeksParaDDB.add(this.createWeek("-", 0));
		weeksParaDDB.add(this.createWeek("Primana", 1));
		weeksParaDDB.add(this.createWeek("Segana", 2));
		weeksParaDDB.add(this.createWeek("Terana", 3));
		weeksParaDDB.add(this.createWeek("Curana", 4));
		weeksParaDDB.add(this.createWeek("Limana", 5));

		this.weeksRepository.saveAll(weeksParaDDB);

		LOG.info("Semanas actualizadas");

		return "Semanas actualizadas correctamente.";
	}

	/**
	 * EN: Builds one week row in memory.
	 * ES: Construye en memoria una fila de semana.
	 *
	 * @param name         EN: name of the week. / ES: nombre de la semana.
	 * @param weekOfMonths EN: position within the month, 0 to 5. / ES: posición dentro del mes, del 0 al 5.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private WeeksEntity createWeek(String name, int weekOfMonths) {

		WeeksEntity newWeek = new WeeksEntity();
		newWeek.setName(name);
		newWeek.setWeekOfMonth(weekOfMonths);

		return newWeek;
	}
}
