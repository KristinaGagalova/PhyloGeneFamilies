/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.matrix;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.substtable.*;


public abstract class AlignDataAffine extends AlignData {
	public AlignDataAffine(String seqX, String seqY, float gapO, float gapE, SubstitutionTable subst) {
		super(seqX, seqY, gapO, gapE, subst);
	}
	
	public AlignDataAffine(ComparableSequence cmpSeqX, ComparableSequence cmpSeqY, float gapO, float gapE, SubstitutionTable subst) {
		super(cmpSeqX, cmpSeqY, gapO, gapE, subst);
	}
}
