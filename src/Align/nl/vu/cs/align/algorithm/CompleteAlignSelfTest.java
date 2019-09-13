package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class CompleteAlignSelfTest extends CompleteAlignSelfAbstractTest {

	public CompleteAlignSelfTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		align = new CompleteAlignSelfAffine();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

/*
	public void testAlignSelf2() {
		final int rept = 20;
		final int gapo = -5;
		final int gapx = -2;
		final char letter = 'A';
		StringBuffer sb = new StringBuffer(100);
		for (int i = 0; i < rept/2; i++) sb.append(letter);
		sb.append("BBB");
		for (int i = rept/2; i < rept; i++) sb.append(letter);
		String seq =  sb.toString();
		AlignData data = new AlignData(seq, seq, gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*rept+gapo+gapx*2);
	}
*/
	public void testAlignAbstract() {
		// !!! fake test
	}
	
	protected AlignData getAlignData(String seqX, String seqY, float gapo, float gapx, SubstitutionTable subst) {
		return new AlignDataAffineGotoh(seqX, seqY, gapo, gapx, subst);
	}

}
