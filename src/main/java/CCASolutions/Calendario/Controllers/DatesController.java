package CCASolutions.Calendario.Controllers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Services.DatesService;

/**
 * EN: Public endpoints that convert a Gregorian date into its VAU calendar equivalent.
 * ES: Endpoints públicos que convierten una fecha gregoriana en su equivalente del
 * calendario VAU.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/conversiontovau")
public class DatesController {

	private static final Logger LOG = LoggerFactory.getLogger(DatesController.class);

	private static final int PRIMER_ANYO_SOPORTADO = 0;
	private static final int ULTIMO_ANYO_SOPORTADO = 2099;

	@Autowired
	private DatesService datesService;

	/**
	 * EN: Converts the date given as a query parameter. Dates outside the range covered by
	 * the database answer 200 with {@code fechaEncontrada = false} and an explanatory
	 * message, which is the contract the frontend already relies on.
	 * ES: Convierte la fecha que llega como parámetro de consulta. Las fechas fuera del
	 * rango cubierto por la base de datos responden 200 con {@code fechaEncontrada = false}
	 * y un mensaje explicativo, que es el contrato que ya usa el frontal.
	 *
	 * @param date EN: Gregorian date to convert, in ISO format. / ES: fecha gregoriana a convertir, en formato ISO.
	 * @return EN: 200 with the conversion, or 500 if the calculation fails. / ES: 200 con la conversión, o 500 si el cálculo falla.
	 */
	@GetMapping("/selected")
	public ResponseEntity<DateDTO> getDateVAU(@RequestParam LocalDate date) {

		LOG.info("Fecha requerida: {}", date);

		if(date.getYear() < PRIMER_ANYO_SOPORTADO || ULTIMO_ANYO_SOPORTADO < date.getYear()) {

			DateDTO body = new DateDTO();
			body.setFechaEncontrada(false);
			body.setMensaje("Error al obtener dateVAU: fecha fuera del rango: " + date);
			body.setFechaO(String.valueOf(date));

			LOG.warn(body.getMensaje());

			return new ResponseEntity<>(body, HttpStatus.OK);
		}

		return this.convertir(date);
	}

	/**
	 * EN: Converts today's date. Shortcut so the frontend does not have to build the date
	 * itself; the range check is unnecessary because the current date is always inside it.
	 * ES: Convierte la fecha de hoy. Atajo para que el frontal no tenga que construir la
	 * fecha; no hace falta comprobar el rango porque la fecha actual siempre está dentro.
	 *
	 * @return EN: 200 with today's conversion, or 500 if the calculation fails. / ES: 200 con la conversión de hoy, o 500 si el cálculo falla.
	 */
	@GetMapping("/today")
	public ResponseEntity<DateDTO> getTodayVAU() {

		LocalDate today = LocalDate.now();

		LOG.info("Fecha hoy requerida: {}", today);

		return this.convertir(today);
	}

	/**
	 * EN: Shared body of the two endpoints: delegates to the service and turns any
	 * exception into a 500 carrying the error message.
	 * ES: Cuerpo común de los dos endpoints: delega en el servicio y convierte cualquier
	 * excepción en un 500 que lleva el mensaje de error.
	 *
	 * @param date EN: date to convert. / ES: fecha a convertir.
	 * @return EN: the HTTP response to send back to the client. / ES: la respuesta HTTP que se devuelve al cliente.
	 */
	private ResponseEntity<DateDTO> convertir(LocalDate date) {

		try {

			DateDTO body = this.datesService.getDateVAUFromDateO(date);

			LOG.info("{} convertida con éxito", date);

			return new ResponseEntity<>(body, HttpStatus.OK);
		}
		catch (Exception e) {

			LOG.error("Error al convertir la fecha {}", date, e);

			DateDTO body = new DateDTO();
			body.setMensaje("Error:" + e);
			body.setFechaO(String.valueOf(date));

			return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
