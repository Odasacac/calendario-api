package CCASolutions.Calendario.Config;

import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import CCASolutions.Calendario.ServiceImpl.JwtServiceImpl;
import CCASolutions.Calendario.Services.JwtService;

/**
 * Seguridad de la API. Todo lo que ya existía (calendario, descargas,
 * poblado de BD) sigue siendo público; solo /api/usuarios/me/** exige un JWT
 * de sesión en la cabecera Authorization. Sin cookies ni sesión de servidor,
 * por eso CSRF no aplica.
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

	private static final int BYTES_MINIMOS_CLAVE = 32;

	@Value("${vau.jwt.secreto}")
	private String secretoJwt;

	@Value("${vau.cors.origenes-usuarios}")
	private List<String> origenesUsuarios;

	@Bean
	SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(basic -> basic.disable())
			.formLogin(form -> form.disable())
			.logout(logout -> logout.disable())
			.authorizeHttpRequests(peticiones -> peticiones
					.requestMatchers("/api/usuarios/me", "/api/usuarios/me/**").authenticated()
					.anyRequest().permitAll())
			.oauth2ResourceServer(servidor -> servidor.jwt(jwt -> jwt.jwtAuthenticationConverter(this.conversorJwt())));

		return http.build();
	}

	@Bean
	SecretKey claveJwt() {

		byte[] bytes = Base64.getDecoder().decode(this.secretoJwt);
		if(bytes.length < BYTES_MINIMOS_CLAVE) {
			throw new IllegalStateException("vau.jwt.secreto debe tener al menos " + BYTES_MINIMOS_CLAVE + " bytes en Base64.");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey claveJwt) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(claveJwt));
	}

	/** Solo acepta tokens de sesión emitidos por esta API (un token de recuperación no sirve aquí). */
	@Bean
	public JwtDecoder jwtDecoder(SecretKey claveJwt) {

		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(JwtServiceImpl.EMISOR),
				new JwtClaimValidator<String>(JwtService.CLAIM_TIPO, JwtService.TIPO_SESION::equals)));
		return decoder;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	/** Reloj UTC con precisión de segundos: las fechas no necesitan fracciones de segundo. */
	@Bean
	Clock reloj() {
		return Clock.tick(Clock.systemUTC(), Duration.ofSeconds(1));
	}

	/**
	 * /api/usuarios/** solo desde los orígenes configurados; el resto de la API
	 * mantiene el "*" que ya tenían los @CrossOrigin de los controladores.
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration usuarios = new CorsConfiguration();
		usuarios.setAllowedOrigins(this.origenesUsuarios);
		usuarios.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		usuarios.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
		usuarios.setMaxAge(3600L);

		CorsConfiguration resto = new CorsConfiguration();
		resto.addAllowedOriginPattern("*");
		resto.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		resto.addAllowedHeader("*");
		resto.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);

		UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
		fuente.registerCorsConfiguration("/api/usuarios/**", usuarios);
		fuente.registerCorsConfiguration("/**", resto);
		return fuente;
	}

	private JwtAuthenticationConverter conversorJwt() {

		JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName(JwtService.CLAIM_ROL);
		roles.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
		conversor.setJwtGrantedAuthoritiesConverter(roles);
		return conversor;
	}
}
