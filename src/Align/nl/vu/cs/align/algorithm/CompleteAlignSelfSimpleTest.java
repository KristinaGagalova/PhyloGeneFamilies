package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class CompleteAlignSelfSimpleTest extends CompleteAlignSelfAbstractTest {

	public CompleteAlignSelfSimpleTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		align = new CompleteAlignSelfSimple();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testAlignAbstract() {
		// !!! fake test
	}
	
	protected AlignData getAlignData(String seqX, String seqY, float gapo, float gapx, SubstitutionTable subst) {
		assertEquals("Different gap penalties", gapo, gapx, Matrix.FLOAT_PRECISION);
		return new AlignDataSimple(seqX, seqY, gapo, subst);
	}

}
