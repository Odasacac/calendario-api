package CCASolutions.Calendario.Services;

public interface DownloadService {
	
	public abstract byte[] getManual();
	
	public abstract byte[] getCalendarForAYear(int yearForCalendar);

}
