package nl.vu.cs.align;

import java.io.*;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

/**
 * Calculates local similarities of random sequences
 */
public class SelfStats {

	private static final boolean DEBUG_PRINT_SEQS = false;
	private static final boolean DEBUG_PRINT_SCORE = false;
	private static final boolean DEBUG_PRINT_PARAMS = false;
	private static final boolean DEBUG_PRINT_DECORATIONS = false;
	
	private static void selfStats(int wordLen, int wordOverlap, boolean self, boolean exact, SubstitutionTable subst) {
		if (DEBUG_PRINT_PARAMS) {
			if (self) {
				System.out.println("SELF cmp");
			} else {
				System.out.println("NOT SELF cmp");		
			}
			if (exact) {
				System.out.println("EXACT cmp");
			} else {
				System.out.println("NOT EXACT cmp");		
			}
		}
		float sumScore = 0f;
		int numScore = 0;
		// check some words
		if (exact) {
			// all of them
			int wordMax = 1;
			// calculate the maximal seq value
			for (int i = 0; i < wordLen; i++) {
				wordMax <<= 1;
			}
			// 
			for (int word1 = 0; word1 < wordMax; word1++) {
				for (int word2 = 0; word2 < wordMax; word2++) {
					if ((!self) || word1 == word2) {
						// create seqs...
						String seq1 = makeSeq(word1, wordLen);
						String seq2 = makeSeq(word2, wordLen);
						// check the score for overlapping
						float score = 0;
						if (wordOverlap > 0) {
							// overlap it the simple way - "global"
							for (int i = 0; i < wordOverlap; i++) {
								 score += subst.getValue(
								 			seq1.charAt(wordLen - wordOverlap + i),
								 			seq2.charAt(i));
							}
						} else {
							score = calcLocalOverlapping(subst, seq1, seq2);
						}
						sumScore += score;
						numScore++;
					}
				}
			}
		} else {
			// check some words
			final int TEST_NUM = 100000;
			for (int i = 0; i < TEST_NUM; i++) {
				String seq1 = makeRandomSeq(wordLen);
				String seq2;
				if (self) {
					seq2 = seq1;
				} else {
					seq2 = makeRandomSeq(wordLen);			
				}
				sumScore += calcLocalOverlapping(subst, seq1, seq2);
				numScore++;
			}
		}
		if (DEBUG_PRINT_DECORATIONS) {
			System.out.print("For wordLen="+wordLen);
			System.out.print(" wordOverlap="+wordOverlap);
			System.out.print(" avg score is ");
		}
		System.out.print(sumScore/numScore);
		System.out.println();
	}

	private static String makeRandomSeq(int wordLen) {
		StringBuffer sb = new StringBuffer(wordLen);
		for (int i = 0; i < wordLen; i++) {
			if (Math.random() < 0.5d) {
				sb.append('A');
			} else {
				sb.append('B');				
			}
		}
		return sb.toString();
	}

	private static float calcLocalOverlapping(
		SubstitutionTable subst,
		String seq1,
		String seq2) {
		float score;
		// local overlapping
		Align align = new CompleteAlignSelfAffine();
		AlignDataAffineGotoh data = new AlignDataAffineGotoh(seq1, seq2, -Matrix.INF, -Matrix.INF, subst);
		score = align.align(data);
		if (DEBUG_PRINT_SCORE) {
			System.out.println("Score: "+score);
		}
		if (DEBUG_PRINT_SEQS) {
			Trace t = new Trace();
			data.markMaxRegion(null, t, 0);
			System.out.println("Seq1: "+seq1);
			System.out.println("Seq2: "+seq2);
			if (score > 0)
				t.show(seq1, seq2, System.out);
			else
				System.out.print("\n\n\n");
		}
		return score;
	}

	private static String makeSeq(int word, int wordLen) {
		StringBuffer result = new StringBuffer(wordLen);
		for (int i = 0; i < wordLen; i++) {
			if ((word & 1) == 0) {
				result.append('A');
			} else {
				result.append('B');
			}
			word >>= 1;
		}
		return result.toString();
	}

	static protected float MA = 1;
	static protected float MB = 1;
	static protected float MM = -1;//-Matrix.INF;
	
	public static void main(String[] arg) {
		
		SubstitutionTable subst = null;
//		ProteinLib library;
		try {
			subst = new SubstitutionTable("BLOSUM62");
//			library = new FastaD	b(arg[0]);
		} catch (IOException ioe) {
			System.err.println("Problem with reading matrix data");
			throw new RuntimeException(ioe.toString());
		}
		
		subst.setValue('A', 'A', MA);
		subst.setValue('B', 'B', MB);
		
		subst.setValue('A', 'B', MM);
		subst.setValue('B', 'A', MM);
		
		for (int i = 0; i < 40; i++) {
			System.out.println("SELF");
			selfStats(i, 0, true /* SELF? */, true /* EXACT? */, subst);
			System.out.println("NOT SELF");
			selfStats(i, 0, false /* SELF? */, true /* EXACT? */, subst);
		}
	}

}
