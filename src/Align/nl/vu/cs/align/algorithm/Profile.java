/*
 * Created on May 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class Profile extends ComparableSequence {
	
	float profile[][];
	// number of meaningful letters in alphabet
	
	SubstitutionTable subst;
	
	/** 
	 * Caches the results of comparison between amino acid (rows) and 
	 * profile position (columns) 
	 */
	float cache[][];
	static final float CACHE_EMPTY = -6666f;

	public Object clone() {
		return new Profile(Matrix.copy(profile), subst);
	}
	
	private Profile(float profile[][], SubstitutionTable subst) {
		this.profile = profile;
		this.subst = subst;
		cache = new float[subst.getAlphabetSize()][length()];
		clearCache();
	}

	public Profile(float [][]m, Sequence seq, 
		int startx, int sizex, int starty, int profileLen, int distanceFromDiag, SubstitutionTable subst, float [][]debugM) {
			
		this.subst = subst;
		
		// initiation	
		profile = new float[subst.getLegitimateAlphabetSize()][profileLen];
		cache = new float[subst.getAlphabetSize()][profileLen];
		
		// clear the profile
		clearCache();

		// first, east projection for maximum		
		float projectMaxE[] = new float[profileLen];
		
		// build the profile from what you see in matrix m
		float []filteredLine = new float[m[0].length];
		for (int y = 0; y < profileLen; y++) {
			
			// the code below is mainly a copy of SelfSimilarity.sumLine
			// don't forget to update both if you change something!
			
			// center is where the maximum trace goes!
			// for tandem repeats, that's their size 
			/*
			int centery = y + starty + distanceFromDiag;
			
			// filter the line
			filteredLine = Matrix.filterLine(m[y+starty], startx, sizex, profileLen, centery, filteredLine);
			*/
			/*
			if (false) {
				Matrix.saveImageGray(Matrix.paintHistogram(m[y+starty], null, 100), true, "no_filter");
				Matrix.saveImageGray(Matrix.paintHistogram(filteredLine, null, 100), true, "yes_filter");
				Matrix.saveImageGray(
					Matrix.paintHistogram(
						Matrix.filterLine(Matrix.fill(Matrix.createEmpty(m[y+starty]), 1f), startx, sizex, profileLen, y+starty, null),
				 		null, 100), 
				 	true, "schema_filter");
			}*/
			
			int endx = startx + sizex - 1;
			int maxSize = Profile.getMaxPosWithFilteringArraySize(startx, endx, profileLen);
			int []pos = new int[maxSize];
			float []val = new float[maxSize];
			
			dupa = true;
			int nPos = Profile.getMaxPosWithFiltering(m, y+starty, startx, endx, 
						profileLen, distanceFromDiag, pos, val, debugM);
				
			// PROBLEM how does it work for triple repeats?
			for (int i = 0; i < nPos; i++) {
				float matchValue = val[i];
				Assert.assertTrue(matchValue >= 0f);
				int matchPos = pos[i];
				
				if (debugM != null) debugM[y+starty][matchPos] = matchValue;
				char c = seq.charAt(matchPos-1);
				if (matchValue > 0 && subst.isLegitimateLetter(c)) {
					int matchedAA = subst.getCharNum(c);
					profile[matchedAA][y] += matchValue;
					projectMaxE[y] = Math.max(matchValue, projectMaxE[y]);
				}
			}
		
			
/*			for (int i = (startx-centery)/profileLen-1; i < (endx-centery)/profileLen; i++) {
				int regStart = centery+i*profileLen+profileLen/2;
				int regEnd = regStart+profileLen-1;
				if (regEnd > startx && regStart < endx) {
					updateProfile(
						filteredLine,
						Math.max(regStart, 0),
						Math.min(regEnd, filteredLine.length-1),
						y,
						projectMaxE,
						seq,
						subst,
						debugM,
						y + starty);
				}
			}*/
		}

		// add values for the diagonal to profile
		for (int p = 0; p < profileLen; p++) {
			char c = seq.charAt(p+starty-1);
			if (subst.isLegitimateLetter(c)) {
				int matchedAA = subst.getCharNum(c);
				profile[matchedAA][p] += projectMaxE[p];
			} 
		}
		
		// normalize values in the profile to one
		float projectProfileSumValues[] = Matrix.projectS(profile);
		for (int p = 0; p < profileLen; p++) {
			if (projectProfileSumValues[p] != 0) {
				for (int letter = 0; letter < profile.length; letter++) {
					profile[letter][p] /= projectProfileSumValues[p]; 
				}
			}
		}
	}

	private void clearCache() {
		for (int y = 0; y < cache.length; y++) {
			for (int x = 0; x < cache[0].length; x++) {
				cache[y][x] = CACHE_EMPTY;
			} 
		} 
	}

	private void updateProfile(
		float[] filteredLine,
		int x,
		int endx,
		int y,
		float[] projectMaxE,
		Sequence seq,
		SubstitutionTable subst, float [][]debugM, int debugY) {
			
		float matchValue = Matrix.highestDiff(filteredLine, x, endx);
		Assert.assertTrue(matchValue >= 0f);
		int matchPos = Matrix.getMaxPos(filteredLine, x, endx);
		if (debugM != null) debugM[debugY][matchPos] = matchValue;
		char c = seq.charAt(matchPos-1);
		if (matchValue > 0 && subst.isLegitimateLetter(c)) {
			int matchedAA = subst.getCharNum(c);
			profile[matchedAA][y] += matchValue;
			projectMaxE[y] = Math.max(matchValue, projectMaxE[y]);
		}
		//debugM[debugY][x] = -20; 
		//debugM[debugY][endx] = -20; 
	}

	public int length() {
		if (profile != null) return profile[0].length;
		else return 0;
	}

	public float score(
		int pos1,
		ComparableSequence seq2,
		int pos2,
		SubstitutionTable subst) {
		return seq2.scoreWithProfile(pos2, this, pos1, subst);

	}

	public float scoreWithProfile(
		int pos2,
		Profile seq1,
		int pos1,
		SubstitutionTable subst) {
		Assert.fail("Profile vs. profile comparison");
		return 0;
	}

	public float scoreWithSequence(
		int pos2,
		Sequence seq1,
		int pos1,
		SubstitutionTable subst) {
		
		// determine AA number
		int matchedAA = subst.getCharNum(seq1.charAt(pos1));
		// find the value in the cache
		if (cache[matchedAA][pos2] != CACHE_EMPTY) 
			return cache[matchedAA][pos2];
			
		float sumScore = 0f;
		for (int i = 0; i < profile.length; i++) {
			sumScore += profile[i][pos2]*subst.getValue(matchedAA, i);
			Assert.assertTrue(subst.getValue(matchedAA, i)==subst.getValue(i, matchedAA));
		}
		cache[matchedAA][pos2] = sumScore;
		return sumScore;
	}
	
	public String toQuestion() {
		if (profile == null) return "";
		int len = profile[0].length;
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < profile[0].length; i++) {
			sb.append("?");
		}
		return sb.toString();
	}

	public static boolean dupa = false;
	
	/**
	 * @return Arrays (position and value) of max for every period <code>tandemRepeatSize</code>
	 */
	public static int getMaxPosWithFiltering(
		float[][] m,
		int y,
		int start,
		int end,
		int tandemRepeatSize,
		int distanceFromDiagonal,
		int[] resultPos,
		float[] resultVal, float debugM[][]) {
			
		int k = 0;
		 
		int centery = y + distanceFromDiagonal;
		
		// filter the line
		float filteredLine[];
		filteredLine = Matrix.filterLine(m[y], start, end-start+1, tandemRepeatSize, centery, null);
		
		
/*		if (!dupa) {
			Matrix.saveImageGray(Matrix.paintHistogram(m[y], null, 100), true, "no_filter"+y);
			Matrix.saveImageGray(Matrix.paintHistogram(filteredLine, null, 100), true, "yes_filter"+y);
			Matrix.saveImageGray(
				Matrix.paintHistogram(
					Matrix.filterLine(Matrix.fill(Matrix.createEmpty(m[y]), 1f), start, end-start+1, tandemRepeatSize, centery, null),
					null, 100), 
				true, "schema_filter"+y);
		}*/
		
		Assert.assertTrue(start <= y && y <= end);
		
		for (int i = (start-centery)/tandemRepeatSize-1; i < (end-centery)/tandemRepeatSize; i++) {
			int regStart = centery+i*tandemRepeatSize+tandemRepeatSize/2;
			int regEnd = regStart+tandemRepeatSize-1;
			if (regEnd > start && regStart < end) {
				
				int left = Math.max(regStart, 0);
				int right =  Math.min(regEnd, filteredLine.length-1);
				
				if (debugM != null) {
					debugM[y][left] = -20; 
					debugM[y][right] = -20;
				}
				 
				resultPos[k] = Matrix.getMaxPos(filteredLine, left, right);
				resultVal[k] = Matrix.highestDiff(filteredLine, left, right);
				k++;
			}
		}
		return k;
	}

	public static int getMaxPosWithFilteringArraySize(
		int start,
		int end,
		int tandemRepeatSize) {
		return (end-start)/tandemRepeatSize+2;
	}

	public boolean isSequence() {
		return false;
	}

	public String toString() {
		throw new RuntimeException("Profile cannot be converted to String");
	}

	public String toStringRepresentation() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < profile.length; i++) {
			sb.append(subst.getNumChar(i));
			sb.append(" ");
			for (int j = 0; j < profile[i].length; j++) {
				int val = (int) (profile[i][j]*9);
				if (val == 0 && profile[i][j] != 0) val = 1;
				sb.append(val);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean isProfile() {
		return true;
	}
	
	public void randomize() {
		clearCache();
		int n = length();
		for (int i = 0; i < n; i++) {
			int j = i + ((int)(Math.random()*(n-i)));
			xchgColumns(i, j);
		}
	}

	/**
	 * remember to clearCache() before! 
	 */
	private void xchgColumns(int i, int j) {
		if (i != j) {
			for (int k = 0; k < profile.length; k++) {
				float v = profile[k][i];
				profile[k][i] = profile[k][j];
				profile[k][j] = v;
			}
		}
	}

	public boolean equals(Object o) {
		throw new RuntimeException("Profile.equals not implemented due to efficiency reasons");
	}

}
