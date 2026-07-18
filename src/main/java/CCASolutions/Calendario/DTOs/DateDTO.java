package CCASolutions.Calendario.DTOs;

public class DateDTO {

	private EclipenoSelectoDTO lastEclipenoSelecto;
	private MetonoInvernalApofasalRemotoDTO metonoInvernalApofasalRemoto;
	private EclipenoINDTO eclipenoVAU;
	private MetonDTO metonoVAU;
	private YearDTO year;
	private MonthDTO month;
	private String week;
	private String day;
	private NotableEventDTO notableEvent;
	private AbsoluteEclipsesDTO absoluteEclipses;
	private CasaleroDTO casalero;
	private EstadoLunaDTO estadoLuna;
	private FestividadesDTO festividades;
	private String mensaje;
	private boolean fechaEncontrada;
	private String fechaO;
	
	

	public String getFechaO() {
		return fechaO;
	}

	public void setFechaO(String fechaO) {
		this.fechaO = fechaO;
	}

	public boolean isFechaEncontrada() {
		return fechaEncontrada;
	}

	public void setFechaEncontrada(boolean fechaEncontrada) {
		this.fechaEncontrada = fechaEncontrada;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public MetonoInvernalApofasalRemotoDTO getMetonoInvernalApofasalRemoto() {
		return metonoInvernalApofasalRemoto;
	}

	public void setMetonoInvernalApofasalRemoto(MetonoInvernalApofasalRemotoDTO metonoInvernalApofasalRemoto) {
		this.metonoInvernalApofasalRemoto = metonoInvernalApofasalRemoto;
	}

	public EclipenoINDTO getEclipenoVAU() {
		return eclipenoVAU;
	}

	public void setEclipenoVAU(EclipenoINDTO eclipenoVAU) {
		this.eclipenoVAU = eclipenoVAU;
	}

	public MetonDTO getMetonoVAU() {
		return metonoVAU;
	}

	public void setMetonoVAU(MetonDTO metonoVAU) {
		this.metonoVAU = metonoVAU;
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
