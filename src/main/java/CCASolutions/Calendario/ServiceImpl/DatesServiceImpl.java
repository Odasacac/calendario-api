package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.DTOs.MoonPhasesDTO;
import CCASolutions.Calendario.DTOs.SolsticiosAndEquinocciosDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.MonthsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.MonthsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.Utils;

@Service
public class DatesServiceImpl implements DatesService {

	@Autowired
	private Utils utils;

	@Autowired
	private DatosRepository datosRepository;

	@Autowired
	private MonthsRepository monthsRepository;

	@Autowired
	private DaysRepository daysRepository;

	@Autowired
	private WeeksRepository weeksRepository;

	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public DateDTO getDateVAUFromDateO(LocalDateTime dateO) {

		DateDTO dateVAU = new DateDTO();

		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("SolsEquiAPI", "MoonPhaseAPI"));
		String solsEquiUrl = "", moonPhaseUrl = "";
		for (DatosEntity url : urls) {
			switch (url.getConcepto()) {
			case "SolsEquiAPI":
				solsEquiUrl = url.getValor().replace("{{XXXX}}", String.valueOf(dateO.getYear() - 1));
				break;
			case "MoonPhaseAPI":
				moonPhaseUrl = url.getValor().replace("{{XXXX}}", String.valueOf(dateO.getYear()));
				break;
			}
		}

		MoonPhasesDTO moonPhases = makeHTTPGetRequest(moonPhaseUrl, MoonPhasesDTO.class);
		SolsticiosAndEquinocciosDTO solsticios = makeHTTPGetRequest(solsEquiUrl, SolsticiosAndEquinocciosDTO.class);

		dateVAU.setYear(getVAUYear(dateO));

		dateVAU.setMonth(getVAUMonth(dateO, solsticios, moonPhases));

		dateVAU.setWeek(getVauWeek(dateO, moonPhases));

		dateVAU.setDay(getVAUDay(dateO, moonPhases));

		return dateVAU;
	}

	private boolean isNewMoon(LocalDateTime dateO, MoonPhasesDTO moonPhases) {
		for (MoonPhasesDTO.Response.Data m : moonPhases.getResponse().getData()) {
			if ("NewMoon".equals(m.getMoonPhase())
					&& LocalDateTime.parse(m.getDate()).toLocalDate().isEqual(dateO.toLocalDate())) {
				return true;
			}
		}
		return false;
	}

	private LocalDateTime getLastNewMoon(LocalDateTime dateO, MoonPhasesDTO moonPhases) {
		LocalDateTime lastNewMoon = null;
		for (MoonPhasesDTO.Response.Data m : moonPhases.getResponse().getData()) {
			if ("NewMoon".equals(m.getMoonPhase())) {
				LocalDateTime nmDate = LocalDateTime.parse(m.getDate());
				if (!nmDate.isAfter(dateO) && (lastNewMoon == null || nmDate.isAfter(lastNewMoon))) {
					lastNewMoon = nmDate;
				}
			}
		}
		return lastNewMoon;
	}

	private String getVAUDay(LocalDateTime dateO, MoonPhasesDTO moonPhases) {
		if (isNewMoon(dateO, moonPhases))
			return "-";

		LocalDateTime lastNewMoon = getLastNewMoon(dateO, moonPhases);
		if (lastNewMoon == null)
			return "?";

		long daysSinceNewMoon = ChronoUnit.DAYS.between(lastNewMoon.toLocalDate(), dateO.toLocalDate());

		int dayVAU;
		if (daysSinceNewMoon <= 7) {
			dayVAU = (int) daysSinceNewMoon;
		} else if (daysSinceNewMoon <= 14) {
			dayVAU = (int) (daysSinceNewMoon - 7);
		} else if (daysSinceNewMoon <= 21) {
			dayVAU = (int) (daysSinceNewMoon - 14);
		} else if (daysSinceNewMoon <= 28) {
			dayVAU = (int) (daysSinceNewMoon - 21);
		} else {
			dayVAU = (int) (daysSinceNewMoon - 21);
		}

		DaysEntity dayEntity = daysRepository.findByDayOfWeek(String.valueOf(dayVAU));
		return dayEntity != null ? dayEntity.getName() : "?";
	}

	private String getVauWeek(LocalDateTime dateO, MoonPhasesDTO moonPhases) {
		if (isNewMoon(dateO, moonPhases))
			return "-";

		LocalDateTime lastNewMoon = getLastNewMoon(dateO, moonPhases);
		if (lastNewMoon == null)
			return "?";

		long daysSinceNewMoon = ChronoUnit.DAYS.between(lastNewMoon.toLocalDate(), dateO.toLocalDate());

		int weekNumber;
		if (daysSinceNewMoon >= 28) {
			weekNumber = 5;
		} else {
			weekNumber = (int) ((daysSinceNewMoon - 1) / 7) + 1;
		}

		WeeksEntity weekEntity = weeksRepository.findByWeekOfMonth(String.valueOf(weekNumber));
		return weekEntity != null ? weekEntity.getName() : "?";
	}

	private String getVAUMonth(LocalDateTime dateO, SolsticiosAndEquinocciosDTO solsticios, MoonPhasesDTO moonPhases) {
		if (isNewMoon(dateO, moonPhases))
			return "-";

		LocalDateTime lastEventDate = null;
		String lastEventName = null;
		for (SolsticiosAndEquinocciosDTO.Response.Data event : solsticios.getResponse().getData()) {
			LocalDateTime eventDate = LocalDateTime.parse(event.getDate());
			if (!eventDate.isAfter(dateO) && (lastEventDate == null || eventDate.isAfter(lastEventDate))) {
				lastEventDate = eventDate;
				lastEventName = event.getPhenomena();
			}
		}

		int season = 0;
		if (lastEventName != null) {
			switch (lastEventName) {
			case "WinterSolstice":
				season = 1;
				break;
			case "VernalEquinox":
				season = 2;
				break;
			case "SummerSolstice":
				season = 3;
				break;
			case "AutumnalEquinox":
				season = 4;
				break;
			}
		}

		int newMoonCount = 0;
		for (MoonPhasesDTO.Response.Data m : moonPhases.getResponse().getData()) {
			LocalDateTime nmDate = LocalDateTime.parse(m.getDate());
			if ("NewMoon".equals(m.getMoonPhase()) && !nmDate.isBefore(lastEventDate) && !nmDate.isAfter(dateO)) {
				newMoonCount++;
			}
		}

		boolean isHibrid = newMoonCount == 0;
		int monthOfSeason = isHibrid ? 0 : newMoonCount;

		MonthsEntity vauMonth = monthsRepository.findBySeasonAndMonthOfSeasonAndHibrid(season, monthOfSeason, isHibrid);
		return vauMonth != null ? vauMonth.getName() : "?";
	}

	private <T> T makeHTTPGetRequest(String url, Class<T> responseType) {
		return restTemplate.getForObject(url, responseType);
	}

	private Long getVAUYear(LocalDateTime dateO) {
		LocalDateTime lastMeton = utils.getLastMeton(dateO);
		if (dateO.toLocalDate().isEqual(lastMeton.toLocalDate()))
			return 0L;
		else if (dateO.getYear() == lastMeton.getYear())
			return 1L;
		else
			return (long) (dateO.getYear() - lastMeton.getYear());
	}
}
