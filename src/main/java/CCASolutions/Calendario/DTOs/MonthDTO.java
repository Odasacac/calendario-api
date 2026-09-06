package CCASolutions.Calendario.DTOs;

/**
 * EN: The VAU month of a date, its qualifier (inherited from the last new moon) and whether
 * the date lands on a new moon, in which case it belongs to no month.
 * ES: El mes VAU de una fecha, su apellido (heredado de la última luna nueva) y si la fecha
 * cae en luna nueva, en cuyo caso no pertenece a ningún mes.
 */
public class MonthDTO {

	private boolean newMoon;
	private String name;
	private String surname;
	

	public boolean isNewMoon() {
		return newMoon;
	}
	public void setNewMoon(boolean newMoon) {
		this.newMoon = newMoon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	
	
}
