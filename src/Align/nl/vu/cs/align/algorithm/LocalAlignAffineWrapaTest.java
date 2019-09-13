/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.algorithm;

import java.io.IOException;

import nl.vu.cs.align.matrix.AlignData;
import nl.vu.cs.align.matrix.AlignDataAffineGotoh;
import nl.vu.cs.align.matrix.Matrix;
import nl.vu.cs.align.substtable.SubstitutionTable;



public class LocalAlignAffineWrapaTest extends LocalAlignAffineTest {

	public LocalAlignAffineWrapaTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		ca = new CompleteAlignAffine(new LocalInit(), 
								new LocalFill(), 
								new LocalAlignAffineWrapa());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ca = null;
	}


	public void testLocalAlignWrapa1() throws IOException {
		final int NUM = 2;
		_testLocalAlignWrap1(NUM);
	}

	public void testLocalAlignWrapa1b() throws IOException {
		final int NUM = 20;
		_testLocalAlignWrap1(NUM);
	}

	private void _testLocalAlignWrap1(final int NUM) throws IOException {
		assertTrue("initialize complete align in setUp method", ca != null);
		
		AlignAffine alignAffine = ca.getAlignAffineMethod();
												
		SubstitutionTable subst = getTestSubst();
		float GO = -5;
		float GX = -2;
		StringBuffer sb;
		AlignData data;
		float f;
		Trace t;
		
		data = alignAffine.createData("A", "A", GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(MA, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(1, t.length());
				
		data = alignAffine.createData("A", "AA", GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*MA, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2, t.length());
		
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("A");
		data = alignAffine.createData("A", sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(NUM*MA, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(NUM, t.length());
		
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(NUM*(MA+MB+MC), f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(NUM*3, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", "BC"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(NUM*(MA+MB+MC)+MB+MC, f, Matrix.FLOAT_PRECISION);		
		assertEquals(NUM*3+2, t.length());
		
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"AC"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MA+MC+GO, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+2, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"BC"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MB+MC+GO, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+2, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"AB"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MA+MB+GO, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+2, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("A");
		data = alignAffine.createData("A", sb.toString()+"B"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*(MA)+GO, f, Matrix.FLOAT_PRECISION);		
		assertEquals(2*(NUM), t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("AB");
		data = alignAffine.createData("AB", sb.toString()+"C"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*(NUM*2), t.length());
		assertEquals(2*NUM*(MA+MB)+GO, f, Matrix.FLOAT_PRECISION);		
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"A"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*(MA+MB+MC)+MA+GO+GX, f, Matrix.FLOAT_PRECISION);		
		assertEquals(2*(NUM*3)+1, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"AB"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MA+MB+GO, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+2, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABCD");
		data = alignAffine.createData("ABCD", sb.toString()+"A"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*(NUM*4)+1, t.length());
		assertEquals(2*NUM*(MA+MB+MC+MD)+MA+GO+2*GX, f, Matrix.FLOAT_PRECISION);		
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"AB"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*(NUM*3)+2, t.length());
		assertEquals(2*NUM*(MA+MB+MC)+MA+MB+GO, f, Matrix.FLOAT_PRECISION);		
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"B"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MB+2*GO, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+1, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"C"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+MC+GO+GX, f, Matrix.FLOAT_PRECISION);		
		assertEquals(2*NUM*3+1, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"DDD"+sb.toString(), -100, -100, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+3*MM, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3+3, t.length());
		
		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+"DDD"+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC)+GO+2*GX, f, Matrix.FLOAT_PRECISION);		
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3, t.length());

	}
	
	/* extracting traces */
	public void testLocalAlignWrapa2() throws IOException {
		assertTrue("initialize complete align in setUp method", ca != null);
		
		AlignAffine alignAffine = ca.getAlignAffineMethod();
												
		SubstitutionTable subst = getTestSubst();
		float GO = -5;
		float GX = -2;
		final int NUM = 20;
		StringBuffer sb;
		AlignData data;
		float f;

		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(NUM*(MA+MB+MC), f, Matrix.FLOAT_PRECISION);		

		Trace t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(NUM*3, t.length());

		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("ABC");
		data = alignAffine.createData("ABC", sb.toString()+sb.toString(), GO, GX, subst);
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		assertEquals(2*NUM*(MA+MB+MC), f, Matrix.FLOAT_PRECISION);		

		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*3, t.length());
	}

	public void testLocalAlignWrapa3() throws IOException {
		assertTrue("initialize complete align in setUp method", ca != null);
		
		AlignAffine alignAffine = ca.getAlignAffineMethod();
												
		SubstitutionTable subst = getTestSubst();
		float GO = -5;
		float GX = -2;
		final int NUM = 1;
		StringBuffer sb;
		AlignData data;
		float f;
		Trace t;

		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("A");
		data = alignAffine.createData("A", sb.toString()+"B"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*NUM*(MA)+GO, f, Matrix.FLOAT_PRECISION);		
		assertEquals(2*(NUM), t.length());

		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("AB");
		data = alignAffine.createData("AB", sb.toString()+"A"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*(NUM*2)+1, t.length());
		assertEquals(2*NUM*(MA+MB)+MA+GO, f, Matrix.FLOAT_PRECISION);		
	}
		
	public void testLocalAlignWrapa4() throws IOException {
		assertTrue("initialize complete align in setUp method", ca != null);
		
		AlignAffine alignAffine = ca.getAlignAffineMethod();
												
		SubstitutionTable subst = getTestSubst();
		float GO = -5;
		float GX = -2;
		final int NUM = 1;
		StringBuffer sb;
		AlignData data;
		float f;
		Trace t;

		// test the gap through
		sb = new StringBuffer();
		for (int i = 0; i <NUM;i++) sb.append("AB");
		data = alignAffine.createData("AB", sb.toString()+"C"+sb.toString(), GO, GX, subst); // !!!check against ABC
		f = LocalAlignAffineWrapa.localAlignAffine((AlignDataAffineGotoh)data, true);
		t = new Trace();
		data.markMaxRegion(null, t, 0);
		assertEquals(2*(NUM*2), t.length());
		assertEquals(2*NUM*(MA+MB)+GO, f, Matrix.FLOAT_PRECISION);		
	}
		
}
