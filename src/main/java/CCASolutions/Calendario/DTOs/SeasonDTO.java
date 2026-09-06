package CCASolutions.Calendario.DTOs;

/**
 * EN: The VAU season of a date and which third of it the date falls in: before the midsison
 * ("iniciante"), on it ("cenítico") or after it ("terminante").
 * ES: La estación VAU de una fecha y en qué tercio de ella cae: antes del midsison
 * ("iniciante"), en él ("cenítico") o después ("terminante").
 */
public class SeasonDTO {

	private String name;
	private String surname;
	private boolean midsisonDay;
	private boolean preMidsison;
	private boolean postMidsison;
	
	public String getName() {
		return name;
	}
	public void setName(String season) {
		this.name = season;
	}
	public boolean isMidsisonDay() {
		return midsisonDay;
	}
	public void setMidsisonDay(boolean midsisonDay) {
		this.midsisonDay = midsisonDay;
	}
	public boolean isPreMidsison() {
		return preMidsison;
	}
	public void setPreMidsison(boolean preMidsison) {
		this.preMidsison = preMidsison;
	}
	public boolean isPostMidsison() {
		return postMidsison;
	}
	public void setPostMidsison(boolean postMidsison) {
		this.postMidsison = postMidsison;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}	
	
}
