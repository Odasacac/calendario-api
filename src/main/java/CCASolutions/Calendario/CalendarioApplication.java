package CCASolutions.Calendario;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalendarioApplication {

	/**
	 * Todo el back trabaja en UTC, se despliegue donde se despliegue: las fechas de la
	 * BD están en UTC (como las da OPALE) y «hoy» es el día UTC.
	 */
	public static final String ZONA = "UTC";

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZONA));
		SpringApplication.run(CalendarioApplication.class, args);
	}

}
