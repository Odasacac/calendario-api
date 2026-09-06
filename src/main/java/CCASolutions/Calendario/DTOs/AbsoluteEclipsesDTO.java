package CCASolutions.Calendario.DTOs;

/**
 * EN: Counters of absolute eclipses, that is, every solar eclipse plus the total lunar ones.
 * They are counted from two origins, the reference eclipeno and the last winter new meton,
 * and split into solar, lunar and total.
 * ES: Contadores de eclipses absolutos, es decir, todos los solares más los lunares totales.
 * Se cuentan desde dos orígenes, el eclípeno de referencia y el último métono invernal
 * nuevo, y se desglosan en solares, lunares y totales.
 */
public class AbsoluteEclipsesDTO {
	
	private int sinceLastEclipenoIN;
	private int sinceLastMetonoIN;
	private int solarSinceLastEclipenoIN;
	private int solarSinceLastMetonoIN;
	private int lunarSinceLastEclipenoIN;
	private int lunarSinceLastMetonoIN;
	
	public int getSinceLastEclipenoIN() {
		return sinceLastEclipenoIN;
	}
	public void setSinceLastEclipenoIN(int sinceLastEclipenoIN) {
		this.sinceLastEclipenoIN = sinceLastEclipenoIN;
	}
	public int getSinceLastMetonoIN() {
		return sinceLastMetonoIN;
	}
	public void setSinceLastMetonoIN(int sinceLastMetonoIN) {
		this.sinceLastMetonoIN = sinceLastMetonoIN;
	}
	public int getSolarSinceLastEclipenoIN() {
		return solarSinceLastEclipenoIN;
	}
	public void setSolarSinceLastEclipenoIN(int solarSinceLastEclipenoIN) {
		this.solarSinceLastEclipenoIN = solarSinceLastEclipenoIN;
	}
	public int getSolarSinceLastMetonoIN() {
		return solarSinceLastMetonoIN;
	}
	public void setSolarSinceLastMetonoIN(int solarSinceLastMetonoIN) {
		this.solarSinceLastMetonoIN = solarSinceLastMetonoIN;
	}
	public int getLunarSinceLastEclipenoIN() {
		return lunarSinceLastEclipenoIN;
	}
	public void setLunarSinceLastEclipenoIN(int lunarSinceLastEclipenoIN) {
		this.lunarSinceLastEclipenoIN = lunarSinceLastEclipenoIN;
	}
	public int getLunarSinceLastMetonoIN() {
		return lunarSinceLastMetonoIN;
	}
	public void setLunarSinceLastMetonoIN(int lunarSinceLastMetonoIN) {
		this.lunarSinceLastMetonoIN = lunarSinceLastMetonoIN;
	}
	
	
	
	
}
