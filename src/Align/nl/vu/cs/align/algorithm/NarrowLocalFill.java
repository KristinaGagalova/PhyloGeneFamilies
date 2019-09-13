package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

/**
 * This class performs narrow alignment -- alignment of a narrow section 
 * 2*(n-sqrt(n^2/2)) around diagonal.
 */
public class NarrowLocalFill implements MatrixFill {

	public void fill(AlignData data) {
		selfLocalFill(data);
	}

	public static void selfLocalFill(AlignData data) {
		LocalFill.localFill(data);
		float [][]m = data.getMatrix();
		int len = data.getSeqX().length();
		Assert.assertTrue(len == data.getSeqY().length());
		
		int r = calcR(len);
		
		for (int y = 1; y <= len; y++) {
			for (int x = 1; x <= len; x++) {
				if (y+r < x || x < y-r) 
					m[y][x] = -Matrix.INF;
			}
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

	public static int calcR(int len) {
		return (int)Math.floor(len-Math.sqrt(len*len/2d));
	}
}
