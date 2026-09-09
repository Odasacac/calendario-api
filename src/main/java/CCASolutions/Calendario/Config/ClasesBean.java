package CCASolutions.Calendario.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Services.DBService;
import CCASolutions.Calendario.Services.DatosService;
import jakarta.annotation.PostConstruct;

@Configuration
public class ClasesBean 
{
	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private DBService dbService;
	
	@Autowired
	private DatosService datosService;
	
	private final static boolean poblarBaseDeDatosAlArrancar = false;
	private static final boolean poblarTablasExtra = false;
    
    @PostConstruct
    void checkearBaseDeDatos() {
    	
    	List<DatosEntity> allDatos = this.datosRepository.findAll();
		boolean soloEstaLaPassword = allDatos.size() == 1 && allDatos.get(0).getConcepto().equals(this.datosService.getPWCode());
    	
    	if(allDatos.isEmpty()) {    
    		
    		System.out.println("Base de datos vacía.");  
    		System.out.println(this.dbService.poblateDBDesdeArranque(poblarBaseDeDatosAlArrancar, poblarTablasExtra)); 	
    	}
    	else if(soloEstaLaPassword) {
    		
    		System.out.println("Sólo está la adminPW en la base de datos, está pendiente poblar.");  
    	}
    	else {
    		
    		System.out.println("Base de datos poblada."); 
    	}
    }
}