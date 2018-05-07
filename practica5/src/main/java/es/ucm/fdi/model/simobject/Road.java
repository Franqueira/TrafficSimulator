package es.ucm.fdi.model.simobject;

import java.util.Map;

import es.ucm.fdi.model.Describable;
import es.ucm.fdi.util.MultiTreeMap;
/**
 * Carretera convencional por la que pasan coches. 
 * Sale de un cruce y llega a otro. 
 * Responsabilidades:
 * Se encarga de hacer circular vehículos en su interior y llevarlos hacia su cruce de destino.
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class Road extends SimObject implements Describable {
	protected int length;
	protected int maxSpeed;
	protected Junction start;//Unused with this implementation
	protected Junction end;
	protected MultiTreeMap<Integer, Vehicle> vehicles;

	public Road(String id, int length, int maxSpeed, Junction start, Junction end) {
		super(id);
		this.length = length;
		this.maxSpeed = maxSpeed;
		vehicles = new MultiTreeMap<>((a, b) -> b - a);
		this.start=start;
		this.end = end;
	}
	public Iterable<Vehicle> getVehiclesOfRoad(){
		return vehicles.innerValues();
	}
	public Junction getEndJunction() {
		return end;
	}
	public Junction getStartJunction(){
		return start;
	}
	public int getLength() {
		return length;
	}
	public void newVehicleR(Vehicle vehicle) {
		vehicles.putValue(0, vehicle);
	}
	public void removeVehicle(Vehicle vehicle) {
		vehicles.removeValue(length, vehicle);
	}
	public int calculateBaseSpeed() {
		long n = vehicles.sizeOfValues();
		if (n < 1){
			n = 1;
		}
		if (maxSpeed < (maxSpeed / n) + 1){
			return maxSpeed;
		}
		else{
			return (maxSpeed / (int) n) + 1;
		}
	}
	public void advance() {
		MultiTreeMap<Integer, Vehicle> news = new MultiTreeMap<>((a, b) -> b-a);
		int baseSpeed = calculateBaseSpeed();
		int reductionFactor = 1;
		for (Vehicle vehicle : vehicles.innerValues()) {
			if(reductionFactor == 1){
				if(vehicle.getFaultyTime() != 0){
					reductionFactor=2;
				}
			}
			if(vehicle.getLocation() < length){
				if (vehicle.getFaultyTime() == 0){
					vehicle.setRealSpeed(baseSpeed / reductionFactor);
				}
				vehicle.advance();
			}
			news.putValue(vehicle.getLocation(), vehicle);
		}
		vehicles = news;
	}
	protected void fillReportDetails(Map<String, String> out) {
		StringBuilder meter = new StringBuilder();
		for (Vehicle v : vehicles.innerValues()) {
			meter.append(v.getFillVehicle() + ",");
		}
		
		if (!vehicles.isEmpty()){
			meter.delete(meter.length() - 1, meter.length());
		}
		out.put("state", meter.toString());
	}

	protected String getReportHeader() {
		return "road_report";
	}

	@Override
	public void describe(Map<String, String> out) {
		out.put("ID",this.id);
		out.put("Source",this.start.getId());
		out.put("Target",this.end.getId());
		out.put("Length",""+this.length);
		out.put("Max Speed",""+this.maxSpeed);
		StringBuilder string=new StringBuilder();
		for (Vehicle v : vehicles.innerValues()) {
			string.append(v.getId()+ ",");
		}
		
		if (!vehicles.isEmpty()){
			string.delete(string.length() - 1, string.length());
		}
		out.put("Vehicles","["+string+"]");
	}
}