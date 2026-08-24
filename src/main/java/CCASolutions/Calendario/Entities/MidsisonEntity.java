package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="midsison")
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
