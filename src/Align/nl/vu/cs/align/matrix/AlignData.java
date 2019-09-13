package nl.vu.cs.align.matrix;

import nl.vu.cs.align.*;
import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.substtable.*;

public abstract class AlignData {
	
	protected float gapO;
	
	protected float gapE;
	
	protected float matrix[][];
	
	String seqX, seqY;
	
	ComparableSequence cmpSeqX, cmpSeqY;
	
	SubstitutionTable subst;

	public AlignData(String seqX, String seqY, float gapO, float gapE, SubstitutionTable subst) {
		this.seqX = seqX;
		this.seqY = seqY;
		this.cmpSeqX = new Sequence(seqX);
		this.cmpSeqY = new Sequence(seqY);
		this.gapO = gapO;
		this.gapE = gapE;
		this.subst = subst;
		this.matrix = new float [seqY.length()+1][seqX.length()+1];
	}
	
	public AlignData(ComparableSequence cmpSeqX, ComparableSequence cmpSeqY, float gapO, float gapE, SubstitutionTable subst) {
		this.seqX = cmpSeqX.isProfile() ? ((Profile)cmpSeqX).toQuestion() : cmpSeqX.toString();
		this.seqY = cmpSeqY.isProfile() ? ((Profile)cmpSeqY).toQuestion() : cmpSeqY.toString();
		this.cmpSeqX = cmpSeqX;
		this.cmpSeqY = cmpSeqY;
		this.gapO = gapO;
		this.gapE = gapE;
		this.subst = subst;
		this.matrix = new float [seqY.length()+1][seqX.length()+1];
	}
	
	/**
	 * Users should use <code>clone()<code> method
	 */
/*	private AlignData(AlignData data) {
		this(data.seqX, data.seqY, data.gapO, data.gapE, data.subst);
		Matrix.copy(matrix, data.matrix);
		Matrix.copy(dir, data.dir);
	}*/
	
/*	public byte[][] getDir() {
		return dir;
	}*/

	public float getGapE() {
		return gapE;
	}

	public float getGapO() {
		return gapO;
	}

	public float[][] getMatrix() {
		return matrix;
	}

	// TODO make it obsolete (us getCmp instead)
	public String getSeqX() {
		return getCmpSeqX().toString();
	}

	public String getSeqY() {
		return getCmpSeqY().toString();
	}

	public ComparableSequence getCmpSeqX() {
		return cmpSeqX;
	}

	public ComparableSequence getCmpSeqY() {
		return cmpSeqY;
	}

	public SubstitutionTable getSubst() {
		return subst;
	}

	public void setGapE(int gapE) {
		this.gapE = gapE;
	}

	public void setGapO(int gapO) {
		this.gapO = gapO;
	}

	public void setMatrixFrom(float[][] matrix) {
		if (this.matrix.length == matrix.length && this.matrix[0].length == matrix[0].length) {
			for (int y = 0; y < matrix.length; y++) {
				for (int x = 0; x < matrix[y].length; x++) {
					this.matrix[y][x] = matrix[y][x];
				}
			}
		} else 
			this.matrix = Matrix.copy(matrix);
	}

	public void setSeqX(String seqX) {
		this.seqX = seqX;
	}

	public void setSeqY(String seqY) {
		this.seqY = seqY;
	}

	public void setSubst(SubstitutionTable subst) {
		this.subst = subst;
	}

	/*	public float localGetMax(float matrix[][], float gapExt) {
		local(matrix, gapExt);
		
		// pick the result
		int[] resultPos = Matrix.getMaxPos(matrix);
		return matrix[resultPos[1]+1][resultPos[0]+1];
	}*/
	
	/**
	 * Return region of maximal similarity 
	 * 	{ { s1start, s1end}, {s2start, s2end} }
	 * 
	 * The result is 0-based!
	 * @see comment for {@link findMax}
	 */
/*	public abstract int[][] findMaxRegion(); {
		int endPos[] = Matrix.getMaxPos(matrix);
		int x = endPos[0]+1;
		int y = endPos[1]+1;
		while (matrix[y][x] != 0) {
			if ((dir[y][x] & AlignData.DIR_UP_LEFT) != 0) {
				y--;
				x--;
			} else if ((dir[y][x] & AlignData.DIR_UP) != 0) {
				y--;
			} else if ((dir[y][x] & AlignData.DIR_LEFT) != 0) {
				x--;
			} else {
				throw new RuntimeException("Direction array not consistent");
			}
		}
		return new int[][] { 
			new int[] {x+1-1 /* -1 because result returned is to be 0-based
							     +1 because we went too far in the matrix/
							     , endPos[0] /* 0-based /}, 
			new int[] {y+1-1, endPos[1]} };
	}*/

	/** Prints how many ambigous entries were calculated 
	 * as a fraction of all non-zero entries
	 */
/*	public int printAmbiguous() {
		int amb = 0;
		int nonZero = 0;
		for (int y = 0; y < dir.length; y++) {
			for (int x = 0; x < dir[0].length; x++) {
				if (dir[y][x] != 0) {
					nonZero++;
					if (dir[y][x] != AlignData.DIR_LEFT && dir[y][x] != AlignData.DIR_UP && dir[y][x] != AlignData.DIR_UP_LEFT) 
						amb++;
					}
			}
		}
		System.out.print(amb+"/"+nonZero);
		return amb;
	}*/
	

	public Trace getMaxTrace() {
		int [][]traceArr = new int[2][];
		markMaxRegion(null, traceArr, 0);
		return new Trace(traceArr, matrix);
	}
	
	/**
	 * Marks the toMark with a trace of maximal result of matrix
	 * @param toMark
	 * @param trace Trace of maximal solution (consequent pairs of matched characters).
	 * 			Should be {null, null} if one wants to get the result { {x1, x2, ...}, {y1, y2, ...} }
	 * @return False if there was no positive value
	 */
	public boolean markMaxRegion(float [][]toMark, int[][] trace, float value) {
		float [][]matrix = getMatrix();
		int endPos[] = Matrix.getMaxPos(matrix, 1, 1);
		int x = endPos[0];
		int y = endPos[1];
		if (matrix[y][x] <= 0) return false;
		int tindex = 0; // trace index
		
		if (trace != null) {
			// allocate the space for the trace
			// it can be too long, though. Will take care
			// of it later
			trace[0] = new int[Math.max(x,y)];
			trace[1] = new int[trace[0].length];
		}
		
		while (matrix[y][x] > 0) {
			
			if (Assert.DEBUG) {
				Assert.assertTrue("Trace out of range", x > 0 && y > 0);
			}
			
			if (toMark != null) {
				toMark[y][x] = value;
			}
			if (trace != null) {
				trace[0][tindex] = x;
				trace[1][tindex] = y;
				tindex++;
			}
			
			int px = getPrevX(x, y);
			int py = getPrevY(x, y);
			
			x = px;
			y = py;
		}
		
		// fix the trace matrix -- reverse and shorten it
		if (trace != null) {
			int [][]tracerev = new int [2][tindex];
			for (int i = 0; i < tindex; i++) {
				tracerev[0][i] = trace[0][tindex-i-1];
				tracerev[1][i] = trace[1][tindex-i-1];
			}
			trace[0] = tracerev[0];
			trace[1] = tracerev[1];
		}
		return true;
	}
	
	/**
	 * @see markMaxRegion
	 */
	public boolean markMaxRegion(float [][]toMark, Trace trace, float value) {
		boolean result;
		if (trace != null) {
			int [][]tarr = new int[2][0];
			result = markMaxRegion(toMark, tarr, value);
			trace.init(tarr[0], tarr[1], this.matrix);
		} else {
			result = markMaxRegion(toMark, (int [][])null, value);
		}
		return result;
	}
	
	/**
	 * @return The x coordinatte of the value on which matrix[y][x] 
	 * was based. The postions along indel should not be returned (i.e.
	 * only coordinations where substitution occured). 
	 * On the end of trace:
	 * <code>data.getMatrix()[getPrevY(x,y)][getPrevX(x,y)] == 0</code>
	 * or 0 is returned.
	 */
	abstract public int getPrevX(int x, int y);
	
	/**
	 * @return The x coordinatte of the value on which matrix[y][x] 
	 * was based. The postions along indel should not be returned (i.e.
	 * only coordinations where substitution occured).
	 * On the end of trace:
	 * <code>data.getMatrix()[getPrevY(x,y)][getPrevX(x,y)] == 0</code>
	 * or 0 is returned.
	 */
	public abstract int getPrevY(int x, int y);
	
	public Object clone() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Clears direction matrix - see subclasses for details.
	 */
	public abstract void clearDir();

}
