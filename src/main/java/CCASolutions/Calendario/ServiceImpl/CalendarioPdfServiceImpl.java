package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Entities.DaysEntity;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.WeeksEntity;
import CCASolutions.Calendario.Others.CalendarioAnualPdf;
import CCASolutions.Calendario.Repositories.DaysRepository;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Repositories.WeeksRepository;
import CCASolutions.Calendario.Services.CalendarioPdfService;

@Service
public class CalendarioPdfServiceImpl implements CalendarioPdfService {

	@Autowired
	private SeasonsRepository seasonsRepository;

	@Autowired
	private WeeksRepository weeksRepository;

	@Autowired
	private DaysRepository daysRepository;

	public byte[] generarCalendarioAnual(int anyoCivil, LocalDate solsticioInicioAnyo, LocalDate ultimoEclipenoInvernalApofasalRemoto, List<DateDTO> dias) {

		return new CalendarioAnualPdf(anyoCivil, solsticioInicioAnyo, ultimoEclipenoInvernalApofasalRemoto, dias,
				this.getEstacionesPorNumero(), this.getSemanasPorNombre(), this.getDiasPorNombre()).generar();
	}

	private Map<Integer, String> getEstacionesPorNumero() {

		Map<Integer, String> estacionesPorNumero = new HashMap<>();

		for(SeasonsEntity estacion : this.seasonsRepository.findAll()) {
			estacionesPorNumero.put(estacion.getSeasonOfTheYear(), estacion.getName());
		}

		return estacionesPorNumero;
	}

	private Map<String, Integer> getSemanasPorNombre() {

		Map<String, Integer> semanasPorNombre = new HashMap<>();

		for(WeeksEntity semana : this.weeksRepository.findAll()) {
			semanasPorNombre.put(semana.getName(), semana.getWeekOfMonth());
		}

		return semanasPorNombre;
	}

	private Map<String, Integer> getDiasPorNombre() {

		Map<String, Integer> diasPorNombre = new HashMap<>();

		for(DaysEntity dia : this.daysRepository.findAll()) {
			diasPorNombre.put(dia.getName(), dia.getDayOfWeek());
		}

		return diasPorNombre;
	}
}
