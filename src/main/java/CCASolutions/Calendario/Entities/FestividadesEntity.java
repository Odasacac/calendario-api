package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

	/*

 		Una festividades es un Evento Reseñable en concreto, a la combinacion de dos o mas
 		
 		Festividades de un año en orden cronológico
 		
 			1 - Cambio de métono: El día del métono inicial nuevo - CM
 			2 - Cambio de año: El día del solsticio de invierno - CA
 			3 - Inicio del primer mes del año: El día de la primera luna nueva de Prierno - IPMA
 			4 - Bienvenida a la primavera: Equinoccio de primavera - BP
 			5 - Mitad de año: El día del solsticio de verano - MA
 			6 - Paso al otoño: El día de la última luna llena antes del equinoccio de otoño - PO
 			7 - Despedida del año: El día de la última luna llena antes del solsticio de invierno - DA
 
 			CAIAP BHMAPODA
 			
	 */
@Entity
@Table(name="festividades")
public class FestividadesEntity implements Serializable {

	private static final long serialVersionUID = -4528683727937150880L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String code;
	private String nombre;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
