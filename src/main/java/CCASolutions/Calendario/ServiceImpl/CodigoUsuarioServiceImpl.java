package CCASolutions.Calendario.ServiceImpl;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Repositories.UsuarioRepository;
import CCASolutions.Calendario.Services.CodigoUsuarioService;

@Service
public class CodigoUsuarioServiceImpl implements CodigoUsuarioService {

	private static final String BASE_POR_DEFECTO = "usuario";

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public String generar(String nombre, String apellido, int anyoNacimiento, Long idExcluido) {

		String nombreLimpio = this.limpiar(nombre);
		String base = (nombreLimpio.isEmpty() ? "" : nombreLimpio.substring(0, 1)) + this.limpiar(apellido);
		if(base.isEmpty()) {
			base = BASE_POR_DEFECTO;
		}

		if(this.estaLibre(base, idExcluido)) {
			return base;
		}

		String conAnyo = base + anyoNacimiento;
		if(this.estaLibre(conAnyo, idExcluido)) {
			return conAnyo;
		}

		int sufijo = 1;
		while(!this.estaLibre(conAnyo + "_" + sufijo, idExcluido)) {
			sufijo++;
		}

		return conAnyo + "_" + sufijo;
	}

	@Override
	public String limpiar(String texto) {

		if(texto == null) {
			return "";
		}

		// NFD separa la letra de su tilde (ñ -> n + ~), y \p{M} quita las marcas.
		return Normalizer.normalize(texto, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]", "");
	}

	private boolean estaLibre(String codigo, Long idExcluido) {

		return idExcluido == null
				? !this.usuarioRepository.existsByCodigoUsuario(codigo)
				: !this.usuarioRepository.existsByCodigoUsuarioAndIdNot(codigo, idExcluido);
	}
}
