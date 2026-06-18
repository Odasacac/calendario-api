package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.MetonosApopericosEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.ApogeosYPerigeosLunaRepository;
import CCASolutions.Calendario.Repositories.MetonosApopericosRepository;
import CCASolutions.Calendario.Repositories.SolsticiosYEquinocciosRepository;
import CCASolutions.Calendario.Services.MetonosApopericosService;

@Service
public class MetonosApopericosServiceImpl implements MetonosApopericosService {

	@Autowired
	private MetonosApopericosRepository metonosApopericosRepository;
	
	@Autowired
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosLunaRepository;
	
	@Autowired
	private SolsticiosYEquinocciosRepository solsticiosYEquinocciosRepository;

	
	public String checkMetonosApoViaDB() {
	
		String resultado = "Metonos apopericos checkeados sin problema.";
		System.out.println("Iniciando evaluacion de metonos apopericos.");
		
		List<MetonosApopericosEntity> metonosApopericosEnBBDD = this.metonosApopericosRepository.findAll();
		
		if(metonosApopericosEnBBDD.isEmpty()) {
			
			List<ApogeosYPerigeosLunaEntity> allApoperis = this.apogeosYPerigeosLunaRepository.findAll();
			List<SolsticiosYEquinocciosEntity> allSoes = this.solsticiosYEquinocciosRepository.findAll();
			
			if(!allApoperis.isEmpty() && ! allSoes.isEmpty()) {
				
				List<MetonosApopericosEntity> metonosParaDB = new ArrayList<>();
				for(SolsticiosYEquinocciosEntity soe : allSoes) {
					
					for (ApogeosYPerigeosLunaEntity apoperi : allApoperis) {
						
						if((apoperi.isEsApogeo() || apoperi.isEsPerigeo()) && Math.abs(ChronoUnit.SECONDS.between(apoperi.getDate(), soe.getDate())) <= 86164) {
							
							MetonosApopericosEntity metonoParaDB = new MetonosApopericosEntity();
							
							metonoParaDB.setDate(soe.getDate());
							metonoParaDB.setApoperiId(apoperi.getId());
							metonoParaDB.setSoeId(soe.getId());
							
							metonoParaDB.setApogeo(apoperi.isEsApogeo());
							metonoParaDB.setPerigeo(apoperi.isEsPerigeo());
							
							metonoParaDB.setInvernal(soe.isSolsticioInvierno());
							metonoParaDB.setPrimaveral(soe.isEquinoccioPrimavera());
							metonoParaDB.setEstival(soe.isSolsticioVerano());
							metonoParaDB.setOtonyal(soe.isEquinoccioOtonyo());
							
							metonoParaDB.setSelecto(apoperi.isEsSelecto());
							metonoParaDB.setInvertido(apoperi.isEsInvertido());
							
							metonosParaDB.add(metonoParaDB);
							
							System.out.println("Nuevo métono encontrado: " + metonoParaDB.getDate());
						}
					}
				}
				this.metonosApopericosRepository.saveAll(metonosParaDB);
				System.out.println("Evaluacion de métonos apopéricosfinalizada.");
				
			}
			else {
				if(allApoperis.isEmpty()) {
					
					System.out.println("No hay apoperis en la base de datos.");
					resultado = "Error al chequear metonos apopéricos, chequear logs,";
				}
				else if (allSoes.isEmpty()){
					
					System.out.println("No hay soes en la base de datos.");
					resultado = "Error al chequear metonos apopéricos, chequear logs,";
				}
			}
		}
		else{
			System.out.println("Ya hay metonos apopéricos en la BBDD");
			resultado="Error a la hora de actualizar los metonos apopéricos, chequear logs.";
		}
		
		return resultado;
	}

	
}
