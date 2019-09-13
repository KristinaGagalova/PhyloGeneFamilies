package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

public class BrokenSelfLocalFill implements MatrixFill {

	public void fill(AlignData data) {
		selfLocalFill(data);
	}

	public static void selfLocalFill(AlignData data) {
		LocalFill.localFill(data);
		float [][]m = data.getMatrix();
		int len = data.getSeqX().length();
		Assert.assertTrue(len == data.getSeqY().length());
		
		int r = NarrowLocalFill.calcR(len);
		
		for (int y = 1; y <= len; y++) {
			for (int x = 1; x <= len; x++) {
				// clear top-right region
				if (y+r <= x) 
					m[y][x] = -Matrix.INF;
					
				// clear region below diagonal
				if (y >= x && y-r < x) 
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
}
