package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;

public class CompleteAlignAffineGotohTest extends CompleteAlignTest {

	/**
	 * Constructor for CompleteAlignAffineGotohTest.
	 * @param arg0
	 */
	public CompleteAlignAffineGotohTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		align = new CompleteAlignAffineGotoh();
	}

	public void testCompleteAlignSeqs1() {
		final int gapo = -2;
		final int gapx = -1;
		
		AlignDataAffineGotoh data = new AlignDataAffineGotoh("ABA", "AA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*2+gapo);
		assertEquals(data.getPrevX(3,2), 1);
		assertEquals(data.getPrevY(3,2), 1);
	}

	public void testCompleteAlignSeqs1b() {
		final int gapo = -2;
		final int gapx = -1;
		
		AlignDataAffineGotoh data = new AlignDataAffineGotoh("AA", "ABA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*2+gapo);
		assertEquals(data.getPrevX(2,3), 1);
		assertEquals(data.getPrevY(2,3), 1);
	}

	public void testCompleteAlignSeqs2() {
		final int gapo = -2;
		final int gapx = -1;
		
		AlignDataAffineGotoh data = new AlignDataAffineGotoh("ABBA", "AA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*2+gapo+gapx);
		assertEquals(data.getPrevX(4,2), 1);
		assertEquals(data.getPrevY(4,2), 1);
	}

	public void testCompleteAlignSeqs2b() {
		final int gapo = -2;
		final int gapx = -1;
		
		AlignDataAffineGotoh data = new AlignDataAffineGotoh("AA", "ABBA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*2+gapo+gapx);
		assertEquals(data.getPrevX(2,4), 1);
		assertEquals(data.getPrevY(2,4), 1);
	}

	public void testCompleteAlignSeqs3() {
		final int gapo = -3;
		final int gapx = -1;
		
		AlignData data = new AlignDataAffineGotoh("ABBBAA", "AAA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*3+gapo+2*gapx);
	}

	public void testCompleteAlignSeqs4() {
		final int gapo = -3;
		final int gapx = -1;
		
		AlignData data = new AlignDataAffineGotoh("AAABBBA", "AAAA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*4+gapo+2*gapx);
	}

	public void testCompleteAlignSeqs5() {
		final int gapo = -3;
		final int gapx = -1;
		
		AlignData data = new AlignDataAffineGotoh("AAAA", "AAABBBA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*4+gapo+2*gapx);
	}

	public void testCompleteAlignSeqs6() {
		final int gapo = -20;
		final int gapx = -20;
		
		AlignData data = new AlignDataAffineGotoh("AAA", "ABA", gapo, gapx, testSubst);
		_testForMaxValue(align, data, MA*2+MM*1);
	}

	public void testAlignAbstract() {
		// !!! fake test
	}
}
