package es.ucm.fdi.simobject;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CarTest {
	@Test
	public void carTest() {
		List<Junction> itinerary = new ArrayList<>();
		itinerary.add(new Junction("j1"));
		itinerary.add(new Junction("j2"));
		itinerary.add(new Junction("j3"));
		Car c = new Car(10, itinerary, "v1", "car", 2, 1, 3, 32);
		Road r1 = new Road("r1", 100, 20, itinerary.get(0), itinerary.get(1));
		Road r2 = new Road("r2", 15, 20, itinerary.get(1), itinerary.get(2));
		c.moveToNextRoad(r1);
		c.avanza();
		c.avanza();
		c.avanza();
		assertTrue("Debería haberse estropeado", c.getTiempoAveria() == 0);
	}

}