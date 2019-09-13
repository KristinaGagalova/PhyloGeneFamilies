package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

/**
 * Puts a mask (corresponding to a self fill) on fill area, 
 */
public class SelfLocalFillMask implements MatrixFill {

	public void fill(AlignData data) {
		selfLocalFillMask(data);
	}
	
	public static void selfLocalFillMask(float matrix[][], float mask) {
		int len = matrix.length-1;
		Assert.assertTrue(len == matrix[0].length-1);
		for (int y = 1; y <= len; y++) {
			for (int x = 1; x <= len; x++) {
				if (x <= y) 
					matrix[y][x] = mask;
			}
		}				
	}

	public static void selfLocalFillMask(AlignData data) {
		float [][]m = data.getMatrix();
		int len = data.getSeqX().length();
		Assert.assertTrue(len == data.getSeqY().length());
		
		for (int y = 1; y <= len; y++) {
			for (int x = 1; x <= len; x++) {
				if (x <= y) 
					m[y][x] = -Matrix.INF;
			}
		}
		
		// TODO what a hack...
		if (data instanceof AlignDataAffineNice) {
			AlignDataAffineNice datanice = (AlignDataAffineNice) data;
			selfLocalFillMask(datanice.getGapX(), AlignDataAffineNice.GAP_STOP);
			selfLocalFillMask(datanice.getGapY(), AlignDataAffineNice.GAP_STOP);
		}
		/*		
		int count = 0;
		for (int y = 1; y <= len; y++) {
			for (int x = 1; x <= len; x++) {
				if (m[y][x] != -Matrix.INF)
					count++;
			}
		}
		System.err.println(count);
		*/
	}
}
