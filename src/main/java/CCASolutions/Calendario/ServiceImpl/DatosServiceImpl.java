package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Services.DatosService;

/**
 * EN: Manages the {@code datos} table, the key/value store holding the OPALE API URLs and
 * the administrator password.
 * ES: Gestiona la tabla {@code datos}, el almacén de clave/valor con las URLs de las APIs
 * de OPALE y la contraseña de administrador.
 */
@Service
public class DatosServiceImpl implements DatosService {

	private static final Logger LOG = LoggerFactory.getLogger(DatosServiceImpl.class);

	private static final String CONCEPTO_PASSWORD = "PW";

	@Autowired
	private DatosRepository datosRepository;

	/**
	 * EN: Inserts the five OPALE API URLs. It only runs when the table holds exactly one
	 * row and that row is the administrator password, which is the state right after a
	 * clean start-up.
	 * ES: Inserta las cinco URLs de las APIs de OPALE. Sólo se ejecuta cuando la tabla tiene
	 * exactamente una fila y esa fila es la contraseña de administrador, que es el estado
	 * justo después de un arranque limpio.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateDatos() {

		LOG.info("Actualizando los Datos.");

		List<DatosEntity> allDatos = this.datosRepository.findAll();

		// Correspondiente a la contraseña del administrador
		if(allDatos.size() != 1 || !CONCEPTO_PASSWORD.equals(allDatos.get(0).getConcepto())) {

			LOG.warn("Ya hay datos en la base de datos");
			return "Error al actualizar los datos, checkear logs: ya hay datos en la base de datos.";
		}

		List<DatosEntity> datosParaDB = new ArrayList<>();

		datosParaDB.add(this.createDato("ASYEF", "https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}"));
		datosParaDB.add(this.createDato("YLP", "https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}"));
		datosParaDB.add(this.createDato("LEPY", "https://opale.imcce.fr/api/v1/phenomena/eclipses/301/{{YYYY}}"));
		datosParaDB.add(this.createDato("SEPY", "https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}"));
		datosParaDB.add(this.createDato("APG", "https://opale.imcce.fr/api/v1/phenomena/distances?date={{YYYY-MM-DD}}&nbd={{DDDD}}&bodies=399,301&calendar=gregorian"));

		this.datosRepository.saveAll(datosParaDB);

		LOG.info("Datos actualizados");

		return "Datos actualizados correctamente.";
	}

	/**
	 * EN: Builds one key/value row in memory.
	 * ES: Construye en memoria una fila de clave/valor.
	 *
	 * @param concepto EN: key, for instance "YLP". / ES: clave, por ejemplo "YLP".
	 * @param valor    EN: value, usually a URL template. / ES: valor, normalmente una plantilla de URL.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private DatosEntity createDato(String concepto, String valor) {

		DatosEntity newDato = new DatosEntity();
		newDato.setConcepto(concepto);
		newDato.setValor(valor);

		return newDato;
	}
}
