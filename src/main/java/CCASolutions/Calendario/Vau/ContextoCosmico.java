package CCASolutions.Calendario.Vau;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import CCASolutions.Calendario.Entities.ApogeosYPerigeosLunaEntity;
import CCASolutions.Calendario.Entities.EclipenosEntity;
import CCASolutions.Calendario.Entities.EclipsesEntity;
import CCASolutions.Calendario.Entities.LunasEntity;
import CCASolutions.Calendario.Entities.MetonsEntity;
import CCASolutions.Calendario.Entities.SolsticiosYEquinocciosEntity;
import CCASolutions.Calendario.Support.Fechas;

/*
 * ==============================================================================
 * EN: Everything the calculators need about one requested date, gathered once.
 *
 *     This replaces DatosCosmicosParaVAUDTO. Three things changed:
 *
 *       1. The requested date and its epoch day are carried here, so no calculator
 *          has to recompute "date.toEpochDay()" inside its loops.
 *       2. The lunar and eclipse lists are bounded windows around the date instead
 *          of centuries-long histories (see CargadorDatosCosmicos for why that is
 *          equivalent).
 *       3. It is built once by CargadorDatosCosmicos and then only read. It is not
 *          shared between requests, so no synchronisation is needed.
 *
 * ES: Todo lo que los calculadores necesitan sobre una fecha consultada, reunido una
 *     sola vez.
 *
 *     Sustituye a DatosCosmicosParaVAUDTO. Han cambiado tres cosas:
 *
 *       1. La fecha consultada y su dia epoch viajan aqui, asi que ningun calculador
 *          tiene que recalcular "date.toEpochDay()" dentro de sus bucles.
 *       2. Las listas de lunas y de eclipses son ventanas acotadas alrededor de la fecha
 *          en vez de historicos de siglos (en CargadorDatosCosmicos se explica por que
 *          es equivalente).
 *       3. Lo construye una sola vez CargadorDatosCosmicos y despues solo se lee. No se
 *          comparte entre peticiones, asi que no necesita sincronizacion.
 * ==============================================================================
 */
public class ContextoCosmico {

	// =========================================================================
	// EN: THE REQUESTED DATE
	// ES: LA FECHA CONSULTADA
	// =========================================================================

	private final LocalDate fecha;

	/*
	 * EN: The requested date as an epoch day. Every distance in the read path is a
	 *     subtraction against this long instead of a ChronoUnit call.
	 * ES: La fecha consultada como dia epoch. Cada distancia del camino de lectura es
	 *     una resta contra este long en lugar de una llamada a ChronoUnit.
	 */
	private final long diaEpoch;

	public ContextoCosmico(LocalDate fecha) {
		this.fecha = fecha;
		this.diaEpoch = fecha.toEpochDay();
	}

	// =========================================================================
	// EN: COSMIC PHENOMENA - windows around the date, oldest first, except the
	//     eclipenos and metonos which come from the catalog newest first.
	// ES: FENOMENOS COSMICOS - ventanas alrededor de la fecha, de la mas antigua a la
	//     mas nueva, salvo eclipenos y metonos que llegan del catalogo al reves.
	// =========================================================================

	private List<SolsticiosYEquinocciosEntity> soes = Collections.emptyList();
	private List<LunasEntity> lunas = Collections.emptyList();
	private List<EclipsesEntity> eclipses = Collections.emptyList();
	private List<EclipenosEntity> eclipenos = Collections.emptyList();
	private List<MetonsEntity> metonos = Collections.emptyList();
	private List<ApogeosYPerigeosLunaEntity> apoperis = Collections.emptyList();

	// =========================================================================
	// EN: ANCHORS - the reference phenomena every VAU unit is counted from.
	// ES: ANCLAS - los fenomenos de referencia desde los que se cuenta cada unidad VAU.
	// =========================================================================

	private EclipenosEntity ultimoEclipenoIN;
	private EclipenosEntity ultimoEclipenoInvernalApofasalRemoto;
	private MetonsEntity ultimoMetonoIN;
	private MetonsEntity ultimoMetonoIApofasalRemoto;

	// =========================================================================
	// EN: VALIDITY - when something is missing the response carries the reason
	//     instead of a converted date.
	// ES: VALIDEZ - cuando falta algo la respuesta lleva el motivo en lugar de una
	//     fecha convertida.
	// =========================================================================

	private boolean valido;
	private String mensaje;

	// =========================================================================
	// EN: ACCESSORS
	// ES: ACCESORES
	// =========================================================================

	public LocalDate getFecha() {
		return this.fecha;
	}

	public long getDiaEpoch() {
		return this.diaEpoch;
	}

	/*
	 * EN: Distance in days from the given phenomenon day to the requested date.
	 *     Positive when the phenomenon is in the past.
	 * ES: Distancia en dias desde el dia del fenomeno dado hasta la fecha consultada.
	 *     Positiva cuando el fenomeno esta en el pasado.
	 */
	public long diasHastaLaFecha(long diaEpochDelFenomeno) {
		return Fechas.dias(diaEpochDelFenomeno, this.diaEpoch);
	}

	public List<SolsticiosYEquinocciosEntity> getSoes() {
		return this.soes;
	}

	public void setSoes(List<SolsticiosYEquinocciosEntity> soes) {
		this.soes = soes;
	}

	public List<LunasEntity> getLunas() {
		return this.lunas;
	}

	public void setLunas(List<LunasEntity> lunas) {
		this.lunas = lunas;
	}

	public List<EclipsesEntity> getEclipses() {
		return this.eclipses;
	}

	public void setEclipses(List<EclipsesEntity> eclipses) {
		this.eclipses = eclipses;
	}

	public List<EclipenosEntity> getEclipenos() {
		return this.eclipenos;
	}

	public void setEclipenos(List<EclipenosEntity> eclipenos) {
		this.eclipenos = eclipenos;
	}

	public List<MetonsEntity> getMetonos() {
		return this.metonos;
	}

	public void setMetonos(List<MetonsEntity> metonos) {
		this.metonos = metonos;
	}

	public List<ApogeosYPerigeosLunaEntity> getApoperis() {
		return this.apoperis;
	}

	public void setApoperis(List<ApogeosYPerigeosLunaEntity> apoperis) {
		this.apoperis = apoperis;
	}

	public EclipenosEntity getUltimoEclipenoIN() {
		return this.ultimoEclipenoIN;
	}

	public void setUltimoEclipenoIN(EclipenosEntity ultimoEclipenoIN) {
		this.ultimoEclipenoIN = ultimoEclipenoIN;
	}

	public EclipenosEntity getUltimoEclipenoInvernalApofasalRemoto() {
		return this.ultimoEclipenoInvernalApofasalRemoto;
	}

	public void setUltimoEclipenoInvernalApofasalRemoto(EclipenosEntity ultimoEclipenoInvernalApofasalRemoto) {
		this.ultimoEclipenoInvernalApofasalRemoto = ultimoEclipenoInvernalApofasalRemoto;
	}

	public MetonsEntity getUltimoMetonoIN() {
		return this.ultimoMetonoIN;
	}

	public void setUltimoMetonoIN(MetonsEntity ultimoMetonoIN) {
		this.ultimoMetonoIN = ultimoMetonoIN;
	}

	public MetonsEntity getUltimoMetonoIApofasalRemoto() {
		return this.ultimoMetonoIApofasalRemoto;
	}

	public void setUltimoMetonoIApofasalRemoto(MetonsEntity ultimoMetonoIApofasalRemoto) {
		this.ultimoMetonoIApofasalRemoto = ultimoMetonoIApofasalRemoto;
	}

	public boolean isValido() {
		return this.valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public String getMensaje() {
		return this.mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
}
