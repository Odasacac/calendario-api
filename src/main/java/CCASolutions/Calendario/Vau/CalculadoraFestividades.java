package CCASolutions.Calendario.Vau;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import CCASolutions.Calendario.DTOs.FestividadesDTO;
import CCASolutions.Calendario.DTOs.MinimaFestividadesDTO;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Repositories.LunasRepository;
import CCASolutions.Calendario.Support.CatalogoCalendario;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: The festivity of the day, the previous one and the next one.
 *
 *     A festivity is a specific notable event, or the combination of two or more.
 *     In chronological order within a year:
 *
 *       CEAR - change of winter apofasal remote eclipeno
 *       CE   - change of winter new eclipeno
 *       CMAR - change of winter apofasal remote metono
 *       CMF  - change of winter new metono
 *       CMA  - change of winter aporic metono
 *       CA   - change of year (winter solstice)
 *       IA   - start of the first month of the year (first new moon after the solstice)
 *       MSI  - winter midsison (midpoint between two solstices/equinoxes)
 *       BP   - welcome of spring (spring equinox)
 *       MSP  - spring midsison
 *       MA   - half of the year (summer solstice)
 *       MSE  - summer midsison
 *       DV   - farewell to summer (last full moon before the autumn equinox)
 *       MSO  - autumn midsison
 *       EO   - entry of autumn (autumn equinox)
 *       DA   - farewell to the year (last full moon before the winter solstice)
 *       LA   - change of aponovo (nearest selected new moon)
 *       MAP  - aponoval midsison (a midsison that falls on a change of aponovo)
 *
 *     What changed here:
 *
 *       - the eighteen festivity codes were local String variables rebuilt on every
 *         request; they are static constants now;
 *       - the names used to be resolved by scanning the whole festividades table
 *         once per festivity - a findAll() plus up to four linear searches per
 *         request; now it is a map lookup in the in-memory catalog;
 *       - the nearest change of aponovo was found by walking a centuries-long list
 *         of lunar phases, because selected new moons are about eighteen months
 *         apart and cannot be found in a short window; it is now two indexed TOP-1
 *         queries;
 *       - the inner "nearest solstice to this full moon" search ran once per full
 *         moon over the whole phase history, which was the second worst hot spot of
 *         the request (tens of thousands of iterations); over the bounded window it
 *         is a few hundred;
 *       - day distances are epoch-day subtractions instead of ChronoUnit calls.
 *
 *     The rules themselves, including their quirks, are preserved: the codes, the
 *     priority between simultaneous festivities and the suppression rules all
 *     produce the same output as before.
 *
 * ES: La festividad del dia, la anterior y la siguiente.
 *
 *     Una festividad es un evento resenable concreto, o la combinacion de dos o mas.
 *     En orden cronologico dentro de un ano:
 *
 *       CEAR - cambio de eclipeno invernal apofasal remoto
 *       CE   - cambio de eclipeno invernal nuevo
 *       CMAR - cambio de metono invernal apofasal remoto
 *       CMF  - cambio de metono invernal nuevo
 *       CMA  - cambio de metono invernal aporico
 *       CA   - cambio de ano (solsticio de invierno)
 *       IA   - inicio del primer mes del ano (primera luna nueva tras el solsticio)
 *       MSI  - midsison invernal (punto medio entre dos solsticios/equinoccios)
 *       BP   - bienvenida de la primavera (equinoccio de primavera)
 *       MSP  - midsison primaveral
 *       MA   - mitad del ano (solsticio de verano)
 *       MSE  - midsison estival
 *       DV   - despedida del verano (ultima luna llena antes del equinoccio de otono)
 *       MSO  - midsison otonal
 *       EO   - entrada del otono (equinoccio de otono)
 *       DA   - despedida del ano (ultima luna llena antes del solsticio de invierno)
 *       LA   - cambio de aponovo (luna nueva selecta mas cercana)
 *       MAP  - midsison aponoval (un midsison que cae en un cambio de aponovo)
 *
 *     Que ha cambiado aqui:
 *
 *       - los dieciocho codigos de festividad eran variables String locales que se
 *         reconstruian en cada peticion; ahora son constantes estaticas;
 *       - los nombres se resolvian recorriendo la tabla festividades completa una vez por
 *         festividad - un findAll() mas hasta cuatro busquedas lineales por peticion;
 *         ahora es una consulta a un mapa del catalogo en memoria;
 *       - el cambio de aponovo mas cercano se buscaba recorriendo una lista de fases
 *         lunares de siglos, porque las lunas nuevas selectas estan a unos dieciocho meses
 *         unas de otras y no se pueden encontrar en una ventana corta; ahora son dos
 *         consultas TOP-1 sobre indice;
 *       - la busqueda interna de "el solsticio mas cercano a esta luna llena" se ejecutaba
 *         una vez por luna llena sobre todo el historico de fases, y era el segundo peor
 *         punto caliente de la peticion (decenas de miles de iteraciones); sobre la ventana
 *         acotada son unos cientos;
 *       - las distancias en dias son restas de dias epoch en lugar de llamadas a ChronoUnit.
 *
 *     Las reglas en si, incluidas sus rarezas, se conservan: los codigos, la prioridad
 *     entre festividades simultaneas y las reglas de supresion producen la misma salida
 *     que antes.
 * ==============================================================================
 */
@Component
public class CalculadoraFestividades {

	private static final Logger log = LoggerFactory.getLogger(CalculadoraFestividades.class);

	// EN: Festivity codes. ES: Codigos de festividad.
	private static final String CAMBIO_DE_ECLIPENO = "CE";
	private static final String CAMBIO_DE_ECLIPENO_IAR = "CEAR";
	private static final String CAMBIO_DE_METONO_IN = "CMF";
	private static final String CAMBIO_DE_METONO_IA = "CMA";
	private static final String CAMBIO_DE_METONO_IAR = "CMAR";
	private static final String CAMBIO_DE_ANYO = "CA";
	private static final String BIENVENIDA_PRIMAVERA = "BP";
	private static final String MITAD_ANYO = "MA";
	private static final String ENTRADA_OTONYO = "EO";
	private static final String INICIO_ANYO = "IA";
	private static final String DESPEDIDA_VERANO = "DV";
	private static final String DESPEDIDA_ANYO = "DA";
	private static final String MIDSISON_INVERNAL = "MSI";
	private static final String MIDSISON_PRIMAVERAL = "MSP";
	private static final String MIDSISON_ESTIVAL = "MSE";
	private static final String MIDSISON_OTONYAL = "MSO";
	private static final String CAMBIO_DE_APONOVO = "LA";
	private static final String MIDSISON_APONOVAL = "MAP";

	/*
	 * EN: A festivity closer than this many days hides the ones it contains: an
	 *     eclipeno hides the start of the first month and the metono changes, and a
	 *     metono hides the start of the first month.
	 * ES: Una festividad mas cercana que estos dias oculta las que contiene: un eclipeno
	 *     oculta el inicio del primer mes y los cambios de metono, y un metono oculta el
	 *     inicio del primer mes.
	 */
	private static final long DIAS_DE_ABSORCION = 100L;

	private final CatalogoCalendario catalogo;
	private final LunasRepository lunasRepository;

	public CalculadoraFestividades(CatalogoCalendario catalogo, LunasRepository lunasRepository) {
		this.catalogo = catalogo;
		this.lunasRepository = lunasRepository;
	}

	// =========================================================================
	// EN: ENTRY POINT - splits the candidates into today, past and future.
	// ES: PUNTO DE ENTRADA - reparte los candidatos en hoy, pasado y futuro.
	// =========================================================================

	public FestividadesDTO calcular(ContextoCosmico contexto) {

		FestividadesDTO festividades = new FestividadesDTO();

		List<MinimaFestividadesDTO> candidatas = candidatas(contexto);

		final long diaDeLaFecha = contexto.getDiaEpoch();

		List<MinimaFestividadesDTO> actuales = new ArrayList<>(4);
		List<MinimaFestividadesDTO> pasadas = new ArrayList<>(candidatas.size());
		List<MinimaFestividadesDTO> futuras = new ArrayList<>(candidatas.size());

		for (MinimaFestividadesDTO candidata : candidatas) {

			if (candidata.getDiasDeDiferenciaConDate() == 0) {

				actuales.add(candidata);

			} else if (candidata.getDate() == null) {

				/*
				 * EN: A dated-less candidate at a non-zero distance can only happen when a
				 *     suppression rule pushed an unfound festivity away; the original threw a
				 *     NullPointerException here.
				 * ES: Una candidata sin fecha a distancia no nula solo puede darse cuando una
				 *     regla de supresion aparta una festividad no encontrada; el original
				 *     lanzaba aqui un NullPointerException.
				 */
				log.warn("La festividad {} no tiene fecha; se descarta.", candidata.getCode());

			} else {

				long diaDeLaFestividad = Fechas.diaEpoch(candidata.getDate());
				if (diaDeLaFestividad > diaDeLaFecha) {
					futuras.add(candidata);
				} else if (diaDeLaFestividad < diaDeLaFecha) {
					pasadas.add(candidata);
				}
			}
		}

		festividades.setFestividadActual(festividadActual(actuales));
		festividades.setFestividadAnterior(nombreConTiempo(masCercana(pasadas), "hace"));
		festividades.setFestividadProxima(nombreConTiempo(masCercana(futuras), "dentro de"));

		return festividades;
	}

	// =========================================================================
	// EN: CANDIDATES - every festivity that could be the closest one.
	// ES: CANDIDATAS - todas las festividades que podrian ser la mas cercana.
	// =========================================================================

	private List<MinimaFestividadesDTO> candidatas(ContextoCosmico contexto) {

		List<MinimaFestividadesDTO> candidatas = new ArrayList<>(16);
		final long diaDeLaFecha = contexto.getDiaEpoch();

		// ---------------------------------------------------------------------
		// EN: 1 - Change of eclipeno, plain and apofasal remote.
		// ES: 1 - Cambio de eclipeno, normal y apofasal remoto.
		// ---------------------------------------------------------------------
		MinimaFestividadesDTO cambioDeEclipeno = nueva(CAMBIO_DE_ECLIPENO);
		MinimaFestividadesDTO cambioDeEclipenoIAR = nueva(CAMBIO_DE_ECLIPENO_IAR);
		long diasAlCE = Long.MAX_VALUE;
		long diasAlCEAR = Long.MAX_VALUE;
		boolean esHoyCE = false;
		boolean esHoyCEAR = false;

		for (EclipenosEntity eclipeno : contexto.getEclipenos()) {

			if (!eclipeno.isInvernal() || !eclipeno.isNuevo()) {
				continue;
			}

			long diaDelEclipeno = Fechas.diaEpoch(eclipeno.getDate());
			boolean esRemoto = eclipeno.isApofasal() && eclipeno.isSelecto();

			if (diaDelEclipeno == diaDeLaFecha) {

				if (esRemoto) {
					hoy(cambioDeEclipenoIAR, eclipeno.getDate());
					esHoyCEAR = true;
				} else {
					hoy(cambioDeEclipeno, eclipeno.getDate());
					esHoyCE = true;
				}

			} else {

				long distancia = Fechas.diasAbs(diaDelEclipeno, diaDeLaFecha);

				/*
				 * EN: Careful, this is intentional: an apofasal remote eclipeno only competes
				 *     for CEAR while today is not already a CEAR. Once it is, it falls through
				 *     to the plain CE branch, exactly as in the original.
				 * ES: Cuidado, esto es intencionado: un eclipeno apofasal remoto solo compite
				 *     por CEAR mientras hoy no sea ya un CEAR. En cuanto lo es, cae a la rama
				 *     del CE normal, exactamente como en el original.
				 */
				if (esRemoto && !esHoyCEAR) {
					if (distancia < diasAlCEAR) {
						diasAlCEAR = distancia;
						a(cambioDeEclipenoIAR, eclipeno.getDate(), distancia);
					}
				} else if (distancia < diasAlCE && !esHoyCE) {
					diasAlCE = distancia;
					a(cambioDeEclipeno, eclipeno.getDate(), distancia);
				}
			}
		}

		candidatas.add(cambioDeEclipeno);
		candidatas.add(cambioDeEclipenoIAR);

		// ---------------------------------------------------------------------
		// EN: 2 - Change of metono: new (fasal), aporic and apofasal remote.
		// ES: 2 - Cambio de metono: nuevo (fasal), aporico y apofasal remoto.
		// ---------------------------------------------------------------------
		MinimaFestividadesDTO cambioDeMetonoIN = nueva(CAMBIO_DE_METONO_IN);
		MinimaFestividadesDTO cambioDeMetonoIA = nueva(CAMBIO_DE_METONO_IA);
		MinimaFestividadesDTO cambioDeMetonoIAR = nueva(CAMBIO_DE_METONO_IAR);
		long diasAlCMIN = Long.MAX_VALUE;
		long diasAlCMIA = Long.MAX_VALUE;
		long diasAlCMIAR = Long.MAX_VALUE;
		boolean esHoyCMN = false;
		boolean esHoyCMA = false;
		boolean esHoyCMAR = false;

		for (MetonsEntity metono : contexto.getMetonos()) {

			if (!metono.isInvernal()) {
				continue;
			}

			boolean esNuevo = metono.isNuevo();
			boolean esAporico = !esNuevo && metono.isAporico();
			if (!esNuevo && !esAporico) {
				continue;
			}

			long diaDelMetono = Fechas.diaEpoch(metono.getDate());
			boolean esRemoto = metono.isApofasal() && metono.isSelecto();
			MinimaFestividadesDTO propia = esNuevo ? cambioDeMetonoIN : cambioDeMetonoIA;

			if (diaDelMetono == diaDeLaFecha) {

				if (esRemoto) {
					hoy(cambioDeMetonoIAR, metono.getDate());
					esHoyCMAR = true;
				} else {
					hoy(propia, metono.getDate());
					if (esNuevo) {
						esHoyCMN = true;
					} else {
						esHoyCMA = true;
					}
				}

			} else {

				long distancia = Fechas.diasAbs(diaDelMetono, diaDeLaFecha);

				if (esRemoto && !esHoyCMAR) {
					if (distancia < diasAlCMIAR) {
						diasAlCMIAR = distancia;
						a(cambioDeMetonoIAR, metono.getDate(), distancia);
					}
				} else if (esNuevo) {
					if (distancia < diasAlCMIN && !esHoyCMN) {
						diasAlCMIN = distancia;
						a(cambioDeMetonoIN, metono.getDate(), distancia);
					}
				} else {
					if (distancia < diasAlCMIA && !esHoyCMA) {
						diasAlCMIA = distancia;
						a(cambioDeMetonoIA, metono.getDate(), distancia);
					}
				}
			}
		}

		/*
		 * EN: A cycle with no plain metono change of its own borrows the date of the
		 *     apofasal remote one, keeping its own (unreachable) distance.
		 * ES: Un ciclo sin cambio de metono normal propio toma prestada la fecha del
		 *     apofasal remoto, conservando su propia distancia (inalcanzable).
		 */
		if (cambioDeMetonoIN.getDate() == null) {
			cambioDeMetonoIN.setDate(cambioDeMetonoIAR.getDate());
		}
		if (cambioDeMetonoIA.getDate() == null) {
			cambioDeMetonoIA.setDate(cambioDeMetonoIAR.getDate());
		}

		candidatas.add(cambioDeMetonoIN);
		candidatas.add(cambioDeMetonoIA);
		candidatas.add(cambioDeMetonoIAR);

		// ---------------------------------------------------------------------
		// EN: 3 - Solstices and equinoxes, plus the midsison between the two that
		//     surround the date.
		// ES: 3 - Solsticios y equinoccios, mas el midsison entre los dos que rodean la
		//     fecha.
		// ---------------------------------------------------------------------
		MinimaFestividadesDTO cambioDeAnyo = nueva(CAMBIO_DE_ANYO);
		MinimaFestividadesDTO bienvenidaPrimavera = nueva(BIENVENIDA_PRIMAVERA);
		MinimaFestividadesDTO mitadAnyo = nueva(MITAD_ANYO);
		MinimaFestividadesDTO entradaOtonyo = nueva(ENTRADA_OTONYO);
		long diasAlCA = Long.MAX_VALUE;
		long diasAlBP = Long.MAX_VALUE;
		long diasAlMA = Long.MAX_VALUE;
		long diasAlEO = Long.MAX_VALUE;
		boolean esHoyCA = false;
		boolean esHoyBP = false;
		boolean esHoyMA = false;
		boolean esHoyEO = false;

		SolsticiosYEquinocciosEntity soeAnterior = null;
		SolsticiosYEquinocciosEntity soeSiguiente = null;
		long diasAlSoeAnterior = Long.MAX_VALUE;
		long diasAlSoeSiguiente = Long.MAX_VALUE;

		// EN: The nearest solstice/equinox of each kind, needed by the lunar festivities.
		// ES: El solsticio/equinoccio mas cercano de cada tipo, necesario para las lunares.
		SolsticiosYEquinocciosEntity solsticioInvernalMasCercano = null;
		SolsticiosYEquinocciosEntity equinoccioOtonyalMasCercano = null;

		for (SolsticiosYEquinocciosEntity soe : contexto.getSoes()) {

			long diaDelSoe = Fechas.diaEpoch(soe.getDate());

			if (diaDelSoe == diaDeLaFecha) {

				if (soe.isSolsticioInvierno()) {
					hoy(cambioDeAnyo, soe.getDate());
					solsticioInvernalMasCercano = soe;
					esHoyCA = true;
				} else if (soe.isEquinoccioPrimavera()) {
					hoy(bienvenidaPrimavera, soe.getDate());
					esHoyBP = true;
				} else if (soe.isSolsticioVerano()) {
					hoy(mitadAnyo, soe.getDate());
					esHoyMA = true;
				} else if (soe.isEquinoccioOtonyo()) {
					hoy(entradaOtonyo, soe.getDate());
					equinoccioOtonyalMasCercano = soe;
					esHoyEO = true;
				}

				continue;
			}

			/*
			 * EN: The solstice/equinox the date sits between. A date that falls exactly on
			 *     one of them is skipped above, so the pair always surrounds it.
			 * ES: El solsticio/equinoccio entre los que se sitúa la fecha. Una fecha que cae
			 *     exactamente en uno de ellos se salta arriba, asi que la pareja siempre la
			 *     rodea.
			 */
			if (diaDelSoe < diaDeLaFecha) {
				long distancia = diaDeLaFecha - diaDelSoe;
				if (distancia < diasAlSoeAnterior) {
					diasAlSoeAnterior = distancia;
					soeAnterior = soe;
				}
			} else {
				long distancia = diaDelSoe - diaDeLaFecha;
				if (distancia < diasAlSoeSiguiente) {
					diasAlSoeSiguiente = distancia;
					soeSiguiente = soe;
				}
			}

			long distancia = Fechas.diasAbs(diaDelSoe, diaDeLaFecha);

			if (soe.isSolsticioInvierno() && !esHoyCA) {
				if (distancia < diasAlCA) {
					diasAlCA = distancia;
					a(cambioDeAnyo, soe.getDate(), distancia);
					solsticioInvernalMasCercano = soe;
				}
			} else if (soe.isEquinoccioPrimavera() && !esHoyBP) {
				if (distancia < diasAlBP) {
					diasAlBP = distancia;
					a(bienvenidaPrimavera, soe.getDate(), distancia);
				}
			} else if (soe.isSolsticioVerano() && !esHoyMA) {
				if (distancia < diasAlMA) {
					diasAlMA = distancia;
					a(mitadAnyo, soe.getDate(), distancia);
				}
			} else if (soe.isEquinoccioOtonyo() && !esHoyEO) {
				if (distancia < diasAlEO) {
					diasAlEO = distancia;
					a(entradaOtonyo, soe.getDate(), distancia);
					equinoccioOtonyalMasCercano = soe;
				}
			}
		}

		candidatas.add(cambioDeAnyo);
		candidatas.add(bienvenidaPrimavera);
		candidatas.add(mitadAnyo);
		candidatas.add(entradaOtonyo);

		/*
		 * EN: The midsison sits halfway between the surrounding pair. At the very edge of
		 *     the generated data one of the two is missing; the original dereferenced a
		 *     sentinel with a null date and the request ended in a 500, so here the
		 *     midsison and the aponoval midsison are simply left out.
		 * ES: El midsison esta a mitad de camino entre la pareja que rodea la fecha. En el
		 *     borde mismo de los datos generados falta uno de los dos; el original
		 *     desreferenciaba un centinela con la fecha a null y la peticion acababa en un
		 *     500, asi que aqui el midsison y el midsison aponoval simplemente se omiten.
		 */
		MinimaFestividadesDTO midsison = null;
		if (soeAnterior != null && soeSiguiente != null) {
			midsison = nueva(codigoDeMidsison(soeAnterior.getStartingSeason()));
			LocalDateTime diaDelMidsison = Fechas.puntoMedio(soeAnterior.getDate(), soeSiguiente.getDate());
			midsison.setDate(diaDelMidsison);
			midsison.setDiasDeDiferenciaConDate(Fechas.diasAbs(diaDeLaFecha, Fechas.diaEpoch(diaDelMidsison)));
			candidatas.add(midsison);
		} else {
			log.warn("No se puede situar el midsison de {}: falta el solsticio/equinoccio anterior o el siguiente.",
					contexto.getFecha());
		}

		// ---------------------------------------------------------------------
		// EN: 4 - Lunar festivities: start of the first month of the year, farewell to
		//     summer, farewell to the year and change of aponovo.
		// ES: 4 - Festividades lunares: inicio del primer mes del ano, despedida del
		//     verano, despedida del ano y cambio de aponovo.
		// ---------------------------------------------------------------------
		MinimaFestividadesDTO inicioPrimerMesAnyo = nueva(INICIO_ANYO);
		MinimaFestividadesDTO despedidaVerano = nueva(DESPEDIDA_VERANO);
		MinimaFestividadesDTO despedidaAnyo = nueva(DESPEDIDA_ANYO);

		if (solsticioInvernalMasCercano != null) {

			long diaDelSolsticioInvernal = Fechas.diaEpoch(solsticioInvernalMasCercano.getDate());
			long diaDelEquinoccioOtonyal = equinoccioOtonyalMasCercano == null
					? Long.MIN_VALUE
					: Fechas.diaEpoch(equinoccioOtonyalMasCercano.getDate());

			long diasEntreLunaYSolsticio = Long.MAX_VALUE;
			long diasEntreDVYLuna = Long.MAX_VALUE;
			long diasEntreDAYLuna = Long.MAX_VALUE;

			for (LunasEntity luna : contexto.getLunas()) {

				long diaDeLaLuna = Fechas.diaEpoch(luna.getDate());

				if (luna.isNueva()) {

					// EN: The year's first month starts on the first new moon after the solstice.
					// ES: El primer mes del ano empieza en la primera luna nueva tras el solsticio.
					if (diaDelSolsticioInvernal < diaDeLaLuna) {
						long distancia = Fechas.diasAbs(diaDelSolsticioInvernal, diaDeLaLuna);
						if (distancia < diasEntreLunaYSolsticio) {
							diasEntreLunaYSolsticio = distancia;
							a(inicioPrimerMesAnyo, luna.getDate(), Fechas.diasAbs(diaDeLaFecha, diaDeLaLuna));
						}
					}

				} else if (luna.isLlena()) {

					/*
					 * EN: A full moon closes a season when the solstice or equinox it belongs to
					 *     is the one that closes it. Finding "the solstice this full moon belongs
					 *     to" is the inner search that used to dominate the request.
					 * ES: Una luna llena cierra una estacion cuando el solsticio o equinoccio al
					 *     que pertenece es el que la cierra. Hallar "el solsticio al que pertenece
					 *     esta luna llena" es la busqueda interna que dominaba la peticion.
					 */
					SolsticiosYEquinocciosEntity soeDeLaLuna = soeMasCercano(contexto.getSoes(), diaDeLaLuna);
					if (soeDeLaLuna == null) {
						continue;
					}
					long diaDelSoeDeLaLuna = Fechas.diaEpoch(soeDeLaLuna.getDate());

					if (soeDeLaLuna.getStartingSeason() == 4
							&& diaDelSoeDeLaLuna == diaDelEquinoccioOtonyal
							&& diaDeLaLuna < diaDelEquinoccioOtonyal) {

						long distancia = Fechas.diasAbs(diaDelSolsticioInvernal, diaDeLaLuna);
						if (distancia < diasEntreDVYLuna) {
							diasEntreDVYLuna = distancia;
							a(despedidaVerano, luna.getDate(), Fechas.diasAbs(diaDeLaLuna, diaDeLaFecha));
						}

					} else if (soeDeLaLuna.getStartingSeason() == 1
							&& diaDelSoeDeLaLuna == diaDelSolsticioInvernal
							&& diaDeLaLuna < diaDelSolsticioInvernal) {

						long distancia = Fechas.diasAbs(diaDelSolsticioInvernal, diaDeLaLuna);
						if (distancia < diasEntreDAYLuna) {
							diasEntreDAYLuna = distancia;
							a(despedidaAnyo, luna.getDate(), Fechas.diasAbs(diaDeLaLuna, diaDeLaFecha));
						}
					}
				}
			}
		}

		MinimaFestividadesDTO cambioDeAponovo = cambioDeAponovo(contexto);

		/*
		 * EN: Suppression rules. When an eclipeno is near there is neither a start of the
		 *     first month nor a metono change, and when a metono is near there is no start
		 *     of the first month: the bigger cycle absorbs the smaller ones.
		 * ES: Reglas de supresion. Si hay un eclipeno cerca no hay festividad de inicio del
		 *     primer mes ni de cambio de metono, y si hay un metono cerca no hay inicio del
		 *     primer mes: el ciclo mayor absorbe a los menores.
		 */
		if (cambioDeEclipeno.getDiasDeDiferenciaConDate() < DIAS_DE_ABSORCION
				|| cambioDeEclipenoIAR.getDiasDeDiferenciaConDate() < DIAS_DE_ABSORCION) {

			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
			cambioDeMetonoIN.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
			cambioDeMetonoIA.setDiasDeDiferenciaConDate(Long.MAX_VALUE);

		} else if (cambioDeMetonoIN.getDiasDeDiferenciaConDate() < DIAS_DE_ABSORCION
				|| cambioDeMetonoIA.getDiasDeDiferenciaConDate() < DIAS_DE_ABSORCION
				|| cambioDeMetonoIAR.getDiasDeDiferenciaConDate() < DIAS_DE_ABSORCION) {

			inicioPrimerMesAnyo.setDiasDeDiferenciaConDate(Long.MAX_VALUE);
		}

		candidatas.add(inicioPrimerMesAnyo);
		candidatas.add(despedidaVerano);
		candidatas.add(despedidaAnyo);
		candidatas.add(cambioDeAponovo);

		// ---------------------------------------------------------------------
		// EN: 5 - Aponoval midsison: a midsison that falls on a change of aponovo.
		// ES: 5 - Midsison aponoval: un midsison que cae en un cambio de aponovo.
		// ---------------------------------------------------------------------
		if (midsison != null && midsison.getDate() != null) {

			MinimaFestividadesDTO midsisonAponoval = nueva(MIDSISON_APONOVAL);
			midsisonAponoval.setDate(midsison.getDate());
			midsisonAponoval.setDiasDeDiferenciaConDate(Long.MAX_VALUE);

			if (cambioDeAponovo.getDate() != null
					&& Fechas.diaEpoch(cambioDeAponovo.getDate()) == Fechas.diaEpoch(midsison.getDate())) {
				midsisonAponoval.setDiasDeDiferenciaConDate(midsison.getDiasDeDiferenciaConDate());
			}

			candidatas.add(midsisonAponoval);
		}

		return candidatas;
	}

	/*
	 * EN: The nearest change of aponovo (selected new moon) in either direction.
	 *     Selected new moons are roughly eighteen months apart, so this is the one
	 *     lookup that genuinely needs to reach outside the bounded window. Two indexed
	 *     TOP-1 queries over the same range the old list covered replace the walk over
	 *     the full phase history; on a tie the earlier one wins, as the old ascending
	 *     scan did.
	 * ES: El cambio de aponovo (luna nueva selecta) mas cercano en cualquier direccion.
	 *     Las lunas nuevas selectas estan a unos dieciocho meses unas de otras, asi que
	 *     esta es la unica busqueda que necesita de verdad salir de la ventana acotada.
	 *     Dos consultas TOP-1 sobre indice en el mismo rango que cubria la lista antigua
	 *     sustituyen el recorrido por todo el historico de fases; en caso de empate gana la
	 *     mas antigua, como hacia el recorrido ascendente antiguo.
	 */
	private MinimaFestividadesDTO cambioDeAponovo(ContextoCosmico contexto) {

		MinimaFestividadesDTO cambioDeAponovo = nueva(CAMBIO_DE_APONOVO);

		LocalDateTime desde = contexto.getUltimoMetonoIApofasalRemoto().getDate().minusYears(1);
		LocalDateTime hasta = Fechas.finDelDia(contexto.getFecha()).plusYears(1);

		LunasEntity anterior = this.lunasRepository.findFirstByNuevaTrueAndSelectaTrueAndDateBetweenOrderByDateDesc(
				desde, Fechas.finDelDia(contexto.getFecha()));
		LunasEntity siguiente = this.lunasRepository.findFirstByNuevaTrueAndSelectaTrueAndDateBetweenOrderByDateAsc(
				Fechas.despuesDelDia(contexto.getFecha()), hasta);

		long diasAlAnterior = anterior == null
				? Long.MAX_VALUE
				: Fechas.diasAbs(contexto.getDiaEpoch(), Fechas.diaEpoch(anterior.getDate()));
		long diasAlSiguiente = siguiente == null
				? Long.MAX_VALUE
				: Fechas.diasAbs(contexto.getDiaEpoch(), Fechas.diaEpoch(siguiente.getDate()));

		if (diasAlAnterior <= diasAlSiguiente && anterior != null) {
			a(cambioDeAponovo, anterior.getDate(), diasAlAnterior);
		} else if (siguiente != null) {
			a(cambioDeAponovo, siguiente.getDate(), diasAlSiguiente);
		} else {
			log.warn("No se ha encontrado ningún cambio de aponovo alrededor de {}.", contexto.getFecha());
		}

		return cambioDeAponovo;
	}

	/*
	 * EN: The solstice or equinox nearest to a given day. Ties keep the first one, as
	 *     the original strict comparison did.
	 * ES: El solsticio o equinoccio mas cercano a un dia dado. Los empates conservan el
	 *     primero, como hacia la comparacion estricta original.
	 */
	private static SolsticiosYEquinocciosEntity soeMasCercano(List<SolsticiosYEquinocciosEntity> soes, long dia) {

		SolsticiosYEquinocciosEntity masCercano = null;
		long menorDistancia = Long.MAX_VALUE;

		for (SolsticiosYEquinocciosEntity soe : soes) {
			long distancia = Fechas.diasAbs(Fechas.diaEpoch(soe.getDate()), dia);
			if (distancia < menorDistancia) {
				menorDistancia = distancia;
				masCercano = soe;
			}
		}

		return masCercano;
	}

	// =========================================================================
	// EN: NAMING
	// ES: NOMBRADO
	// =========================================================================

	/*
	 * EN: Today's festivity. One candidate is reported directly; several are collapsed
	 *     by a fixed priority, except for the special case of a midsison falling on a
	 *     change of aponovo, which becomes the aponoval midsison.
	 * ES: La festividad de hoy. Una sola candidata se informa directamente; varias se
	 *     resuelven por una prioridad fija, salvo el caso especial de un midsison que cae
	 *     en un cambio de aponovo, que pasa a ser el midsison aponoval.
	 */
	private String festividadActual(List<MinimaFestividadesDTO> actuales) {

		if (actuales.isEmpty()) {
			return "";
		}

		if (actuales.size() == 1) {
			return nombreOVacio(actuales.get(0).getCode());
		}

		boolean hayMidsison = false;
		boolean hayAponovo = false;

		for (MinimaFestividadesDTO festividad : actuales) {
			String code = festividad.getCode();
			if (MIDSISON_INVERNAL.equals(code) || MIDSISON_PRIMAVERAL.equals(code) || MIDSISON_ESTIVAL.equals(code)
					|| MIDSISON_OTONYAL.equals(code)) {
				hayMidsison = true;
			}
			if (CAMBIO_DE_APONOVO.equals(code)) {
				hayAponovo = true;
			}
		}

		if (actuales.size() == 2 && hayMidsison && hayAponovo) {
			return nombreOVacio(MIDSISON_APONOVAL);
		}

		/*
		 * EN: Priority between simultaneous changes of cycle: the bigger cycle wins.
		 *     Note that "CMN" is checked but never produced - the new-metono code is
		 *     "CMF" - which is how the original behaved, so it is kept as is.
		 * ES: Prioridad entre cambios de ciclo simultaneos: gana el ciclo mayor. Notese que
		 *     se comprueba "CMN" pero nunca se produce - el codigo del metono nuevo es
		 *     "CMF" - que es como se comportaba el original, asi que se mantiene igual.
		 */
		String elegido = "";

		for (MinimaFestividadesDTO festividad : actuales) {

			String code = festividad.getCode();

			switch (code == null ? "" : code) {
				case "CEAR":
					elegido = code;
					break;
				case "CMAR":
					if (!elegido.equals("CEAR")) {
						elegido = code;
					}
					break;
				case "CE":
					if (!elegido.equals("CEAR") && !elegido.equals("CMAR")) {
						elegido = code;
					}
					break;
				case "CMN":
					if (!elegido.equals("CE") && !elegido.equals("CEAR") && !elegido.equals("CMAR")) {
						elegido = code;
					}
					break;
				case "CMA":
					if (!elegido.equals("CMN") && !elegido.equals("CE") && !elegido.equals("CEAR")
							&& !elegido.equals("CMAR")) {
						elegido = code;
					}
					break;
				case "CA":
					if (!elegido.equals("CMA") && !elegido.equals("CMN") && !elegido.equals("CE")
							&& !elegido.equals("CEAR") && !elegido.equals("CMAR")) {
						elegido = code;
					}
					break;
				default:
					break;
			}
		}

		return nombreOVacio(elegido);
	}

	/*
	 * EN: The closest candidate of a group, by day distance.
	 * ES: La candidata mas cercana de un grupo, por distancia en dias.
	 */
	private static MinimaFestividadesDTO masCercana(List<MinimaFestividadesDTO> festividades) {

		MinimaFestividadesDTO masCercana = null;
		long menorDistancia = Long.MAX_VALUE;

		for (MinimaFestividadesDTO festividad : festividades) {
			if (festividad.getDiasDeDiferenciaConDate() < menorDistancia) {
				menorDistancia = festividad.getDiasDeDiferenciaConDate();
				masCercana = festividad;
			}
		}

		return masCercana;
	}

	/*
	 * EN: "<name> hace N días" / "<name> dentro de N días". An unknown code yields an
	 *     empty string, exactly as the old linear search over the table did.
	 * ES: "<nombre> hace N días" / "<nombre> dentro de N días". Un codigo desconocido
	 *     produce una cadena vacia, exactamente como hacia la busqueda lineal antigua
	 *     sobre la tabla.
	 */
	private String nombreConTiempo(MinimaFestividadesDTO festividad, String tiempo) {

		if (festividad == null) {
			return "";
		}

		String nombre = this.catalogo.nombreFestividad(festividad.getCode());
		if (nombre == null) {
			return "";
		}

		long dias = festividad.getDiasDeDiferenciaConDate();

		return nombre + " " + tiempo + " " + dias + " " + Fechas.literalDias(dias);
	}

	private String nombreOVacio(String code) {
		String nombre = this.catalogo.nombreFestividad(code);
		return nombre == null ? "" : nombre;
	}

	private static String codigoDeMidsison(int estacionDelSoeAnterior) {
		switch (estacionDelSoeAnterior) {
			case 1:
				return MIDSISON_INVERNAL;
			case 2:
				return MIDSISON_PRIMAVERAL;
			case 3:
				return MIDSISON_ESTIVAL;
			case 4:
				return MIDSISON_OTONYAL;
			default:
				return null;
		}
	}

	// =========================================================================
	// EN: SMALL BUILDERS - they only exist to keep the rules above readable.
	// ES: PEQUENOS CONSTRUCTORES - solo existen para que las reglas de arriba se lean bien.
	// =========================================================================

	/*
	 * EN: Careful, the distance is deliberately left at its default of zero. A
	 *     candidate whose phenomenon is never found keeps distance zero and therefore
	 *     counts as "today", which is what the original did and what the suppression
	 *     rules below are calibrated against. Initialising it to Long.MAX_VALUE would
	 *     look tidier and would silently change the response.
	 * ES: Cuidado, la distancia se deja a proposito en su valor por defecto de cero. Una
	 *     candidata cuyo fenomeno no se encuentra nunca conserva distancia cero y por
	 *     tanto cuenta como "hoy", que es lo que hacia el original y con lo que estan
	 *     calibradas las reglas de supresion de abajo. Inicializarla a Long.MAX_VALUE
	 *     pareceria mas limpio y cambiaria la respuesta en silencio.
	 */
	private static MinimaFestividadesDTO nueva(String code) {
		MinimaFestividadesDTO festividad = new MinimaFestividadesDTO();
		festividad.setCode(code);
		return festividad;
	}

	private static void hoy(MinimaFestividadesDTO festividad, LocalDateTime fecha) {
		festividad.setDate(fecha);
		festividad.setDiasDeDiferenciaConDate(0);
	}

	private static void a(MinimaFestividadesDTO festividad, LocalDateTime fecha, long dias) {
		festividad.setDate(fecha);
		festividad.setDiasDeDiferenciaConDate(dias);
	}
}
