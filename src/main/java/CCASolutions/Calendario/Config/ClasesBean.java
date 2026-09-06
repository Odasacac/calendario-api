package CCASolutions.Calendario.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import CCASolutions.Calendario.DTOs.PoblateDBDTO;
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
	
    @Bean
    BCryptPasswordEncoder passwordEncoder() 
	{
		return new BCryptPasswordEncoder();
    }
    
    @PostConstruct
    void construirBaseDeDatos() {
    	
    	DatosEntity dbPassword = this.datosRepository.findByConcepto(this.datosService.getPWCode());
    	
    	if(dbPassword == null) {    
    		
    		System.out.println("Base de datos vacía.");
    		PoblateDBDTO poblateDBDTO = new PoblateDBDTO(poblarBaseDeDatosAlArrancar, true);  
    		System.out.println(this.dbService.poblateDB(poblateDBDTO)); 
 		 	
    	}
    }
}