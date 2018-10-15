package timerApplication;

import java.util.Vector;

public class Session {

	Vector<Lap> laps = new Vector<Lap>();
	
	public Session() {

		laps = new Vector<Lap>();

	}
	
	public void addLap(Lap l) {
		
		laps.add(l);
		
	}
	
	public float calculateAverageTime() {
		
		/* This method should calculate the
		   average time of all laps in the 
		   collection. It needs to return a 
		   float value */
		
		float avg = 0, sum = 0;
		
		for(int i = 0; i < laps.size(); i++) {
			
			sum = sum + laps.elementAt(i).getLapTime();
		}
		
		avg = sum/laps.size();
		
		return avg;
	}
	
	
	public Lap getFastestLap() {
		
		/* This method should step through the
		   collection, and return the Lap object
		   whose lap time is smallest (fastest). */
		
		Lap fastest = laps.firstElement(); // set lap to first element in the vector
		
		for(int i = 1; i < laps.size(); i++) { // iterate as many times as the amt of elements in the vector
			
			if(laps.get(i).getLapTime() < fastest.getLapTime()) { // compare fastest to all elements
				
				fastest = laps.get(i); // assign fastest new value if new is greater than its previous value
			}
		}
		
		return fastest;   
		
	}
	
	public Lap getSlowestLap() {
		
		/* This method should step through the
		   collection, and return the Lap object
		   whose lap time is largest (slowest). */
		
		Lap slowest = laps.firstElement(); // set lap to first element in the vector
		
		for(int i = 1; i < laps.size(); i++) { // iterate as many times as the amt of elements in the vector
			
			if(laps.get(i).getLapTime() > slowest.getLapTime()) { // compare fastest to all elements
				
				slowest = laps.get(i); // assign fastest new value if new is greater than its previous value
			}
		}
		
		return slowest;      
		
	}

}
