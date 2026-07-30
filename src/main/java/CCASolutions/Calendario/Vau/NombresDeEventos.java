package CCASolutions.Calendario.Vau;

import CCASolutions.Calendario.DTOs.MidsisonDTO;
import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;

/*
 * ==============================================================================
 * EN: Human readable name of a notable event.
 *
 *     The original method was a 290 line cascade that built its result with about
 *     forty "evento = evento + ..." statements. In Java each of those allocates a
 *     new String and copies everything written so far, which makes naming a single
 *     event quadratic in the length of its name - and it is called three times per
 *     request. A StringBuilder writes each fragment once.
 *
 *     The cascade itself is unchanged, including the priority between kinds of event
 *     (eclipeno, then metono, then solstice, then eclipse, then midsison, then moon,
 *     then apogee) and the exact spacing of every name, because those strings are
 *     part of the API response.
 *
 * ES: Nombre legible de un evento resenable.
 *
 *     El metodo original era una cascada de 290 lineas que construia su resultado con
 *     unas cuarenta sentencias "evento = evento + ...". En Java cada una de ellas crea un
 *     String nuevo y copia todo lo escrito hasta el momento, lo que hace que nombrar un
 *     solo evento sea cuadratico en la longitud de su nombre - y se llama tres veces por
 *     peticion. Un StringBuilder escribe cada fragmento una sola vez.
 *
 *     La cascada en si no cambia, incluida la prioridad entre tipos de evento (eclipeno,
 *     luego metono, luego solsticio, luego eclipse, luego midsison, luego luna, luego
 *     apogeo) y el espaciado exacto de cada nombre, porque esas cadenas forman parte de
 *     la respuesta de la API.
 * ==============================================================================
 */
public final class NombresDeEventos {

	private NombresDeEventos() {
	}

	/*
	 * EN: Names the highest priority non-null argument. Everything null means there is
	 *     no event, and the result is an empty string.
	 * ES: Nombra el argumento no nulo de mayor prioridad. Si todo es nulo no hay evento,
	 *     y el resultado es una cadena vacia.
	 */
	public static String nombrar(LunasEntity luna,
			SolsticiosYEquinocciosEntity soe,
			MetonsEntity metono,
			EclipsesEntity eclipse,
			EclipenosEntity eclipeno,
			ApogeosYPerigeosLunaEntity apoperi,
			MidsisonDTO midsison) {

		if (eclipeno != null) {
			return nombreDeEclipeno(eclipeno);
		}
		if (metono != null) {
			return nombreDeMetono(metono);
		}
		if (soe != null) {
			return nombreDeSoe(soe);
		}
		if (eclipse != null) {
			return nombreDeEclipse(eclipse);
		}
		if (midsison != null) {
			return nombreDeMidsison(midsison);
		}
		if (luna != null) {
			return nombreDeLuna(luna);
		}
		if (apoperi != null) {
			return nombreDeApoperi(apoperi);
		}
		return "";
	}

	// =========================================================================
	// EN: ECLIPENO
	// ES: ECLIPENO
	// =========================================================================

	private static String nombreDeEclipeno(EclipenosEntity eclipeno) {

		StringBuilder nombre = new StringBuilder(48);

		if (eclipeno.isInvernal()) {
			nombre.append("Eclípeno invernal ");
		} else if (eclipeno.isPrimaveral()) {
			nombre.append("Eclípeno primaveral ");
		} else if (eclipeno.isEstival()) {
			nombre.append("Eclípeno estival ");
		} else if (eclipeno.isOtonyal()) {
			nombre.append("Eclípeno otoñal ");
		}

		if (eclipeno.isApofasal()) {

			nombre.append("apofasal ");

			if (eclipeno.isSelecto() && eclipeno.isNuevo()) {
				nombre.append("remoto");
			} else if (eclipeno.isSelecto() && eclipeno.isLleno()) {
				nombre.append("brillante");
			} else if (eclipeno.isInvertido() && eclipeno.isNuevo()) {
				nombre.append("velado");
			} else if (eclipeno.isInvertido() && eclipeno.isLleno()) {
				nombre.append("tenue");
			}

		} else {

			/*
			 * EN: The leading spaces here are deliberate: the season fragment above already
			 *     ends with one, so the original produced a double space in this branch and
			 *     clients may rely on it.
			 * ES: Los espacios iniciales de aqui son deliberados: el fragmento de estacion de
			 *     arriba ya termina con uno, asi que el original producia un espacio doble en
			 *     esta rama y los clientes pueden depender de ello.
			 */
			if (eclipeno.isNuevo()) {
				nombre.append(" nuevo");
			} else if (eclipeno.isLleno()) {
				nombre.append(" lleno");
			}

			if (eclipeno.isSelecto()) {
				nombre.append(" selecto");
			} else if (eclipeno.isInvertido()) {
				nombre.append(" invertido");
			}
		}

		return nombre.toString();
	}

	// =========================================================================
	// EN: METONO
	// ES: METONO
	// =========================================================================

	private static String nombreDeMetono(MetonsEntity metono) {

		StringBuilder nombre = new StringBuilder(48);

		if (metono.isInvernal()) {
			nombre.append("Métono invernal");
		} else if (metono.isPrimaveral()) {
			nombre.append("Métono primaveral");
		} else if (metono.isEstival()) {
			nombre.append("Métono estival");
		} else if (metono.isOtonyal()) {
			nombre.append("Métono otoñal");
		}

		if (metono.isSelecto()) {

			if (metono.isApofasal()) {
				nombre.append(" apofasal");
				if ((metono.isFasal() && metono.isNuevo()) || (metono.isApoperico() && metono.isAporico())) {
					nombre.append(" remoto");
				} else if ((metono.isFasal() && metono.isLleno()) || (metono.isApoperico() && metono.isPerico())) {
					nombre.append(" brillante");
				}
			} else {
				nombre.append(faseDelMetono(metono));
				nombre.append(" selecto");
			}

		} else if (metono.isInvertido()) {

			if (metono.isApofasal()) {
				nombre.append(" apofasal");
				if ((metono.isFasal() && metono.isNuevo()) || (metono.isApoperico() && metono.isPerico())) {
					nombre.append(" velado");
				} else if ((metono.isFasal() && metono.isLleno()) || (metono.isApoperico() && metono.isAporico())) {
					nombre.append(" tenue");
				}
			} else {
				nombre.append(faseDelMetono(metono));
				nombre.append(" invertido");
			}

		} else {

			/*
			 * EN: A plain metono only carries its phase, and only when the phase family
			 *     matches: fasal metonos are new or full, apoperic ones aporic or peric.
			 * ES: Un metono normal solo lleva su fase, y solo cuando la familia de fase
			 *     coincide: los metonos fasales son nuevos o llenos, los apopericos aporicos
			 *     o pericos.
			 */
			if (metono.isFasal()) {
				if (metono.isNuevo()) {
					nombre.append(" nuevo");
				} else if (metono.isLleno()) {
					nombre.append(" lleno");
				}
			} else if (metono.isApoperico()) {
				if (metono.isAporico()) {
					nombre.append(" apórico");
				} else if (metono.isPerico()) {
					nombre.append(" périco");
				}
			}
		}

		return nombre.toString();
	}

	/*
	 * EN: The phase fragment shared by the selected and inverted branches. Unlike the
	 *     plain branch above, this one does not check the phase family first, exactly
	 *     as in the original.
	 * ES: El fragmento de fase que comparten las ramas selecta e invertida. A diferencia
	 *     de la rama normal de arriba, esta no comprueba primero la familia de fase,
	 *     exactamente como en el original.
	 */
	private static String faseDelMetono(MetonsEntity metono) {
		if (metono.isFasal() && metono.isNuevo()) {
			return " nuevo";
		}
		if (metono.isFasal() && metono.isLleno()) {
			return " lleno";
		}
		if (metono.isApoperico() && metono.isAporico()) {
			return " apórico";
		}
		if (metono.isApoperico() && metono.isPerico()) {
			return " périco";
		}
		return "";
	}

	// =========================================================================
	// EN: SOLSTICES, EQUINOXES, ECLIPSES, MIDSISON, MOON, APOGEE
	// ES: SOLSTICIOS, EQUINOCCIOS, ECLIPSES, MIDSISON, LUNA, APOGEO
	// =========================================================================

	private static String nombreDeSoe(SolsticiosYEquinocciosEntity soe) {
		if (soe.isSolsticioInvierno()) {
			return "Solsticio de invierno";
		}
		if (soe.isEquinoccioPrimavera()) {
			return "Equinoccio de primavera";
		}
		if (soe.isSolsticioVerano()) {
			return "Solsticio de verano";
		}
		if (soe.isEquinoccioOtonyo()) {
			return "Equinoccio de otoño";
		}
		return "";
	}

	private static String nombreDeEclipse(EclipsesEntity eclipse) {

		StringBuilder nombre = new StringBuilder(32);

		if (eclipse.isDeLuna()) {
			nombre.append("Eclipse de luna");
		} else if (eclipse.isDeSol()) {
			nombre.append("Eclipse de sol");
		}

		if (eclipse.isEsAnular()) {
			nombre.append(" anular");
		} else if (eclipse.isEsHibrido()) {
			nombre.append(" híbrido");
		} else if (eclipse.isEsParcial()) {
			nombre.append(" parcial");
		} else if (eclipse.isEsPenumbral()) {
			nombre.append(" penumbral");
		} else if (eclipse.isEsTotal()) {
			nombre.append(" total");
		}

		return nombre.toString();
	}

	private static String nombreDeMidsison(MidsisonDTO midsison) {
		switch (midsison.getLastSoeSeason()) {
			case 1:
				return "Midsison invernal";
			case 2:
				return "Midsison primaveral";
			case 3:
				return "Midsison estival";
			case 4:
				return "Midsison otoñal";
			default:
				return "Midsison";
		}
	}

	private static String nombreDeLuna(LunasEntity luna) {

		// EN: A selected new moon has a name of its own: it opens an aponovo.
		// ES: Una luna nueva selecta tiene nombre propio: abre un aponovo.
		if (luna.isNueva() && luna.isSelecta()) {
			return "Luna aponoval";
		}

		StringBuilder nombre = new StringBuilder(32);

		if (luna.isNueva()) {
			nombre.append("Luna nueva");
		} else if (luna.isCuartoCreciente()) {
			nombre.append("Luna cuarto creciente");
		} else if (luna.isLlena()) {
			nombre.append("Luna llena");
		} else if (luna.isCuartoMenguante()) {
			nombre.append("Luna cuarto menguante");
		}

		if (luna.isSelecta()) {
			nombre.append(" selecta");
		} else if (luna.isInvertida()) {
			nombre.append(" invertida");
		}

		return nombre.toString();
	}

	private static String nombreDeApoperi(ApogeosYPerigeosLunaEntity apoperi) {
		if (apoperi.isEsApogeo()) {
			return "Luna durmiente";
		}
		if (apoperi.isEsPerigeo()) {
			return "Luna presente";
		}
		return "Luna ";
	}
}
