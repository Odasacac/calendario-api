package CCASolutions.Calendario.DTOs;

import java.time.LocalDate;

/**
 * EN: The casalero of an eclipeno: the name it gets from the first phenomenon happening
 * after it. "Metónico" when that is a meton, "Eclipelar" when it is an absolute eclipse.
 * ES: El casalero de un eclípeno: el nombre que recibe del primer fenómeno que ocurre
 * después de él. "Metónico" cuando es un métono, "Eclipelar" cuando es un eclipse absoluto.
 */
public class CasaleroDTO {
	
	private LocalDate dateO;	
	private String tipo;

	private boolean nuevo;
	private boolean lleno;
	
	private boolean invernal;
	private boolean primaveral;
	private boolean estival;
	private boolean otonyal;
	
	private boolean deSol;
	private boolean deLuna;
	
	private boolean fasal;
	private boolean apoperico;
	
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
	public boolean isInvernal() {
		return invernal;
	}
	public void setInvernal(boolean invernal) {
		this.invernal = invernal;
	}
	public boolean isPrimaveral() {
		return primaveral;
	}
	public void setPrimaveral(boolean primaveral) {
		this.primaveral = primaveral;
	}
	public boolean isEstival() {
		return estival;
	}
	public void setEstival(boolean estival) {
		this.estival = estival;
	}
	public boolean isOtonyal() {
		return otonyal;
	}
	public void setOtonyal(boolean otonyal) {
		this.otonyal = otonyal;
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
	public boolean isFasal() {
		return fasal;
	}
	public void setFasal(boolean fasal) {
		this.fasal = fasal;
	}
	public boolean isApoperico() {
		return apoperico;
	}
	public void setApoperico(boolean apoperico) {
		this.apoperico = apoperico;
	}
}
