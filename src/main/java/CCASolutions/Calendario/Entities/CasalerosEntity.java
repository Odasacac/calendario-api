package CCASolutions.Calendario.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="casaleros")
public class CasalerosEntity implements Serializable {
	
	/*
	 
	 ¿Qué fenómeno ocurrirá primero después de un eclípeno inicial nuevo?
	 
	 	¿Un métono o un eclipse absoluto? Eso es un Casalero
	 	
	 	Eclipenos iniciales nuevos:
	 	
	 		1889
	 		1870
	 		1862
	 		1786
	 		1517
	 		1498
			1479
			
			
			
			
	 */
	
	private static final long serialVersionUID = -8157953109064013194L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)	
	private Long id;
	
	private LocalDateTime date;
	private int year;
	private LocalDateTime eclipenDate;
	private int eclipenYear;
	private Long eclipenoId;
	private Long metonoId;
	private Long eclipseId;
	private boolean eclipenoInicialNuevo;
	
	private boolean metonico;
	private boolean metonicoNuevo;
	private boolean metonicoLleno;
	private boolean metonicoInicial;
	private boolean metonicoBicuartal;
	private boolean metonicoCuartal;
	private boolean metonicoTricuartal;
	
	
	private boolean eclipelar;
	private boolean eclipelarDeSol;
	private boolean eclipelarDeLuna;
	
	
	
	
	public boolean isMetonicoCuartal() {
		return metonicoCuartal;
	}
	public void setMetonicoCuartal(boolean metonicoCuartal) {
		this.metonicoCuartal = metonicoCuartal;
	}
	public boolean isMetonicoTricuartal() {
		return metonicoTricuartal;
	}
	public void setMetonicoTricuartal(boolean metonicoTricuartal) {
		this.metonicoTricuartal = metonicoTricuartal;
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
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public LocalDateTime getEclipenDate() {
		return eclipenDate;
	}
	public void setEclipenDate(LocalDateTime eclipenDate) {
		this.eclipenDate = eclipenDate;
	}
	public int getEclipenYear() {
		return eclipenYear;
	}
	public void setEclipenYear(int eclipenYear) {
		this.eclipenYear = eclipenYear;
	}
	public Long getEclipenoId() {
		return eclipenoId;
	}
	public void setEclipenoId(Long eclipenoId) {
		this.eclipenoId = eclipenoId;
	}
	public Long getMetonoId() {
		return metonoId;
	}
	public void setMetonoId(Long metonoId) {
		this.metonoId = metonoId;
	}
	public Long getEclipseId() {
		return eclipseId;
	}
	public void setEclipseId(Long eclipseId) {
		this.eclipseId = eclipseId;
	}
	public boolean isEclipenoInicialNuevo() {
		return eclipenoInicialNuevo;
	}
	public void setEclipenoInicialNuevo(boolean eclipenoInicialNuevo) {
		this.eclipenoInicialNuevo = eclipenoInicialNuevo;
	}
	public boolean isMetonico() {
		return metonico;
	}
	public void setMetonico(boolean metonico) {
		this.metonico = metonico;
	}
	public boolean isMetonicoNuevo() {
		return metonicoNuevo;
	}
	public void setMetonicoNuevo(boolean metonicoNuevo) {
		this.metonicoNuevo = metonicoNuevo;
	}
	public boolean isMetonicoLleno() {
		return metonicoLleno;
	}
	public void setMetonicoLleno(boolean metonicoLleno) {
		this.metonicoLleno = metonicoLleno;
	}
	public boolean isMetonicoInicial() {
		return metonicoInicial;
	}
	public void setMetonicoInicial(boolean metonicoInicial) {
		this.metonicoInicial = metonicoInicial;
	}
	public boolean isMetonicoBicuartal() {
		return metonicoBicuartal;
	}
	public void setMetonicoBicuartal(boolean metonicoBicuartal) {
		this.metonicoBicuartal = metonicoBicuartal;
	}
	public boolean isEclipelar() {
		return eclipelar;
	}
	public void setEclipelar(boolean eclipelar) {
		this.eclipelar = eclipelar;
	}
	public boolean isEclipelarDeSol() {
		return eclipelarDeSol;
	}
	public void setEclipelarDeSol(boolean eclipelarDeSol) {
		this.eclipelarDeSol = eclipelarDeSol;
	}
	public boolean isEclipelarDeLuna() {
		return eclipelarDeLuna;
	}
	public void setEclipelarDeLuna(boolean eclipelarDeLuna) {
		this.eclipelarDeLuna = eclipelarDeLuna;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
