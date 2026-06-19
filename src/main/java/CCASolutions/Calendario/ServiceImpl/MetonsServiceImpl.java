package CCASolutions.Calendario.ServiceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
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
	private ApogeosYPerigeosLunaRepository apogeosYPerigeosRepository;
	
	public String poblateMetonos() {
		
		String resultado = "Metonos checkeados sin problema.";
		
		System.out.println("Iniciando evaluacion de metonos.");
		
		List<MetonsEntity> metonosEnBBDD = this.metonsRepository.findAll();
		
		if(metonosEnBBDD.isEmpty()) {
			List<LunasEntity> allLunas = this.lunasRepository.findAll();
			List<ApogeosYPerigeosLunaEntity> allApoperis = this.apogeosYPerigeosRepository.findAll();
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
							
							System.out.println("Nuevo métono fasal apopérico: " + nuevoMetono.getDate().toLocalDate());
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


}
