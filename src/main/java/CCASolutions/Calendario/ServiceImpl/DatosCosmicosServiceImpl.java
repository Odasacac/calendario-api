package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.DatosCosmicosRangoDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.DatosCosmicosService;

@Service
public class DatosCosmicosServiceImpl implements DatosCosmicosService {

	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository; 
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;
	
	@Autowired
	private EclipenosService eclipenosService;
	
	@Autowired
	private MetonsService metonsService;
	
	private final static int numeroAnyosInferior = 1;
	private final static int numeroAnyoSuperior = 1;
	private final static int numeroMesesInferior = 3;
	private final static int numeroMesesSuperior = 3;

	
	public DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date) {
		
		DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO = new DatosCosmicosParaVAUDTO();
		LocalDateTime dateO = date.atTime(LocalTime.MAX);	
		
		
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAllByOrderByDateDesc();
		
		if(!allEclipenos.isEmpty()) {

			datosCosmicosParaVAUDTO.setEclipenos(allEclipenos);
			datosCosmicosParaVAUDTO.setLastEclipenoIN(this.eclipenosService.getLastEclipenoIN(allEclipenos, date));
			datosCosmicosParaVAUDTO.setLastEclipenoInvernalApofasalRemoto(this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(allEclipenos, date));
			
			if(datosCosmicosParaVAUDTO.getLastEclipenoIN() != null && datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto() != null) {
				
				List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto().getDate().minusYears(numeroAnyosInferior), dateO.plusYears(numeroAnyoSuperior));
				
				if(!allMetons.isEmpty()) {
					
					datosCosmicosParaVAUDTO.setMetons(allMetons);
					datosCosmicosParaVAUDTO.setLastMetonIN(this.metonsService.getLastMetonINForDate(allMetons, date));
					datosCosmicosParaVAUDTO.setLastMetonIApofasalRemoto(this.metonsService.getLastMetonIApofasalRemoto(allMetons,date));
					
					if(datosCosmicosParaVAUDTO.getLastMetonIN() != null) {									
						
						datosCosmicosParaVAUDTO.setLunas(this.lunasRepository.findByDateBetween(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate(), dateO.plusYears(numeroAnyoSuperior)));
						datosCosmicosParaVAUDTO.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(datosCosmicosParaVAUDTO.getLastMetonIN().getDate().minusYears(numeroAnyosInferior), dateO.plusYears(numeroAnyoSuperior)));
						datosCosmicosParaVAUDTO.setEclipses(this.eclipsesRepository.findEclipsesAbsoluteQuery(datosCosmicosParaVAUDTO.getLastEclipenoIN().getDate().toLocalDate().atStartOfDay(), dateO.plusYears(numeroAnyoSuperior)));
						datosCosmicosParaVAUDTO.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetween(dateO.minusMonths(numeroMesesInferior), dateO.plusMonths(numeroMesesSuperior)));
						
						if(datosCosmicosParaVAUDTO.getApoperis().isEmpty()){
							datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado apoperis.");
							System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
						}
						else if(datosCosmicosParaVAUDTO.getSoes().isEmpty()) {
							datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado soes.");
							System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
						}
						else if(datosCosmicosParaVAUDTO.getLunas().isEmpty()) {
							datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado fases lunares.");
							System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
						}
						else if(datosCosmicosParaVAUDTO.getEclipses().isEmpty()) {
							datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado eclipses.");
							System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
						}
						else {
							datosCosmicosParaVAUDTO.setValido(true);
						}
					}
					else {
						datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
						System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
					}				
				}
				else {
					datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado métonos.");
					System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
				}
			}
			else {
				if(datosCosmicosParaVAUDTO.getLastEclipenoIN() == null) {
					datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un eclípeno inicial nuevo anterior a la fecha proporcionada.");
					System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
				}
				else if (datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto() == null) {
					datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un eclípeno invernal apofasal remoto anterior a la fecha proporcionada.");
					System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
				}
				
			}
		}
		else {
			datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no hay eclipenos");
			System.out.println(datosCosmicosParaVAUDTO.getMensaje());	
		}
				
		return datosCosmicosParaVAUDTO;
	}


	public DatosCosmicosRangoDTO getDatosCosmicosRango(LocalDate desde, LocalDate hasta) {

		DatosCosmicosRangoDTO datosCosmicosRango = new DatosCosmicosRangoDTO();
		LocalDateTime hastaO = hasta.atTime(LocalTime.MAX);

		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAllByOrderByDateDesc();
		datosCosmicosRango.setEclipenos(allEclipenos);

		LocalDate primeraFechaConEclipenos = null;
		EclipenosEntity lastEclipenoIN = null;
		EclipenosEntity lastEclipenoInvernalApofasalRemoto = null;

		for(LocalDate date = desde; !allEclipenos.isEmpty() && !date.isAfter(hasta) && primeraFechaConEclipenos == null; date = date.plusDays(1)) {

			lastEclipenoIN = this.eclipenosService.getLastEclipenoIN(allEclipenos, date);
			lastEclipenoInvernalApofasalRemoto = this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(allEclipenos, date);

			if(lastEclipenoIN != null && lastEclipenoInvernalApofasalRemoto != null) {
				primeraFechaConEclipenos = date;
			}
		}

		if(primeraFechaConEclipenos != null) {

			LocalDateTime primeraFechaO = primeraFechaConEclipenos.atTime(LocalTime.MAX);
			LocalDateTime metonsDesde = lastEclipenoInvernalApofasalRemoto.getDate().minusYears(numeroAnyosInferior);
			List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(metonsDesde, hastaO.plusYears(numeroAnyoSuperior));
			List<MetonsEntity> metonsPrimeraFecha = this.filtrarPorFecha(allMetons, MetonsEntity::getDate, metonsDesde, true, primeraFechaO.plusYears(numeroAnyoSuperior));

			MetonsEntity lastMetonIN = this.metonsService.getLastMetonINForDate(metonsPrimeraFecha, primeraFechaConEclipenos);
			MetonsEntity lastMetonIApofasalRemoto = this.metonsService.getLastMetonIApofasalRemoto(metonsPrimeraFecha, primeraFechaConEclipenos);

			if(lastMetonIN != null && lastMetonIApofasalRemoto != null) {

				datosCosmicosRango.setMetons(allMetons);
				datosCosmicosRango.setMetonsDesde(metonsDesde);

				datosCosmicosRango.setLunasDesde(lastMetonIApofasalRemoto.getDate());
				datosCosmicosRango.setLunas(this.lunasRepository.findByDateBetween(datosCosmicosRango.getLunasDesde(), hastaO.plusYears(numeroAnyoSuperior)));

				datosCosmicosRango.setSoesDesde(lastMetonIN.getDate().minusYears(numeroAnyosInferior));
				datosCosmicosRango.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(datosCosmicosRango.getSoesDesde(), hastaO.plusYears(numeroAnyoSuperior)));

				datosCosmicosRango.setEclipsesDesde(lastEclipenoIN.getDate().toLocalDate().atStartOfDay());
				datosCosmicosRango.setEclipses(this.eclipsesRepository.findEclipsesAbsoluteQuery(datosCosmicosRango.getEclipsesDesde(), hastaO.plusYears(numeroAnyoSuperior)));

				datosCosmicosRango.setApoperisDesde(primeraFechaO.minusMonths(numeroMesesInferior));
				datosCosmicosRango.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetween(datosCosmicosRango.getApoperisDesde(), hastaO.plusMonths(numeroMesesSuperior)));

				datosCosmicosRango.setValido(true);
			}
		}

		return datosCosmicosRango;
	}


	public DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date, DatosCosmicosRangoDTO datosCosmicosRango) {

		if(!datosCosmicosRango.isValido()) {
			return this.getDatosCosmicos(date);
		}

		DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO = new DatosCosmicosParaVAUDTO();
		LocalDateTime dateO = date.atTime(LocalTime.MAX);

		List<EclipenosEntity> allEclipenos = datosCosmicosRango.getEclipenos();

		datosCosmicosParaVAUDTO.setEclipenos(allEclipenos);
		datosCosmicosParaVAUDTO.setLastEclipenoIN(this.eclipenosService.getLastEclipenoIN(allEclipenos, date));
		datosCosmicosParaVAUDTO.setLastEclipenoInvernalApofasalRemoto(this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(allEclipenos, date));

		if(datosCosmicosParaVAUDTO.getLastEclipenoIN() != null && datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto() != null) {

			LocalDateTime metonsDesde = datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto().getDate().minusYears(numeroAnyosInferior);

			if(metonsDesde.isBefore(datosCosmicosRango.getMetonsDesde())) {
				return this.getDatosCosmicos(date);
			}

			List<MetonsEntity> allMetons = this.filtrarPorFecha(datosCosmicosRango.getMetons(), MetonsEntity::getDate, metonsDesde, true, dateO.plusYears(numeroAnyoSuperior));

			if(!allMetons.isEmpty()) {

				datosCosmicosParaVAUDTO.setMetons(allMetons);
				datosCosmicosParaVAUDTO.setLastMetonIN(this.metonsService.getLastMetonINForDate(allMetons, date));
				datosCosmicosParaVAUDTO.setLastMetonIApofasalRemoto(this.metonsService.getLastMetonIApofasalRemoto(allMetons,date));

				if(datosCosmicosParaVAUDTO.getLastMetonIN() != null) {

					LocalDateTime lunasDesde = datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate();
					LocalDateTime soesDesde = datosCosmicosParaVAUDTO.getLastMetonIN().getDate().minusYears(numeroAnyosInferior);
					LocalDateTime eclipsesDesde = datosCosmicosParaVAUDTO.getLastEclipenoIN().getDate().toLocalDate().atStartOfDay();
					LocalDateTime apoperisDesde = dateO.minusMonths(numeroMesesInferior);

					if(lunasDesde.isBefore(datosCosmicosRango.getLunasDesde()) || soesDesde.isBefore(datosCosmicosRango.getSoesDesde())
							|| eclipsesDesde.isBefore(datosCosmicosRango.getEclipsesDesde()) || apoperisDesde.isBefore(datosCosmicosRango.getApoperisDesde())) {
						return this.getDatosCosmicos(date);
					}

					datosCosmicosParaVAUDTO.setLunas(this.filtrarPorFecha(datosCosmicosRango.getLunas(), LunasEntity::getDate, lunasDesde, true, dateO.plusYears(numeroAnyoSuperior)));
					datosCosmicosParaVAUDTO.setSoes(this.filtrarPorFecha(datosCosmicosRango.getSoes(), SolsticiosYEquinocciosEntity::getDate, soesDesde, false, dateO.plusYears(numeroAnyoSuperior)));
					datosCosmicosParaVAUDTO.setEclipses(this.filtrarPorFecha(datosCosmicosRango.getEclipses(), EclipsesEntity::getDate, eclipsesDesde, true, dateO.plusYears(numeroAnyoSuperior)));
					datosCosmicosParaVAUDTO.setApoperis(this.filtrarPorFecha(datosCosmicosRango.getApoperis(), ApogeosYPerigeosLunaEntity::getDate, apoperisDesde, true, dateO.plusMonths(numeroMesesSuperior)));

					if(datosCosmicosParaVAUDTO.getApoperis().isEmpty()){
						datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado apoperis.");
					}
					else if(datosCosmicosParaVAUDTO.getSoes().isEmpty()) {
						datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado soes.");
					}
					else if(datosCosmicosParaVAUDTO.getLunas().isEmpty()) {
						datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado fases lunares.");
					}
					else if(datosCosmicosParaVAUDTO.getEclipses().isEmpty()) {
						datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado eclipses.");
					}
					else {
						datosCosmicosParaVAUDTO.setValido(true);
					}
				}
				else {
					datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un métono anterior a la fecha proporcionada.");
				}
			}
			else {
				datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se han encontrado métonos.");
			}
		}
		else if(datosCosmicosParaVAUDTO.getLastEclipenoIN() == null) {
			datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un eclípeno inicial nuevo anterior a la fecha proporcionada.");
		}
		else {
			datosCosmicosParaVAUDTO.setMensaje("Error al obtener dateVAU: no se ha encontrado un eclípeno invernal apofasal remoto anterior a la fecha proporcionada.");
		}

		return datosCosmicosParaVAUDTO;
	}


	private <T> List<T> filtrarPorFecha(List<T> lista, Function<T, LocalDateTime> fecha, LocalDateTime desde, boolean desdeIncluido, LocalDateTime hasta) {

		List<T> filtrada = new ArrayList<>();

		for(T elemento : lista) {

			LocalDateTime fechaElemento = fecha.apply(elemento);
			boolean despuesDeDesde = desdeIncluido ? !fechaElemento.isBefore(desde) : fechaElemento.isAfter(desde);

			if(despuesDeDesde && !fechaElemento.isAfter(hasta)) {
				filtrada.add(elemento);
			}
		}

		return filtrada;
	}
}
