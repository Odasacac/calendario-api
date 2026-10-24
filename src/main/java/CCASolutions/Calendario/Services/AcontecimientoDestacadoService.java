package CCASolutions.Calendario.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import CCASolutions.Calendario.DTOs.FormularioAcontecimientoDTO;
import CCASolutions.Calendario.DTOs.AcontecimientoDestacadoDTO;
import CCASolutions.Calendario.DTOs.PaginaDTO;

/**
 * Acontecimientos destacados que cada usuario apunta en el calendario (uno por día
 * como máximo). Todas las operaciones se limitan a los acontecimientos del propio
 * usuario: un id ajeno se trata igual que uno inexistente.
 */
public interface AcontecimientoDestacadoService {

	public static final LocalDate FECHA_MINIMA = LocalDate.of(1, 1, 1);
	public static final LocalDate FECHA_MAXIMA = LocalDate.of(2099, 12, 31);

	public static final int TAMANYO_MINIMO = 5;
	public static final int TAMANYO_MAXIMO = 50;

	/**
	 * Una página de acontecimientos del usuario, del más reciente al más antiguo.
	 * El tamaño se ajusta a 5..50; una página por encima de la última devuelve la
	 * última (pasa al borrar el único elemento de la última página).
	 */
	public abstract PaginaDTO<AcontecimientoDestacadoDTO> listar(Long idUsuario, int pagina, int tamanyo);

	/** Todas las fechas con acontecimiento, para marcarlas en los calendarios. */
	public abstract List<LocalDate> fechas(Long idUsuario);

	public abstract Optional<AcontecimientoDestacadoDTO> buscarPorFecha(Long idUsuario, LocalDate fecha);

	public abstract AcontecimientoDestacadoDTO crear(Long idUsuario, FormularioAcontecimientoDTO formulario);

	public abstract AcontecimientoDestacadoDTO modificar(Long idUsuario, Long idAcontecimiento, FormularioAcontecimientoDTO formulario);

	/** Borrado físico: el registro desaparece de la base de datos. */
	public abstract void eliminar(Long idUsuario, Long idAcontecimiento);
}
