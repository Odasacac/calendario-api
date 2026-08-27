package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CCASolutions.Calendario.DTOs.DatosCosmicosParaVAUDTO;
import CCASolutions.Calendario.DTOs.FestividadesDTO;
import CCASolutions.Calendario.DTOs.MinimaFestividadesDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.FestividadesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.FestividadesRepository;
import CCASolutions.Calendario.Services.FestividadesService;

@Service
public class FestividadesServiceImpl implements FestividadesService {

	@Autowired
	private FestividadesRepository festividadesRepository;
	
	
	private static final String CAMBIO_DE_ECLIPENO_IAR_CODE = "CEAR";	
	
	private static final String CAMBIO_DE_ECLIPENO_CODE = "CE";
	private static final String CAMBIO_DE_METONO_IAR_CODE = "CMAR";
	
	private static final String CAMBIO_DE_METONO_IN_CODE = "CMF";
	private static final String CAMBIO_DE_METONO_IA_CODE = "CMA";
	
	private static final String CAMBIO_DE_ANYO_CODE = "CA";
	private static final String INICIO_ANYO_CODE = "IA";
	private static final String MIDISSON_INVERNAL_CODE = "MSI";	
	
	private static final String BIENVENIDA_PRIMAVERA_CODE = "BP";
	private static final String MIDISSON_PRIMAVERAL_CODE = "MSP";
	
	private static final String MITAD_ANYO_CODE = "MA";	
	private static final String MIDISSON_ESTIVAL_CODE = "MSE";
	
	private static final String DESPEDIDA_VERANO_CODE = "DV";
	private static final String MIDISSON_OTONYAL_CODE = "MSO";

	private static final String ENTRADA_OTONYO_CODE = "EO";
	private static final String DESPEDIDA_ANYO_CODE = "DA";	

	private static final String CAMBIO_DE_APONOVO_CODE = "LA";
	private static final String MIDISSON_APONOVAL_CODE = "MAP";

	
	
	public FestividadesDTO getFestividades(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
		
		FestividadesDTO festividades = new FestividadesDTO();
		
		List<MinimaFestividadesDTO> festividadesObtenidasDTO = this.getFestividadesDesdeFecha(date, datosCosmicosParaVAUDTO);
		List<FestividadesEntity> festividadesEntities = this.festividadesRepository.findAll();
		
		List<MinimaFestividadesDTO> festividadesActuales = new ArrayList<>();
		List<MinimaFestividadesDTO> festividadesPasadas = new ArrayList<>();
		List<MinimaFestividadesDTO> festividadesFuturas = new ArrayList<>();
		
		for(MinimaFestividadesDTO festividad : festividadesObtenidasDTO) {
			
			if(festividad.getDiasDeDiferenciaConDate() == 0) {
				
				festividadesActuales.add(festividad);
			}
			else if(festividad.getDate().toLocalDate().isAfter(date)) {
				
				festividadesFuturas.add(festividad);
			}
			else if(festividad.getDate().toLocalDate().isBefore(date)) {
				
				festividadesPasadas.add(festividad);
			}
		}
		
		festividades.setFestividadActual(this.getFestividadActual(festividadesEntities, festividadesActuales));
		festividades.setFestividadAnterior(this.getFestividadAnterior(festividadesEntities, festividadesPasadas));
		festividades.setFestividadProxima(this.getFestividadProxima(festividadesEntities, festividadesFuturas));		
		
		return festividades;		
	}		
	
	private String getFestividadActual(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesActuales) {

		String festividadActual = "";
		
	    if (!festividadesActuales.isEmpty()) {

	    	boolean hayMidsison = false;
	    	boolean hayAponovo = false;

	    	for (MinimaFestividadesDTO festividad : festividadesActuales) {

	    		String code = festividad.getCode();

	    		if (MIDISSON_INVERNAL_CODE.equals(code) || MIDISSON_PRIMAVERAL_CODE.equals(code) || MIDISSON_ESTIVAL_CODE.equals(code) || MIDISSON_OTONYAL_CODE.equals(code)) {
	        	
	    			hayMidsison = true;
	    		}

	    		if (CAMBIO_DE_APONOVO_CODE.equals(code)) {
	    			
	    			hayAponovo = true;
	    		}
	    	}

	    	if (hayMidsison && hayAponovo) {
	    		
	    		festividadActual = getNombreFestividad(festividadesEntities, MIDISSON_APONOVAL_CODE);
	    	}


	    	String[] prioridad = {
	    		MIDISSON_APONOVAL_CODE,
	    		CAMBIO_DE_APONOVO_CODE,
	    		DESPEDIDA_ANYO_CODE,
	    		ENTRADA_OTONYO_CODE,
	    		MIDISSON_OTONYAL_CODE,
	    		DESPEDIDA_VERANO_CODE,
	    		MIDISSON_ESTIVAL_CODE,
	    		MITAD_ANYO_CODE,
	    		MIDISSON_PRIMAVERAL_CODE,
	    		BIENVENIDA_PRIMAVERA_CODE,
	    		MIDISSON_INVERNAL_CODE,
	    		INICIO_ANYO_CODE,
	    		CAMBIO_DE_ANYO_CODE,
	    		CAMBIO_DE_METONO_IA_CODE,
	    		CAMBIO_DE_METONO_IN_CODE,
	    		CAMBIO_DE_ECLIPENO_CODE,
	    		CAMBIO_DE_METONO_IAR_CODE,
	    		CAMBIO_DE_ECLIPENO_IAR_CODE
	    	};

	    	for (String codigoPrioritario : prioridad) {

	    		for (MinimaFestividadesDTO festividad : festividadesActuales) {

	    			if (codigoPrioritario.equals(festividad.getCode())) {

	    				festividadActual = getNombreFestividad(festividadesEntities, codigoPrioritario);
	    			}
	    		}
	    	}
	    }
	    
	    return festividadActual;
	}
	
	private String getNombreFestividad(
	        List<FestividadesEntity> festividadesEntities,
	        String code) {

	    for (FestividadesEntity entity : festividadesEntities) {

	        if (code.equals(entity.getCode())) {
	            return entity.getNombre();
	        }
	    }

	    return "";
	}
	
	private String getFestividadAnterior(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesPasadas) {
		
		MinimaFestividadesDTO festividadMasCercanaDTO = this.getFestividadParaGetName(festividadesPasadas);
		String festividadAnterior = this.getFestividadName(festividadMasCercanaDTO, festividadesEntities).replace("{{TTTT}}", "hace");		
		
		return festividadAnterior;
	}

	private String getFestividadProxima(List<FestividadesEntity> festividadesEntities, List<MinimaFestividadesDTO> festividadesFuturas) {
	
		MinimaFestividadesDTO festividadMasCercanaDTO = this.getFestividadParaGetName(festividadesFuturas);
		String festividadProxima = this.getFestividadName(festividadMasCercanaDTO, festividadesEntities).replace("{{TTTT}}", "dentro de");		
		
		return festividadProxima;
	}
	
	private String getFestividadName(MinimaFestividadesDTO festividadMasCercanaDTO, List<FestividadesEntity> festividadesEntities) {
		
		String name = "";
		String dias = "días";
		
		for(FestividadesEntity entity : festividadesEntities) {
				
			if(entity.getCode().equals(festividadMasCercanaDTO.getCode())) {
					
				if(festividadMasCercanaDTO.getDiasDeDiferenciaConDate() == 1) {
					dias = "día";
				}
				name = entity.getNombre() + " {{TTTT}} " + festividadMasCercanaDTO.getDiasDeDiferenciaConDate() + " " + dias;
			}		
		}

		return name;
	}
	
	private MinimaFestividadesDTO getFestividadParaGetName(List<MinimaFestividadesDTO> festividadesFuturas) {
		
		MinimaFestividadesDTO festividadParaGetName = new MinimaFestividadesDTO();
		
		long diasMinimosEntreDateYFestividad = Long.MAX_VALUE;
		
		for(MinimaFestividadesDTO festividad : festividadesFuturas) {
			
			if(festividad.getDiasDeDiferenciaConDate() < diasMinimosEntreDateYFestividad) {
				
				diasMinimosEntreDateYFestividad = festividad.getDiasDeDiferenciaConDate();
				festividadParaGetName = festividad;				
				
			}
		}
		
		return festividadParaGetName;
	}
	
	private List<MinimaFestividadesDTO> getFestividadesDesdeFecha(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {
		
		List<MinimaFestividadesDTO> festividadesObtenidasDTO = new ArrayList<>();

		
		// 1 - Cambio de eclipeno 
		MinimaFestividadesDTO cambioDeEclipeno = new MinimaFestividadesDTO();
		cambioDeEclipeno.setCode(CAMBIO_DE_ECLIPENO_CODE);		
		long diasMinimosDeDiferenciaEntreCEYDate = Long.MAX_VALUE;
		boolean esHoyCE = false;
		
		MinimaFestividadesDTO cambioDeEclipenoIAR = new MinimaFestividadesDTO();
		cambioDeEclipenoIAR.setCode(CAMBIO_DE_ECLIPENO_IAR_CODE);		
		long diasMinimosDeDiferenciaEntreCEARYDate = Long.MAX_VALUE;
		boolean esHoyCEAR = false;
		
		for(int i = 0; i<datosCosmicosParaVAUDTO.getEclipenos().size(); i++) {
			
			EclipenosEntity eclipeno = datosCosmicosParaVAUDTO.getEclipenos().get(i);
			
			if(eclipeno.isInvernal() && eclipeno.isNuevo()) {
				
				if(eclipeno.getDate().toLocalDate().isEqual(date)) {
					
					if(eclipeno.isApofasal() && eclipeno.isSelecto()) {
						cambioDeEclipenoIAR.setDate(eclipeno.getDate());
						cambioDeEclipenoIAR.setDiasDeDiferenciaConDate(0);
						esHoyCEAR=true;
					}
					else {
						cambioDeEclipeno.setDate(eclipeno.getDate());
						cambioDeEclipeno.setDiasDeDiferenciaConDate(0);
						esHoyCE=true;
					}
					
				}
				else {
					
					long diasDeDiferenciaEntreCEYDate = Math.abs(ChronoUnit.DAYS.between(eclipeno.getDate().toLocalDate(), date));
					
					if(eclipeno.isApofasal() && eclipeno.isSelecto() && !esHoyCEAR) {
						if(diasDeDiferenciaEntreCEYDate < diasMinimosDeDiferenciaEntreCEARYDate) {
							diasMinimosDeDiferenciaEntreCEARYDate = diasDeDiferenciaEntreCEYDate;
							cambioDeEclipenoIAR.setDate(eclipeno.getDate());
							cambioDeEclipenoIAR.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCEARYDate);
						}
					}
					else if(diasDeDiferenciaEntreCEYDate < diasMinimosDeDiferenciaEntreCEYDate && !esHoyCE) {
						diasMinimosDeDiferenciaEntreCEYDate = diasDeDiferenciaEntreCEYDate;
						cambioDeEclipeno.setDate(eclipeno.getDate());
						cambioDeEclipeno.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCEYDate);
					}
				}
			}
		}
		
		festividadesObtenidasDTO.add(cambioDeEclipeno);
		festividadesObtenidasDTO.add(cambioDeEclipenoIAR);
		
		
		
		// 2 - Cambio de metono fasal y aporico
		MinimaFestividadesDTO cambioDeMetonoIN = new MinimaFestividadesDTO();
		cambioDeMetonoIN.setCode(CAMBIO_DE_METONO_IN_CODE);		
		long diasMinimosDeDiferenciaEntreCMINYDate = Long.MAX_VALUE;
		boolean esHoyCMN = false;
		
		MinimaFestividadesDTO cambioDeMetonoIA = new MinimaFestividadesDTO();
		cambioDeMetonoIA.setCode(CAMBIO_DE_METONO_IA_CODE);		
		long diasMinimosDeDiferenciaEntreCMIAYDate = Long.MAX_VALUE;
		boolean esHoyCMA = false;
		
		MinimaFestividadesDTO cambioDeMetonoIAR = new MinimaFestividadesDTO();
		cambioDeMetonoIAR.setCode(CAMBIO_DE_METONO_IAR_CODE);		
		long diasMinimosDeDiferenciaEntreCMIARYDate = Long.MAX_VALUE;
		boolean esHoyCMAR = false;
		
		for(int i = 0; i<datosCosmicosParaVAUDTO.getMetons().size(); i++) {
			
			MetonsEntity metono = datosCosmicosParaVAUDTO.getMetons().get(i);
			
			if(metono.isInvernal()) {
				
				if(metono.isNuevo()) {
					
					if(metono.getDate().toLocalDate().isEqual(date)) {
						
						if(metono.isApofasal() && metono.isSelecto()) {
							cambioDeMetonoIAR.setDate(metono.getDate());
							cambioDeMetonoIAR.setDiasDeDiferenciaConDate(0);
							esHoyCMAR=true;
						}
						else {
							cambioDeMetonoIN.setDate(metono.getDate());
							cambioDeMetonoIN.setDiasDeDiferenciaConDate(0);
							esHoyCMN=true;
						}
						
					}
					else {
						
						long diasDeDiferenciaEntrCMYDate = Math.abs(ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date));
						
						if(metono.isApofasal() && metono.isSelecto() && !esHoyCMAR) {
												
							if(diasDeDiferenciaEntrCMYDate < diasMinimosDeDiferenciaEntreCMIARYDate) {
								diasMinimosDeDiferenciaEntreCMIARYDate = diasDeDiferenciaEntrCMYDate;
								cambioDeMetonoIAR.setDate(metono.getDate());
								cambioDeMetonoIAR.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCMIARYDate);
							}
						}
						else if(diasDeDiferenciaEntrCMYDate < diasMinimosDeDiferenciaEntreCMINYDate && !esHoyCMN) {
								diasMinimosDeDiferenciaEntreCMINYDate = diasDeDiferenciaEntrCMYDate;
								cambioDeMetonoIN.setDate(metono.getDate());
								cambioDeMetonoIN.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCMINYDate);
						}
					}								
				}
				else if (metono.isAporico()) {
					
					if(metono.getDate().toLocalDate().isEqual(date)) {
						
						if(metono.isApofasal() && metono.isSelecto()) {
							cambioDeMetonoIAR.setDate(metono.getDate());
							cambioDeMetonoIAR.setDiasDeDiferenciaConDate(0);
							esHoyCMAR=true;
						}
						else {
							cambioDeMetonoIA.setDate(metono.getDate());
							cambioDeMetonoIA.setDiasDeDiferenciaConDate(0);
							esHoyCMA=true;
						}
						
					}
					else {
						
						long diasDeDiferenciaEntrCMYDate = Math.abs(ChronoUnit.DAYS.between(metono.getDate().toLocalDate(), date));
						
						if(metono.isApofasal() && metono.isSelecto() && !esHoyCMAR) {							
							
							if(diasDeDiferenciaEntrCMYDate < diasMinimosDeDiferenciaEntreCMIARYDate) {
								diasMinimosDeDiferenciaEntreCMIARYDate = diasDeDiferenciaEntrCMYDate;
								cambioDeMetonoIAR.setDate(metono.getDate());
								cambioDeMetonoIAR.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCMIARYDate);
							}
						}
						
						else if(diasDeDiferenciaEntrCMYDate < diasMinimosDeDiferenciaEntreCMIAYDate && !esHoyCMA ) {
							diasMinimosDeDiferenciaEntreCMIAYDate = diasDeDiferenciaEntrCMYDate;
							cambioDeMetonoIA.setDate(metono.getDate());
							cambioDeMetonoIA.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCMIAYDate);
						}
					}
					
				}
				
			}
		}
		
		if(cambioDeMetonoIN.getDate() == null) {
			cambioDeMetonoIN.setDate(cambioDeMetonoIAR.getDate());
		}
		if(cambioDeMetonoIA.getDate() == null) {
			cambioDeMetonoIA.setDate(cambioDeMetonoIAR.getDate());
		}
		
		festividadesObtenidasDTO.add(cambioDeMetonoIN);
		festividadesObtenidasDTO.add(cambioDeMetonoIA);
		festividadesObtenidasDTO.add(cambioDeMetonoIAR);
		
		
		
		// 3 - Cambio de año, Bienvenida a la Primavera, Mitad de año y Entrada del otoño y midsisons
		MinimaFestividadesDTO cambioDeAnyo = new MinimaFestividadesDTO();
		cambioDeAnyo.setCode(CAMBIO_DE_ANYO_CODE);		
		long diasMinimosDeDiferenciaEntreCAYDate = Long.MAX_VALUE;
		boolean esHoyCA = false;
		
		MinimaFestividadesDTO bienvenidaPrimavera = new MinimaFestividadesDTO();
		bienvenidaPrimavera.setCode(BIENVENIDA_PRIMAVERA_CODE);		
		long diasMinimosDeDiferenciaEntreBPYDate = Long.MAX_VALUE;
		boolean esHoyBP =false;
		
		MinimaFestividadesDTO pasoOtonyo = new MinimaFestividadesDTO();
		pasoOtonyo.setCode(ENTRADA_OTONYO_CODE);		
		long diasMinimosDeDiferenciaEntrePOYDate = Long.MAX_VALUE;
		boolean esHoyBO =false;
		
		MinimaFestividadesDTO mitadAnyo = new MinimaFestividadesDTO();
		mitadAnyo.setCode(MITAD_ANYO_CODE);		
		long diasMinimosDeDiferenciaEntreMAYDate = Long.MAX_VALUE;
		boolean esHoyMA = false;
		
		MinimaFestividadesDTO midsison = new MinimaFestividadesDTO();
		long diasMinimosDeDiferenciaEntreLastSoeYDate =Long.MAX_VALUE;
		long diasMinimosDeDiferenciaEntreNextSoeYDate =Long.MAX_VALUE;
		SolsticiosYEquinocciosEntity lastSoe = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity nextSoe = new SolsticiosYEquinocciosEntity();
		
		SolsticiosYEquinocciosEntity sIMasCercano = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity sVMasCercano = new SolsticiosYEquinocciosEntity(); // Tendra utilidad cuando haya festividades con luna en verano
		SolsticiosYEquinocciosEntity eOMasCercano = new SolsticiosYEquinocciosEntity();
		SolsticiosYEquinocciosEntity ePMasCercano = new SolsticiosYEquinocciosEntity(); // Tendra utilidad cuando haya festividades con luna en primavera
		
		
		for(int j = 0; j<datosCosmicosParaVAUDTO.getSoes().size(); j++) {
			
			SolsticiosYEquinocciosEntity soe = datosCosmicosParaVAUDTO.getSoes().get(j);
			
			if(soe.getDate().toLocalDate().isEqual(date)) {
				
				if(soe.isSolsticioInvierno()) {
					
					cambioDeAnyo.setDate(soe.getDate());
					cambioDeAnyo.setDiasDeDiferenciaConDate(0);
					sIMasCercano=soe;
					esHoyCA = true;
				}
				else if(soe.isEquinoccioPrimavera()) {
					
					bienvenidaPrimavera.setDate(soe.getDate());
					bienvenidaPrimavera.setDiasDeDiferenciaConDate(0);
					ePMasCercano=soe;
					esHoyBP = true;
				}
				else if(soe.isSolsticioVerano()) {
					
					mitadAnyo.setDate(soe.getDate());
					mitadAnyo.setDiasDeDiferenciaConDate(0);
					sVMasCercano=soe;
					esHoyMA = true;
				}
				else if(soe.isEquinoccioOtonyo()) {
					
					pasoOtonyo.setDate(soe.getDate());
					pasoOtonyo.setDiasDeDiferenciaConDate(0);
					eOMasCercano=soe;
					esHoyBO = true;
				}
				
			}
			else {
					
				if(soe.getDate().toLocalDate().isBefore(date)) {
					
					long diasDeDiferenciaEntreSoeYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
					if(diasDeDiferenciaEntreSoeYDate < diasMinimosDeDiferenciaEntreLastSoeYDate) {
						diasMinimosDeDiferenciaEntreLastSoeYDate=diasDeDiferenciaEntreSoeYDate;
						lastSoe = soe;
					}
						
				}
				else if(soe.getDate().toLocalDate().isAfter(date)) {
					
					long diasDeDiferenciaEntreSoeYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
					if(diasDeDiferenciaEntreSoeYDate < diasMinimosDeDiferenciaEntreNextSoeYDate) {
						diasMinimosDeDiferenciaEntreNextSoeYDate=diasDeDiferenciaEntreSoeYDate;
						nextSoe = soe;
					}
				}
				
				if(soe.isSolsticioInvierno() && !esHoyCA) {
					
					long diasDeDiferenciaEntreCAYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreCAYDate < diasMinimosDeDiferenciaEntreCAYDate) {
						
						diasMinimosDeDiferenciaEntreCAYDate = diasDeDiferenciaEntreCAYDate;
						cambioDeAnyo.setDate(soe.getDate());
						cambioDeAnyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreCAYDate);
						sIMasCercano=soe;
					}
				}
					
				else if(soe.isEquinoccioPrimavera() && !esHoyBP) {
						
					long diasDeDiferenciaEntreBPYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreBPYDate < diasMinimosDeDiferenciaEntreBPYDate) {
						
						diasMinimosDeDiferenciaEntreBPYDate = diasDeDiferenciaEntreBPYDate;
						bienvenidaPrimavera.setDate(soe.getDate());
						bienvenidaPrimavera.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreBPYDate);
						ePMasCercano=soe;
					}
				}
				else if(soe.isSolsticioVerano() && !esHoyMA) {
						
					long diasDeDiferenciaEntreMAYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntreMAYDate < diasMinimosDeDiferenciaEntreMAYDate) {
						
						diasMinimosDeDiferenciaEntreMAYDate = diasDeDiferenciaEntreMAYDate;
						mitadAnyo.setDate(soe.getDate());
						mitadAnyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreMAYDate);
						sVMasCercano=soe;
					}
				}
			
				else if(soe.isEquinoccioOtonyo() && !esHoyBO) {
						
					long diasDeDiferenciaEntrePOYDate = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), date));
						
					if(diasDeDiferenciaEntrePOYDate < diasMinimosDeDiferenciaEntrePOYDate) {
						
						diasMinimosDeDiferenciaEntrePOYDate = diasDeDiferenciaEntrePOYDate;
						pasoOtonyo.setDate(soe.getDate());
						pasoOtonyo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntrePOYDate);
						eOMasCercano=soe;
					}
				}
			}
		}
		
		
		LocalDateTime diaDelMidsison = lastSoe.getDate().plusSeconds((ChronoUnit.SECONDS.between(lastSoe.getDate(), nextSoe.getDate()))/2);
		midsison.setDate(diaDelMidsison);
		midsison.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(date, midsison.getDate().toLocalDate())));
		
		switch(lastSoe.getStartingSeason()) {
			case 1:
				midsison.setCode(MIDISSON_INVERNAL_CODE);
				break;
			
			case 2:
				midsison.setCode(MIDISSON_PRIMAVERAL_CODE);
				break;
			
			case 3:
				midsison.setCode(MIDISSON_ESTIVAL_CODE);
				break;
			
			case 4: 
				midsison.setCode(MIDISSON_OTONYAL_CODE);
				break;
		}

		
		festividadesObtenidasDTO.add(cambioDeAnyo);
		festividadesObtenidasDTO.add(bienvenidaPrimavera);
		festividadesObtenidasDTO.add(mitadAnyo);
		festividadesObtenidasDTO.add(pasoOtonyo);
		festividadesObtenidasDTO.add(midsison);
		
		
		// 4 - Inicio del primer mes del año, despedida del verano, despedida del año y cambio de aponovo
		
		MinimaFestividadesDTO inicioPrimerMesAnyo = new MinimaFestividadesDTO();
		inicioPrimerMesAnyo.setCode(INICIO_ANYO_CODE);		
		long diasMinimosDeDiferenciaEntreLunaYSI = Long.MAX_VALUE;

		
		MinimaFestividadesDTO despedidaVerano = new MinimaFestividadesDTO();
		despedidaVerano.setCode(DESPEDIDA_VERANO_CODE);		
		long diasMinimosDeDiferenciaEntreDVYLuna = Long.MAX_VALUE;
		
		MinimaFestividadesDTO despedidaAnyo = new MinimaFestividadesDTO();
		despedidaAnyo.setCode(DESPEDIDA_ANYO_CODE);		
		long diasMinimosDeDiferenciaEntreDAYLuna = Long.MAX_VALUE;
		
		MinimaFestividadesDTO cambioDeAponovo = new MinimaFestividadesDTO();
		cambioDeAponovo.setCode(CAMBIO_DE_APONOVO_CODE);		
		long diasMinimosDeDiferenciaEntreAponovoYDate = Long.MAX_VALUE;

		
		for(LunasEntity luna : datosCosmicosParaVAUDTO.getLunas()) {
			
	
			if(luna.isNueva()) {
				
				if(sIMasCercano.getDate().toLocalDate().isBefore(luna.getDate().toLocalDate())) {
								
					long diasDeDiferenciaEntreLunaYSI = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreLunaYSI < diasMinimosDeDiferenciaEntreLunaYSI) {
							
						diasMinimosDeDiferenciaEntreLunaYSI = diasDeDiferenciaEntreLunaYSI;
						inicioPrimerMesAnyo.setDate(luna.getDate());
						inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(date, luna.getDate().toLocalDate())));
									
					}
				}
				
				if(luna.isSelecta() ) {
					
					long diasDeDiferenciaEntreAponovoYDate = Math.abs(ChronoUnit.DAYS.between(date, luna.getDate().toLocalDate()));
					if(diasDeDiferenciaEntreAponovoYDate < diasMinimosDeDiferenciaEntreAponovoYDate) {
						
						diasMinimosDeDiferenciaEntreAponovoYDate = diasDeDiferenciaEntreAponovoYDate;
						cambioDeAponovo.setDate(luna.getDate());
						cambioDeAponovo.setDiasDeDiferenciaConDate(diasMinimosDeDiferenciaEntreAponovoYDate);
									
					}
				}
				
			}
			else if (luna.isLlena()) {
				
				SolsticiosYEquinocciosEntity soeMasCercanoALaLuna = new SolsticiosYEquinocciosEntity();
				
				long diasMinimosDeDiferenciaEntreSoeYLuna = Long.MAX_VALUE;
				for(SolsticiosYEquinocciosEntity soe : datosCosmicosParaVAUDTO.getSoes()) {
					
					long diasDeDiferenciaEntreSoeYLuna = Math.abs(ChronoUnit.DAYS.between(soe.getDate().toLocalDate(), luna.getDate().toLocalDate()));
					
					if(diasDeDiferenciaEntreSoeYLuna < diasMinimosDeDiferenciaEntreSoeYLuna) {
						
						diasMinimosDeDiferenciaEntreSoeYLuna = diasDeDiferenciaEntreSoeYLuna;
						soeMasCercanoALaLuna=soe;
					}					
				}
				
				
				if(soeMasCercanoALaLuna.getStartingSeason()==4 && soeMasCercanoALaLuna.getDate().toLocalDate().equals(eOMasCercano.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(eOMasCercano.getDate().toLocalDate())) {
					
					long diasDeDiferenciaEntreDVYLuna = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreDVYLuna < diasMinimosDeDiferenciaEntreDVYLuna) {
							
						diasMinimosDeDiferenciaEntreDVYLuna = diasDeDiferenciaEntreDVYLuna;
						despedidaVerano.setDate(luna.getDate());
						despedidaVerano.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date)));		
					}		
				}
				else if(soeMasCercanoALaLuna.getStartingSeason()==1 && soeMasCercanoALaLuna.getDate().toLocalDate().equals(sIMasCercano.getDate().toLocalDate()) && luna.getDate().toLocalDate().isBefore(sIMasCercano.getDate().toLocalDate())) {
					

					long diasDeDiferenciaEntreDAYLuna = Math.abs(ChronoUnit.DAYS.between(sIMasCercano.getDate().toLocalDate(), luna.getDate().toLocalDate()));
						
					if(diasDeDiferenciaEntreDAYLuna < diasMinimosDeDiferenciaEntreDAYLuna) {
							
						diasMinimosDeDiferenciaEntreDAYLuna = diasDeDiferenciaEntreDAYLuna;
						despedidaAnyo.setDate(luna.getDate());
						despedidaAnyo.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(luna.getDate().toLocalDate(), date)));
						
					}			
				}				
			}
		}
		

		// Si hay un eclipeno no hay festividad de inicio del primer mes ni de cambio de metono
		// Y si hay un metono, no hay inicio del primer mes
		if(cambioDeEclipeno.getDiasDeDiferenciaConDate() < 100 || cambioDeEclipenoIAR.getDiasDeDiferenciaConDate() < 100) { 
			
			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
			cambioDeMetonoIN.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
			cambioDeMetonoIA.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
		}
		else if(cambioDeMetonoIN.getDiasDeDiferenciaConDate() < 100 || cambioDeMetonoIA.getDiasDeDiferenciaConDate() < 100 || cambioDeMetonoIAR.getDiasDeDiferenciaConDate() < 100) {
			
			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);

		}
			
		
		
		festividadesObtenidasDTO.add(inicioPrimerMesAnyo);
		festividadesObtenidasDTO.add(despedidaVerano);
		festividadesObtenidasDTO.add(despedidaAnyo);
		festividadesObtenidasDTO.add(cambioDeAponovo);
		
		
		// 5 - Midsison aponoval		
		
		MinimaFestividadesDTO midsisonAponoval = new MinimaFestividadesDTO();
		midsisonAponoval.setCode(MIDISSON_APONOVAL_CODE);
		midsisonAponoval.setDate(midsison.getDate());
		midsisonAponoval.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
		
		if(cambioDeAponovo.getDate().toLocalDate().isEqual(midsison.getDate().toLocalDate())) {
			midsisonAponoval.setDate(midsison.getDate());
			midsisonAponoval.setDiasDeDiferenciaConDate(midsison.getDiasDeDiferenciaConDate());
		}
		
		festividadesObtenidasDTO.add(midsisonAponoval);
		
		
		
		
		return festividadesObtenidasDTO;
	}
	

	public String poblateFestividades() {

		System.out.println("Actualizando las Festividades.");
		
		String resultado = "Festividades actualizadas correctamente.";
		
		List<FestividadesEntity> allFestividades = this.festividadesRepository.findAll();
		
		if(allFestividades.isEmpty()) {
			
			List<FestividadesEntity> festividadParaDDB = new ArrayList<>();
			
			festividadParaDDB.add(this.crearFestividad("CEAR", "Cambio de eclípeno invernal apofasal remoto", false));
			
			festividadParaDDB.add(this.crearFestividad("CE", "Cambio de eclípeno invernal nuevo", false));			
			festividadParaDDB.add(this.crearFestividad("CMAR", "Cambio de métono invernal apofasal remoto", false));
			
			festividadParaDDB.add(this.crearFestividad("CMF", "Cambio de métono invernal nuevo", false));
			festividadParaDDB.add(this.crearFestividad("CMA", "Cambio de métono invernal apórico", false));
			
			festividadParaDDB.add(this.crearFestividad("CA", "Cambio de año", false));
			festividadParaDDB.add(this.crearFestividad("IA", "Inicio del primer mes del año", true));
			festividadParaDDB.add(this.crearFestividad("MSI", "Midsison invernal", false));
			
			festividadParaDDB.add(this.crearFestividad("BP", "Bienvenida de la primavera", false));
			festividadParaDDB.add(this.crearFestividad("MSP", "Midsison primaveral", false));
			
			festividadParaDDB.add(this.crearFestividad("MA", "Mitad del año", false));
			festividadParaDDB.add(this.crearFestividad("MSE", "Midsison estival", false));
			
			festividadParaDDB.add(this.crearFestividad("DV", "Despedida del verano", true));
			festividadParaDDB.add(this.crearFestividad("MSO", "Midsison otoñal", false));
			
			festividadParaDDB.add(this.crearFestividad("EO", "Entrada del otoño", false));
			festividadParaDDB.add(this.crearFestividad("DA", "Despedida del año", true));
			
			festividadParaDDB.add(this.crearFestividad("LA", "Cambio de aponovo", false));
			festividadParaDDB.add(this.crearFestividad("MAP", "Midsison aponoval", false));
			
			this.festividadesRepository.saveAll(festividadParaDDB);
		}
		else {
			System.out.println("Ya hay festividades en la base de datos.");
			resultado = "Error al actualizar las festividades: ya hay festividades en la base de datos.";
		}
		System.out.println("Festividades actualizadas");
		return resultado;
	}
	
	private FestividadesEntity crearFestividad(String code, String name, boolean lunar) {
		
		FestividadesEntity newFestividad = new FestividadesEntity();

		newFestividad.setCode(code);
		newFestividad.setNombre(name);		
		newFestividad.setLunar(lunar);
		
		return newFestividad;
	}

}
