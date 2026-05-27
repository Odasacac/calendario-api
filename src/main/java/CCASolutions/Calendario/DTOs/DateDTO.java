package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private EclipenoDTO eclipenoIN;
	private MetonDTO metonoIN;
	private YearDTO year;
	private MonthDTO month;
	private String week;
	private String day;
	private NotableEventDTO notableEvent;
	private AbsoluteEclipsesDTO absoluteEclipses;
	private CasaleroDTO casalero;
	private EstadoLunaDTO estadoLuna;
	
	
	public EstadoLunaDTO getEstadoLuna() {
		return estadoLuna;
	}

	public void setEstadoLuna(EstadoLunaDTO estadoLuna) {
		this.estadoLuna = estadoLuna;
	}

	public CasaleroDTO getCasalero() {
		return casalero;
	}

	public void setCasalero(CasaleroDTO casalero) {
		this.casalero = casalero;
	}

	public AbsoluteEclipsesDTO getAbsoluteEclipses() {
		return absoluteEclipses;
	}

	public void setAbsoluteEclipses(AbsoluteEclipsesDTO absoluteEclipse) {
		this.absoluteEclipses = absoluteEclipse;
	}


	public NotableEventDTO getNotableEvent() {
		return notableEvent;
	}

	public void setNotableEvent(NotableEventDTO notableEvent) {
		this.notableEvent = notableEvent;
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
