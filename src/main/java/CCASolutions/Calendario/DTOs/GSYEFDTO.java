package CCASolutions.Calendario.DTOs;

import java.util.List;

public class GSYEFDTO {
	private Response response;

	public Response getResponse() { 
		return response; 
	
	}
	public void setResponse(Response response) { 
		this.response = response; 
	}

	public static class Response {
	
		private List<FenomenoDTO> data;
		
		public List<FenomenoDTO> getData() {
			return data; 
		}
		
		public void setData(List<FenomenoDTO> data) { 
			this.data = data; 
		}
	}
}
