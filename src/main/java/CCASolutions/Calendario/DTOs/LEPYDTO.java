package CCASolutions.Calendario.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public class LEPYDTO {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {

        private List<LunarEclipse> lunareclipse;

        public List<LunarEclipse> getLunareclipse() {
            return lunareclipse;
        }

        public void setLunareclipse(List<LunarEclipse> lunareclipse) {
            this.lunareclipse = lunareclipse;
        }
    }

    public static class LunarEclipse {

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