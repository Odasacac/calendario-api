package CCASolutions.Calendario.DTOs;

import java.util.List;


public class AGPDTO {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {

        private List<ApogeosDTO> data;

        public List<ApogeosDTO> getData() {
            return data;
        }

        public void setData(List<ApogeosDTO> data) {
            this.data = data;
        }
    }
}