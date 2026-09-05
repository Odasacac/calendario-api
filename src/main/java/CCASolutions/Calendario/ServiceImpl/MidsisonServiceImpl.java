package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MidsisonEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MidsisonService;

@Service
public class MidsisonServiceImpl implements MidsisonService{

	@Autowired
	private MidsisonRepository midsisonRepository;
	
	@Autowired
	private LunasRepository lunasRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apoperisRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;
	
	public String poblateMidsison() {
		
		/*
		Un midsison es el momento en el que hacia el lastSOE al nextSOE pasara el mismo tiempo.
		
		Siempre son referentes a la estacion pasada: Midsison invernal.
		
		Se coge un soe, se coge el siguiente soe a ese
		
	*/
		System.out.println("Iniciando poblar Midsison.");
		
		String resultado = "Todo ha salido mal";	
		
		try {
			
			List<MidsisonEntity> allMidsisonsFromDB = this.midsisonRepository.findAll();
			
			if(allMidsisonsFromDB.isEmpty()) {
				List<SolsticiosYEquinocciosEntity> allSoesFromDB = this.solsticiosYEquinocciosRepository.findAll();
				
				if(allSoesFromDB.isEmpty()) {
					System.out.println("No hay soes en base de datos");
					resultado = "Error al poblar los midsisons: no hay soes en base de datos.";
				}
				else {
					
					List<MidsisonEntity> midsisonsForDB = new ArrayList<>();
					for(int i = 0; i<allSoesFromDB.size()-1; i++) {
						
						SolsticiosYEquinocciosEntity pastSoe = allSoesFromDB.get(i);
						SolsticiosYEquinocciosEntity nextSoe = allSoesFromDB.get(i+1);	
						
						MidsisonEntity midsison = new MidsisonEntity();
						
						midsison.setPastSOEId(pastSoe.getId());
						midsison.setNextSOEId(nextSoe.getId());
						
						midsison.setDate(pastSoe.getDate().plusSeconds((ChronoUnit.SECONDS.between(pastSoe.getDate(), nextSoe.getDate()))/2));
						
						midsison.setLastSoeInvernal(pastSoe.isSolsticioInvierno());
						midsison.setLastSoePrimaveral(pastSoe.isEquinoccioPrimavera());
						midsison.setLastSoeEstival(pastSoe.isSolsticioVerano());
						midsison.setLastSoeOtonyal(pastSoe.isEquinoccioOtonyo());
						
						midsisonsForDB.add(midsison);					
					}
					
					System.out.println("Midsisons poblados correctamente, ahora a ver si llenos o nuevos también.");
					
					List<LunasEntity> allLunasFromDB = this.lunasRepository.findAll();
					List<ApogeosYPerigeosLunaEntity> allApoperisFromDB = this.apoperisRepository.findAll();
					List<EclipsesEntity> allEclipsesFromDB = this.eclipsesRepository.findAll();
					
					if(!allLunasFromDB.isEmpty() || !allApoperisFromDB.isEmpty() || !allEclipsesFromDB.isEmpty()) {	
						
						for(MidsisonEntity midsison: midsisonsForDB) {
							
							for(LunasEntity luna: allLunasFromDB) {
	
								if ((luna.isNueva() || luna.isLlena()) && Math.abs(ChronoUnit.SECONDS.between(luna.getDate(), midsison.getDate())) <= 86164 ) {
									
									midsison.setLunaId(luna.getId());
									midsison.setNuevo(luna.isNueva());
									midsison.setLleno(luna.isLlena());
									
									if(luna.isSelecta() || luna.isInvertida()) {
										midsison.setSelecto(luna.isSelecta());
										midsison.setInvertido(luna.isInvertida());									
																																																															
									}
								}
							}
							
							for(ApogeosYPerigeosLunaEntity apoperi : allApoperisFromDB) {
								
								if (Math.abs(ChronoUnit.SECONDS.between(apoperi.getDate(), midsison.getDate())) <= 86164 ) {
									
									midsison.setAporico(apoperi.isEsApogeo());
									midsison.setPerico(apoperi.isEsPerigeo());
									midsison.setApoperiId(apoperi.getId());
									
									if(apoperi.isEsSelecto() || apoperi.isEsInvertido()) {
										midsison.setSelecto(apoperi.isEsSelecto());
										midsison.setInvertido(apoperi.isEsInvertido());									
																																																															
									}
								}
							}
							
							if(midsison.getApoperiId() != null && midsison.getLunaId() != null) {
								midsison.setApofasal(true);
							}
							
							if(midsison.isNuevo() || midsison.isLleno()) {
								
								
								for(EclipsesEntity eclipse : allEclipsesFromDB) {
		
									if ((eclipse.isDeSol()) || (eclipse.isDeLuna() && eclipse.isEsTotal()) && Math.abs(ChronoUnit.SECONDS.between(eclipse.getDate(), midsison.getDate())) <= 86164) {						
										midsison.setEclipse(true);
										midsison.setEclipseId(eclipse.getId());
									}
								}
							}
							
						}
					}
					else {
						System.out.println("No hay lunas o apoperis en base de datos");
					}
					
					this.midsisonRepository.saveAll(midsisonsForDB);
					
					if(allLunasFromDB.isEmpty() || allApoperisFromDB.isEmpty() || allEclipsesFromDB.isEmpty()) {
						resultado = "Midsisons poblados parcialmente: no hay lunas, apoperis o eclipses en base de datos.";
					}
					else {
						resultado = "Midsisons poblados correctamente.";
					}	
				}
			}
			else {
				System.out.println("Ya hay midsisons en base de datos");
				resultado = "Error al poblar los midsisons: ya hay midsisons en base de datos.";
			}
			
		}
		catch (Exception e) {
			System.out.println("No se ha podido conectar a la base de datos: " + e.getMessage());
			resultado = "Error al poblar los midsisons, checkear logs.";
		}
		
		System.out.println("Poblate midsisons finalizado.");		
		
		return resultado;

	}

}
