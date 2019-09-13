package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

/**
 * Class represents local alignment algorithm
 */
public class LocalAlignSimple extends Align {
	
	public float align(AlignData data) {
		return localAlignSimple((AlignDataSimple)data);
	}

	/**
	 * Fills in the matrix using precomputed position-specific scores.
	 */
	public static float localAlignSimple(AlignDataSimple data) {
		float[][] matrix = data.getMatrix();
		byte[][] dir = data.getDir();
		float gapO = data.getGapO();
		float gapE = data.getGapE();
		// check params
		Assert.assertTrue(gapE == gapO);
		
		Matrix.clear(dir);
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		// perform calc
		for (int y = 1; y <= yLen; y++) {
			for (int x = 1; x <= xLen; x++) {
				float subst = matrix[y-1][x-1]+matrix[y][x];
				float extx = matrix[y][x-1]+gapE;
				float exty = matrix[y-1][x]+gapE;
				
				if (0 >= subst && 0 >= extx && 0 >= exty) {
					matrix[y][x] = 0;
					dir[y][x] |= AlignDataSimple.DIR_NONE;
				} else {
					if (subst >= extx && subst >= exty) {
						matrix[y][x] = subst;
						dir[y][x] |= AlignDataSimple.DIR_UP_LEFT;
					}
		
					if (extx >= subst && extx >= exty) {
						matrix[y][x] = extx;
						dir[y][x] |= AlignDataSimple.DIR_LEFT;
					}
		
					if (exty >= subst && exty >= extx) {
						matrix[y][x] = exty;
						dir[y][x] |= AlignDataSimple.DIR_UP;
					}
				}
			}
		}
		return Matrix.getMax(matrix);
	}
	
	
}
