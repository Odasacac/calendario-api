package CCASolutions.Calendario.ServiceImpl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Services.EclipenosService;

@Service
public class EclipenosServiceImpl implements EclipenosService{

	@Autowired
	private DatosRepository datosRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	public String poblateEclipenos() {
		
		String resultado = "Eclipenos actualizados sin problema.";
		
		List<DatosEntity> urls = datosRepository.findByConceptoIn(Arrays.asList("LEPY", "SEPY"));	
		
		String apiEclipsesLunares = "";
		String apiEclipsesSolares = "";
		
		for (DatosEntity url : urls) 
		{
			switch (url.getConcepto()) {
			
				case "LEPY":					
					apiEclipsesLunares = url.getValor();
					break;
				
				case "SEPY":					
					apiEclipsesSolares = url.getValor();
					break;					
			}
		}
		
		if(apiEclipsesLunares != null && apiEclipsesSolares != null) {
			
			try {
				
				for (int i = 0; i <= 2100; i++) {
					
					System.out.println("Actualizando los eclípenos del anyo: " + i);
				}
			}
			catch (Exception e)
			{
				System.out.println("Error al evaluar los eclipenos: " + e);
				resultado = "Error al evaluar los eclipenos, revisar logs";
			}
			
		}
		else {
			
			System.out.println("La URL de la API para obtener los eclipses es nula.");
		}
		
		return resultado;
	}

}
