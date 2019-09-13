/*
 * Created on Nov 29, 2003
 */
package nl.vu.cs.align;

import java.util.*;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.repeats.*;


/**
 * Extracts alignments scoring enough
 */
public class Extract {

		public static void extractAlignments(
			ComparableSequence cmpSeqX,
			ComparableSequence cmpSeqY,
			boolean selfSimilarity,
			AlignAffine align,
			AlignDataAffine data,
			float pvalue,
			int maxIteration,
			int minIteration,
			Collection bestTraces,
			Collection insignificantTraces,
			SelfSimilarity.RepeatStatisticsParam statsParam,
			double evdK,
			double evdLambda,
			String proteinName,
			int typeNum,
			String outputDir,
			short[][] debugWEUpdate,
			ProgressBar pb) {
				
			if (Assert.DEBUG && selfSimilarity) {
				Assert.assertTrue(cmpSeqX.equals(cmpSeqY));
			}
				
			Assert.assertTrue(minIteration <= maxIteration);
			Assert.assertTrue(minIteration == 0 || (minIteration > 0 && insignificantTraces != null)); 
			
			float [][]matrixEveryTrace = null;
			if (SelfSimilarity.DEBUG_VISUAL_EVERY_TRACE && statsParam == null) {
				matrixEveryTrace = Matrix.createEmpty(data.getMatrix());
				Matrix.decorate(matrixEveryTrace, true);
			}
				
			Trace lastTrace = null;
			
			int allTraceLen = 0;
			boolean insignificant = false; // start of extracting insignificant traces, which have to be validated
			for (int iteration = 1; iteration <= maxIteration || iteration <= minIteration; iteration++) {
				// reconstruct the array
				pb.advance("Align affine");
				float value = ((LocalAlignAffineNice) align).align(data, lastTrace, debugWEUpdate);
				
	 
				if (SelfSimilarity.DEBUG_WE_UPDATE_EVERY_TRACE && debugWEUpdate != null) {
					
					int sum = 0; 
					float [][]tmp = new float[debugWEUpdate.length][debugWEUpdate[0].length];
					for (int y = 0; y < tmp.length; y++) {
						int lenx = tmp[0].length;
						for (int x = 0; x < lenx; x++) {
							tmp[y][x] = debugWEUpdate[y][x];
							sum += debugWEUpdate[y][x];
						}
					}
					long area = Matrix.area(debugWEUpdate.length, debugWEUpdate[0].length, selfSimilarity);
					System.err.println("iteration:"+iteration+" updates:"+sum+" ratio:"+((double) sum) / area+ " traces len: "+allTraceLen);
				}
	
				// save the matrix
				if (SelfSimilarity.DEBUG_VISUAL_SW && iteration == 1 && statsParam == null) {
					float[][] m = data.getMatrix();
					Matrix.saveImageRGBMinMax(m, m, m, 0, Matrix.getMax(m), true, SelfSimilarity.createImageFileName(outputDir, proteinName, "s-w "+typeNum));
				}
		
				// is the score statistically significant?
				if (statsParam == null) {
					boolean traceBelowThreshold;
					// use EVD distribution
					double bychance;
					bychance = EstimateEVDParams.calculatePValue(iteration, value, 
										cmpSeqX.length(), cmpSeqY.length(), 
										selfSimilarity, evdK, evdLambda);
					
					// TODO !!!it may not be so smart to quit the iteration now
					// there may be some scores still statistically significant!
					// due to the fact that iteration number is thaken into
					// account when calculating significance.
					traceBelowThreshold = bychance > pvalue;
					 
					if (SelfSimilarity.DEBUG_VISUAL_SIMPLE) {
						System.err.println("Trace "+ value + " to go, cutoff "+bychance);
					}
					
					if (pvalue >= 0 && traceBelowThreshold) {
						// it's time to finish the extraction
						if (iteration > minIteration) 
							break;
						else
							insignificant = true;
					}
				}
				
				// remember the trace
				int [][]trace = new int[2][];
				pb.advance("Mark the trace");
				boolean maxFound = data.markMaxRegion(null, trace, value);
				
				if (!maxFound) {
					System.err.println("No max value found");
					break;
				}
				
				Trace candidate = new Trace(trace, (float[][])null);
				candidate.setValue(value);
				lastTrace = (Trace) candidate.clone(); // store the trace for the next iteration
				if (!insignificant)
					bestTraces.add(candidate);
				else
					insignificantTraces.add(candidate);
					
				allTraceLen += candidate.length();
	
				if (statsParam != null) {
					// for statistics purposes, check whether the coverage has been exceeded
					allTraceLen += trace.length;
					if (allTraceLen > statsParam.maxAllTraceLen) {
						break;
					}
				}
				 
				if (matrixEveryTrace != null) {
					System.err.println("Trace no.: "+iteration);
					Trace t = new Trace(trace, data.getMatrix());
					t.show(cmpSeqX.toString(), cmpSeqY.toString(), System.err);
					System.err.println(t);
					System.err.println("Gaps no.: "+t.gapNumber());
					t.paintOne(matrixEveryTrace);
					Matrix.saveImageGray(matrixEveryTrace, true, SelfSimilarity.createImageFileName(outputDir, proteinName, "traces "+typeNum+" "+iteration));
				}
				
				// prepare source array for the next step
				// of Waterman-Eggert algorithm
				pb.advance("Prepare source array for the next step");
				lastTrace.setValue(-Matrix.INF);
				lastTrace.paint(data.getMatrix());
			}
		}
}
