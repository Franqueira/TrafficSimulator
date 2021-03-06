package es.ucm.fdi.model.simobject;

import java.util.LinkedHashMap;
import java.util.Map;

import es.ucm.fdi.model.Describable;
/**
 * Superclase que engloba todos los objetos de la simulación
 * Responsabilidades comunes a todos:
 * Generar y rellenar un mapa en el que se guardan todos los datos propios del objeto de simulación.
 * 
 * @see Describable
 * @author Miguel Franqueira Varela
 * 
 */
public abstract class SimObject implements Describable{
	protected String id;

	public SimObject(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> report(int time) {
		Map<String, String> out = new LinkedHashMap<>();
		out.put("", getReportHeader());
		out.put("id", id);
		out.put("time", "" + time);
		fillReportDetails(out);
		return out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (!(obj instanceof SimObject)){
			return false;
		}
		SimObject other = (SimObject) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	protected abstract void fillReportDetails(Map<String, String> out);

	protected abstract String getReportHeader();
}
