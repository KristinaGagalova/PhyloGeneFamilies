package nl.vu.cs.align.algorithm;

import java.io.*;
import java.util.Collection;
import java.util.Vector;

import nl.vu.cs.align.matrix.Matrix;
import nl.vu.cs.align.substtable.*;
import junit.framework.*;

public class TraceTest extends TestCase {

	public TraceTest(String arg0) {
		super(arg0);
	}

	public void testMultiplyDirectedTracesNotNull1() {
		int [][]trace1 = { {1}, {2} };
		int [][]trace2 = { {2}, {3} };
		Trace result = Trace.multiplyDirectedTraces(new Trace(trace1, new float[1]), new Trace(trace2, new float[1]), 1);
		assertNotNull(result);
		assertEquals("Trace length not proper", 1, result.length());
		assertEquals("Trace not proper:"+result, 3, result.start[0]);
		assertEquals("Trace not proper:"+result, 1, result.end[0]);
	}

	public void testMultiplyDirectedTracesNull1() {
		int [][]trace1 = { {1}, {2} };
		int [][]trace2 = { {3}, {4} };
		Trace result = Trace.multiplyDirectedTraces(new Trace(trace1, new float[1]), new Trace(trace2, new float[1]), 1);
		assertNull(result);
	}

	public void testMultiplyDirectedTracesNotNull2() {
		int [][]trace1 = { {1}, {2} };
		int [][]trace2 = { {2}, {3} };
		Trace result = Trace.multiplyDirectedTraces(new Trace(trace1, new float[1]), new Trace(trace2, new float[1]), 1);
		assertNotNull(result);
		assertEquals("Trace length not proper", 1, result.length());
		assertEquals("Trace not proper", 3, result.start[0]);
		assertEquals("Trace not proper", 1, result.end[0]);
	}

	public void testMultiplyDirectedTracesSelf() {
		int [][]traceA = {	{1, 2}, 
							{2, 3} };
		Trace trace = new Trace(traceA, new float[2]);
		Trace result = Trace.multiplyDirectedTraces(trace, trace, 1);
		assertNotNull(result);
		Trace goodTrace = new Trace(new int[] {3}, new int[] {1}, new float[1]);
		assertTrue("Trace: "+result+", "+goodTrace, result.equals(goodTrace));
	}

	public void testMultiplyDirectedTraces1() {
		int [][]trace1A = {	{1, 2, 4}, 
								{2, 4, 5} };
		int [][]trace2A = {	{1, 2}, 
								{2, 5} };
		Trace trace1 = new Trace(trace1A, new float[3]);
		Trace trace2 = new Trace(trace2A, new float[2]);
		Trace result = Trace.multiplyDirectedTraces(trace1, trace2, 1);
		assertNotNull(result);
		assertTrue(result.equals(new Trace(
			new int[] {5}, 
			new int[] {1}, 
			new float[1])));
	}

	public void testGapLengthAndNumber() {
		int [][]trace1A = {	{1, 3, 4, 5}, 
								{2, 3, 5, 8} };
		Trace trace1 = new Trace(trace1A, new float[4]);
		assertEquals(4, trace1.gapLength());
		assertEquals(3, trace1.gapNumber());
	}

	public void testValueTransitivity() {
		int [][]trace1A = {	{1, 2, 3, 4}, 
							{2, 3, 4, 5} };
		float[] value1A = {5, 6, 7, 8};
		Trace trace1 = new Trace(trace1A, value1A);
		int [][]trace2A = {	{2, 3, 4, 5}, 
							{2, 3, 4, 5} };
		float[] value2A = {1, 7, 2, 9};
		Trace trace2 = new Trace(trace2A, value2A);

		Trace result = Trace.multiplyDirectedTraces(trace1, trace2, 1);
		float[] valueResult = {1, 6, 2, 8};		
		for (int i = 0; i < result.value.length; i++) {
			assertTrue(result.value[i] == valueResult[i]);
		}
	}

	public void testValueTransitivityScale() {
		int [][]trace1A = {	{1, 2, 3, 4}, 
							{2, 3, 4, 5} };
		float[] value1A = {5, 6, 7, 8};
		Trace trace1 = new Trace(trace1A, value1A);
		int [][]trace2A = {	{2, 3, 4, 5}, 
							{2, 3, 4, 5} };
		float[] value2A = {1, 7, 2, 9};
		Trace trace2 = new Trace(trace2A, value2A);

		Trace result = Trace.multiplyDirectedTraces(trace1, trace2, 2);
		float[] valueResult = {0.5f, 3, 1, 4};		
		for (int i = 0; i < result.value.length; i++) {
			assertTrue(result.value[i] == valueResult[i]);
		}
	}

	public void testConvertToSensitive() throws IOException {
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		
		final int LEN = 1000;
		StringBuffer buffer = new StringBuffer(LEN);
		for (int i = 0; i < LEN; i++) {
			buffer.append(SubstitutionTable.AACID[(int) (Math.random()*SubstitutionTable.AACID.length)]);
		}
		String seq = buffer.toString();
		int [][]trace1A = new int[2][LEN-2];
		float[] value1A = new float[LEN-2];
		int [][]trace2A = new int[2][LEN-2];
		float[] value2A = new float[LEN-2];
		for (int i = 0; i < LEN-2; i++) {
			trace1A[0][i] = i+1;
			trace1A[1][i] = i+2;
			trace2A[0][i] = i+2;
			trace2A[1][i] = i+3;
		}
		Trace trace1 = new Trace(trace1A, value1A);
		trace1.convertToSensitiveNoCorr(1, seq, seq, subst, -8, -2);
		Trace trace2 = new Trace(trace2A, value2A);
		trace2.convertToSensitiveNoCorr(1, seq, seq, subst, -8, -2);

		Trace result = Trace.multiplyDirectedTraces(trace1, trace2, 1);
		for (int i = 0; i < result.value.length; i++) {
			assertTrue(result.value[i] == 
				Math.min(subst.getValue(seq.charAt(i), seq.charAt(i+1)), 
						subst.getValue(seq.charAt(i+1), seq.charAt(i+2))));
		}
	}
	
	public void testExpand1A() {
		int [][]trace1A = {	{1, 3}, 
							{1, 2} };
		Trace trace1 = new Trace(trace1A);
		trace1.expand();
		Assert.assertTrue(trace1.length() == 3);
		Assert.assertTrue(trace1.gapNumber() == 0);
		Assert.assertTrue(trace1.gapLength() == 0);
		Assert.assertTrue(trace1.start[0] == 1);
		Assert.assertTrue(trace1.end[0] == 1);
		Assert.assertTrue(trace1.start[trace1.length()-1] == 3);
		Assert.assertTrue(trace1.end[trace1.length()-1] == 2);
	}

	public void testExpand1() {
		int [][]trace1A = {	{1, 3, 4}, 
							{1, 2, 4} };
		Trace trace1 = new Trace(trace1A);
		trace1.expand();
		
		int [][]trace2A = {	{1, 2, 3, 4, 4}, 
							{1, 2, 2, 3, 4} };
		Trace trace2 = new Trace(trace2A);
		Assert.assertTrue(trace1.equals(trace2));
	}

	public void testExpand1B() {
		int [][]trace1A = {	{1, 4}, 
							{1, 2} };
		Trace trace1 = new Trace(trace1A);
		trace1.expand();
		
		int [][]trace2A = {	{1, 2, 3, 4}, 
							{1, 2, 2, 2} };
		Trace trace2 = new Trace(trace2A);
		Assert.assertTrue(trace1.equals(trace2));
	}

	public void testExpand1C() {
		int [][]trace1A = {	{1, 2}, 
							{1, 4} };
		Trace trace1 = new Trace(trace1A);
		trace1.expand();
		
		int [][]trace2A = {	{1, 2, 2, 2}, 
							{1, 2, 3, 4} };
		Trace trace2 = new Trace(trace2A);
		Assert.assertTrue(trace1.equals(trace2));
	}

	public void testExpand2() {
		int [][]trace1A = {	{1, 2, 3}, 
							{1, 2, 3} };
		Trace trace1 = new Trace(trace1A);
		Trace trace2 = (Trace)trace1.clone();
		trace1.expand();
		Assert.assertTrue(trace1.equals(trace2));
	}

	public void testExpand3() {
		int [][]trace1A = {	{1}, 
							{1} };
		Trace trace1 = new Trace(trace1A);
		Trace trace2 = (Trace)trace1.clone();
		trace1.expand();
		Assert.assertTrue(trace1.equals(trace2));
	}
	
	public void testWrapaToNormal1() {
		int [][]trace1A = {	{1, 1}, 
							{1, 2} };		

		Trace trace1a = new Trace(trace1A);
		assertEquals(2, trace1a.wrapaToNormal(1).size());
		assertEquals(2, trace1a.wrapaToNormal(2).size());
	}

	public void testWrapaToNormal2() {
		int [][]trace1A = {	{1, 2}, 
							{1, 2} };		

		Trace trace1a = new Trace(trace1A);
		assertEquals(1, trace1a.wrapaToNormal(1).size());
		assertEquals(2, trace1a.wrapaToNormal(2).size());
	}

	public void testWrapaToNormal3() {
		int [][]trace1A = {	{1, 2, 3, 1, 2, 3}, 
							{1, 2, 3, 4, 5, 6} };		
		int [][]trace1B = {	{2, 3, 1}, 
							{2, 3, 4} };		
		int [][]trace1C = {	{3, 1, 2}, 
							{3, 4, 5} };		
		int [][]trace1D = {	{3}, 
							{6} };		
		int [][]trace1E = {	{1, 2}, 
							{1, 2} };		

		Trace trace1a = new Trace(trace1A);
		assertEquals(2, trace1a.wrapaToNormal(1).size());
		assertEquals(3, trace1a.wrapaToNormal(2).size());
		assertTrue(trace1a.wrapaToNormal(2).toArray()[1].equals(new Trace(trace1B)));
		Collection wrapped = trace1a.wrapaToNormal(3);
		assertEquals(3, wrapped.size());
		assertTrue(wrapped.toArray()[0].equals(new Trace(trace1E)));
		assertTrue(wrapped.toArray()[1].equals(new Trace(trace1C)));
		assertTrue(wrapped.toArray()[2].equals(new Trace(trace1D)));
	}
	
	public void testMove() {
		int [][]trace1A = {	{1, 11}, 
							{2, 12} };		
		Trace trace1a = new Trace(trace1A);

		int [][]trace1B = {	{3, 13}, 
							{5, 15} };		
		Trace trace1b = new Trace(trace1B);
		
		trace1a.move(2, 3);
		assertTrue(trace1a.equals(trace1b));
	}
	
	public void testGetYGaps() {
		int [][]traceA = {	{1, 2}, 
							{1, 2} };		
		Trace trace = new Trace(traceA);
		Vector v = new Vector();
		v.add(trace);
		int[] res = Trace.getYGaps(2, v);
		assertTrue(Matrix.equalsTo(res, new int[] {0, 0, 0}));
		
		
		traceA = new int[][] {	{1, 2}, 
								{1, 3} };		
		trace = new Trace(traceA);
		v.clear();
		v.add(trace);
		res = Trace.getYGaps(2, v);
		assertTrue(Matrix.equalsTo(res, new int[] {0, 1, 0}));

		traceA = new int[][] {	{1, 2, 3}, 
								{1, 2, 5} };		
		trace = new Trace(traceA);
		v.clear();
		v.add(trace);
		res = Trace.getYGaps(3, v);
		assertTrue(Matrix.equalsTo(res, new int[] {0, 0, 2, 0}));
	}

}
