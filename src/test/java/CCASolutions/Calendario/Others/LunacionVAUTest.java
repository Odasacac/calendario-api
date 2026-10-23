package CCASolutions.Calendario.Others;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.DiaCalendarioDTO;
import CCASolutions.Calendario.DTOs.FestividadesDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.DTOs.NotableEventDTO;
import CCASolutions.Calendario.DTOs.SeasonDTO;
import CCASolutions.Calendario.DTOs.YearDTO;

class LunacionVAUTest {

	private static final List<String> SEMANAS = List.of("Terra", "Primana", "Segana", "Terana", "Curana", "Limana");
	private static final List<String> DIAS = List.of("Luno", "Sole", "Merco", "Venuro", "Marto", "Júpeno", "Saturino");

	private final LunacionVAU lunacionVAU = new LunacionVAU(
			Map.of("-", 0, "Invierno", 1, "Primavera", 2, "Verano", 3, "Otoño", 4),
			Map.of("-", 0, "Primana", 1, "Segana", 2, "Terana", 3, "Curana", 4, "Limana", 5),
			Map.of("Terra", 0, "Luno", 1, "Sole", 2, "Merco", 3, "Venuro", 4, "Marto", 5, "Júpeno", 6, "Saturino", 7, "Liminol", 8, "Caelumbra", 9),
			SEMANAS, DIAS);

	private static DateDTO dia(LocalDate fecha, String mes, String semana, String nombreDia, String estacion, String anyo) {

		DateDTO dia = new DateDTO();
		dia.setFechaEncontrada(true);
		dia.setFechaO(fecha.toString());

		MonthDTO month = new MonthDTO();
		month.setName(mes);
		month.setNewMoon("Terra".equals(nombreDia));
		dia.setMonth(month);
		dia.setWeek(semana);
		dia.setDay(nombreDia);

		SeasonDTO season = new SeasonDTO();
		season.setName(estacion);
		dia.setSeason(season);

		YearDTO year = new YearDTO();
		year.setSolsticiosDeInviernoSinceLastMetonIN(anyo);
		year.setEsSolsticioDeInvierno("-".equals(anyo));
		dia.setYear(year);

		MetonINDTO metonIN = new MetonINDTO();
		metonIN.setNumberOfMetonIN(43);
		MetonDTO meton = new MetonDTO();
		meton.setMetonsIN(metonIN);
		dia.setMetonoVAU(meton);
		return dia;
	}

	/** Terra + 4 semanas + 2 días de Limana (31 días). */
	private static List<DateDTO> lunacionCompleta(LocalDate terra) {

		List<DateDTO> dias = new ArrayList<>();
		dias.add(dia(terra, "-", "-", "Terra", "Otoño", "11"));
		LocalDate fecha = terra.plusDays(1);
		for(int semana = 1; semana <= 4; semana++) {
			for(String nombre : DIAS) {
				dias.add(dia(fecha, "Pridor", SEMANAS.get(semana), nombre, "Otoño", "11"));
				fecha = fecha.plusDays(1);
			}
		}
		dias.add(dia(fecha, "Pridor", "Limana", "Liminol", "Otoño", "11"));
		dias.add(dia(fecha.plusDays(1), "Pridor", "Limana", "Caelumbra", "Otoño", "11"));
		return dias;
	}

	@Test
	void colocaCadaDiaEnSuSemanaYColumna() {

		LunacionDTO lunacion = this.lunacionVAU.construir(lunacionCompleta(LocalDate.of(2026, 10, 10)));
		List<DiaCalendarioDTO> dias = lunacion.getDias();

		assertEquals(LocalDate.of(2026, 10, 10), lunacion.getInicio());
		assertEquals(LocalDate.of(2026, 11, 9), lunacion.getFin());

		assertTrue(dias.get(0).isTerra());
		assertNull(dias.get(0).getSemana());

		// Segana · Júpeno
		DiaCalendarioDTO jupeno = dias.get(1 + 7 + 5);
		assertEquals(2, jupeno.getSemana());
		assertEquals(5, jupeno.getColumna());
		assertEquals("Júpeno", jupeno.getDia());

		// Limana reutiliza las primeras columnas
		DiaCalendarioDTO liminol = dias.get(29);
		DiaCalendarioDTO caelumbra = dias.get(30);
		assertEquals(5, liminol.getSemana());
		assertEquals(0, liminol.getColumna());
		assertEquals(1, caelumbra.getColumna());
	}

	@Test
	void terraTomaElMesDelDiaSiguienteYNoRepiteElMes() {

		LunacionDTO lunacion = this.lunacionVAU.construir(lunacionCompleta(LocalDate.of(2026, 10, 10)));

		assertEquals("Pridor", lunacion.getDias().get(0).getMes());
		assertEquals(1, lunacion.getMeses().size());
		assertEquals("Pridor", lunacion.getMeses().get(0).getNombre());
		assertEquals(List.of(4), lunacion.getEstaciones());
		assertEquals("11", lunacion.getAnyo());
		assertEquals(43, lunacion.getMetono());
		assertNull(lunacion.getCambioDeAnyo());
	}

	@Test
	void elSolsticioNoTieneEstacionYMarcaElCambioDeAnyo() {

		List<DateDTO> dias = lunacionCompleta(LocalDate.of(2026, 12, 9));
		// 12 días después de Terra: solsticio (sin estación ni año); después, invierno del año 12 y otro mes
		for(int i = 12; i < dias.size(); i++) {
			DateDTO dia = dias.get(i);
			boolean solsticio = i == 12;
			dia.getSeason().setName(solsticio ? "-" : "Invierno");
			dia.getYear().setSolsticiosDeInviernoSinceLastMetonIN(solsticio ? "-" : "12");
			dia.getYear().setEsSolsticioDeInvierno(solsticio);
			if(!solsticio) {
				dia.getMonth().setName("Oterno liminal");
			}
		}

		LunacionDTO lunacion = this.lunacionVAU.construir(dias);

		assertEquals(0, lunacion.getDias().get(12).getEstacion());
		assertEquals(LocalDate.of(2026, 12, 21), lunacion.getCambioDeAnyo());
		assertEquals(List.of(4, 1), lunacion.getEstaciones());
		assertEquals("11", lunacion.getAnyo());
		assertEquals("12", lunacion.getAnyoFinal());
		assertEquals(2, lunacion.getMeses().size());
		assertEquals("Oterno liminal", lunacion.getMeses().get(1).getNombre());
	}

	@Test
	void marcaFestividadesYEventosComoElPdf() {

		List<DateDTO> dias = lunacionCompleta(LocalDate.of(2026, 10, 10));

		FestividadesDTO festividades = new FestividadesDTO();
		festividades.setFestividadActual("Midsison otoñal");
		dias.get(5).setFestividades(festividades);

		NotableEventDTO eclipse = new NotableEventDTO();
		eclipse.setToday("Eclipse de sol anular");
		dias.get(6).setNotableEvent(eclipse);

		NotableEventDTO luna = new NotableEventDTO();
		luna.setToday("Luna llena");
		dias.get(7).setNotableEvent(luna);

		List<DiaCalendarioDTO> celdas = this.lunacionVAU.construir(dias).getDias();

		assertEquals("Midsison otoñal", celdas.get(5).getFestividad());
		assertTrue(celdas.get(6).isEventoResenyable());
		assertTrue(celdas.get(6).isEclipse());
		assertEquals("Luna llena", celdas.get(7).getEvento());
		assertFalse(celdas.get(7).isEventoResenyable());
	}

	@Test
	void unDiaSinDatosNoRompeLaLunacion() {

		List<DateDTO> dias = lunacionCompleta(LocalDate.of(1434, 1, 10));
		DateDTO roto = new DateDTO();
		roto.setFechaO(dias.get(3).getFechaO());
		dias.set(3, roto);

		DiaCalendarioDTO celda = this.lunacionVAU.construir(dias).getDias().get(3);
		assertTrue(celda.isSinDatos());
		assertNull(celda.getSemana());
	}
}
