package CCASolutions.Calendario.ServiceImpl;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DateDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.FasesLunaresEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.FasesLunaresRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.CalendarioPdfService;
import CCASolutions.Calendario.Services.DatesService;
import CCASolutions.Calendario.Services.DownloadService;
import CCASolutions.Calendario.Services.EclipenosService;


@Service
public class DownloadServiceImpl implements DownloadService {

	@Autowired
	private DatesService datesService;

	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	@Autowired
	private FasesLunaresRepository lunasRepository;

	@Autowired
	private CalendarioPdfService calendarioPdfService;

	@Autowired
	private EclipenosRepository eclipenosRepository;

	@Autowired
	private EclipenosService eclipenosService;

	private final static String MANUAL_PATH = "Documentacion/Manual_Calendario.pdf";
	private final static String MANUAL_DESPLIEGUE_PATH = "Documentacion/Manual_de_despliegue.pdf";
	private final static int ESTACION_INVIERNO = 1;

	private final Map<Integer, byte[]> calendariosGenerados = new ConcurrentHashMap<>();

	public byte[] getManual() {

		return this.leerDocumento(MANUAL_PATH);
	}

	public byte[] getManualDespliegue() {

		return this.leerDocumento(MANUAL_DESPLIEGUE_PATH);
	}

	/** Documentos de la carpeta Documentacion, empaquetados en el classpath (ver pom.xml). */
	private byte[] leerDocumento(String ruta) {

		byte[] documento = null;

		ClassPathResource recurso = new ClassPathResource(ruta);

		if(recurso.exists()) {

			try (InputStream stream = recurso.getInputStream()) {

				documento = stream.readAllBytes();
			}
			catch(Exception e) {

				System.out.println("Error al leer " + ruta + ": " + e);
			}
		}
		else {

			System.out.println("No se ha encontrado en el classpath: " + ruta);
		}

		return documento;
	}

	public byte[] getCalendarForAYear(int yearForCalendar) {

		byte[] calendario = this.calendariosGenerados.get(yearForCalendar);

		if(calendario == null) {

			calendario = this.generarCalendario(yearForCalendar);

			if(calendario != null) {
				this.calendariosGenerados.put(yearForCalendar, calendario);
			}
		}

		return calendario;
	}

	private byte[] generarCalendario(int yearForCalendar) {

		byte[] calendario = null;

		SolsticiosYEquinocciosEntity solsticioAnyoAnterior = this.solsticiosYEquinocciosRepository.findByYearAndStartingSeason(yearForCalendar - 1, ESTACION_INVIERNO);
		SolsticiosYEquinocciosEntity solsticioAnyoSeleccionado = this.solsticiosYEquinocciosRepository.findByYearAndStartingSeason(yearForCalendar, ESTACION_INVIERNO);

		if(solsticioAnyoAnterior != null && solsticioAnyoSeleccionado != null) {

			LocalDate inicio = this.getInicioDelOterno(solsticioAnyoAnterior);
			LocalDate fin = this.getFinDelOternoLiminal(solsticioAnyoSeleccionado);

			System.out.println("Generando el calendario de " + yearForCalendar + ": del " + inicio + " al " + fin);

			List<DateDTO> dias = this.datesService.getDatesVAUFromRange(inicio, fin);

			calendario = this.calendarioPdfService.generarCalendarioAnual(yearForCalendar, solsticioAnyoAnterior.getDate().toLocalDate(),
					this.getUltimoEclipenoInvernalApofasalRemoto(solsticioAnyoAnterior.getDate().toLocalDate().plusDays(1)), dias);

			System.out.println("Calendario de " + yearForCalendar + " generado");
		}
		else {

			System.out.println("Error al generar el calendario de " + yearForCalendar + ": no se han encontrado sus solsticios de invierno.");
		}

		return calendario;
	}

	// El Oterno empieza con la última luna nueva anterior al solsticio de invierno
	private LocalDate getInicioDelOterno(SolsticiosYEquinocciosEntity solsticioDeInvierno) {

		FasesLunaresEntity lunaNuevaAnterior = this.lunasRepository.findFirstByDateBeforeAndNuevaIsTrueOrderByDateDesc(solsticioDeInvierno.getDate().toLocalDate().atStartOfDay());

		return lunaNuevaAnterior != null ? lunaNuevaAnterior.getDate().toLocalDate() : solsticioDeInvierno.getDate().toLocalDate();
	}

	// El Oterno liminal termina el día antes de la primera luna nueva desde el solsticio de invierno
	private LocalDate getFinDelOternoLiminal(SolsticiosYEquinocciosEntity solsticioDeInvierno) {

		FasesLunaresEntity lunaNuevaSiguiente = this.lunasRepository.findTopByDateGreaterThanEqualAndNuevaIsTrueOrderByDateAsc(solsticioDeInvierno.getDate().toLocalDate().atStartOfDay());

		return lunaNuevaSiguiente != null ? lunaNuevaSiguiente.getDate().toLocalDate().minusDays(1) : solsticioDeInvierno.getDate().toLocalDate();
	}

	private LocalDate getUltimoEclipenoInvernalApofasalRemoto(LocalDate date) {

		EclipenosEntity eclipeno = this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(this.eclipenosRepository.findAllByOrderByDateDesc(), date);

		return eclipeno != null ? eclipeno.getDate().toLocalDate() : null;
	}

}
