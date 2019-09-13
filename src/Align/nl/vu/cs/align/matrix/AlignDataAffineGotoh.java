package nl.vu.cs.align.matrix;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.substtable.*;

public class AlignDataAffineGotoh extends AlignDataAffine {

	/**	
	 * gapStart matrix describes start of the gap. 
	 * <code>gapStart[y][x] = 0 && matrix[y][x] > 0 </code> means match 
	 * (X[x],Y[y]) and (X[x-1],Y[y-1]);
	 * 
	 * <code>gapStart[y][x] = gs</code> means match (X[x],Y[y]) and:
	 * <bullets> 
	 * 		<bullet>gs > 0 -> (X[gs],Y[y-1])
	 * 		<bullet>gs < 0 -> (X[x-1],Y[-gs]) 
	 * </bullets>
	 */
	short [][]gapStart;

	/**	
	 * matrix describes end of the gap. 
	 */
	short [][]gapEndX;

	/**	
	 * matrix describes end of the gap. 
	 */
	short [][]gapEndY;
	
	boolean wrapAround;

	public AlignDataAffineGotoh(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst) {
		super(seqX, seqY, gapO, gapX, subst);
		this.gapStart = new short [seqY.length()+1][seqX.length()+1];
	}

	public AlignDataAffineGotoh(Profile prof, Sequence seq, float gapO, float gapX, SubstitutionTable subst) {
		super(prof, seq, gapO, gapX, subst);
		this.gapStart = new short [seqY.length()+1][seqX.length()+1];
	}
	
	public int getPrevX(int x, int y) {
		int r;
		// resolve whether this is the first letter in the trace
		if (gapStart[y][x] == 0 && matrix[y-1][x-1] == 0f) { // TODO is y-1, x-1 a bug?
			r = 0;
		} else if (gapStart[y][x] == 0 || gapStart[y][x] < 0) { 
			r = x - 1;
		} else {
			r = gapStart[y][x];
		}
		
		if (wrapAround && r == 0 && y > 1) r = matrix[0].length-1;
		return r;
	}


	public int getPrevY(int x, int y) {
		// resolve whether this is the first letter in the trace
		if (gapStart[y][x] == 0 && matrix[y-1][x-1] == 0f) {
			return 0;
		}
		// not the first letter - trace the previous one
		if (gapStart[y][x] == 0 || gapStart[y][x] > 0) {
			return y - 1;
		} else {
			return -gapStart[y][x];
		}
	}

	public void clearDir() {
		Matrix.clear(gapStart);
	}

	public short[][] getGapMatrix() {
		return gapStart;
	}

	public short[][] getGapEndMatrixX() {
		return null;
	}

	public short[][] getGapEndMatrixY() {
		return null;
	}
	
	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround; 
	}

}
