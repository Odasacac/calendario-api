package CCASolutions.Calendario.Others;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DiaCalendarioDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MesLunacionDTO;

/**
 * Organiza los días VAU de una lunación como la rejilla del calendario en
 * PDF (CalendarioAnualPdf): fila Terra, semanas Primana..Limana y 7 columnas.
 * Replica sus reglas para que la pantalla y el PDF cuenten lo mismo:
 * - Terra no tiene mes propio: abre el mes que empieza al día siguiente.
 * - El día del solsticio o equinoccio no pertenece a ninguna estación (0).
 * - Limana reutiliza las primeras columnas.
 */
public class LunacionVAU {

	public static final String SIN_VALOR = "-";
	private static final int PRIMER_DIA_LIMANA = 8;

	private final Map<String, Integer> numeroDeEstacion;
	private final Map<String, Integer> semanasPorNombre;
	private final Map<String, Integer> diasPorNombre;
	private final List<String> semanas;
	private final List<String> diasSemana;

	/**
	 * @param numeroDeEstacion nombre de estación -> 0..4 (tabla seasons)
	 * @param semanasPorNombre nombre de semana -> 0..5 (tabla weeks)
	 * @param diasPorNombre    nombre de día -> 0..9 (tabla days)
	 * @param semanas          nombres de semana por número (0 = Terra)
	 * @param diasSemana       nombres de Luno a Saturino
	 */
	public LunacionVAU(Map<String, Integer> numeroDeEstacion, Map<String, Integer> semanasPorNombre, Map<String, Integer> diasPorNombre,
			List<String> semanas, List<String> diasSemana) {

		this.numeroDeEstacion = numeroDeEstacion;
		this.semanasPorNombre = semanasPorNombre;
		this.diasPorNombre = diasPorNombre;
		this.semanas = semanas;
		this.diasSemana = diasSemana;
	}

	/** Lunación a partir de sus días en orden (de Terra a la víspera de la siguiente Terra). */
	public LunacionDTO construir(List<DateDTO> dias) {

		String[] meses = nombresDeMes(dias);
		int[] estaciones = this.estacionesDeCadaDia(dias);

		LunacionDTO lunacion = new LunacionDTO();
		lunacion.setInicio(LocalDate.parse(dias.get(0).getFechaO()));
		lunacion.setFin(LocalDate.parse(dias.get(dias.size() - 1).getFechaO()));
		lunacion.setSemanas(this.semanas);
		lunacion.setDiasSemana(this.diasSemana);

		List<DiaCalendarioDTO> celdas = new ArrayList<>();
		List<MesLunacionDTO> mesesDeLaLunacion = new ArrayList<>();
		List<Integer> estacionesDeLaLunacion = new ArrayList<>();

		for(int i = 0; i < dias.size(); i++) {

			DateDTO dia = dias.get(i);
			celdas.add(this.celda(dia, meses[i], estaciones[i]));

			if(tieneDatos(dia)) {

				MesLunacionDTO ultimo = mesesDeLaLunacion.isEmpty() ? null : mesesDeLaLunacion.get(mesesDeLaLunacion.size() - 1);
				if(ultimo == null || !ultimo.getNombre().equals(meses[i])) {
					MesLunacionDTO mes = new MesLunacionDTO();
					mes.setNombre(meses[i]);
					mes.setEstacion(estaciones[i]);
					mesesDeLaLunacion.add(mes);
					ultimo = mes;
				}
				if(ultimo.getApellido() == null && !esTerra(dia) && texto(dia.getMonth().getSurname()) != null) {
					ultimo.setApellido(dia.getMonth().getSurname());
				}

				if(estaciones[i] != 0 && !estacionesDeLaLunacion.contains(estaciones[i])) {
					estacionesDeLaLunacion.add(estaciones[i]);
				}

				String anyo = dia.getYear() == null ? null : texto(dia.getYear().getSolsticiosDeInviernoSinceLastMetonIN());
				if(anyo != null) {
					if(lunacion.getAnyo() == null) {
						lunacion.setAnyo(anyo);
					}
					lunacion.setAnyoFinal(anyo);
				}

				if(dia.getYear() != null && dia.getYear().isEsSolsticioDeInvierno()) {
					lunacion.setCambioDeAnyo(LocalDate.parse(dia.getFechaO()));
				}

				if(lunacion.getMetono() == null && dia.getMetonoVAU() != null && dia.getMetonoVAU().getMetonsIN() != null) {
					lunacion.setMetono(dia.getMetonoVAU().getMetonsIN().getNumberOfMetonIN());
					lunacion.setApellidoMetono(dia.getMetonoVAU().getMetonsIN().getLastMetonSurname());
				}
			}
		}

		lunacion.setDias(celdas);
		lunacion.setMeses(mesesDeLaLunacion);
		lunacion.setEstaciones(estacionesDeLaLunacion);
		return lunacion;
	}

	/** Mes de cada día; Terra toma el del día siguiente (como mucho dos días por delante). */
	public static String[] nombresDeMes(List<DateDTO> dias) {

		String[] meses = new String[dias.size()];

		for(int i = 0; i < dias.size(); i++) {

			DateDTO dia = dias.get(i);
			String nombre = tieneDatos(dia) ? dia.getMonth().getName() : null;

			for(int siguiente = i + 1; SIN_VALOR.equals(nombre) && siguiente < dias.size() && siguiente <= i + 2; siguiente++) {
				if(tieneDatos(dias.get(siguiente))) {
					nombre = dias.get(siguiente).getMonth().getName();
				}
			}

			meses[i] = nombre == null || SIN_VALOR.equals(nombre) ? null : nombre;
		}

		return meses;
	}

	public static boolean tieneDatos(DateDTO dia) {
		return dia.isFechaEncontrada() && dia.getMonth() != null && dia.getWeek() != null && dia.getDay() != null;
	}

	public static boolean esTerra(DateDTO dia) {
		return tieneDatos(dia) && dia.getMonth().isNewMoon();
	}

	/** Métono, eclípeno o eclipse: los eventos que el PDF resalta. */
	public static boolean esEventoResenyable(String evento) {
		return evento != null && (evento.startsWith("Métono") || evento.startsWith("Eclípeno") || evento.startsWith("Eclipse"));
	}

	// ------------------------------------------------------------------

	private DiaCalendarioDTO celda(DateDTO dia, String mes, int estacion) {

		DiaCalendarioDTO celda = new DiaCalendarioDTO();
		celda.setFecha(LocalDate.parse(dia.getFechaO()));
		celda.setSinDatos(!tieneDatos(dia));
		celda.setEstacion(estacion);
		celda.setMes(mes);

		if(!tieneDatos(dia)) {
			return celda;
		}

		celda.setTerra(esTerra(dia));
		String[] partes = dia.getDay().split(" ", 2);
		celda.setDia(partes[0]);
		celda.setApellidoDia(partes.length > 1 ? partes[1] : null);

		Integer semana = this.semanasPorNombre.get(dia.getWeek());
		Integer diaDeLaSemana = this.diasPorNombre.get(partes[0]);
		if(!celda.isTerra() && semana != null && diaDeLaSemana != null && semana >= 1 && diaDeLaSemana >= 1) {
			celda.setSemana(semana);
			celda.setColumna(diaDeLaSemana < PRIMER_DIA_LIMANA ? diaDeLaSemana - 1 : diaDeLaSemana - PRIMER_DIA_LIMANA);
		}

		String festividad = dia.getFestividades() == null ? null : texto(dia.getFestividades().getFestividadActual());
		String evento = dia.getNotableEvent() == null ? null : texto(dia.getNotableEvent().getToday());
		celda.setFestividad(festividad);
		celda.setEvento(evento);
		celda.setEventoResenyable(esEventoResenyable(evento));
		celda.setEclipse(evento != null && evento.startsWith("Eclipse"));
		return celda;
	}

	/**
	 * Estación de cada día por su nombre. Sin datos, toma la del siguiente día
	 * que la tenga (o la del anterior, al final de la lunación).
	 */
	private int[] estacionesDeCadaDia(List<DateDTO> dias) {

		Integer[] propia = new Integer[dias.size()];
		for(int i = 0; i < dias.size(); i++) {
			DateDTO dia = dias.get(i);
			if(tieneDatos(dia) && dia.getSeason() != null && dia.getSeason().getName() != null) {
				propia[i] = this.numeroDeEstacion.get(dia.getSeason().getName());
			}
		}

		int[] estaciones = new int[dias.size()];
		for(int i = 0; i < dias.size(); i++) {

			Integer estacion = propia[i];
			for(int siguiente = i + 1; estacion == null && siguiente < dias.size(); siguiente++) {
				estacion = propia[siguiente];
			}
			if(estacion == null) {
				estacion = i > 0 ? estaciones[i - 1] : 0;
			}
			estaciones[i] = estacion;
		}
		return estaciones;
	}

	private static String texto(String valor) {
		return valor == null || valor.isBlank() || SIN_VALOR.equals(valor) ? null : valor;
	}
}
