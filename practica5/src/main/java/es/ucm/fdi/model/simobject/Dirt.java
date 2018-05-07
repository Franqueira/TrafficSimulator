package es.ucm.fdi.model.simobject;

import java.util.Map;

import es.ucm.fdi.util.MultiTreeMap;
/**Tipo de carretera en la que la velocidad de los vehículos es proporcional 
 * al número de vehículos que haya averiados delante
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class Dirt extends Road {
	
	protected String type;

	public Dirt(String id, int lon, int maxv, Junction princ, Junction fin, String type) {
		super(id, lon, maxv, princ, fin);
		this.type = type;
	}
	
	public int calculateBaseSpeed() {
		return maxSpeed;
	}
	public void advance() {
		MultiTreeMap<Integer, Vehicle> nuevos = new MultiTreeMap<>((a, b) -> b-a);
		int velocidadBase = calculateBaseSpeed();
		int factorReduccion = 1;
		for (Vehicle v : vehicles.innerValues()) {
			if(v.getFaultyTime() != 0){
				factorReduccion++;
			}
			if(v.getLocation() < length){
				if (v.getFaultyTime() == 0){
					v.setRealSpeed(velocidadBase / factorReduccion);
				}
				v.advance();
			}
			nuevos.putValue(v.getLocation(), v);
		}
		vehicles = nuevos;
	}
	public void fillReportDetails(Map<String, String> out) {
		out.put("type", type);
		super.fillReportDetails(out);
	}
}
