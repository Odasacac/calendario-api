package CCASolutions.Calendario.ServiceImpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.SesionDTO;
import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;
import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Repositories.UsuarioRepository;
import CCASolutions.Calendario.Services.JwtService;
import jakarta.annotation.PostConstruct;

@Service
public class JwtServiceImpl implements JwtService {

	public static final String EMISOR = "calendario-vau";
	private static final String CLAIM_HUELLA = "hp";

	@Autowired
	private JwtEncoder jwtEncoder;

	@Autowired
	private SecretKey claveJwt;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private Clock reloj;

	@Value("${vau.jwt.minutos-sesion}")
	private long minutosSesion;

	@Value("${vau.jwt.minutos-recuperacion}")
	private long minutosRecuperacion;

	private NimbusJwtDecoder decoderRecuperacion;

	@PostConstruct
	void crearDecoderRecuperacion() {

		this.decoderRecuperacion = NimbusJwtDecoder.withSecretKey(this.claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
		this.decoderRecuperacion.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(EMISOR),
				new JwtClaimValidator<String>(CLAIM_TIPO, TIPO_RECUPERACION::equals)));
	}

	@Override
	public SesionDTO crearSesion(UsuarioEntity usuario) {

		Instant ahora = Instant.now(this.reloj);
		Instant expira = ahora.plus(this.minutosSesion, ChronoUnit.MINUTES);

		JwtClaimsSet claims = this.claimsBase(usuario, ahora, expira)
				.claim(CLAIM_TIPO, TIPO_SESION)
				.claim(CLAIM_ROL, usuario.getRol().name())
				.build();

		SesionDTO sesion = new SesionDTO();
		sesion.setToken(this.firmar(claims));
		sesion.setExpiraEn(expira);
		return sesion;
	}

	@Override
	public String crearTokenRecuperacion(UsuarioEntity usuario) {

		Instant ahora = Instant.now(this.reloj);
		JwtClaimsSet claims = this.claimsBase(usuario, ahora, ahora.plus(this.minutosRecuperacion, ChronoUnit.MINUTES))
				.claim(CLAIM_TIPO, TIPO_RECUPERACION)
				.claim(CLAIM_HUELLA, huella(usuario.getContrasenya()))
				.build();

		return this.firmar(claims);
	}

	@Override
	public UsuarioEntity validarTokenRecuperacion(String token) {

		try {

			Jwt jwt = this.decoderRecuperacion.decode(token);
			UsuarioEntity usuario = this.usuarioRepository.findById(Long.valueOf(jwt.getSubject())).orElse(null);

			boolean valido = usuario != null
					&& usuario.getEstado() != EstadoUsuarioEnum.ELIMINADO
					&& MessageDigest.isEqual(
							huella(usuario.getContrasenya()).getBytes(StandardCharsets.UTF_8),
							String.valueOf(jwt.getClaimAsString(CLAIM_HUELLA)).getBytes(StandardCharsets.UTF_8));

			if(valido) {
				return usuario;
			}
		}
		catch(JwtException | NumberFormatException e) {
			/* token caducado, manipulado o de otro tipo */
		}

		throw new UsuarioException(HttpStatus.BAD_REQUEST, UsuarioException.TOKEN_RECUPERACION_INVALIDO,
				"El enlace de recuperación ha caducado o ya se ha usado. Vuelve a empezar.");
	}

	private JwtClaimsSet.Builder claimsBase(UsuarioEntity usuario, Instant emitido, Instant expira) {

		return JwtClaimsSet.builder()
				.issuer(EMISOR)
				.subject(String.valueOf(usuario.getId()))
				.issuedAt(emitido)
				.expiresAt(expira);
	}

	private String firmar(JwtClaimsSet claims) {

		JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
		return this.jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
	}

	/** Primeros 16 bytes del SHA-256 del hash: identifica la contraseña sin exponerla. */
	private static String huella(String hashContrasenya) {

		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(hashContrasenya.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(Arrays.copyOf(digest, 16));
		}
		catch(NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
