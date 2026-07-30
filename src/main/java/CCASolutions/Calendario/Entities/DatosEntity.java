package CCASolutions.Calendario.Entities;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/*
 * EN: Configuration rows (external API URLs and the administrator password hash),
 *     always read by their "concepto" key.
 * ES: Filas de configuracion (URLs de la API externa y el hash de la contrasena de
 *     administrador), siempre se leen por su clave "concepto".
 */
@Entity
@Table(name="datos", indexes = {
		@Index(name="idx_datos_concepto", columnList="concepto")
})
public class DatosEntity implements Serializable{

	private static final long serialVersionUID = 1810326363593043815L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String concepto;
	private String valor;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
