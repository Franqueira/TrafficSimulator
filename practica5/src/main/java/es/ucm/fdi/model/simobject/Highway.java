package es.ucm.fdi.model.simobject;

import java.util.Map;

import es.ucm.fdi.util.MultiTreeMap;
/**Tipo de vehículo con varios carriles para soportar un nivel de tráfico mayor que
 * las carreteras convencionales.
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class Highway extends Road {
	
	protected String type;
	protected int numLanes;

	public Highway(String id, int lon, int maxv, Junction princ, Junction fin, String type, int numLanes) {
		super(id, lon, maxv, princ, fin);
		this.type = type;
		this.numLanes = numLanes;
	}
	public int calculateBaseSpeed() {
		long n = vehicles.sizeOfValues();
		if (n < 1){
			n = 1;
		}
		int value = (maxSpeed*numLanes / (int) n) + 1;
		if (maxSpeed < value){
			return maxSpeed;
		}
		else{
			return value;
		}
	}
	public void advance() {
		MultiTreeMap<Integer, Vehicle> news = new MultiTreeMap<>((a, b) -> b-a);
		int baseSpeed = calculateBaseSpeed();
		int reductionFactor = 1;
		int numFaulties = 0;
		for (Vehicle v : vehicles.innerValues()) {
			if(reductionFactor == 1){
				if(v.getFaultyTime() != 0){
					numFaulties++;
					if(numFaulties >= numLanes){ 
						reductionFactor=2;
					}
				}
			}
			if(v.getLocation() < length){
				if (v.getFaultyTime() == 0){
					v.setRealSpeed(baseSpeed / reductionFactor);
				}
				v.advance();
			}
			news.putValue(v.getLocation(), v);
		}
		vehicles = news;
	}
	/**Guarda los datos de la autopista en el mapa pasado como argumento*/
	public void fillReportDetails(Map<String, String> out) {
		out.put("type", type);
		super.fillReportDetails(out);
	}
}
