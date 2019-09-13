package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;



/**
 * Class represents local alignment algorithm
 */
public class LocalAlignAffineNice extends AlignAffine {
	
	public float align(AlignData data) {
		return localAlignAffine((AlignDataAffineNice)data);
	}

	public float align(AlignData data, Trace trace) {
		return localAlignAffine((AlignDataAffineNice)data, trace);
	}

	private static short debugM[][];
	
	public float align(AlignData data, Trace t, short _debugM[][]) {
		debugM = _debugM;
		float v = localAlignAffine((AlignDataAffineNice)data, t);
		debugM = null;
		return v;
	}
	
	public static float localAlignAffine(AlignDataAffineNice data) {
		return localAlignAffine(data, null);
	}
	
	/**
	 * Fills in the matrix using precomputed position-specific scores.
	 * 
	 * @param gapStart matrix describes start of the gap. 
	 * <code>gapStart[y][x] = gs</code> means match (X[x],Y[y]) and:
	 * <bullets> 
	 * 		<bullet>gs > 0 -> (X[gs],Y[y-1])
	 * 		<bullet>gs < 0 -> (X[x-1],Y[-gs]) 
	 * </bullets>
	 */
	public static float localAlignAffine(AlignDataAffineNice data, Trace t) {
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqX().length() <= Short.MAX_VALUE);
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqY().length() <= Short.MAX_VALUE);
		float[][] matrix = data.getMatrix();
		float[][] gx = data.getGapX();
		float[][] gy = data.getGapY();
		float _gapO = data.getGapO();
		float _gapE = data.getGapE();
		final float GAP_STOP = AlignDataAffineNice.GAP_STOP;
		
		float maxValue = 0; // this is current maximal value of the matrix
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		//int yspan = t == null ? yLen : yLen /*t.end[tlen-1]*/ - t.end[0] + 1;
		
		// reset variables 
		int tracexStart[] = data.tracexStart;
		int tracexEnd[] = data.tracexEnd;
		int traceyStart; // where the trace starts
		int traceyEnd;	// where the trace ends
		
		for (int i = 0; i < tracexStart.length; i++) {
			tracexStart[i] = xLen+1; // a number big enough to skip any iteration...
			tracexEnd[i] = 0;
		}
		
		// a point (tracey[i],tracexmin[i]) has to be updated 
		if (t != null) {
			traceyStart = t.end[0];
			traceyEnd = t.end[t.end.length-1];
			//t.expand();
			int tlength = t.length();

			// put traces on the vertical vector
			// for every entry <x,y> in trace, update <x+1,y>, <x, y+1>, <x+1, y+1> 
			for (int i = tlength-1; i >=0; i--) {
				int ypos = t.end[i];
				tracexStart[ypos] = t.start[i]+1;  
				tracexEnd[ypos] = t.start[i]+1;  
				tracexStart[ypos+1] = t.start[i]; // Math.min  
				if (tracexEnd[ypos+1] < t.start[i]+1) tracexEnd[ypos+1] = t.start[i]+1;  
			}
		} else {
			traceyStart = 1;
			traceyEnd = matrix.length-1;
			for (int i = 1; i < tracexStart.length; i++) {
				tracexStart[i] = 1;
				tracexEnd[i] = matrix[0].length-1;
			}
		}
		
		// profile preparations
		int seqX[] = null, seqY[] = null;
		boolean seqXProfile = false, seqYProfile = false;
		
		// determine whether the seqX is a profile
		Profile profileX = null;
		Sequence sequenceY = null;
		if (data.getCmpSeqX().isSequence()) {
			seqX = data.getSubst().convertToCodes(data.getCmpSeqX().toString());
		} else {
			seqXProfile = true;
			profileX = (Profile) data.getCmpSeqX();
			sequenceY = (Sequence) data.getCmpSeqY();
		}
		
		// determine whether the seqY is a profile
		if (data.getCmpSeqY().isSequence()) {
			seqY = data.getSubst().convertToCodes(data.getCmpSeqY().toString());
		} else {
			seqYProfile = true;
		}
		
		Assert.assertTrue(!seqYProfile);
		
		float substM[][] = data.getSubst().getMatrix();
		
		// update according to the trace
		SubstitutionTable subst = data.getSubst();
		boolean updateDown = true;
		for (int y = traceyStart, 
				nextXStart = tracexStart[y], 
				nextXEnd = tracexEnd[y]; 
					(y <= traceyEnd+1 || updateDown) && y <= yLen; 
					y++) {
			
			updateDown = false;
			
			int xStart = nextXStart;
			int xEnd = nextXEnd;
			
			nextXStart = tracexStart[y+1];
			nextXEnd = tracexEnd[y+1];
			
			boolean updateRight = true;
			for (int x = xStart; 
					(x <= xEnd || updateRight) && x <= xLen; 
					x++) {
						
				updateRight = false;
				
				if (debugM != null) debugM[y][x]++;
				
				float myx = matrix[y][x];
				float substitution = -Matrix.INF;
				if (myx != -Matrix.INF) {
					if (seqXProfile) {
						substitution = profileX.scoreWithSequence(x-1, sequenceY, y-1, subst);
					} else {
						substitution = substM[seqX[x-1]][seqY[y-1]];//matrix[y][x];
					}
				}
					
				// TODO the following is a potential bug because
				// 0 should be taken instead of -INF
				float prevSubst = matrix[y-1][x-1];
				float prevGapX = gx[y-1][x-1];
				float prevGapY = gy[y-1][x-1];
				
				// choose larger value of gaps
				float prevGap;
				if (prevGapX >= prevGapY) 
					prevGap = prevGapX;
				else
					prevGap = prevGapY;
				// choose larger of gaps and subst
				float newValue = substitution + 
							( (prevSubst >= prevGap) ? prevSubst : prevGap );
				// local alignment
				if (newValue < 0) newValue = 0;
				// store the calculated value if not banned
				if (myx != newValue && myx != -Matrix.INF) {
					// update the rest if writing to the matrix is not banned
					myx = matrix[y][x] = newValue;
					updateRight = true;
					updateDown = true;
					if (x+1 > nextXEnd) nextXEnd = x+1;
					if (x < nextXStart) nextXStart = x;
				}
				
				if (newValue >= maxValue) 
					maxValue = newValue;
				
				float gyx;
				// update gx matrix
				prevSubst = matrix[y][x-1]+_gapO;
				prevGap = gx[y][x-1]+_gapE;
				newValue = (prevSubst >= prevGap) ? prevSubst : prevGap;
				if (newValue < 0) newValue = 0;
				gyx = gx[y][x];
				if (gyx != newValue && gyx != GAP_STOP) {
					gx[y][x] = newValue; 
					updateRight = true;
					updateDown = true;
					if (x+1 > nextXEnd) nextXEnd = x+1;
					if (x < nextXStart) nextXStart = x;
				}
				
				// update gx matrix
				prevSubst = matrix[y-1][x]+_gapO;
				prevGap = gy[y-1][x]+_gapE;
				newValue = (prevSubst >= prevGap) ? prevSubst : prevGap;
				if (newValue < 0) newValue = 0;
				gyx = gy[y][x];
				if (gyx != newValue && gyx != GAP_STOP) {
					gy[y][x] = newValue;
					updateRight = true;
					updateDown = true;
					if (x+1 > nextXEnd) nextXEnd = x+1;
					if (x < nextXStart) nextXStart = x;
				} 
			}
		}
		return Matrix.getMax(matrix);
	}

	/**
	 * Fills in the matrix using precomputed position-specific scores.
	 * 
	 * @param gapStart matrix describes start of the gap. 
	 * <code>gapStart[y][x] = gs</code> means match (X[x],Y[y]) and:
	 * <bullets> 
	 * 		<bullet>gs > 0 -> (X[gs],Y[y-1])
	 * 		<bullet>gs < 0 -> (X[x-1],Y[-gs]) 
	 * </bullets>
	 */
	public static float localAlignAffineold(AlignDataAffineNice data) {
		Assert.assertTrue("Seqence too long for the implementation", data.getSeqX().length() <= Short.MAX_VALUE);
		Assert.assertTrue("Seqence too long for the implementation", data.getSeqY().length() <= Short.MAX_VALUE);
		float[][] matrix = data.getMatrix();
		float[][] gx = data.getGapX();
		float[][] gy = data.getGapY();
		float _gapO = data.getGapO();
		float _gapE = data.getGapE();
		
		float maxValue = 0; // this is current maximal value of the matrix
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		int tracey[] = new int[matrix.length-1];
		int tracex[] = new int[matrix.length-1];
		for (int i = 0; i < tracey.length; i++) {
			tracey[i] = i+1;
			tracex[i] = 1;
		}
		
		for (int i = 0; i < tracey.length; i++) {
			
			// perform calc
			for (int y = 1; y <= yLen; y++) {
				
				for (int x = 1; x <= xLen; x++) {
					
					float substitution = matrix[y][x];
					float prevSubst = matrix[y-1][x-1];
					float prevGapX = gx[y-1][x-1];
					float prevGapY = gy[y-1][x-1];
					
					// choose larger value of gaps
					float prevGap;
					if (prevGapX >= prevGapY) 
						prevGap = prevGapX;
					else
						prevGap = prevGapY;
					// choose larger of gaps and subst
					float newValue = substitution + 
								( (prevSubst >= prevGap) ? prevSubst : prevGap );
					// local alignment
					if (newValue < 0) newValue = 0;
					// store the calculated value
					matrix[y][x] = newValue;
					
					if (newValue >= maxValue) 
						maxValue = newValue;
					
					// update gx matrix
					prevSubst = matrix[y][x-1]+_gapO;
					prevGap = gx[y][x-1]+_gapE;
					gx[y][x] = (prevSubst >= prevGap) ? prevSubst : prevGap;
					
					// update gx matrix
					prevSubst = matrix[y-1][x]+_gapO;
					prevGap = gy[y-1][x]+_gapE;
					gy[y][x]= (prevSubst >= prevGap) ? prevSubst : prevGap;
					
				}
			}
		}
		return Matrix.getMax(matrix);
	}

	public AlignDataAffine createData(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst) {
		return new AlignDataAffineNice(seqX, seqY, gapO, gapX, subst);
	}
	
	public AlignDataAffine createData(ComparableSequence seqX, ComparableSequence seqY, float gapO, float gapX, SubstitutionTable subst) {
		if (seqX.isSequence() && seqY.isSequence()) {
			return new AlignDataAffineNice(seqX.toString(), seqY.toString(), gapO, gapX, subst);
		}
		return new AlignDataAffineNice((Profile)seqX, (Sequence)seqY, gapO, gapX, subst);
	}
	
}
