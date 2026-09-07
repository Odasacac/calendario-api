package CCASolutions.Calendario.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.Entities.DatosEntity;
import CCASolutions.Calendario.Repositories.DatosRepository;
import CCASolutions.Calendario.Services.DatosService;

@Service
public class DatosServiceImpl implements DatosService {

	@Autowired
	private DatosRepository datosRepository;
	
	private final static String PW_CODE = "PW";
	private final static String PW_VALUE = "admin";
	
	private final static String API_LUNAR_FASES = "YLP";
	private final static String API_APOPERIS = "APG";
	private final static String API_SOES = "ASYEF";
	private final static String API_LUNAR_ECLIPSES = "LEPY";
	private final static String API_SOLAR_ECLIPSES = "SEPY";	
	
	public String getPWCode() {		
		return PW_CODE;
	}

	public String getApiLunarFases() {
		return API_LUNAR_FASES;
	}

	public String getApiApoperis() {
		return API_APOPERIS;
	}

	public String getApiSoes() {
		return API_SOES;
	}

	public String getApiLunarEclipses() {
		return API_LUNAR_ECLIPSES;
	}

	public String getApiSolarEclipses() {
		return API_SOLAR_ECLIPSES;
	}

	public String poblateSoloPassword() {
		
		String resultado = "La adminPW poblada en base de datos con éxito, queda pendiente poblar.";		
		
		try {
			
			DatosEntity passwordParaDB = this.createDato(PW_CODE, PW_VALUE);		
			this.datosRepository.save(passwordParaDB);		
		}
		catch(Exception e) {
			
			resultado = "Error poblando la adminPW en base de datos: " + e;
		}		
		
		return resultado;
	}
	
	public String poblateDatos() {
		
		String resultado = "Datos actualizados correctamente.";
		
		System.out.println("Actualizando los Datos.");
		
		List<DatosEntity> allDatos = this.datosRepository.findAll();
		List<DatosEntity> datosParaDB = new ArrayList<>();
		boolean soloEstaLaPassword = allDatos.size() == 1 && allDatos.get(0).getConcepto().equals(getPWCode());
		
		if(allDatos.isEmpty()) {
	
			datosParaDB.add(this.createDato(PW_CODE, PW_VALUE));
			datosParaDB.addAll(this.getDatos());
		}
		else if (soloEstaLaPassword) {
			
			datosParaDB.addAll(this.getDatos());
		}
		else {
			
			System.out.println("Ya hay datos en la base de datos.");
			resultado = "Error poblando los datos: ya hay datos en la base de datos.";
		}
		
		if(!datosParaDB.isEmpty()) {
			
			this.datosRepository.saveAll(datosParaDB);
		}
		
		System.out.println("Datos actualizados");
		return resultado;
	}	

	private List<DatosEntity> getDatos(){
		
		List<DatosEntity> datos = new ArrayList<>();
		
		datos.add(this.createDato(API_SOES, "https://opale.imcce.fr/api/v1/phenomena/equinoxessolstices/399?year={{YYYY}}&nbd={{NNNN}}"));
		datos.add(this.createDato(API_LUNAR_FASES, "https://opale.imcce.fr/api/v1/phenomena/moonphases?year={{YYYY}}"));
		datos.add(this.createDato(API_LUNAR_ECLIPSES, "https://opale.imcce.fr/api/v1/phenomena/eclipses/301/{{YYYY}}"));
		datos.add(this.createDato(API_SOLAR_ECLIPSES, "https://opale.imcce.fr/api/v1/phenomena/eclipses/10/{{YYYY}}"));
		datos.add(this.createDato(API_APOPERIS, "https://opale.imcce.fr/api/v1/phenomena/distances?date={{YYYY-MM-DD}}&nbd={{DDDD}}&bodies=399,301&calendar=gregorian"));						

		return datos;
	}
	
	private DatosEntity createDato(String concepto, String valor) {
		
		DatosEntity newDato = new DatosEntity();
		newDato.setConcepto(concepto);		
		
		if(newDato.getConcepto().equals(PW_CODE)) {
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			newDato.setValor(encoder.encode(valor));
		}
		else {
			
			newDato.setValor(valor);
		}
		
		return newDato;
	}

}
