package timerApplication;


public class LapTimerThread implements Runnable {

	private boolean running;
	private float lapSeconds, totalSeconds;
	private LapTimer parent;
	private float increment = (float)0.1;

	/* The constructor takes the parent frame and the total
	   seconds that the application has been running for so
	   far as parameters, and then launches a thread. */
	public LapTimerThread(LapTimer parent, float totalSeconds) {

		this.parent = parent;
		
		running = true;
		
		lapSeconds = (float)0.0;
		this.totalSeconds = totalSeconds;
		(new Thread(this)).start();
	}
	
	public LapTimerThread(LapTimer parent, float lapSeconds, float totalSeconds) {
		
			this.parent = parent;
			
			running = true;
			
			this.lapSeconds = lapSeconds;
			this.totalSeconds = totalSeconds;
			(new Thread(this)).start();
	}

	@Override
	/* This method should keep incrementing the two counters - 
	   lapSeconds and totalSeconds - by the increment each
	   tenth of a second. It should then update the corresponding
	   text fields in the main display. */
	public void run() {
	
		while(running) {
			// update the text field every time the loop executes
			
			try {
				
			parent.updateLapDisplay(getLapSeconds());
			parent.updateTotalDisplay(getTotalSeconds());
			Thread.sleep(100);// wait 1/10 of a second
			
			
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			lapSeconds = lapSeconds+increment;
			totalSeconds = totalSeconds+increment;
		}
		
	}
	
	/* This method stops the thread. */
	public void stop() {

		/* Insert code here */
		running = false;
		lapSeconds = (float)0.0;
		parent.updateLapDisplay(lapSeconds); // set lap display to 0
	}
	
	public float getLapSeconds() {
		return lapSeconds;
		
	}
	
	public float getTotalSeconds() {
		
		return totalSeconds;
		
	}

}
