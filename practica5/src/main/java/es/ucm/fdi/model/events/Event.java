package es.ucm.fdi.model.events;

import java.util.Map;

import es.ucm.fdi.model.Describable;
import es.ucm.fdi.model.RoadMap;
/**
 * Se encarga de guardar en el RoadMap el resultado de ese evento,
 * Si es de creación lo añade y si es de fallo modifica los vehículos afectados.
 * También almacena la interfaz interna Builder.
 * 
 * @author Miguel Franqueira Varela
 * */
public abstract class Event implements Describable {
	protected int time;

	public Event(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}
	public abstract void execute(RoadMap things);

		/**
		 * 
		 * Interfaz interna que se contiene los métodos necesarios
		 * para crear un evento a partir de la lectura de un mapa.
		 * Las clases que la implementan crean el evento correspondiente a la clase en la
		 * que son internas.
		 * 
		 * @author Miguel Franqueira Varela
		 *
		 */
	public interface Builder {

		public abstract boolean canParse(String title, String type);

		public abstract Event parse(Map<String, String> map);

		default boolean isValidId(String id) {
			return id.matches("[a-zA-Z0-9_]+");
		}

		default String checkId(Map<String, String> map) {
			String id = map.get("id");
			if (id == null){
				throw new IllegalArgumentException("Missing id");
			}else if (!isValidId(id)){
				throw new IllegalArgumentException("Invalid id");
			}
			return id;
		}

		default int checkNoNegativeIntOptional(String s, Map<String, String> map) {
			int check = (map.containsKey(s) ? Integer.parseInt(map.get(s)) : 0);
			if (check < 0){
				throw new IllegalArgumentException("Negative " + s);
			}
			return check;
		}

		default int checkPositiveInt(String s, Map<String, String> map) {
			String num = map.get(s);
			if (num == null){
				throw new IllegalArgumentException("Missing " + s);
			}
			int check = Integer.parseInt(num);
			if (check <= 0){
				throw new IllegalArgumentException("No positive " + s);
			}
			return check;
		}

		default String checkContains(String s, Map<String, String> map) {
			if (!map.containsKey(s)){
				throw new IllegalArgumentException("Missing " + s);
			}
			return map.get(s);
		}
	}
}