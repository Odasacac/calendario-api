package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.DateVAUDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DaysService;

/**
 * EN: Manages the VAU days table and the reverse conversion from a VAU week and day back
 * into an offset in days from the new moon.
 * ES: Gestiona la tabla de días VAU y la conversión inversa de una semana y un día VAU a
 * un desfase en días desde la luna nueva.
 */
@Service
public class DaysServiceImpl implements DaysService{

	private static final Logger LOG = LoggerFactory.getLogger(DaysServiceImpl.class);

	@Autowired
	private WeeksRepository weeksRepository;

	@Autowired
	private DaysRepository daysRepository;

	/**
	 * EN: Inserts the ten fixed day rows, from Terra to Caelumbra. Does nothing if the
	 * table already has rows.
	 * ES: Inserta las diez filas fijas de días, de Terra a Caelumbra. No hace nada si la
	 * tabla ya tiene filas.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateDays() {

		LOG.info("Actualizando los Días.");

		if(this.daysRepository.count() > 0) {

			LOG.warn("Ya hay días en la base de datos.");
			return "Error al actualizar los días: ya hay días en la base de datos.";
		}

		List<DaysEntity> daysParaBD = new ArrayList<>();

		daysParaBD.add(this.createDay(0, "Terra"));
		daysParaBD.add(this.createDay(1, "Luno"));
		daysParaBD.add(this.createDay(2, "Sole"));
		daysParaBD.add(this.createDay(3, "Merco"));
		daysParaBD.add(this.createDay(4, "Venuro"));
		daysParaBD.add(this.createDay(5, "Marto"));
		daysParaBD.add(this.createDay(6, "Júpeno"));
		daysParaBD.add(this.createDay(7, "Saturino"));
		daysParaBD.add(this.createDay(8, "Liminol"));
		daysParaBD.add(this.createDay(9, "Caelumbra"));

		this.daysRepository.saveAll(daysParaBD);

		LOG.info("Days actualizados");

		return "Días actualizados correctamente.";
	}

	/**
	 * EN: Reverse operation of the conversion: given a VAU week and day, works out how many
	 * days must be added to the new moon to reach that date. From the fourth week onwards
	 * the offset stays at 21, because the last week is shorter.
	 * ES: Operación inversa de la conversión: dada una semana y un día VAU, calcula cuántos
	 * días hay que sumar a la luna nueva para llegar a esa fecha. A partir de la cuarta
	 * semana el desfase se mantiene en 21, porque la última semana es más corta.
	 *
	 * @param dateVAU EN: VAU date holding the week and day names. / ES: fecha VAU con los nombres de semana y día.
	 * @return EN: days to add, or 0 if the week or day is unknown. / ES: días a sumar, o 0 si la semana o el día no existen.
	 */
	public long getDiasASumarALaLunaNueva(DateVAUDTO dateVAU) {

		WeeksEntity semana = this.weeksRepository.findByName(dateVAU.getWeek());
		DaysEntity dia = this.daysRepository.findByName(dateVAU.getDay());

		// Ninguno de los dos se comprobaba antes de usarse
		if (semana == null || dia == null) {

			LOG.warn("No se ha encontrado la semana '{}' o el día '{}' en la base de datos", dateVAU.getWeek(), dateVAU.getDay());
			return 0L;
		}

		switch(semana.getWeekOfMonth()) {

			case 1:
				return dia.getDayOfWeek();

			case 2:
				return dia.getDayOfWeek() + 7L;

			case 3:
				return dia.getDayOfWeek() + 14L;

			// A partir de la cuarta semana el desfase se mantiene en 21 días
			case 4:
			case 5:
				return dia.getDayOfWeek() + 21L;

			default:
				return 0L;
		}
	}

	/**
	 * EN: Builds one day row in memory.
	 * ES: Construye en memoria una fila de día.
	 *
	 * @param dayOfWeek EN: position within the week, 0 to 9. / ES: posición dentro de la semana, del 0 al 9.
	 * @param name      EN: name of the day. / ES: nombre del día.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private DaysEntity createDay(int dayOfWeek, String name) {

		DaysEntity newDato = new DaysEntity();
		newDato.setDayOfWeek(dayOfWeek);
		newDato.setName(name);

		return newDato;
	}
}
