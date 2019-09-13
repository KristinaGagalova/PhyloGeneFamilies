/*
 * Created on May 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.RandomSimilarity;
import nl.vu.cs.align.substtable.*;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Sequence extends ComparableSequence {
	
	protected String seq;
	
	public Sequence(String seq) {
		this.seq = seq;
	}

	public int length() {
		return seq.length();
	}
	
	/**
	 * @param pos (0-based)
	 * @return Character at position i
	 */
	public char charAt(int pos) {
		return seq.charAt(pos);
	}

	public float score(
		int pos1,
		ComparableSequence seq2,
		int pos2,
		SubstitutionTable subst) {
		return seq2.scoreWithSequence(pos2, this, pos1, subst);

	}

	public float scoreWithProfile(
		int pos2,
		Profile seq1,
		int pos1,
		SubstitutionTable subst) {
		return seq1.scoreWithSequence(pos1, this, pos2, subst);

	}

	public float scoreWithSequence(
		int pos2,
		Sequence seq1,
		int pos1,
		SubstitutionTable subst) {
		return subst.getValue(seq1.charAt(pos1), this.charAt(pos2));
	}
	
	public String toString() {
		return seq;
	}

	public boolean isSequence() {
		return true;
	}

	public boolean isProfile() {
		return false;
	}

	public Object clone() {
		return new Sequence(seq);
	}
	
	public void randomize() {
		int n = seq.length();
		int hash[] = RandomSimilarity.generateRandomNumbers(n);
		StringBuffer stringBuffer = new StringBuffer(n);
		for (int i = 0; i < n; i++) {
			stringBuffer.append(seq.charAt(hash[i]));
		}
		seq = stringBuffer.toString();
	}

	/* (non-Javadoc)
	 * @see nl.vu.cs.align.algorithm.ComparableSequence#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Sequence)
			return seq.equals(((Sequence) o).seq);
		else
			return false;
	}

	/**
	 * @param i
	 * @param tSeqStart
	 * @return
	 */
	public Sequence subSequence(int beginIndex, int endIndex) {
		return new Sequence(seq.substring(beginIndex, endIndex));
	}
}
