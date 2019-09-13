/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;


public abstract class AlignAffine extends Align {
	
	public abstract AlignDataAffine createData(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst);
	
	public abstract AlignDataAffine createData(ComparableSequence seqX, ComparableSequence seqY, float gapO, float gapX, SubstitutionTable subst);

}
