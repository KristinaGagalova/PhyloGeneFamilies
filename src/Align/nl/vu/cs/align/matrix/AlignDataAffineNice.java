package nl.vu.cs.align.matrix;

import nl.vu.cs.align.*;
import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.substtable.*;

public class AlignDataAffineNice extends AlignDataAffine {
	
	/**	
	 * The value gx[y][x] corresponds to the alignment of X[1..x] vs Y[1..y]
	 * ending with X[x] matched against the space. 
	 *		 ..............X[x]
	 *		 ......Y[y].... -  
	 */
	float [][]gx;

	/**	
	 * The value gy[y][x] corresponds to the alignment of X[1..x] vs Y[1..y]
	 * ending with Y[y] matched against the space. 
	 *		 ......X[x].... -  
	 *		 ..............Y[y]
	 */
	float [][]gy;
	
	public static final float GAP_STOP = -Matrix.INF;

	// data used in LocalAlignAffineNice
	public int tracexStart[];
	public int tracexEnd[];
	
	public AlignDataAffineNice(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst) {
		super(seqX, seqY, gapO, gapX, subst);
		this.gx = Matrix.createEmpty(super.matrix);
		this.gy = Matrix.createEmpty(super.matrix);
		this.tracexStart = new int [matrix.length+1];
		this.tracexEnd = new int [matrix.length+1];
	}
	
	public AlignDataAffineNice(Profile prof, Sequence seq, float gapO, float gapX, SubstitutionTable subst) {
		super(prof, seq, gapO, gapX, subst);
		this.gx = Matrix.createEmpty(super.matrix);
		this.gy = Matrix.createEmpty(super.matrix);
		this.tracexStart = new int [matrix.length+1];
		this.tracexEnd = new int [matrix.length+1];
	}

	public int getPrevX(int x, int y) {
		
		float substitution = cmpSeqX.scoreWithSequence(x-1, (Sequence)cmpSeqY, y-1, subst);
		//substitution = profileX.scoreWithSequence(x-1, sequenceY, y-1, subst);
		//subst.getValue(seqX.charAt(x-1), seqY.charAt(y-1)); // bug!
		float m = matrix[y][x]-substitution;
		
		// was there substitution?
		// TODO nasty hack!, because ((a+b)-b) != a
//		if (matrix[y-1][x-1] == m)
		if (Math.abs(matrix[y-1][x-1] - m) < Matrix.FLOAT_PRECISION)
			return x-1;
		
		// was Y[y-1] matched against the space?
		if (Math.abs(gy[y-1][x-1]-m) < Matrix.FLOAT_PRECISION) {
			return x-1+0;
		}
		
		// was X[x-1] matched against the space?
		if (Math.abs(gx[y-1][x-1] - m) < Matrix.FLOAT_PRECISION) {
			x--;
			y--;
			// travel gy backwards the gx matrix
			while (Math.abs(gx[y][x] - (matrix[y][x-1] + gapO)) >= Matrix.FLOAT_PRECISION) {
				Assert.assertTrue(gx[y][x] == gx[y][x-1] + gapE);
				x--;
			}
			return x-1;
		}  else {
			Assert.fail("No back trace found!");
			return 0;
		}
	}

	public int getPrevY(int x, int y) {
		float substitution = cmpSeqX.scoreWithSequence(x-1, (Sequence)cmpSeqY, y-1, subst);
		//float substitution = subst.getValue(seqX.charAt(x-1), seqY.charAt(y-1)); // bug
		float m = matrix[y][x]-substitution;
		
		// was there substitution?
		// for the nasty hacks with math.abs, see getPrevX
		if (Math.abs(matrix[y-1][x-1] - m) < Matrix.FLOAT_PRECISION)
			return y-1;
		
		// was X[x-1] matched against the space?
		if (Math.abs(gx[y-1][x-1] - m) < Matrix.FLOAT_PRECISION) {
			return y-1;
		}
		
		// was Y[y-1] matched against the space?
		if (Math.abs(gy[y-1][x-1]-m) < Matrix.FLOAT_PRECISION) {
			x--;
			y--;
			// travel backwards the gy matrix
			while (Math.abs(gy[y][x] - (matrix[y-1][x] + gapO)) >= Matrix.FLOAT_PRECISION) {
				Assert.assertTrue(gy[y][x] == gy[y-1][x] + gapE);
				y--;
			}
			return y-1;
		}  else {
			Assert.fail("No back trace found!");
			return 0;
		}
	}

	public void clearDir() {
		Assert.fail("Not implemented");
		//Matrix.clear(gapStart);
	}

	public float[][] getGapX() {
		return gx;
	}

	public float[][] getGapY() {
		return gy;
	}

}
