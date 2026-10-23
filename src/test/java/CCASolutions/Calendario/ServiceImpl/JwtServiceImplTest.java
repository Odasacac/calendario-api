package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import CCASolutions.Calendario.Config.SeguridadConfig;
import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;
import CCASolutions.Calendario.Enums.RolUsuarioEnum;
import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Repositories.UsuarioRepository;

class JwtServiceImplTest {

	private final SecretKey clave = new SecretKeySpec(new byte[32], "HmacSHA256");
	private final SeguridadConfig config = new SeguridadConfig();
	private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

	private JwtServiceImpl servicio;
	private JwtDecoder decoderSesion;
	private UsuarioEntity usuario;

	@BeforeEach
	void preparar() {

		this.servicio = new JwtServiceImpl();
		ReflectionTestUtils.setField(this.servicio, "jwtEncoder", this.config.jwtEncoder(this.clave));
		ReflectionTestUtils.setField(this.servicio, "claveJwt", this.clave);
		ReflectionTestUtils.setField(this.servicio, "usuarioRepository", this.usuarioRepository);
		ReflectionTestUtils.setField(this.servicio, "reloj", Clock.systemUTC());
		ReflectionTestUtils.setField(this.servicio, "minutosSesion", 120L);
		ReflectionTestUtils.setField(this.servicio, "minutosRecuperacion", 10L);
		ReflectionTestUtils.invokeMethod(this.servicio, "crearDecoderRecuperacion");
		this.decoderSesion = this.config.jwtDecoder(this.clave);

		this.usuario = new UsuarioEntity();
		this.usuario.setId(42L);
		this.usuario.setRol(RolUsuarioEnum.USER);
		this.usuario.setEstado(EstadoUsuarioEnum.ACTIVO);
		this.usuario.setContrasenya("$2a$hash-original");
		when(this.usuarioRepository.findById(42L)).thenReturn(Optional.of(this.usuario));
	}

	@Test
	void elTokenDeSesionLlevaIdYRol() {

		Jwt jwt = this.decoderSesion.decode(this.servicio.crearSesion(this.usuario).getToken());

		assertEquals("42", jwt.getSubject());
		assertEquals("USER", jwt.getClaimAsString("rol"));
		assertEquals("sesion", jwt.getClaimAsString("tipo"));
	}

	@Test
	void unTokenDeRecuperacionNoSirveComoSesion() {

		String token = this.servicio.crearTokenRecuperacion(this.usuario);
		assertThrows(JwtException.class, () -> this.decoderSesion.decode(token));
	}

	@Test
	void unTokenDeSesionNoSirveParaRecuperar() {

		String token = this.servicio.crearSesion(this.usuario).getToken();
		assertThrows(UsuarioException.class, () -> this.servicio.validarTokenRecuperacion(token));
	}

	@Test
	void elTokenDeRecuperacionEsDeUnSoloUso() {

		String token = this.servicio.crearTokenRecuperacion(this.usuario);
		assertEquals(this.usuario, this.servicio.validarTokenRecuperacion(token));

		this.usuario.setContrasenya("$2a$hash-nuevo");

		UsuarioException e = assertThrows(UsuarioException.class, () -> this.servicio.validarTokenRecuperacion(token));
		assertEquals(UsuarioException.TOKEN_RECUPERACION_INVALIDO, e.getCodigo());
	}

	@Test
	void unTokenManipuladoSeRechaza() {

		String token = this.servicio.crearTokenRecuperacion(this.usuario);
		String manipulado = token.substring(0, token.length() - 4) + "AAAA";

		assertThrows(UsuarioException.class, () -> this.servicio.validarTokenRecuperacion(manipulado));
	}
}
