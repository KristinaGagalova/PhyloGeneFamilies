/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.algorithm;



public class LocalAlignAffineGotohTest extends LocalAlignAffineTest {

	public LocalAlignAffineGotohTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		ca = new CompleteAlignAffine(new LocalInit(), 
								new LocalFill(), 
								new LocalAlignAffineGotoh());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ca = null;
	}



}
