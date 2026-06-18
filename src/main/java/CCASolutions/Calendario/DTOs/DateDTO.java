package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private EclipenoSelectoDTO lastEclipenoSelecto;
	private EclipenoINDTO eclipenoIN;
	private MetonDTO metonoIN;
	private MetonosApopericosDTO metonoApoperico;
	private YearDTO year;
	private MonthDTO month;
	private String week;
	private String day;
	private NotableEventDTO notableEvent;
	private AbsoluteEclipsesDTO absoluteEclipses;
	private CasaleroDTO casalero;
	private EstadoLunaDTO estadoLuna;
	private FestividadesDTO festividades;
	
	
	
	public MetonosApopericosDTO getMetonoApoperico() {
		return metonoApoperico;
	}

	public void setMetonoApoperico(MetonosApopericosDTO metonoApoperico) {
		this.metonoApoperico = metonoApoperico;
	}

	public EclipenoSelectoDTO getLastEclipenoSelecto() {
		return lastEclipenoSelecto;
	}

	public void setLastEclipenoSelecto(EclipenoSelectoDTO lastEclipenoSelecto) {
		this.lastEclipenoSelecto = lastEclipenoSelecto;
	}

	public FestividadesDTO getFestividades() {
		return festividades;
	}

	public void setFestividades(FestividadesDTO festividades) {
		this.festividades = festividades;
	}

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
	public EclipenoINDTO getEclipenoIN() {
		return eclipenoIN;
	}

	public void setEclipenoIN(EclipenoINDTO eclipenoIN) {
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
