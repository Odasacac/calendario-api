package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Services.DatosService;

@Service
public class DatosServiceImpl implements DatosService {

	@Autowired
	private DatosRepository datosRepository;

	public String poblateDatos() {
		String resultado = "Datos actualizados correctamente.";
		
		System.out.println("Actualizando los Datos.");
		
		List<DatosEntity> allDatos = this.datosRepository.findAll();
		
		if(allDatos.size()==1 && allDatos.get(0).getConcepto().equals("PW")) // Correspondiente a la contraseña del administrador
		{
			
			List<DatosEntity> datosParaDDB = new ArrayList<>();
			
			datosParaDDB.add(this.createDato("ASYEF", "https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}"));
			datosParaDDB.add(this.createDato("YLP", "https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}"));
			datosParaDDB.add(this.createDato("LEPY", "https://opale.imcce.fr/api/v1/phenomena/eclipses/301/{{YYYY}}"));
			datosParaDDB.add(this.createDato("SEPY", "https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}"));
			
			this.datosRepository.saveAll(datosParaDDB);
			
		}
		else {
			System.out.println("Ya hay datos en la base de datos.");
			resultado = "Error al actualizar los datos, checkear logs.";
		}
		System.out.println("Datos actualizados");
		return resultado;
	}
	
	private DatosEntity createDato(String concepto, String valor) {
		
		DatosEntity newDato = new DatosEntity();
		newDato.setConcepto(concepto);
		newDato.setValor(valor);
		
		return newDato;
	}

}
