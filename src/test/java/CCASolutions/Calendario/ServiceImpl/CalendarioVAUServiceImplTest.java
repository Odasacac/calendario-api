package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import CCASolutions.Calendario.DTOs.AnyoIndiceDTO;
import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.LunacionDTO;
import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MetonoIndiceDTO;
import CCASolutions.Calendario.DTOs.MonthDTO;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.FasesLunaresRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DatesService;

@ExtendWith(MockitoExtension.class)
class CalendarioVAUServiceImplTest {

	@Mock private DatesService datesService;
	@Mock private FasesLunaresRepository fasesLunaresRepository;
	@Mock private MetonsRepository metonsRepository;
	@Mock private SolsticiosYEquinocciosRepository solsticiosRepository;
	@Mock private SeasonsRepository seasonsRepository;
	@Mock private WeeksRepository weeksRepository;
	@Mock private DaysRepository daysRepository;

	@InjectMocks
	private CalendarioVAUServiceImpl servicio;

	@BeforeEach
	void preparar() {
		lenient().when(this.seasonsRepository.findAll()).thenReturn(List.of());
		lenient().when(this.weeksRepository.findAll()).thenReturn(List.of());
		lenient().when(this.daysRepository.findAll()).thenReturn(List.of());
	}

	private static FasesLunaresEntity lunaNueva(LocalDateTime fecha) {
		FasesLunaresEntity luna = new FasesLunaresEntity();
		luna.setDate(fecha);
		luna.setNueva(true);
		return luna;
	}

	private static List<DateDTO> diasVAU(LocalDate desde, LocalDate hasta) {
		List<DateDTO> dias = new ArrayList<>();
		for(LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
			DateDTO dia = new DateDTO();
			dia.setFechaO(d.toString());
			dia.setFechaEncontrada(true);
			MonthDTO mes = new MonthDTO();
			mes.setName(d.equals(desde) ? "-" : "Pridor");
			mes.setNewMoon(d.equals(desde));
			dia.setMonth(mes);
			dia.setWeek(d.equals(desde) ? "-" : "Primana");
			dia.setDay(d.equals(desde) ? "Terra" : "Luno");
			dias.add(dia);
		}
		return dias;
	}

	@Test
	void laLunacionVaDeTerraALaVisperaDeLaSiguienteYSeGuardaEnCache() {

		LocalDate terra = LocalDate.of(2026, 10, 10);
		when(this.fasesLunaresRepository.findFirstByDateBeforeAndNuevaIsTrueOrderByDateDesc(any()))
				.thenReturn(lunaNueva(terra.atTime(4, 49)));
		when(this.fasesLunaresRepository.findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(LocalDate.of(2026, 10, 11).atStartOfDay()))
				.thenReturn(lunaNueva(LocalDateTime.of(2026, 11, 9, 7, 2)));
		when(this.datesService.getDatesVAUFromRange(terra, LocalDate.of(2026, 11, 8)))
				.thenReturn(diasVAU(terra, LocalDate.of(2026, 11, 8)));

		LunacionDTO lunacion = this.servicio.getLunacion(LocalDate.of(2026, 10, 23));

		assertEquals(terra, lunacion.getInicio());
		assertEquals(LocalDate.of(2026, 11, 8), lunacion.getFin());
		assertEquals(LocalDate.of(2026, 10, 9), lunacion.getAnterior());
		assertEquals(LocalDate.of(2026, 11, 9), lunacion.getSiguiente());

		// Otra fecha de la misma lunación no vuelve a calcular.
		this.servicio.getLunacion(LocalDate.of(2026, 11, 1));
		verify(this.datesService, times(1)).getDatesVAUFromRange(terra, LocalDate.of(2026, 11, 8));
	}

	@Test
	void fueraDelRangoNavegableDaError() {

		assertThrows(ResponseStatusException.class, () -> this.servicio.getLunacion(LocalDate.of(2100, 1, 1)));
		assertThrows(ResponseStatusException.class, () -> this.servicio.getLunacion(LocalDate.of(1433, 12, 31)));
	}

	@Test
	void losAnyosVanDelDiaSiguienteAUnSolsticioAlSolsticioSiguiente() {

		MetonsEntity metono = new MetonsEntity();
		metono.setId(1671L);
		metono.setDate(LocalDateTime.of(2014, 12, 21, 22, 3));
		MetonsEntity siguiente = new MetonsEntity();
		siguiente.setId(1687L);
		siguiente.setDate(LocalDateTime.of(2033, 12, 21, 12, 46));
		when(this.metonsRepository.findAllByInvernalIsTrueAndNuevoIsTrue()).thenReturn(List.of(siguiente, metono));
		when(this.datesService.getDateVAUFromDateO(any())).thenAnswer(i -> {
			LocalDate fecha = i.getArgument(0);
			MetonINDTO in = new MetonINDTO();
			in.setNumberOfMetonIN(fecha.getYear() == 2014 ? 43 : 44);
			MetonDTO m = new MetonDTO();
			m.setMetonsIN(in);
			DateDTO vau = new DateDTO();
			vau.setMetonoVAU(m);
			return vau;
		});

		List<SolsticiosYEquinocciosEntity> solsticios = new ArrayList<>();
		for(int anyo = 2015; anyo <= 2033; anyo++) {
			SolsticiosYEquinocciosEntity s = new SolsticiosYEquinocciosEntity();
			s.setDate(LocalDateTime.of(anyo, 12, anyo == 2015 ? 22 : 21, 12, 0));
			s.setSolsticioInvierno(true);
			solsticios.add(s);
		}
		when(this.solsticiosRepository.findByDateAfterAndDateLessThanEqual(any(), any())).thenReturn(solsticios);

		List<MetonoIndiceDTO> metonos = this.servicio.getMetonos();
		assertEquals(43, metonos.get(0).getNumero());
		assertEquals(LocalDate.of(2014, 12, 22), metonos.get(0).getInicio());
		assertEquals(LocalDate.of(2033, 12, 21), metonos.get(0).getFin());

		List<AnyoIndiceDTO> anyos = this.servicio.getAnyos(1671L);
		assertEquals(19, anyos.size());
		assertEquals(0, anyos.get(0).getNumero());
		assertEquals(LocalDate.of(2014, 12, 22), anyos.get(0).getInicio());
		assertEquals(LocalDate.of(2015, 12, 22), anyos.get(0).getFin());
		assertEquals(LocalDate.of(2015, 12, 23), anyos.get(1).getInicio());
		assertEquals(18, anyos.get(18).getNumero());
		assertEquals(LocalDate.of(2033, 12, 21), anyos.get(18).getFin());
	}

	@Test
	void unMetonoSinDatosVAUTomaElNumeroDeSusVecinos() {

		Integer[] numeros = { null, null, 3, null, 5, null };
		CalendarioVAUServiceImpl.completarNumeros(numeros);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5, 6 }, numeros);
	}

	@Test
	void losMesesDeUnAnyoNoPuedenPedirIntervalosEnormes() {

		assertThrows(ResponseStatusException.class,
				() -> this.servicio.getMeses(LocalDate.of(2000, 1, 1), LocalDate.of(2003, 1, 1)));
	}
}
