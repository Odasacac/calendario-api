package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.EclipenoINDTO;
import CCASolutions.Calendario.DTOs.EclipenoSelectoDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Repositories.EclipenosRepository;
import CCASolutions.Calendario.Repositories.EclipsesRepository;
import CCASolutions.Calendario.Repositories.MetonsRepository;
import CCASolutions.Calendario.Services.EclipenosService;

@Service
public class EclipenosServiceImpl implements EclipenosService{

	@Autowired
	private MetonsRepository metonsRepository;
	
	@Autowired
	private EclipenosRepository eclipenosRepository;
	
	@Autowired
	private EclipsesRepository eclipsesRepository;
	
	public EclipenosEntity getLastEclipenoIN(List<EclipenosEntity> allEclipenos, LocalDate date) {
		
		EclipenosEntity lastEclipenoIN = null;
		
		long diasMinimosDeDiferenciaEntreEclipenoYDate =Long.MAX_VALUE;		
		for(EclipenosEntity eclipeno : allEclipenos) {
					
			if(!eclipeno.getDate().toLocalDate().isAfter(date) && eclipeno.isInvernal() && eclipeno.isNuevo() && (eclipeno.isEsAnular() || eclipeno.isEsTotal())) {	
				
				long diasDeDiferenciaEntreEclipenoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreEclipenoYDate < diasMinimosDeDiferenciaEntreEclipenoYDate) {
					lastEclipenoIN = new EclipenosEntity();
					diasMinimosDeDiferenciaEntreEclipenoYDate = diasDeDiferenciaEntreEclipenoYDate;
					lastEclipenoIN = eclipeno;
				}
			}
		}
		
		return lastEclipenoIN;
	}
	
	
	public EclipenosEntity getLastEclipenoInvernalApofasalRemoto(List<EclipenosEntity> allEclipenos, LocalDate date) {
		
		/* InvernalApofasalRemoto
		
			Invernal = solsticio de invierno
			Apofasal = luna y apoperi ambos a menos de un dia sideral
			Remoto = Luna nueva y apogeo
		
		*/
		EclipenosEntity InvernalApofasalRemoto = null;
		
		long diasMinimosDeDiferenciaEntreEclipenoYDate =Long.MAX_VALUE;		
		for(EclipenosEntity eclipeno : allEclipenos) {
					
			if(!eclipeno.getDate().toLocalDate().isAfter(date) && eclipeno.isInvernal() && eclipeno.isNuevo() && eclipeno.isApofasal() && eclipeno.isSelecto() && (eclipeno.isEsAnular() || eclipeno.isEsTotal() )) {	
				
				long diasDeDiferenciaEntreEclipenoYDate = ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreEclipenoYDate < diasMinimosDeDiferenciaEntreEclipenoYDate) {
					InvernalApofasalRemoto = new EclipenosEntity();
					diasMinimosDeDiferenciaEntreEclipenoYDate = diasDeDiferenciaEntreEclipenoYDate;
					InvernalApofasalRemoto = eclipeno;
				}
			}
		}
		
		return InvernalApofasalRemoto;
	}
	
	public EclipenoSelectoDTO getVAUEclipenoSelecto(EclipenosEntity lastEclipenoSelecto, LocalDate date) {
		
		EclipenoSelectoDTO eclipenoSelectoVAU = new EclipenoSelectoDTO();
		
		eclipenoSelectoVAU.setDaysSinceCurrentEclipenoSelectoIN("hace " + ChronoUnit.DAYS.between(lastEclipenoSelecto.getDate().toLocalDate(), date) + " días");
		eclipenoSelectoVAU.setEclipenoINSelectoDay(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date));
		
		
		return eclipenoSelectoVAU;
	}
	
	public EclipenoINDTO getVAUEclipeno(List<EclipenosEntity> allEclipenos, EclipenosEntity lastEclipenoSelecto, LocalDate date) {
		
		EclipenoINDTO eclipenoVAU = new EclipenoINDTO();
		
		
		if(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)){
			
			eclipenoVAU.setEclipenoINDay(true);
			eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(0);
			eclipenoVAU.setNumberOfEclipenoIN(0);
			eclipenoVAU.setYearOfCurrentEclipenoIN(lastEclipenoSelecto.getYear());
			
		}
		else {
			
			List<EclipenosEntity> eclipenosIN = new ArrayList<>();
			
			for(EclipenosEntity eclipeno : allEclipenos) {
				
				if(eclipeno.isInvernal() && eclipeno.isNuevo() && !eclipeno.getDate().isBefore(lastEclipenoSelecto.getDate()) && !eclipeno.getDate().toLocalDate().isAfter(date)) {
					eclipenosIN.add(eclipeno);
				}
			}
			
			if(!eclipenosIN.isEmpty()) {
				eclipenoVAU.setYearOfCurrentEclipenoIN(eclipenosIN.get(0).getYear());
				eclipenoVAU.setEclipenoINDay(eclipenosIN.get(0).getDate().toLocalDate().isEqual(date));
				
				int eclipenosDesdeElLastEclipenSelecto = (eclipenosIN.size()-1); // -1 porque incluye el del eclipeno
				
				// No se suma un eclipeno hasta que pase el dia del eclipeno, pero si es el dia de eclipeno no se resta, que se ha restado antes
				
				if(eclipenoVAU.isEclipenoINDay() && !lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)) {
					
					eclipenosDesdeElLastEclipenSelecto = eclipenosDesdeElLastEclipenSelecto-1;
				}
				
				eclipenoVAU.setEclipenosINSinceLastEclipenoINSelecto(eclipenosDesdeElLastEclipenSelecto);
				int yearOfTheEclipeno = eclipenosDesdeElLastEclipenSelecto +1;
				
				if(lastEclipenoSelecto.getDate().toLocalDate().isEqual(date)) { //Si es el dia del eclipeno selecto, no estamos en ningun eclipeno
					yearOfTheEclipeno= yearOfTheEclipeno-1;
				}
				eclipenoVAU.setNumberOfEclipenoIN(yearOfTheEclipeno);
				
				if(eclipenosIN.get(0).isInvertido() && eclipenosIN.get(0).isApofasal() && yearOfTheEclipeno != 0 && !eclipenoVAU.isEclipenoINDay()) {
					eclipenoVAU.setLastEclipenoSurname("(Invertido)");
				}
				else if(eclipenosIN.get(0).isSelecto() && eclipenosIN.get(0).isApofasal() && yearOfTheEclipeno != 0 && !eclipenoVAU.isEclipenoINDay()) {
					eclipenoVAU.setLastEclipenoSurname("(Selecto)");
				}
			}
			
		}
		
		return eclipenoVAU;
	}

	
	public String poblateEclipenos() {
		
		String resultado = "Eclipenos actualizados sin problema.";		
		List<EclipenosEntity> allEclipenos = this.eclipenosRepository.findAll();
		List<MetonsEntity> metonos = this.metonsRepository.findAll();
		System.out.println("Iniciando actualizar eclipenos");
		if(allEclipenos.isEmpty() && !metonos.isEmpty()) {
			try {
				
				for (MetonsEntity meton : metonos) {
					
					List<EclipsesEntity> eclipses = this.eclipsesRepository.findByYear(meton.getYear());
					
					if(meton.isFasal()) {
						
						for(EclipsesEntity eclipse : eclipses) {
							
							long segundosDeDiferencia = Math.abs(ChronoUnit.SECONDS.between(eclipse.getDate(), meton.getDate()));
							
							if(segundosDeDiferencia <= 86164) {
								System.out.println("Actualizando los eclípenos del anyo: " + meton.getYear());
								EclipenosEntity eclipeno = new EclipenosEntity();
								
								eclipeno.setDate(meton.getDate());
								eclipeno.setYear(meton.getYear());
								
								eclipeno.setInvernal(meton.isInvernal());
								eclipeno.setPrimaveral(meton.isPrimaveral());
								eclipeno.setEstival(meton.isEstival());
								eclipeno.setOtonyal(meton.isOtonyal());
								
								eclipeno.setLleno(meton.isLleno());
								eclipeno.setNuevo(meton.isNuevo());
								
								eclipeno.setEsAnular(eclipse.isEsAnular());
								eclipeno.setEsParcial(eclipse.isEsParcial());
								eclipeno.setEsTotal(eclipse.isEsTotal());
								eclipeno.setEsPenumbral(eclipse.isEsPenumbral());
								eclipeno.setEsHibrido(eclipse.isEsHibrido());	
								
								eclipeno.setEclipseId(eclipse.getId());
								eclipeno.setMetonoId(meton.getId());
								eclipeno.setInvertido(meton.isInvertido());
								eclipeno.setSelecto(meton.isSelecto());
								
								eclipeno.setApofasal(meton.isApofasal());
														
								this.eclipenosRepository.save(eclipeno);
							}
						}
					}
				}	
					
			}
			catch (Exception e) {
				System.out.println("Error al evaluar los eclipenos: " + e);
					resultado = "Error al evaluar los eclipenos, revisar logs";
			}
				
		}
		else {
			if(!allEclipenos.isEmpty()) {
				System.out.println("Ya hay eclípenos en la base de datos.");
				resultado = "Error al actualizar los eclípenos: ya hay eclípenos en la base de datos.";
			}
			else if(metonos.isEmpty()){
				System.out.println("No hay métonos en la base de datos.");
				resultado = "Error al actualizar los eclípenos: no hay métonos en la base de datos.";
			}
		}

		

		return resultado;
	}

}
