package nl.vu.cs.align.matrix;

import nl.vu.cs.align.*;
import nl.vu.cs.align.substtable.*;

public class AlignDataSimple extends AlignData {

	/** Direction NONE */
	public static final int DIR_NONE = 0;
	
	/** Direction UP */
	public static final int DIR_UP = 1;
	
	/** Direction UP-LEFT */
	public static final int DIR_UP_LEFT = 2;
	
	/** Direction LEFT */
	public static final int DIR_LEFT = 4;
	
	byte dir[][];
	
	public AlignDataSimple(String seqX, String seqY, float gap, SubstitutionTable subst) {
		super(seqX, seqY, gap, gap, subst);
		this.dir = new byte [seqY.length()+1][seqX.length()+1];
	}

	/**
	 * Marks the toMark with a trace of maximal result of matrix
	 * @param toMark
	 * @param trace Trace of maximal solution (consequent pairs of matched characters).
	 * 			Should be {null, null} if one wants to get the result { {x1, x2, ...}, {y1, y2, ...} }
	 */
/*	public void markMaxRegion(float [][]toMark, int[][] trace, float value) {
		float [][]matrix = getMatrix();
		int endPos[] = Matrix.getMaxPos(matrix);
		int x = endPos[0]+1;
		int y = endPos[1]+1;
		int tindex = 0; // trace index
		
		if (trace != null) {
			// allocate the space for the trace
			// it can be too long, though. Will take care
			// of it later
			trace[0] = new int[Math.max(x,y)];
			trace[1] = new int[trace[0].length];
		}
		
		while (matrix[y][x] > 0) {
			if ((dir[y][x] & DIR_UP_LEFT) != 0) {
				if (toMark != null) {
					toMark[y][x] = value;
				}
				if (trace != null) {
					trace[0][tindex] = x;
					trace[1][tindex] = y;
					tindex++;
				}
				y--;
				x--;
			} else if ((dir[y][x] & DIR_UP) != 0) {
				y--;
			} else if ((dir[y][x] & DIR_LEFT) != 0) {
				x--;
			} else {
				throw new RuntimeException("Direction array not consistent");
			}
		}
	}*/
		

	public int getPrevX(int x, int y) {
		if ((dir[y][x] & DIR_UP_LEFT) != 0) {
			return x-1;
		} else if ((dir[y][x] & DIR_LEFT) != 0) {
			while ((dir[y][x] & DIR_LEFT) != 0) {
				x--;
			}
			return x;
		} else if ((dir[y][x] & DIR_UP) != 0) {
			return x-1;
		}
		Assert.fail("Trace lost");
		return -1;
	}

	public int getPrevY(int x, int y) {
		if ((dir[y][x] & DIR_UP_LEFT) != 0) {
			return y-1;
		} else if ((dir[y][x] & DIR_LEFT) != 0) {
			return y-1;
		} else if ((dir[y][x] & DIR_UP) != 0) {
			while ((dir[y][x] & DIR_UP) != 0) {
				y--;
			}
			return y;
		}
		Assert.fail("Trace lost");
		return -1;
	}

	public void clearDir() {
		Matrix.clear(dir);
	}

	public byte[][] getDir() {
		return dir;
	}

}
