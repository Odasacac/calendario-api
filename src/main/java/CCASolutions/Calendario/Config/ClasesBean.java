package CCASolutions.Calendario.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Enums.ModoPoblacionEnum;
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
	
	private static final ModoPoblacionEnum MODO_POBLACION = ModoPoblacionEnum.DESDE_CERO_SIN_TABLAS_EXTRA;

    
    @PostConstruct
    void checkearBaseDeDatos() {
    	
    	List<DatosEntity> allDatos = this.datosRepository.findAll();
		boolean soloEstaLaPassword = allDatos.size() == 1 && allDatos.get(0).getConcepto().equals(this.datosService.getPWCode());
		
    	if(allDatos.isEmpty()) {    
    		
        	System.out.println(this.dbService.poblateDBDesdeArranque(MODO_POBLACION)); 		
    	}
    	else if(soloEstaLaPassword) {
    		
    		System.out.println("Sólo está la adminPW en la base de datos, está pendiente poblar.");  
    	}
    	else {
    		
    		System.out.println("La base de datos está poblada."); 
    	}
    }
}