package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;

public interface DownloadService {
	
	public abstract byte[] getManual();
	
	public abstract byte[] getCalendarForAYear(CalendarByYearDTO yearForCalendar);

}
