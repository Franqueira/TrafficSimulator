package es.ucm.fdi.exceptions;

@SuppressWarnings("serial")
/**
 * 
 * Excepción creada para mostrar errores propios de la simulación.
 * 
 * @author Miguel Franqueira Varela
 *
 */
public class SimulatorException extends RuntimeException{
	
	public SimulatorException(String message) {
		super(message);
	}
	public SimulatorException(String message, Throwable cause){
		super(message,cause);
	}

}
