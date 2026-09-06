package CCASolutions.Calendario.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import jakarta.annotation.PostConstruct;

/**
 * EN: Application-wide beans: the password encoder, the cache manager for the reference
 * tables, and the start-up check that guarantees there is an administrator password.
 * ES: Beans de ámbito general de la aplicación: el codificador de contraseñas, el gestor
 * de caché de las tablas de referencia y la comprobación de arranque que garantiza que
 * existe una contraseña de administrador.
 */
@Configuration
@EnableCaching
public class ClasesBean
{
	private static final Logger LOG = LoggerFactory.getLogger(ClasesBean.class);

	private static final String CONCEPTO_PASSWORD = "PW";

	@Autowired
	private DatosRepository datosRepository;

	/**
	 * EN: BCrypt encoder used to store and verify the administrator password.
	 * ES: Codificador BCrypt con el que se almacena y se verifica la contraseña de administrador.
	 *
	 * @return EN: the shared encoder instance. / ES: la instancia compartida del codificador.
	 */
	@Bean
	BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
    }

	/**
	 * EN: In-memory cache for the reference tables. They are a few dozen rows that only
	 * change when the database is repopulated, so a concurrent map is enough and it saves
	 * five to eight round trips to MySQL on every date conversion.
	 * ES: Caché en memoria para las tablas de referencia. Son unas pocas decenas de filas
	 * que sólo cambian al repoblar la base de datos, así que un mapa concurrente basta
	 * y evita ir a MySQL entre cinco y ocho veces por cada conversión de fecha.
	 *
	 * @return EN: the cache manager holding the five reference caches. / ES: el gestor de caché con las cinco cachés de referencia.
	 */
	@Bean
	CacheManager cacheManager()
	{
		return new ConcurrentMapCacheManager("semanas", "dias", "meses", "estaciones", "festividades");
	}

	/**
	 * EN: On start-up, creates the default administrator password if the {@code datos}
	 * table does not have one yet, so the population endpoint is never left unprotected.
	 * ES: Al arrancar, crea la contraseña de administrador por defecto si la tabla
	 * {@code datos} todavía no tiene ninguna, para que el endpoint de poblado no quede
	 * nunca desprotegido.
	 */
    @PostConstruct
    void setAdminPassword() {

    	DatosEntity dbPassword = this.datosRepository.findByConcepto(CONCEPTO_PASSWORD);

    	if(dbPassword == null) {

    		dbPassword = new DatosEntity();
    		dbPassword.setConcepto(CONCEPTO_PASSWORD);
    		dbPassword.setValor(this.passwordEncoder().encode("admintest"));
    		this.datosRepository.save(dbPassword);

    		LOG.warn("No había contraseña de administrador en la BD: se ha creado la contraseña por defecto.");
    	}
    }
}
