package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.SeasonDTO;
import CCASolutions.Calendario.Entities.SeasonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.SeasonsRepository;
import CCASolutions.Calendario.Services.SeasonsService;

@Service
public class SeasonsServiceImpl implements SeasonsService{

	@Autowired
	private SeasonsRepository seasonsRepository;
	

	public SeasonDTO getVAUSeason(LocalDate date, List<SolsticiosYEquinocciosEntity> allSoes) {
		
		SeasonDTO seasonDTO = new SeasonDTO();
		
		SolsticiosYEquinocciosEntity lastSoe  = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity nextSoe = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity actualSoe = null; //Por si hiciera falta saber el soe actual
		int startingSeason = 0;
		
		long diasMinimosDeDiferenciaEntreSoePasadoYDate = Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreSoeFuturoYDate = Long.MAX_VALUE;
		boolean caeEnSoe = false;
		
		for(int i = 0; i<allSoes.size() && !caeEnSoe ;i++) {
			
			SolsticiosYEquinocciosEntity soe = allSoes.get(i);
			
			if(soe.getDate().toLocalDate().isBefore(date)) {
				
				long diasDeDiferenciaEntreSoePasadoYDate = ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date);
				
				if(diasDeDiferenciaEntreSoePasadoYDate < diasMinimosDeDiferenciaEntreSoePasadoYDate) {
					diasMinimosDeDiferenciaEntreSoePasadoYDate = diasDeDiferenciaEntreSoePasadoYDate;
					lastSoe=soe;
				}
			}
			else if(soe.getDate().toLocalDate().isEqual(date)) {
				actualSoe = soe;
				caeEnSoe = true;
			}
			else if(soe.getDate().toLocalDate().isAfter(date)){
				long diasDeDiferenciaEntreSoeFuturoYDate = ChronoUnit.DAYS.between(date, soe.getDate().toLocalDate());
				
				if(diasDeDiferenciaEntreSoeFuturoYDate < diasMinimosDeDiferenciaEntreSoeFuturoYDate) {
					diasMinimosDeDiferenciaEntreSoeFuturoYDate = diasDeDiferenciaEntreSoeFuturoYDate;
					nextSoe =soe;
					
				}
			}
		}
		
		
		if(lastSoe != null && nextSoe != null && !caeEnSoe) {
			
			String surname = "";
			
			startingSeason = lastSoe.getStartingSeason();			

			LocalDate midSeasonDate = lastSoe.getDate().plusSeconds((ChronoUnit.SECONDS.between(lastSoe.getDate(), nextSoe.getDate()))/2).toLocalDate();
		
			if(date.isBefore(midSeasonDate)) {
				seasonDTO.setPreMidsison(true);
				surname="iniciante";
			}
			else if(date.isEqual(midSeasonDate)) {
				seasonDTO.setMidsisonDay(true);
				surname="cenítico";
			}
			else if(date.isAfter(midSeasonDate)) {
				seasonDTO.setPostMidsison(true);
				surname="terminante";
			}
			
			seasonDTO.setSurname(surname);				
		}
	
		seasonDTO.setName(this.seasonsRepository.findBySeasonOfTheYear(startingSeason).getName());
		
		return seasonDTO;
	}


	public String poblateSeasons() {
		
		System.out.println("Actualizando las Estaciones.");
		
		String resultado = "Estaciones actualizadas correctamente.";
		
		List<SeasonsEntity> allSeasons = this.seasonsRepository.findAll();
		
		if(allSeasons.isEmpty()) {
			
			List<SeasonsEntity> seasonsParaDDB = new ArrayList<>();
			
			seasonsParaDDB.add(this.createSeason("-", 0));
			seasonsParaDDB.add(this.createSeason("Invierno", 1));
			seasonsParaDDB.add(this.createSeason("Primavera", 2));
			seasonsParaDDB.add(this.createSeason("Verano", 3));
			seasonsParaDDB.add(this.createSeason("Otoño", 4));
			
			
			this.seasonsRepository.saveAll(seasonsParaDDB);
			
		}
		else {
			System.out.println("Ya hay estaciones en la base de datos.");
			resultado = "Error al actualizar las estaciones: ya hay estaciones en la base de datos.";
		}
		System.out.println("Estaciones actualizadas");
		return resultado;
	}
	
	private SeasonsEntity createSeason(String name, int seasonOfTheYear) {
		
		SeasonsEntity newSeason = new SeasonsEntity();
		newSeason.setName(name);
		newSeason.setSeasonOfTheYear(seasonOfTheYear);
		
		return newSeason;
	}
	
}
