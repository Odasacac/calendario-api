package CCASolutions.Calendario.DTOs;

import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;

public class DateWithEntitiesDTO {
	
	private MetonsEntity meton;
	private MonthsEntity month;
	private WeeksEntity week;
	private DaysEntity day;
	private boolean validated;
	
	
	
	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	public MetonsEntity getMeton() {
		return meton;
	}
	public void setMeton(MetonsEntity meton) {
		this.meton = meton;
	}
	public MonthsEntity getMonth() {
		return month;
	}
	public void setMonth(MonthsEntity month) {
		this.month = month;
	}
	public WeeksEntity getWeek() {
		return week;
	}
	public void setWeek(WeeksEntity week) {
		this.week = week;
	}
	public DaysEntity getDay() {
		return day;
	}
	public void setDay(DaysEntity day) {
		this.day = day;
	}

}
