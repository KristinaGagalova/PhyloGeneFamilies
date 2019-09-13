package nl.vu.cs.align;


public abstract class ProgressBar {
	
	String name;
	
	float max;
	
	float current;
	
	int lastShown = -1;
	
	long startTime;
	
	public void start(String name, float maxValue) {
		this.name = name;
		max = maxValue;
		current = 0;
		startTime = System.currentTimeMillis();
		
		showBar(" starting");
	}
	
	public void info(String s) {
	}
	
	public void advance() {
		advance(1);
	}
	
	public void advance(String reason) {
		advance(1, reason);
	}
	
	public void advance(float a) {
		advance(a, null);
	}
	
	public void advance(float a, String reason) {
		current += a;
		showBar(reason);
	}
	
	public void stop() {
		long stopTime = System.currentTimeMillis();
		long timediff = stopTime - startTime;
		current = max;
		showBar("completed in "+(timediff/1000)+" seconds");
		
	}
	
	protected abstract void showBar(String reason);
	
}
