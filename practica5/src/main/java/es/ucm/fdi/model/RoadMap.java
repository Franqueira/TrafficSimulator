package es.ucm.fdi.model;

import java.util.*;

import es.ucm.fdi.model.simobject.*;

/**
 * Clase en la que se guarda todos los objetos de simulación.
 * Se encarga de la gestión de estos objetos de simulación, 
 * añadiendo o devolviendo el objeto u objetos pedidos.
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class RoadMap {
	private List<Vehicle> vehicles;
	private List<Road> roads;
	private List<Junction> junctions;

	private Map<String, SimObject> map;

	public RoadMap() {
		reset();
	}
	/**Devuelve el vehículo cuya id coincida con la pasada como argumento*/
	public Vehicle getVehicle(String id) {
		SimObject obj = map.get(id);
		if (obj instanceof Vehicle){
			return (Vehicle) obj;
		}else{
			return null;
		}
	}
	/**Devuelve la carretera cuya id coincida con la pasada como argumento*/
	public Road getRoad(String id) {
		SimObject obj = map.get(id);
		if (obj instanceof Road){
			return (Road) obj;
		}else{
			return null;
		}
	}
	/**Devuelve el cruce cuya id coincida con la pasada como argumento*/
	public Junction getJunction(String id) {
		SimObject obj = map.get(id);
		if (obj instanceof Junction){
			return (Junction) obj;
		}else{
			return null;
		}
	}
	/**Devuelve el objeto de simulación cuya id coincida con la pasada como argumento*/
	public SimObject getObject(String id) {
		return map.get(id);
	}
	/**Devuelve una lista con todos los vehículos*/
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	/**Devuelve una lista con todas las carreteras*/
	public List<Road> getRoads() {
		return roads;
	}
	/**Devuelve una lista con todos los cruces*/
	public List<Junction> getJunctions() {
		return junctions;
	}
	/**Añade un vehículo a la simulación*/
	public void addVehicle(Vehicle v) {
		map.put(v.getId(), v);
		vehicles.add(v);
	}
	/**Devuelve una lista con todas las carreteras*/
	public void addRoad(Road r) {
		map.put(r.getId(), r);
		roads.add(r);
	}
	/**Devuelve una lista con todos los cruces*/
	public void addJunction(Junction j) {
		map.put(j.getId(), j);
		junctions.add(j);
	}
	public void reset(){
		this.vehicles = new ArrayList<>();
		this.roads = new ArrayList<>();
		this.junctions = new ArrayList<>();
		this.map = new HashMap<>();
		
	}
}
