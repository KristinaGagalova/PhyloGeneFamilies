package nl.vu.cs.align.repeats;

import junit.framework.*;

/**
 * @author radek
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for nl.vu.cs.align.repeats");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(StatToolsTest.class));
		suite.addTest(new TestSuite(EstimateEVDParamsTest.class));
		//$JUnit-END$
		return suite;
	}
}
