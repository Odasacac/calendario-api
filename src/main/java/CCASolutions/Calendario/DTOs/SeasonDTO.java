package CCASolutions.Calendario.DTOs;

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
