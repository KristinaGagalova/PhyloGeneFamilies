package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;



/**
 * Class represents local alignment algorithm
 */
public class LocalAlignAffineWrapa extends AlignAffine {
	
	public float align(AlignData data) {
		return localAlignAffine((AlignDataAffineGotoh)data);
	}

	public static float localAlignAffine(AlignDataAffine data) {
		return localAlignAffine(data, false);
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
	public static float localAlignAffine(AlignDataAffine _data, boolean wrapAround) {
		AlignDataAffineGotoh data = (AlignDataAffineGotoh) _data;
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqX().length() <= Short.MAX_VALUE);
		Assert.assertTrue("Seqence too long for the implementation", data.getCmpSeqY().length() <= Short.MAX_VALUE);
		float[][] matrix = data.getMatrix();
		float[][] gapX = Matrix.createEmpty(matrix);
		float[][] gapY = Matrix.createEmpty(matrix);
		short[][] gapStart = data.getGapMatrix();
		short[][] gapEndX = data.getGapEndMatrixX();
		short[][] gapEndY = data.getGapEndMatrixY();
		float _gapO = data.getGapO();
		float _gapE = data.getGapE();
		data.setWrapAround(wrapAround);
		SubstitutionTable subst = data.getSubst();
		Profile profileX = null;
		Sequence cmpSeqY = null;
		String seqX = null;
		if (data.getCmpSeqX().isProfile() || data.getCmpSeqY().isProfile()) {
			Assert.assertTrue(data.getCmpSeqX().isProfile() && data.getCmpSeqY().isSequence());
			profileX = (Profile) data.getCmpSeqX();
			cmpSeqY = (Sequence) data.getCmpSeqY();
		} else {
			// seqX
			seqX = data.getSeqX();
		}
		Assert.assertTrue(data.getCmpSeqY().isSequence());
		String seqY = data.getSeqY();
		
		Matrix.clear(gapStart);
		
		int yLen = matrix.length-1;
		int xLen = matrix[0].length-1;
		
		// gapY[x] - alignment finished with gap in X
		// ......X[x].... -  
		// ..............Y[y]
		short gapXStart[][] = new short[matrix.length][matrix[0].length];
		short gapYStart[][] = new short[matrix.length][matrix[0].length];
		
		// perform calc
		for (int y = 1; y <= yLen; y++) {
			
			// gapX corrensponds to alignment ending with "-"
			// ..............X[x]
			// ......Y[y].... -  
				
			for (int iter = 1; iter <= (wrapAround ? 2 : 1); iter++) {
				
				if (iter == 2) {
					Assert.assertTrue(wrapAround);
					// prepare the second iteration
					matrix[y][0] = matrix[y][xLen];
					gapStart[y][0] = gapStart[y][xLen];
					//gapX[y-1][0] = gapX[y-1][xLen];
					gapY[y][0] = gapY[y][xLen];
					gapYStart[y][0] = gapYStart[y][xLen];
					gapX[y][0] = gapX[y][xLen];
					gapXStart[y][0] = gapXStart[y][xLen];
					// gapXStart propagates itself to the next iteration
				}
				
				for (int x = 1; x <= xLen; x++) {
					
					float scoreChar;
					
					if (matrix[y][x] > -Matrix.INF) {
						if (profileX == null) {
							scoreChar = subst.getValue(seqX.charAt(x-1), seqY.charAt(y-1));
						} else {
							scoreChar = profileX.scoreWithSequence(x-1, cmpSeqY, y-1, subst);
						}
					} else {
						scoreChar = -Matrix.INF;
					}
					// assertion: matrix[y-1][x-1] >= 0
					float diag = matrix[y-1][x-1]+scoreChar;
					
					// gapX corrensponds to alignment ending with "-"
					// ..............X[x-1]
					// ......Y[y-1]... -  
					float extx = gapX[y-1][x-1]+scoreChar;
					
					// gapY[x-1] - alignment finished with gap in X
					// ......X[x-1]   -   
					// ......Y[y-2] Y[y-1]
					float exty = gapY[y-1][x-1]+scoreChar;
					
					if (0 >= diag && 0 >= extx && 0 >= exty) {
						matrix[y][x] = 0;
						gapStart[y][x] = 0; // any global stuff will sneak in -Matrix.INF - warining!
					} else {
						if (diag >= extx && diag >= exty) {
							matrix[y][x] = diag;
							gapStart[y][x] = 0;
						} else if (extx >= diag && extx >= exty) {
							matrix[y][x] = extx;
							gapStart[y][x] = (short)gapXStart[y-1][x-1];
						} else if (exty >= diag && exty >= extx) {
							matrix[y][x] = exty;
							gapStart[y][x] = (short)-gapYStart[y-1][x-1];
						} else {
							throw new RuntimeException("Not really");
						}
					}
					
					// a new gap may be open in X
					// .........X[x]    -  
					// .........Y[y-1] Y[y]
					if (y>1) {
						float gapYCont = gapY[y-1][x]+_gapE;
						float gapYNew = matrix[y-1][x]+_gapO;
					
						if (gapYCont > gapYNew) {
							gapY[y][x] = gapYCont;
							gapYStart[y][x] = gapYStart[y-1][x];
						} else {
							gapY[y][x] = gapYNew;
							gapYStart[y][x] = (short) (y-1); // the value stores the last matched pair
						}
					}
				
					// a new gap may be open in Y
					// .........X[x-1] X[x]
					// .........Y[y]    -  
					float gapXCont = gapX[y][x-1]+_gapE;
					float gapXNew = matrix[y][x-1]+_gapO; // matrix[y][x] is next iteration's matrix[y][x-1]
				
					if (gapXCont > gapXNew) {
						gapX[y][x] = gapXCont;
						gapXStart[y][x] = gapXStart[y][x-1];
					} else {
						gapX[y][x] = gapXNew;
						gapXStart[y][x] = (short)(x-1);
					}
				}
				
			}
		}
		return Matrix.getMax(matrix);
	}
	/*
	public static float localAlignAffineUpdate(AlignDataAffineGotoh data, Trace trace) {
		return 0f;Matrix.getMax(matrix);
	}*/
		


	public AlignDataAffine createData(String seqX, String seqY, float gapO, float gapX, SubstitutionTable subst) {
		return new AlignDataAffineGotoh(seqX, seqY, gapO, gapX, subst);
	}

	public AlignDataAffine createData(ComparableSequence seqX, ComparableSequence seqY, float gapO, float gapX, SubstitutionTable subst) {
		return new AlignDataAffineGotoh((Profile)seqX, (Sequence)seqY, gapO, gapX, subst);
	}
	

}
