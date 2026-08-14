package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.MetonDTO;
import CCASolutions.Calendario.DTOs.MetonIADTO;
import CCASolutions.Calendario.DTOs.MetonINDTO;
import CCASolutions.Calendario.DTOs.MetonoInvernalApofasalRemotoDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MetonsService;


@Service
public class MetonsServiceImpl implements MetonsService {
	
	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;
	
	public MetonsEntity getLastMetonIApofasalRemoto(List<MetonsEntity> allMetons, LocalDate date) {
		
		MetonsEntity lastMetonIApofasalRemoto = new MetonsEntity();
		
		long diasMinimosDeDiferenciaEntreMetonoYDate =Long.MAX_VALUE;
		
		for(MetonsEntity metono : allMetons) {									
			
			if(!metono.getDate().toLocalDate().isAfter(date) && metono.isInvernal() && metono.isApofasal() && metono.isSelecto() && metono.isNuevo()) {
				
				long diasDeDiferenciaEntreMetonoYDate = ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreMetonoYDate < diasMinimosDeDiferenciaEntreMetonoYDate) {
					lastMetonIApofasalRemoto = new MetonsEntity();
					diasMinimosDeDiferenciaEntreMetonoYDate = diasDeDiferenciaEntreMetonoYDate;
					lastMetonIApofasalRemoto = metono;
				}
			}
		}
		return lastMetonIApofasalRemoto;
	}
	
	
	public MetonsEntity getLastMetonINForDate(List<MetonsEntity> allMetons, LocalDate date) {
		
		MetonsEntity lastMetonINForDate = new MetonsEntity();
		
		long diasMinimosDeDiferenciaEntreMetonoYDate =Long.MAX_VALUE;
		
		for(MetonsEntity metono : allMetons) {									
			
			if(!metono.getDate().toLocalDate().isAfter(date) && metono.isInvernal() && metono.isNuevo()) {
				
				long diasDeDiferenciaEntreMetonoYDate = ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreMetonoYDate < diasMinimosDeDiferenciaEntreMetonoYDate) {
					lastMetonINForDate = new MetonsEntity();
					diasMinimosDeDiferenciaEntreMetonoYDate = diasDeDiferenciaEntreMetonoYDate;
					lastMetonINForDate = metono;
				}
			}
		}
		return lastMetonINForDate;
	}
	

	
	public MetonoInvernalApofasalRemotoDTO getMetonoInvernalApofasalRemoto(EclipenosEntity lastEclipenoInvernalApofasalRemoto, List<MetonsEntity> allMetons, LocalDate date) {
		
		MetonoInvernalApofasalRemotoDTO metonoInvernalApofasalRemotoDTO = new MetonoInvernalApofasalRemotoDTO();
		
		if(lastEclipenoInvernalApofasalRemoto.getDate().toLocalDate().isEqual(date)){
			
			metonoInvernalApofasalRemotoDTO.setMetonoInvernalApofasalRemotoDay(true);
			metonoInvernalApofasalRemotoDTO.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(0);
			metonoInvernalApofasalRemotoDTO.setNumberOfMetonoInvernalApofasalRemoto(0);
			metonoInvernalApofasalRemotoDTO.setYearOfCurrentMetonoInvernalApofasalRemoto(0);
		}
		else {
			
			List<MetonsEntity> metonosInvernalesApofasalesRemotos = new ArrayList<>();
			
			for(MetonsEntity metono : allMetons) {
				
				if(metono.isInvernal() && metono.isApofasal() && metono.isNuevo() && metono.isSelecto() && !metono.getDate().isBefore(lastEclipenoInvernalApofasalRemoto.getDate()) && !metono.getDate().toLocalDate().isAfter(date)) {
					metonosInvernalesApofasalesRemotos.add(metono);
				}
			}
			
			metonoInvernalApofasalRemotoDTO.setYearOfCurrentMetonoInvernalApofasalRemoto(metonosInvernalesApofasalesRemotos.get(0).getYear());
			metonoInvernalApofasalRemotoDTO.setMetonoInvernalApofasalRemotoDay(metonosInvernalesApofasalesRemotos.get(0).getDate().toLocalDate().isEqual(date));
			
			int metonosIARDesdeElLastEclipenSelecto = (metonosInvernalesApofasalesRemotos.size()-1); // -1 porque incluye el del eclipeno
			
			// No se suma un eclipeno hasta que pase el dia del eclipeno, pero si es el dia de eclipeno no se resta, que se ha restado antes
			
			if(metonoInvernalApofasalRemotoDTO.isMetonoInvernalApofasalRemotoDay() && !lastEclipenoInvernalApofasalRemoto.getDate().toLocalDate().isEqual(date)) {
				
				metonosIARDesdeElLastEclipenSelecto = metonosIARDesdeElLastEclipenSelecto-1;
			}
			
			metonoInvernalApofasalRemotoDTO.setMetonosInvernalApofasalRemotoSinceLastEclipenoINSelecto(metonosIARDesdeElLastEclipenSelecto);
			int yearOfTheMetono= metonosIARDesdeElLastEclipenSelecto +1;
			
			if(lastEclipenoInvernalApofasalRemoto.getDate().toLocalDate().isEqual(date)) { //Si es el dia del eclipeno selecto, no estamos en ningun metono
				yearOfTheMetono= yearOfTheMetono-1;
			}
			metonoInvernalApofasalRemotoDTO.setNumberOfMetonoInvernalApofasalRemoto(yearOfTheMetono);
		}
		
		
		return metonoInvernalApofasalRemotoDTO;
	}
	

	
	public MetonDTO getVAUMeton (MetonsEntity lastMetonIApofasalRemoto, EclipenosEntity lastEclipenoINSelecto, List<MetonsEntity> metons, LocalDate date) {
		
		MetonDTO metonVAU = new MetonDTO();
		
		MetonINDTO metonINDTO = new MetonINDTO();
		MetonIADTO metonIADTO = new MetonIADTO();
		
		List<MetonsEntity> metonsIN = new ArrayList<>();
		List<MetonsEntity> metonsIA = new ArrayList<>();
		
		for(MetonsEntity meton : metons) {
			
			if(meton.isInvernal() && !meton.getDate().toLocalDate().isAfter(date)) {
				if(meton.isNuevo() && !meton.getDate().isBefore(lastMetonIApofasalRemoto.getDate())) {
					metonsIN.add(meton);
				}
				else if(meton.isAporico() && !meton.getDate().isBefore(lastMetonIApofasalRemoto.getDate())) {
					metonsIA.add(meton); 
				}		
			}
		}
		
		metonINDTO.setYearOfCurrentMetonIN(metonsIN.get(0).getYear());
		metonIADTO.setYearOfCurrentMetonIA(metonsIA.get(0).getYear());
		
		metonINDTO.setMetonoINDay(metonsIN.get(0).getDate().toLocalDate().isEqual(date));
		metonIADTO.setMetonoIADay(metonsIA.get(0).getDate().toLocalDate().isEqual(date));
		
		int metonosINDesdeElLastEclipen = (metonsIN.size()-1); // -1 porque incluye el del eclipeno
		
		// No se suma un metono hasta que pase el dia del metono, pero si es el dia de eclipeno no se resta, que se ha restado antes
		
		if(metonINDTO.isMetonoINDay() && !lastMetonIApofasalRemoto.getDate().toLocalDate().isEqual(date)) {
			
			metonosINDesdeElLastEclipen = metonosINDesdeElLastEclipen-1;
		}
		
		metonINDTO.setMetonosINSinceLastEclipenoIN(metonosINDesdeElLastEclipen);
		int yearOfTheMetonIN = metonosINDesdeElLastEclipen +1;
		
		if(lastMetonIApofasalRemoto.getDate().toLocalDate().isEqual(date)) { //Si es el dia del metonoIAR, no estamos en ningun metono
			yearOfTheMetonIN= yearOfTheMetonIN-1;
		}
		
		metonINDTO.setNumberOfMetonIN(yearOfTheMetonIN);
		
		
		
		
		int metonosIADesdeElMetonIApofasalRemoto = (metonsIA.size()-1);
		
		if(metonIADTO.isMetonoIADay() && !lastMetonIApofasalRemoto.getDate().toLocalDate().isEqual(date)) {
			
			metonosIADesdeElMetonIApofasalRemoto = metonosIADesdeElMetonIApofasalRemoto-1;
		}
		
		metonIADTO.setMetonosIASinceLastEclipenoSelecto(metonosIADesdeElMetonIApofasalRemoto);
		int yearOfTheMetonIA = metonosIADesdeElMetonIApofasalRemoto +1;
		
		if(lastMetonIApofasalRemoto.getDate().toLocalDate().isEqual(date)) {
			yearOfTheMetonIA=yearOfTheMetonIA-1;
		}
		
		metonIADTO.setNumberOfMetonIA(yearOfTheMetonIA);
		
		
		
		if(metonsIN.get(0).isInvertido() && yearOfTheMetonIN != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonsIN.get(0).isSelecto() && yearOfTheMetonIN != 0 && !metonINDTO.isMetonoINDay()) {
			metonINDTO.setLastMetonSurname("(Selecto)");
		}
		
		
		if(metonsIA.get(0).isInvertido() && yearOfTheMetonIN != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Invertido)");
		}
		else if(metonsIA.get(0).isSelecto() && yearOfTheMetonIN != 0 && !metonIADTO.isMetonoIADay()) {
			metonIADTO.setLastMetonSurname("(Selecto)");
		}
		
		
		metonVAU.setMetonsIN(metonINDTO);
		metonVAU.setMetonsIA(metonIADTO);
		
		return metonVAU;
	}
	
	public String poblateMetonos() {
		
		String resultado = "Metonos checkeados sin problema.";
		
		System.out.println("Iniciando evaluacion de metonos.");
		
		List<MetonsEntity> metonosEnBBDD = this.metonsRepository.findAll();
		
		if(metonosEnBBDD.isEmpty()) {
			List<LunasEntity> allLunas = this.lunasRepository.findAll();
			List<ApogeosYPerigeosLunaEntity> allApoperis = this.apogeosYPerigeosLunaRepository.findAll();
			List<SolsticiosYEquinocciosEntity> allSoes = this.solsticiosYEquinocciosRepository.findAll();
			
			if(!allLunas.isEmpty() && ! allSoes.isEmpty()) {
				
				List<MetonsEntity> metonosParaDB = new ArrayList<>();
				for(SolsticiosYEquinocciosEntity soe : allSoes) {
					
					for (LunasEntity luna : allLunas) {
						
						if((luna.isNueva() || luna.isLlena()) && Math.abs(ChronoUnit.SECONDS.between(luna.getDate(), soe.getDate())) <= 86164) {
							
							MetonsEntity nuevoMetono = new MetonsEntity();
							
							nuevoMetono.setFasal(true);
							
							nuevoMetono.setLunaId(luna.getId());
							nuevoMetono.setSelecto(luna.isSelecta());
							nuevoMetono.setInvertido(luna.isInvertida());
							nuevoMetono.setNuevo(luna.isNueva());
							nuevoMetono.setLleno(luna.isLlena());
							
							nuevoMetono.setSoeId(soe.getId());
							nuevoMetono.setYear(soe.getYear());
							nuevoMetono.setDate(soe.getDate());
							nuevoMetono.setInvernal(soe.isSolsticioInvierno());
							nuevoMetono.setPrimaveral(soe.isEquinoccioPrimavera());
							nuevoMetono.setEstival(soe.isSolsticioVerano());
							nuevoMetono.setOtonyal(soe.isEquinoccioOtonyo());
										
							metonosParaDB.add(nuevoMetono);
							
							System.out.println("Nuevo métono fasal encontrado: " + nuevoMetono.getDate().toLocalDate());
						}
					}
					
					
					for(ApogeosYPerigeosLunaEntity apoperi : allApoperis) {
						
						if((apoperi.isEsApogeo() || apoperi.isEsPerigeo()) && Math.abs(ChronoUnit.SECONDS.between(apoperi.getDate(), soe.getDate())) <= 86164) {
							
							MetonsEntity nuevoMetono = new MetonsEntity();
							
							nuevoMetono.setApoperico(true);
							
							nuevoMetono.setApoperiId(apoperi.getId());
							nuevoMetono.setSelecto(apoperi.isEsSelecto());
							nuevoMetono.setInvertido(apoperi.isEsInvertido());
							nuevoMetono.setPerico(apoperi.isEsPerigeo());
							nuevoMetono.setAporico(apoperi.isEsApogeo());
							
							nuevoMetono.setSoeId(soe.getId());
							nuevoMetono.setYear(soe.getYear());
							nuevoMetono.setDate(soe.getDate());
							
							nuevoMetono.setInvernal(soe.isSolsticioInvierno());
							nuevoMetono.setPrimaveral(soe.isEquinoccioPrimavera());
							nuevoMetono.setEstival(soe.isSolsticioVerano());
							nuevoMetono.setOtonyal(soe.isEquinoccioOtonyo());
										
							metonosParaDB.add(nuevoMetono);
							
							System.out.println("Nuevo métono apopérico encontrado: " + nuevoMetono.getDate().toLocalDate());
						}
					}
					
				}
				
				System.out.println("Actualizando métonos apofasales");
				Map<Long, LunasEntity> lunasMap = allLunas.stream().collect(Collectors.toMap(LunasEntity::getId, l -> l));
				Map<Long, ApogeosYPerigeosLunaEntity> apoperiMap = allApoperis.stream().collect(Collectors.toMap(ApogeosYPerigeosLunaEntity::getId, a -> a));
				
				for(MetonsEntity meton : metonosParaDB) {
					
					if(meton.isSelecto() || meton.isInvertido()) {
						if(meton.getLunaId() != null) {
							
							ApogeosYPerigeosLunaEntity apoperiMasCercano = new ApogeosYPerigeosLunaEntity();
							Long minimosDiasDeDiferenciaEntreLunaYApoperi = Long.MAX_VALUE;
							
							for(ApogeosYPerigeosLunaEntity apoperi : allApoperis) {
								
								Long diasDeDiferenciaEntreLunaYApoperi = Math.abs(ChronoUnit.DAYS.between(apoperi.getDate().toLocalDate(), lunasMap.get(meton.getLunaId()).getDate().toLocalDate()));
								
								if(diasDeDiferenciaEntreLunaYApoperi < minimosDiasDeDiferenciaEntreLunaYApoperi) {
									minimosDiasDeDiferenciaEntreLunaYApoperi = diasDeDiferenciaEntreLunaYApoperi;
									apoperiMasCercano = apoperi;
								}
							}
							
							if(Math.abs(ChronoUnit.SECONDS.between(apoperiMasCercano.getDate(), meton.getDate())) <= 86164 &&  Math.abs(ChronoUnit.SECONDS.between(lunasMap.get(meton.getLunaId()).getDate(), meton.getDate())) <= 86164 &&  Math.abs(ChronoUnit.SECONDS.between(apoperiMasCercano.getDate(), meton.getDate())) <= 86164) {
								meton.setApofasal(true);
								System.out.println("Nuevo métono apofasal encontrado: " + meton.getDate().toLocalDate());
							}						
						}
						else if (meton.getApoperiId() != null) {
							
							LunasEntity lunaMasCercana = new LunasEntity();
							Long minimosDiasDeDiferenciaEntreLunaYApoperi = Long.MAX_VALUE;
							for(LunasEntity luna : allLunas) {
								
								if(luna.isNueva() || luna.isLlena()) {
									Long diasDeDiferenciaEntreLunaYApoperi = Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), apoperiMap.get(meton.getApoperiId()).getDate().toLocalDate()));
									
									if(diasDeDiferenciaEntreLunaYApoperi < minimosDiasDeDiferenciaEntreLunaYApoperi) {
										minimosDiasDeDiferenciaEntreLunaYApoperi = diasDeDiferenciaEntreLunaYApoperi;
										lunaMasCercana = luna;
									}
								}
							}
							
							if(Math.abs(ChronoUnit.SECONDS.between(lunaMasCercana.getDate(), meton.getDate())) <= 86164 &&  Math.abs(ChronoUnit.SECONDS.between(apoperiMap.get(meton.getApoperiId()).getDate(), meton.getDate())) <= 86164 &&  Math.abs(ChronoUnit.SECONDS.between(apoperiMap.get(meton.getApoperiId()).getDate(), lunaMasCercana.getDate())) <= 86164) {
								
								meton.setApofasal(true);
								System.out.println("Nuevo métono apofasal encontrado: " + meton.getDate().toLocalDate());
								
							}											
						}
					}									
				}
				
				this.metonsRepository.saveAll(metonosParaDB);				
				System.out.println("Evaluacion de métonos finalizada.");
			}
			else {
				if(allLunas.isEmpty()) {
					
					System.out.println("No hay lunas en la base de datos.");
					resultado = "Error al chequear metonos: no hay lunas en la base de datos.";
				}
				else if (allSoes.isEmpty()){
					
					System.out.println("No hay soes en la base de datos.");
					resultado = "Error al chequear metonos: no hay soes en la base de datos.";
				}
			}
		}
		else {
			System.out.println("Ya hay metonos en la BBDD");
			resultado="Error a la hora de actualizar los metonos: Ya hay metonos en la base de datos.";
		}
		
		
		return resultado;
	}


}
