package CCASolutions.Calendario.Utils;

/**
 * EN: Breaks down the ISO dates returned by the OPALE API.
 * <p>
 * ES: Descomposición de las fechas ISO que devuelve la API de OPALE.
 * <p>
 * EN: Dates before year 1 arrive with a sign ("-4700-01-01T12:00:00"), so they cannot
 * simply be parsed with LocalDateTime. This splitting was copied verbatim in four
 * places (moon phases, lunar eclipses, solar eclipses and solstices/equinoxes).
 * <p>
 * ES: Las fechas anteriores al año 1 llegan con signo ("-4700-01-01T12:00:00"), de
 * modo que no se pueden parsear con LocalDateTime sin más. Este troceo estaba
 * copiado literalmente en cuatro sitios (lunas, eclipses lunares, eclipses
 * solares y solsticios/equinoccios).
 */
public final class FechasApi {

	/**
	 * EN: Utility class, never instantiated.
	 * ES: Clase de utilidad, no se instancia nunca.
	 */
	private FechasApi() {
	}

	/**
	 * EN: Splits an ISO date coming from the API into its year, month, day, hour, minute
	 * and second components, handling the negative years of the proleptic calendar.
	 * ES: Trocea una fecha ISO procedente de la API en sus componentes de año, mes, día,
	 * hora, minuto y segundo, teniendo en cuenta los años negativos del calendario proléptico.
	 *
	 * @param fechaApi EN: value returned by the API; converted with {@code String.valueOf}. / ES: valor devuelto por la API; se convierte con {@code String.valueOf}.
	 * @return EN: the six components of the date. / ES: los seis componentes de la fecha.
	 */
	public static Descompuesta descomponer(Object fechaApi) {

		String fecha = String.valueOf(fechaApi);

		String[] partes = fecha.split("T");
		String[] partesFecha = partes[0].split("-");
		String[] partesHora = partes[1].split(":");

		int year;
		int month;
		int day;

		if (fecha.startsWith("-")) {

			// EN: the split leaves an empty first element, so the fields are shifted by one
			// ES: el split deja un primer elemento vacío: los campos van desplazados uno
			year = Integer.parseInt("-" + partesFecha[1]);
			month = Integer.parseInt(partesFecha[2]);
			day = Integer.parseInt(partesFecha[3]);
		}
		else {

			year = Integer.parseInt(partesFecha[0]);
			month = Integer.parseInt(partesFecha[1]);
			day = Integer.parseInt(partesFecha[2]);
		}

		int hour = Integer.parseInt(partesHora[0]);
		int minute = Integer.parseInt(partesHora[1]);
		int second = Integer.parseInt(partesHora[2]);

		return new Descompuesta(year, month, day, hour, minute, second);
	}

	/**
	 * EN: The six numeric components of a date, as the historical tables
	 * (all_faseslunares, all_eclipses, all_soes) store them.
	 * ES: Los seis componentes numéricos de una fecha, tal y como los almacenan las
	 * tablas históricas (all_faseslunares, all_eclipses, all_soes).
	 */
	public static final class Descompuesta {

		private final int year;
		private final int month;
		private final int day;
		private final int hour;
		private final int minute;
		private final int second;

		/**
		 * EN: Private constructor; instances only come from {@link FechasApi#descomponer}.
		 * ES: Constructor privado; las instancias sólo salen de {@link FechasApi#descomponer}.
		 */
		private Descompuesta(int year, int month, int day, int hour, int minute, int second) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}

		/**
		 * EN: Year; negative for dates before year 1.
		 * ES: Año; negativo para las fechas anteriores al año 1.
		 */
		public int getYear() {
			return year;
		}

		/**
		 * EN: Month of the year, 1 to 12.
		 * ES: Mes del año, del 1 al 12.
		 */
		public int getMonth() {
			return month;
		}

		/**
		 * EN: Day of the month, 1 to 31.
		 * ES: Día del mes, del 1 al 31.
		 */
		public int getDay() {
			return day;
		}

		/**
		 * EN: Hour of the day, 0 to 23.
		 * ES: Hora del día, de 0 a 23.
		 */
		public int getHour() {
			return hour;
		}

		/**
		 * EN: Minute of the hour, 0 to 59.
		 * ES: Minuto de la hora, de 0 a 59.
		 */
		public int getMinute() {
			return minute;
		}

		/**
		 * EN: Second of the minute, 0 to 59.
		 * ES: Segundo del minuto, de 0 a 59.
		 */
		public int getSecond() {
			return second;
		}
	}
}
