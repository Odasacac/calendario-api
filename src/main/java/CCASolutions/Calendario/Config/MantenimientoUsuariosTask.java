package CCASolutions.Calendario.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import CCASolutions.Calendario.Services.UsuarioService;

/** Cron diario que actualiza el estado de las cuentas (ver UsuarioService#mantenimientoDiario). */
@Configuration
@EnableScheduling
public class MantenimientoUsuariosTask {

	@Autowired
	private UsuarioService usuarioService;

	@Scheduled(cron = "${vau.usuarios.cron}")
	void revisarUsuarios() {

		try {
			int modificados = this.usuarioService.mantenimientoDiario();
			System.out.println("Mantenimiento de usuarios: " + modificados + " cuenta(s) actualizada(s).");
		}
		catch(Exception e) {
			System.out.println("Error en el mantenimiento de usuarios: " + e);
		}
	}
}
