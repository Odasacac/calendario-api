package CCASolutions.Calendario.DTOs;


public class LunarEclipseDTO {

	  private String date;
	    private String type;


	    public LunarEclipseDTO(String date, String type) {
	        this.date = date;
	        this.type = type;
	    }

	    public String getDate() {
	        return date;
	    }

	    public void setDate(String date) {
	        this.date = date;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    } 

}