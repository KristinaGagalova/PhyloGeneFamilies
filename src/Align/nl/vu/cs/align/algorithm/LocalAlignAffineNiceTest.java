/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.algorithm;

import java.io.*;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;


public class LocalAlignAffineNiceTest extends LocalAlignAffineTest {

	public LocalAlignAffineNiceTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		ca = new CompleteAlignAffine(new LocalInit(), 
								new LocalFill(), 
								new LocalAlignAffineNice());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		ca = null;
	}

	public void testWE1() throws IOException {
		LocalAlignAffineNice align = new LocalAlignAffineNice();
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		AlignDataAffine data = align.createData("AAA", "AAA", -100, -100, subst);
		LocalInit.localInit(data);
		LocalFill.localFill(data);
		Trace lastTrace = null;
		float MA = subst.getValue('A', 'A');
		float results[] = new float[] {3*MA, 2*MA, 2*MA, MA, MA}; 
		for (int i = 0; i < 5; i++) {
			float val = align.align(data, lastTrace);
			assertEquals(results[i], val, Matrix.FLOAT_PRECISION);
			lastTrace = data.getMaxTrace();
			lastTrace.setValue(-Matrix.INF);
			lastTrace.paint(data.getMatrix());
		}
		assertTrue(align.align(data, lastTrace) <= 0);
	}

	public void testWE2() throws IOException {
		LocalAlignAffineNice align = new LocalAlignAffineNice();
		String seq = "HLGVKVFSVAITPDHLEPRLSIIATDHTYRRNFTAADWGQSRDAEEAISQTIDTIVDMIK";
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		AlignDataAffine data = align.createData(seq, seq, -8, -2, subst);
		LocalInit.localInit(data);
		SelfLocalFill.selfLocalFill(data);
		Trace lastTrace = null;
		for (int i = 0; i < 5; i++) {
			float val = align.align(data, lastTrace);
			lastTrace = data.getMaxTrace();
			lastTrace.setValue(-Matrix.INF);
			lastTrace.paint(data.getMatrix());
		}
	}
	
	public void testWE3() throws IOException {
		final int NUM = 5;
		LocalAlignAffineNice align = new LocalAlignAffineNice();
		String seq = "CVR";
		float scoreSeq = 18;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < NUM; i++) {
			sb.append(seq);
		}
		seq = sb.toString();
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		AlignDataAffine data = align.createData(seq, seq, -8, -2, subst);
		LocalInit.localInit(data);
		SelfLocalFill.selfLocalFill(data);
		Trace lastTrace = null;
		for (int i = 0; i < NUM-1; i++) {
			float val = align.align(data, lastTrace);
			assertEquals(scoreSeq*((NUM-1)-i), val, Matrix.FLOAT_PRECISION);
			lastTrace = data.getMaxTrace();
			lastTrace.setValue(-Matrix.INF);
			lastTrace.paint(data.getMatrix());
		}
		assertTrue(align.align(data, lastTrace) <= scoreSeq);
	}

	public void testWE4() throws IOException {
		String seq = "MFLKDLGCGTNTCQNLPFDVTPMFLKDLGCGTNTCQNLPFDVTPMFLKDLGCGTNTCQNL"+
					"PFDVTPMFLKDLGCGTNTCQNLPFDVTPMFLKDLGCGTNTCQNLPFDVTPMFLKDLGCGT"+
					"NTCQNLPFDVTPMFLKDLGCGTNTCQNLPFDVTP";
		_testTillDrop(seq, seq, new SelfLocalFill(), Integer.MAX_VALUE);
	}

	public void testWE5() throws IOException {
		LocalAlignAffineNice align = new LocalAlignAffineNice();

		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		String seq1 = 
			"MFLKDLGCG"+
			"DVTP"+
			"MFLKDLGCG"+
			"MFLKDLGCG"+
			"MFLKDLGCG";
		String seq2 = 
			"MFLKDLGCG"+
			"MFLKDLGCG"+
			"MFLKDLGCG"+
			"DVTP"+
			"MFLKDLGCG";
		_testTillDrop(seq1, seq2, new LocalFill(), Integer.MAX_VALUE);
	}
	
	public void testWE6() throws IOException {
		String seq = 
		"MxxxxxxxxxxxTQPWRSLGAEMTTFSQKILANACTLVMCSPLESGLPGHDGQDGRECPH"+
		"GEKxxxxxxxxxxxxxxxxxxxxIGPKGDNGFVGEPGPKGDTxxxxxxxxxxxxxxxxxS"+
		"GKQxxxxxxxxxxxxxxxxxxxxxxxxxxxxFPGPSGLKGEKGAPGETGAPGRAGVTGPS"+
		"GAIGPQGPSGARGPPGLKGDRGDPGETGAKGESGLAEVNALKQRVTILDGHLRRFQNAFS"+
		"QYKKAVLFPDGQAVGEKIFKTAGAVKSYSDAEQLCREAKGQLASPRSSAENEAVTQMVRA"+
		"QEKNAYLSMNDISTEGRFTYPTGEILVYSNWADGEPNNSDEGQPENCVEIFPDGKWNDVP"+
		"CSKQLLVICEF";
		_testTillDrop(seq, seq, new SelfLocalFill(), 100);
	}


	public void _testTillDrop(String seq1, String seq2, MatrixFill mf, int numIter) throws IOException {
		LocalAlignAffineNice align = new LocalAlignAffineNice();

		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		AlignDataAffine data = align.createData(seq1, seq2, -8, -2, subst);
		LocalInit.localInit(data);
		mf.fill(data);
		Trace lastTrace = null;
		int debugInt = 0;
		for (int i = 0; i < numIter; i++) {
			float val = align.align(data, lastTrace);
			if (val == 0) break;
			lastTrace = data.getMaxTrace();
			lastTrace.setValue(-Matrix.INF);
			lastTrace.paint(data.getMatrix());
		}
	}
/*	
	public void testWrapAround() throws IOException {
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		final int NUM = 100;
		LocalAlignAffineNice align = new LocalAlignAffineNice();
		String letter = "A";
		//float scoreSeq = 18;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < NUM; i++) {
			sb.append(letter);
		}
		String seq = sb.toString();
		AlignDataAffine data = align.createData("A", seq, -8, -2, subst);
		LocalInit.localInit(data);
		Trace lastTrace = null;
		float val = align.align(data, lastTrace);
		assertEquals(subst.getValue('A', 'A')*NUM, val, Matrix.FLOAT_PRECISION);
		lastTrace = data.getMaxTrace();
		lastTrace.setValue(-Matrix.INF);
		lastTrace.paint(data.getMatrix());
		assertTrue(align.align(data, lastTrace) == 0);
	}
	*/
}
