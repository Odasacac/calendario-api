package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.EclipenosService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.UtilsService;

@Service
public class UtilsServiceImpl implements UtilsService {

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

	
	public DatosCosmicosParaVAUDTO getDatosCosmicos(LocalDate date) {
		
		DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO = new DatosCosmicosParaVAUDTO();
		LocalDateTime dateO = date.atTime(LocalTime.MAX);	
		
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAllByOrderByDateDesc();
		
		if(!allEclipenos.isEmpty()) {

			datosCosmicosParaVAUDTO.setEclipenos(allEclipenos);
			datosCosmicosParaVAUDTO.setLastEclipenoIN(this.eclipenosService.getLastEclipenoIN(allEclipenos, date));
			datosCosmicosParaVAUDTO.setLastEclipenoInvernalApofasalRemoto(this.eclipenosService.getLastEclipenoInvernalApofasalRemoto(allEclipenos, date));
			
			if(datosCosmicosParaVAUDTO.getLastEclipenoIN() != null && datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto() != null) {
				
				List<MetonsEntity> allMetons = this.metonsRepository.findByDateBetweenOrderByDateDesc(datosCosmicosParaVAUDTO.getLastEclipenoInvernalApofasalRemoto().getDate().minusYears(1), dateO.plusYears(1));
				
				if(!allMetons.isEmpty()) {
					
					datosCosmicosParaVAUDTO.setMetons(allMetons);
					datosCosmicosParaVAUDTO.setLastMetonIN(this.metonsService.getLastMetonINForDate(allMetons, date));
					datosCosmicosParaVAUDTO.setLastMetonIApofasalRemoto(this.metonsService.getLastMetonIApofasalRemoto(allMetons,date));
					
					if(datosCosmicosParaVAUDTO.getLastMetonIN() != null) {									
						
						datosCosmicosParaVAUDTO.setLunas(this.lunasRepository.findByDateBetween(datosCosmicosParaVAUDTO.getLastMetonIApofasalRemoto().getDate().minusYears(1), dateO.plusYears(1)));
						datosCosmicosParaVAUDTO.setSoes(this.solsticiosYEquinocciosRepository.findByDateAfterAndDateLessThanEqual(datosCosmicosParaVAUDTO.getLastMetonIN().getDate().minusYears(1), dateO.plusYears(1)));
						datosCosmicosParaVAUDTO.setEclipses(this.eclipsesRepository.findByDateBetweenAndEsParcialIsFalseAndEsPenumbralIsFalse(datosCosmicosParaVAUDTO.getLastEclipenoIN().getDate().toLocalDate().atStartOfDay(), dateO.plusYears(1)));
						datosCosmicosParaVAUDTO.setApoperis(this.apogeosYPerigeosLunaRepository.findByDateBetween(dateO.minusMonths(3), dateO.plusMonths(3)));
						
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
}
