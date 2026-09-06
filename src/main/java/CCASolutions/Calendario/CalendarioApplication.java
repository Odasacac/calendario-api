package CCASolutions.Calendario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EN: Entry point of the Calendario service, the backend that converts Gregorian dates
 * into the VAU calendar and keeps the astronomical tables it relies on.
 * ES: Punto de entrada del servicio Calendario, el backend que convierte fechas
 * gregorianas al calendario VAU y mantiene las tablas astronómicas en las que se apoya.
 */
@SpringBootApplication
public class CalendarioApplication {

	/**
	 * EN: Boots the Spring context and starts the embedded web server.
	 * ES: Arranca el contexto de Spring y levanta el servidor web embebido.
	 *
	 * @param args EN: command-line arguments passed on to Spring Boot. / ES: argumentos de línea de comandos que se pasan a Spring Boot.
	 */
	public static void main(String[] args) {
		SpringApplication.run(CalendarioApplication.class, args);
	}

}
