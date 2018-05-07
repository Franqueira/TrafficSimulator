package es.ucm.fdi.model.simobject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.exceptions.SimulatorException;
import es.ucm.fdi.model.Describable;

/**
 * Cruce general. Responsabilidades:
 * Hacer que los vehículos que provienen de diferentes carreteras que están
 * conectadas a este cruce circulen a su siguiente carretera atendiendo a 
 * un orden, de manera que solo el coche de unas de las carreteras puede
 * avanzar a la vez.
 * 
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class Junction extends SimObject implements Describable {
	protected Map<Road, IncomingRoad> knowIncomings;
	protected List<IncomingRoad> incomings;
	protected int trafficLight;
	protected Map<Junction, Road> knowOutgoing;

	public Junction(String id){
		super(id);
		knowIncomings = new HashMap<>();
		incomings = new ArrayList<>();
		trafficLight = 0;
		knowOutgoing = new HashMap<>();
	}
	public void addVehicle(Vehicle vehicle) {
		knowIncomings.get(vehicle.getRoad()).queue.add(vehicle);
	}
	public void addOutgoing(Road road) {
		knowOutgoing.put(road.getEndJunction(), road);
	}
	public boolean isTrafficLightGreen(Road r){
		return knowIncomings.get(r).isTrafficLightGreen;
	}
	public void addIncoming(Road road) {
		IncomingRoad incRoad = new IncomingRoad(road.getId());
		knowIncomings.put(road, incRoad);
		incomings.add(incRoad);
		trafficLight = incomings.size()-1;
	}
	
	public void moveVehicleToNextRoad(Vehicle vehicle) {
		Junction nextJunction = vehicle.getNextJunction();
		if (nextJunction != null) {
			Road r = knowOutgoing.get(nextJunction);
			if (r == null){
				throw new SimulatorException("A vehicle goes over ghost roads");
			}
			vehicle.moveToNextRoad(r);
		} else{
			vehicle.arrived();
		}
	}
	public void advance() {
		if (!incomings.isEmpty()) {
			IncomingRoad roadGreen = incomings.get(trafficLight);
			if (!roadGreen.queue.isEmpty()) {
				Vehicle lucky = roadGreen.queue.getFirst();
				lucky.getRoad().removeVehicle(lucky);
				roadGreen.queue.pop();
				moveVehicleToNextRoad(lucky);
			}
			advanceTrafficLight();
		}
	}
	protected void advanceTrafficLight(){
		IncomingRoad roadGreen = incomings.get(trafficLight);
		roadGreen.isTrafficLightGreen = false;
		trafficLight++;
		if (trafficLight == incomings.size()){
			trafficLight = 0;
		}
		incomings.get(trafficLight).isTrafficLightGreen = true;
	}
	protected void fillReportDetails(Map<String, String> out) {
		StringBuilder reportJunct = new StringBuilder();
		incomings.forEach(r -> reportJunct.append(r.generateReport() + ","));

		if (incomings.size() != 0){
			reportJunct.delete(reportJunct.length() - 1, reportJunct.length());
		}

		out.put("queues", reportJunct.toString());
	}

	protected String getReportHeader() {
		return "junction_report";
	}
	/**
	 * Clase interna del simulador que representa una carretera de entrada.
	 * Guarda los coches del final de la carretera y el correspondiente semáforo.
	 * 
	 * @author Miguel Franqueira Varela
	 *
	 */
	protected class IncomingRoad {
		protected ArrayDeque<Vehicle> queue;
		protected String id;
		protected boolean isTrafficLightGreen;

		public IncomingRoad(String id) {
			queue = new ArrayDeque<>();
			this.id = id;
			isTrafficLightGreen = false;
		}

		protected String generateReport() {
			/*StringBuilder vehiclesInQueue = new StringBuilder();
			queue.forEach(vehicle -> vehiclesInQueue.append(vehicle.getId() + ","));
			if (queue.size() != 0){
				vehiclesInQueue.delete(vehiclesInQueue.length() - 1,
						vehiclesInQueue.length());
			}*/
			StringBuilder string = new StringBuilder();
			string.append("(" + id + ",");
			string.append(trafficLightReport());
			string.append(",[" + generateVehiclesInQueue() + "])");

			return string.toString();
		}
		private StringBuilder generateVehiclesInQueue(){
			StringBuilder vehiclesInQueue = new StringBuilder();
			queue.forEach(vehicle -> vehiclesInQueue.append(vehicle.getId() + ","));
			if (queue.size() != 0){
				vehiclesInQueue.delete(vehiclesInQueue.length() - 1,
						vehiclesInQueue.length());
			}
			return vehiclesInQueue;
			
		}
		protected String trafficLightReport(){
			return isTrafficLightGreen ? "green" : "red";
		}
	}

	@Override
	public void describe(Map<String, String> out) {
		out.put("ID", this.id);
		StringBuilder green=new StringBuilder();
		StringBuilder red=new StringBuilder();
		for(IncomingRoad ir:incomings){
			if(ir.isTrafficLightGreen){
				green.append(ir.generateReport() + ",");		
			} else{
				red.append(ir.generateReport()+",");
			}
		}
		if(green.length()!=0){
			green.delete(green.length()-1,green.length());
		}
		if(red.length()!=0){
			red.delete(red.length()-1,red.length());
		}
		out.put("Green","["+green.toString()+"]");
		out.put("Red","["+red.toString()+"]");
	}
}
