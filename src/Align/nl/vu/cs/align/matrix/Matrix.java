package nl.vu.cs.align.matrix;

import java.awt.image.*;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;

import nl.vu.cs.align.*;

import com.keypoint.*;
import com.sun.image.codec.jpeg.*;

public class Matrix {

	public static int INF = 10000;
	
	// used to compare floats
	public static final float FLOAT_PRECISION = 0.0001f;
	
	public static float[] copy(float []m) {
		float []aCopy = new float[m.length];
		copy(aCopy, m);
		return aCopy;
	}

	public static float[][] copy(float [][]m) {
		float [][]aCopy = new float[m.length][m[0].length];
		copy(aCopy, m);
		return aCopy;
	}

	public static byte[][] copy(byte [][]m) {
		byte [][]aCopy = new byte[m.length][m[0].length];
		copy(aCopy, m);
		return aCopy;
	}

	/**
	 * m1 := m2
	 */	
	public static void copy(float []m1, float []m2) {
		Assert.assertTrue(m1.length == m2.length);
		int len = m1.length;
		for (int x = 0; x < len; x++) {
			m1[x] = m2[x];
		}
	}
	
	/**
	 * m1 := m2
	 */	
	public static void copy(float [][]m1, float [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m1[y][x] = m2[y][x];
			}
		}
	}
	
	/**
	 * m1 := m2
	 */	
	public static void copy(byte [][]m1, byte [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m1[y][x] = m2[y][x];
			}
		}
	}
	
	public static float[] fill(float []m, float value) {
		for (int i = 0; i < m.length; i++) {
			m[i] = value;
		}
		return m;
	}
	
    public static float[][] fill(float [][]m, float value) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j]= value;
            }
        }
        return m;
    }
    
	public static float[] createEmpty(float []m) {
		float []aNew = new float[m.length];
		return aNew;
	}
	
	public static float[][] createEmpty(float [][]m) {
		float [][]aNew = new float[m.length][m[0].length];
		return aNew;
	}
	
	public static short[][] createEmpty(short [][]m) {
		short [][]aNew = new short[m.length][m[0].length];
		return aNew;
	}
	
	public static void print(float[][] m) {
		for (int y = 0; y < m.length; y++) {
			for (int x = 0; x < m[0].length; x++) {
				System.out.print(m[y][x]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	public static void print(float[] m) {
		for (int x = 0; x < m.length; x++) {
			System.out.print(m[x]);
			System.out.print("\t");
		}
	}
	
	/**
	 * Return (x, y) position of the maximal value 
	 */
	public static int[] getMaxPos(float[][] m) {
		// pick the result
		float result = m[0][0];
		int maxx = 0, maxy = 0;
		int ylen = m.length;
		int xlen = m[0].length;
		for (int y = 0; y < ylen; y++) {
			for (int x = 0; x < xlen; x++) {
				float val = m[y][x];
				if (val > result) {
					result = val;
					maxx = x;
					maxy = y;
				}
			}
		}
		return new int[] {maxx, maxy};
	}
	
	/**
	 * Return (x, y) position of the maximal value, searching the matrix
	 * starting at given positions. 
	 */
	public static int[] getMaxPos(float[][] m, int startx, int starty) {
		int maxx = startx, maxy = starty;
		float result = m[starty][startx];
		int ylen = m.length;
		int xlen = m[0].length;
		for (int y = starty; y < ylen; y++) {
			for (int x = startx; x < xlen; x++) {
				float val = m[y][x];
				if (val > result) {
					result = val;
					maxx = x;
					maxy = y;
				}
			}
		}
		return new int[] {maxx, maxy};
	}
	
	/**
	 * Return the first position of the maximal value 
	 * 
	 * The result is 0-based!
	 */
	public static int getMaxPos(float[] m) {
		// pick the result
		float result = m[0];
		int maxx = 0;
		int xlen = m.length;
		for (int x = 1; x < xlen; x++) {
			float val = m[x];
			if (val > result) {
				result = val;
				maxx = x;
			}
		}
		return maxx;
	}
	
	/**
	 * Return the maximal value withing the range defined
	 * by parameters <code>first</code> and <code>last</code> (inclusive). 
	 */
	public static float getMax(float[] m, int first, int last) {
		// pick the result
		float result = m[first];
		for (int x = first+1; x <= last; x++) {
			float val = m[x];
			if (val > result) {
				result = val;
			}
		}
		return result;
	}
	
	public static float getMax(float[] m) {
		return getMax(m, 0, m.length-1);
	}
	
	/**
	 * Return the maximal value withing the range defined
	 * by parameters <code>first</code> and <code>last</code> (inclusive). 
	 */
	public static int getMax(int[] m, int first, int last) {
		// pick the result
		int result = m[first];
		for (int x = first+1; x <= last; x++) {
			int val = m[x];
			if (val > result) {
				result = val;
			}
		}
		return result;
	}
	
	public static int getMax(int[] m) {
		return getMax(m, 0, m.length-1);
	}
	
	/**
	 * Return the first position of the maximal value withing the range defined
	 * by parameters <code>first</code> and <code>last</code> (inclusive). 
	 * 
	 * The result is 0-based!
	 */
	public static int getMaxPos(float[] m, int first, int last) {
		first = first < 0 ? 0 : first;
		last = last > m.length-1 ? m.length-1 : last;
		// pick the result
		float maxv = m[first];
		int maxx = first;
		for (int x = first+1; x <= last; x++) {
			float val = m[x];
			if (val >= maxv) {
				maxv = val;
				maxx = x;
			}
		}
		return maxx;
	}
	
	public static float getMax(float [][]m) {
		int ylen = m.length;
		int xlen = m[0].length;
		float result = m[0][0];
		for (int y = 0; y < ylen; y++) {
			for (int x = 0; x < xlen; x++) {
				if (m[y][x] > result) {
					result = m[y][x];
				}
			}
		}
		return result;
	}
	
	public static float getMin(float [][]m) {
		int ylen = m.length;
		int xlen = m[0].length;
		float result = m[0][0];
		for (int y = 0; y < ylen; y++) {
			for (int x = 0; x < xlen; x++) {
				if (m[y][x] < result) {
					result = m[y][x];
				}
			}
		}
		return result;
	}
	
	/**
	 * All sizes of r, g, b matrices has to be equal.
	 * All entries has to be in min..max range.
	 */
	public static void saveImageRGB(float [][]r, float [][]g, float [][]b, boolean invert, String fname) {
		// create the image from m
		float min = Float.POSITIVE_INFINITY;
		if (r != null) min = Math.min(min, Matrix.getMin(r));
		if (g != null) min = Math.min(min, Matrix.getMin(g));
		if (b != null) min = Math.min(min, Matrix.getMin(b));
		
		float max = Float.NEGATIVE_INFINITY;
		if (r != null) max = Math.max(max, Matrix.getMax(r));
		if (g != null) max = Math.max(max, Matrix.getMax(g));
		if (b != null) max = Math.max(max, Matrix.getMax(b));
		
		saveImageRGBMinMax(r,g,b,min,max,invert,fname);
	}

	/**
	 * All sizes of r, g, b matrices has to be equal.
	 * All entries has to be in min..max range.
	 */
	public static void saveImageRGBMinMax(float [][]r, float [][]g, float [][]b, float min, float max, boolean invert, String fname) {
		// create the image from m
		float [][]m = null;
		if (r != null) m = r;
		else if (g != null) m = g;
		else if (b != null) m = b;
		
		if (m == null) throw new RuntimeException("Cannot save empty matrices");
		float span;
		if (max != min) span = max-min;
		else span = 1f;
		
		int lenx = m[0].length;
		int leny = m.length;
		BufferedImage bufferedImage = new BufferedImage(lenx, leny, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < lenx; x++) {
			for (int y = 0; y < leny; y++) {
					int rv = 0;
					int gv = 0;
					int bv = 0;
					if (r != null) rv = translateValue(r[y][x], min, span);
					if (g != null) gv = translateValue(g[y][x], min, span);
					if (b != null) bv = translateValue(b[y][x], min, span);
					if (invert) {
						rv = 255-rv;
						gv = 255-gv;
						bv = 255-bv;
					}
					int rgbv = (rv<<16) | (gv<<8) | bv;
					bufferedImage.setRGB(x, y, rgbv);
			}
		}
		
		writePNG(bufferedImage, fname);
	}

	/**
	 * @param r
	 * @param x
	 * @param y
	 * @param min
	 * @param span
	 * @return
	 */
	private static int translateValue(float value, float min, float span) {
		if (value < min) value = min;
		return Math.round(((value-min)/span) * 255);
	}

	static int JPEG_QUALITY = 90;
	
	private static void writeJPG(BufferedImage bufferedImage, String fname) {
		try {
			// write the image
		    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fname+".jpg"));
		    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
	//	    quality = Math.max(0, Math.min(quality, 100));
		    param.setQuality(JPEG_QUALITY, false);
		    encoder.setJPEGEncodeParam(param);
		    encoder.encode(bufferedImage);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.toString());
		}
	}
	
	private static void writePNG(BufferedImage bufferedImage, String fname) {
		try{
			PngEncoderB png =  new PngEncoderB(bufferedImage,
				PngEncoder.NO_ALPHA,
				0 /*0=none, 1=sub, 2=up*/, 1 /* 1-9 */);
			
			// write the image
		    FileOutputStream outfile = new FileOutputStream(fname+".png");
			byte []pngbytes = png.pngEncode();
			if (pngbytes == null) {
				throw new RuntimeException("Failed to save PNG file");
			} else {
				outfile.write( pngbytes );
			}
			outfile.flush();
			outfile.close();
			
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.toString());
		}
	}
	
	public static void saveImageGrayMinMax(float [][]m, float min, float max, boolean invert, String fname) {
		saveImageRGBMinMax(m, m, m, min, max, invert, fname);
	}
	
	public static void saveImageGray(float [][]m, String fname) {
		saveImageRGBMinMax(m, m, m, getMin(m), getMax(m), false, fname);
	}
	
	public static void saveImageGray(float [][]m, boolean invert, String fname) {
		saveImageRGBMinMax(m, m, m, getMin(m), getMax(m), invert, fname);
	}
	
	/**
	 * Mirrors top right tringle into bottom left one
	 */
	public static void mirrorTopRight(float[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		Assert.assertTrue(lenx == leny);
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				if (x > y) {
					m[x][y] = m[y][x];
				}
			}
		}
	}

	public static void clearBottomLeft(float[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		Assert.assertTrue(lenx == leny);
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				if (y > x) {
					m[y][x] = 0;
				}
			}
		}
	}

	public static void clearTopRight(float[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		Assert.assertTrue(lenx == leny);
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				if (y < x) {
					m[y][x] = 0;
				}
			}
		}
	}

	public static void clear(short[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m[y][x] = 0;
			}
		}
	}

	public static void clear(float[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m[y][x] = 0;
			}
		}
	}

	public static void clear(byte[][] m) {
		int lenx = m[0].length;
		int leny = m.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m[y][x] = 0;
			}
		}
	}

	public static void clearDiagonal(float[][] m) {
		int len = Math.min(m.length, m[0].length);
		for (int y = 0; y < len; y++) {
			m[y][y] = 0f;
		}
	}

	/**
	 * Scales every entry from the matrix to min..max
	 */
	public static void scale(float[][] m, float desired_min, float desired_max) {
		int lenx = m[0].length;
		int leny = m.length;
		float curr_min = getMin(m);
		float curr_max = getMax(m);
		if (curr_min < curr_max) {
			float curr_range = curr_max - curr_min;
			float desired_range = desired_max - desired_min;
			float scale = desired_range / curr_range;
			for (int y = 0; y < leny; y++) {
				for (int x = 0; x < lenx; x++) {
					m[y][x] = (m[y][x] - curr_min) * scale + desired_min;
				}
			}
		} else {
			for (int y = 0; y < leny; y++) {
				for (int x = 0; x < lenx; x++) {
					m[y][x] = desired_min;
				}
			}
		}
	}
	
	/**
	 * m1 -= m2
	 */
	public static void subtract(float[][] m1, float [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m1[y][x] -= m2[y][x];
			}
		}
	}

	/**
	 * m1 += m2
	 */
	public static void add(float[][] m1, float [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m1[y][x] += m2[y][x];
			}
		}
	}

	/**
	 * m1 min= m2
	 */
	public static void min(float[][] m1, float [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				if (m1[y][x] > m2[y][x]) m1[y][x] = m2[y][x];
			}
		}
	}

	/**
	 * m1 max= m2
	 */
	public static void max(float[][] m1, float [][]m2) {
		Assert.assertTrue(m1.length == m2.length);
		Assert.assertTrue(m1[0].length == m2[0].length);
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				if (m1[y][x] < m2[y][x]) m1[y][x] = m2[y][x];
			}
		}
	}

	public static boolean equalsTo(float[][] m, float[][] p) {
		if (m.length != p.length || m[0].length != p[0].length) 
			return false;
			
		for (int y = 0; y < m.length; y++) {
			for (int x = 0; x < m[0].length; x++) {
				if (m[y][x] != p[y][x])
					return false;
			}
		}
		
		return true;
	}
	
	public static boolean equalsTo(float[] m, float[] p, float precision) {
		if (m.length != p.length) 
			return false;
			
		for (int x = 0; x < m.length; x++) {
			if (Matrix.equalTo(m[x], p[x], precision))
				return false;
		}
		
		return true;
	}
	
	public static boolean equalsTo(float[] m, float[] p) {
		if (m.length != p.length) 
			return false;
			
		for (int x = 0; x < m.length; x++) {
			if (!Matrix.equalTo(m[x], p[x], Matrix.FLOAT_PRECISION))
				return false;
		}
		
		return true;
	}
	
	public static boolean equalsTo(int[] m, int[] p) {
		if (m.length != p.length) 
			return false;
			
		for (int x = 0; x < m.length; x++) {
			if (m[x] != p[x])
				return false;
		}
		
		return true;
	}
	
	public static boolean equalTo(float a, float b) {
		return equalTo(a, b, Matrix.FLOAT_PRECISION);
	}

	public static boolean equalTo(float a, float b, float margin) {
		return b >= a-margin && b <= a+margin;
	}

	/**
	 * Swaps top-left part of part of matrix f[off..f.length][off..f.length] 
	 * with bottom right. Starting offset for this transposition is off
	 */
	public static void swapTopLeft(float[][] f, int off) {
		Assert.assertTrue(f.length == f[0].length);
		int len = f.length;
		for (int y = off; y < len; y++) {
			for (int x = off; x < len-y+off; x++) {
				float tmp = f[y][x];
				int ny = len-1-x+off;
				int nx = len-1-y+off;
				f[y][x] = f[ny][nx];
				f[ny][nx] = tmp;
			}
		}
	}

	/**
	 * Method fillInWithTrace.
	 * @param m Matrix to fill in
	 * @param trace Trace to follow
	 * @param value Fills in with value
	 */
	public static void fillInWithTrace(
		float[][] m,
		int[][] trace,
		float value) {
			Assert.assertTrue(trace[0].length == trace[1].length);
			int len = trace[0].length;
			for (int i = 0; i < len; i++) {
				m[trace[1][i]][trace[0][i]] = value;
			}
	}

	public static void decorate(float[][] m) {
		decorate(m, false);
	}
	
	public static void decorate(float[][] m, boolean diag) {
		if (m.length <=5 || m[0].length <= 5) return; 
		float max = Matrix.getMax(m);
		if (max == 0) max = 1; // a quick fix for case of matrix of 0's
		for (int i = 1; i < m[0].length; i++) {
			m[0][i] = max;
		}
		for (int i = 1; i < m[0].length; i += 10) {
			m[1][i] = max;
		}
		for (int i = 1; i < m[0].length; i += 100) {
			m[2][i] = max;
			m[3][i] = max;
		}
		for (int i = 1; i < m[0].length; i += 1000) {
			m[4][i] = max;
			m[5][i] = max;
		}
		for (int i = 1; i < m.length; i++) {
			m[i][0] = max;
		}
		for (int i = 1; i < m.length; i += 10) {
			m[i][1] = max;
		}
		for (int i = 1; i < m.length; i += 100) {
			m[i][2] = max;
			m[i][3] = max;
		}
		for (int i = 1; i < m.length; i += 1000) {
			m[i][4] = max;
			m[i][5] = max;
		}
		if (diag) {
			for (int i = 1; i < Math.min(m.length, m[0].length); i += 1) {
				m[i][i] = max;
			}
		}
	}
	
	public static void decorateWithGrid(float[][] m) {
		float max = Matrix.getMax(m);
		if (max == 0) max = 1; // a quick fix for case of matrix of 0's
		for (int i = 0; i < m[0].length; i += 10) {
			drawVLine(m, i, 1, i, m.length-1, max);
		}
		for (int i = 0; i < m.length; i += 10) {
			drawHLine(m, 1, i, m[i].length-1, i, max);
		}
	}
	
	public static void drawLine(float [][]m, int x1, int y1, int x2, int y2, float v) {
		Assert.assertTrue(x1 == x2);
		int x = x1;
		y1 = Math.min(y1, y2);
		y2 = Math.max(y1, y2);
		for (int y = y1; y <= y2; y++) {
			m[y][x] = v;
		}
	}

	public static void drawVLine(float [][]m, int x1, int y1, int x2, int y2, float v) {
		Assert.assertTrue(x1 == x2);
		int x = x1;
		y1 = Math.min(y1, y2);
		y2 = Math.max(y1, y2);
		for (int y = y1; y <= y2; y++) {
			m[y][x] = v;
		}
	}

	public static void drawHLine(float [][]m, int x1, int y1, int x2, int y2, float v) {
		Assert.assertTrue(y1 == y2);
		int y = y1;
		x1 = Math.min(x1, x2);
		x2 = Math.max(x1, x2);
		for (int x = x1; x <= x2; x++) {
			m[y][x] = v;
		}
	}

	public static boolean isMirrored(float[][] m) {
		int y = (int) (Math.random()*m.length);
		for (int x = 0; x < m[0].length; x++) {
			if (m[y][x] != m[x][y]) 
				return false;
		}
		return true;
	}

	public static float[] createMovingAverage(float[] m, int window) {
		float []mAvg = createMovingAverageNoCorrection(m, window);
		
		// correct the values in the matrix, so the sum will be the same
		correctAddToSum(mAvg, Matrix.sum(m));
		return mAvg;
	}
	
	/** Adds/subtracts value from each entry of the matrix, so the
	 * resulting sum of entries is <code>avg</code> 
	 *
	 * @param m
	 * @param avg
	 * @return
	 */
	public static void correctAddToSum(float[] m, float sumDest) {
		float correction = (sumDest - Matrix.sum(m)) / m.length; 
		for (int i = 0; i < m.length; i++) {
			 m[i] += correction;
		}
	}
	
	/** Scales (multiplies) value from each entry of the matrix, so the
	 * resulting sum of entries is <code>avg</code> 
	 *
	 * @param m
	 * @param avg
	 * @return
	 */
	public static void correctScaleToSum(float[] m, float sumDest) {
		float sum = Matrix.sum(m);
		if (sum != 0f) {
			float correction = sumDest/sum; 
			for (int i = 0; i < m.length; i++) {
				 m[i] *= correction;
			}
		}
	}
	
	public static float[] createMovingAverageNoCorrection(float[] m, int window) {
		Assert.assertTrue(window > 0);
		window = Math.min(window, m.length);
		float mAvg[] = Matrix.createEmpty(m);
		int halfFloor = window/2;
		int halfCeiling = (window+1)/2;
		int lookBack = halfCeiling;
		int lookForward = halfFloor;
		
		// calculate the avg for the start of the matrix
		float a = 0f; // average
		for (int i = 0; i < window; i++) {
			a += m[i];
		}
		
		if (a >= 0) {
		} else {
			// BUG System.err.println("Warning: avg value < 0:"+a);
		}

		// fill in the matrix
		for (int i = 0; i < m.length; i++) {
			if (i >= lookBack && i < m.length-lookForward) {
				a -= m[i-lookBack];
				a += m[i+lookForward];
			}
			mAvg[i] = a/window;
		}
		return mAvg;
	}
	
	public static float sum(float []m) {
		float sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum;
	}

	/**
	 * Projects the matrix onto a vector.
	 * @return Projected vector
	 */
	public static float[] projectMaxE(float[][] m, int startx, int starty, int lenx, int leny) {
		float[] hproject = new float[leny];
		for (int y = starty; y < starty+leny; y++) {
			hproject[y-starty] = m[y][startx];
		}
		
		for (int y = starty; y < starty+leny; y++) {
			for (int x = startx+1; x < startx+lenx; x++) {
				hproject[y-starty] = Math.max(hproject[y-starty], m[y][x]);
			}
		}
		return hproject;
	}

	public static float[] projectMaxE(float[][] m) {
		return projectMaxE(m, 0, 0, m[0].length, m.length);
	}
	
	/**
	 * Projects the matrix onto a vector.
	 * @return Projected vector
	 */
	public static float[] projectE(float[][] m, int startx, int starty, int lenx, int leny) {
		float[] hproject = new float[leny];
		
		for (int y = starty; y < starty+leny; y++) {
			for (int x = startx; x < startx+lenx; x++) {
				hproject[y-starty] += m[y][x];
			}
		}
		return hproject;
	}
	
	/**
	 * Projects the matrix onto a vector.
	 * @return Projected vector
	 */
	public static float[] projectE(float[][] m) {
		return projectE(m, 0, 0, m[0].length, m.length);
	}
	
	/**
	 * Projects the matrix onto a vector.
	 * @return Projected vector
	 */
	public static float[] projectS(float[][] m, int startx, int starty, int lenx, int leny) {
		float[] vproject = new float[lenx];
		
		for (int y = starty; y < starty+leny; y++) {
			for (int x = startx; x < startx+lenx; x++) {
				vproject[x-startx] += m[y][x];
			}
		}
		return vproject;
	}
	
	/**
	 * Projects the matrix onto a vector.
	 * @return Projected vector
	 */
	public static float[] projectS(float[][] m) {
		return projectS(m, 0, 0, m[0].length, m.length);
	}

	/**
	 * Projects top right half of the matrix (excluding the diagonal)
	 * onto a vector in SE direction.
	 * 
	 * Note that the diagonal is excluded from the projection.
	 * 
	 * @return Projected vector in SE dir
	 */
	public static float[] projectTriangleSE(float[][] m) {
		return projectTriangleSE(m, 0, m.length);
	}
	
	/**
	 * Projects top right half of the matrix (excluding the diagonal)
	 * onto a vector in SE direction.
	 * 
	 * Note that the diagonal is excluded from the projection.
	 * 
	 * @return Projected vector in SE dir
	 */
	public static float[] projectTriangleSE(float[][] m, int start, int len) {
		float[] dproject = new float[len-1];
		for (int x = start+1; x < start+len; x++) {
			for (int i = 0; i+x < start+len; i++) {
				dproject[x-start-1] += m[i+start][x+i];
			}
		}
		return dproject;
	}
	
	/**
	 * Change each entry to the value between min and max.
	 * 
	 * @param m
	 * @param min
	 * @param max
	 */
	public static void random(float[][] m, float min, float max) {
		for (int y = 0; y < m.length; y++) {
			for (int x = 0; x < m[0].length; x++) {
				m[y][x] = (float) Math.random()*(max-min) - min;
			}
		}
	}

	public static String toString(float[] m) {
		StringBuffer sb = new StringBuffer();
		for (int x = 0; x < m.length; x++) {
			sb.append(m[x]);
			sb.append("\t");
		}
		return sb.toString();
	}

		/* old version:
		 /**
		 * Projects top right half of the matrix (including the diagonal)
		 * onto a vector in SW direction
		 * @return Projected vector in SW dir
		 *
		private static float[] projectSW(float[][] m) {
			float[] dproject = new float[m.length+m[0].length-1];
			for (int i = 0; i < m.length-1/*+m[0].length*; i++) {
				for (int y = 0; y <= i; y++) {
	//				if (x-y < m[0].length-1 && y <= x)
						dproject[i] += m[y][i-y]+m[y][i-y+1];
	//				if (x-y < m[0].length-1)
	//					dproject[x] += m[y][x-y+1];
				}
			}
			return dproject;
		}
		 */
	
		/**
		 * Paints histogram onto the matrix. If <code>null</code> is passed
		 * as the matrix, a new one is created, of <code>defaultHeight</code> height
		 * and <code>hist.lenght</code> width.
		 */
		public static float[][] paintHistogram(float[] hist, float[][] m, int defaultHeight) {
			// create the new matrix
			float maxMatrix;
			if (m == null) {
				m = new float[defaultHeight][hist.length];
				maxMatrix = 1f;
			}
			else {
				maxMatrix = Matrix.getMax(m);
			}
			// paint histogram on the matrix
			float maxHist = Matrix.getMax(hist);
			int len = m[0].length;
			int height = m.length;
			for (int i = 0; i < len; i++) {
				int y = (int)(height-1-((hist[i]/maxHist)*(height-1)));
				Matrix.drawLine(m, i, y, i, m.length-1, maxMatrix);
				//m[y][i] = maxMatrix;
			}
			return m;
		}

		/**
		 * Filter the line with sin-filter.
		 * 
		 * @param line
		 * @param startx
		 * @param sizex
		 * @param profileLen
		 * @param filtered
		 */
		public static float[] filterLine(float[] line, int startx, int sizex, int profileLen, int center, float[] filtered) {
			Assert.assertTrue((startx <= center && center < startx+sizex) || !SelfSimilarity.PARAM_DIAG_FILTER);
			if (filtered == null) filtered = new float[line.length];
			
			for (int x = startx; x < startx+sizex; x++) {
				if (SelfSimilarity.PARAM_DIAG_FILTER && Math.abs(x - center) <= profileLen/2) {
					// empty around diagonal
					filtered[x] = 0f; 
				} else {
					double sinx = Math.sin(Math.PI*(((double) x - center)/profileLen) + Math.PI/2d);
					filtered[x] = (float) ( line[x] * (sinx * sinx) ); 
				}
			}
			return filtered;
		}

		/**
		 * Doesn't modify the line!
		 * 
		 * @param line
		 * @param start
		 * @param end
		 * 
		 * @return The difference between the two highest values in <code>line</code>.
		 */
		public static float highestDiff(float[] line, int start, int end) {
			// retrieve two highest values:
			int val1i = Matrix.getMaxPos(line, start, end);
			float val1 = line[val1i];
			
			line[val1i] = 0;
			
			float val2 = Matrix.getMax(line, start, end);
			
			line[val1i] = val1;
			
			return val1-val2;
		}

		public static long area(int x, int y, boolean self) {
			if (self && x != y)
				Assert.fail("Square matrix expected");
			if (self)
				return x*(x-1)/2;
			else
				return x*y;
		}

		/**
		 * 
		 * @param traceValues A colloection of float[] to be collapsed
		 * @return Single float[] with all the values
		 */
		public static float[] collapseToFloatArray(Collection traceValues) {
			float []alignScoresParticipants;
			int size = 0;
			for (Iterator iter = traceValues.iterator(); iter.hasNext();) {
				float[] tv = (float[]) iter.next();
				size += tv.length;
			}
			alignScoresParticipants = new float[size];
			int pos = 0;
			for (Iterator iter = traceValues.iterator(); iter.hasNext();) {
				float[] tv = (float[]) iter.next();
				System.arraycopy(tv, 0, alignScoresParticipants, pos, tv.length);
				pos += tv.length;
			}
			return alignScoresParticipants;
		}

		/**
		 * 
		 * @param traceValues A colloection of double[] to be collapsed
		 * @return Single float[] with all the values
		 */
		public static double[] collapseToDoubleArray(Collection traceValues) {
			double []alignScoresParticipants;
			int size = 0;
			for (Iterator iter = traceValues.iterator(); iter.hasNext();) {
				double[] tv = (double[]) iter.next();
				size += tv.length;
			}
			alignScoresParticipants = new double[size];
			int pos = 0;
			for (Iterator iter = traceValues.iterator(); iter.hasNext();) {
				double[] tv = (double[]) iter.next();
				System.arraycopy(tv, 0, alignScoresParticipants, pos, tv.length);
				pos += tv.length;
			}
			return alignScoresParticipants;
		}

		/**
		 * 
		 * @param start
		 * @param end
		 * @param len
		 * @return distance from start to end with wrapping
		 */
		public static int wrapDistance(int start, int end, int len) {
			if (start <= end)
				return end-start;
			else
				return end+len-start;
		}

		/**
		 * @param i
		 * @param profileLength
		 * @return
		 */
		public static int wrapNextPos(int i, int len) {
			i++;
			if (i >= len) i-= len;
			return i;
		}
	
}
