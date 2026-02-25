package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.MetonsService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
public class MetonsServiceImpl implements MetonsService {
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Autowired
	private LunasService lunasService;
	
	public String checkMetonosSinceToViaAPI(int since, int to) {
		
		String resultado = "Metonos checkeados sin problema.";
		
		if(since > to) {
			
			resultado = "Since (" + since + ") es mayor que to (" + to + ").";
		}
		else {
			
			System.out.println("Iniciando evaluacion de metonos desde el anyo " + since + " hasta el anyo " + to + ".");
			List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("ASYEF", "YLP"));	
			
			String apiGetLunasUrl = "";
			String apiGetSYEUrl = "";
			
			for (DatosEntity url : urls) 
			{
				switch (url.getConcepto()) {
				
					case "ASYEF":					
						apiGetSYEUrl = url.getValor();
						break;
					
					case "YLP":					
						apiGetLunasUrl = url.getValor();
						break;					
				}
			}
			
			try {
				
				for(int anyoCheckeado = since; anyoCheckeado<=to; anyoCheckeado++) {
					
					System.out.println("Evaluando metonos en el anyo " + anyoCheckeado);
					List<FenomenoDTO> solsticiosYEquinocciosDelAnyo = this.solsticiosYEquinocciosService.getSolsticiosYEquinocciosDelAnyoViaAPI(String.valueOf(anyoCheckeado), apiGetSYEUrl);
					List<LunarPhaseDTO> fasesLunaresDelAnyo = this.lunasService.getFasesLunaresDelAnyoViaAPI(String.valueOf(anyoCheckeado), apiGetLunasUrl);
					
					for(LunarPhaseDTO luna : fasesLunaresDelAnyo) {
						
						if (luna.getMoonPhase().equals("NewMoon") || luna.getMoonPhase().equals("FullMoon")){
							
							LocalDateTime fechaLuna = luna.getDate();
							
							for(FenomenoDTO soe : solsticiosYEquinocciosDelAnyo) {
								
								LocalDateTime fechaSoe = soe.getDate();
								
								long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(fechaLuna, fechaSoe));
								
								if(segundosDeDiferencia <= 86164) {
									
									MetonsEntity nuevoMetono = new MetonsEntity();
									
									nuevoMetono.setDate(fechaSoe);
									nuevoMetono.setYear(fechaSoe.getYear());
									
									if(luna.getMoonPhase().equals("NewMoon")) {
										nuevoMetono.setNuevo(true);
									}
									else {
										nuevoMetono.setLleno(true);
									}
									
									switch (soe.getPhenomena()) {
									
										case "WinterSolstice":
											nuevoMetono.setInicial(true);
											nuevoMetono.setSolsticial(true);
											break;
											
										case "VernalEquinox":
											nuevoMetono.setCuartal(true);
											nuevoMetono.setEquinoccial(true);
											break;
											
										case "SummerSolstice":
											nuevoMetono.setBicuartal(true);
											nuevoMetono.setSolsticial(true);
											break;
											
										case "AutumnalEquinox":
											nuevoMetono.setTricuartal(true);
											nuevoMetono.setEquinoccial(true);
											break;
									
									}
									
									List<MetonsEntity> metonosDelAnyo = this.metonsRepository.findByYear(fechaSoe.getYear());
									
									if(metonosDelAnyo.isEmpty()) {
										
										this.metonsRepository.save(nuevoMetono);
										System.out.println("Nuevo metono encontrado.");
									}
									else {
										
										boolean metonoYaExiste = false;
										for(int i = 0; i<metonosDelAnyo.size(); i++) {
											
											if(metonosDelAnyo.get(i).getDate().isEqual(nuevoMetono.getDate())){
												
												metonoYaExiste=true;																			
											}									
										}
										
										if(!metonoYaExiste) {
											
											this.metonsRepository.save(nuevoMetono);
											System.out.println("Nuevo metono encontrado.");	
										}									
									}
											
								}
								
							}
							
						}
					}
					
					System.out.println("Fin de la evaluacion de metonos del anyo " + anyoCheckeado);
				}
			}
			catch(Exception e) {
				
				System.out.println("Error al evaluar los metonos: " + e);
				resultado = "Error al evaluar los metonos, revisar logs";
			}
		}		
		
		return resultado;
	}
	

}
