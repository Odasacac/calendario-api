package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.MidsisonEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.MidsisonRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MidsisonService;

@Service
public class MidsisonServiceImpl implements MidsisonService{

	@Autowired
	private MidsisonRepository midsisonRepository;
	
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
					
					this.midsisonRepository.saveAll(midsisonsForDB);
					resultado = "Midsisons poblados correctamente.";
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
