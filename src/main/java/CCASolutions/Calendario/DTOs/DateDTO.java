package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private EclipenoDTO eclipeno;
	private MetonDTO meton;
	private YearDTO year;
	private MonthDTO month;
	private String week;
	private String day;
	private String eventoReseñable;
	private SolilunioDTO solilunio;
	
	
	
	public SolilunioDTO getSolilunio() {
		return solilunio;
	}

	public void setSolilunio(SolilunioDTO solilunio) {
		this.solilunio = solilunio;
	}

	public String getEventoReseñable() {
		return eventoReseñable;
	}

	public void setEventoReseñable(String eventoReseñable) {
		this.eventoReseñable = eventoReseñable;
	}

	public EclipenoDTO getEclipeno() {
		return eclipeno;
	}

	public void setEclipeno(EclipenoDTO eclipeno) {
		this.eclipeno = eclipeno;
	}

	public MetonDTO getMeton() {
		return meton;
	}

	public void setMeton(MetonDTO meton) {
		this.meton = meton;
	}

	public YearDTO getYear() {
		return year;
	}

	public void setYear(YearDTO year) {
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
