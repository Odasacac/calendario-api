package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private EclipenoDTO eclipenoIN;
	private MetonDTO metonoIN;
	private YearDTO year;
	private MonthDTO month;
	private String week;
	private String day;
	private String eventoReseñable;
	private SoliluniosDTO solilunios;
	
	
	
	public SoliluniosDTO getSolilunios() {
		return solilunios;
	}

	public void setSolilunios(SoliluniosDTO solilunio) {
		this.solilunios = solilunio;
	}

	public String getEventoReseñable() {
		return eventoReseñable;
	}

	public void setEventoReseñable(String eventoReseñable) {
		this.eventoReseñable = eventoReseñable;
	}	
	
	public EclipenoDTO getEclipenoIN() {
		return eclipenoIN;
	}

	public void setEclipenoIN(EclipenoDTO eclipenoIN) {
		this.eclipenoIN = eclipenoIN;
	}

	public MetonDTO getMetonoIN() {
		return metonoIN;
	}

	public void setMetonoIN(MetonDTO metonoIN) {
		this.metonoIN = metonoIN;
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
