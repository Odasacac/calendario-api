package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private String eclipeno;
	private MetonDTO meton;
	private String year;
	private MonthDTO month;
	private String week;
	private String day;

	
	
	public String getEclipeno() {
		return eclipeno;
	}

	public void setEclipeno(String eclipeno) {
		this.eclipeno = eclipeno;
	}

	public MetonDTO getMeton() {
		return meton;
	}

	public void setMeton(MetonDTO meton) {
		this.meton = meton;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public MonthDTO getMonth() {
		return month;
	}

	public void setMonth(MonthDTO month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

}
