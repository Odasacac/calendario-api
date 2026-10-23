package CCASolutions.Calendario.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import CCASolutions.Calendario.Repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CodigoUsuarioServiceImplTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@InjectMocks
	private CodigoUsuarioServiceImpl servicio;

	private void ocupados(String... codigos) {
		Set<String> set = Set.of(codigos);
		when(this.usuarioRepository.existsByCodigoUsuario(anyString())).thenAnswer(i -> set.contains(i.getArgument(0)));
	}

	@Test
	void usaInicialMasApellidoSiEstaLibre() {
		this.ocupados();
		assertEquals("ccasado", this.servicio.generar("Carlos", "Casado", 1993, null));
	}

	@Test
	void anyadeElAnyoDeNacimientoSiYaExiste() {
		this.ocupados("ccasado");
		assertEquals("ccasado1993", this.servicio.generar("Carlos", "Casado", 1993, null));
	}

	@Test
	void anyadeSufijoNumericoTrasElAnyo() {
		this.ocupados("ccasado", "ccasado1993", "ccasado1993_1");
		assertEquals("ccasado1993_2", this.servicio.generar("Carlos", "Casado", 1993, null));
	}

	@Test
	void quitaTildesEspaciosYMayusculas() {
		this.ocupados();
		assertEquals("anunezdelafuente", this.servicio.generar("Álvaro", "Núñez de la Fuente", 2000, null));
	}

	@Test
	void usaBasePorDefectoSiNoQuedanLetras() {
		this.ocupados();
		assertEquals("usuario", this.servicio.generar("李", "王", 1990, null));
	}

	@Test
	void alEditarNoCuentaElCodigoDelPropioUsuario() {
		when(this.usuarioRepository.existsByCodigoUsuarioAndIdNot(eq("ccasado"), eq(7L))).thenReturn(false);
		assertEquals("ccasado", this.servicio.generar("Carlos", "Casado", 1993, 7L));
	}
}
