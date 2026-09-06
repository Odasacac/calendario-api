package CCASolutions.Calendario.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import CCASolutions.Calendario.Services.TablasReferenciaService;

/**
 * EN: Works out the VAU festivities around a date and manages the fixed festivity table.
 * ES: Calcula las festividades VAU alrededor de una fecha y gestiona la tabla fija de
 * festividades.
 */
@Service
public class FestividadesServiceImpl implements FestividadesService {

	private static final Logger LOG = LoggerFactory.getLogger(FestividadesServiceImpl.class);

	@Autowired
	private FestividadesRepository festividadesRepository;

	@Autowired
	private TablasReferenciaService tablasReferenciaService;

	private static final String CAMBIO_DE_ECLIPENO_IAR_CODE = "CEAR";

	private static final String CAMBIO_DE_ECLIPENO_CODE = "CE";
	private static final String CAMBIO_DE_METONO_IAR_CODE = "CMAR";
	private static final String CAMBIO_DE_METONO_IN_CODE = "CMF";
	private static final String CAMBIO_DE_METONO_IA_CODE = "CMA";
	private static final String CAMBIO_DE_ANYO_CODE = "CA";

	private static final String INICIO_ANYO_CODE = "IA";
	private static final String MIDSISON_INVERNAL_CODE = "MSI";

	private static final String BIENVENIDA_PRIMAVERA_CODE = "BP";
	private static final String MIDSISON_PRIMAVERAL_CODE = "MSP";

	private static final String MITAD_ANYO_CODE = "MA";
	private static final String MIDSISON_ESTIVAL_CODE = "MSE";

	private static final String ENTRADA_OTONYO_CODE = "EO";
	private static final String MIDSISON_OTONYAL_CODE = "MSO";

	private static final String CAMBIO_DE_APONOVO_CODE = "LA";
	private static final String MIDSISON_APONOVAL_CODE = "MAP";

	/**
	 * EN: Codes ordered from least to most relevant: when several festivities fall on the same
	 * day, the last one appearing in this list wins.
	 * ES: Códigos ordenados de menor a mayor relevancia: cuando varias festividades caen
	 * el mismo día gana la última que aparezca en esta lista.
	 */
	private static final String[] PRIORIDAD = {
			CAMBIO_DE_APONOVO_CODE,
			INICIO_ANYO_CODE,
			MIDSISON_OTONYAL_CODE,
			MIDSISON_ESTIVAL_CODE,
			MIDSISON_PRIMAVERAL_CODE,
			MIDSISON_INVERNAL_CODE,
			MIDSISON_APONOVAL_CODE,
			ENTRADA_OTONYO_CODE,
			MITAD_ANYO_CODE,
			BIENVENIDA_PRIMAVERA_CODE,
			CAMBIO_DE_ANYO_CODE,
			CAMBIO_DE_METONO_IA_CODE,
			CAMBIO_DE_METONO_IN_CODE,
			CAMBIO_DE_ECLIPENO_CODE,
			CAMBIO_DE_METONO_IAR_CODE,
			CAMBIO_DE_ECLIPENO_IAR_CODE
	};


	/**
	 * EN: Works out today's festivity, the previous one and the next one. Computes every
	 * candidate, splits them by whether they fall before, on or after the date, and keeps the
	 * closest one on each side.
	 * ES: Calcula la festividad de hoy, la anterior y la próxima. Calcula todas las candidatas,
	 * las reparte según caigan antes, en o después de la fecha, y se queda con la más cercana
	 * por cada lado.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: the three festivities, already formatted as text. / ES: las tres festividades, ya formateadas como texto.
	 */
	public FestividadesDTO getFestividades(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		FestividadesDTO festividades = new FestividadesDTO();

		List<MinimaFestividadesDTO> festividadesObtenidasDTO = this.getFestividadesDesdeFecha(date, datosCosmicosParaVAUDTO);

		// La tabla de festividades tiene dieciseis filas fijas y se consultaba entera en
		// cada peticion; ahora viene de la cache y se indexa por codigo una sola vez
		Map<String, String> nombresPorCodigo = new HashMap<>();

		for (FestividadesEntity entity : this.tablasReferenciaService.getFestividades()) {
			nombresPorCodigo.put(entity.getCode(), entity.getNombre());
		}

		List<MinimaFestividadesDTO> festividadesActuales = new ArrayList<>();
		MinimaFestividadesDTO festividadPasadaMasCercana = null;
		MinimaFestividadesDTO festividadFuturaMasCercana = null;

		for (MinimaFestividadesDTO festividad : festividadesObtenidasDTO) {

			// Una festividad sin fecha es una que no se ha llegado a calcular. Antes
			// entraba en el grupo de "hoy" solo porque su contador seguia a cero.
			if (festividad.getDate() == null) {
				continue;
			}

			if (festividad.getDiasDeDiferenciaConDate() == 0) {

				festividadesActuales.add(festividad);
			}
			else if (festividad.getDate().toLocalDate().isAfter(date)) {

				festividadFuturaMasCercana = this.masCercana(festividadFuturaMasCercana, festividad);
			}
			else if (festividad.getDate().toLocalDate().isBefore(date)) {

				festividadPasadaMasCercana = this.masCercana(festividadPasadaMasCercana, festividad);
			}
		}

		festividades.setFestividadActual(this.getFestividadActual(nombresPorCodigo, festividadesActuales));
		festividades.setFestividadAnterior(this.getFestividadName(festividadPasadaMasCercana, nombresPorCodigo, "hace"));
		festividades.setFestividadProxima(this.getFestividadName(festividadFuturaMasCercana, nombresPorCodigo, "dentro de"));

		return festividades;
	}

	/**
	 * EN: Keeps whichever of the two festivities is closer to the date.
	 * ES: Se queda con la de las dos festividades que esté más cerca de la fecha.
	 *
	 * @param actual    EN: best candidate so far; may be {@code null}. / ES: mejor candidata hasta ahora; admite {@code null}.
	 * @param candidata EN: new candidate. / ES: candidata nueva.
	 * @return EN: the closer of the two. / ES: la más cercana de las dos.
	 */
	private MinimaFestividadesDTO masCercana(MinimaFestividadesDTO actual, MinimaFestividadesDTO candidata) {

		if (actual == null || candidata.getDiasDeDiferenciaConDate() < actual.getDiasDeDiferenciaConDate()) {
			return candidata;
		}

		return actual;
	}

	/**
	 * EN: Picks the festivity of the day when several fall at once. A midsison plus a change of
	 * aponovo is a festivity of its own, the aponoval midsison. Otherwise the most relevant
	 * code wins, relevance growing towards the end of the priority list.
	 * ES: Elige la festividad del día cuando caen varias a la vez. Un midsison más un cambio de
	 * aponovo es una festividad propia, el midsison aponoval. En el resto de casos gana el
	 * código más relevante, y la relevancia crece hacia el final de la lista de prioridad.
	 *
	 * @param nombresPorCodigo    EN: code to name lookup. / ES: traducción de código a nombre.
	 * @param festividadesActuales EN: festivities falling on the date. / ES: festividades que caen en la fecha.
	 * @return EN: the name of the festivity, or an empty string if there is none. / ES: el nombre de la festividad, o cadena vacía si no hay ninguna.
	 */
	private String getFestividadActual(Map<String, String> nombresPorCodigo, List<MinimaFestividadesDTO> festividadesActuales) {

		if (festividadesActuales.isEmpty()) {
			return "";
		}

		boolean hayMidsison = false;
		boolean hayAponovo = false;

		for (MinimaFestividadesDTO festividad : festividadesActuales) {

			String code = festividad.getCode();

			if (MIDSISON_INVERNAL_CODE.equals(code) || MIDSISON_PRIMAVERAL_CODE.equals(code)
					|| MIDSISON_ESTIVAL_CODE.equals(code) || MIDSISON_OTONYAL_CODE.equals(code)) {

				hayMidsison = true;
			}

			if (CAMBIO_DE_APONOVO_CODE.equals(code)) {
				hayAponovo = true;
			}
		}

		if (hayMidsison && hayAponovo) {

			return nombresPorCodigo.getOrDefault(MIDSISON_APONOVAL_CODE, "");
		}

		// El bucle original recorria la tabla entera sin cortar, de modo que se quedaba
		// con la ultima coincidencia. Recorriendola al reves y saliendo al primer acierto
		// el resultado es el mismo y queda explicito cual es el criterio.
		for (int i = PRIORIDAD.length - 1; i >= 0; i--) {

			for (MinimaFestividadesDTO festividad : festividadesActuales) {

				if (PRIORIDAD[i].equals(festividad.getCode())) {

					return nombresPorCodigo.getOrDefault(PRIORIDAD[i], "");
				}
			}
		}

		return "";
	}

	/**
	 * EN: Formats a festivity as text, with its distance in days and the right connector.
	 * ES: Formatea una festividad como texto, con su distancia en días y el conector adecuado.
	 *
	 * @param festividad       EN: festivity to name; may be {@code null}. / ES: festividad a nombrar; admite {@code null}.
	 * @param nombresPorCodigo EN: code to name lookup. / ES: traducción de código a nombre.
	 * @param conector         EN: "hace" for the past, "dentro de" for the future. / ES: "hace" para el pasado, "dentro de" para el futuro.
	 * @return EN: the formatted text, or an empty string. / ES: el texto formateado, o cadena vacía.
	 */
	private String getFestividadName(MinimaFestividadesDTO festividad, Map<String, String> nombresPorCodigo, String conector) {

		if (festividad == null) {
			return "";
		}

		String nombre = nombresPorCodigo.get(festividad.getCode());

		if (nombre == null) {
			return "";
		}

		long dias = festividad.getDiasDeDiferenciaConDate();

		return nombre + " " + conector + " " + dias + " " + (dias == 1 ? "día" : "días");
	}

	/**
	 * EN: Computes every festivity candidate around the date, in five blocks: changes of
	 * eclipeno, changes of meton, the solar festivities plus the midsison, the start of the
	 * year and the change of aponovo, and finally the aponoval midsison. The presence of an
	 * eclipeno cancels the changes of meton and the start of the year, and a meton cancels the
	 * start of the year, because the greater cycle absorbs the lesser ones.
	 * ES: Calcula todas las festividades candidatas alrededor de la fecha, en cinco bloques:
	 * cambios de eclípeno, cambios de métono, las festividades solares junto al midsison, el
	 * inicio del año y el cambio de aponovo, y por último el midsison aponoval. La presencia de
	 * un eclípeno anula los cambios de métono y el inicio de año, y un métono anula el inicio de
	 * año, porque el ciclo mayor absorbe a los menores.
	 *
	 * @param date                    EN: date being consulted. / ES: fecha que se consulta.
	 * @param datosCosmicosParaVAUDTO EN: phenomena already loaded for that date. / ES: fenómenos ya cargados para esa fecha.
	 * @return EN: every candidate, each with its date and its distance in days. / ES: todas las candidatas, cada una con su fecha y su distancia en días.
	 */
	private List<MinimaFestividadesDTO> getFestividadesDesdeFecha(LocalDate date, DatosCosmicosParaVAUDTO datosCosmicosParaVAUDTO) {

		List<MinimaFestividadesDTO> festividadesObtenidasDTO = new ArrayList<>();

		// 1 - Cambio de eclipeno
		MinimaFestividadesDTO cambioDeEclipeno = this.crearFestividadVacia(CAMBIO_DE_ECLIPENO_CODE);
		MinimaFestividadesDTO cambioDeEclipenoIAR = this.crearFestividadVacia(CAMBIO_DE_ECLIPENO_IAR_CODE);

		this.calcularCambiosDeEclipeno(date, datosCosmicosParaVAUDTO.getEclipenos(), cambioDeEclipeno, cambioDeEclipenoIAR);

		festividadesObtenidasDTO.add(cambioDeEclipeno);
		festividadesObtenidasDTO.add(cambioDeEclipenoIAR);

		// 2 - Cambio de metono fasal y aporico
		MinimaFestividadesDTO cambioDeMetonoIN = this.crearFestividadVacia(CAMBIO_DE_METONO_IN_CODE);
		MinimaFestividadesDTO cambioDeMetonoIA = this.crearFestividadVacia(CAMBIO_DE_METONO_IA_CODE);
		MinimaFestividadesDTO cambioDeMetonoIAR = this.crearFestividadVacia(CAMBIO_DE_METONO_IAR_CODE);

		this.calcularCambiosDeMetono(date, datosCosmicosParaVAUDTO.getMetons(), cambioDeMetonoIN, cambioDeMetonoIA, cambioDeMetonoIAR);

		if(cambioDeMetonoIN.getDate() == null) {
			cambioDeMetonoIN.setDate(cambioDeMetonoIAR.getDate());
		}

		if(cambioDeMetonoIA.getDate() == null) {
			cambioDeMetonoIA.setDate(cambioDeMetonoIAR.getDate());
		}

		festividadesObtenidasDTO.add(cambioDeMetonoIN);
		festividadesObtenidasDTO.add(cambioDeMetonoIA);
		festividadesObtenidasDTO.add(cambioDeMetonoIAR);

		// 3 - Cambio de año, Bienvenida a la Primavera, Mitad de año, Entrada del otoño y midsisons
		MinimaFestividadesDTO cambioDeAnyo = this.crearFestividadVacia(CAMBIO_DE_ANYO_CODE);
		MinimaFestividadesDTO bienvenidaPrimavera = this.crearFestividadVacia(BIENVENIDA_PRIMAVERA_CODE);
		MinimaFestividadesDTO pasoOtonyo = this.crearFestividadVacia(ENTRADA_OTONYO_CODE);
		MinimaFestividadesDTO mitadAnyo = this.crearFestividadVacia(MITAD_ANYO_CODE);
		MinimaFestividadesDTO midsison = new MinimaFestividadesDTO();

		SolsticiosYEquinocciosEntity[] referencias = this.calcularFestividadesSolares(date, datosCosmicosParaVAUDTO.getSoes(),
				cambioDeAnyo, bienvenidaPrimavera, mitadAnyo, pasoOtonyo);

		SolsticiosYEquinocciosEntity lastSoe = referencias[0];
		SolsticiosYEquinocciosEntity nextSoe = referencias[1];
		SolsticiosYEquinocciosEntity sIMasCercano = referencias[2];

		// Sin soe anterior y posterior no hay midsison que calcular; antes se leia la
		// fecha de una entidad vacia y saltaba un NullPointerException
		if (lastSoe != null && nextSoe != null) {

			LocalDateTime diaDelMidsison = lastSoe.getDate().plusSeconds(ChronoUnit.SECONDS.between(lastSoe.getDate(), nextSoe.getDate()) / 2);

			midsison.setDate(diaDelMidsison);
			midsison.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(date, diaDelMidsison.toLocalDate())));
			midsison.setCode(this.getCodigoMidsison(lastSoe.getStartingSeason()));
		}
		else {
			LOG.warn("No se ha podido calcular el midsison de {}: falta el soe anterior o el posterior", date);
		}

		festividadesObtenidasDTO.add(cambioDeAnyo);
		festividadesObtenidasDTO.add(bienvenidaPrimavera);
		festividadesObtenidasDTO.add(mitadAnyo);
		festividadesObtenidasDTO.add(pasoOtonyo);
		festividadesObtenidasDTO.add(midsison);

		// 4 - Inicio del primer mes del año y cambio de aponovo
		MinimaFestividadesDTO inicioPrimerMesAnyo = this.crearFestividadVacia(INICIO_ANYO_CODE);
		MinimaFestividadesDTO cambioDeAponovo = this.crearFestividadVacia(CAMBIO_DE_APONOVO_CODE);

		this.calcularFestividadesLunares(date, datosCosmicosParaVAUDTO.getLunas(), sIMasCercano, inicioPrimerMesAnyo, cambioDeAponovo);

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
		festividadesObtenidasDTO.add(cambioDeAponovo);

		// 5 - Midsison aponoval
		MinimaFestividadesDTO midsisonAponoval = this.crearFestividadVacia(MIDSISON_APONOVAL_CODE);
		midsisonAponoval.setDate(midsison.getDate());
		midsisonAponoval.setDiasDeDiferenciaConDate(Long.MAX_VALUE);

		if(midsison.getDate() != null && cambioDeAponovo.getDate() != null
				&& cambioDeAponovo.getDate().toLocalDate().isEqual(midsison.getDate().toLocalDate())) {

			midsisonAponoval.setDiasDeDiferenciaConDate(midsison.getDiasDeDiferenciaConDate());
		}

		festividadesObtenidasDTO.add(midsisonAponoval);

		return festividadesObtenidasDTO;
	}

	/**
	 * EN: Builds an empty festivity carrying only its code.
	 * ES: Construye una festividad vacía que sólo lleva su código.
	 *
	 * @param code EN: festivity code. / ES: código de la festividad.
	 * @return EN: the DTO, with no date yet. / ES: el DTO, todavía sin fecha.
	 */
	private MinimaFestividadesDTO crearFestividadVacia(String code) {

		MinimaFestividadesDTO festividad = new MinimaFestividadesDTO();
		festividad.setCode(code);

		return festividad;
	}

	/**
	 * EN: Festivity code of the midsison, which takes its name from the season that has just
	 * ended.
	 * ES: Código de festividad del midsison, que toma su nombre de la estación que acaba de
	 * terminar.
	 *
	 * @param startingSeason EN: season opened by the previous solstice, 1 to 4. / ES: estación que abre el solsticio anterior, del 1 al 4.
	 * @return EN: the code, or {@code null} if the season is unknown. / ES: el código, o {@code null} si la estación no es conocida.
	 */
	private String getCodigoMidsison(int startingSeason) {

		switch(startingSeason) {

			case 1:
				return MIDSISON_INVERNAL_CODE;

			case 2:
				return MIDSISON_PRIMAVERAL_CODE;

			case 3:
				return MIDSISON_ESTIVAL_CODE;

			case 4:
				return MIDSISON_OTONYAL_CODE;

			default:
				return null;
		}
	}

	/**
	 * EN: Finds the closest change of eclipeno on either side. Apofasal selecto eclipenos feed
	 * the remote festivity and the rest the ordinary one; once one of them is found on the date
	 * itself, no later candidate can displace it.
	 * ES: Localiza el cambio de eclípeno más cercano por cada lado. Los eclípenos apofasales
	 * selectos alimentan la festividad remota y el resto la corriente; en cuanto se encuentra
	 * uno en la propia fecha, ninguna candidata posterior puede desplazarlo.
	 *
	 * @param date                EN: date being consulted. / ES: fecha que se consulta.
	 * @param eclipenos           EN: every eclipeno. / ES: todos los eclípenos.
	 * @param cambioDeEclipeno    EN: ordinary festivity, filled in by this method. / ES: festividad corriente, que rellena este método.
	 * @param cambioDeEclipenoIAR EN: remote festivity, filled in by this method. / ES: festividad remota, que rellena este método.
	 */
	private void calcularCambiosDeEclipeno(LocalDate date, List<EclipenosEntity> eclipenos,
			MinimaFestividadesDTO cambioDeEclipeno, MinimaFestividadesDTO cambioDeEclipenoIAR) {

		long diasMinimosCE = Long.MAX_VALUE;
		long diasMinimosCEAR = Long.MAX_VALUE;
		boolean esHoyCE = false;
		boolean esHoyCEAR = false;

		for(EclipenosEntity eclipeno : eclipenos) {

			if(!eclipeno.isInvernal() || !eclipeno.isNuevo()) {
				continue;
			}

			LocalDate fechaEclipeno = eclipeno.getDate().toLocalDate();
			boolean esApofasalSelecto = eclipeno.isApofasal() && eclipeno.isSelecto();

			if(fechaEclipeno.isEqual(date)) {

				if(esApofasalSelecto) {

					cambioDeEclipenoIAR.setDate(eclipeno.getDate());
					cambioDeEclipenoIAR.setDiasDeDiferenciaConDate(0);
					esHoyCEAR = true;
				}
				else {

					cambioDeEclipeno.setDate(eclipeno.getDate());
					cambioDeEclipeno.setDiasDeDiferenciaConDate(0);
					esHoyCE = true;
				}

				continue;
			}

			long diasDeDiferencia = Math.abs(ChronoUnit.DAYS.between(fechaEclipeno, date));

			if(esApofasalSelecto && !esHoyCEAR) {

				if(diasDeDiferencia < diasMinimosCEAR) {

					diasMinimosCEAR = diasDeDiferencia;
					cambioDeEclipenoIAR.setDate(eclipeno.getDate());
					cambioDeEclipenoIAR.setDiasDeDiferenciaConDate(diasMinimosCEAR);
				}
			}
			else if(diasDeDiferencia < diasMinimosCE && !esHoyCE) {

				diasMinimosCE = diasDeDiferencia;
				cambioDeEclipeno.setDate(eclipeno.getDate());
				cambioDeEclipeno.setDiasDeDiferenciaConDate(diasMinimosCE);
			}
		}
	}

	/**
	 * EN: Finds the closest change of meton on either side, in three flavours: winter new,
	 * winter aporic and winter apofasal remote.
	 * ES: Localiza el cambio de métono más cercano por cada lado, en tres variantes: invernal
	 * nuevo, invernal apórico e invernal apofasal remoto.
	 *
	 * @param date              EN: date being consulted. / ES: fecha que se consulta.
	 * @param metons            EN: metons in range. / ES: métonos del rango.
	 * @param cambioDeMetonoIN  EN: new meton festivity, filled in by this method. / ES: festividad de métono nuevo, que rellena este método.
	 * @param cambioDeMetonoIA  EN: aporic meton festivity, filled in by this method. / ES: festividad de métono apórico, que rellena este método.
	 * @param cambioDeMetonoIAR EN: remote meton festivity, filled in by this method. / ES: festividad de métono remoto, que rellena este método.
	 */
	private void calcularCambiosDeMetono(LocalDate date, List<MetonsEntity> metons,
			MinimaFestividadesDTO cambioDeMetonoIN, MinimaFestividadesDTO cambioDeMetonoIA, MinimaFestividadesDTO cambioDeMetonoIAR) {

		long diasMinimosCMIN = Long.MAX_VALUE;
		long diasMinimosCMIA = Long.MAX_VALUE;
		long diasMinimosCMIAR = Long.MAX_VALUE;
		boolean esHoyCMN = false;
		boolean esHoyCMA = false;
		boolean esHoyCMAR = false;

		for(MetonsEntity metono : metons) {

			if(!metono.isInvernal()) {
				continue;
			}

			boolean esNuevo = metono.isNuevo();

			if(!esNuevo && !metono.isAporico()) {
				continue;
			}

			MinimaFestividadesDTO destino = esNuevo ? cambioDeMetonoIN : cambioDeMetonoIA;
			LocalDate fechaMetono = metono.getDate().toLocalDate();
			boolean esApofasalSelecto = metono.isApofasal() && metono.isSelecto();

			if(fechaMetono.isEqual(date)) {

				if(esApofasalSelecto) {

					cambioDeMetonoIAR.setDate(metono.getDate());
					cambioDeMetonoIAR.setDiasDeDiferenciaConDate(0);
					esHoyCMAR = true;
				}
				else {

					destino.setDate(metono.getDate());
					destino.setDiasDeDiferenciaConDate(0);

					if(esNuevo) {
						esHoyCMN = true;
					}
					else {
						esHoyCMA = true;
					}
				}

				continue;
			}

			long diasDeDiferencia = Math.abs(ChronoUnit.DAYS.between(fechaMetono, date));

			if(esApofasalSelecto && !esHoyCMAR) {

				if(diasDeDiferencia < diasMinimosCMIAR) {

					diasMinimosCMIAR = diasDeDiferencia;
					cambioDeMetonoIAR.setDate(metono.getDate());
					cambioDeMetonoIAR.setDiasDeDiferenciaConDate(diasMinimosCMIAR);
				}
			}
			else if(esNuevo) {

				if(diasDeDiferencia < diasMinimosCMIN && !esHoyCMN) {

					diasMinimosCMIN = diasDeDiferencia;
					cambioDeMetonoIN.setDate(metono.getDate());
					cambioDeMetonoIN.setDiasDeDiferenciaConDate(diasMinimosCMIN);
				}
			}
			else if(diasDeDiferencia < diasMinimosCMIA && !esHoyCMA) {

				diasMinimosCMIA = diasDeDiferencia;
				cambioDeMetonoIA.setDate(metono.getDate());
				cambioDeMetonoIA.setDiasDeDiferenciaConDate(diasMinimosCMIA);
			}
		}
	}

	/**
	 * EN: Finds the four solar festivities closest to the date, one per solstice and equinox,
	 * and at the same time the solstice or equinox on either side, which the midsison needs.
	 * ES: Localiza las cuatro festividades solares más cercanas a la fecha, una por solsticio y
	 * equinoccio, y al mismo tiempo el solsticio o equinoccio de cada lado, que hace falta para
	 * el midsison.
	 *
	 * @param date               EN: date being consulted. / ES: fecha que se consulta.
	 * @param soes               EN: solstices and equinoxes in range. / ES: solsticios y equinoccios del rango.
	 * @param cambioDeAnyo       EN: change of year, filled in by this method. / ES: cambio de año, que rellena este método.
	 * @param bienvenidaPrimavera EN: welcoming of spring, filled in by this method. / ES: bienvenida de la primavera, que rellena este método.
	 * @param mitadAnyo          EN: middle of the year, filled in by this method. / ES: mitad del año, que rellena este método.
	 * @param pasoOtonyo         EN: entry of autumn, filled in by this method. / ES: entrada del otoño, que rellena este método.
	 * @return EN: three slots: previous soe, next soe and closest winter solstice; any of them
	 *         may be {@code null}. / ES: tres posiciones: soe anterior, soe posterior y
	 *         solsticio de invierno más cercano; cualquiera de ellos puede ser {@code null}.
	 */
	private SolsticiosYEquinocciosEntity[] calcularFestividadesSolares(LocalDate date, List<SolsticiosYEquinocciosEntity> soes,
			MinimaFestividadesDTO cambioDeAnyo, MinimaFestividadesDTO bienvenidaPrimavera,
			MinimaFestividadesDTO mitadAnyo, MinimaFestividadesDTO pasoOtonyo) {

		long diasMinimosLastSoe = Long.MAX_VALUE;
		long diasMinimosNextSoe = Long.MAX_VALUE;
		long diasMinimosCA = Long.MAX_VALUE;
		long diasMinimosBP = Long.MAX_VALUE;
		long diasMinimosMA = Long.MAX_VALUE;
		long diasMinimosPO = Long.MAX_VALUE;

		boolean esHoyCA = false;
		boolean esHoyBP = false;
		boolean esHoyMA = false;
		boolean esHoyBO = false;

		SolsticiosYEquinocciosEntity lastSoe = null;
		SolsticiosYEquinocciosEntity nextSoe = null;
		SolsticiosYEquinocciosEntity sIMasCercano = null;

		for(SolsticiosYEquinocciosEntity soe : soes) {

			LocalDate fechaSoe = soe.getDate().toLocalDate();

			if(fechaSoe.isEqual(date)) {

				if(soe.isSolsticioInvierno()) {

					cambioDeAnyo.setDate(soe.getDate());
					cambioDeAnyo.setDiasDeDiferenciaConDate(0);
					sIMasCercano = soe;
					esHoyCA = true;
				}
				else if(soe.isEquinoccioPrimavera()) {

					bienvenidaPrimavera.setDate(soe.getDate());
					bienvenidaPrimavera.setDiasDeDiferenciaConDate(0);
					esHoyBP = true;
				}
				else if(soe.isSolsticioVerano()) {

					mitadAnyo.setDate(soe.getDate());
					mitadAnyo.setDiasDeDiferenciaConDate(0);
					esHoyMA = true;
				}
				else if(soe.isEquinoccioOtonyo()) {

					pasoOtonyo.setDate(soe.getDate());
					pasoOtonyo.setDiasDeDiferenciaConDate(0);
					esHoyBO = true;
				}

				continue;
			}

			long diasDeDiferencia = Math.abs(ChronoUnit.DAYS.between(fechaSoe, date));

			if(fechaSoe.isBefore(date)) {

				if(diasDeDiferencia < diasMinimosLastSoe) {
					diasMinimosLastSoe = diasDeDiferencia;
					lastSoe = soe;
				}
			}
			else if(diasDeDiferencia < diasMinimosNextSoe) {

				diasMinimosNextSoe = diasDeDiferencia;
				nextSoe = soe;
			}

			if(soe.isSolsticioInvierno() && !esHoyCA) {

				if(diasDeDiferencia < diasMinimosCA) {

					diasMinimosCA = diasDeDiferencia;
					cambioDeAnyo.setDate(soe.getDate());
					cambioDeAnyo.setDiasDeDiferenciaConDate(diasMinimosCA);
					sIMasCercano = soe;
				}
			}
			else if(soe.isEquinoccioPrimavera() && !esHoyBP) {

				if(diasDeDiferencia < diasMinimosBP) {

					diasMinimosBP = diasDeDiferencia;
					bienvenidaPrimavera.setDate(soe.getDate());
					bienvenidaPrimavera.setDiasDeDiferenciaConDate(diasMinimosBP);
				}
			}
			else if(soe.isSolsticioVerano() && !esHoyMA) {

				if(diasDeDiferencia < diasMinimosMA) {

					diasMinimosMA = diasDeDiferencia;
					mitadAnyo.setDate(soe.getDate());
					mitadAnyo.setDiasDeDiferenciaConDate(diasMinimosMA);
				}
			}
			else if(soe.isEquinoccioOtonyo() && !esHoyBO) {

				if(diasDeDiferencia < diasMinimosPO) {

					diasMinimosPO = diasDeDiferencia;
					pasoOtonyo.setDate(soe.getDate());
					pasoOtonyo.setDiasDeDiferenciaConDate(diasMinimosPO);
				}
			}
		}

		return new SolsticiosYEquinocciosEntity[] { lastSoe, nextSoe, sIMasCercano };
	}

	/**
	 * EN: Finds the two lunar festivities: the start of the first month of the year, which is
	 * the first new moon after the closest winter solstice, and the change of aponovo, which is
	 * the closest new moon at apogee.
	 * ES: Localiza las dos festividades lunares: el inicio del primer mes del año, que es la
	 * primera luna nueva después del solsticio de invierno más cercano, y el cambio de aponovo,
	 * que es la luna nueva en apogeo más próxima.
	 *
	 * @param date                EN: date being consulted. / ES: fecha que se consulta.
	 * @param lunas               EN: moon phases in range. / ES: fases lunares del rango.
	 * @param sIMasCercano        EN: closest winter solstice; may be {@code null}. / ES: solsticio de invierno más cercano; admite {@code null}.
	 * @param inicioPrimerMesAnyo EN: start of the year, filled in by this method. / ES: inicio del año, que rellena este método.
	 * @param cambioDeAponovo     EN: change of aponovo, filled in by this method. / ES: cambio de aponovo, que rellena este método.
	 */
	private void calcularFestividadesLunares(LocalDate date, List<LunasEntity> lunas, SolsticiosYEquinocciosEntity sIMasCercano,
			MinimaFestividadesDTO inicioPrimerMesAnyo, MinimaFestividadesDTO cambioDeAponovo) {

		long diasMinimosEntreLunaYSI = Long.MAX_VALUE;
		long diasMinimosEntreAponovoYDate = Long.MAX_VALUE;

		// Sin solsticio de invierno de referencia no hay inicio de año que calcular
		LocalDate fechaSIMasCercano = sIMasCercano != null ? sIMasCercano.getDate().toLocalDate() : null;

		for(LunasEntity luna : lunas) {

			if(!luna.isNueva()) {
				continue;
			}

			LocalDate fechaLuna = luna.getDate().toLocalDate();

			if(fechaSIMasCercano != null && fechaSIMasCercano.isBefore(fechaLuna)) {

				long diasDeDiferenciaEntreLunaYSI = Math.abs(ChronoUnit.DAYS.between(fechaSIMasCercano, fechaLuna));

				if(diasDeDiferenciaEntreLunaYSI < diasMinimosEntreLunaYSI) {

					diasMinimosEntreLunaYSI = diasDeDiferenciaEntreLunaYSI;
					inicioPrimerMesAnyo.setDate(luna.getDate());
					inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Math.abs(ChronoUnit.DAYS.between(date, fechaLuna)));
				}
			}

			if(luna.isSelecta()) {

				long diasDeDiferenciaEntreAponovoYDate = Math.abs(ChronoUnit.DAYS.between(date, fechaLuna));

				if(diasDeDiferenciaEntreAponovoYDate < diasMinimosEntreAponovoYDate) {

					diasMinimosEntreAponovoYDate = diasDeDiferenciaEntreAponovoYDate;
					cambioDeAponovo.setDate(luna.getDate());
					cambioDeAponovo.setDiasDeDiferenciaConDate(diasMinimosEntreAponovoYDate);
				}
			}
		}
	}


	/**
	 * EN: Inserts the sixteen fixed festivity rows. Does nothing if the table already has rows.
	 * ES: Inserta las dieciséis filas fijas de festividades. No hace nada si la tabla ya tiene
	 * filas.
	 *
	 * @return EN: message describing the outcome. / ES: mensaje que describe el resultado.
	 */
	@Transactional
	public String poblateFestividades() {

		LOG.info("Actualizando las Festividades.");

		if(this.festividadesRepository.count() > 0) {

			LOG.warn("Ya hay festividades en la base de datos.");
			return "Error al actualizar las festividades: ya hay festividades en la base de datos.";
		}

		List<FestividadesEntity> festividadParaDDB = new ArrayList<>();

		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_ECLIPENO_IAR_CODE, "Cambio de eclípeno invernal apofasal remoto", false, "Eclipse, solsticio de invierno, apogeo y luna nueva"));

		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_ECLIPENO_CODE, "Cambio de eclípeno invernal nuevo", false, "Eclipse, solsticio de invierno, luna nueva"));
		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_METONO_IAR_CODE, "Cambio de métono invernal apofasal remoto", false, "Solsticio de invierno, apogeo y luna nueva"));

		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_METONO_IN_CODE, "Cambio de métono invernal nuevo", false, "Solsticio de invierno, luna nueva"));
		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_METONO_IA_CODE, "Cambio de métono invernal apórico", false, "Solsticio de invierno y apogeo"));

		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_ANYO_CODE, "Cambio de año", false, "Solsticio de invierno"));
		festividadParaDDB.add(this.crearFestividad(INICIO_ANYO_CODE, "Inicio del primer mes del año", true, "Primera luna nueva despues de solsticio de invierno"));
		festividadParaDDB.add(this.crearFestividad(MIDSISON_INVERNAL_CODE, "Midsison invernal", false, "Dia equidistante entre solsticio de invierno y equinoccio de primavera"));

		festividadParaDDB.add(this.crearFestividad(BIENVENIDA_PRIMAVERA_CODE, "Bienvenida de la primavera", false, "Equinoccio de primavera"));
		festividadParaDDB.add(this.crearFestividad(MIDSISON_PRIMAVERAL_CODE, "Midsison primaveral", false, "Dia equidistante entre sequinoccio de primavera y solsticio de verano"));

		festividadParaDDB.add(this.crearFestividad(MITAD_ANYO_CODE, "Mitad del año", false, "Solsticio de verano"));
		festividadParaDDB.add(this.crearFestividad(MIDSISON_ESTIVAL_CODE, "Midsison estival", false, "Dia equidistante entre solsticio de verano y equinoccio de otoño"));

		festividadParaDDB.add(this.crearFestividad(ENTRADA_OTONYO_CODE, "Entrada del otoño", false, "Equinoccio de otoño"));
		festividadParaDDB.add(this.crearFestividad(MIDSISON_OTONYAL_CODE, "Midsison otoñal", false, "Dia equidistante entre equinoccio de otoño y solsticio de invierno"));

		festividadParaDDB.add(this.crearFestividad(CAMBIO_DE_APONOVO_CODE, "Cambio de aponovo", false, "Luna nueva en apogeo"));
		festividadParaDDB.add(this.crearFestividad(MIDSISON_APONOVAL_CODE, "Midsison aponoval", false, "Midsison y cambio de aponovo"));

		this.festividadesRepository.saveAll(festividadParaDDB);

		LOG.info("Festividades actualizadas");

		return "Festividades actualizadas correctamente.";
	}

	/**
	 * EN: Builds one festivity row in memory.
	 * ES: Construye en memoria una fila de festividad.
	 *
	 * @param code        EN: short code identifying it. / ES: código corto que la identifica.
	 * @param name        EN: name shown to the user. / ES: nombre que se muestra al usuario.
	 * @param lunar       EN: whether it depends on the moon rather than the sun. / ES: si depende de la luna y no del sol.
	 * @param descripcion EN: astronomical phenomenon behind it. / ES: fenómeno astronómico que hay detrás.
	 * @return EN: the entity, not yet persisted. / ES: la entidad, todavía sin persistir.
	 */
	private FestividadesEntity crearFestividad(String code, String name, boolean lunar, String descripcion) {

		FestividadesEntity newFestividad = new FestividadesEntity();

		newFestividad.setCode(code);
		newFestividad.setNombre(name);
		newFestividad.setLunar(lunar);
		newFestividad.setDescripcion(descripcion);

		return newFestividad;
	}
}
