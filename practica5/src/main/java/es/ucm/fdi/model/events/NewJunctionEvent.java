package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.exceptions.SimulatorException;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.simobject.Junction;

/**
 * Crea un nuevo cruce.
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class NewJunctionEvent extends Event {

	protected String id;

	public NewJunctionEvent(int time, String id) {
		super(time);
		this.id = id;
	}

	@Override
	public void execute(RoadMap things) {
		if (things.getObject(id) != null){
			throw new SimulatorException("Ups, " + id + " already exists");
		}
		things.addJunction(new Junction(id));
	}

	public static class Builder implements Event.Builder {
		
		public boolean canParse(String title, String type){
			return "new_junction".equals(title) && "".equals(type);
		}
		
		public Event parse(Map<String, String> map) {
			try {
				String id = checkId(map);

				int time = checkNoNegativeIntOptional("time", map);

				return new NewJunctionEvent(time, id);
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Incorrect arguments for new_junction");
			}
		}
	}

	@Override
	public void describe(Map<String, String> out) {
		out.put("Time", ""+this.time);
		out.put("Type","New Junction "+this.id);		
	}
}
