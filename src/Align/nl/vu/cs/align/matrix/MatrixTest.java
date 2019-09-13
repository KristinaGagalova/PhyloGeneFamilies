package nl.vu.cs.align.matrix;

import java.io.*;

import junit.framework.*;
import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.substtable.*;

public class MatrixTest extends TestCase {

	public MatrixTest(String arg0) {
		super(arg0);
	}

	public void testSwapTopLeft() {
		float m[][] = new float[2][2];
		m[0][0] = 1.0f;	m[0][1] = 2.0f;
		m[1][0] = 3.0f;	m[1][1] = 4.0f;
		
		// nothing shoud be swapped
		Matrix.swapTopLeft(m, 1); 
		assertTrue(m[1][1] == 4.0f);
		assertTrue(m[0][1] == 2.0f);
		
		// swap the array
		Matrix.swapTopLeft(m, 0);
		assertTrue(m[0][0] == 4.0f);
		assertTrue(m[1][1] == 1.0f);		
		assertTrue(m[0][1] == 2.0f);
	}
	
	public void testTraces() throws IOException {
		String seq = 
			"MAAKVASTSSEEAEGSLVTEGEMGEKALPVVYKRYICSFADCGAAYNKNWKLQAHLCKHT"+
			"GEKPFPCKEEGCEKGFTSLHHLTRHSLTHTGEKNFTCDSDGCDLRFTTKANMKKHFNRFH"+
			"NIKICVYVCHFENCGKAFKKHNQLKVHQFSHTQQLPYECPHEGCDKRFSLPSRLKRHEKV"+
			"HAGYPCKKDDSCSFVGKTWTLYLKHVAECHQDLAVCDVCNRKFRHKDYLRDHQKTHEKER"+
			"TVYLCPRDGCDRSYTTAFNLRSHIQSFHEEQRPFVCEHAGCGKCFAMKKSLERHSVVHDP"+
			"EKRKLKEKCPRPKRSLASRLTGYIPPKSKEKNASVSGTEKTDSLVKNKPSGTETNGSLVL"+
			"DKLTIQ";
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		final int gap = -4;
		_testTraces(new LocalAlignSimple(), new AlignDataSimple(seq, seq, gap, subst));
		_testTraces(new LocalAlignAffineGotoh(), new AlignDataAffineGotoh(seq, seq, gap, gap, subst));
	}

	public void _testTraces(Align align, AlignData data) throws IOException {
				LocalInit.localInit(data);
				SelfLocalFill.selfLocalFill(data);
				float source_matrix[][] = Matrix.copy(data.getMatrix());
				float traces[][] = Matrix.createEmpty(source_matrix);
		
				// get sequential max regions
				align.align(data);
				// leave the trace
				int [][]trace = new int[2][];
				data.markMaxRegion(traces, trace, 1f);
		//		Matrix.saveImageGray(traces, true, "test1");
				float shadow[][] = Matrix.createEmpty(traces);
				Matrix.fillInWithTrace(shadow, trace, 1f);
		//		Matrix.saveImageGray(shadow, true, "test2");
				assertTrue(Matrix.equalsTo(traces, shadow));
	}
	
	public static void assertEquals(float []exp, float []val) {
		if (!Matrix.equalsTo(exp, val))
			fail("expected: <"+Matrix.toString(exp)+"> was: <"+Matrix.toString(val)+">");		
	}
	
	public void testCreateMovingAverageNoCorrection1() {
		float []m;
		float mAvg[];
		m = new float [] {1, 2, 3};
		mAvg = Matrix.createMovingAverageNoCorrection(m, 3);
		assertEquals(new float[] {2, 2, 2}, mAvg);
		mAvg = Matrix.createMovingAverageNoCorrection(m, 1);
		assertEquals(m, mAvg);
		m = new float [] {1, 2, 3};
		mAvg = Matrix.createMovingAverageNoCorrection(m, 10);
		assertEquals(new float[] {2, 2, 2}, mAvg);
		m = new float [] {2, 4, 6};
		mAvg = Matrix.createMovingAverageNoCorrection(m, 2);
		assertEquals(new float[] {3, 5, 5}, mAvg);
	}
	
	public void testCreateMovingAverageNoCorrection2() {
		float []m;
		float mAvg[];
		m = new float [] {0, 0, 0, 3, 0, 0, 0};
		mAvg = Matrix.createMovingAverageNoCorrection(m, 3);
		assertEquals(new float[] {0, 0, 1, 1, 1, 0, 0}, mAvg);
		m = new float [] {0, 0, 0, 3, 0, 0, 3};
		mAvg = Matrix.createMovingAverageNoCorrection(m, 3);
		assertEquals(new float[] {0, 0, 1, 1, 1, 1, 1}, mAvg);
	}
	
	public void testCreateMovingAverage() {
		float []m;
		float mAvg[];
		// a random sanity checks
		for (int k = 0; k < 100; k++) {
			// create random matrix
			int mLen = (int) (Math.random()*100)+1;
			m = new float[mLen]; 
			int window = (int) ((Math.random()*100) + 1);
			for (int i = 0; i < m.length; i++) {
				m[i] = (float) ( (int) (Math.random()*1000)); 
			}
			// calc avg
			mAvg = Matrix.createMovingAverageNoCorrection(m, window);
			if (mAvg[0] >= 0) {
			} else {
				fail(""+mAvg[0]);
			}
			mAvg = Matrix.createMovingAverage(m, window);
			if (mAvg[0] >= 0) {
			} else {
				fail(""+mAvg[0]);
			}
			// sanity check
			float sumM = Matrix.sum(m);
			float sumMAvg = Matrix.sum(mAvg);
			
			assertEquals(sumM, sumMAvg, 1);
		}
	}
	
	public void testCorrectToSum() {
		float []m = { 1, 2, 3 };
		Matrix.correctAddToSum(m, 9);
		assertEquals(new float[] {2, 3, 4}, m);		
	}
	
	public void testProjectMaxE() {
		float [][]m = { {-5, -6, -7}, {-1, -2, -3} };		
		assertEquals(new float[] {-5, -1}, Matrix.projectMaxE(m, 0, 0, 3, 2));
		assertEquals(new float[] {-2}, Matrix.projectMaxE(m, 1, 1, 2, 1));
	}

	public void testProjectE() {
		float [][]m = { {-5, -6, -7}, {-1, -2, -3} };		
		assertEquals(new float[] {-18, -6}, Matrix.projectE(m, 0, 0, 3, 2));
		assertEquals(new float[] {-5}, Matrix.projectE(m, 1, 1, 2, 1));
	}

	public void testProjectS() {
		float [][]m = { {-5, -6, -7}, {-1, -2, -3} };		
		assertEquals(new float[] {-6, -8, -10}, Matrix.projectS(m, 0, 0, 3, 2));
		assertEquals(new float[] {-8, -10}, Matrix.projectS(m, 1, 0, 2, 2));
	}
	
	public void testProjectTriangleSE() {
		float [][]m = { {-5, -6, -7, 1}, 
						{-1, -2, -3, 2},
						{ 1, -1, -8, 3},
						{-1, -2, -3, 4}};
		assertEquals(new float[] {-6, -5, 1}, Matrix.projectTriangleSE(m));
		assertEquals(new float[] {-6, -5, 1}, Matrix.projectTriangleSE(m, 0, 4));
		assertEquals(new float[] {-6}, Matrix.projectTriangleSE(m, 0, 2));
		assertEquals(new float[] {-3}, Matrix.projectTriangleSE(m, 1, 2));
		assertEquals(new float[] { 3}, Matrix.projectTriangleSE(m, 2, 2));
		assertEquals(new float[] {-9, -7}, Matrix.projectTriangleSE(m, 0, 3));
		assertEquals(new float[] {0, 2}, Matrix.projectTriangleSE(m, 1, 3));
		
		try {
			Matrix.projectTriangleSE(m, 2, 3);
			fail("Should raise an IndexOutOfBoundsException");
		} catch (IndexOutOfBoundsException success) {}
		
		m = new float[50][50];
		Matrix.random(m, -100f, 100f);
		assertEquals(Matrix.projectTriangleSE(m), MatrixTest._projectSE(m));
	}


	public static float[] _projectSE(float[][] m) {
		float[] dproject = new float[m.length-1];
		for (int x = 1; x < m[0].length; x++) {
			for (int i = 0; i+x < m.length; i++) {
				dproject[x-1] += m[i][x+i];
			}
		}
		return dproject;
	}

}
