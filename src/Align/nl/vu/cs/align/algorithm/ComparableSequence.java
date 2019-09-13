/*
 * Created on May 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.substtable.*;

public abstract class ComparableSequence {
	
	public abstract int length();
	
	public abstract float score(int pos1, 
		ComparableSequence seq2, int pos2, 
		SubstitutionTable subst);
		
	public abstract float scoreWithProfile(int pos2, 
		Profile seq1, int pos1, 
		SubstitutionTable subst);
				
	public abstract float scoreWithSequence(int pos2, 
		Sequence seq1, int pos1, 
		SubstitutionTable subst);
		
	public abstract String toString();
	
	public abstract boolean isSequence();
	
	public abstract boolean isProfile();
	
	public abstract Object clone();
	
	public abstract void randomize();
	
	public abstract boolean equals(Object o);

}
