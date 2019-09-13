package nl.vu.cs.align;

public class TextProgressBar extends ProgressBar {

	public TextProgressBar() {
	}
	
	public TextProgressBar(String name, float maxValue) {
		start(name, maxValue);
	}
	
	public void info(String s) {
		System.err.println(s);
	}
	
	protected void showBar(String reason) {
		int percent = 100;
		boolean reasonGiven = (reason != null) && (reason.length() >0);
		if(max > 0) percent = Math.round((current*100) / max);
		
		if (percent > 100) percent = 100;
		if (percent < 0) percent = 0;
		
		if (percent != lastShown || reasonGiven) {
			System.err.print(name);
			if (reasonGiven) {
				System.err.print(" ");
				System.err.print(reason);
			}
			System.err.print(": ");
			System.err.print(percent);
			System.err.println("%");
			lastShown = percent;
		}
	}
	
}
