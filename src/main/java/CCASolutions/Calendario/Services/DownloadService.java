package CCASolutions.Calendario.Services;

public interface DownloadService {
	
	public abstract byte[] getManual();

	public abstract byte[] getManualDespliegue();

	public abstract byte[] getCalendarForAYear(int yearForCalendar);

}
