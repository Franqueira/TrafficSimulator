package es.ucm.fdi.model.simobject;

import java.util.Map;
/**
 * Cruce en el que la siguiente carretera entrante en poner su semáforo
 * en verde es aquella que tiene más vehículos en su cola.
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class MostCrowed extends Junction {
	
	protected String type;

	public MostCrowed(String id, String type) {
		super(id);
		this.type = type;
	}
	public void addIncoming(Road r) {
		IncomingRoad ir = new IncomingRoad(r.getId());
		knowIncomings.put(r, ir);
		incomings.add(ir);
		advanceTrafficLight();
	}
	public void advance() {
		if (!incomings.isEmpty()) {
			IncomingRoad roadGreen = (IncomingRoad) incomings.get(trafficLight);
			if (!roadGreen.queue.isEmpty()) {
				Vehicle lucky = roadGreen.queue.getFirst();
				lucky.getRoad().removeVehicle(lucky);
				roadGreen.queue.pop();
				moveVehicleToNextRoad(lucky);
			}
			
			roadGreen.timeUnitsUsed++;
			if (roadGreen.timeUnitsUsed == roadGreen.timeInterval){ 
				advanceTrafficLight();
			}
		}
	}
	protected void advanceTrafficLight(){
		IncomingRoad roadGreen = (IncomingRoad) incomings.get(trafficLight);
		roadGreen.isTrafficLightGreen = false;
		
		IncomingRoad moreVehicles = roadGreen;
		int max = trafficLight;
		for(int i = 0; i<incomings.size(); i++){
			IncomingRoad r = (IncomingRoad) incomings.get(i);
			if((r.queue.size() > moreVehicles.queue.size() && i!=trafficLight) || max == trafficLight){
				moreVehicles = r;
				max = i;
			}
		}
		
		trafficLight = max;
		moreVehicles.isTrafficLightGreen = true;
		moreVehicles.timeInterval = Math.max(moreVehicles.queue.size() / 2, 1);
		moreVehicles.timeUnitsUsed = 0;
	}
	protected void fillReportDetails(Map<String, String> out) {
		super.fillReportDetails(out);
		out.put("type", type);
	}
	protected class IncomingRoad extends Junction.IncomingRoad {

		protected int timeInterval;
		protected int timeUnitsUsed;

		public IncomingRoad(String r) {
			super(r);
			this.timeInterval = 0;
			this.timeUnitsUsed = 0;
		}
		protected String trafficLightReport() {
			StringBuilder r = new StringBuilder();
			r.append(super.trafficLightReport());
			if (isTrafficLightGreen){
				r.append(":" + (timeInterval - timeUnitsUsed));
			}
			return r.toString();
		}
	}
}
