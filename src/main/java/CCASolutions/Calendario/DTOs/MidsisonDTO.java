package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class MidsisonDTO {

		private LocalDateTime date;
		private int lastSoeSeason;
		private boolean nuevo;
		private boolean lleno;
		private boolean selecto;
		private boolean invertido;
		private boolean eclipse;
		private boolean apofasal;
		private boolean aporico;
		private boolean perico;
		
		
		public boolean isAporico() {
			return aporico;
		}
		public void setAporico(boolean aporico) {
			this.aporico = aporico;
		}
		public boolean isPerico() {
			return perico;
		}
		public void setPerico(boolean perico) {
			this.perico = perico;
		}
		public boolean isApofasal() {
			return apofasal;
		}
		public void setApofasal(boolean apofasal) {
			this.apofasal = apofasal;
		}
		public boolean isEclipse() {
			return eclipse;
		}
		public void setEclipse(boolean eclipse) {
			this.eclipse = eclipse;
		}
		public boolean isNuevo() {
			return nuevo;
		}
		public void setNuevo(boolean nuevo) {
			this.nuevo = nuevo;
		}
		public boolean isLleno() {
			return lleno;
		}
		public void setLleno(boolean lleno) {
			this.lleno = lleno;
		}
		public boolean isSelecto() {
			return selecto;
		}
		public void setSelecto(boolean selecto) {
			this.selecto = selecto;
		}
		public boolean isInvertido() {
			return invertido;
		}
		public void setInvertido(boolean invertido) {
			this.invertido = invertido;
		}
		public LocalDateTime getDate() {
			return date;
		}
		public void setDate(LocalDateTime date) {
			this.date = date;
		}
		public int getLastSoeSeason() {
			return lastSoeSeason;
		}
		public void setLastSoeSeason(int lastSoeSeason) {
			this.lastSoeSeason = lastSoeSeason;
		}			
}
