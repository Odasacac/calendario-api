package CCASolutions.Calendario.DTOs;

import java.util.List;

public class YLPDTO {
	private Response response;

	public Response getResponse() { 
		return response; 
	
	}
	public void setResponse(Response response) { 
		this.response = response; 
	}

	public static class Response {
	
		private List<LunarPhaseDTO> data;
		
		public List<LunarPhaseDTO> getData() {
			return data; 
		}
		
		public void setData(List<LunarPhaseDTO> data) { 
			this.data = data; 
		}
	}
}

