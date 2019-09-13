package nl.vu.cs.align;

public class FakeProgressBar extends ProgressBar {
	
	private static FakeProgressBar instance;

	public FakeProgressBar() {
	}
	
	public FakeProgressBar(String name, float maxValue) {
	}
	
	protected void showBar(String reason) {
	}
	
	public static FakeProgressBar getInstance() {
		if (instance == null)
			instance = new FakeProgressBar();
		return instance;
	}

}
