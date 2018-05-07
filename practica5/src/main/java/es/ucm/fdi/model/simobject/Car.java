package es.ucm.fdi.model.simobject;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Vehículo que se puede averiar con una cierta probabilidad.
 * 
 * @author Miguel Franqueira Varela
 * 
 */
public class Car extends Vehicle {

	protected String type;
	protected int resistanceKm;
	protected int kmSinceFault;
	protected double faultProbability;
	protected int maxFaultDuration;
	protected Random numRand;

	public Car(int velMax, List<Junction> cruc, String id, String type,
			int resistanceKm, double faultProbability, int maxFaultDuration,
			long seed) {
		super(velMax, cruc, id);
		this.type = type;
		this.resistanceKm = resistanceKm;
		this.kmSinceFault = 0;
		this.faultProbability = faultProbability;
		this.maxFaultDuration = maxFaultDuration;
		this.numRand = new Random(seed);
	}
	public void advance() {
		if (faultyTime == 0 && kmSinceFault > resistanceKm
				&& numRand.nextDouble() < faultProbability) {
			setTimeFault(numRand.nextInt(maxFaultDuration) + 1);
		}

		if (faultyTime > 0) {
			faultyTime--;
			kmSinceFault = 0;
		} else {
			if (location + realSpeed >= realRoad.getLength()) {
				kilometrage += realRoad.getLength() - location;
				kmSinceFault += realRoad.getLength() - location;
				location = realRoad.getLength();
				realSpeed = 0;

				realRoad.getEndJunction().addVehicle(this);
			} else {
				kilometrage += realSpeed;
				kmSinceFault += realSpeed;
				location += realSpeed;
			}
		}
	}
	public void fillReportDetails(Map<String, String> out) {
		out.put("type", type);
		super.fillReportDetails(out);
	}

}
