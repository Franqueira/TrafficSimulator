package es.ucm.fdi.model;

import java.io.OutputStream;
import java.util.*;

import javax.swing.SwingUtilities;

import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simobject.Junction;
import es.ucm.fdi.model.simobject.Road;
import es.ucm.fdi.model.simobject.SimObject;
import es.ucm.fdi.model.simobject.Vehicle;
import es.ucm.fdi.util.MultiTreeMap;

/**
 * 
 * Se encarga de simular todo. Para ello tiene acceso a todos los objetos de
 * simulación, también almacena todos los eventos y la lista de listener.
 * Responsabilidades: Se encarga de que todos los objetos de simulación avancen
 * Genera el report en la salida pasada por el controlador.
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class TrafficSimulator {
	private RoadMap objects;
	private MultiTreeMap<Integer, Event> events;
	private int timeCounter;
	private List<Listener> listeners;
	private List<SimObject> filter;

	public TrafficSimulator() {
		listeners = new ArrayList<>();
		filter = new ArrayList<>();
		objects = new RoadMap();
		events = new MultiTreeMap<>();
		timeCounter = 0;
	}

	public boolean hasEvents() {
		return events.size() != 0;
	}

	public void setTimeCounter(int timeCounter) {
		this.timeCounter = timeCounter;
	}

	public void setFilter(List<SimObject> filter) {
		this.filter = filter;
	}

	public void addEvent(Event e) {
		if (e.getTime() < timeCounter) {
			fireUpdateEvent(EventType.ERROR,
					"ERROR adding a event.\nWe don't travel back in time!\nEvent time is: "
							+ e.getTime() + "\n"
							+ "Time of the simulation is: " + timeCounter
							+ "\n");
			throw new IllegalArgumentException(
					"ERROR adding a event.\nWe don't travel back in time!\nEvent time is: "
							+ e.getTime() + "\n"
							+ "Time of the simulation is: " + timeCounter
							+ "\n");
		}
		events.putValue(e.getTime(), e);
		fireUpdateEvent(EventType.NEWEVENT, null);
	}

	public void run(int numSteps, OutputStream out) {
		try {
			int timeLimit = timeCounter + numSteps - 1;
			while (timeCounter <= timeLimit) {
				List<Event> nowEvents = events.get(timeCounter);
				if (nowEvents != null) {
					for (Event e : nowEvents) {
						e.execute(objects);
					}
				}
				for (Road r : objects.getRoads()) {
					r.advance();
				}

				for (Junction j : objects.getJunctions()) {
					j.advance();
				}
				timeCounter++;
				if (out != null) {
					generateReport(out);
				}
			}
		} catch (Exception e) {
			fireUpdateEvent(EventType.ERROR,
					"Error running the simulator at time " + timeCounter + ":\n"
							+ e.getCause());
			System.err.println("Error running the simulator at time "
					+ timeCounter + ":\n" + e.getMessage());
		}
		fireUpdateEvent(EventType.ADVANCED, null); // no se si va a ir aquí
	}

	private void addSectionsFor(List<? extends SimObject> it, Ini report) {
		for (SimObject j : it) {
			Map<String, String> map = j.report(timeCounter);
			IniSection section = new IniSection(map.get(""));
			map.remove("");
			map.forEach((k, v) -> section.setValue(k, v));
			report.addsection(section);
		}
	}

	public void reset() {
		objects.reset();
		events.clear();
		timeCounter = 0;
		fireUpdateEvent(EventType.RESET, null);

	}

	public void generateReport(OutputStream out) {
		try {
			Ini report = new Ini();
			if (filter.isEmpty()) {
				addSectionsFor(objects.getJunctions(), report);
				addSectionsFor(objects.getRoads(), report);
				addSectionsFor(objects.getVehicles(), report);
			} else {
				addSectionsFor(filter, report);
			}

			report.store(out);
		} catch (Exception e) {
			fireUpdateEvent(EventType.ERROR, "Error saving in the ini at time "
					+ timeCounter + ":\n" + e.getMessage());
			System.err.println("Error saving in the ini at time :"
					+ timeCounter + ":\n" + e.getMessage());
		}
	}

	private void fireUpdateEvent(EventType type, String error) {
		UpdateEvent ue = new UpdateEvent(type);
		for (Listener l : listeners) {
			SwingUtilities.invokeLater(() -> l.update(ue, error));
		}
	}

	public void addSimulatorListener(Listener l) {
		listeners.add(l);
		UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
		// evita pseudo-recursividad
		SwingUtilities.invokeLater(() -> l.update(ue, null));
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	public interface Listener {
		void update(UpdateEvent ue, String error);
	}

	public enum EventType {
		REGISTERED, RESET, NEWEVENT, ADVANCED, ERROR;
	}

	/**
	 * 
	 * Clase interna del simulador que permite crear un nuevo evento y tener
	 * acceso a todos los datos del simulador desde otras clases.
	 * 
	 * @author Miguel Franqueira Varela
	 *
	 */
	public class UpdateEvent {
		private EventType evento;

		public UpdateEvent(EventType evento) {
			this.evento = evento;
		}

		public EventType getEvent() {
			return evento;
		}

		public List<Road> getRoads() {
			return objects.getRoads();
		}

		public RoadMap getRoadMap() {
			return objects;
		}

		public List<Junction> getJunctions() {
			return objects.getJunctions();
		}

		public List<Vehicle> getVehicles() {
			return objects.getVehicles();
		}

		public List<Event> getEvenQueue() {
			return events.valuesList();
		}

		public int getCurrentTime() {
			return (int) System.currentTimeMillis();
		}
	}
}
