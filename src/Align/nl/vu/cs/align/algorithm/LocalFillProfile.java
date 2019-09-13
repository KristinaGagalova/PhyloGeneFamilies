package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class LocalFillProfile implements MatrixFill {

	public void fill(AlignData data) {
		//localFill(data);
		Assert.fail("not implemented");
	}
	
	public static void localFill(AlignData data, Profile profile, Sequence seq) {
		//ComparableSequence seqX = new Sequence(data.getSeqX());
		//ComparableSequence seqY = new Sequence(data.getSeqX());
//		String seqX = data.getSeqX();
//		String seqY = data.getSeqY();
		SubstitutionTable subst = data.getSubst();
		float m[][] = data.getMatrix();
		
		int lenX = profile.length();
		int lenY = seq.length();
		for (int y = 1; y <= lenY; y++) {
			for (int x = 1; x <= lenX; x++) {
				m[y][x] = profile.scoreWithSequence(x-1, seq, y-1, subst);
//				subst.getValue(seqX.charAt(x-1), seqY.charAt(y-1));
			}
		}		
	}

}
