package CCASolutions.Calendario.Services;

import CCASolutions.Calendario.DTOs.CalendarByYearDTO;

public interface DownloadService {
	
	public abstract byte[] getPDF();
	
	public abstract byte[] getCalendarForAYear(CalendarByYearDTO yearForCalendar);

}
