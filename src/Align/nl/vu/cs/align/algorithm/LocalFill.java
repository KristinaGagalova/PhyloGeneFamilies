package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class LocalFill implements MatrixFill {

	public void fill(AlignData data) {
		localFill(data);
	}
	
	public static void localFill(AlignData data) {
		/*String seqX = data.getSeqX();
		String seqY = data.getSeqY();
		SubstitutionTable subst = data.getSubst();
		float m[][] = data.getMatrix();
		
		int lenX = seqX.length();
		int lenY = seqY.length();
		for (int y = 1; y <= lenY; y++) {
			for (int x = 1; x <= lenX; x++) {
				m[y][x] = subst.getValue(seqX.charAt(x-1), seqY.charAt(y-1));
			}
		}
		*/
		String seqX = data.getSeqX();
		String seqY = data.getSeqY();
		SubstitutionTable subst = data.getSubst();
		float m[][] = data.getMatrix();
		
		int lenX = seqX.length();
		int lenY = seqY.length();
		int []seqXcode = subst.convertToCodes(seqX);
		int []seqYcode = subst.convertToCodes(seqY);
		float [][]matrix = subst.getMatrix();
		for (int y = 1; y <= lenY; y++) {
			for (int x = 1; x <= lenX; x++) {
				m[y][x] = matrix[seqXcode[x-1]][seqYcode[y-1]];
			}
		}
		
	}

}
