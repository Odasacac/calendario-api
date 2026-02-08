package CCASolutions.Calendario.Cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Services.LunasService;
import CCASolutions.Calendario.Services.SolsticiosYEquinocciosService;

@Service
@EnableScheduling
public class CronUpdateDatabase {

	@Autowired
	private LunasService lunasService;
	
	@Autowired
	private SolsticiosYEquinocciosService solsticiosYEquinocciosService;
	
	@Scheduled(cron = "0 1 0 1 1 ?")
	public void updateLunas() {
		
		System.out.println("Iniciando la actualización de lunas.");
		try {

			System.out.println(this.lunasService.updateLunas());
			
		}
		catch (Exception e) {
			
			System.out.println("Error al actualizar lunas: " + e);
		}
	}
	
	
	@Scheduled(cron = "0 3 0 1 1 ?")
	public void updateSolsticiosYEquinoccios() {
		
		System.out.println("Iniciando la actualización de solsticios y equinoccios.");
		try {
			
			System.out.println(this.solsticiosYEquinocciosService.updateSolsticiosYEquinoccios());
			
		}
		catch (Exception e) {
			
			System.out.println("Error al actualizar SYE: " + e);
		}

	}
	
}
