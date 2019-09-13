package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public abstract class CompleteAlignSelfAbstractTest extends CompleteAlignTest {

	/**
	 * Constructor for CompleteAlignLocalTest.
	 * @param arg0
	 */
	public CompleteAlignSelfAbstractTest(String arg0) {
		super(arg0);
	}


	public void testAlign1() {
		final char letter = SubstitutionTable.AACID[0];
		AlignData data = getAlignData(""+letter, ""+letter, -1, -1, blosum62);
		_testForMaxValue(align, data, 0f);
	}

	public void testAlign2() {
		final char letter = SubstitutionTable.AACID[0];
		AlignData data = getAlignData(""+letter+letter, ""+letter+letter, -1, -1, blosum62);
		_testForMaxValue(align, data, blosum62.getValue(letter, letter));
	}

	public void testAlign3() {
		final int rept = 100;
		final char letter = SubstitutionTable.AACID[0];
		StringBuffer sb = new StringBuffer(100);
		for (int i = 0; i < rept; i++) sb.append(letter);
		String seq =  sb.toString();
		AlignData data = getAlignData(seq, seq, -1, -1, blosum62);
		_testForMaxValue(align, data, blosum62.getValue(letter, letter)*(rept-1));
	}
	
	abstract protected AlignData getAlignData(String seqX, String seqY, float gapo, float gapx, SubstitutionTable subst);
}
