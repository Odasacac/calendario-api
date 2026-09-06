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
 		
 			1 - Cambio de eclípeno: El día del eclípeno inicial nuevo - CE
 			2 - Cambio de métono: El día del métono inicial nuevo - CM
 			3 - Cambio de año: El día del solsticio de invierno - CA
 			4 - Inicio del primer mes del año: El día de la primera luna nueva despues del solsticio de invierno - IA
 			5 - Bienvenida a la primavera: El día del equinoccio de primavera - BP
 			6 - Mitad de año: El día del solsticio de verano - MA
 			7 - Despedida del verano: El dia de la ultima luna llena antes del equinoccio de otoño - DV
 			8 - Entrada del otoño: El día del equinoccio de otoño - PO
 			9 - Despedida del año: El día de la última luna llena antes del solsticio de invierno - DA
 
 			
	 */
/**
 * EN: One VAU festivity, table {@code festividades}: sixteen fixed rows, each identified by
 * a short code and describing the astronomical phenomenon behind it.
 * ES: Una festividad VAU, tabla {@code festividades}: dieciséis filas fijas, cada una
 * identificada por un código corto y que describe el fenómeno astronómico que hay detrás.
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
	private boolean lunar;
	private String descripcion;
	
	
	
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public boolean isLunar() {
		return lunar;
	}
	public void setLunar(boolean lunar) {
		this.lunar = lunar;
	}
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
