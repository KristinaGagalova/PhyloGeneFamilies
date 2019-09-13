package nl.vu.cs.align.proteinlibs;

import junit.framework.*;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for nl.vu.cs.align.proteinlibs");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(FastaDbTest.class));
		//$JUnit-END$
		return suite;
	}
}
