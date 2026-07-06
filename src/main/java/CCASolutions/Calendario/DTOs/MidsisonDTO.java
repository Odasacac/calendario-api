package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class MidsisonDTO {

		private LocalDateTime date;
		private int lastSoeSeason;
		
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
