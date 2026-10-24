package CCASolutions.Calendario.DTOs;

import java.util.List;

/** Una página de resultados. pagina empieza en 0. */
public class PaginaDTO<T> {

	private List<T> contenido;
	private int pagina;
	private int tamanyo;
	private long totalElementos;
	private int totalPaginas;

	public List<T> getContenido() {
		return contenido;
	}
	public void setContenido(List<T> contenido) {
		this.contenido = contenido;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public int getTamanyo() {
		return tamanyo;
	}
	public void setTamanyo(int tamanyo) {
		this.tamanyo = tamanyo;
	}
	public long getTotalElementos() {
		return totalElementos;
	}
	public void setTotalElementos(long totalElementos) {
		this.totalElementos = totalElementos;
	}
	public int getTotalPaginas() {
		return totalPaginas;
	}
	public void setTotalPaginas(int totalPaginas) {
		this.totalPaginas = totalPaginas;
	}
}
