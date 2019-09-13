package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

/**
 * Creates a matrix with independent positions for a self comparison.
 */
public class SelfLocalIndependentFill implements MatrixFill {

	public void fill(AlignData data) {
		localFill(data);
	}
	
	public static void localFill(AlignData data) {
		String seqX = data.getSeqX();
		String seqY = data.getSeqY();
		SubstitutionTable subst = data.getSubst();
		float m[][] = data.getMatrix();
		
		int lenX = seqX.length();
		int lenY = seqY.length();
		for (int y = 1; y <= lenY; y++) {
			for (int x = 1; x <= lenX; x++) {
				if (x <= y) 
					m[y][x] = -Matrix.INF;
				else
					m[y][x] = subst.getValue(seqX.charAt((int)(Math.random()*lenX)), seqY.charAt((int)(Math.random()*lenY)));
			}
		}		
	}

}
