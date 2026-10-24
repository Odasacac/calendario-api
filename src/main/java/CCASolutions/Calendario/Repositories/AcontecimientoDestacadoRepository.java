package CCASolutions.Calendario.Repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CCASolutions.Calendario.Entities.AcontecimientoDestacadoEntity;

public interface AcontecimientoDestacadoRepository extends JpaRepository <AcontecimientoDestacadoEntity, Long> {
	Page<AcontecimientoDestacadoEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pagina);
	Optional<AcontecimientoDestacadoEntity> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
	Optional<AcontecimientoDestacadoEntity> findByIdAndUsuarioId(Long id, Long usuarioId);
	boolean existsByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
	boolean existsByUsuarioIdAndFechaAndIdNot(Long usuarioId, LocalDate fecha, Long id);

	/** Solo las fechas: lo justo para marcar los días en los calendarios. */
	@Query("SELECT a.fecha FROM AcontecimientoDestacadoEntity a WHERE a.usuario.id = :usuarioId ORDER BY a.fecha DESC")
	List<LocalDate> findFechasByUsuarioId(@Param("usuarioId") Long usuarioId);
}
