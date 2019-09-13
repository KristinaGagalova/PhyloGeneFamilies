package nl.vu.cs.align;

public class Assert {
	public static final boolean DEBUG = true;
	
	static public void assertTrue(boolean b) {
		if (!b) {
			throw new RuntimeException("Assertion failed");
		}
	}
	
	static public void assertTrue(String s, boolean b) {
		if (!b) {
			throw new RuntimeException("Assertion failed: "+s);
		}
	}

	static public void fail(String s) {
		throw new RuntimeException("Assertion failed: "+s);
	}
	
	static public void fail() {
		throw new RuntimeException("Assertion failed");
	}
}
