package es.ucm.fdi.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import es.ucm.fdi.model.TrafficSimulator;
import es.ucm.fdi.model.TrafficSimulator.*;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simobject.*;
import es.ucm.fdi.view.*;

@SuppressWarnings("serial")
/**
 * 
 * Ventana principal de la interfaz gráfica del programa. Responsabilidades:
 * Mostrar todos los paneles divididos así como crear todas las acciones,las tablas y 
 * editores de texto llamando a otras clases. 
 * Crear el menú y la barra de tareas con las acciones creadas.
 * Mantener todo el contenido que tiene en su interior actualizado, para ello tiene un listener.
 * @see TrafficSimulator
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class SimWindow extends JFrame {
	private Controller control;
	private TrafficSimulator simulator; // no es necesario

	private File file;
	private JFileChooser chooser;
	private SimulationListener listener;

	private GUITextArea eventsEditor;
	private GUITable<Event> eventsQueue;
	private GUITextArea reportsArea;
	private GUITable vehiclesTable;
	private GUITable roadsTable;
	private GUITable junctionsTable;
	private RoadMapPaint roadMap;
	private JLabel statusBar;
	private HashMap<Command, SimulatorAction> actions;
	private JToolBar bar;
	private JSpinner steps;
	private JTextField time;
	private JCheckBoxMenuItem redirectOutput;
	private OutputStream out;
	private List<SimObject> filter;
	private static final Dimension ROADMAP_SIZE = new Dimension(100, 100);

	public SimWindow(Controller c, String file){
		super("Traffic Simulator");
		setSize(1000, 700);
		control = c;
		if(file!=null){
			this.file = new File(file);
		}
		// lo tengo como atributo simplemente para simplificar futuro código
		simulator = control.getSimulator();
		listener = new SimulationListener();
		simulator.addSimulatorListener(listener);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createActions();
		inicializa();
		addBars();
		addMenu();
		addPanels();
		if (file != null) {
			try {
				eventsEditor.readFile(this.file);
			} catch (Exception e) {
				showErrorMessage("Failure reading the file"
						+ this.file.getAbsolutePath());
			}
		}
	}

	private void inicializa() {

		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
		chooser.setCurrentDirectory(new File("src/test/resources/examples/"));
		filter = new ArrayList<>();
		JTextArea area = new JTextArea("");
		SimulatorAction[] acts = new SimulatorAction[4];
		acts[0] = actions.get(Command.Clear);
		acts[1] = actions.get(Command.Open);
		acts[2] = actions.get(Command.Save);
		acts[3] = actions.get(Command.Filter);
		try {
			eventsEditor = new GUITextArea(true, "Events", new JTextArea(""),
					acts);
			reportsArea = new GUITextArea(false, "Reports", area, acts);
		} catch (IOException e) {
			showErrorMessage("Failing creating Events or Reports area:\n"
					+ e.getMessage());
		}
		addEventsQueue();
		addVehiclesTable();
		addRoadsTable();
		addJunctionsTable();
		addStatusBar();
		redirectOutput = new JCheckBoxMenuItem("Redirect Output");
		out = reportsArea.getGUITextAreaOutputStream();
		roadMap = new RoadMapPaint(ROADMAP_SIZE);
	}

	private void addEventsQueue() {
		String[] rows = { "#", "Time", "Type" };
		List<Event> events = new ArrayList<>();
		eventsQueue = new GUITable<>(rows, events);
		eventsQueue.setBorder("Events Queue");
	}

	private void addVehiclesTable() {
		String[] rows = { "ID", "Road", "Location", "Speed", "Km",
				"Faulty Units", "Itinerary" };
		List<Vehicle> vehiculos = new ArrayList<>();
		vehiclesTable = new GUITable<>(rows, vehiculos);
		vehiclesTable.setBorder("Vehicles");
	}

	private void addRoadsTable() {
		String[] rows = { "ID", "Source", "Target", "Length", "Max Speed",
				"Vehicles" };
		List<Road> roads = new ArrayList<>();
		roadsTable = new GUITable<>(rows, roads);
		roadsTable.setBorder("Roads");
	}

	private void addJunctionsTable() {
		String[] rows = { "ID", "Green", "Red" };
		List<Junction> junctions = new ArrayList<>();
		junctionsTable = new GUITable<>(rows, junctions);
		junctionsTable.setBorder("Junctions");
	}

	private void addStatusBar() {
		JPanel statusBarPanel = new JPanel();
		statusBar = new JLabel("Traffic Simulator");
		statusBarPanel.add(statusBar);
	}

	private void addBars() {

		steps = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));
		// steps.setPreferredSize(new Dimension(3,3));
		time = new JTextField("0", 1);
		bar = new JToolBar();
		bar.add(actions.get(Command.Open));
		bar.add(actions.get(Command.Save));
		bar.add(actions.get(Command.Clear));

		bar.addSeparator();

		bar.add(actions.get(Command.Events));
		bar.add(actions.get(Command.Play));
		bar.add(actions.get(Command.Reset));
		bar.add(new JLabel("Steps: "));
		bar.add(steps);
		add(bar, BorderLayout.NORTH);
		bar.add(new JLabel("Time: "));
		bar.add(time);

		bar.addSeparator();

		bar.add(actions.get(Command.Report));
		bar.add(actions.get(Command.Filter));
		bar.add(actions.get(Command.Delete_Report));
		bar.add(actions.get(Command.Save_Report));
		bar.add(actions.get(Command.Exit));
	}

	private void addMenu() {

		// add actions to menubar, and bar to window
		JMenu file = new JMenu("File");
		JMenu simulator = new JMenu("Simulator");
		JMenu reports = new JMenu("Reports");
		file.add(actions.get(Command.Open));
		file.add(actions.get(Command.Save));
		file.addSeparator();
		file.add(actions.get(Command.Save_Report));
		file.addSeparator();
		file.add(actions.get(Command.Exit));
		/*
		 * .add(actions.get(Command.Clear));
		 * simulator.add(actions.get(Command.Events));
		 */
		simulator.add(actions.get(Command.Play));
		simulator.add(actions.get(Command.Reset));
		simulator.add(redirectOutput);
		reports.add(actions.get(Command.Report));
		reports.add(actions.get(Command.Filter));
		reports.add(actions.get(Command.Delete_Report));
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		menu.add(simulator);
		menu.add(reports);
		setJMenuBar(menu);
	}

	private void createActions() {

		actions = new HashMap<>();
		actions.put(Command.Open, new SimulatorAction(Command.Open, "open.png",
				"Abrir archivo", KeyEvent.VK_O, "control shift O",
				() -> loadEvent()));
		actions.put(Command.Save, new SimulatorAction(Command.Save, "save.png",
				"Guardar los eventos", KeyEvent.VK_S, "control S",
				() -> saveEvent()));
		actions.put(Command.Clear, new SimulatorAction(Command.Clear,
				"clear.png", "Limpiar zona de eventos", KeyEvent.VK_L,
				"control shift L", () -> clearEvents()));
		actions.put(Command.Events, new SimulatorAction(Command.Events,
				"events.png", "Muestra los eventos", KeyEvent.VK_E,
				"control shift E", () -> putEventsOnTable()));
		actions.put(Command.Play, new SimulatorAction(Command.Play, "play.png",
				"Avanza la simulación", KeyEvent.VK_P, "control shift P",
				() -> play()));
		actions.put(Command.Reset, new SimulatorAction(Command.Reset,
				"reset.png", "Reiniciar la aplicación", KeyEvent.VK_C,
				"control shift C", () -> reset()));
		actions.put(Command.Report, new SimulatorAction(Command.Report,
				"report.png", "Genera el report", KeyEvent.VK_R,
				"control shift R", () -> report()));
		actions.put(Command.Delete_Report, new SimulatorAction(
				Command.Delete_Report, "delete_report.png",
				"Eliminar los reports", KeyEvent.VK_D, "Control shift W",
				() -> deleteReport()));
		actions.put(Command.Save_Report, new SimulatorAction(
				Command.Save_Report, "save_report.png", "Guarda los reports",
				KeyEvent.VK_G, "control shift G", () -> saveReport()));
		actions.put(Command.Exit, new SimulatorAction(Command.Exit, "exit.png",
				"Salir de la aplicacion", KeyEvent.VK_A, "control shift X",
				() -> System.exit(0)));
		actions.put(Command.Filter, new SimulatorAction(Command.Filter,
				"filter.png", "Filter reports", KeyEvent.VK_F,
				"control shift F", () -> filter()));
		actions.get(Command.Play).setEnabled(false);
		actions.get(Command.Delete_Report).setEnabled(false);
	}

	public void addPanels() {
		JSplitPane eventsSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				eventsEditor, eventsQueue);
		JSplitPane topRow = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				eventsSplit, reportsArea);

		JSplitPane vehiclesAndRoadsSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, vehiclesTable, roadsTable);
		JSplitPane allTablesSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				vehiclesAndRoadsSplit, junctionsTable);
		JSplitPane bottomRow = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				allTablesSplit, roadMap);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topRow, bottomRow);

		add(mainSplit, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		setVisible(true);

		eventsSplit.setDividerLocation(.5);
		eventsSplit.setResizeWeight(.5);

		topRow.setDividerLocation(.66);
		topRow.setResizeWeight(.66);

		allTablesSplit.setDividerLocation(.6);
		allTablesSplit.setResizeWeight(.6);

		vehiclesAndRoadsSplit.setDividerLocation(.5);
		vehiclesAndRoadsSplit.setResizeWeight(.5);

		bottomRow.setDividerLocation(.5);
		bottomRow.setResizeWeight(.5);

		mainSplit.setDividerLocation(.33);
		mainSplit.setResizeWeight(.33);

	}

	private class SimulationListener implements TrafficSimulator.Listener {
		private void refreshTables(UpdateEvent ue) {
			eventsQueue.setElements(ue.getEvenQueue());
			vehiclesTable.setElements(ue.getVehicles());
			roadsTable.setElements(ue.getRoads());
			junctionsTable.setElements(ue.getJunctions());
			roadMap.setRoadMap(ue.getRoadMap());
		}

		@Override
		public void update(UpdateEvent ue, String error) {
			EventType type = ue.getEvent();
			statusBar.removeAll();
			switch (type) {
			case REGISTERED:
				statusBar.setText("A new listener has been registered");
				break;
			case RESET:
				refreshTables(ue);
				statusBar.setText("The simulation has been reset.");
				break;
			case NEWEVENT:
				eventsQueue.setElements(ue.getEvenQueue());
				statusBar.setText("New event has been registered.");
				break;
			case ADVANCED:
				refreshTables(ue);
				time.setText(Integer.toString(Integer.parseInt(time.getText())
						+ (int) steps.getValue()));
				statusBar.setText("The simulator has avanced on time.");
				break;
			case ERROR:
				showErrorMessage(error);
				break;
			}
		}
	}

	private void showErrorMessage(String e) {
		JOptionPane.showMessageDialog(this, e, "ERROR",
				JOptionPane.ERROR_MESSAGE);

	}

	protected void loadEvent() {
		int valid = chooser.showOpenDialog(getParent());
		if (valid == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			if (file != null && file.canRead()) {
				try {
					eventsEditor.readFile(file);
				} catch (Exception e) {
					showErrorMessage("Failure reading events in the file:\n"
							+ file.getAbsolutePath());
				}
				actions.get(Command.Clear).setEnabled(true);
			}
		}
	}

	protected void saveEvent() {
		int valid = chooser.showSaveDialog(getParent());
		if (valid == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();

			try {
				FileOutputStream output;
				output = new FileOutputStream(file);
				output.write(eventsEditor.getText().getBytes());
			} catch (Exception e) {
				showErrorMessage("Failure writing events in the file:\n"
						+ file.getAbsolutePath());

			}
		}
	}

	protected void clearEvents() {
		eventsEditor.clear();
		actions.get(Command.Clear).setEnabled(false);
	}

	protected void putEventsOnTable() {
		control.setIn(eventsEditor.getFileTextInputStream());
		try {
			control.loadEvents();
		} catch (Exception e) {
			showErrorMessage("Error loading events from eventsEditor. "
					+ "It is probably because of the file:\n"
					+ file.getAbsolutePath() + "\n" + e.getMessage());
		}
		eventsEditor.clear();
		actions.get(Command.Play).setEnabled(true);
		actions.get(Command.Clear).setEnabled(false);
	}

	protected void play() {
		control.setOut(redirectOutput.isSelected() ? out : null);
		simulator.setTimeCounter(Integer.parseInt(time.getText()));
		control.setTicks((int) steps.getValue());
		control.run();
		actions.get(Command.Delete_Report).setEnabled(true);
	}

	protected void reset() {
		simulator.reset();
		time.setText("0");
		actions.get(Command.Play).setEnabled(false);
		actions.get(Command.Delete_Report).setEnabled(false);
	}

	protected void report() {
		/*
		 * solo genera los del tiempo asociado. si se quiere que se generen en
		 * cada ciclo del tiempo hay que activar el redirectOutput
		 */
		simulator.generateReport(out);
		actions.get(Command.Delete_Report).setEnabled(true);
	}

	protected void deleteReport() {
		reportsArea.clear();
		actions.get(Command.Delete_Report).setEnabled(false);
	}

	protected void filter() {
		filter.clear();
		List<Junction> junctions = junctionsTable.getSelectedObjects();
		for (Junction c : junctions) {
			filter.add(c);
		}
		List<Road> roads = roadsTable.getSelectedObjects();
		for (Road c : roads) {
			filter.add(c);
		}
		List<Vehicle> vehicles = vehiclesTable.getSelectedObjects();
		for (Vehicle c : vehicles) {
			filter.add(c);
		}
		simulator.setFilter(filter);

	}

	protected void saveReport() {
		int valid = chooser.showSaveDialog(getParent());
		if (valid == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			try (FileOutputStream output = new FileOutputStream(file)) {
				output.write(reportsArea.getText().getBytes());
			} catch (IOException e) {
				showErrorMessage("Failure writing reports in the file:\n"
						+ file.getAbsolutePath());
			}
		}
	}
}
