package CCASolutions.Calendario.DTOs;

import java.util.List;

public class MoonPhasesDTO {

    private Link link;
    private Request request;
    private Response response;

    // Getters y Setters
    public Link getLink() { return link; }
    public void setLink(Link link) { this.link = link; }

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    // ----- Clases internas -----
    public static class Link {
        private String self;
        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }
    }

    public static class Request {
        private String calendar;
        private String tag;
        private String timescale;
        private int year;

        public String getCalendar() { return calendar; }
        public void setCalendar(String calendar) { this.calendar = calendar; }

        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }

        public String getTimescale() { return timescale; }
        public void setTimescale(String timescale) { this.timescale = timescale; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
    }

    public static class Response {
        private String calendar;
        private List<Data> data;
        private Description description;
        private String timescale;
        private Unit unit;

        public String getCalendar() { return calendar; }
        public void setCalendar(String calendar) { this.calendar = calendar; }

        public List<Data> getData() { return data; }
        public void setData(List<Data> data) { this.data = data; }

        public Description getDescription() { return description; }
        public void setDescription(Description description) { this.description = description; }

        public String getTimescale() { return timescale; }
        public void setTimescale(String timescale) { this.timescale = timescale; }

        public Unit getUnit() { return unit; }
        public void setUnit(Unit unit) { this.unit = unit; }

        // Clase interna Data (cada fase lunar)
        public static class Data {
            private String date;
            private String moonPhase;

            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }

            public String getMoonPhase() { return moonPhase; }
            public void setMoonPhase(String moonPhase) { this.moonPhase = moonPhase; }
        }

        public static class Description {
            private String date;
            private String moonPhase;

            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }

            public String getMoonPhase() { return moonPhase; }
            public void setMoonPhase(String moonPhase) { this.moonPhase = moonPhase; }
        }

        public static class Unit {
            private String date;

            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }
        }
    }
}
