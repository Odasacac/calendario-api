package CCASolutions.Calendario.ServiceImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.CambioContrasenyaDTO;
import CCASolutions.Calendario.DTOs.EdicionPerfilDTO;
import CCASolutions.Calendario.DTOs.EliminacionCuentaDTO;
import CCASolutions.Calendario.DTOs.LoginDTO;
import CCASolutions.Calendario.DTOs.PerfilUsuarioDTO;
import CCASolutions.Calendario.DTOs.RegistroUsuarioDTO;
import CCASolutions.Calendario.DTOs.RestablecimientoContrasenyaDTO;
import CCASolutions.Calendario.DTOs.SesionDTO;
import CCASolutions.Calendario.DTOs.TokenRecuperacionDTO;
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
import CCASolutions.Calendario.Services.UsuarioService;
import jakarta.annotation.PostConstruct;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	public static final int EDAD_MAYORIA = 18;
	public static final int DIAS_INACTIVIDAD = 30;
	public static final int DIAS_SUSPENSION_HASTA_ELIMINAR = 30;
	public static final int ANYOS_HASTA_SUSPENDER = 1;
	private static final LocalDate FECHA_NACIMIENTO_MINIMA = LocalDate.of(1900, 1, 1);

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CodigoUsuarioService codigoUsuarioService;

	@Autowired
	private IntentosLoginService intentosLoginService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Clock reloj;

	/**
	 * Se compara contra este hash cuando el usuario no existe, para que el
	 * tiempo de respuesta no revele qué cuentas hay.
	 */
	private String hashFicticio;

	@PostConstruct
	void prepararHashFicticio() {
		this.hashFicticio = this.passwordEncoder.encode("vau-contrasenya-ficticia-1");
	}

	@Override
	@Transactional
	public SesionDTO registrar(RegistroUsuarioDTO registro) {

		String correo = normalizarCorreo(registro.getCorreo());
		if(this.usuarioRepository.existsByCorreo(correo)) {
			throw correoExistente();
		}

		LocalDateTime ahora = LocalDateTime.now(this.reloj);
		LocalDate fechaNacimiento = this.validarFechaNacimiento(registro.getFechaNacimiento());
		String nombre = normalizarTexto(registro.getNombre());
		String apellido = normalizarTexto(registro.getApellido());

		UsuarioEntity usuario = new UsuarioEntity();
		usuario.setNombre(nombre);
		usuario.setApellido(apellido);
		usuario.setFechaNacimiento(fechaNacimiento);
		usuario.setEsMayorDeEdad(this.esMayorDeEdad(fechaNacimiento));
		usuario.setCorreo(correo);
		usuario.setContrasenya(this.passwordEncoder.encode(registro.getContrasenya()));
		usuario.setCodigoUsuario(this.codigoUsuarioService.generar(nombre, apellido, fechaNacimiento.getYear(), null));
		usuario.setEstado(EstadoUsuarioEnum.ACTIVO);
		usuario.setRol(RolUsuarioEnum.USER);
		usuario.setFechaCreacion(ahora);
		usuario.setFechaLastLogin(ahora);

		try {
			usuario = this.usuarioRepository.saveAndFlush(usuario);
		}
		catch(DataIntegrityViolationException e) {
			// Dos registros simultáneos con el mismo correo: la restricción UNIQUE decide.
			throw correoExistente();
		}

		System.out.println("Usuario registrado: " + usuario.getCodigoUsuario());
		return this.sesionPara(usuario, false);
	}

	@Override
	@Transactional
	public SesionDTO login(LoginDTO login) {

		String identificador = login.getIdentificador().trim().toLowerCase(Locale.ROOT);
		Optional<UsuarioEntity> encontrado = identificador.contains("@")
				? this.usuarioRepository.findByCorreo(identificador)
				: this.usuarioRepository.findByCodigoUsuario(identificador);

		String claveIntentos = encontrado.map(u -> "u:" + u.getId()).orElse("i:" + identificador);
		this.intentosLoginService.comprobarBloqueo(claveIntentos);

		String hash = encontrado.map(UsuarioEntity::getContrasenya).orElse(this.hashFicticio);
		boolean coincide = this.passwordEncoder.matches(login.getContrasenya(), hash) && encontrado.isPresent();

		if(!coincide) {
			this.fallo(claveIntentos, UsuarioException.CREDENCIALES_INVALIDAS, "Usuario o contraseña incorrectos.");
		}

		this.intentosLoginService.limpiar(claveIntentos);
		UsuarioEntity usuario = encontrado.get();
		boolean reactivada = false;

		// El estado solo se revela a quien ya ha demostrado conocer la contraseña.
		switch(usuario.getEstado()) {

			case ELIMINADO:
				throw new UsuarioException(HttpStatus.FORBIDDEN, UsuarioException.CUENTA_ELIMINADA, "La cuenta está eliminada.")
						.conDetalle("fechaEliminacion", fechaIso(usuario.getFechaEliminacion()));

			case SUSPENDIDO:
				if(usuario.getMotivoSuspension() != MotivoSuspensionEnum.USUARIO) {
					throw new UsuarioException(HttpStatus.FORBIDDEN, UsuarioException.CUENTA_SUSPENDIDA, "La cuenta está suspendida.")
							.conDetalle("motivo", usuario.getMotivoSuspension() == null ? null : usuario.getMotivoSuspension().name())
							.conDetalle("fechaSuspension", fechaIso(usuario.getFechaSuspension()));
				}
				usuario.setFechaSuspension(null);
				usuario.setMotivoSuspension(null);
				reactivada = true;
				break;

			default:
				break;
		}

		usuario.setEstado(EstadoUsuarioEnum.ACTIVO);
		usuario.setFechaLastLogin(LocalDateTime.now(this.reloj));
		usuario.setEsMayorDeEdad(this.esMayorDeEdad(usuario.getFechaNacimiento()));
		this.usuarioRepository.save(usuario);

		return this.sesionPara(usuario, reactivada);
	}

	@Override
	public TokenRecuperacionDTO verificarRecuperacion(VerificacionRecuperacionDTO verificacion) {

		String codigo = verificacion.getCodigoUsuario().trim().toLowerCase(Locale.ROOT);
		String claveIntentos = "r:" + codigo;
		this.intentosLoginService.comprobarBloqueo(claveIntentos);

		String correo = normalizarCorreo(verificacion.getCorreo());
		UsuarioEntity usuario = this.usuarioRepository.findByCodigoUsuario(codigo)
				.filter(u -> u.getCorreo().equals(correo))
				.filter(u -> u.getEstado() != EstadoUsuarioEnum.ELIMINADO)
				.orElse(null);

		if(usuario == null) {
			this.fallo(claveIntentos, UsuarioException.DATOS_RECUPERACION_INVALIDOS,
					"No hay ninguna cuenta con ese código de usuario y ese correo.");
		}

		this.intentosLoginService.limpiar(claveIntentos);
		TokenRecuperacionDTO respuesta = new TokenRecuperacionDTO();
		respuesta.setToken(this.jwtService.crearTokenRecuperacion(usuario));
		return respuesta;
	}

	@Override
	@Transactional
	public void restablecerContrasenya(RestablecimientoContrasenyaDTO restablecimiento) {

		if(!restablecimiento.getNueva().equals(restablecimiento.getConfirmacion())) {
			throw new UsuarioException(HttpStatus.BAD_REQUEST, UsuarioException.CONTRASENYAS_NO_COINCIDEN, "Las contraseñas no coinciden.");
		}

		UsuarioEntity usuario = this.jwtService.validarTokenRecuperacion(restablecimiento.getToken());
		usuario.setContrasenya(this.passwordEncoder.encode(restablecimiento.getNueva()));
		this.usuarioRepository.save(usuario);

		// Quien recupera la contraseña no debe seguir bloqueado por sus fallos anteriores.
		this.intentosLoginService.limpiar("u:" + usuario.getId());
		System.out.println("Contraseña restablecida: " + usuario.getCodigoUsuario());
	}

	@Override
	public PerfilUsuarioDTO getPerfil(Long idUsuario) {
		return this.perfil(this.usuarioConSesion(idUsuario));
	}

	@Override
	@Transactional
	public PerfilUsuarioDTO editarPerfil(Long idUsuario, EdicionPerfilDTO edicion) {

		UsuarioEntity usuario = this.usuarioConSesion(idUsuario);
		String nombre = normalizarTexto(edicion.getNombre());
		String apellido = normalizarTexto(edicion.getApellido());
		LocalDate fechaNacimiento = this.validarFechaNacimiento(edicion.getFechaNacimiento());

		boolean cambiaCodigo = !nombre.equals(usuario.getNombre())
				|| !apellido.equals(usuario.getApellido())
				|| fechaNacimiento.getYear() != usuario.getFechaNacimiento().getYear();

		usuario.setNombre(nombre);
		usuario.setApellido(apellido);
		usuario.setFechaNacimiento(fechaNacimiento);
		usuario.setEsMayorDeEdad(this.esMayorDeEdad(fechaNacimiento));

		if(cambiaCodigo) {
			usuario.setCodigoUsuario(this.codigoUsuarioService.generar(nombre, apellido, fechaNacimiento.getYear(), usuario.getId()));
		}

		return this.perfil(this.usuarioRepository.save(usuario));
	}

	@Override
	@Transactional
	public void cambiarContrasenya(Long idUsuario, CambioContrasenyaDTO cambio) {

		UsuarioEntity usuario = this.usuarioConSesion(idUsuario);
		this.comprobarContrasenyaActual(usuario, cambio.getActual());

		if(this.passwordEncoder.matches(cambio.getNueva(), usuario.getContrasenya())) {
			throw new UsuarioException(HttpStatus.BAD_REQUEST, UsuarioException.CONTRASENYA_REPETIDA,
					"La nueva contraseña debe ser distinta de la actual.");
		}

		usuario.setContrasenya(this.passwordEncoder.encode(cambio.getNueva()));
		this.usuarioRepository.save(usuario);
	}

	@Override
	@Transactional
	public void suspenderCuenta(Long idUsuario) {

		UsuarioEntity usuario = this.usuarioConSesion(idUsuario);
		usuario.setEstado(EstadoUsuarioEnum.SUSPENDIDO);
		usuario.setMotivoSuspension(MotivoSuspensionEnum.USUARIO);
		usuario.setFechaSuspension(LocalDateTime.now(this.reloj));
		this.usuarioRepository.save(usuario);
		System.out.println("Cuenta suspendida por el usuario: " + usuario.getCodigoUsuario());
	}

	@Override
	@Transactional
	public void eliminarCuenta(Long idUsuario, EliminacionCuentaDTO eliminacion) {

		UsuarioEntity usuario = this.usuarioConSesion(idUsuario);
		this.comprobarContrasenyaActual(usuario, eliminacion.getContrasenya());

		usuario.setEstado(EstadoUsuarioEnum.ELIMINADO);
		usuario.setFechaEliminacion(LocalDateTime.now(this.reloj));
		this.usuarioRepository.save(usuario);
		System.out.println("Cuenta eliminada por el usuario: " + usuario.getCodigoUsuario());
	}

	@Override
	@Transactional
	public int mantenimientoDiario() {

		LocalDateTime ahora = LocalDateTime.now(this.reloj);
		List<UsuarioEntity> modificados = new ArrayList<>();

		for(UsuarioEntity usuario : this.usuarioRepository.findByEstadoNot(EstadoUsuarioEnum.ELIMINADO)) {

			boolean cambia = false;
			EstadoUsuarioEnum estado = usuario.getEstado();

			if(estado == EstadoUsuarioEnum.SUSPENDIDO) {

				if(usuario.getFechaSuspension() != null
						&& usuario.getFechaSuspension().plusDays(DIAS_SUSPENSION_HASTA_ELIMINAR).isBefore(ahora)) {

					usuario.setEstado(EstadoUsuarioEnum.ELIMINADO);
					usuario.setFechaEliminacion(ahora);
					cambia = true;
				}
			}
			else {

				LocalDateTime ultimoAcceso = usuario.getFechaLastLogin() != null ? usuario.getFechaLastLogin() : usuario.getFechaCreacion();

				if(ultimoAcceso.plusYears(ANYOS_HASTA_SUSPENDER).isBefore(ahora)) {

					usuario.setEstado(EstadoUsuarioEnum.SUSPENDIDO);
					usuario.setMotivoSuspension(MotivoSuspensionEnum.INACTIVIDAD);
					usuario.setFechaSuspension(ahora);
					cambia = true;
				}
				else if(estado == EstadoUsuarioEnum.ACTIVO && ultimoAcceso.plusDays(DIAS_INACTIVIDAD).isBefore(ahora)) {

					usuario.setEstado(EstadoUsuarioEnum.INACTIVO);
					cambia = true;
				}
			}

			boolean mayorDeEdad = this.esMayorDeEdad(usuario.getFechaNacimiento());
			if(mayorDeEdad != usuario.isEsMayorDeEdad()) {
				usuario.setEsMayorDeEdad(mayorDeEdad);
				cambia = true;
			}

			if(cambia) {
				modificados.add(usuario);
			}
		}

		this.usuarioRepository.saveAll(modificados);
		this.intentosLoginService.purgarCaducados();
		return modificados.size();
	}

	// ------------------------------------------------------------------

	/** Usuario del token; si ya no puede usar la cuenta, la sesión deja de valer aunque el JWT no haya caducado. */
	private UsuarioEntity usuarioConSesion(Long idUsuario) {

		return this.usuarioRepository.findById(idUsuario)
				.filter(u -> u.getEstado() == EstadoUsuarioEnum.ACTIVO || u.getEstado() == EstadoUsuarioEnum.INACTIVO)
				.orElseThrow(() -> new UsuarioException(HttpStatus.UNAUTHORIZED, UsuarioException.SESION_NO_VALIDA,
						"La sesión ya no es válida. Vuelve a iniciar sesión."));
	}

	private void comprobarContrasenyaActual(UsuarioEntity usuario, String contrasenya) {

		String claveIntentos = "u:" + usuario.getId();
		this.intentosLoginService.comprobarBloqueo(claveIntentos);

		if(!this.passwordEncoder.matches(contrasenya, usuario.getContrasenya())) {
			// 400 y no 401: con sesión válida, un 401 haría que el front la cerrase.
			this.fallo(claveIntentos, HttpStatus.BAD_REQUEST, UsuarioException.CONTRASENYA_ACTUAL_INCORRECTA,
					"La contraseña actual no es correcta.");
		}
		this.intentosLoginService.limpiar(claveIntentos);
	}

	private void fallo(String claveIntentos, String codigo, String mensaje) {
		this.fallo(claveIntentos, HttpStatus.UNAUTHORIZED, codigo, mensaje);
	}

	/** Apunta el fallo y lanza el error, o el bloqueo si era el último intento. */
	private void fallo(String claveIntentos, HttpStatus status, String codigo, String mensaje) {

		int restantes = this.intentosLoginService.registrarFallo(claveIntentos);
		if(restantes == 0) {
			this.intentosLoginService.comprobarBloqueo(claveIntentos);
		}
		throw new UsuarioException(status, codigo, mensaje).conDetalle("intentosRestantes", restantes);
	}

	private SesionDTO sesionPara(UsuarioEntity usuario, boolean reactivada) {

		SesionDTO sesion = this.jwtService.crearSesion(usuario);
		sesion.setReactivada(reactivada);
		sesion.setUsuario(this.perfil(usuario));
		return sesion;
	}

	private PerfilUsuarioDTO perfil(UsuarioEntity usuario) {

		PerfilUsuarioDTO perfil = new PerfilUsuarioDTO();
		perfil.setCodigoUsuario(usuario.getCodigoUsuario());
		perfil.setNombre(usuario.getNombre());
		perfil.setApellido(usuario.getApellido());
		perfil.setFechaNacimiento(usuario.getFechaNacimiento());
		perfil.setEsMayorDeEdad(usuario.isEsMayorDeEdad());
		perfil.setCorreo(usuario.getCorreo());
		perfil.setEstado(usuario.getEstado());
		perfil.setRol(usuario.getRol());
		perfil.setFechaCreacion(usuario.getFechaCreacion());
		perfil.setFechaLastLogin(usuario.getFechaLastLogin());
		return perfil;
	}

	private LocalDate validarFechaNacimiento(LocalDate fecha) {

		if(fecha == null || fecha.isBefore(FECHA_NACIMIENTO_MINIMA) || !fecha.isBefore(LocalDate.now(this.reloj))) {
			throw new UsuarioException(HttpStatus.BAD_REQUEST, UsuarioException.FECHA_NACIMIENTO_INVALIDA,
					"La fecha de nacimiento no es válida.");
		}
		return fecha;
	}

	private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
		return Period.between(fechaNacimiento, LocalDate.now(this.reloj)).getYears() >= EDAD_MAYORIA;
	}

	private static UsuarioException correoExistente() {
		return new UsuarioException(HttpStatus.CONFLICT, UsuarioException.CORREO_EXISTENTE, "Ya existe una cuenta con ese correo.");
	}

	private static String normalizarCorreo(String correo) {
		return correo.trim().toLowerCase(Locale.ROOT);
	}

	/** Quita espacios sobrantes: "  Carlos   José " -> "Carlos José". */
	private static String normalizarTexto(String texto) {
		return texto.trim().replaceAll("\\s+", " ");
	}

	private static String fechaIso(LocalDateTime fecha) {
		return fecha == null ? null : fecha.toLocalDate().toString();
	}
}
