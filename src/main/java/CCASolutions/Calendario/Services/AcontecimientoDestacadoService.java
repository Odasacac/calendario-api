package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import CCASolutions.Calendario.DTOs.FormularioAcontecimientoDTO;
import CCASolutions.Calendario.DTOs.AcontecimientoDestacadoDTO;

/**
 * Acontecimientos destacados que cada usuario apunta en el calendario (uno por día
 * como máximo). Todas las operaciones se limitan a los acontecimientos del propio
 * usuario: un id ajeno se trata igual que uno inexistente.
 */
public interface AcontecimientoDestacadoService {

	public static final LocalDate FECHA_MINIMA = LocalDate.of(1, 1, 1);
	public static final LocalDate FECHA_MAXIMA = LocalDate.of(2099, 12, 31);

	/** Acontecimientos del usuario, del más reciente al más antiguo. */
	public abstract List<AcontecimientoDestacadoDTO> listar(Long idUsuario);

	public abstract Optional<AcontecimientoDestacadoDTO> buscarPorFecha(Long idUsuario, LocalDate fecha);

	public abstract AcontecimientoDestacadoDTO crear(Long idUsuario, FormularioAcontecimientoDTO formulario);

	public abstract AcontecimientoDestacadoDTO modificar(Long idUsuario, Long idAcontecimiento, FormularioAcontecimientoDTO formulario);

	/** Borrado físico: el registro desaparece de la base de datos. */
	public abstract void eliminar(Long idUsuario, Long idAcontecimiento);
}
