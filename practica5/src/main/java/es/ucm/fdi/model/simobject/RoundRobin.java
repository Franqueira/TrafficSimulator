package es.ucm.fdi.model.simobject;

import java.util.Map;
/**
 * Tipo de cruce en el que la duración del semáforo en verde es mayor
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class RoundRobin extends Junction {
	
	protected int minTime;
	protected int maxTime;
	protected String type;

	public RoundRobin(String id, int minTime, int maxTime, String type) {
		super(id);
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.type = type;
	}
	public void addIncoming(Road r) {
		RoundRobin.IncomingRoad ir = new IncomingRoad(r.getId(),maxTime);
		knowIncomings.put(r, ir);
		incomings.add(ir);
		trafficLight = incomings.size()-1;
		incomings.get(trafficLight).isTrafficLightGreen = true;
	}
	public void advance() {
		if (!incomings.isEmpty()) {
			IncomingRoad roadGreen = (IncomingRoad) incomings.get(trafficLight);
			if (!roadGreen.queue.isEmpty()) {
				Vehicle lucky = roadGreen.queue.getFirst();
				lucky.getRoad().removeVehicle(lucky);
				roadGreen.queue.pop();
				roadGreen.used++;
				moveVehicleToNextRoad(lucky);
			}
			
			roadGreen.timeUnitsUsed++;
			
			if(roadGreen.timeUnitsUsed == roadGreen.timeInterval){
				advanceTrafficLight();
			}
		}
	}

	protected void advanceTrafficLight(){
		IncomingRoad roadGreen = (IncomingRoad) incomings.get(trafficLight);
		roadGreen.isTrafficLightGreen = false;
		
		if(roadGreen.used == roadGreen.timeUnitsUsed){
			roadGreen.timeInterval = Math.min(roadGreen.timeInterval + 1, maxTime);
		}
		if(roadGreen.used == 0){
			roadGreen.timeInterval = Math.max(roadGreen.timeInterval - 1, minTime);
		}
		
		roadGreen.timeUnitsUsed = 0;
		
		trafficLight++;
		if (trafficLight == incomings.size()){
			trafficLight = 0;
		}
		incomings.get(trafficLight).isTrafficLightGreen = true;
	}

	protected void fillReportDetails(Map<String, String> out) {
		super.fillReportDetails(out);
		out.put("type", type);
	}

	protected class IncomingRoad extends Junction.IncomingRoad {
		
		protected int timeInterval;
		protected int timeUnitsUsed;
		protected int used;
		
		public IncomingRoad(String id, int maxTime) {
			super(id);
			this.timeInterval = maxTime;
			this.timeUnitsUsed = -1;
			this.used = 0;
		}
		
		protected String trafficLightReport(){
			StringBuilder string = new StringBuilder();
			string.append(super.trafficLightReport());
			if(isTrafficLightGreen){ 
				string.append(":" + (timeInterval - timeUnitsUsed));
			}
			return string.toString();
		}
	}
}
