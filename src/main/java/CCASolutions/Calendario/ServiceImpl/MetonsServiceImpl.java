package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.FenomenoDTO;
import CCASolutions.Calendario.DTOs.LunarPhaseDTO;
import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
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
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	
	public String checkMetonosViaDB() {
		
		String resultado = "Metonos checkeados sin problema.";
		
		System.out.println("Iniciando evaluacion de metonos.");
		
		List<MetonsEntity> metonosEnBBDD = this.metonsRepository.findAll();
		
		if(metonosEnBBDD.isEmpty()) {
			List<LunasEntity> allLunas = this.lunasRepository.findAll();
			List<SolsticiosYEquinocciosEntity> allSoes = this.solsticiosYEquinocciosRepository.findAll();
			
			if(!allLunas.isEmpty() && ! allSoes.isEmpty()) {
				
				List<MetonsEntity> metonosParaDB = new ArrayList<>();
				for(SolsticiosYEquinocciosEntity soe : allSoes) {
					
					for (LunasEntity luna : allLunas) {
						
						if((luna.isNueva() || luna.isLlena()) && Math.abs(ChronoUnit.SECONDS.between(luna.getDate(), soe.getDate())) <= 86164) {
							
							MetonsEntity nuevoMetono = new MetonsEntity();
							
							nuevoMetono.setLunaId(luna.getId());
							nuevoMetono.setSelecto(luna.isSelecta());
							nuevoMetono.setTransicional(luna.isTransicional());
							nuevoMetono.setNuevo(luna.isNueva());
							nuevoMetono.setLleno(luna.isLlena());
							
							nuevoMetono.setSoeId(soe.getId());
							nuevoMetono.setYear(soe.getYear());
							nuevoMetono.setDate(soe.getDate());
							nuevoMetono.setInicial(soe.isSolsticioInvierno());
							nuevoMetono.setCuartal(soe.isEquinoccioPrimavera());
							nuevoMetono.setBicuartal(soe.isSolsticioVerano());
							nuevoMetono.setTricuartal(soe.isEquinoccioOtonyo());
							
							nuevoMetono.setSolsticial(nuevoMetono.getInicial() || nuevoMetono.getBicuartal());
							nuevoMetono.setEquinoccial(nuevoMetono.getCuartal() || nuevoMetono.getTricuartal());
										
							metonosParaDB.add(nuevoMetono);
							
							System.out.println("Nuevo métono encontrado: " + nuevoMetono.getDate());
						}
					}
				}
				this.metonsRepository.saveAll(metonosParaDB);
				System.out.println("Evaluacion de métonos finalizada.");
			}
			else {
				if(allLunas.isEmpty()) {
					
					System.out.println("No hay lunas en la base de datos.");
					resultado = "Error al chequear metonos, chequear logs,";
				}
				else if (allSoes.isEmpty()){
					
					System.out.println("No hay soes en la base de datos.");
					resultado = "Error al chequear metonos, chequear logs,";
				}
			}
		}
		else {
			System.out.println("Ya hay metonos en la BBDD");
			resultado="Error a la hora de actualizar los metonos, chequear logs.";
		}
		
		
		return resultado;
	}
	
	public String checkMetonosSinceToViaAPI() {
		
		String resultado = "Metonos checkeados sin problema.";
					
		System.out.println("Iniciando evaluacion de metonos.");
		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("ASYEF", "YLP"));	
		List<MetonsEntity> allMetons = this.metonsRepository.findAll();
		
		String apiGetLunasUrl = "";
		String apiGetSYEUrl = "";
		
		if(allMetons.isEmpty()) {
			
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
					
				for(int anyoCheckeado = 0; anyoCheckeado<=2100; anyoCheckeado++) {
						
					System.out.println("Evaluando metonos en el anyo " + anyoCheckeado);
					
					try {
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
					}
					
					catch(Exception e) {
						
						System.out.println("Error evaluando los metonos del anyo " + anyoCheckeado + ": " + e);
					}
					
						
					System.out.println("Fin de la evaluacion de metonos del anyo " + anyoCheckeado);
				}
			}
			catch(Exception e) {
					
				System.out.println("Error al evaluar los metonos: " + e);
				resultado = "Error al evaluar los metonos, revisar logs";
			}	
		}
		else {
			System.out.println("Ya hay métonos en la base de datos");
			resultado = "Error al actualizar los métonos, checkear logs.";
		}
			
			
		
		return resultado;
	}
	

}
