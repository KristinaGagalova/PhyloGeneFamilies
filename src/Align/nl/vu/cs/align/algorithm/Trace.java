package nl.vu.cs.align.algorithm;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class Trace {
	public int start[];
	public int end[];
	public float value[];

	/**
	 * @deprecated No value filled in.
	 */	
	public Trace(int start[], int end[]) {
		Assert.assertTrue(start.length == end.length);
		this.start = start;
		this.end = end;
		this.value = new float[start.length];
	}
	
	/**
	 * @ deprecated No value filled in.
	 */	
	public Trace(int arr[][]) {
		this(arr[0], arr[1]);
	}

	/**
	 * create empty trace to be initiated
	 */
	public Trace() {
	}

	/**
	 * Constructs the trace from given trace array and values
	 */
	public Trace(int start[], int end[], float value[]) {
		init(start, end, value);
	}
	
	/**
	 * Create a partial trace from the long one
	 * 
	 * @param start
	 * @param end
	 * @param value
	 * @param traceStart
	 * @param traceLen
	 */
	public Trace(int[] start, int[] end, float[] value, int traceStart, int traceLen) {
		this.start = new int[traceLen];
		this.end = new int[traceLen];
		this.value = new float[traceLen];
		System.arraycopy(start, traceStart, this.start, 0, traceLen);
		System.arraycopy(end, traceStart, this.end, 0, traceLen);
		System.arraycopy(value, traceStart, this.value, 0, traceLen);
	}

	public void init(int start[], int end[], float value[]) {
		Assert.assertTrue(start.length == end.length);
		this.start = start;
		this.end = end;
		Assert.assertTrue(start.length == value.length);
		this.value = value;
	}
	
	/**
	 * Constructs the trace from given trace array and values
	 */
	public Trace(int arr[][], float value[]) {
		this(arr[0], arr[1], value);
	}
	
	/**
	 * Constructs the trace from given trace array and a matrix.
	 * Values are taken from over the diagonal (top right half of the matrix)
	 */
	public Trace(int start[], int end[], float matrix[][]) {
		if(matrix == null)
			init(start, end);
		else
			init(start, end, matrix);
	}
	
	public void init(int start[], int end[], float matrix[][]) {
		Assert.assertTrue(start.length == end.length);
		this.start = start;
		this.end = end;
		value = new float[start.length];
		for (int i = 0; i < value.length; i++) {
			value[i] = matrix[end[i]][start[i]];
		}
	}
	
	public void init(int start[], int end[]) {
		Assert.assertTrue(start.length == end.length);
		this.start = start;
		this.end = end;
		value = new float[start.length];
	}
	
	public Trace(int arr[][], float matrix[][]) {
		this(arr[0], arr[1], matrix);
	}
	
	/**
	 * @return Number of matches
	 */
	public int length() {
		return start.length;
	}
	
	/**
	 * @return Return the size of the area covered by the trace. Returns smaller from
	 * 	two values (along X and Y axis). See UUP_ECOLI for the reason.
	 */
	public int coveredMin() {
		int last = start.length-1;
		return Math.min(start[last]-start[0]+1, end[last]-end[0]+1);
	}

	/**
	 * Multiplies traces, treated as directed edges trace1.start[i]->trace1.end[i]
	 * 
	 * The order of parameters is important!
	 * 
	 * @param trace1
	 * @param trace2
	 * @param scale The value by which the strength of the signal is divided
	 * @return
	 */	
	public static Trace multiplyDirectedTraces(Trace trace1, Trace trace2, float scale) {
		// there's a quiet assumption that traceX[0][y] < traceX[1][y]
		
		int trace1len = trace1.length();
		int trace2len = trace2.length();
		
		// fast check for intersection
		if (trace1.end[trace1len-1] < trace2.start[0]) return null;
		
		// create the resulting trace
		int []resultStart = new int [Math.min(trace1len, trace2len)];
		int []resultEnd = new int [resultStart.length];
		float []value = new float[resultStart.length];
		int resultIndex = 0;
		
		
		int arrowHead = 0; // index of "arrow head" in trace1
		int arrowTail = 0; // index of "arrow tail" in trace2
		for (;;) {
			while (arrowTail < trace2len && trace1.end[arrowHead] > trace2.start[arrowTail]) {
				arrowTail++;
			}
			if (arrowTail >= trace2len) break;
			while (arrowHead < trace1len 
					&& trace1.end[arrowHead] < trace2.start[arrowTail]) {
				arrowHead++;
			}
			if (arrowHead >= trace1len) break;
			if (trace1.end[arrowHead] == trace2.start[arrowTail]) {
				resultStart[resultIndex] = trace1.start[arrowHead];
				resultEnd[resultIndex] = trace2.end[arrowTail];
				value[resultIndex] = Math.min(trace1.value[arrowHead], trace2.value[arrowTail]) / scale;
				resultIndex++;
				arrowHead++;
				if (arrowHead >= trace1len) break;
			}
		}

		// set the new length for the trace, 
		// correcting the direction of edges, which might have flipped
		if (resultIndex > 0) {
			int []result0 = new int[resultIndex];
			int []result1 = new int[resultIndex];
			float []resultValue = new float[resultIndex];
			
			for (int i = 0; i < resultIndex; i++) {
				// correct the direction of edges
				if (resultEnd[i] <= resultStart[i]) {
					result0[i] = resultStart[i];
					result1[i] = resultEnd[i];
				} else {
					result0[i] = resultEnd[i];
					result1[i] = resultStart[i];
				}
			}
			System.arraycopy(value, 0, resultValue, 0, resultIndex);
			resultStart = result0;
			resultEnd = result1;
			
			return new Trace(resultStart, resultEnd, resultValue);
		}
		
		return null;
		
	}

	public boolean equals(Object t) {
		if (!(t instanceof Trace)) {
			return false;
		}
		Trace trace = (Trace)t;
		if (!Matrix.equalsTo(start, trace.start)) return false;
		if (!Matrix.equalsTo(end, trace.end)) return false;
		if (!Matrix.equalsTo(value, trace.value)) return false;
		
		return true;
	}

	public void paint(float[][] m) {
		for (int i = 0; i < length(); i++) {
			m[end[i]][start[i]] = value[i];
		}
	}

	public void paintMax(float[][] m) {
		for (int i = 0; i < length(); i++) {
			if (m[end[i]][start[i]] < value[i]) {
				m[end[i]][start[i]] = value[i];
			}
		}
	}

	public void paintOne(float[][] m) {
		for (int i = 0; i < length(); i++) {
			m[end[i]][start[i]] = 1.0f;
		}
	}
	
	public void paintValue(float[][] m, float v) {
		for (int i = 0; i < length(); i++) {
			m[end[i]][start[i]] = v;
		}
	}
	
	public void paintAdditivelyOne(float[][] m) {
		for (int i = 0; i < length(); i++) {
			m[end[i]][start[i]] += 1.0f;
		}
	}

	public void paintAdditively(float[][] m) {
		for (int i = 0; i < length(); i++) {
			m[end[i]][start[i]] += value[i];
		}
	}

	/**
	 * @return Reversed trace e.g., if there was an edge 1,2 in t, then 
	 * there is an edge 2,1 in t.reverse()
	 */
	public Trace reverse() {
		return new Trace(end, start, value);
	}

	public void show(String seqStart, String seqEnd, PrintStream p) {
		StringBuffer seqStartOut = new StringBuffer();
		StringBuffer matchOut = new StringBuffer();
		StringBuffer seqEndOut = new StringBuffer();
		int lastStart = start[0];
		int lastEnd = end[0];
		for (int i = 0; i < start.length; i++) {
			// we need some spaces to be inserted
			for (int j = lastStart+1; j < start[i]; j++) {
				seqStartOut.append(seqStart.charAt(j-1));
				matchOut.append(" ");
				seqEndOut.append(" ");
			}
			// we need some spaces to be inserted
			for (int j = lastEnd+1; j < end[i]; j++) {
				seqStartOut.append(" ");
				matchOut.append(" ");
				seqEndOut.append(seqEnd.charAt(j-1));
			}
			// match!
			seqStartOut.append(seqStart.charAt(start[i]-1));
			matchOut.append("|");
			seqEndOut.append(seqEnd.charAt(end[i]-1));
			
			lastStart = start[i];
			lastEnd = end[i];
		}
		p.println(seqStartOut);
		p.println(matchOut);
		p.println(seqEndOut);
	}
	
	static class TraceStrengthComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			Trace t0 = (Trace)arg0;
			Trace t1 = (Trace)arg1;
			float v = t0.value[t0.length()-1] - t1.value[t1.length()-1];
			if (v < 0) return -1;
			else if (v > 0) return 1;
			else return 0;
		}
	}
	
	public static void sort(List traces) {
		Collections.sort((List)traces, new TraceStrengthComparator());
	}

	/**
	 * @deprecated Use carefully! Painting a new trace may overwrite the old one.
	 * Use paintMax if you didn't sort the collection to paint
	 */
	public static void paint(float[][] m, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paint(m);
		}
	}

	public static void paintMax(float[][] m, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paintMax(m);
		}
	}

	public static void paintOne(float[][] m, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paintOne(m);
		}
	}

	public static void paintAdditively(float[][] m, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paintAdditively(m);
		}
	}

	public static void paintAdditivelyOne(float[][] m, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paintAdditivelyOne(m);
		}
	}

	public static void paintValue(float[][] m, Collection traces, int v) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.paintValue(m, v);
		}
	}

	/**
	 * Multiply traces
	 */
	public static Collection multiplyTraces(Collection traces) {
		Vector transitiveTraces = new Vector(4*traces.size()*traces.size());
		Object[] t = traces.toArray();
		for (int t1 = 0; t1 < t.length; t1++) {
			for (int t2 = t1; t2 < t.length; t2++) {
				// multiply both values
				// !!! in fact, maybe each trace should get half of its strength,
				// because there are 2 resulting traces from multiplying two traces...
				// Or e.g. avg may be taken.
				// there are 4 traces resulting from multiplying 2 traces
				Trace result;
				Trace trace1 = (Trace)t[t1];
				Trace trace2 = (Trace)t[t2];
				result = Trace.multiplyDirectedTraces(trace1, trace2, 4);
				if (result != null) transitiveTraces.add(result);
				result = Trace.multiplyDirectedTraces(trace1, trace2.reverse(), 4);
				if (result != null) transitiveTraces.add(result);
				result = Trace.multiplyDirectedTraces(trace1.reverse(), trace2, 4);
				if (result != null) transitiveTraces.add(result);
				result = Trace.multiplyDirectedTraces(trace1.reverse(), trace2.reverse(), 4);
				if (result != null) transitiveTraces.add(result);
			}
		}
		return transitiveTraces;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < start.length; i++) {
			sb.append(start[i]+"\t"+end[i]+"\n");
//			sb.append("("+start[i]+"->"+end[i]+" ["+value[i]+"]) ");
		}
		return sb.toString();
	}

	public String toString(String seqx, String seqy) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < start.length; i++) {
			sb.append(start[i]+" ("+seqx.charAt(start[i]-1)+")"+"\t"+end[i]+" ("+seqy.charAt(end[i]-1)+")"+"\n");
//			sb.append("("+start[i]+"->"+end[i]+" ["+value[i]+"]) ");
		}
		return sb.toString();
	}

	private static int gapLength(int []t) {
		int len = 0;
		for (int i = 1; i < t.length; i++) {
			int gapLen = (t[i] - 1) - t[i-1];
			len += gapLen > 0 ? gapLen : 0;
		}
		return len;
	}
	
	/**
	 * @return The sum of gap in the trace
	 */
	public int gapLength() {
		return gapLength(start)+gapLength(end);
	}

	private static int gapNumber(int []t) {
		int len = 0;
		for (int i = 1; i < t.length; i++) {
			len += (t[i] - 1) - t[i-1] <= 0 ? 0 : 1;
		}
		return len;
	}
	
	/**
	 * @return The number of gaps
	 */
	public int gapNumber() {
		return gapNumber(start)+gapNumber(end);
	}

	/**
	 * @param f
	 */
	public void setValue(float f) {
		for (int i = 0; i < value.length; i++) {
			value[i] = f;
		}		
	}
	
	/**
	 * @param f
	 */
	public void setValueArray(float[] value) {
		Assert.assertTrue(value.length == start.length);
		this.value = value;
	}
	
	/**
	 * Recalculates the weights of trace taking aa similarity into account.
	 * Then all values < 0 zeroed.
	 * 
	 * Warning! discards the previous edge weights!
	 * 
	 * @param bestTraces
	 * @return
	 */
	public void convertToSensitive(int smooth, String seq1, String seq2, SubstitutionTable subst, float gapo, float gapx) {
		convertToSensitiveNoCorr(smooth, seq1, seq2, subst, gapo, gapx);
		for (int j = 0; j < value.length; j++) {
			if (value[j] < 0) value[j] = 0;
		}
	}
	
	/**
	 * Increases the strength of each cell by <code>addValue</code>.
	 * @param addValue
	 */
	public void addValue(float addValue) {
		for (int i = 0; i < value.length; i++) {
			value[i] += addValue;
		}
	}
	
	/**
	 * Recalculates the weights of trace taking aa similarity into account.
	 * 
	 * Warning! discards the previous edge weights!
	 * 
	 * @param bestTraces
	 * @return
	 */
	public void convertToSensitiveNoCorr(int smooth, String seq1, String seq2, SubstitutionTable subst, float gapo, float gapx) {
		if (value == null) value = new float[start.length];
		float sumV = 0;
		for (int j = 0; j < value.length; j++) {
			float v = subst.getValue(seq1.charAt(start[j]-1), seq2.charAt(end[j]-1)); 
			value[j] = v;
			sumV +=v;
		}
		value = Matrix.createMovingAverage(value, smooth);
		Matrix.correctAddToSum(value, this.score(seq1, seq2, subst, gapo, gapx));
	}
	
	public float score(String seq1, String seq2, SubstitutionTable subst, float gapo, float gapx) {
		Assert.assertTrue(gapo <= 0 && gapx <= 0);
		Assert.assertTrue(gapo <= gapx);
		float sumV = 0;
		for (int j = 0; j < value.length; j++) {
			float v = subst.getValue(seq1.charAt(start[j]-1), seq2.charAt(end[j]-1)); 
			sumV +=v;
		}
		sumV += (gapo-gapx)*this.gapNumber() + gapx*this.gapLength();
		return sumV;  
	}
	
	public float score(ComparableSequence cmpSeqX, ComparableSequence cmpSeqY, SubstitutionTable subst, float gapo, float gapx) {
		Assert.assertTrue(gapo <= 0 && gapx <= 0);
		Assert.assertTrue(gapo <= gapx);
		float sumV = 0;
		for (int j = 0; j < value.length; j++) {
			float v = cmpSeqX.score(start[j]-1, cmpSeqY, end[j]-1, subst); 
			sumV +=v;
		}
		sumV += (gapo-gapx)*this.gapNumber() + gapx*this.gapLength();
		return sumV;  
	}
	
	public static float score(ComparableSequence cmpSeqX, ComparableSequence cmpSeqY, SubstitutionTable subst, float gapo, float gapx, Collection c) {
		float scoreSum = 0;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			scoreSum += t.score(cmpSeqX, cmpSeqY, subst, gapo, gapx);
		}
		return scoreSum;
	}
	/**
	 * Normalized for the trace length
	 * 
	 * @param seq1
	 * @param seq2
	 * @param subst
	 * @param gapo
	 * @param gapx
	 * @return
	 */
	public float normalScore(String seq1, String seq2, SubstitutionTable subst, float gapo, float gapx) {
		return score(seq1, seq2, subst, gapo, gapx) / this.length();
	}
	
	public Object clone() {
		int newStart[] = new int[start.length];
		System.arraycopy(start, 0, newStart, 0, start.length);
		int newEnd[] = new int[end.length];
		System.arraycopy(end, 0, newEnd, 0, end.length);
		float newValue[] = new float[value.length];
		System.arraycopy(value, 0, newValue, 0, value.length);		
		return new Trace(newStart, newEnd, newValue);
	}

	/**
	 * Calculate median distance to diagonal
	 * 
	 * PROBLEM Should be weighted with either transitive matrix (beter) or scoring (worse)
	 * otherwise e.g. for tripet repeats the distance from diagonal might
	 * not be a multiplication of 3!
	 * 
	 * PROBLEM The calculation of the distance should be correlated with
	 * the calculation of the length of the trace (otherwise: see UUP_ECOLI).
	 * Otherwise tweaking of the profile may be needed.
	 */
	public int medianDistanceToDiagonal(AlignData data) {
		int len = this.length();
		int distance = 0;
		SubstitutionTable subst = data.getSubst();
		String seqX = data.getSeqX();
		String seqY = data.getSeqY();
		float score = this.score(seqX, seqY, subst, 0f, 0f);
		float sumScore = 0;
		for (int i = 0; i < len; i++) {
			sumScore += subst.getValue(seqX.charAt(start[i]-1), seqY.charAt(end[i]-1));
			Assert.assertTrue(start[i]-end[i] > 0);
			distance = start[i]-end[i];
			if (sumScore >= score/2) break;
		}
		return distance;
	}

	/**
	 * Include gaps as matching regions in the trace
	 */
	public void expand() {
		int gapLen = gapLength();
		Assert.assertTrue(gapLen >= 0);
		if (gapLen > 0) {
			int oldLen = length();
			int newLen =  oldLen + gapLen;
			int []newStart = new int[newLen];
			int []newEnd = new int[newLen];
			float []newValue = new float[newLen];
			
			for (int oldi = 0, newi = 0; oldi < oldLen-1; oldi++) {
				newStart[newi] = start[oldi]; 
				newEnd[newi] = end[oldi]; 
				newi++;
				// fill in gaps				
				int step;
				step = start[oldi+1] - start[oldi];
				for (int j = 1, inc = 0; j < step; j++) {
					newStart[newi] = start[oldi]+j; 
					newEnd[newi] = end[oldi]+1; 
					newi++;
				}
				step = end[oldi+1] - end[oldi];
				for (int j = 1; j < step; j++) {
					newStart[newi] = start[oldi]+1; 
					newEnd[newi] = end[oldi]+j; 
					newi++;
				}
			}
			newStart[newLen-1] = start[oldLen-1];
			newEnd[newLen-1] = end[oldLen-1];
			
			start = newStart;
			end = newEnd;
			value = newValue;
		}
	}

	/**
	 * Converts wrap-around trace into a collection of "normal" traces
	 * for which start[i] < start[i+1]
	 * @return
	 */
	public Collection wrapaToNormal(int breakingPoint) {
		Assert.assertTrue(breakingPoint > 0); // this concerns traces, should start from 1
		Collection result = new Vector();
		int len = start.length;
		
		int traceStart = 0;
		int i = 0;
		while (i < len) {
			// break when crossing the breakingPoint, i+1 start a new trace
			if (i == len-1
					// normal case of breaking at i (the new trace start at >=breakingPoint)
					|| (start[i] < breakingPoint && start[i+1] >= breakingPoint)
					// analyze the breaking point when wrapping around 
					|| (start[i+1] <= start[i] 
						&& (start[i] < breakingPoint || start[i+1] >= breakingPoint))
					) {
				Trace t = new Trace(start, end, value, traceStart, (i-traceStart)+1); 
				Assert.assertTrue(value[traceStart] == t.value[0]);
				result.add(t);
				traceStart = i+1;
			}
			i++;
		}
		if (Assert.DEBUG && breakingPoint == 1) wrapaToNormalCheck(result);
		return result;
	}
	
	public Collection wrapaToNormal() {
		Collection result = wrapaToNormal(1);
		
		wrapaToNormalCheck(result);
		return result;

	}

	private void wrapaToNormalCheck(Collection result) {
		if (Assert.DEBUG) {
			String MSG = "Converting trace failed";
			// check whether the generated collection is the same as the original
			int pos = 0;
			for (Iterator iter = result.iterator(); iter.hasNext();) {
				Trace t = (Trace) iter.next();
				for (int j = 0; j < t.length(); j++) {
					if (start[pos] != t.start[j] 
							|| end[pos] != t.end[j] 
							|| value[pos] != t.value[j])
						Assert.fail(MSG);
					pos++;
				}
			}
			if (pos < length()-1) Assert.fail(MSG);
		}
	}

	public static void move(int dx, int dy, Collection traces) {
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.move(dx, dy);
		}
	}

	/**
	 * @param dx
	 * @param dy
	 */
	public void move(int dx, int dy) {
		int len = length();
		for (int i = 0; i < len; i++) {
			start[i] += dx;
			end[i] += dy;
		}
	}

	public static Collection wrapaToNormal(int start, Collection repeats) {
		Collection result = new Vector();
		for (Iterator iter = repeats.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			result.addAll(t.wrapaToNormal(start));
		}
		return result;
	}

	/**
	 * 
	 * @param profileLength
	 * @param repeats 1-based traces
	 * @return 0-based list of gaps before the position
	 */
	public static int[] getYGaps(int profileLength, Collection repeats) {
		int []profGaps = new int[profileLength+1]; // number of gaps *before* the position
		for (Iterator iter = repeats.iterator(); iter.hasNext(); ) {
			Trace t = (Trace) iter.next();
			for (int i = 0; i < t.length()-1; i++) {
				// for two consequtive matches in the trace,
				// check for gap in the sequence
				int gapSize = (t.end[i+1]-t.end[i])-1;
				if (gapSize > 0) {
					// introduce a gap in the profile
					int profPos = t.start[i+1]-1;
					profGaps[profPos] = Math.max(profGaps[profPos], gapSize);
				}
			}
		}
		
		return profGaps;
	}

	/**
	 * @param seq
	 * @param repeatsFound
	 */
	public static String maskSequence(String seq, Collection traces) {
		StringBuffer sb = new StringBuffer(seq);
		for (Iterator iter = traces.iterator(); iter.hasNext();) {
			Trace t = (Trace) iter.next();
			t.maskSequence(sb);
		}
		return sb.toString();
	}

	/**
	 * @param sb
	 */
	private void maskSequence(StringBuffer sb) {
		for (int y = 0; y < end.length; y++) {
			sb.setCharAt(end[y]-1, 'x');
		}
	}
}
