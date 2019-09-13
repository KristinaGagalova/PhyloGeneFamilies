package nl.vu.cs.align.algorithm;

import junit.framework.*;

public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for nl.vu.cs.align.algorithm");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TraceTest.class));
		suite.addTest(new TestSuite(AlignTest.class));
		suite.addTest(new TestSuite(CompleteAlignSelfSimpleTest.class));
		suite.addTest(new TestSuite(CompleteAlignSelfTest.class));
		suite.addTest(new TestSuite(CompleteAlignTest.class));
		suite.addTest(new TestSuite(LocalAlignSimpleTest.class));
		suite.addTest(new TestSuite(LocalAlignAffineTest.class));
		suite.addTest(new TestSuite(LocalAlignAffineGotohTest.class));
		suite.addTest(new TestSuite(LocalAlignAffineWrapaTest.class));
		suite.addTest(new TestSuite(LocalAlignAffineNiceTest.class));
		suite.addTest(new TestSuite(AlignDDPMinTest.class));
		suite.addTest(new TestSuite(AlignDDPTest.class));
		suite.addTest(new TestSuite(CompleteAlignAffineGotohTest.class));
		suite.addTest(new TestSuite(CompleteAlignSimpleTest.class));
		//$JUnit-END$
		return suite;
	}
}
