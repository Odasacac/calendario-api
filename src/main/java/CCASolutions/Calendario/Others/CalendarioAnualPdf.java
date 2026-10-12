package CCASolutions.Calendario.Others;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import CCASolutions.Calendario.DTOs.DateDTO;

public class CalendarioAnualPdf {

	private static final Color TINTA = new Color(0x1F, 0x29, 0x37);
	private static final Color TINTA_SUAVE = new Color(0x5F, 0x6B, 0x7A);
	private static final Color LINEA = new Color(0xC4, 0xCB, 0xD3);
	private static final Color FONDO_CABECERA = new Color(0xEE, 0xF1, 0xF4);
	private static final Color FONDO_FESTIVO = new Color(0xFF, 0xF1, 0xCC);
	private static final Color TEXTO_FESTIVO = new Color(0x8A, 0x52, 0x00);
	private static final Color FONDO_EVENTO = new Color(0xDB, 0xEA, 0xFB);
	private static final Color TEXTO_EVENTO = new Color(0x1C, 0x4E, 0x8A);
	private static final Color FONDO_FESTIVO_Y_EVENTO = new Color(0xFF, 0xD8, 0xB0);
	private static final Color TEXTO_FESTIVO_Y_EVENTO = new Color(0x9C, 0x3D, 0x00);
	private static final Color FONDO_SEGUNDO_MES = new Color(0xF1, 0xED, 0xF8);
	private static final Map<Integer, Color> COLOR_ESTACION = Map.of(
			0, TINTA_SUAVE,
			1, new Color(0x2F, 0x5F, 0x98),
			2, new Color(0x3C, 0x7D, 0x3E),
			3, new Color(0xB3, 0x26, 0x26),
			4, new Color(0x7B, 0x4A, 0x26));
	private static final Map<Integer, String> ADJETIVO_ESTACION = Map.of(
			1, "invernal",
			2, "primaveral",
			3, "estival",
			4, "otoñal");

	private static final String[] DIAS_SEMANA = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
	private static final String[] DIAS_SEMANA_CORTOS = {"lun", "mar", "mié", "jue", "vie", "sáb", "dom"};
	private static final String[] MESES = {"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
	private static final String[] MESES_CORTOS = {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};

	private static final Rectangle A4_HORIZONTAL = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
	private static final String DESTINO_FESTIVIDADES = "festividades";
	private static final String DESTINO_EVENTOS = "eventos";

	private static final String SIN_DATOS = "Sin datos VAU";
	private static final String SIN_VALOR = "-";
	private static final int DIAS_POR_SEMANA = 7;
	private static final int PRIMER_DIA_LIMANA = 8;
	private static final float ANCHO_ETIQUETA_SEMANA = 62f;
	private static final float TAMANYO_MAXIMO_ESTRUCTURA = 8.5f;
	private static final float TAMANYO_MINIMO_ESTRUCTURA = 5f;
	private static final float ESPACIO_ENTRE_SECCIONES = 18f;
	private static final float MARGEN_DE_SEGURIDAD = 30f;

	private final int anyoCivil;
	private final LocalDate solsticioInicioAnyo;
	private final LocalDate ultimoEclipenoInvernalApofasalRemoto;
	private final List<DateDTO> dias;
	private final Map<Integer, String> estacionesPorNumero;
	private final Map<String, Integer> semanasPorNombre;
	private final Map<String, Integer> diasPorNombre;

	private final List<MesDelCalendario> meses = new ArrayList<>();
	private final List<TramoDeEstacion> tramos = new ArrayList<>();
	private final List<List<Integer>> lunaciones = new ArrayList<>();
	private final String[] mesDeCadaDia;
	private final int[] estacionDeCadaDia;
	private String anyoVAU = SIN_VALOR;
	private String metonoInvernalNuevo = SIN_VALOR;

	public CalendarioAnualPdf(int anyoCivil, LocalDate solsticioInicioAnyo, LocalDate ultimoEclipenoInvernalApofasalRemoto, List<DateDTO> dias,
			Map<Integer, String> estacionesPorNumero, Map<String, Integer> semanasPorNombre, Map<String, Integer> diasPorNombre) {

		this.anyoCivil = anyoCivil;
		this.solsticioInicioAnyo = solsticioInicioAnyo;
		this.ultimoEclipenoInvernalApofasalRemoto = ultimoEclipenoInvernalApofasalRemoto;
		this.dias = dias;
		this.estacionesPorNumero = estacionesPorNumero;
		this.semanasPorNombre = semanasPorNombre;
		this.diasPorNombre = diasPorNombre;
		this.mesDeCadaDia = new String[dias.size()];
		this.estacionDeCadaDia = new int[dias.size()];
	}

	public byte[] generar() {

		this.prepararEstructura();

		ByteArrayOutputStream salida = new ByteArrayOutputStream();
		Document documento = new Document(A4_HORIZONTAL, 28, 28, 40, 32);
		PdfWriter writer = PdfWriter.getInstance(documento, salida);
		writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
		writer.setPageEvent(new CabeceraYPie());

		documento.addTitle("Calendario VAU · Año " + this.anyoVAU + " · Métono invernal nuevo " + this.metonoInvernalNuevo);
		documento.addSubject("Calendario VAU de referencia para el año civil " + this.anyoCivil);
		documento.addAuthor("CalendarioVAU");
		documento.addCreator("CalendarioVAU");
		documento.open();

		this.escribirPortada(documento, writer);
		documento.newPage();
		this.escribirFestividades(documento);
		this.escribirEventosResenyables(documento);
		this.escribirLeyenda(documento);

		for(List<Integer> lunacion : this.lunaciones) {
			documento.newPage();
			this.escribirLunacion(documento, lunacion);
		}

		this.escribirMarcadores(writer);
		documento.close();

		return salida.toByteArray();
	}

	private void prepararEstructura() {

		for(int i = 0; i < this.dias.size(); i++) {
			this.mesDeCadaDia[i] = this.calcularMes(i);
		}
		this.calcularEstaciones();

		List<Integer> lunacionActual = null;
		MesDelCalendario mesActual = null;
		TramoDeEstacion tramoActual = null;

		for(int i = 0; i < this.dias.size(); i++) {

			if(lunacionActual == null || this.esTerra(this.dias.get(i))) {
				lunacionActual = new ArrayList<>();
				this.lunaciones.add(lunacionActual);
			}
			lunacionActual.add(i);

			if(mesActual == null || !mesActual.nombre.equals(this.mesDeCadaDia[i])) {
				mesActual = new MesDelCalendario(this.meses.size(), this.mesDeCadaDia[i]);
				this.meses.add(mesActual);
			}
			mesActual.dias.add(i);

			if(tramoActual == null || tramoActual.mes != mesActual || tramoActual.estacion != this.estacionDeCadaDia[i]) {
				tramoActual = new TramoDeEstacion(mesActual, this.estacionDeCadaDia[i]);
				mesActual.tramos++;
				this.tramos.add(tramoActual);
			}
			tramoActual.dias.add(i);
		}

		DateDTO primerDiaDelAnyo = this.getPrimerDiaDelAnyoVAU();

		if(primerDiaDelAnyo != null) {

			this.anyoVAU = primerDiaDelAnyo.getYear().getSolsticiosDeInviernoSinceLastMetonIN();

			if(primerDiaDelAnyo.getMetonoVAU() != null && primerDiaDelAnyo.getMetonoVAU().getMetonsIN() != null) {

				String apellido = primerDiaDelAnyo.getMetonoVAU().getMetonsIN().getLastMetonSurname();
				this.metonoInvernalNuevo = primerDiaDelAnyo.getMetonoVAU().getMetonsIN().getMetonosINSinceLastEclipenoIN() + (apellido != null ? " " + apellido : "");
			}
		}
	}

	// Terra (luna nueva) no tiene mes propio: abre el mes que empieza al día siguiente
	private String calcularMes(int indice) {

		DateDTO dia = this.dias.get(indice);

		if(!this.tieneDatos(dia)) {
			return SIN_DATOS;
		}

		String nombre = dia.getMonth().getName();

		for(int siguiente = indice + 1; SIN_VALOR.equals(nombre) && siguiente < this.dias.size() && siguiente <= indice + 2; siguiente++) {

			if(this.tieneDatos(this.dias.get(siguiente))) {
				nombre = this.dias.get(siguiente).getMonth().getName();
			}
		}

		return nombre == null || SIN_VALOR.equals(nombre) ? SIN_DATOS : nombre;
	}

	private DateDTO getPrimerDiaDelAnyoVAU() {

		DateDTO primerDia = null;

		for(DateDTO dia : this.dias) {

			boolean esDelAnyo = LocalDate.parse(dia.getFechaO()).isAfter(this.solsticioInicioAnyo);

			if(primerDia == null && esDelAnyo && this.tieneDatos(dia) && dia.getYear() != null && !SIN_VALOR.equals(dia.getYear().getSolsticiosDeInviernoSinceLastMetonIN())) {
				primerDia = dia;
			}
		}

		return primerDia;
	}

	private boolean tieneDatos(DateDTO dia) {
		return dia.isFechaEncontrada() && dia.getMonth() != null && dia.getWeek() != null && dia.getDay() != null;
	}

	private boolean esTerra(DateDTO dia) {
		return this.tieneDatos(dia) && dia.getMonth().isNewMoon();
	}

	// Los meses híbridos se reparten entre dos estaciones: cada día lleva la suya y el día del solsticio o equinoccio abre la nueva
	private void calcularEstaciones() {

		Map<String, Integer> numeroPorNombre = new HashMap<>();
		this.estacionesPorNumero.forEach((numero, nombre) -> numeroPorNombre.put(nombre, numero));

		Integer[] estacionDelDTO = new Integer[this.dias.size()];
		for(int i = 0; i < this.dias.size(); i++) {

			DateDTO dia = this.dias.get(i);

			if(this.tieneDatos(dia) && dia.getSeason() != null && dia.getSeason().getName() != null) {
				estacionDelDTO[i] = numeroPorNombre.get(dia.getSeason().getName());
			}
			if(estacionDelDTO[i] != null && estacionDelDTO[i] == 0) {
				estacionDelDTO[i] = null;
			}
		}

		for(int i = 0; i < this.dias.size(); i++) {

			Integer estacion = estacionDelDTO[i];

			for(int siguiente = i + 1; estacion == null && siguiente < this.dias.size(); siguiente++) {
				estacion = estacionDelDTO[siguiente];
			}

			if(estacion == null) {
				estacion = i > 0 ? this.estacionDeCadaDia[i - 1] : 0;
			}

			this.estacionDeCadaDia[i] = estacion;
		}
	}

	private boolean empiezaEstacion(int indice) {
		return indice > 0 && this.estacionDeCadaDia[indice] != this.estacionDeCadaDia[indice - 1];
	}

	private String getNombreEstacion(int estacion) {
		return this.estacionesPorNumero.getOrDefault(estacion, SIN_VALOR);
	}

	private String getNombreConArticulo(int estacion) {

		String nombre = this.getNombreEstacion(estacion);

		return (nombre.endsWith("a") ? "la " : "el ") + nombre;
	}

	private String getNombreDelTramo(TramoDeEstacion tramo) {

		String adjetivo = ADJETIVO_ESTACION.get(tramo.estacion);

		return tramo.mes.tramos > 1 && adjetivo != null ? tramo.mes.nombre + " (parte " + adjetivo + ")" : tramo.mes.nombre;
	}

	private String getAnyo(List<Integer> indices) {

		for(int indice : indices) {

			DateDTO dia = this.dias.get(indice);

			if(this.tieneDatos(dia) && dia.getYear() != null && !SIN_VALOR.equals(dia.getYear().getSolsticiosDeInviernoSinceLastMetonIN())) {
				return dia.getYear().getSolsticiosDeInviernoSinceLastMetonIN();
			}
		}

		return SIN_VALOR;
	}

	private void escribirPortada(Document documento, PdfWriter writer) {

		LocalDate inicio = this.getFecha(0);
		LocalDate fin = this.getFecha(this.dias.size() - 1);

		Paragraph marca = new Paragraph(this.espaciado("CALENDARIO VAU", 3f, fuente(10, Font.BOLD, COLOR_ESTACION.get(1))));
		marca.setSpacingAfter(6);
		documento.add(marca);

		Paragraph anyo = new Paragraph("AÑO: " + this.anyoVAU, fuente(30, Font.BOLD, TINTA));
		anyo.setLeading(32);
		documento.add(anyo);

		Paragraph metono = new Paragraph("MÉTONO INVERNAL NUEVO: " + this.metonoInvernalNuevo, fuente(17, Font.BOLD, TINTA));
		metono.setSpacingBefore(2);
		documento.add(metono);

		String eclipeno = this.ultimoEclipenoInvernalApofasalRemoto != null ? fechaLarga(this.ultimoEclipenoInvernalApofasalRemoto) : SIN_VALOR;
		Paragraph ultimoEclipeno = new Paragraph("ÚLTIMO ECLÍPENO INVERNAL APOFASAL REMOTO: " + eclipeno, fuente(13, Font.BOLD, TINTA));
		ultimoEclipeno.setSpacingBefore(3);
		documento.add(ultimoEclipeno);

		Paragraph rango = new Paragraph();
		rango.setSpacingBefore(6);
		rango.add(new Chunk("Calendario para el año civil " + this.anyoCivil + ". ", fuente(11, Font.BOLD, TINTA)));
		rango.add(new Chunk("Del " + fechaLarga(inicio) + " al " + fechaLarga(fin) + " (" + this.dias.size() + " días).", fuente(11, Font.NORMAL, TINTA_SUAVE)));
		documento.add(rango);

		Paragraph titulo = titular("Estructura del año");
		titulo.setSpacingBefore(8);
		documento.add(titulo);

		float ancho = documento.right() - documento.left();

		// La tabla tiene que caber entera en la portada: se reduce la letra hasta que quepa
		float disponible = writer.getVerticalPosition(true) - documento.bottom() - MARGEN_DE_SEGURIDAD;
		PdfPTable estructura = null;

		for(float tamanyo = TAMANYO_MAXIMO_ESTRUCTURA; estructura == null || (estructura.getTotalHeight() > disponible && tamanyo >= TAMANYO_MINIMO_ESTRUCTURA); tamanyo -= 0.25f) {

			estructura = this.tablaEstructura(tamanyo);
			estructura.setTotalWidth(ancho);
			estructura.setLockedWidth(true);
		}

		documento.add(estructura);
	}

	private PdfPTable tablaEstructura(float tamanyo) {

		PdfPTable tabla = new PdfPTable(new float[] {34f, 12f, 22f, 22f, 10f});
		tabla.setSpacingBefore(4);

		for(String cabecera : new String[] {"Mes", "Año VAU", "Desde", "Hasta", "Días"}) {
			PdfPCell celda = celdaEstructura(new Phrase(cabecera, fuente(tamanyo, Font.BOLD, TINTA_SUAVE)), tamanyo);
			celda.setBackgroundColor(FONDO_CABECERA);
			tabla.addCell(celda);
		}

		int estacionAnterior = -1;

		for(TramoDeEstacion tramo : this.tramos) {

			if(tramo.estacion != estacionAnterior) {

				PdfPCell banda = celdaEstructura(new Phrase(this.getNombreEstacion(tramo.estacion).toUpperCase(), fuente(tamanyo - 0.5f, Font.BOLD, Color.WHITE)), tamanyo);
				banda.setColspan(5);
				banda.setBackgroundColor(COLOR_ESTACION.getOrDefault(tramo.estacion, TINTA_SUAVE));
				banda.setBorderColor(Color.WHITE);
				tabla.addCell(banda);
				estacionAnterior = tramo.estacion;
			}

			Chunk enlace = new Chunk(this.getNombreDelTramo(tramo), fuente(tamanyo, Font.BOLD, TINTA));
			enlace.setLocalGoto(tramo.mes.destino());

			tabla.addCell(celdaEstructura(new Phrase(enlace), tamanyo));
			tabla.addCell(celdaEstructura(new Phrase(this.getAnyo(tramo.dias), fuente(tamanyo, Font.NORMAL, TINTA)), tamanyo));
			tabla.addCell(celdaEstructura(new Phrase(fechaMedia(this.getFecha(tramo.dias.get(0))), fuente(tamanyo, Font.NORMAL, TINTA)), tamanyo));
			tabla.addCell(celdaEstructura(new Phrase(fechaMedia(this.getFecha(tramo.dias.get(tramo.dias.size() - 1))), fuente(tamanyo, Font.NORMAL, TINTA)), tamanyo));
			tabla.addCell(celdaEstructura(new Phrase(String.valueOf(tramo.dias.size()), fuente(tamanyo, Font.NORMAL, TINTA)), tamanyo));
		}

		return tabla;
	}

	private static PdfPCell celdaEstructura(Phrase contenido, float tamanyo) {

		PdfPCell celda = new PdfPCell(contenido);
		celda.setBorderColor(LINEA);
		celda.setLeading(0, 1.15f);
		celda.setPaddingTop(tamanyo / 6);
		celda.setPaddingBottom(tamanyo / 3);
		celda.setPaddingLeft(5);
		return celda;
	}

	private void escribirLeyenda(Document documento) {

		Paragraph titulo = titular("Leyenda");
		titulo.setSpacingBefore(ESPACIO_ENTRE_SECCIONES);
		documento.add(titulo);

		PdfPTable leyenda = this.tablaLeyenda();
		leyenda.setWidthPercentage(100);
		leyenda.setSpacingBefore(10);
		documento.add(leyenda);
	}

	private PdfPTable tablaLeyenda() {

		PdfPTable tabla = new PdfPTable(new float[] {2.5f, 15f, 2.5f, 31f, 2.5f, 22f, 2.5f, 22f});

		this.filaLeyenda(tabla, FONDO_FESTIVO, "Festividad");
		this.filaLeyenda(tabla, FONDO_EVENTO, "Evento reseñable: métono, eclípeno o eclipse");
		this.filaLeyenda(tabla, FONDO_FESTIVO_Y_EVENTO, "Festividad y evento reseñable");
		this.filaLeyenda(tabla, FONDO_SEGUNDO_MES, "Segundo mes de la lunación");

		return tabla;
	}

	private void filaLeyenda(PdfPTable tabla, Color color, String texto) {

		PdfPCell muestra = new PdfPCell();
		muestra.setFixedHeight(12);
		muestra.setBackgroundColor(color);
		muestra.setBorderColor(LINEA);
		tabla.addCell(muestra);

		PdfPCell descripcion = celdaSinBorde();
		descripcion.setPhrase(new Phrase(texto, fuente(8, Font.NORMAL, TINTA)));
		descripcion.setPaddingLeft(6);
		descripcion.setVerticalAlignment(Element.ALIGN_MIDDLE);
		tabla.addCell(descripcion);
	}

	private void escribirFestividades(Document documento) {

		List<Integer> diasConFestividad = new ArrayList<>();
		for(int i = 0; i < this.dias.size(); i++) {
			if(this.getFestividad(this.dias.get(i)) != null) {
				diasConFestividad.add(i);
			}
		}

		this.escribirListaDeFechas(documento, "Festividades", DESTINO_FESTIVIDADES, 0,
				"Festividad", diasConFestividad, this::getFestividad,
				"No hay festividades en este calendario.");
	}

	private void escribirEventosResenyables(Document documento) {

		List<Integer> diasConEvento = new ArrayList<>();
		for(int i = 0; i < this.dias.size(); i++) {
			if(this.esEventoResenyable(this.getEvento(this.dias.get(i)))) {
				diasConEvento.add(i);
			}
		}

		this.escribirListaDeFechas(documento, "Eventos reseñables", DESTINO_EVENTOS, ESPACIO_ENTRE_SECCIONES,
				"Evento", diasConEvento, this::getEvento,
				"No hay métonos, eclípenos ni eclipses en este calendario.");
	}

	private void escribirListaDeFechas(Document documento, String titulo, String destino, float espacioAntes, String cabeceraTexto,
			List<Integer> indices, Function<DateDTO, String> texto, String sinResultados) {

		Chunk tituloConDestino = new Chunk(titulo, fuente(13, Font.BOLD, TINTA));
		tituloConDestino.setLocalDestination(destino);
		Paragraph titular = new Paragraph(tituloConDestino);
		titular.setSpacingBefore(espacioAntes);
		documento.add(titular);

		if(indices.isEmpty()) {
			documento.add(new Paragraph(sinResultados, fuente(10, Font.ITALIC, TINTA_SUAVE)));
			return;
		}

		PdfPTable tabla = new PdfPTable(new float[] {40f, 40f, 20f});
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(10);
		tabla.setHeaderRows(1);

		for(String cabecera : new String[] {cabeceraTexto, "Fecha VAU", "Fecha civil"}) {
			tabla.addCell(celdaCabecera(cabecera, Element.ALIGN_LEFT));
		}

		for(int indice : indices) {

			DateDTO dia = this.dias.get(indice);

			PdfPCell[] fila = {
					celdaTabla(new Phrase(texto.apply(dia), fuente(9, Font.BOLD, this.getColorTexto(dia, TINTA)))),
					celdaTabla(new Phrase(this.getFechaVAU(indice), fuente(9, Font.NORMAL, TINTA))),
					celdaTabla(new Phrase(fechaMedia(this.getFecha(indice)), fuente(9, Font.NORMAL, TINTA)))
			};

			for(PdfPCell celda : fila) {
				celda.setBackgroundColor(this.getColorFondo(dia));
				tabla.addCell(celda);
			}
		}

		documento.add(tabla);
	}

	// El evento del día ya viene priorizado por NotableEventService: eclípeno, métono, solsticio/equinoccio, eclipse...
	private boolean esEventoResenyable(String evento) {
		return evento != null && (evento.startsWith("Métono") || evento.startsWith("Eclípeno") || evento.startsWith("Eclipse"));
	}

	private boolean tieneEventoResenyable(DateDTO dia) {
		return this.esEventoResenyable(this.getEvento(dia));
	}

	private Color getColorFondo(DateDTO dia) {

		boolean festividad = this.getFestividad(dia) != null;
		boolean evento = this.tieneEventoResenyable(dia);

		if(festividad && evento) {
			return FONDO_FESTIVO_Y_EVENTO;
		}
		if(festividad) {
			return FONDO_FESTIVO;
		}
		return evento ? FONDO_EVENTO : null;
	}

	private Color getColorTexto(DateDTO dia, Color porDefecto) {

		boolean festividad = this.getFestividad(dia) != null;
		boolean evento = this.tieneEventoResenyable(dia);

		if(festividad && evento) {
			return TEXTO_FESTIVO_Y_EVENTO;
		}
		if(festividad) {
			return TEXTO_FESTIVO;
		}
		return evento ? TEXTO_EVENTO : porDefecto;
	}

	private String getFechaVAU(int indice) {

		DateDTO dia = this.dias.get(indice);

		if(!this.tieneDatos(dia)) {
			return SIN_DATOS;
		}

		if(this.esTerra(dia)) {
			return "Terra · luna nueva de " + this.mesDeCadaDia[indice];
		}

		return this.mesDeCadaDia[indice] + " · " + dia.getWeek() + " · " + dia.getDay();
	}

	private void escribirLunacion(Document documento, List<Integer> lunacion) {

		List<MesDelCalendario> mesesDeLaLunacion = new ArrayList<>();
		for(MesDelCalendario mes : this.meses) {
			if(lunacion.contains(mes.primerDia())) {
				mesesDeLaLunacion.add(mes);
			}
		}

		String mesPrincipal = mesesDeLaLunacion.isEmpty() ? SIN_DATOS : mesesDeLaLunacion.get(0).nombre;

		List<Integer> estacionesDeLaLunacion = new ArrayList<>();
		for(int indice : lunacion) {
			if(!estacionesDeLaLunacion.contains(this.estacionDeCadaDia[indice])) {
				estacionesDeLaLunacion.add(this.estacionDeCadaDia[indice]);
			}
		}

		Paragraph estaciones = new Paragraph();
		for(int i = 0; i < estacionesDeLaLunacion.size(); i++) {

			int estacion = estacionesDeLaLunacion.get(i);

			if(i > 0) {
				estaciones.add(new Chunk("  ·  ", fuente(9, Font.BOLD, LINEA)));
			}
			estaciones.add(this.espaciado(this.getNombreEstacion(estacion).toUpperCase(), 2f, fuente(9, Font.BOLD, COLOR_ESTACION.getOrDefault(estacion, TINTA_SUAVE))));
		}
		documento.add(estaciones);

		Paragraph titulo = new Paragraph();
		titulo.setLeading(28);
		for(int i = 0; i < mesesDeLaLunacion.size(); i++) {

			MesDelCalendario mes = mesesDeLaLunacion.get(i);
			Chunk nombre = new Chunk(mes.nombre.toUpperCase(), fuente(24, Font.BOLD, i == 0 ? TINTA : COLOR_ESTACION.getOrDefault(this.estacionDeCadaDia[mes.primerDia()], TINTA)));
			nombre.setLocalDestination(mes.destino());

			if(i > 0) {
				titulo.add(new Chunk("  ·  ", fuente(24, Font.NORMAL, LINEA)));
			}
			titulo.add(nombre);

			String apellido = this.getApellidoDelMes(mes);
			if(apellido != null) {
				titulo.add(new Chunk(" " + apellido, fuente(14, Font.ITALIC, TINTA_SUAVE)));
			}
		}
		documento.add(titulo);

		Paragraph subtitulo = new Paragraph(this.getSubtituloLunacion(lunacion), fuente(9.5f, Font.NORMAL, TINTA_SUAVE));
		subtitulo.setSpacingAfter(8);
		documento.add(subtitulo);

		documento.add(this.tablaLunacion(lunacion, mesPrincipal));
	}

	private String getApellidoDelMes(MesDelCalendario mes) {

		for(int indice : mes.dias) {

			DateDTO dia = this.dias.get(indice);

			if(this.tieneDatos(dia) && !this.esTerra(dia) && dia.getMonth().getSurname() != null && !dia.getMonth().getSurname().isBlank()) {
				return dia.getMonth().getSurname();
			}
		}

		return null;
	}

	private String getSubtituloLunacion(List<Integer> lunacion) {

		StringBuilder subtitulo = new StringBuilder();
		subtitulo.append("Del ").append(fechaLarga(this.getFecha(lunacion.get(0))))
				.append(" al ").append(fechaLarga(this.getFecha(lunacion.get(lunacion.size() - 1))))
				.append(" · ").append(lunacion.size()).append(" días");

		String estacionInicial = null;
		String estacionFinal = null;
		LocalDate cambioDeAnyo = null;

		for(int indice : lunacion) {

			DateDTO dia = this.dias.get(indice);

			if(this.tieneDatos(dia) && dia.getSeason() != null && dia.getSeason().getName() != null && !SIN_VALOR.equals(dia.getSeason().getName())) {

				if(estacionInicial == null) {
					estacionInicial = dia.getSeason().getName();
				}
				estacionFinal = dia.getSeason().getName();
			}

			if(this.tieneDatos(dia) && dia.getYear() != null && dia.getYear().isEsSolsticioDeInvierno()) {
				cambioDeAnyo = this.getFecha(indice);
			}
		}

		if(estacionInicial != null && !estacionInicial.equals(estacionFinal)) {
			subtitulo.append(" · Mes híbrido: de ").append(estacionInicial).append(" a ").append(estacionFinal);
		}

		if(cambioDeAnyo != null) {
			subtitulo.append(" · Cambio de año VAU en el solsticio de invierno: ").append(fechaMedia(cambioDeAnyo));
		}

		return subtitulo.toString();
	}

	private PdfPTable tablaLunacion(List<Integer> lunacion, String mesPrincipal) {

		Integer terra = null;
		Map<Integer, Integer> posiciones = new LinkedHashMap<>();
		List<Integer> sinPosicion = new ArrayList<>();
		int semanas = 4;

		for(int indice : lunacion) {

			DateDTO dia = this.dias.get(indice);
			Integer posicion = this.getPosicion(dia);

			if(this.esTerra(dia) && terra == null) {
				terra = indice;
			}
			else if(posicion != null) {
				posiciones.put(posicion, indice);
				semanas = Math.max(semanas, posicion / DIAS_POR_SEMANA + 1);
			}
			else {
				sinPosicion.add(indice);
			}
		}

		float altoFila = semanas > 4 ? 77f : 96f;
		float[] anchos = new float[DIAS_POR_SEMANA + 1];
		anchos[0] = ANCHO_ETIQUETA_SEMANA;
		for(int i = 1; i < anchos.length; i++) {
			anchos[i] = (A4_HORIZONTAL.getWidth() - 56f - ANCHO_ETIQUETA_SEMANA) / DIAS_POR_SEMANA;
		}

		PdfPTable tabla = new PdfPTable(anchos.length);
		tabla.setTotalWidth(anchos);
		tabla.setLockedWidth(true);
		tabla.setSplitLate(false);

		tabla.addCell(this.celdaEtiquetaSemana(this.getNombreSemana(0), 26f));
		tabla.addCell(this.celdaTerra(terra));

		tabla.addCell(celdaCabecera("", Element.ALIGN_CENTER));
		for(int columna = 1; columna <= DIAS_POR_SEMANA; columna++) {
			tabla.addCell(celdaCabecera(this.getNombreDia(columna), Element.ALIGN_CENTER));
		}

		for(int semana = 0; semana < semanas; semana++) {

			tabla.addCell(this.celdaEtiquetaSemana(this.getNombreSemana(semana + 1), altoFila));

			int huecos = 0;

			for(int columna = 0; columna < DIAS_POR_SEMANA; columna++) {

				Integer indice = posiciones.get(semana * DIAS_POR_SEMANA + columna);

				if(indice != null) {
					this.anyadirHueco(tabla, huecos, altoFila);
					huecos = 0;
					tabla.addCell(this.celdaDia(indice, mesPrincipal, altoFila));
				}
				else {
					huecos++;
				}
			}

			this.anyadirHueco(tabla, huecos, altoFila);
		}

		for(int i = 0; i < sinPosicion.size(); i += DIAS_POR_SEMANA) {

			tabla.addCell(this.celdaEtiquetaSemana(SIN_DATOS, 46f));

			int diasEnLaFila = Math.min(DIAS_POR_SEMANA, sinPosicion.size() - i);
			for(int columna = 0; columna < diasEnLaFila; columna++) {
				tabla.addCell(this.celdaDiaSinDatos(sinPosicion.get(i + columna)));
			}

			this.anyadirHueco(tabla, DIAS_POR_SEMANA - diasEnLaFila, 46f);
		}

		return tabla;
	}

	// Posición dentro de la rejilla: semana (Primana = 0) * 7 + columna. Limana reutiliza las primeras columnas
	private Integer getPosicion(DateDTO dia) {

		if(!this.tieneDatos(dia) || this.esTerra(dia)) {
			return null;
		}

		Integer semana = this.semanasPorNombre.get(dia.getWeek());
		Integer diaDeLaSemana = this.diasPorNombre.get(dia.getDay().split(" ")[0]);

		if(semana == null || diaDeLaSemana == null || semana < 1 || diaDeLaSemana < 1) {
			return null;
		}

		int columna = diaDeLaSemana < PRIMER_DIA_LIMANA ? diaDeLaSemana - 1 : diaDeLaSemana - PRIMER_DIA_LIMANA;

		return (semana - 1) * DIAS_POR_SEMANA + columna;
	}

	private String getNombreSemana(int semanaDelMes) {

		for(Map.Entry<String, Integer> semana : this.semanasPorNombre.entrySet()) {
			if(semana.getValue() == semanaDelMes) {
				return SIN_VALOR.equals(semana.getKey()) ? "Terra" : semana.getKey();
			}
		}

		return "";
	}

	private String getNombreDia(int diaDeLaSemana) {

		for(Map.Entry<String, Integer> dia : this.diasPorNombre.entrySet()) {
			if(dia.getValue() == diaDeLaSemana) {
				return dia.getKey();
			}
		}

		return "";
	}

	private PdfPCell celdaEtiquetaSemana(String nombre, float alto) {

		PdfPCell celda = new PdfPCell(new Phrase(nombre, fuente(9, Font.BOLD, TINTA_SUAVE)));
		celda.setFixedHeight(alto);
		celda.setBackgroundColor(FONDO_CABECERA);
		celda.setBorderColor(LINEA);
		celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
		celda.setHorizontalAlignment(Element.ALIGN_CENTER);
		return celda;
	}

	private PdfPCell celdaTerra(Integer indice) {

		PdfPCell celda = new PdfPCell();
		celda.setColspan(DIAS_POR_SEMANA);
		celda.setFixedHeight(26f);
		celda.setBorderColor(LINEA);
		celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
		celda.setPaddingLeft(8);

		if(indice == null) {
			celda.setBackgroundColor(FONDO_CABECERA);
			celda.setPhrase(new Phrase("La luna nueva de esta lunación cae antes del inicio del calendario", fuente(8, Font.ITALIC, TINTA_SUAVE)));
			return celda;
		}

		DateDTO dia = this.dias.get(indice);
		String festividad = this.getFestividad(dia);
		String evento = this.getEvento(dia);
		boolean eventoResenyable = this.esEventoResenyable(evento);

		Phrase texto = new Phrase();
		texto.add(new Chunk("Terra", fuente(11, Font.BOLD, TINTA)));
		texto.add(new Chunk("   " + fechaLarga(this.getFecha(indice)), fuente(9, Font.NORMAL, TINTA_SUAVE)));
		texto.add(new Chunk("   ·   " + (evento != null ? evento : "Luna nueva"), fuente(9, eventoResenyable ? Font.BOLD : Font.NORMAL, this.getColorTexto(dia, TINTA))));
		if(festividad != null) {
			texto.add(new Chunk("   ·   " + festividad, fuente(9, Font.BOLD, this.getColorTexto(dia, TEXTO_FESTIVO))));
		}
		if(this.empiezaEstacion(indice)) {
			texto.add(new Chunk("   ·   Empieza " + this.getNombreConArticulo(this.estacionDeCadaDia[indice]), fuente(9, Font.BOLD, COLOR_ESTACION.getOrDefault(this.estacionDeCadaDia[indice], TINTA))));
		}
		celda.setBackgroundColor(this.getColorFondo(dia));
		celda.setPhrase(texto);
		celda.setCellEvent(new EsquinaDelDia(null, true));

		return celda;
	}

	private PdfPCell celdaDia(int indice, String mesPrincipal, float alto) {

		DateDTO dia = this.dias.get(indice);
		LocalDate fecha = this.getFecha(indice);
		String festividad = this.getFestividad(dia);
		String evento = this.getEvento(dia);
		boolean segundoMes = !mesPrincipal.equals(this.mesDeCadaDia[indice]);
		boolean empiezaMes = segundoMes && (indice == 0 || !this.mesDeCadaDia[indice].equals(this.mesDeCadaDia[indice - 1]));

		PdfPCell celda = new PdfPCell();
		celda.setFixedHeight(alto);
		celda.setBorderColor(LINEA);
		celda.setPadding(4);
		celda.setPaddingLeft(5);
		celda.setUseAscender(true);

		Color fondo = this.getColorFondo(dia);
		celda.setBackgroundColor(fondo == null && segundoMes ? FONDO_SEGUNDO_MES : fondo);

		String[] partesDelDia = dia.getDay().split(" ", 2);
		Paragraph nombre = new Paragraph(partesDelDia[0], fuente(11, Font.BOLD, TINTA));
		nombre.setLeading(13);
		celda.addElement(nombre);

		if(partesDelDia.length > 1) {
			celda.addElement(lineaPequenya(partesDelDia[1], Font.ITALIC, TINTA_SUAVE));
		}

		if(this.empiezaEstacion(indice)) {
			celda.addElement(lineaPequenya("Empieza " + this.getNombreConArticulo(this.estacionDeCadaDia[indice]), Font.BOLD, COLOR_ESTACION.getOrDefault(this.estacionDeCadaDia[indice], TINTA)));
		}

		if(empiezaMes) {
			celda.addElement(lineaPequenya("Empieza " + this.mesDeCadaDia[indice], Font.BOLD, COLOR_ESTACION.getOrDefault(this.estacionDeCadaDia[indice], TINTA)));
		}

		if(festividad != null) {
			celda.addElement(lineaPequenya(festividad, Font.BOLD, this.getColorTexto(dia, TEXTO_FESTIVO)));
		}

		if(evento != null && this.esEventoResenyable(evento)) {
			celda.addElement(lineaPequenya(evento, Font.BOLD, this.getColorTexto(dia, TEXTO_EVENTO)));
		}
		else if(evento != null) {
			celda.addElement(lineaPequenya(evento, Font.NORMAL, TINTA_SUAVE));
		}

		celda.setCellEvent(new EsquinaDelDia(fechaCorta(fecha), true));

		return celda;
	}

	private PdfPCell celdaDiaSinDatos(int indice) {

		PdfPCell celda = new PdfPCell(new Phrase(SIN_DATOS, fuente(8, Font.ITALIC, TINTA_SUAVE)));
		celda.setFixedHeight(46f);
		celda.setBorderColor(LINEA);
		celda.setVerticalAlignment(Element.ALIGN_BOTTOM);
		celda.setPaddingBottom(6);
		celda.setPaddingLeft(5);
		celda.setCellEvent(new EsquinaDelDia(fechaCorta(this.getFecha(indice)), true));
		return celda;
	}

	// Los días que no existen en la lunación no se dibujan: la fila se completa con un espacio en blanco sin bordes
	private void anyadirHueco(PdfPTable tabla, int columnas, float alto) {

		if(columnas > 0) {

			PdfPCell hueco = new PdfPCell();
			hueco.setColspan(columnas);
			hueco.setFixedHeight(alto);
			hueco.setBorder(Rectangle.NO_BORDER);
			tabla.addCell(hueco);
		}
	}

	private void escribirMarcadores(PdfWriter writer) {

		PdfOutline raiz = writer.getRootOutline();
		new PdfOutline(raiz, PdfAction.gotoLocalPage("portada", false), "Año " + this.anyoVAU + " · Métono invernal nuevo " + this.metonoInvernalNuevo);
		new PdfOutline(raiz, PdfAction.gotoLocalPage(DESTINO_FESTIVIDADES, false), "Festividades");
		new PdfOutline(raiz, PdfAction.gotoLocalPage(DESTINO_EVENTOS, false), "Eventos reseñables");

		PdfOutline estacionActual = null;
		int numeroEstacionActual = -1;

		for(TramoDeEstacion tramo : this.tramos) {

			if(estacionActual == null || tramo.estacion != numeroEstacionActual) {
				estacionActual = new PdfOutline(raiz, PdfAction.gotoLocalPage(tramo.mes.destino(), false), this.getNombreEstacion(tramo.estacion), false);
				numeroEstacionActual = tramo.estacion;
			}

			new PdfOutline(estacionActual, PdfAction.gotoLocalPage(tramo.mes.destino(), false), this.getNombreDelTramo(tramo) + " (" + fechaMedia(this.getFecha(tramo.dias.get(0))) + ")");
		}
	}

	private LocalDate getFecha(int indice) {
		return LocalDate.parse(this.dias.get(indice).getFechaO());
	}

	private String getFestividad(DateDTO dia) {

		String festividad = this.tieneDatos(dia) && dia.getFestividades() != null ? dia.getFestividades().getFestividadActual() : null;

		return festividad == null || festividad.isBlank() ? null : festividad;
	}

	private String getEvento(DateDTO dia) {

		String evento = this.tieneDatos(dia) && dia.getNotableEvent() != null ? dia.getNotableEvent().getToday() : null;

		return evento == null || evento.isBlank() ? null : evento;
	}

	private Chunk espaciado(String texto, float espacio, Font fuente) {

		Chunk chunk = new Chunk(texto, fuente);
		chunk.setCharacterSpacing(espacio);
		return chunk;
	}

	private static Paragraph titular(String texto) {

		Paragraph titular = new Paragraph(texto, fuente(13, Font.BOLD, TINTA));
		titular.setSpacingAfter(4);
		return titular;
	}

	private static Paragraph lineaPequenya(String texto, int estilo, Color color) {

		Paragraph linea = new Paragraph(texto, fuente(7, estilo, color));
		linea.setLeading(8.5f);
		return linea;
	}

	private static PdfPCell celdaCabecera(String texto, int alineacion) {

		PdfPCell celda = new PdfPCell(new Phrase(texto, fuente(8.5f, Font.BOLD, TINTA_SUAVE)));
		celda.setBackgroundColor(FONDO_CABECERA);
		celda.setBorderColor(LINEA);
		celda.setHorizontalAlignment(alineacion);
		celda.setPadding(4);
		return celda;
	}

	private static PdfPCell celdaTabla(Phrase contenido) {

		PdfPCell celda = new PdfPCell(contenido);
		celda.setBorderColor(LINEA);
		celda.setPadding(3.5f);
		celda.setPaddingLeft(5);
		return celda;
	}

	private static PdfPCell celdaSinBorde() {

		PdfPCell celda = new PdfPCell();
		celda.setBorder(Rectangle.NO_BORDER);
		celda.setPadding(0);
		return celda;
	}

	private static Font fuente(float tamanyo, int estilo, Color color) {
		return new Font(Font.HELVETICA, tamanyo, estilo, color);
	}

	private static String fechaLarga(LocalDate fecha) {
		return DIAS_SEMANA[fecha.getDayOfWeek().getValue() - 1] + " " + fecha.getDayOfMonth() + " de " + MESES[fecha.getMonthValue() - 1] + " de " + fecha.getYear();
	}

	private static String fechaMedia(LocalDate fecha) {
		return DIAS_SEMANA_CORTOS[fecha.getDayOfWeek().getValue() - 1] + " " + fecha.getDayOfMonth() + " " + MESES_CORTOS[fecha.getMonthValue() - 1] + " " + fecha.getYear();
	}

	private static String fechaCorta(LocalDate fecha) {

		String corta = DIAS_SEMANA_CORTOS[fecha.getDayOfWeek().getValue() - 1] + " " + fecha.getDayOfMonth() + " " + MESES_CORTOS[fecha.getMonthValue() - 1];

		return fecha.getDayOfYear() == 1 ? corta + " " + fecha.getYear() : corta;
	}

	private static class MesDelCalendario {

		private final int numero;
		private final String nombre;
		private final List<Integer> dias = new ArrayList<>();
		private int tramos;

		private MesDelCalendario(int numero, String nombre) {
			this.numero = numero;
			this.nombre = nombre;
		}

		private String destino() {
			return "mes-" + this.numero;
		}

		private int primerDia() {
			return this.dias.get(0);
		}
	}

	// Parte de un mes que cae dentro de una sola estación
	private static class TramoDeEstacion {

		private final MesDelCalendario mes;
		private final int estacion;
		private final List<Integer> dias = new ArrayList<>();

		private TramoDeEstacion(MesDelCalendario mes, int estacion) {
			this.mes = mes;
			this.estacion = estacion;
		}
	}

	// Fecha civil en la esquina superior derecha y casilla para marcar el día en la inferior
	private static class EsquinaDelDia implements PdfPCellEvent {

		private final String fechaCivil;
		private final boolean casilla;

		private EsquinaDelDia(String fechaCivil, boolean casilla) {
			this.fechaCivil = fechaCivil;
			this.casilla = casilla;
		}

		@Override
		public void cellLayout(PdfPCell cell, Rectangle posicion, PdfContentByte[] lienzos) {

			PdfContentByte lienzo = lienzos[PdfPTable.TEXTCANVAS];

			if(this.fechaCivil != null) {
				ColumnText.showTextAligned(lienzo, Element.ALIGN_RIGHT, new Phrase(this.fechaCivil, fuente(7.5f, Font.NORMAL, TINTA_SUAVE)),
						posicion.getRight() - 4, posicion.getTop() - 10, 0);
			}

			if(this.casilla) {
				float lado = 7f;
				lienzo.saveState();
				lienzo.setColorStroke(TINTA_SUAVE);
				lienzo.setLineWidth(0.6f);
				lienzo.rectangle(posicion.getRight() - lado - 4, posicion.getBottom() + 4, lado, lado);
				lienzo.stroke();
				lienzo.restoreState();
			}
		}
	}

	private class CabeceraYPie extends PdfPageEventHelper {

		private final BaseFont normal;
		private final BaseFont negrita;

		private CabeceraYPie() {

			try {
				this.normal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				this.negrita = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			}
			catch(Exception e) {
				throw new IllegalStateException("No se han podido cargar las fuentes del calendario", e);
			}
		}

		@Override
		public void onStartPage(PdfWriter writer, Document document) {

			if(writer.getPageNumber() == 1) {
				writer.getDirectContent().localDestination("portada", new PdfDestination(PdfDestination.FIT));
			}
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {

			PdfContentByte lienzo = writer.getDirectContent();
			Rectangle pagina = document.getPageSize();
			float izquierda = document.left();
			float derecha = document.right();

			if(writer.getPageNumber() > 1) {

				float alturaCabecera = pagina.getTop() - 24;
				this.texto(lienzo, this.negrita, 8, COLOR_ESTACION.get(1), "CALENDARIO VAU", izquierda, alturaCabecera, Element.ALIGN_LEFT);
				this.texto(lienzo, this.negrita, 8, TINTA, "AÑO " + anyoVAU + "  ·  MÉTONO INVERNAL NUEVO " + metonoInvernalNuevo, (izquierda + derecha) / 2, alturaCabecera, Element.ALIGN_CENTER);
				this.texto(lienzo, this.normal, 8, TINTA_SUAVE, "Año civil de referencia " + anyoCivil, derecha, alturaCabecera, Element.ALIGN_RIGHT);

				lienzo.saveState();
				lienzo.setColorStroke(LINEA);
				lienzo.setLineWidth(0.5f);
				lienzo.moveTo(izquierda, alturaCabecera - 5);
				lienzo.lineTo(derecha, alturaCabecera - 5);
				lienzo.stroke();
				lienzo.restoreState();
			}

			float alturaPie = pagina.getBottom() + 16;
			this.texto(lienzo, this.normal, 7.5f, TINTA_SUAVE, "Generado por CalendarioVAU", izquierda, alturaPie, Element.ALIGN_LEFT);
			this.texto(lienzo, this.normal, 7.5f, TINTA_SUAVE, "Página " + writer.getPageNumber(), derecha, alturaPie, Element.ALIGN_RIGHT);
		}

		private void texto(PdfContentByte lienzo, BaseFont fuente, float tamanyo, Color color, String texto, float x, float y, int alineacion) {

			lienzo.saveState();
			lienzo.beginText();
			lienzo.setFontAndSize(fuente, tamanyo);
			lienzo.setColorFill(color);
			lienzo.showTextAligned(alineacion, texto, x, y, 0);
			lienzo.endText();
			lienzo.restoreState();
		}
	}
}
