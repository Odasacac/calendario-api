package CCASolutions.Calendario.DTOs;

/**
 * EN: One solar eclipse as the OPALE API returns it: instant and type.
 * ES: Un eclipse solar tal y como lo devuelve la API de OPALE: instante y tipo.
 */
public class SolarEclipseDTO {

    private String date;
    private String type;

    public SolarEclipseDTO() {}

    public SolarEclipseDTO(String date, String type) {
        this.date = date;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
