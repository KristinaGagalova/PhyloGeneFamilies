package nl.vu.cs.align.substtable;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.*;


public class SubstitutionTable {
	
	public static char AACID[] = new char[] 
		  {'A', 'R', 'N', 'D', 'C', 
		   'Q', 'E', 'G', 'H', 'I', 
		   'L', 'K', 'M', 'F', 'P', 
		   'S', 'T', 'W', 'Y', 'V'};
	
	public static String AACID_3_LETTER[] = new String[] 
		  {"Ala", "Arg", "Asn", "Asp", "Cys", 
		   "Gln", "Glu", "Gly", "His", "Ile", 
		   "Leu", "Lys", "Met", "Phe", "Pro", 
		   "Ser", "Thr", "Trp", "Tyr", "Val"};
	
	public static int ROBINSON_SUM = 450431;
	/** 
	 * aacid count taken from ROBINSON&ROBINSON PNAS (1991) 88, p. 8880
	 */
	public static int AACID_COUNT_ROBINSON[] = new int[] 
		  {35155, 23105, 20212, 24161, 8669, 
		   19208, 28354, 33229, 9906, 23161, 
		   40625, 25872, 10101, 17367, 23435, 
		   32070, 26311, 5990, 14488, 29012};
	
	/** 
	 * aacid freqs taken from ROBINSON&ROBINSON PNAS (1991) 88, p. 8880
	 */
	public static double AACID_FREQ_ROBINSON[];
	{
		AACID_FREQ_ROBINSON = new double[AACID_COUNT_ROBINSON.length];
		for (int i = 0; i < AACID_COUNT_ROBINSON.length; i++) {
			AACID_FREQ_ROBINSON[i] = AACID_COUNT_ROBINSON[i] / (double)ROBINSON_SUM;
		}
	}
	
	private int charNumArray[];
	private char numCharArray[] = new char[255];
	private static final int UNKNOWN_CHAR = 255;

	protected float[][] matrix;
	
	private SubstitutionTable(int maxLetters) {
		charNumArray = new int[maxLetters];
		for (int i = 0; i < maxLetters; i++) {
			charNumArray[i] = -1;
		}
	}

	/**
	 * Reads input from a file in format:

#  Matrix made by matblas from blosum62.iij
#  * column uses minimum score
#  BLOSUM Clustered Scoring Matrix in 1/2 Bit Units
#  Blocks Database = /data/blocks_5.0/blocks.dat
#  Cluster Percentage: >= 62
#  Entropy =   0.6979, Expected =  -0.5209
   A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V  B  Z  X  *
A  4 -1 -2 -2  0 -1 -1  0 -2 -1 -1 -1 -1 -2 -1  1  0 -3 -2  0 -2 -1  0 -4 
R -1  5  0 -2 -3  1  0 -2  0 -3 -2  2 -1 -3 -2 -1 -1 -3 -2 -3 -1  0 -1 -4 
N -2  0  6  1 -3  0  0  0  1 -3 -3  0 -2 -3 -2  1  0 -4 -2 -3  3  0 -1 -4 
D -2 -2  1  6 -3  0  2 -1 -1 -3 -4 -1 -3 -3 -1  0 -1 -4 -3 -3  4  1 -1 -4 
C  0 -3 -3 -3  9 -3 -4 -3 -3 -1 -1 -3 -1 -2 -3 -1 -1 -2 -2 -1 -3 -3 -2 -4 
Q -1  1  0  0 -3  5  2 -2  0 -3 -2  1  0 -3 -1  0 -1 -2 -1 -2  0  3 -1 -4 
E -1  0  0  2 -4  2  5 -2  0 -3 -3  1 -2 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4 
G  0 -2  0 -1 -3 -2 -2  6 -2 -4 -4 -2 -3 -3 -2  0 -2 -2 -3 -3 -1 -2 -1 -4 
H -2  0  1 -1 -3  0  0 -2  8 -3 -3 -1 -2 -1 -2 -1 -2 -2  2 -3  0  0 -1 -4 
I -1 -3 -3 -3 -1 -3 -3 -4 -3  4  2 -3  1  0 -3 -2 -1 -3 -1  3 -3 -3 -1 -4 
L -1 -2 -3 -4 -1 -2 -3 -4 -3  2  4 -2  2  0 -3 -2 -1 -2 -1  1 -4 -3 -1 -4 
K -1  2  0 -1 -3  1  1 -2 -1 -3 -2  5 -1 -3 -1  0 -1 -3 -2 -2  0  1 -1 -4 
M -1 -1 -2 -3 -1  0 -2 -3 -2  1  2 -1  5  0 -2 -1 -1 -1 -1  1 -3 -1 -1 -4 
F -2 -3 -3 -3 -2 -3 -3 -3 -1  0  0 -3  0  6 -4 -2 -2  1  3 -1 -3 -3 -1 -4 
P -1 -2 -2 -1 -3 -1 -1 -2 -2 -3 -3 -1 -2 -4  7 -1 -1 -4 -3 -2 -2 -1 -2 -4 
S  1 -1  1  0 -1  0  0  0 -1 -2 -2  0 -1 -2 -1  4  1 -3 -2 -2  0  0  0 -4 
T  0 -1  0 -1 -1 -1 -1 -2 -2 -1 -1 -1 -1 -2 -1  1  5 -2 -2  0 -1 -1  0 -4 
W -3 -3 -4 -4 -2 -2 -3 -2 -2 -3 -2 -3 -1  1 -4 -3 -2 11  2 -3 -4 -3 -2 -4 
Y -2 -2 -2 -3 -2 -1 -2 -3  2 -1 -1 -2 -1  3 -3 -2 -2  2  7 -1 -3 -2 -1 -4 
V  0 -3 -3 -3 -1 -2 -2 -3 -3  3  1 -2  1 -1 -2 -2  0 -3 -1  4 -3 -2 -1 -4 
B -2 -1  3  4 -3  0  1 -1  0 -3 -4  0 -3 -3 -2  0 -1 -4 -3 -3  4  1 -1 -4 
Z -1  0  0  1 -3  3  4 -2  0 -3 -3  1 -1 -3 -1  0 -1 -3 -2 -2  1  4 -1 -4 
X  0 -1 -1 -1 -2 -1 -1 -1 -1 -1 -1 -1 -1 -1 -2  0  0 -2 -1 -1 -1 -1 -1 -4 
* -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4 -4  1 
 
	 */
	public SubstitutionTable(String fname) throws IOException {
		this(UNKNOWN_CHAR+1);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
		
		String line;
		line = br.readLine();
		
		// skip empty lines or #
		while (line != null && isEmptyLine(line)) {
			line = br.readLine();
		}
		
		// read vertical list of AA
		// assign a number to each of the AA starting from 0
		if (line != null) {
			
		    StringTokenizer st = new StringTokenizer(line);
		    int charNum = 0;
		    while (st.hasMoreTokens()) {
		        char c = st.nextToken().charAt(0);
		        setCharNum(c, charNum);
		        charNum++;
		    }
		    
			// create the substitution matrix
			matrix = new float[charNum][charNum];
		}
		
		// read the table
		line = br.readLine();
		int yCharNum = 0;
		while (line != null) {
			if (!isEmptyLine(line)) {
				StringTokenizer st = new StringTokenizer(line);
				// read the y character
		        char c = st.nextToken().charAt(0);
				Assert.assertTrue(yCharNum == getCharNum(c));
	
				// read the values
				int xCharNum = 0;
			    while (st.hasMoreTokens()) {
			    	String entry = st.nextToken();
			    	int entryVal = Integer.valueOf(entry).intValue();
			    	setValue(yCharNum, xCharNum, entryVal);
			    	xCharNum++;
			    }
			    
				// advance y number
				yCharNum++;
			}
			line = br.readLine();
		}
	}

	public void setValue(int yCharNum, int xCharNum, float val) {
		matrix[yCharNum][xCharNum] = val;
		matrix[xCharNum][yCharNum] = val;
	}

	public void setValue(char yChar, char xChar, float val) {
		setValue(getCharNum(yChar),getCharNum(xChar), val);
	}

	public float getValue(int yCharNum, int xCharNum) {
		return matrix[yCharNum][xCharNum];
	}

	public float getValue(char yChar, char xChar) {
		Assert.assertTrue(yChar != '?' && xChar != '?');
		return matrix[getCharNum(yChar)][getCharNum(xChar)];
	}

	public int getCharNum(char c) {
		return charNumArray[getNumericValue(c)];
	}

	private int getNumericValue(char c) {
		int v = Character.getNumericValue(c);
		if (v < 0)
			return UNKNOWN_CHAR;
		return v;
	}


	public char getNumChar(int num) {
		Assert.assertTrue(numCharArray[num] != 0);
		return numCharArray[num];
	}

	private void setCharNum(char c, int charNum) {
		int cValue = getNumericValue(c);
		Assert.assertTrue(cValue != -1);
		Assert.assertTrue(cValue >= 0 && cValue < charNumArray.length);
		Assert.assertTrue(charNumArray[cValue] == -1 || charNumArray[cValue] == charNum);
		charNumArray[cValue] = charNum;
		numCharArray[charNum] = c;
	}


	private boolean isEmptyLine(String line) {
		for (int i = 0; i<line.length(); i++) {
			if (!Character.isWhitespace(line.charAt(i))) {
				if (line.charAt(i) == '#') 
					return true;
				else 
					return false;
			}
		}
		return true;
	}

	/**
	 * @return True for a regular amino acid.
	 */
	public static boolean isAAcid(char c) {
		c = Character.toUpperCase(c);
		for (int i = 0; i < AACID.length; i++) {
			if (c == AACID[i]) return true;
		}
		return false;
	}
	
	/**
	 * @return True for a regular amino acid or special aa like X
	 */
	public boolean isAllowedLetter(char c) {
		c = Character.toUpperCase(c);
		int num = getNumericValue(c);
		if (num < 0 || num >= numCharArray.length)
			return false;
		return true;
	}
	
	/**
	 * @return The size of the alphabet, including special chars
	 */
	public int getAlphabetSize() {
		return matrix.length;
	}
	
	/**
	 * @return The size of the alphabet, only letters which can be part of profile
	 * (no special ones, like X etc).
	 */
	public int getLegitimateAlphabetSize() {
		return AACID.length;
	}
	
	/**
	 * @return The size of the alphabet, only letters which can be part of profile
	 * (no special ones, like X etc).
	 */
	public boolean isLegitimateLetter(char c) {
		return isAAcid(c);
	}
	
	public float [][]getMatrix() {
		return matrix;
	}
	
	/**
	 * @param seq
	 * @return Sequence converted ready to do subst.getMatrix()[seq[i]][seq[j]]
	 */
	public int []convertToCodes(String seq) {
		int result[] = new int[seq.length()];
		for (int i = 0; i < result.length; i++) {
//			if (!isAAcid(seq.charAt(i)))
//				Assert.fail("Char "+seq.charAt(i)+ " from "+seq+" was supposed to be AA");
			result[i] = getCharNum(seq.charAt(i));
		}
		return result;
	}

	/**
	 * Removes everything but amino acids
	 * 
	 * @param seq
	 * @return Filtered sequence
	 */
	public static String filterAASeq(String seq) {
		String randomSeq;
		{
			StringBuffer sb = new StringBuffer(seq.length());
			for (int i = 0; i < seq.length(); i++) {
				if (SubstitutionTable.isAAcid(seq.charAt(i)))
					sb.append(seq.charAt(i));
			}
			randomSeq = sb.toString();
		}
		return randomSeq;
	}

	/**
	 * No special characters allowed!
	 */
	public boolean isProtein(String seq) {
		for (int i = 0; i < seq.length(); i++) {
			if (!SubstitutionTable.isAAcid(seq.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isAllowedSeq(String seq) {
		for (int i = 0; i < seq.length(); i++) {
			if (!isAllowedLetter(seq.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
