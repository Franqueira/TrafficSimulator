package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.exceptions.SimulatorException;
import es.ucm.fdi.model.RoadMap;
import es.ucm.fdi.model.simobject.Vehicle;

/**
 * 
 * Hace fallar los vehículos que tiene guardados como atributo (tiene guardado un string con las ids).
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class MakeVehicleFaultyEvent extends Event {
	
	protected int tiempoAveria;
	protected String vehicles;

	public MakeVehicleFaultyEvent(int time, int tiempoAveria, String vehicles) {
		super(time);
		this.tiempoAveria = tiempoAveria;
		this.vehicles = vehicles;
	}

	@Override
	public void execute(RoadMap things) {
		String[] arrayVehicles = vehicles.split(",");

		for (String vehicle : arrayVehicles) {
			Vehicle unlucky = things.getVehicle(vehicle);
			if (unlucky == null){
				throw new SimulatorException("Hitting the air");
			}
			unlucky.setTimeFault(tiempoAveria);
		}
	}

	public static class Builder implements Event.Builder {
		
		public boolean canParse(String title, String type){
			return "make_vehicle_faulty".equals(title) && "".equals(type);
		}

		public Event parse(Map<String, String> map) {
			try {
				
				int time = checkNoNegativeIntOptional("time", map);
				
				int tiempoAveria = checkPositiveInt("duration", map);

				String listaIds = checkContains("vehicles", map);

				return new MakeVehicleFaultyEvent(time, tiempoAveria, listaIds);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Incorrect arguments for make_vehicle_faulty");
			}
		}
	}

	@Override
	public void describe(Map<String, String> out) {
		out.put("Time", ""+this.time);
		out.put("Type","Break Vehicles "+"["+vehicles+"]");
	}
}
