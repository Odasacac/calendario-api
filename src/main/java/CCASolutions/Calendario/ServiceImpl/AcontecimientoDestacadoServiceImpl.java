package CCASolutions.Calendario.ServiceImpl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCASolutions.Calendario.DTOs.AcontecimientoDestacadoDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.EventoDelDiaDTO;
import CCASolutions.Calendario.DTOs.FormularioAcontecimientoDTO;
import CCASolutions.Calendario.Entities.AcontecimientoDestacadoEntity;
import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Exceptions.UsuarioException;
import CCASolutions.Calendario.Repositories.AcontecimientoDestacadoRepository;
import CCASolutions.Calendario.Repositories.CasalerosRepository;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Services.AcontecimientoDestacadoService;
import CCASolutions.Calendario.Services.DatosCosmicosService;
import CCASolutions.Calendario.Services.NotableEventService;
import CCASolutions.Calendario.Services.UsuarioService;

@Service
public class AcontecimientoDestacadoServiceImpl implements AcontecimientoDestacadoService {

	@Autowired
	private AcontecimientoDestacadoRepository acontecimientoDestacadoRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private DatosCosmicosService datosCosmicosService;

	@Autowired
	private NotableEventService notableEventService;

	@Autowired
	private MidsisonRepository midsisonRepository;

	@Autowired
	private CasalerosRepository casalerosRepository;

	@Autowired
	private Clock reloj;

	@Override
	@Transactional(readOnly = true)
	public List<AcontecimientoDestacadoDTO> listar(Long idUsuario) {

		this.usuarioService.getUsuarioConSesion(idUsuario);
		return this.acontecimientoDestacadoRepository.findByUsuarioIdOrderByFechaDesc(idUsuario).stream()
				.map(AcontecimientoDestacadoServiceImpl::aDTO)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<AcontecimientoDestacadoDTO> buscarPorFecha(Long idUsuario, LocalDate fecha) {

		this.usuarioService.getUsuarioConSesion(idUsuario);
		return this.acontecimientoDestacadoRepository.findByUsuarioIdAndFecha(idUsuario, fecha).map(AcontecimientoDestacadoServiceImpl::aDTO);
	}

	@Override
	@Transactional
	public AcontecimientoDestacadoDTO crear(Long idUsuario, FormularioAcontecimientoDTO formulario) {

		UsuarioEntity usuario = this.usuarioService.getUsuarioConSesion(idUsuario);
		LocalDate fecha = validarFecha(formulario.getFecha());

		if(this.acontecimientoDestacadoRepository.existsByUsuarioIdAndFecha(idUsuario, fecha)) {
			throw duplicado(fecha);
		}

		LocalDateTime ahora = LocalDateTime.now(this.reloj);
		AcontecimientoDestacadoEntity acontecimiento = new AcontecimientoDestacadoEntity();
		acontecimiento.setUsuario(usuario);
		acontecimiento.setFecha(fecha);
		acontecimiento.setDescripcion(formulario.getDescripcion().trim());
		acontecimiento.setFechaCreacion(ahora);
		acontecimiento.setFechaModificacion(ahora);
		this.rellenarEventoReseñable(acontecimiento);

		return aDTO(this.guardar(acontecimiento));
	}

	@Override
	@Transactional
	public AcontecimientoDestacadoDTO modificar(Long idUsuario, Long idAcontecimiento, FormularioAcontecimientoDTO formulario) {

		this.usuarioService.getUsuarioConSesion(idUsuario);
		AcontecimientoDestacadoEntity acontecimiento = this.buscarPropio(idUsuario, idAcontecimiento);
		LocalDate fecha = validarFecha(formulario.getFecha());

		if(this.acontecimientoDestacadoRepository.existsByUsuarioIdAndFechaAndIdNot(idUsuario, fecha, idAcontecimiento)) {
			throw duplicado(fecha);
		}

		acontecimiento.setFecha(fecha);
		acontecimiento.setDescripcion(formulario.getDescripcion().trim());
		acontecimiento.setFechaModificacion(LocalDateTime.now(this.reloj));
		this.rellenarEventoReseñable(acontecimiento);

		return aDTO(this.guardar(acontecimiento));
	}

	@Override
	@Transactional
	public void eliminar(Long idUsuario, Long idAcontecimiento) {

		this.usuarioService.getUsuarioConSesion(idUsuario);
		this.acontecimientoDestacadoRepository.delete(this.buscarPropio(idUsuario, idAcontecimiento));
	}

	// ------------------------------------------------------------------

	/**
	 * Calcula el evento reseñable de la fecha del acontecimiento con la misma
	 * lógica que la fecha VAU. Si lo hay, guarda su nombre y los ids de todos
	 * los fenómenos de ese día; si no, deja todo vacío. Si los datos cósmicos
	 * no cubren la fecha, el acontecimiento se guarda igualmente sin evento.
	 */
	void rellenarEventoReseñable(AcontecimientoDestacadoEntity acontecimiento) {

		limpiarEventoReseñable(acontecimiento);
		LocalDate fecha = acontecimiento.getFecha();

		try {

			DatosCosmicosParaVAUDTO datosCosmicos = this.datosCosmicosService.getDatosCosmicos(fecha);
			if(!datosCosmicos.isValido()) {
				return;
			}

			EventoDelDiaDTO evento = this.notableEventService.getEventoDelDia(fecha, datosCosmicos);
			if(!evento.hayEvento()) {
				return;
			}

			acontecimiento.setNotableEvent(true);
			acontecimiento.setNotableEventType(evento.getNombre());
			acontecimiento.setFaseLunarId(evento.getLuna() == null ? null : evento.getLuna().getId());
			acontecimiento.setApoperiId(evento.getApoperi() == null ? null : evento.getApoperi().getId());
			acontecimiento.setSoeId(evento.getSoe() == null ? null : evento.getSoe().getId());
			acontecimiento.setMetonoId(evento.getMeton() == null ? null : evento.getMeton().getId());
			acontecimiento.setEclipseId(evento.getEclipse() == null ? null : evento.getEclipse().getId());
			acontecimiento.setEclipenoId(evento.getEclipeno() == null ? null : evento.getEclipeno().getId());

			LocalDateTime inicioDia = fecha.atStartOfDay();
			LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
			if(evento.isMidsison()) {
				this.midsisonRepository.findByDateBetween(inicioDia, finDia).stream().findFirst()
						.ifPresent(midsison -> acontecimiento.setMidsisonId(midsison.getId()));
			}
			this.casalerosRepository.findByDateBetween(inicioDia, finDia).stream().findFirst()
					.ifPresent(casalero -> acontecimiento.setCasaleroId(casalero.getId()));
		}
		catch(Exception e) {
			limpiarEventoReseñable(acontecimiento);
			System.out.println("No se ha podido calcular el evento reseñable del " + fecha + ": " + e);
		}
	}

	private static void limpiarEventoReseñable(AcontecimientoDestacadoEntity acontecimiento) {

		acontecimiento.setNotableEvent(false);
		acontecimiento.setNotableEventType(null);
		acontecimiento.setFaseLunarId(null);
		acontecimiento.setApoperiId(null);
		acontecimiento.setSoeId(null);
		acontecimiento.setMetonoId(null);
		acontecimiento.setEclipseId(null);
		acontecimiento.setEclipenoId(null);
		acontecimiento.setMidsisonId(null);
		acontecimiento.setCasaleroId(null);
	}

	private AcontecimientoDestacadoEntity buscarPropio(Long idUsuario, Long idAcontecimiento) {

		return this.acontecimientoDestacadoRepository.findByIdAndUsuarioId(idAcontecimiento, idUsuario)
				.orElseThrow(() -> new UsuarioException(HttpStatus.NOT_FOUND, UsuarioException.ACONTECIMIENTO_NO_ENCONTRADO,
						"No existe ese acontecimiento destacado."));
	}

	/** La restricción UNIQUE (usuario, fecha) cubre la carrera entre dos peticiones simultáneas. */
	private AcontecimientoDestacadoEntity guardar(AcontecimientoDestacadoEntity acontecimiento) {

		try {
			return this.acontecimientoDestacadoRepository.saveAndFlush(acontecimiento);
		}
		catch(DataIntegrityViolationException e) {
			throw duplicado(acontecimiento.getFecha());
		}
	}

	private static LocalDate validarFecha(LocalDate fecha) {

		if(fecha.isBefore(FECHA_MINIMA) || fecha.isAfter(FECHA_MAXIMA)) {
			throw new UsuarioException(HttpStatus.BAD_REQUEST, UsuarioException.FECHA_FUERA_DE_RANGO,
					"La fecha debe estar entre " + FECHA_MINIMA + " y " + FECHA_MAXIMA + ".")
					.conDetalle("minima", FECHA_MINIMA.toString())
					.conDetalle("maxima", FECHA_MAXIMA.toString());
		}
		return fecha;
	}

	private static UsuarioException duplicado(LocalDate fecha) {
		return new UsuarioException(HttpStatus.CONFLICT, UsuarioException.ACONTECIMIENTO_DUPLICADO,
				"Ya tienes un acontecimiento destacado ese día.").conDetalle("fecha", fecha.toString());
	}

	private static AcontecimientoDestacadoDTO aDTO(AcontecimientoDestacadoEntity acontecimiento) {

		AcontecimientoDestacadoDTO dto = new AcontecimientoDestacadoDTO();
		dto.setId(acontecimiento.getId());
		dto.setFecha(acontecimiento.getFecha());
		dto.setDescripcion(acontecimiento.getDescripcion());
		dto.setFechaCreacion(acontecimiento.getFechaCreacion());
		dto.setFechaModificacion(acontecimiento.getFechaModificacion());
		dto.setNotableEvent(acontecimiento.isNotableEvent());
		dto.setNotableEventType(acontecimiento.getNotableEventType());
		return dto;
	}
}
