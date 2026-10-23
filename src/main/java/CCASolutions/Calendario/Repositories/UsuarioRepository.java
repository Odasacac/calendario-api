package CCASolutions.Calendario.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.UsuarioEntity;
import CCASolutions.Calendario.Enums.EstadoUsuarioEnum;

public interface UsuarioRepository extends JpaRepository <UsuarioEntity, Long> {
	Optional<UsuarioEntity> findByCorreo(String correo);
	Optional<UsuarioEntity> findByCodigoUsuario(String codigoUsuario);
	boolean existsByCorreo(String correo);
	boolean existsByCodigoUsuario(String codigoUsuario);
	boolean existsByCodigoUsuarioAndIdNot(String codigoUsuario, Long id);
	List<UsuarioEntity> findByEstadoNot(EstadoUsuarioEnum estado);
}
