package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;

public class SolarEclipseDTO {

    private LocalDateTime date;
    private String type;

    public SolarEclipseDTO() {}

    public SolarEclipseDTO(LocalDateTime date, String type) {
        this.date = date;
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}