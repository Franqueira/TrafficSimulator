package es.ucm.fdi.model.simobject;

import java.util.List;
import java.util.Map;
/**
 * 
 * Clase que contiene los datos de una bicicleta.
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class Bike extends Vehicle {

	protected String type;

	public Bike(int velMax, List<Junction> cruc, String id, String type) {
		super(velMax, cruc, id);
		this.type = type;
	}
	public void setTimeFault(int n) {
		if(realSpeed > maxSpeed/2){
			faultyTime += n;
			realSpeed = 0;
		}
	}
	public void fillReportDetails(Map<String, String> out) {
		out.put("type", type);
		super.fillReportDetails(out);
	}

}
