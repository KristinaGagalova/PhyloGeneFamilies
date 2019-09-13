/*
 * Created on Dec 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nl.vu.cs.align;

import junit.framework.*;
import nl.vu.cs.align.algorithm.*;

/**
 * @author radek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SelfSimilarityTest extends TestCase {

	/**
	 * Constructor for SelfSimilarityTest.
	 * @param arg0
	 */
	public SelfSimilarityTest(String arg0) {
		super(arg0);
	}

	public void testCreateAlignment0() {
		int tA[][] = new int[][] { 	{1, 2},
									{1, 2} };
		Trace t0 = new Trace(tA);
		StringBuffer gapsAlign = new StringBuffer("-------------------");
		String res;
		
		t0 = new Trace(tA);
		int profGaps[] = new int[] {0, 0, 0, 0};
		res = SelfSimilarity.createAlignment0(t0, "0123456789", 1, 3, profGaps, gapsAlign).toString();
		assertEquals("12-", res);

		res = SelfSimilarity.createAlignment0(t0, "0123456789", 0, 3, profGaps, gapsAlign).toString();
		assertEquals("-12", res);
	}

}
