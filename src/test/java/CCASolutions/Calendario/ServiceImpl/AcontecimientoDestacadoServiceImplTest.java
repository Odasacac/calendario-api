package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import CCASolutions.Calendario.DTOs.AcontecimientoDestacadoDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EventoDelDiaDTO;
import CCASolutions.Calendario.DTOs.FormularioAcontecimientoDTO;
import CCASolutions.Calendario.Entities.AcontecimientoDestacadoEntity;
import CCASolutions.Calendario.Entities.CasalerosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.MidsisonEntity;
import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Repositories.AcontecimientoDestacadoRepository;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Services.DatosCosmicosService;
import CCASolutions.Calendario.Services.NotableEventService;
import CCASolutions.Calendario.Services.UsuarioService;

@ExtendWith(MockitoExtension.class)
class AcontecimientoDestacadoServiceImplTest {

	private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 23, 12, 0);
	private static final LocalDate DIA = LocalDate.of(2026, 10, 23);

	@Mock private AcontecimientoDestacadoRepository repositorio;
	@Mock private UsuarioService usuarioService;
	@Mock private DatosCosmicosService datosCosmicosService;
	@Mock private NotableEventService notableEventService;
	@Mock private MidsisonRepository midsisonRepository;
	@Mock private CasalerosRepository casalerosRepository;

	@InjectMocks
	private AcontecimientoDestacadoServiceImpl servicio;

	private UsuarioEntity usuario;

	@BeforeEach
	void preparar() {

		ReflectionTestUtils.setField(this.servicio, "reloj", Clock.fixed(AHORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
		this.usuario = new UsuarioEntity();
		this.usuario.setId(1L);
		lenient().when(this.usuarioService.getUsuarioConSesion(1L)).thenReturn(this.usuario);
		lenient().when(this.repositorio.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
		// Por defecto los datos cósmicos no cubren la fecha: sin evento reseñable.
		lenient().when(this.datosCosmicosService.getDatosCosmicos(any())).thenReturn(new DatosCosmicosParaVAUDTO());
	}

	private DatosCosmicosParaVAUDTO datosValidos() {
		DatosCosmicosParaVAUDTO datos = new DatosCosmicosParaVAUDTO();
		datos.setValido(true);
		when(this.datosCosmicosService.getDatosCosmicos(DIA)).thenReturn(datos);
		return datos;
	}

	private static FormularioAcontecimientoDTO formulario(LocalDate fecha, String descripcion) {
		FormularioAcontecimientoDTO f = new FormularioAcontecimientoDTO();
		f.setFecha(fecha);
		f.setDescripcion(descripcion);
		return f;
	}

	private AcontecimientoDestacadoEntity acontecimiento(Long id, LocalDate fecha) {
		AcontecimientoDestacadoEntity s = new AcontecimientoDestacadoEntity();
		s.setId(id);
		s.setUsuario(this.usuario);
		s.setFecha(fecha);
		s.setDescripcion("Algo");
		s.setFechaCreacion(AHORA.minusDays(5));
		s.setFechaModificacion(AHORA.minusDays(5));
		return s;
	}

	@Test
	void creaConFechasDeCreacionYModificacion() {

		AcontecimientoDestacadoDTO creado = this.servicio.crear(1L, formulario(DIA, "  Nació mi sobrina  "));

		assertEquals(DIA, creado.getFecha());
		assertEquals("Nació mi sobrina", creado.getDescripcion());
		assertEquals(AHORA, creado.getFechaCreacion());
		assertEquals(AHORA, creado.getFechaModificacion());
		assertFalse(creado.isNotableEvent());
	}

	@Test
	void siEseDiaHayEventoReseñableGuardaSuNombreYLosIdsDeLosFenomenos() {

		DatosCosmicosParaVAUDTO datos = this.datosValidos();

		FasesLunaresEntity luna = new FasesLunaresEntity();
		luna.setId(11L);
		EclipsesEntity eclipse = new EclipsesEntity();
		eclipse.setId(22L);
		EventoDelDiaDTO evento = new EventoDelDiaDTO();
		evento.setNombre("Luna llena con eclipse");
		evento.setLuna(luna);
		evento.setEclipse(eclipse);
		evento.setMidsison(true);
		when(this.notableEventService.getEventoDelDia(DIA, datos)).thenReturn(evento);

		MidsisonEntity midsison = new MidsisonEntity();
		midsison.setId(33L);
		when(this.midsisonRepository.findByDateBetween(DIA.atStartOfDay(), DIA.atTime(LocalTime.MAX))).thenReturn(List.of(midsison));
		CasalerosEntity casalero = new CasalerosEntity();
		casalero.setId(44L);
		when(this.casalerosRepository.findByDateBetween(DIA.atStartOfDay(), DIA.atTime(LocalTime.MAX))).thenReturn(List.of(casalero));

		AcontecimientoDestacadoDTO creado = this.servicio.crear(1L, formulario(DIA, "Algo"));

		assertTrue(creado.isNotableEvent());
		assertEquals("Luna llena con eclipse", creado.getNotableEventType());
		verify(this.repositorio).saveAndFlush(argThat(a -> a.isNotableEvent()
				&& Long.valueOf(11L).equals(a.getFaseLunarId())
				&& Long.valueOf(22L).equals(a.getEclipseId())
				&& Long.valueOf(33L).equals(a.getMidsisonId())
				&& Long.valueOf(44L).equals(a.getCasaleroId())
				&& a.getApoperiId() == null && a.getSoeId() == null && a.getMetonoId() == null && a.getEclipenoId() == null));
	}

	@Test
	void sinEventoReseñableNoRellenaNingunId() {

		DatosCosmicosParaVAUDTO datos = this.datosValidos();
		EventoDelDiaDTO sinEvento = new EventoDelDiaDTO();
		sinEvento.setNombre("");
		when(this.notableEventService.getEventoDelDia(DIA, datos)).thenReturn(sinEvento);

		AcontecimientoDestacadoDTO creado = this.servicio.crear(1L, formulario(DIA, "Algo"));

		assertFalse(creado.isNotableEvent());
		assertNull(creado.getNotableEventType());
		verify(this.casalerosRepository, never()).findByDateBetween(any(), any());
	}

	@Test
	void unFalloAlCalcularElEventoNoImpideGuardar() {

		when(this.datosCosmicosService.getDatosCosmicos(DIA)).thenThrow(new IllegalStateException("sin datos"));

		AcontecimientoDestacadoDTO creado = this.servicio.crear(1L, formulario(DIA, "Algo"));
		assertFalse(creado.isNotableEvent());
	}

	@Test
	void alCambiarLaFechaSeRecalculaElEvento() {

		AcontecimientoDestacadoEntity a = this.acontecimiento(7L, DIA.minusDays(1));
		a.setNotableEvent(true);
		a.setNotableEventType("Cuarto creciente");
		a.setFaseLunarId(5L);
		when(this.repositorio.findByIdAndUsuarioId(7L, 1L)).thenReturn(Optional.of(a));

		AcontecimientoDestacadoDTO modificado = this.servicio.modificar(1L, 7L, formulario(DIA, "Algo"));

		assertFalse(modificado.isNotableEvent());
		assertNull(a.getFaseLunarId());
	}

	@Test
	void soloUnAcontecimientoPorDia() {

		when(this.repositorio.existsByUsuarioIdAndFecha(1L, DIA)).thenReturn(true);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.crear(1L, formulario(DIA, "Otro")));
		assertEquals(UsuarioException.ACONTECIMIENTO_DUPLICADO, e.getCodigo());
		assertEquals(HttpStatus.CONFLICT, e.getStatus());
		verify(this.repositorio, never()).saveAndFlush(any());
	}

	@Test
	void laRestriccionUnicaTambienDaDuplicado() {

		when(this.repositorio.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk"));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.crear(1L, formulario(DIA, "Otro")));
		assertEquals(UsuarioException.ACONTECIMIENTO_DUPLICADO, e.getCodigo());
	}

	@Test
	void rechazaFechasFueraDelCalendario() {

		UsuarioException e = assertThrows(UsuarioException.class,
				() -> this.servicio.crear(1L, formulario(LocalDate.of(2100, 1, 1), "Futuro")));
		assertEquals(UsuarioException.FECHA_FUERA_DE_RANGO, e.getCodigo());
	}

	@Test
	void modificarCambiaFechaDescripcionYFechaDeModificacion() {

		AcontecimientoDestacadoEntity s = this.acontecimiento(7L, DIA);
		when(this.repositorio.findByIdAndUsuarioId(7L, 1L)).thenReturn(Optional.of(s));

		AcontecimientoDestacadoDTO modificado = this.servicio.modificar(1L, 7L, formulario(DIA.plusDays(1), "Cambiado"));

		assertEquals(DIA.plusDays(1), modificado.getFecha());
		assertEquals("Cambiado", modificado.getDescripcion());
		assertEquals(AHORA.minusDays(5), modificado.getFechaCreacion());
		assertEquals(AHORA, modificado.getFechaModificacion());
	}

	@Test
	void modificarNoPuedeMoverAUnDiaOcupado() {

		when(this.repositorio.findByIdAndUsuarioId(7L, 1L)).thenReturn(Optional.of(this.acontecimiento(7L, DIA)));
		when(this.repositorio.existsByUsuarioIdAndFechaAndIdNot(1L, DIA.plusDays(1), 7L)).thenReturn(true);

		UsuarioException e = assertThrows(UsuarioException.class,
				() -> this.servicio.modificar(1L, 7L, formulario(DIA.plusDays(1), "Cambiado")));
		assertEquals(UsuarioException.ACONTECIMIENTO_DUPLICADO, e.getCodigo());
	}

	@Test
	void unAcontecimientoAjenoSeTrataComoInexistente() {

		when(this.repositorio.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.eliminar(1L, 99L));
		assertEquals(UsuarioException.ACONTECIMIENTO_NO_ENCONTRADO, e.getCodigo());
		assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
		verify(this.repositorio, never()).delete(any());
	}

	@Test
	void eliminarBorraElRegistro() {

		AcontecimientoDestacadoEntity s = this.acontecimiento(7L, DIA);
		when(this.repositorio.findByIdAndUsuarioId(7L, 1L)).thenReturn(Optional.of(s));

		this.servicio.eliminar(1L, 7L);
		verify(this.repositorio).delete(s);
	}

	@Test
	void listaYBuscaPorFecha() {

		when(this.repositorio.findByUsuarioIdOrderByFechaDesc(1L)).thenReturn(List.of(this.acontecimiento(2L, DIA), this.acontecimiento(1L, DIA.minusDays(3))));
		when(this.repositorio.findByUsuarioIdAndFecha(1L, DIA.minusDays(1))).thenReturn(Optional.empty());

		assertEquals(2, this.servicio.listar(1L).size());
		assertTrue(this.servicio.buscarPorFecha(1L, DIA.minusDays(1)).isEmpty());
	}

	@Test
	void sinSesionValidaNoHayAcceso() {

		when(this.usuarioService.getUsuarioConSesion(2L)).thenThrow(
				new UsuarioException(HttpStatus.UNAUTHORIZED, UsuarioException.SESION_NO_VALIDA, ""));

		assertThrows(UsuarioException.class, () -> this.servicio.listar(2L));
		verify(this.repositorio, never()).findByUsuarioIdOrderByFechaDesc(any());
	}
}
