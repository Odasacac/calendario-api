package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import CCASolutions.Calendario.DTOs.CambioContrasenyaDTO;
import CCASolutions.Calendario.DTOs.EdicionPerfilDTO;
import CCASolutions.Calendario.DTOs.EliminacionCuentaDTO;
import CCASolutions.Calendario.DTOs.LoginDTO;
import CCASolutions.Calendario.DTOs.PerfilUsuarioDTO;
import CCASolutions.Calendario.DTOs.RegistroUsuarioDTO;
import CCASolutions.Calendario.DTOs.RestablecimientoContrasenyaDTO;
import CCASolutions.Calendario.DTOs.SesionDTO;
import CCASolutions.Calendario.DTOs.VerificacionRecuperacionDTO;
import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;
import CCASolutions.Calendario.Enums.MotivoSuspensionEnum;
import CCASolutions.Calendario.Enums.RolUsuarioEnum;
import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Repositories.UsuarioRepository;
import CCASolutions.Calendario.Services.CodigoUsuarioService;
import CCASolutions.Calendario.Services.IntentosLoginService;
import CCASolutions.Calendario.Services.JwtService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

	private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 23, 3, 0);
	private static final String CONTRASENYA = "secreta123";

	/** Coste mínimo de BCrypt para que los tests vayan rápidos. */
	private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);

	@Mock private UsuarioRepository usuarioRepository;
	@Mock private CodigoUsuarioService codigoUsuarioService;
	@Mock private IntentosLoginService intentosLoginService;
	@Mock private JwtService jwtService;

	@InjectMocks
	private UsuarioServiceImpl servicio;

	@BeforeEach
	void preparar() {

		ReflectionTestUtils.setField(this.servicio, "passwordEncoder", this.encoder);
		ReflectionTestUtils.setField(this.servicio, "reloj", Clock.fixed(AHORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
		ReflectionTestUtils.invokeMethod(this.servicio, "prepararHashFicticio");

		lenient().when(this.usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
		lenient().when(this.usuarioRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
		lenient().when(this.jwtService.crearSesion(any())).thenAnswer(i -> {
			SesionDTO sesion = new SesionDTO();
			sesion.setToken("token");
			sesion.setExpiraEn(Instant.EPOCH);
			return sesion;
		});
	}

	private UsuarioEntity usuario(EstadoUsuarioEnum estado) {

		UsuarioEntity u = new UsuarioEntity();
		u.setId(1L);
		u.setNombre("Carlos");
		u.setApellido("Casado");
		u.setFechaNacimiento(LocalDate.of(1993, 5, 10));
		u.setEsMayorDeEdad(true);
		u.setCorreo("carlos@vau.com");
		u.setContrasenya(this.encoder.encode(CONTRASENYA));
		u.setCodigoUsuario("ccasado");
		u.setEstado(estado);
		u.setRol(RolUsuarioEnum.USER);
		u.setFechaCreacion(AHORA.minusYears(2));
		u.setFechaLastLogin(AHORA.minusDays(1));
		return u;
	}

	private static LoginDTO login(String identificador, String contrasenya) {
		LoginDTO login = new LoginDTO();
		login.setIdentificador(identificador);
		login.setContrasenya(contrasenya);
		return login;
	}

	// --- Registro -----------------------------------------------------

	@Test
	void registraConCorreoNormalizadoContrasenyaHasheadaYCodigoGenerado() {

		RegistroUsuarioDTO registro = new RegistroUsuarioDTO();
		registro.setNombre("  Carlos ");
		registro.setApellido("Casado");
		registro.setFechaNacimiento(LocalDate.of(2010, 1, 1));
		registro.setCorreo(" Carlos@VAU.com ");
		registro.setContrasenya(CONTRASENYA);
		when(this.codigoUsuarioService.generar("Carlos", "Casado", 2010, null)).thenReturn("ccasado");

		SesionDTO sesion = this.servicio.registrar(registro);
		PerfilUsuarioDTO perfil = sesion.getUsuario();

		assertEquals("carlos@vau.com", perfil.getCorreo());
		assertEquals("ccasado", perfil.getCodigoUsuario());
		assertEquals(EstadoUsuarioEnum.ACTIVO, perfil.getEstado());
		assertEquals(RolUsuarioEnum.USER, perfil.getRol());
		assertFalse(perfil.isEsMayorDeEdad());
		assertEquals(AHORA, perfil.getFechaCreacion());
		verify(this.usuarioRepository).saveAndFlush(argThat(u ->
				!u.getContrasenya().equals(CONTRASENYA) && this.encoder.matches(CONTRASENYA, u.getContrasenya())));
	}

	@Test
	void nombreYApellidoQuedanConMayusculaInicialYElRestoEnMinuscula() {

		assertEquals("María-José", UsuarioServiceImpl.normalizarTexto("  mARÍA-josé "));
		assertEquals("Casado Aguilera", UsuarioServiceImpl.normalizarTexto("CASADO   aguilera"));
		assertEquals("Ñúñez", UsuarioServiceImpl.normalizarTexto("ñÚÑEZ"));
	}

	@Test
	void registraYEditaConElNombreCapitalizado() {

		RegistroUsuarioDTO registro = new RegistroUsuarioDTO();
		registro.setNombre("cARLOS");
		registro.setApellido("CASADO");
		registro.setFechaNacimiento(LocalDate.of(1993, 5, 10));
		registro.setCorreo("c@vau.com");
		registro.setContrasenya(CONTRASENYA);
		when(this.codigoUsuarioService.generar("Carlos", "Casado", 1993, null)).thenReturn("ccasado");

		PerfilUsuarioDTO perfil = this.servicio.registrar(registro).getUsuario();
		assertEquals("Carlos", perfil.getNombre());
		assertEquals("Casado", perfil.getApellido());

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
		EdicionPerfilDTO e = new EdicionPerfilDTO();
		e.setNombre("carlos");
		e.setApellido("casado");
		e.setFechaNacimiento(u.getFechaNacimiento());

		// Mismo nombre tras capitalizar: no cambia el código.
		assertEquals("Carlos", this.servicio.editarPerfil(1L, e).getNombre());
		verify(this.codigoUsuarioService, never()).generar("Carlos", "Casado", 1993, 1L);
	}

	@Test
	void rechazaCorreoRepetido() {

		RegistroUsuarioDTO registro = new RegistroUsuarioDTO();
		registro.setCorreo("carlos@vau.com");
		when(this.usuarioRepository.existsByCorreo("carlos@vau.com")).thenReturn(true);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.registrar(registro));
		assertEquals(UsuarioException.CORREO_EXISTENTE, e.getCodigo());
		assertEquals(HttpStatus.CONFLICT, e.getStatus());
	}

	@Test
	void rechazaFechaDeNacimientoFutura() {

		RegistroUsuarioDTO registro = new RegistroUsuarioDTO();
		registro.setNombre("Carlos");
		registro.setApellido("Casado");
		registro.setCorreo("c@vau.com");
		registro.setFechaNacimiento(AHORA.toLocalDate().plusDays(1));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.registrar(registro));
		assertEquals(UsuarioException.FECHA_NACIMIENTO_INVALIDA, e.getCodigo());
	}

	// --- Login --------------------------------------------------------

	@Test
	void loginPorCodigoDeUsuarioActualizaUltimoAcceso() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));

		SesionDTO sesion = this.servicio.login(login(" CCasado ", CONTRASENYA));

		assertEquals("token", sesion.getToken());
		assertEquals(AHORA, u.getFechaLastLogin());
		verify(this.intentosLoginService).limpiar("u:1");
	}

	@Test
	void loginPorCorreo() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCorreo("carlos@vau.com")).thenReturn(Optional.of(u));

		assertEquals("token", this.servicio.login(login("Carlos@vau.com", CONTRASENYA)).getToken());
	}

	@Test
	void contrasenyaIncorrectaInformaDeLosIntentosRestantes() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));
		when(this.intentosLoginService.registrarFallo("u:1")).thenReturn(2);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", "otra1234")));
		assertEquals(UsuarioException.CREDENCIALES_INVALIDAS, e.getCodigo());
		assertEquals(2, e.getDetalles().get("intentosRestantes"));
	}

	@Test
	void usuarioInexistenteDaElMismoErrorQueContrasenyaIncorrecta() {

		when(this.usuarioRepository.findByCodigoUsuario("nadie")).thenReturn(Optional.empty());
		when(this.intentosLoginService.registrarFallo("i:nadie")).thenReturn(2);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("nadie", CONTRASENYA)));
		assertEquals(UsuarioException.CREDENCIALES_INVALIDAS, e.getCodigo());
	}

	@Test
	void alAgotarLosIntentosDevuelveBloqueo() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));
		when(this.intentosLoginService.registrarFallo("u:1")).thenReturn(0);
		UsuarioException bloqueo = new UsuarioException(HttpStatus.TOO_MANY_REQUESTS, UsuarioException.DEMASIADOS_INTENTOS, "");
		// Primera comprobación (antes de validar) pasa; la de después del fallo bloquea.
		doNothing().doThrow(bloqueo).when(this.intentosLoginService).comprobarBloqueo("u:1");

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", "mala1234")));
		assertEquals(UsuarioException.DEMASIADOS_INTENTOS, e.getCodigo());
	}

	@Test
	void cuentaBloqueadaNoLlegaAComprobarLaContrasenya() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));
		doThrow(new UsuarioException(HttpStatus.TOO_MANY_REQUESTS, UsuarioException.DEMASIADOS_INTENTOS, ""))
				.when(this.intentosLoginService).comprobarBloqueo("u:1");

		assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", CONTRASENYA)));
		verify(this.usuarioRepository, never()).save(any());
	}

	@Test
	void usuarioInactivoVuelveAActivo() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.INACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));

		this.servicio.login(login("ccasado", CONTRASENYA));
		assertEquals(EstadoUsuarioEnum.ACTIVO, u.getEstado());
	}

	@Test
	void suspensionDelPropioUsuarioSeLevantaAlEntrar() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.SUSPENDIDO);
		u.setMotivoSuspension(MotivoSuspensionEnum.USUARIO);
		u.setFechaSuspension(AHORA.minusDays(3));
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));

		SesionDTO sesion = this.servicio.login(login("ccasado", CONTRASENYA));

		assertTrue(sesion.isReactivada());
		assertEquals(EstadoUsuarioEnum.ACTIVO, u.getEstado());
		assertNull(u.getMotivoSuspension());
		assertNull(u.getFechaSuspension());
	}

	@Test
	void suspensionPorOtroMotivoImpideEntrar() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.SUSPENDIDO);
		u.setMotivoSuspension(MotivoSuspensionEnum.INACTIVIDAD);
		u.setFechaSuspension(AHORA.minusDays(3));
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", CONTRASENYA)));
		assertEquals(UsuarioException.CUENTA_SUSPENDIDA, e.getCodigo());
		assertEquals("INACTIVIDAD", e.getDetalles().get("motivo"));
		assertEquals(EstadoUsuarioEnum.SUSPENDIDO, u.getEstado());
	}

	@Test
	void cuentaEliminadaInformaDeLaFecha() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ELIMINADO);
		u.setFechaEliminacion(LocalDateTime.of(2026, 9, 1, 12, 0));
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", CONTRASENYA)));
		assertEquals(UsuarioException.CUENTA_ELIMINADA, e.getCodigo());
		assertEquals("2026-09-01", e.getDetalles().get("fechaEliminacion"));
	}

	@Test
	void elEstadoNoSeRevelaSinLaContrasenyaCorrecta() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ELIMINADO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));
		when(this.intentosLoginService.registrarFallo("u:1")).thenReturn(2);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.login(login("ccasado", "mala1234")));
		assertEquals(UsuarioException.CREDENCIALES_INVALIDAS, e.getCodigo());
	}

	// --- Recuperación -------------------------------------------------

	@Test
	void verificaCodigoYCorreoDeLaMismaCuenta() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(u));
		when(this.jwtService.crearTokenRecuperacion(u)).thenReturn("tok-rec");

		VerificacionRecuperacionDTO v = new VerificacionRecuperacionDTO();
		v.setCodigoUsuario("CCASADO");
		v.setCorreo("CARLOS@vau.com");

		assertEquals("tok-rec", this.servicio.verificarRecuperacion(v).getToken());
	}

	@Test
	void rechazaCorreoQueNoEsDeEseCodigo() {

		when(this.usuarioRepository.findByCodigoUsuario("ccasado")).thenReturn(Optional.of(this.usuario(EstadoUsuarioEnum.ACTIVO)));
		when(this.intentosLoginService.registrarFallo("r:ccasado")).thenReturn(2);

		VerificacionRecuperacionDTO v = new VerificacionRecuperacionDTO();
		v.setCodigoUsuario("ccasado");
		v.setCorreo("otro@vau.com");

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.verificarRecuperacion(v));
		assertEquals(UsuarioException.DATOS_RECUPERACION_INVALIDOS, e.getCodigo());
	}

	@Test
	void restablecerExigeQueLasContrasenyasCoincidan() {

		RestablecimientoContrasenyaDTO r = new RestablecimientoContrasenyaDTO();
		r.setToken("tok");
		r.setNueva("nueva1234");
		r.setConfirmacion("nueva12345");

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.restablecerContrasenya(r));
		assertEquals(UsuarioException.CONTRASENYAS_NO_COINCIDEN, e.getCodigo());
		verify(this.jwtService, never()).validarTokenRecuperacion(anyString());
	}

	@Test
	void restableceLaContrasenyaYDesbloqueaLaCuenta() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.jwtService.validarTokenRecuperacion("tok")).thenReturn(u);

		RestablecimientoContrasenyaDTO r = new RestablecimientoContrasenyaDTO();
		r.setToken("tok");
		r.setNueva("nueva1234");
		r.setConfirmacion("nueva1234");
		this.servicio.restablecerContrasenya(r);

		assertTrue(this.encoder.matches("nueva1234", u.getContrasenya()));
		verify(this.intentosLoginService).limpiar("u:1");
	}

	// --- Perfil -------------------------------------------------------

	@Test
	void editarNombreRecalculaElCodigo() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
		when(this.codigoUsuarioService.generar("Lucía", "Casado", 1993, 1L)).thenReturn("lcasado");

		EdicionPerfilDTO e = new EdicionPerfilDTO();
		e.setNombre("Lucía");
		e.setApellido("Casado");
		e.setFechaNacimiento(u.getFechaNacimiento());

		assertEquals("lcasado", this.servicio.editarPerfil(1L, e).getCodigoUsuario());
	}

	@Test
	void editarSinCambiosQueAfectenAlCodigoNoLoRecalcula() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

		EdicionPerfilDTO e = new EdicionPerfilDTO();
		e.setNombre("Carlos");
		e.setApellido("Casado");
		e.setFechaNacimiento(LocalDate.of(1993, 12, 31));

		assertEquals("ccasado", this.servicio.editarPerfil(1L, e).getCodigoUsuario());
		verify(this.codigoUsuarioService, never()).generar(anyString(), anyString(), anyInt(), any());
	}

	@Test
	void cambiarContrasenyaExigeLaActual() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
		when(this.intentosLoginService.registrarFallo("u:1")).thenReturn(2);

		CambioContrasenyaDTO c = new CambioContrasenyaDTO();
		c.setActual("mala1234");
		c.setNueva("nueva1234");

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.cambiarContrasenya(1L, c));
		assertEquals(UsuarioException.CONTRASENYA_ACTUAL_INCORRECTA, e.getCodigo());
		// Con un 401 el front interpretaría que la sesión ha caducado.
		assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
	}

	@Test
	void cambiarContrasenyaPorLaMismaSeRechaza() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

		CambioContrasenyaDTO c = new CambioContrasenyaDTO();
		c.setActual(CONTRASENYA);
		c.setNueva(CONTRASENYA);

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.cambiarContrasenya(1L, c));
		assertEquals(UsuarioException.CONTRASENYA_REPETIDA, e.getCodigo());
	}

	@Test
	void suspenderMarcaElMotivoDeUsuario() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

		this.servicio.suspenderCuenta(1L);

		assertEquals(EstadoUsuarioEnum.SUSPENDIDO, u.getEstado());
		assertEquals(MotivoSuspensionEnum.USUARIO, u.getMotivoSuspension());
		assertEquals(AHORA, u.getFechaSuspension());
	}

	@Test
	void eliminarExigeContrasenyaYEsBorradoLogico() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

		EliminacionCuentaDTO d = new EliminacionCuentaDTO();
		d.setContrasenya(CONTRASENYA);
		this.servicio.eliminarCuenta(1L, d);

		assertEquals(EstadoUsuarioEnum.ELIMINADO, u.getEstado());
		assertEquals(AHORA, u.getFechaEliminacion());
		verify(this.usuarioRepository, never()).delete(any());
	}

	@Test
	void unaCuentaSuspendidaNoPuedeUsarSuSesion() {

		when(this.usuarioRepository.findById(1L)).thenReturn(Optional.of(this.usuario(EstadoUsuarioEnum.SUSPENDIDO)));

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.getPerfil(1L));
		assertEquals(UsuarioException.SESION_NO_VALIDA, e.getCodigo());
		assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
	}

	// --- Cron ---------------------------------------------------------

	@Test
	void mantenimientoDiarioAplicaCadaRegla() {

		UsuarioEntity reciente = this.usuario(EstadoUsuarioEnum.ACTIVO);
		reciente.setFechaLastLogin(AHORA.minusDays(29));

		UsuarioEntity inactivo = this.usuario(EstadoUsuarioEnum.ACTIVO);
		inactivo.setFechaLastLogin(AHORA.minusDays(31));

		UsuarioEntity unAnyo = this.usuario(EstadoUsuarioEnum.INACTIVO);
		unAnyo.setFechaLastLogin(AHORA.minusYears(1).minusDays(1));

		UsuarioEntity suspendidoViejo = this.usuario(EstadoUsuarioEnum.SUSPENDIDO);
		suspendidoViejo.setMotivoSuspension(MotivoSuspensionEnum.USUARIO);
		suspendidoViejo.setFechaSuspension(AHORA.minusDays(31));

		UsuarioEntity suspendidoReciente = this.usuario(EstadoUsuarioEnum.SUSPENDIDO);
		suspendidoReciente.setMotivoSuspension(MotivoSuspensionEnum.ADMINISTRADOR);
		suspendidoReciente.setFechaSuspension(AHORA.minusDays(10));

		UsuarioEntity cumpleDieciocho = this.usuario(EstadoUsuarioEnum.ACTIVO);
		cumpleDieciocho.setFechaNacimiento(AHORA.toLocalDate().minusYears(18));
		cumpleDieciocho.setEsMayorDeEdad(false);

		when(this.usuarioRepository.findByEstadoNot(EstadoUsuarioEnum.ELIMINADO)).thenReturn(
				List.of(reciente, inactivo, unAnyo, suspendidoViejo, suspendidoReciente, cumpleDieciocho));

		assertEquals(4, this.servicio.mantenimientoDiario());

		assertEquals(EstadoUsuarioEnum.ACTIVO, reciente.getEstado());
		assertEquals(EstadoUsuarioEnum.INACTIVO, inactivo.getEstado());
		assertEquals(EstadoUsuarioEnum.SUSPENDIDO, unAnyo.getEstado());
		assertEquals(MotivoSuspensionEnum.INACTIVIDAD, unAnyo.getMotivoSuspension());
		assertEquals(AHORA, unAnyo.getFechaSuspension());
		assertEquals(EstadoUsuarioEnum.ELIMINADO, suspendidoViejo.getEstado());
		assertEquals(AHORA, suspendidoViejo.getFechaEliminacion());
		assertEquals(EstadoUsuarioEnum.SUSPENDIDO, suspendidoReciente.getEstado());
		assertTrue(cumpleDieciocho.isEsMayorDeEdad());
		verify(this.intentosLoginService).purgarCaducados();
	}

	@Test
	void sinLoginPrevioSeCuentaDesdeLaCreacion() {

		UsuarioEntity u = this.usuario(EstadoUsuarioEnum.ACTIVO);
		u.setFechaLastLogin(null);
		u.setFechaCreacion(AHORA.minusDays(40));
		when(this.usuarioRepository.findByEstadoNot(EstadoUsuarioEnum.ELIMINADO)).thenReturn(List.of(u));

		this.servicio.mantenimientoDiario();
		assertEquals(EstadoUsuarioEnum.INACTIVO, u.getEstado());
		assertNotEquals(EstadoUsuarioEnum.SUSPENDIDO, u.getEstado());
	}
}
