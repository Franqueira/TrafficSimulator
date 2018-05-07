package es.ucm.fdi.simobject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import es.ucm.fdi.model.simobject.Highway;
import es.ucm.fdi.model.simobject.Junction;
import es.ucm.fdi.model.simobject.Vehicle;
import static org.junit.Assert.*;

public class HighwayTest {
	@Test
	public void highwayTest() {
		List<Junction> itinerary = new ArrayList<>();
		itinerary.add(new Junction("j1"));
		itinerary.add(new Junction("j2"));
		itinerary.add(new Junction("j3"));
		Vehicle v = new Vehicle(5, itinerary, "v1");
		Vehicle v1 = new Vehicle(15, itinerary, "v2");
		Highway r1 = new Highway("r1", 30, 10, itinerary.get(0),
				itinerary.get(1), "highway", 2);
		Highway r2 = new Highway("r2", 15, 20, itinerary.get(1),
				itinerary.get(2), "highway", 2);
		itinerary.get(1).addIncoming(r1);
		itinerary.get(1).addOutgoing(r2);
		itinerary.get(2).addIncoming(r2);
		v.moveToNextRoad(r1);
		v1.moveToNextRoad(r1);
		v.setTimeFault(2);
		r1.advance();
		assertTrue("Velocidad inadecuada",
				v.getRealSpeed() == 0 && v1.getRealSpeed() == 10);
	}

}
