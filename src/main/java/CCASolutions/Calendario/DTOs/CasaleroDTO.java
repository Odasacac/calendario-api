package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

public class CasaleroDTO {
	
	private LocalDate dateO;	
	private String tipo;

	private boolean nuevo;
	private boolean lleno;
	private boolean inicial;
	private boolean bicuartal;
	private boolean cuartal;
	private boolean tricuartal;
	
	
	private boolean deSol;
	private boolean deLuna;
	
	
	

	public boolean isCuartal() {
		return cuartal;
	}
	public void setCuartal(boolean cuartal) {
		this.cuartal = cuartal;
	}
	public boolean isTricuartal() {
		return tricuartal;
	}
	public void setTricuartal(boolean tricuartal) {
		this.tricuartal = tricuartal;
	}
	public LocalDate getDateO() {
		return dateO;
	}
	public void setDateO(LocalDate dateO) {
		this.dateO = dateO;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	public boolean isInicial() {
		return inicial;
	}
	public void setInicial(boolean inicial) {
		this.inicial = inicial;
	}
	public boolean isBicuartal() {
		return bicuartal;
	}
	public void setBicuartal(boolean bicuartal) {
		this.bicuartal = bicuartal;
	}
	public boolean isDeSol() {
		return deSol;
	}
	public void setDeSol(boolean deSol) {
		this.deSol = deSol;
	}
	public boolean isDeLuna() {
		return deLuna;
	}
	public void setDeLuna(boolean deLuna) {
		this.deLuna = deLuna;
	}	
}
