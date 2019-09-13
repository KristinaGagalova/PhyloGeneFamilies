/*
 * Created on Nov 28, 2003
 *
 */
package nl.vu.cs.align.repeats;

public class StatTools {

	public static double[] linearRegression(float[] x, float[] y) {
		// TODO update the weights accordingly to the amount of information which was collected
		// for the specific point of the chart
		double sumxx = 0;
		double sumx = 0;
		double sumxy = 0;
		double sumy = 0;
		double sumyy = 0;
		int n = 0;
//		int intoAccount = (int) (y.length * 0.9); TODO maybe we don't need all of them?
//		if (intoAccount < 10) intoAccount = y.length; // sanity
		int intoAccount = y.length;
		for (int i = 0; i < intoAccount; i++) {
			double score = (double) x[i];
			double acc = (double) y[i];
			sumx += score;
			sumxx += score*score;
			sumxy += acc*score;
			sumy += acc;
			sumyy += acc*acc;
			n += 1;
		}
		double ssxx = sumxx - sumx * sumx / n;
		double ssyy = sumyy - sumy * sumy / n;
		double ssxy = sumxy - sumx * sumy /n;
		double b = ssxy / ssxx;
		double a = sumy/n - b * sumx/n;
		return new double[] {a, b};
	}

	/**
	 * result[i] = #(values <= alignScoresAll[i])
	 * @param alignScoreAll
	 * @return
	 */
	public static float[] accumulateScores(float[] alignScoresAll) {
		int n = alignScoresAll.length;
		float alignScoresAcc[] = new float[n];
		for (int i = 0; i < n; i++) {
			alignScoresAcc[i] = i+1; 
		}
		
		// This performes much better when commented! :-)
		// results in avrg scores if a few scores achieve the same result
//		// alignScoresAcc[i] = #(alignScoreAll[j] >= alignScoreAll[i])
//		for (int i = n-2; i >= 0; i--) {
//			// sanity check
//			if (alignScoresAll[i] > alignScoresAll[i+1]) Assert.fail("Array not sorted");
//			// propagete the number further
//			if (alignScoresAll[i] == alignScoresAll[i+1])
//				alignScoresAcc[i] = alignScoresAcc[i+1]; 
//		}
		
		return alignScoresAcc;
	}

	public static double factorial(int f) {
		if (f == 0) return 1d;
		double result = 1;
		for (int i = 2; i <= f; i++) {
			result *= i;
		}
		return result;
	}
}
