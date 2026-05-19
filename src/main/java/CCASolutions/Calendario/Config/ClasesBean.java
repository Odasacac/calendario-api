package CCASolutions.Calendario.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import jakarta.annotation.PostConstruct;

@Configuration
public class ClasesBean 
{
	@Autowired
	private DatosRepository datosRepository;
	
    @Bean
    BCryptPasswordEncoder passwordEncoder() 
	{
		return new BCryptPasswordEncoder();
    }
    
    @PostConstruct
    void setAdminPassword() {
    	DatosEntity dbPassword = this.datosRepository.findByConcepto("PW");
    	
    	if(dbPassword == null) {
    		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    		dbPassword = new DatosEntity();
    		dbPassword.setConcepto("PW");
    		dbPassword.setValor(encoder.encode("admintest"));
    		this.datosRepository.save(dbPassword);
    	}
    }
}