/*
 * Created on Nov 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nl.vu.cs.align.repeats;

import nl.vu.cs.align.Assert;
import junit.framework.TestCase;

/**
 * @author radek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StatToolsTest extends TestCase {

	/**
	 * Constructor for SelfSimilarityTest.
	 * @param arg0
	 */
	public StatToolsTest(String arg0) {
		super(arg0);
	}
	
	public void testAccumulateScores() {
		float []f;
		float []result;
		
		Assert.assertTrue(StatTools.accumulateScores(new float[0]).length == 0);
		
		// single number
		f = new float[] {1};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 1); 
		Assert.assertTrue(result[0] == 1);
		 
		// two numbers
		f = new float[] {1, 2};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 2); 
		Assert.assertTrue(result[0] == 2);		
		Assert.assertTrue(result[1] == 1);
				
		// two numbers
		f = new float[] {2, 2};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 2); 
		Assert.assertTrue(result[0] == 2);		
		Assert.assertTrue(result[1] == 2);
				
		// three numbers
		f = new float[] {1, 2, 2};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 3); 
		Assert.assertTrue(result[0] == 3 && result[1] == 2 && result[2] == 2);		
				
		// three numbers
		f = new float[] {1, 1, 2};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 3); 
		Assert.assertTrue(result[0] == 3 && result[1] == 3 && result[2] == 1);		
				
		// three numbers
		f = new float[] {2, 2, 2};
		result = StatTools.accumulateScores(f); 
		Assert.assertTrue(result.length == 3); 
		Assert.assertTrue(result[0] == 3 && result[1] == 3 && result[2] == 3);		
	}
}
