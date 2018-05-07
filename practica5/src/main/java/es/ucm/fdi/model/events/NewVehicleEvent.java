package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.exceptions.SimulatorException;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.simobject.Junction;
import es.ucm.fdi.model.simobject.Vehicle;

/**
 * Crea un nuevo vehículo
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class NewVehicleEvent extends Event {

	protected String id;
	protected int maxSpeed;
	protected String[] junctions;

	public NewVehicleEvent(int time, String id, int maxSpeed, String[] junctions) {
		super(time);
		this.id = id;
		this.maxSpeed = maxSpeed;
		this.junctions = junctions;
	}

	@Override
	public void execute(RoadMap things) {
		if (things.getObject(id) != null) {
			throw new SimulatorException("Ups, " + id + " already exists");
		}
		List<Junction> it = new ArrayList<>();
		for (String s : junctions) {
			Junction step = things.getJunction(s);
			if (step == null) {
				throw new SimulatorException(
						"The vehicle"+id+" goes over ghost junctions");
			}
			it.add(step);
		}

		Vehicle v = new Vehicle(maxSpeed, it, id);
		things.addVehicle(v);
		things.getJunction(junctions[0]).moveVehicleToNextRoad(v);
	}

	public static class Builder implements Event.Builder {

		public boolean canParse(String title, String type) {
			return "new_vehicle".equals(title) && "".equals(type);
		}

		public Event parse(Map<String, String> map) {
			try {
				String id = checkId(map);

				int time = checkNoNegativeIntOptional("time", map);

				int maxSpeed = checkPositiveInt("max_speed", map);

				String[] junctions = checkContains("itinerary", map).split(",");
				if (junctions.length < 2) {
					throw new SimulatorException("Missing destination on "+id);
				}

				return new NewVehicleEvent(time, id, maxSpeed, junctions);
			} catch (Exception  e) {
				throw new IllegalArgumentException(
						"Incorrect arguments for new_vehicle", e);
			}
		}
	}

	@Override
	public void describe(Map<String, String> out) {
		out.put("Time", ""+this.time);
		out.put("Type","New Vehicle "+this.id);
		
	}
}
