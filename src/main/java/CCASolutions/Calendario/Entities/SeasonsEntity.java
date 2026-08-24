package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="seasons")
public class SeasonsEntity implements Serializable {

		private static final long serialVersionUID = -3476625787442393891L;
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		private String name;		
		private int seasonOfTheYear;
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getSeasonOfTheYear() {
			return seasonOfTheYear;
		}
		public void setSeasonOfTheYear(int seasonOfTheYear) {
			this.seasonOfTheYear = seasonOfTheYear;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}				
}
