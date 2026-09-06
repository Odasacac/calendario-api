package CCASolutions.Calendario.DTOs;

import java.util.List;

/**
 * EN: Raw envelope of the OPALE moon phase API. Only exists to unwrap the JSON down to the
 * list of phases.
 * ES: Envoltorio en bruto de la API de fases lunares de OPALE. Sólo existe para desenvolver
 * el JSON hasta la lista de fases.
 */
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

