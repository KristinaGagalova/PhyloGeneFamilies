/*
 * Created on Nov 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nl.vu.cs.align;

import nl.vu.cs.align.repeats.*;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author radek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for nl.vu.cs.align");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(StatToolsTest.class));
		suite.addTest(new TestSuite(SelfSimilarityTest.class));
		//$JUnit-END$
		return suite;
	}
}
