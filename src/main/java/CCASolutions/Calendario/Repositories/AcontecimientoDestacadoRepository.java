package CCASolutions.Calendario.Repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.AcontecimientoDestacadoEntity;

public interface AcontecimientoDestacadoRepository extends JpaRepository <AcontecimientoDestacadoEntity, Long> {
	List<AcontecimientoDestacadoEntity> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
	Optional<AcontecimientoDestacadoEntity> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
	Optional<AcontecimientoDestacadoEntity> findByIdAndUsuarioId(Long id, Long usuarioId);
	boolean existsByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
	boolean existsByUsuarioIdAndFechaAndIdNot(Long usuarioId, LocalDate fecha, Long id);
}
