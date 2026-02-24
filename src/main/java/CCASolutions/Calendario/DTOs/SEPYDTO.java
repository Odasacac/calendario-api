package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public class SEPYDTO {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {

        private List<SolarEclipse> data;

        public List<SolarEclipse> getData() {
            return data;
        }

        public void setData(List<SolarEclipse> data) {
            this.data = data;
        }
    }

    public static class SolarEclipse {

        private String type;
        private Events events;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Events getEvents() {
            return events;
        }

        public void setEvents(Events events) {
            this.events = events;
        }
    }

    public static class Events {

        private Greatest greatest;

        public Greatest getGreatest() {
            return greatest;
        }

        public void setGreatest(Greatest greatest) {
            this.greatest = greatest;
        }
    }

    public static class Greatest {

        private LocalDateTime date;

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }
}