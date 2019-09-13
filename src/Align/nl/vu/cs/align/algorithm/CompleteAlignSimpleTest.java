package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.tools.*;

public class CompleteAlignSimpleTest extends CompleteAlignTest {

	public CompleteAlignSimpleTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		align = new CompleteAlignSimple();
	}

	public void testCompleteAlignSeqs9() {
		final int gapx = -1;
		String s1 =                                "AABAABBAABBBAA";
		String s2 = StringToolbox.clearWhitespaces("AA AA  AA   AA");
		
		AlignData data = new AlignDataSimple(
		s1+s2, 
		s2+s1, 
		gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		(new LocalAlignSimple()).align(data);
		assertEquals((MA*8+6*gapx)*2, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
	}

	public void testAlignAbstract() {
		// !!! fake test
	}
}
