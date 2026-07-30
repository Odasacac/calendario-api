package CCASolutions.Calendario.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;

/*
 * ==============================================================================
 * EN: Shared beans and the first-run administrator password.
 *
 *     Two things changed here:
 *
 *       - The initial password is no longer the literal "admintest" compiled into the
 *         jar. It is read from a property, which can be supplied as an environment
 *         variable, and it still falls back to the old value so an existing
 *         deployment keeps working. It is only ever used on a brand new database.
 *       - The seeding moved from @PostConstruct to ApplicationReadyEvent. Running a
 *         repository call from @PostConstruct of a @Configuration class forces the
 *         persistence layer to initialise earlier than Spring intends, and it happens
 *         before the datasource is fully validated; on ApplicationReadyEvent the
 *         context is complete and a database that is down is reported cleanly instead
 *         of breaking the startup of the whole application.
 *
 * ES: Beans compartidos y la contrasena de administrador del primer arranque.
 *
 *     Aqui han cambiado dos cosas:
 *
 *       - La contrasena inicial ya no es el literal "admintest" compilado en el jar. Se lee
 *         de una propiedad, que se puede suministrar como variable de entorno, y sigue
 *         recurriendo al valor antiguo para que un despliegue existente siga funcionando.
 *         Solo se usa sobre una base de datos completamente nueva.
 *       - La siembra pasa de @PostConstruct a ApplicationReadyEvent. Llamar a un repositorio
 *         desde el @PostConstruct de una clase @Configuration obliga a la capa de
 *         persistencia a inicializarse antes de lo que Spring pretende, y ocurre antes de
 *         que el datasource este validado del todo; en ApplicationReadyEvent el contexto
 *         esta completo y una base de datos caida se informa limpiamente en lugar de romper
 *         el arranque de toda la aplicacion.
 * ==============================================================================
 */
@Configuration
public class ClasesBean {

	private static final Logger log = LoggerFactory.getLogger(ClasesBean.class);

	/*
	 * EN: Key under which the administrator password hash lives in the "datos" table.
	 * ES: Clave bajo la que vive el hash de la contrasena de administrador en "datos".
	 */
	private static final String CONCEPTO_PASSWORD = "PW";

	private final DatosRepository datosRepository;

	/*
	 * EN: Initial administrator password, only applied to an empty database.
	 * ES: Contrasena inicial de administrador, solo se aplica a una base de datos vacia.
	 */
	@Value("${calendario.admin.password-inicial:admintest}")
	private String passwordInicial;

	public ClasesBean(DatosRepository datosRepository) {
		this.datosRepository = datosRepository;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * EN: Stores the hashed administrator password the first time the application runs
	 *     against an empty database.
	 * ES: Guarda el hash de la contrasena de administrador la primera vez que la
	 *     aplicacion se ejecuta contra una base de datos vacia.
	 */
	@EventListener(ApplicationReadyEvent.class)
	void crearPasswordDeAdministradorSiHaceFalta() {

		try {

			if (this.datosRepository.findByConcepto(CONCEPTO_PASSWORD) != null) {
				return;
			}

			DatosEntity password = new DatosEntity();
			password.setConcepto(CONCEPTO_PASSWORD);
			password.setValor(new BCryptPasswordEncoder().encode(this.passwordInicial));
			this.datosRepository.save(password);

			log.info("Contraseña de administrador inicial creada. Cámbiala con la propiedad "
					+ "calendario.admin.password-inicial antes de exponer la API.");

		} catch (Exception e) {

			log.error("No se ha podido comprobar o crear la contraseña de administrador", e);
		}
	}
}
