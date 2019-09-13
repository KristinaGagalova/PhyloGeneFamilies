package nl.vu.cs.align.matrix;

import junit.framework.*;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for nl.vu.cs.align.matrix");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(MatrixTest.class));
		//$JUnit-END$
		return suite;
	}
}
