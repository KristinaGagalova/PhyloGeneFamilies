package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;



/**
 * Class represents local alignment algorithm
 */
public class LocalAlignAffineGotoh extends AlignAffine {
	
	public float align(AlignData data) {
		return localAlignAffine((AlignDataAffineGotoh)data);
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
	public static float localAlignAffine(AlignDataAffineGotoh data) {
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqX().length() <= Short.MAX_VALUE);
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqY().length() <= Short.MAX_VALUE);
		float[][] matrix = data.getMatrix();
		short[][] gapStart = data.getGapMatrix();
		short[][] gapEndX = data.getGapEndMatrixX();
		short[][] gapEndY = data.getGapEndMatrixY();
		float _gapO = data.getGapO();
		float _gapE = data.getGapE();
		
		Matrix.clear(gapStart);
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		// gapY[x] - alignment finished with gap in X
		// ......X[x].... -  
		// ..............Y[y]
		float gapY[] = new float[matrix[0].length];
		short gapYStart[] = new short[gapY.length];
		for (int x = 0; x < matrix[0].length; x++) {
			gapY[x] = -Matrix.INF;
			gapYStart[x] = 0;
		}
		
		// perform calc
		for (int y = 1; y <= yLen; y++) {
			
			// gapX corrensponds to alignment ending with "-"
			// ..............X[x]
			// ......Y[y].... -  
			float gapX = -Matrix.INF;
			short gapXStart = 0;
			
			for (int x = 1; x <= xLen; x++) {
				
				// assertion: matrix[y-1][x-1] >= 0
				float subst = matrix[y-1][x-1]+matrix[y][x];
				
				// gapX corrensponds to alignment ending with "-"
				// ..............X[x-1]
				// ......Y[y-1]... -  
				float extx = gapX+matrix[y][x];
				
				// gapY[x-1] - alignment finished with gap in X
				// ......X[x-1]   -   
				// ......Y[y-2] Y[y-1]
				float exty = gapY[x-1]+matrix[y][x];
				
				if (0 >= subst && 0 >= extx && 0 >= exty) {
					matrix[y][x] = 0;
					gapStart[y][x] = 0;
//					dir[y][x] |= AlignData.DIR_NONE;
				} else {
					if (subst >= extx && subst >= exty) {
						matrix[y][x] = subst;
						gapStart[y][x] = 0;
//						dir[y][x] |= AlignData.DIR_UP_LEFT;
					} else if (extx >= subst && extx >= exty) {
						matrix[y][x] = extx;
						gapStart[y][x] = (short)gapXStart;
						if (gapEndX != null) gapEndX[y-1][gapXStart] = (short)x; 
//						dir[y][x] |= AlignData.DIR_LEFT;
					} else if (exty >= subst && exty >= extx) {
						matrix[y][x] = exty;
						gapStart[y][x] = (short)-gapYStart[x-1];
						if (gapEndY != null) gapEndY[gapYStart[x-1]][x-1] = (short)y; 
//						dir[y][x] |= AlignData.DIR_UP;
					} else {
						throw new RuntimeException("Not really");
					}
				}
				
				// a new gap may be open in X
				// .........X[x]    -  
				// .........Y[y-1] Y[y]
				if (y>1) {
					float gapYCont = gapY[x]+_gapE;
					float gapYNew = matrix[y-2][x]+_gapO;
					
					if (gapYCont > gapYNew) {
						gapY[x] = gapYCont;
					} else {
						gapY[x] = gapYNew;
						gapYStart[x] = (short) (y-2); // the value stores the last matched pair
					}
				}
				
				// a new gap may be open in Y
				// .........X[x-1] X[x]
				// .........Y[y]    -  
				float gapXCont = gapX+_gapE;
				float gapXNew = matrix[y-1][x-1]+_gapO; // matrix[y][x] is next iteration's matrix[y][x-1]
				
				if (gapXCont > gapXNew) {
					gapX = gapXCont;
				} else {
					gapX = gapXNew;
					gapXStart = (short)(x-1);
				}
				
			}
		}
		return Matrix.getMax(matrix);
	}
	/*
	public static float localAlignAffineUpdate(AlignDataAffineGotoh data, Trace trace) {
		return 0f;Matrix.getMax(matrix);
	}*/
		

	/**
	 * Fills in the matrix using precomputed position-specific scores.
	 * No trace matrix calculation.
	 */
	static float localAlignAffineOld(AlignData data) {
		
		float[][] matrix = data.getMatrix();
		float _gapO = data.getGapO();
		float _gapE = data.getGapE();
		
		// check params
		Assert.assertTrue(_gapE == _gapO);
		
		data.clearDir();
//		Matrix.clear(dir);
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		// gapY[x] - alignment finished with gap in X
		// ......X[x].... -  
		// ..............Y[y]
		float gapY[] = new float[matrix[0].length];
		for (int x = 0; x < matrix[0].length; x++) {
			gapY[x] = -Matrix.INF;
		}
		
		// perform calc
		for (int y = 1; y <= yLen; y++) {
			
			// gapX corrensponds to alignment ending with "-"
			// ..............X[x]
			// ......Y[y].... -  
			float gapX = -Matrix.INF;
			
			for (int x = 1; x <= xLen; x++) {
				
				// assertion: matrix[y-1][x-1] >= 0
				float subst = matrix[y-1][x-1]+matrix[y][x];
				
				// gapX corrensponds to alignment ending with "-"
				// ..............X[x-1]
				// ......Y[y-1]... -  
				float extx = gapX+matrix[y][x];
				
				// gapY[x-1] - alignment finished with gap in X
				// ......X[x-1]   -   
				// ......Y[y-2] Y[y-1]
				float exty = gapY[x-1]+matrix[y][x];
				
				if (0 >= subst && 0 >= extx && 0 >= exty) {
					matrix[y][x] = 0;
//					dir[y][x] |= AlignData.DIR_NONE;
				} else {
					if (subst >= extx && subst >= exty) {
						matrix[y][x] = subst;
//						dir[y][x] |= AlignData.DIR_UP_LEFT;
					} else if (extx >= subst && extx >= exty) {
						matrix[y][x] = extx;
//						dir[y][x] |= AlignData.DIR_LEFT;
					} else if (exty >= subst && exty >= extx) {
						matrix[y][x] = exty;
//						dir[y][x] |= AlignData.DIR_UP;
					}
				}
				
				// a new gap may be open in X
				// .........X[x]    -  
				// .........Y[y-1] Y[y]
				if (y>1) {
					float gapYCont = gapY[x]+_gapE;
					float gapYNew = matrix[y-2][x]+_gapO;
					
					if (gapYCont > gapYNew) {
						gapY[x] = gapYCont;
					} else {
						gapY[x] = gapYNew;
					}
				}
				
//				if (y>1)
//					gapY[x] = Math.max(gapY[x]+_gapE, matrix[y-2][x]+_gapO);
				
				// a new gap may be open in Y
				// .........X[x-1] X[x]
				// .........Y[y]    -  
//				gapX = Math.max(gapX+_gapE, matrix[y-1][x-1]+_gapO); // matrix[y][x] is next iteration's matrix[y][x-1]
				float gapXCont = gapX+_gapE;
				float gapXNew = matrix[y-1][x-1]+_gapO; // matrix[y][x] is next iteration's matrix[y][x-1]
				
				if (gapXCont > gapXNew) {
					gapX = gapXCont;
				} else {
					gapX = gapXNew;
				}
				
			}
		}
		return Matrix.getMax(matrix);
	}

	public AlignDataAffine createData(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst) {
		return new AlignDataAffineGotoh(seqX, seqY, gapO, gapX, subst);
	}

	public AlignDataAffine createData(ComparableSequence seqX, ComparableSequence seqY, float gapO, float gapX, SubstitutionTable subst) {
		return new AlignDataAffineGotoh((Profile)seqX, (Sequence)seqY, gapO, gapX, subst);
	}
	

}
