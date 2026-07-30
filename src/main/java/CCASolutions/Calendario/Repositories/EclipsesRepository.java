package CCASolutions.Calendario.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import CCASolutions.Calendario.Entities.EclipsesEntity;

/*
 * ==============================================================================
 * EN: Eclipses (~9.900 rows).
 *
 *     The read path used to load every non partial, non penumbral eclipse from the
 *     last "eclipeno inicial nuevo" (which can be more than a century back) up to a
 *     year after the requested date - around 1.300 entities per request - and then
 *     counted them in Java.
 *
 *     Now the counting is done by MySQL over an index and only the handful of
 *     eclipses near the requested date are materialised.
 *
 * ES: Eclipses (~9.900 filas).
 *
 *     El camino de lectura cargaba todos los eclipses no parciales ni penumbrales desde
 *     el ultimo "eclipeno inicial nuevo" (que puede estar mas de un siglo atras) hasta
 *     un ano despues de la fecha consultada - unas 1.300 entidades por peticion - y
 *     luego los contaba en Java.
 *
 *     Ahora el conteo lo hace MySQL sobre un indice y solo se materializan los pocos
 *     eclipses cercanos a la fecha consultada.
 * ==============================================================================
 */
public interface EclipsesRepository extends JpaRepository<EclipsesEntity, Long> {

	/*
	 * EN: Used by the population job, which walks the table year by year.
	 * ES: Lo usa el proceso de poblacion, que recorre la tabla ano a ano.
	 */
	List<EclipsesEntity> findByYear(int year);

	/*
	 * EN: Used by the population job to attach an eclipse to the following eclipeno.
	 * ES: Lo usa el proceso de poblacion para asociar un eclipse al eclipeno siguiente.
	 */
	EclipsesEntity findFirstByDateAfterAndEsParcialIsFalseAndEsPenumbralIsFalseOrderByDateAsc(LocalDateTime date);

	/*
	 * EN: The bounded window of visible (non partial, non penumbral) eclipses around
	 *     the requested date, oldest first. Enough to resolve "the eclipse of today",
	 *     "the previous one" and "the next one".
	 * ES: La ventana acotada de eclipses visibles (no parciales ni penumbrales) alrededor
	 *     de la fecha consultada, del mas antiguo al mas nuevo. Suficiente para resolver
	 *     "el eclipse de hoy", "el anterior" y "el siguiente".
	 */
	List<EclipsesEntity> findByEsParcialFalseAndEsPenumbralFalseAndDateBetweenOrderByDateAsc(LocalDateTime desde,
			LocalDateTime hasta);

	/*
	 * EN: Solar and lunar counters over [desde, hasta). Each one is called twice: once
	 *     from the last eclipeno and once from the last metono, which is all the
	 *     "absolute eclipses" block of the response needs.
	 * ES: Contadores solares y lunares sobre [desde, hasta). Cada uno se llama dos veces:
	 *     una desde el ultimo eclipeno y otra desde el ultimo metono, que es todo lo que
	 *     necesita el bloque de "eclipses absolutos" de la respuesta.
	 */
	long countByEsParcialFalseAndEsPenumbralFalseAndDeSolTrueAndDateGreaterThanEqualAndDateLessThan(
			LocalDateTime desde, LocalDateTime hasta);

	long countByEsParcialFalseAndEsPenumbralFalseAndDeLunaTrueAndDateGreaterThanEqualAndDateLessThan(
			LocalDateTime desde, LocalDateTime hasta);
}
