package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;
import nl.vu.cs.align.tools.*;

public class LocalAlignAffineTest extends AlignTest {

	// the field has to be initialized in superclass
	protected CompleteAlignAffine ca;

	public LocalAlignAffineTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testAlignAffine() throws Exception {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		assertTrue("initialize complete align in setUp method", ca != null);
		
		AlignAffine alignAffine = ca.getAlignAffineMethod();
												
		SubstitutionTable subst = getTestSubst();
		float GO = -5;
		float GX = -2;
		AlignData data;
		float f;
		
		data = alignAffine.createData("ABA", "AA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f,2*MA+GO));

		data = alignAffine.createData("AA", "ABA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 2*MA+GO));
		
		data = alignAffine.createData("ABBA", "AA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 2*MA+GO+GX));

		data = alignAffine.createData("AA", "ABBA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 2*MA+GO+GX));

		data = alignAffine.createData("ABAAA", "AAABA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 4*MA+2*GO));

		data = alignAffine.createData("AAABA", "ABAAA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 4*MA+2*GO));

		data = alignAffine.createData("ABBBA", "AA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 2*MA+GO+2*GX));

		data = alignAffine.createData("AA", "ABBBA", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 2*MA+GO+2*GX));

		data = alignAffine.createData("CAABBBAAD", "DAAAAC", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 4*MA+GO+2*GX));

		data = alignAffine.createData("DAAAAC", "CAABBBAAD", GO, GX, subst);
		f = ca.align(data);
		assertTrue(Matrix.equalTo(f, 4*MA+GO+2*GX));
	}
	
	public void testCompleteAlignSeqs7() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		
		AlignDataAffine data = ca.getAlignAffineMethod().createData("AA", "ABA", gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals(MA*2+gapo, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
	}

	public void testCompleteAlignSeqs8() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		
		AlignDataAffine data = ca.getAlignAffineMethod().createData("ABA", "AA", gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals(MA*2+gapo, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
	}

	public void testCompleteAlignSeqs9() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		String s1 =                                "AABAABBAABBBAA";
		String s2 = StringToolbox.clearWhitespaces("AA AA  AA   AA");
		
		AlignData data = ca.getAlignAffineMethod().createData(
		s1+s2, 
		s2+s1.replace('B', 'C'), 
		gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals((MA*8+3*gapo+3*gapx)*2, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
		
		// check the trace
		int x = s1.length() + s2.length(); 
		int y = x; 
		int i = 0;
		while (data.getMatrix()[y][x] > 0) {
			int nx = data.getPrevX(x,y);
			int ny = data.getPrevY(x,y);
			x = nx;
			y = ny;
			i++;
		}
		assertEquals("Wrong end of trace", 0, x);
		assertEquals("Wrong end of trace", 0, y);
		int nA = s2.length()*2;
		assertEquals("Wrong number of matches in the trace", nA, i);
	}

	public void testCompleteAlignSeqs10() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		String s1 =                                "CAABAABBAABBBAA";
		String s2 = StringToolbox.clearWhitespaces("DAA AA  AA   AA");
		
		AlignData data = ca.getAlignAffineMethod().createData(
		s1, 
		s2, 
		gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals(MA*8+3*gapo+3*gapx, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
		
		// check the trace
		int x = s1.length(); 
		int y = s2.length(); 
		int i = 0;
		while (data.getMatrix()[y][x] > 0) {
			int nx = data.getPrevX(x,y);
			int ny = data.getPrevY(x,y);
			x = nx;
			y = ny;
			i++;
		}
		int nA = s2.length()-1;
		assertEquals("Wrong number of matches in the trace", nA, i);
	}

	public void testCompleteAlignSeqs11() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		String s1 =                                "CAABAABBAABBBAA";
		String s2 = StringToolbox.clearWhitespaces("DAA AA  AA   AA");
		
		AlignData data = ca.getAlignAffineMethod().createData(
		s1, 
		s2, 
		gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals(MA*8+3*gapo+3*gapx, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
		
		// check the trace
		int x = s1.length(); 
		int y = s2.length(); 
		int i = 0;
		while (data.getMatrix()[y][x] > 0) {
			int nx = data.getPrevX(x,y);
			int ny = data.getPrevY(x,y);
			x = nx;
			y = ny;
			i++;
		}
		int nA = s2.length()-1;
		assertEquals("Wrong number of matches in the trace", nA, i);
	}

	public void testCompleteAlignSeqs11b() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -3;
		final int gapx = -1;
		String s1 = StringToolbox.clearWhitespaces("DAA AA  AA   AA");
		String s2 =                                "CAABAABBAABBBAA";
		
		AlignData data = ca.getAlignAffineMethod().createData(
		s1, 
		s2, 
		gapo, gapx, testSubst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		assertEquals(MA*8+3*gapo+3*gapx, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);

		// check the trace
		int x = s1.length(); 
		int y = s2.length(); 
		int i = 0;
		while (data.getMatrix()[y][x] > 0) {
			int nx = data.getPrevX(x,y);
			int ny = data.getPrevY(x,y);
			x = nx;
			y = ny;
			i++;
		}
		int nA = s1.length()-1;
		assertEquals("Wrong number of matches in the trace", nA, i);
	}

	public void testCompleteAlignSeqs12() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -12;
		final int gapx = -2;
		String s1 = "KLMWAW";
		String s2 = "AW";
		
		AlignData data = ca.getAlignAffineMethod().createData(
			s1, 
			s2, 
			gapo, gapx, blosum62);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		
		Trace t = new Trace();
		data.markMaxRegion(null, t, 0);
		
		assertEquals(4+11, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
	}

	public void testCompleteAlignSeqs13() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -12;
		final int gapx = -2;
		String s1 = "WWWWWW";
		String s2 = "WWWTTWWW";
		
		AlignData data = ca.getAlignAffineMethod().createData(
			s1, 
			s2, 
			gapo, gapx, blosum62);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		
		Trace t = new Trace();
		data.markMaxRegion(null, t, 0);
	}

	public void testCompleteAlignSeqs13b() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -12;
		final int gapx = -2;
		String s1 = "WWWTTWWW";
		String s2 = "WWWWWW";
		
		AlignData data = ca.getAlignAffineMethod().createData(
			s1, 
			s2, 
			gapo, gapx, blosum62);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		ca.getAlignAffineMethod().align(data);
		
		Trace t = new Trace();
		data.markMaxRegion(null, t, 0);
	}

	public void testCompleteAlignSeqs14() {
		if (this.getClass().equals(LocalAlignAffineTest.class)) {
			// no test for this class
			return;
		}
		
		final int gapo = -12;
		final int gapx = -2;
		String _s2= StringToolbox.clearWhitespaces("CEKGFTSLH HLTRH");
		String s1= StringToolbox.clearWhitespaces( "CGAAYNK NWKLQAH");
		s1 = s1+_s2;
		
		AlignData data = ca.getAlignAffineMethod().createData(
			s1, 
			s1, 
			gapo, gapx, blosum62);
		LocalInit.localInit(data);
		SelfLocalFill.selfLocalFill(data);
		ca.getAlignAffineMethod().align(data);
		
		Trace t = new Trace();
		data.markMaxRegion(null, t, 0);
		
		assertEquals(13, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
	}

}
