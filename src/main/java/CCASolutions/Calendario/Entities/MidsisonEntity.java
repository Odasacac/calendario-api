package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * EN: One midsison, table {@code midsison}: the instant exactly halfway between one solstice
 * or equinox and the next. It points to both, and marks whichever moon phase, apogee,
 * perigee or eclipse falls within one sidereal day of it.
 * ES: Un midsison, tabla {@code midsison}: el instante exactamente equidistante entre un
 * solsticio o equinoccio y el siguiente. Apunta a ambos, y marca la fase lunar, el apogeo,
 * el perigeo o el eclipse que caiga dentro de un día sideral.
 */
@Entity
@Table(name="midsison", indexes = @Index(name = "idx_midsison_date", columnList = "date"))
public class MidsisonEntity implements Serializable {

		private static final long serialVersionUID = -3474625787442393891L;
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		private LocalDateTime date;
		private Long pastSOEId;
		private Long nextSOEId;
		
		private boolean lastSoeInvernal;
		private boolean lastSoePrimaveral;
		private boolean lastSoeEstival;
		private boolean lastSoeOtonyal;
		
		private boolean nuevo;
		private boolean lleno;
		private Long lunaId;
		private boolean selecto;
		private boolean invertido;
		private boolean apofasal;
		private Long apoperiId;
		private boolean aporico;
		private boolean perico;
		
		private boolean eclipse;
		private Long eclipseId;
				
		
		
		public Long getApoperiId() {
			return apoperiId;
		}
		public void setApoperiId(Long apoperiId) {
			this.apoperiId = apoperiId;
		}
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
		public Long getEclipseId() {
			return eclipseId;
		}
		public void setEclipseId(Long eclipseId) {
			this.eclipseId = eclipseId;
		}
		public boolean isEclipse() {
			return eclipse;
		}
		public void setEclipse(boolean eclipse) {
			this.eclipse = eclipse;
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
		public Long getLunaId() {
			return lunaId;
		}
		public void setLunaId(Long lunaId) {
			this.lunaId = lunaId;
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
		public boolean isLastSoeInvernal() {
			return lastSoeInvernal;
		}
		public void setLastSoeInvernal(boolean lastSoeInvernal) {
			this.lastSoeInvernal = lastSoeInvernal;
		}
		public boolean isLastSoePrimaveral() {
			return lastSoePrimaveral;
		}
		public void setLastSoePrimaveral(boolean lastSoePrimaveral) {
			this.lastSoePrimaveral = lastSoePrimaveral;
		}
		public boolean isLastSoeEstival() {
			return lastSoeEstival;
		}
		public void setLastSoeEstival(boolean lastSoeEstival) {
			this.lastSoeEstival = lastSoeEstival;
		}
		public boolean isLastSoeOtonyal() {
			return lastSoeOtonyal;
		}
		public void setLastSoeOtonyal(boolean lastSoeOtonyal) {
			this.lastSoeOtonyal = lastSoeOtonyal;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public LocalDateTime getDate() {
			return date;
		}
		public void setDate(LocalDateTime date) {
			this.date = date;
		}
	
		public Long getPastSOEId() {
			return pastSOEId;
		}
		public void setPastSOEId(Long pastSOEId) {
			this.pastSOEId = pastSOEId;
		}
		public Long getNextSOEId() {
			return nextSOEId;
		}
		public void setNextSOEId(Long nextSOEId) {
			this.nextSOEId = nextSOEId;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
		

}
