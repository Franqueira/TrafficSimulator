package es.ucm.fdi.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.events.MakeVehicleFaultyEvent;
import es.ucm.fdi.model.events.NewBikeEvent;
import es.ucm.fdi.model.events.NewCarEvent;
import es.ucm.fdi.model.events.NewDirtEvent;
import es.ucm.fdi.model.events.NewHighwayEvent;
import es.ucm.fdi.model.events.NewJunctionEvent;
import es.ucm.fdi.model.events.NewMostCrowedEvent;
import es.ucm.fdi.model.events.NewRoadEvent;
import es.ucm.fdi.model.events.NewRoundRobinEvent;
import es.ucm.fdi.model.events.NewVehicleEvent;

/**
 * 
 * Controla el simulador, para ello guarda la entrada, la salida, el número de
 * ticks a ejecutar y el simulador. Responsabilidades:
 * Llamar al método run del simulador pasándole la salida.
 * Cargar los eventos desde el fichero de entrada.
 * 
 * @see TrafficSimulator
 * @author Miguel Franqueira Varela
 *
 */

public class Controller {
	private static Event.Builder[] avaliableEvents = {
			new NewVehicleEvent.Builder(), new NewCarEvent.Builder(),
			new NewBikeEvent.Builder(), new NewRoadEvent.Builder(),
			new NewHighwayEvent.Builder(), new NewDirtEvent.Builder(),
			new NewJunctionEvent.Builder(), new NewRoundRobinEvent.Builder(),
			new NewMostCrowedEvent.Builder(),
			new MakeVehicleFaultyEvent.Builder() };

	private TrafficSimulator simulator;
	private InputStream in;
	private OutputStream out;
	private int ticks;

	public Controller(InputStream in, OutputStream out, int ticks) {
		this.in = in;
		this.out = out;
		this.ticks = ticks;
		this.simulator = new TrafficSimulator();
	}

	protected OutputStream getOut() {
		return this.out;
	}

	protected void setOut(OutputStream out) {
		this.out = out;
	}

	protected void setIn(InputStream in) {
		this.in = in;
	}

	protected void setTicks(int ticks) {
		this.ticks = ticks;
	}

	protected TrafficSimulator getSimulator() {
		return simulator;
	}

	public void run() {
		simulator.run(ticks, out);
	}

	public void loadEvents() throws IOException {
		
		Ini init = new Ini(in);
		List<IniSection> list = init.getSections();

		for (IniSection i : list) {
			try {
				boolean found = false;
				Map<String, String> map = i.getKeysMap();
				for (Event.Builder b : avaliableEvents) {
					if (b.canParse(i.getTag(), map.getOrDefault("type", ""))) {
						simulator.addEvent(b.parse(map));
						found = true;
						break;
					}
				}
				
				if (!found) {
					throw new IllegalArgumentException(
							"Not sure about what is this: " + i.getTag());
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("ERROR in the inisection:\n"
						+ i.toString() + "\n" + e.getMessage());
			}

		}
	}
}