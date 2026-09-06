package CCASolutions.Calendario.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.DatosEntity;

/**
 * EN: Key/value store holding the OPALE API URLs and the hashed administrator password.
 * ES: Almacén de clave/valor con las URLs de las APIs de OPALE y la contraseña de
 * administrador cifrada.
 */
public interface DatosRepository extends JpaRepository <DatosEntity, Long> {

	/**
	 * EN: One entry by its key: "PW" for the password, "YLP", "ASYEF", "LEPY", "SEPY" and
	 * "APG" for the API URLs.
	 * ES: Una entrada por su clave: "PW" para la contraseña, y "YLP", "ASYEF", "LEPY",
	 * "SEPY" y "APG" para las URLs de las APIs.
	 */
	 DatosEntity findByConcepto(String concepto);

	/**
	 * EN: Several entries at once, to avoid one query per key.
	 * ES: Varias entradas de una vez, para no hacer una consulta por clave.
	 */
	 List<DatosEntity> findByConceptoIn(List<String> conceptos);
}
