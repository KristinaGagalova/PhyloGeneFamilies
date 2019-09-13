/*
 * Created on Nov 28, 2003
 */
package nl.vu.cs.align.repeats;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.*;
import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;


public class EstimateEVDParams {
	
	public static double[] estimateEVDParams(
			ComparableSequence cmpSeqX,
			ComparableSequence cmpSeqY,
			boolean selfSimilarity,
			AlignAffine align,
			SubstitutionTable subst,
			float gapo,
			float gapx,
			String proteinName,
			int typeNum,
			String outputDir,
			ProgressBar pb) {
				
				if (selfSimilarity) {
					Assert.assertTrue(cmpSeqX.equals(cmpSeqY));
				}
				
				// the minimal number of protein shufflings
				int minShuffleNum = 100;
				
				// the number of repeats to be extracted
				//int minRepeatAllNum = 100; // at least repeats to overall to extract (from many shuffles)
				
				// max number of repeats to be extracted per "protein" pair
				int maxRepeatNumPerShuffle = 1;//2*(minRepeatAllNum/minShuffleNum);
				
				// where the traces are stored			
				Collection traceValues = new Vector(minShuffleNum); // a vector of arrays
				
				// create a sequence stripped from X's
				// TODO what if size = 0 afterwards? pull it up!
				// FEATURE limit the size of sequences to some reasonable size (350?)
				ComparableSequence randomCmpSeqX_long = 
					cmpSeqX.isSequence() ? 
						filterAACmpSeq(cmpSeqX) : (ComparableSequence) cmpSeqX.clone();
				ComparableSequence randomCmpSeqY_long = 
						cmpSeqY.isSequence() ? 
							filterAACmpSeq(cmpSeqY) : (ComparableSequence) cmpSeqY.clone();
							
				if (randomCmpSeqX_long.length() == 0 ||	randomCmpSeqY_long.length() == 0) {
					return new double[] { Double.NaN, /* evdK*/ Double.NaN /* evdLambda */ };
				}														
//				final double MAX_ALL_REPEAT_LEN_RATIO = 0.013d;
				// TODO this can be expressed better: 
				// 		Sum(sqares of repeat length)/area
				// for the very simple reason that coverage of repeats is proportional to square of
				// the length, not the length
	
				int allRepeatNum = 0;
				ComparableSequence randomCmpSeqX = shortenSequence(randomCmpSeqX_long, SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE); // the size is needed at the end of the method
				ComparableSequence randomCmpSeqY = 
							shortenSequence(randomCmpSeqY_long, SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE); // the size is needed at the end of the method
				double sumTraceLen = 0;
				for (int shuffle = 0; shuffle < minShuffleNum/* || allRepeatNum < minRepeatAllNum*/; shuffle++) {
					
					// sanity check
					if (randomCmpSeqX_long.isSequence()) Assert.assertTrue(randomCmpSeqX_long.toString().toUpperCase().indexOf('X') < 0);
					if (randomCmpSeqY_long.isSequence()) Assert.assertTrue(randomCmpSeqY_long.toString().toUpperCase().indexOf('X') < 0);
					
					// randomize the sequences
					randomCmpSeqX_long.randomize();
					randomCmpSeqY_long.randomize();
					
					// shorten the sequences to speed up calculations
					randomCmpSeqX = shortenSequence(randomCmpSeqX_long, SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE);
					randomCmpSeqY = shortenSequence(randomCmpSeqY_long, SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE);
					
					//final double MAX_SW_COVERAGE_RATIO = 0.03; // maximal coverage before deciding that traces become dependent
					// the sum of lengths of extracted repeats should not be more than MAX_ALL_REPEAT_LEN_RATIO of the area, 
					// because the 75% coverage then is exceeded
//					long area = Matrix.area(randomCmpSeqX.length(), randomCmpSeqY.length(), selfSimilarity);
//					int maxAllRepeatLen = (int) (0.25 *
//							(randomCmpSeqX.length()*randomCmpSeqY.length())/350.0 );//(int) (MAX_ALL_REPEAT_LEN_RATIO * area);
					int maxAllRepeatLen = (int) (0.25 *
							(randomCmpSeqX.length()+randomCmpSeqY.length()) / 2.0 );//(int) (MAX_ALL_REPEAT_LEN_RATIO * area);
							
					Collection randomTraces = new Vector(minShuffleNum);
					
					if (selfSimilarity) {
						randomCmpSeqY = randomCmpSeqX;
					}
				
					AlignDataAffine data = align.createData(randomCmpSeqX, randomCmpSeqY, gapo, gapx, subst);
			
					LocalInit.localInit(data);
			
					// PROBLEM???
//					Assert.assertTrue(!selfSimilarity); // estimation of params only for different strings
					if (selfSimilarity)
						SelfLocalFill.selfLocalFill(data);
						
					if (SelfSimilarity.DEBUG_VISUAL_STATS) {
						System.err.println("debuging random traces");
					}
					short debugWEUpdate[][] = 
						((SelfSimilarity.DEBUG_WE_UPDATE_STATS 
							|| SelfSimilarity.DEBUG_WE_UPDATE_EVERY_TRACE) 
								&& cmpSeqX.isSequence()) ? 
							new short[data.getMatrix().length][data.getMatrix()[0].length] // debugWEUpdate
							: null; 
							
					// extract randomNum repeats
					Extract.extractAlignments(
						randomCmpSeqX,
						randomCmpSeqY,
						selfSimilarity,
						align,
						data,
						-1,
						maxRepeatNumPerShuffle,
						0, // minTracesNum
						randomTraces,
						null, // insignificanttraces
						new SelfSimilarity.RepeatStatisticsParam(maxAllRepeatLen/*, MAX_SW_COVERAGE_RATIO*/), // stats only 
						-1.0, // evdK
						-1.0, // evdLambda
						proteinName,
						typeNum,
						outputDir,
						debugWEUpdate, // debugWEUpdate
						pb);
						
					if (debugWEUpdate != null) {
						float [][]tmp = new float[debugWEUpdate.length][debugWEUpdate[0].length];
						for (int y = 0; y < tmp.length; y++) {
							int lenx = tmp[0].length;
							for (int x = 0; x < lenx; x++) {
								tmp[y][x] = debugWEUpdate[y][x];
							}
						}
						Matrix.saveImageGray(tmp, true, SelfSimilarity.createImageFileName(outputDir, proteinName, "update shuffle "+shuffle+" "));
					}
						
					float alignScores[] = new float [randomTraces.size()];
					{
						int k = 0; 
						for (Iterator iter = randomTraces.iterator(); iter.hasNext();) {
							Trace t = (Trace) iter.next();
							alignScores[k++] = t.value[0];
							sumTraceLen += t.length();
						}
						
						Assert.assertTrue(k == alignScores.length);
						traceValues.add(alignScores);
						allRepeatNum += alignScores.length;
						
						// write some statistics...
						if (SelfSimilarity.DEBUG_VISUAL_ESTIMATION) {
							System.err.println("Repeats extracted for protein "+shuffle+": "+ alignScores.length);
						}
					}
				}
				
				// caclulate the avg repeat length
				int avgTraceLen = (int) (sumTraceLen / traceValues.size());
				int avgTraceLenX = avgTraceLen;
				int avgTraceLenY = avgTraceLen;
				
				if (randomCmpSeqX.length() < SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE) avgTraceLenX = 0;
				if (randomCmpSeqY.length() < SelfSimilarity.PARAM_MAX_ESTIM_SEQ_SIZE) avgTraceLenY = 0;
				
				// calculate the area corrected for the repeat length
				double s_area = (double) Matrix.area(randomCmpSeqX.length()-avgTraceLenX, randomCmpSeqY.length()-avgTraceLenY, selfSimilarity);
				return evdRegression(traceValues, s_area);
		}

		private static ComparableSequence shortenSequence(
			ComparableSequence seqLong,
			int maxSize) {
			ComparableSequence seqShort = seqLong;
			if (seqLong.isSequence() && seqLong.length() > maxSize) {
				seqShort = ((Sequence) seqLong).subSequence(0, maxSize);
			}
			return seqShort;
		}

		public static double[] evdRegression(Collection traceValues, double area) {
			// PROBLEM!!!Quick fix -- flatten the array of results (treat each trace as a best scoring segment)
			//float 
			System.err.println("flatten the array according to W&V'94");
			float []flat = Matrix.collapseToFloatArray(traceValues);
			
			if (flat.length == 0)
				return new double[] { Double.NaN, Double.NaN };
			
			traceValues = new Vector();
			traceValues.add(flat);
			if (SelfSimilarity.DEBUG_VISUAL_STATS) {
				PrintStream ps = System.err;
				ps.println("Random sequences extracted:");
				for (int i = 0; i < flat.length; i++) {
					ps.println(flat[i]);
				}
			}
			
			// calculate accumulated scores
			Collection traceAcc = new Vector(traceValues.size());
			//int participants = traceValues.size();
			for (Iterator iter = traceValues.iterator(); iter.hasNext();) {
				float alignScores[] = (float[]) iter.next();
				
				// sort the matrix of alignScores
				Arrays.sort(alignScores); // TODO not needed?
				if (alignScores.length > 0)
					Assert.assertTrue(alignScores[0] > 0f);
			
				// accumulate (previously: chart array)
				float []alignScoresAcc = StatTools.accumulateScores(alignScores); 	
				traceAcc.add(alignScoresAcc);
			}
			
			// log(-log(data/(count+1)))
			// count+1 to avoid last value will be -inf = log(-log(1))
			for (Iterator iter = traceAcc.iterator(); iter.hasNext();) {
				float alignScoresAcc[] = (float[]) iter.next();
				double count = alignScoresAcc.length; 
				for (int i = 0; i < alignScoresAcc.length; i++) {
					alignScoresAcc[i] = (float) Math.log(-Math.log(alignScoresAcc[i]/(count)));
				}
			}
			
			System.err.println("hack for last value");
			traceAcc = dropLastPos(traceAcc);
			traceValues = dropLastPos(traceValues);
			
			// collapse traceValues into 1 array
			float []alignScoresAll = Matrix.collapseToFloatArray(traceValues);
			float []alignScoresAccAll = Matrix.collapseToFloatArray(traceAcc);
			Assert.assertTrue(alignScoresAccAll.length == alignScoresAll.length);
			// delete the values collection, we have things collapsed  
			traceValues = null;
			traceAcc = null;
			
			// sanity checks
			Assert.assertTrue(alignScoresAll[0] > 0f);
			Assert.assertTrue(alignScoresAll[alignScoresAll.length-1] > 0f);
			
			// perform the linear regression on the collected data
			double ab[] = StatTools.linearRegression(alignScoresAll, alignScoresAccAll);
			double a = ab[0];
			double b = ab[1];
			
			if (SelfSimilarity.DEBUG_VISUAL_ESTIMATION) {
				System.err.println("a = "+a+" b = "+b);
			}
			// Xs are stripped - the real length may be different!
			double K = Math.exp(a-Math.log(area)); 
			return new double[] { K, /* evdK*/ -b /* evdLambda */ };
		}

		/**
		 * 
		 * @return collection of arrays without the last element
		 */
		public static Collection dropLastPos(Collection c) {
			Collection newc = new Vector(c.size());
			for (Iterator iter = c.iterator(); iter.hasNext();) {
				float a[] = (float[]) iter.next();
				float newa[] = new float[a.length-1];
				System.arraycopy(a, 0, newa, 0, a.length-1);
				newc.add(newa);
			}
			return newc;
		}

		/**
		 * Filter cmpSeq for X's. Don't do anything to profile, since it doesn't contain X's.
		 * @param cmpSeq
		 */
		private static ComparableSequence filterAACmpSeq(ComparableSequence cmpSeq) {
			if (cmpSeq.isSequence()) {
				String seq = cmpSeq.toString();
				ComparableSequence randomCmpSeq = new Sequence(SubstitutionTable.filterAASeq(seq));
				return randomCmpSeq; 
			}
			throw new RuntimeException("Filtering not implemented for profiles");
		}

		/**
		 * Calculates how many repeats are missed due to the using wrong EVD parameters.
		 */
		public static void findEVDDiscrepancy(String seq, SubstitutionTable subst, float gapo, float gapx, float pvalue, boolean debugDetails, String proteinName, String outputDir) {
			int maxIteration = 200;
			AlignAffine align = new LocalAlignAffineNice();
			AlignDataAffine data = prepareAlignData(seq, seq, subst, gapo, gapx, align);
			Vector bestTracesSDM = new Vector();
			Vector bestTracesBetween = new Vector();
			
			double evdLambda = Float.NaN;
			double evdK = Float.NaN;
			
			evdLambda = EstimateEVDParams.getEVDParamLambda(gapo, gapx);
			evdK = EstimateEVDParams.getEVDParamK(gapo, gapx);
						
			Assert.assertTrue(evdLambda != Float.NaN && evdK != Float.NaN);
						
			// find regions of maximal similarity above pscore threshold
			
			Trace lastTrace = null;
			int between = 0;
			for (int iteration = 1; iteration <= maxIteration; iteration++) {
				// reconstruct the array
				float value = ((LocalAlignAffineNice) align).align(data, lastTrace);
				
				double bychanceRDM = EstimateEVDParams.calculatePValue(iteration, value, 
									seq.length(), seq.length(), 
									true, EstimateEVDParams.getEVDParamK(gapo, gapx), EstimateEVDParams.getEVDParamLambda(gapo, gapx)); // TODO WHAT IS -1?
									
				double bychanceSDM = EstimateEVDParams.calculatePValue(iteration, value, 
									seq.length(), seq.length(), 
									false, EstimateEVDParams.getEVDParamK(gapo, gapx), EstimateEVDParams.getEVDParamLambda(gapo, gapx));
				
				
				// remember the trace
				int [][]trace = new int[2][];
				boolean maxFound = data.markMaxRegion(null, trace, value);
				if (!maxFound) {
					System.err.println("No max value found");
					break;
				}
				
				lastTrace = new Trace(trace);
				
				//Assert.assertTrue(""+bychanceRDM+" "+bychanceSDM, bychanceRDM <= bychanceSDM);
				
				if (bychanceRDM > pvalue) {
					break;
				}
	
				if (debugDetails) {
					System.err.println("Score: "+value+" EVD RDM: "+bychanceRDM+" EVD SDM: "+bychanceSDM);
					System.err.println(
						"["+lastTrace.start[0]+":"+lastTrace.start[lastTrace.length()-1]+"]" +
						"["+lastTrace.end[0]+":"+lastTrace.end[lastTrace.length()-1]+"]");
				}
				//lastTrace.show(seq, seq, System.out);
	//			System.out.println("Score: "+value+" EVD RDM: "+bychanceRDM+" EVD SDM: "+bychanceSDM);
				
				if (bychanceSDM > pvalue) {
					between++;
					bestTracesBetween.add(lastTrace);
				} else {
					bestTracesSDM.add(lastTrace);
				}
				
				// prepare source array for the next step
				// of Waterman-Eggert algorithm
				lastTrace.setValue(-Matrix.INF);
				lastTrace.paint(data.getMatrix());
			}
			
			for (Iterator iter = bestTracesSDM.iterator(); iter.hasNext();) {
				Trace t = (Trace) iter.next();
				
				if (debugDetails) {	
					System.err.println(
						"["+t.start[0]+":"+t.start[t.length()-1]+"]" +
						"["+t.end[0]+":"+t.end[t.length()-1]+"]");
					
					// print detailed trace
					System.err.print(t);//t.show(seq, seq, System.out);
				}
			}
			
			System.err.println(proteinName + " SDM EVDs: " + bestTracesSDM.size() + " between EVDs: "+between);
	
			for (Iterator iter = bestTracesBetween.iterator(); iter.hasNext();) {
				Trace t = (Trace) iter.next();
				
				if (debugDetails) {	
					System.err.println(
						"["+t.start[0]+":"+t.start[t.length()-1]+"]" +
						"["+t.end[0]+":"+t.end[t.length()-1]+"]");
					System.err.print(t);	
				}
			}
			
			if (/*debugDetails && */between > 0 && SelfSimilarity.DEBUG_VISUAL_SUMMARY) {
				
				// -------------------------------------------------------------
				// EVD
				// -------------------------------------------------------------
				float [][]tracesPainted1 = Matrix.createEmpty(data.getMatrix());
				Trace.paintOne(tracesPainted1, bestTracesSDM);
				
				float [][]tracesPainted2 = Matrix.createEmpty(data.getMatrix());
				Trace.paintOne(tracesPainted2, bestTracesBetween);
				
				Matrix.decorate(tracesPainted1);
				Matrix.decorate(tracesPainted2);
				
				Matrix.saveImageRGB(tracesPainted1, tracesPainted2, tracesPainted2, 
					SelfSimilarity.VISUAL_PICT_INVERT, 
					SelfSimilarity.createImageFileName(outputDir, proteinName, "EVDs"));
					
				// -------------------------------------------------------------
				// trans
				// -------------------------------------------------------------
				float [][]tracesPainted3 = Matrix.createEmpty(data.getMatrix());
				Collection bestTraces = new Vector();
				bestTraces.addAll(bestTracesBetween); 
				bestTraces.addAll(bestTracesSDM); 
				Collection transitiveTraces = Trace.multiplyTraces(bestTraces);
				Trace.paintOne(tracesPainted3, transitiveTraces);
				
				Matrix.decorate(tracesPainted3);
				
				// find transitivity between traces
				Matrix.clear(tracesPainted1);
				
				Trace.paintOne(tracesPainted1, transitiveTraces);
				Matrix.decorate(tracesPainted1);
				
				Matrix.saveImageRGB(tracesPainted1, tracesPainted1, tracesPainted1, 
					SelfSimilarity.VISUAL_PICT_INVERT, 
					SelfSimilarity.createImageFileName(outputDir, proteinName, "trans"));
				
				// -------------------------------------------------------------
				// intersect
				// -------------------------------------------------------------
				// find intersections between traces
				Matrix.clear(tracesPainted1);
				Matrix.clear(tracesPainted2);
				Matrix.clear(tracesPainted3);
				
				Trace.paintOne(tracesPainted1, bestTraces);
				
				// alignment traces
				Trace.paintOne(tracesPainted1, bestTraces);
				// paint additively one trace
				for (Iterator iter = transitiveTraces.iterator();iter.hasNext();) {
					Trace t = (Trace) iter.next();
					t.paintAdditivelyOne(tracesPainted1);
				}
				
				// erase everything not confirmed
				for (int y = 0; y < tracesPainted1.length; y++)
					for (int x = 0; x < tracesPainted1[0].length; x++)
						if (tracesPainted1[y][x] > 1.0f) tracesPainted1[y][x] = 1.0f;
						else tracesPainted1[y][x] = 0.0f;
				
				Matrix.decorate(tracesPainted1);
				
				Matrix.saveImageRGB(tracesPainted1, tracesPainted1, tracesPainted1, 
					SelfSimilarity.VISUAL_PICT_INVERT, 
					SelfSimilarity.createImageFileName(outputDir, proteinName, "intersect"));
			}
		}

		public static AlignDataAffine prepareAlignData(
			String seqX,
			String seqY,
			SubstitutionTable subst,
			float gapo,
			float gapx,
			AlignAffine align) {
			AlignDataAffine data = align.createData(seqX, seqY, gapo, gapx, subst);
			
			LocalInit.localInit(data);
			
			if (SelfSimilarity.DEBUG_MUTUAL_ALIGN) {
				LocalFill.localFill(data);
			} else {
				SelfLocalFill.selfLocalFill(data);
				
			}
			return data;
		}

			/**
			 * Calculate the probability that <code>r</code>-th suboptimal alignment
			 * gets a score highter than <code>score</code>.
			 * 
			 * P(H_(r) > score) = 1 - e^(-a) + \Sigma(i=0, r-1, a^i/i!)
			 * 	where a = Kmne^(\Lambda*score)
			 */
			public static double calculatePValue(int r, double score, int m, int n, boolean selfcmp, double evdK, double evdLambda) {
		//TODO enable this Assert.assertTrue("self comparison expected!", selfcmp);
				Assert.assertTrue(r>0);
		//		double lambda = getEVDParamLambda(gapo, gapx);
		//		double K = getEVDParamK(gapo, gapx);
				int area;
				if (selfcmp) {
					Assert.assertTrue(n == m);
					area = n*(n-1)/2;
				} else {
					area = n*m;
				}
				double a = evdK*area*Math.exp(-evdLambda*score);
				double sum = 0;
				for (int i = 0; i <= r-1; i++) {
					// calculate tmpsum = Math.pow(a, i) / factorial(i);
					double tmpsum = 1;
					// calculate a^i/i!, 1 for i = 0;
					for (int j = 1; j <= i; j++) {
						tmpsum *= a/j;
					}
					sum += tmpsum;
				}
				double result = Math.exp(-a) * sum;
				return 1.0d - result;
			}

			/**
			 * Calculates a score
			 * @return A score for corresponding to pvalue
			 */
			public static int calculatePScore(float pvalue, int n, int m, boolean selfcmp, double evdK, double evdLambda) {
				Assert.assertTrue("self comparison expected!", selfcmp);
				// correction for self comparison
				int area;
				if (selfcmp) {
					Assert.assertTrue(n == m);
					area = n*(n-1)/2;
				} else {
					area = n*m;
				}
				// numerically correct value
				double pscore = -(Math.log(Math.log(1/pvalue))-Math.log(area*evdK))/evdLambda;
				return (int)Math.round(pscore);
			}

			static float getEVDParamLambda(float gapo, float gapx) {
				if (Math.round(gapo) == -12 && Math.round(gapx) == -2) {
					return 0.300f;
				}
				if (Math.round(gapo) == -8 && Math.round(gapx) == -2) {
					return 0.215f;
				}
				throw new RuntimeException("Not implemented");
			}

			static float getEVDParamK(float gapo, float gapx) {
				if (Math.round(gapo) == -12 && Math.round(gapx) == -2) {
					return 0.09f;
				}
				if (Math.round(gapo) == -8 && Math.round(gapx) == -2) {
					return 0.021f;
				}
				throw new RuntimeException("Not implemented");
			}

			private static void estimateEVDParams(String _seq, SubstitutionTable subst, float gapo, float gapx) {
				AlignAffine align = new LocalAlignAffineGotoh();
				
				for (int iter = 0; iter < 100; iter++) {
					String seq = RandomSimilarity.generateRandomSequence(_seq);
					//String seqY = RandomSimilarity.generateRandomSequence(_seqY);
			
					AlignDataAffine data = EstimateEVDParams.prepareAlignData(seq, seq, subst, gapo, gapx, align);			
					float value = align.align(data);
			
				}
			}

			/**
			 * @return May return null as the result of failed statistical estimation (e.g. no positive scoring matches)
			**/
			public static AlignDataAffine extractSignificantAlignments(
				ComparableSequence cmpSeqX,
				ComparableSequence cmpSeqY,
				boolean selfSimilarity,
				AlignAffine align,
				SubstitutionTable subst,
				float gapo,
				float gapx,
				float pvalue,
				int maxIteration,
				Collection bestTraces,
				boolean repeatsExtraction,
				double evdParamsReturn[],
				String proteinName,
				int typeNum,
				String outputDir,
				ProgressBar pb) {
					
				double evdLambda;
				double evdK;
				
				// Estimate the parameters
				double[] evdParams = EstimateEVDParams.estimateEVDParams(
					cmpSeqX,
					cmpSeqY,
					selfSimilarity,
					align,
					subst,
					gapo,
					gapx,
					proteinName,
					typeNum,
					outputDir,
					pb);
				evdK = evdParams[0];
				evdLambda = evdParams[1];

				AlignDataAffine data = align.createData(cmpSeqX, cmpSeqY, gapo, gapx, subst);
				
				if (evdParamsReturn != null) {
					evdParamsReturn[0] = evdK;
					evdParamsReturn[1] = evdLambda;
				}				
				if (Double.isNaN(evdK) || Double.isNaN(evdLambda) || Double.isInfinite(evdK) || Double.isInfinite(evdK)) {
					return data;
				}
				
				if (SelfSimilarity.DEBUG_VISUAL_ESTIMATION) {
					System.err.println("evdK="+evdK+" evdLambda="+evdLambda);
				}
				
				if (SelfSimilarity.DEBUG_EXIT_AFTER_ESTIMATION) {
					System.err.println("exititng");	
					System.exit(0);
				}
							
				
				// AlignDataAffine data = align.createData(cmpSeqX, cmpSeqY, gapo, gapx, subst);
				
				LocalInit.localInit(data);
				
				if (selfSimilarity) {
					SelfLocalFillMask.selfLocalFillMask(data);
				}
							
		//		short debugWEUpdate[][] = null;
		//		if (DEB UG_WE_UPDATE || DEB UG_WE_UPDATE_EVERY_TRACE)
		//			debugWEUpdate = new short [data.getMatrix().length][data.getMatrix()[0].length];
					
				// find regions of maximal similarity above pscore threshold
				// 
				if (!repeatsExtraction) {
					short debugWEUpdate[][] = 
						(SelfSimilarity.DEBUG_WE_UPDATE | SelfSimilarity.DEBUG_WE_UPDATE_EVERY_TRACE) ? 
							new short[data.getMatrix().length][data.getMatrix()[0].length] // debugWEUpdate
							: null; 
							
					Collection insignificantTraces = new Vector(SelfSimilarity.PARAM_MIN_TRACES_NUM);
					
					Extract.extractAlignments(
						cmpSeqX,
						cmpSeqY,
						selfSimilarity,
						align,
						data,
						pvalue,
						maxIteration,
						SelfSimilarity.PARAM_MIN_TRACES_NUM,
						bestTraces,
						insignificantTraces,
						null, /* no stats */
						evdK,
						evdLambda,
						proteinName,
						typeNum,
						outputDir,
						debugWEUpdate,
						pb);
						
					Collection c = new Vector(insignificantTraces);
					c.addAll(bestTraces);
					
					// multiply them
					Collection multiplied = Trace.multiplyTraces(c);
					
					// print them out if desired
					if (SelfSimilarity.DEBUG_STATS_INSIGN) {
						float transM[][] = Matrix.createEmpty(data.getMatrix());
						Trace.paintAdditively(transM, multiplied);
						Matrix.saveImageGray(transM, 
							SelfSimilarity.VISUAL_PICT_INVERT, 
							SelfSimilarity.createImageFileName(outputDir, proteinName, "insign multiplied"));
					}
					
					// check whether any insignificant trace is confirmed
					for (Iterator iter = c.iterator(); iter.hasNext();) {
						Trace tWeak = (Trace) iter.next();
						boolean confirmed = false;
						
						for (Iterator iter2 = multiplied.iterator(); iter2.hasNext() && !confirmed;) {
							Trace tMult = (Trace) iter2.next();
							
							if (selfSimilarity) {
								// are the traces above the diagonal?
								Assert.assertTrue(tWeak.end[0] <= tWeak.start[0]);
								Assert.assertTrue(tMult.end[0] <= tMult.start[0]);
							}
							
							// try to confirm any tWeak
							for (int i = 0; i < tWeak.start.length; i++) {
								int j = 0;
								
								// skip the lower starting pos
								for (j = 0; j < tMult.start.length && tMult.start[j] < tWeak.start[i]; j++) { }
								if (j < tMult.start.length && tMult.start[j] == tWeak.start[i]) {
									if (tMult.end[j] == tWeak.end[i]){
										// trace confirmed!
										confirmed = true;
										break;
									}
								} 
							}
						}
						if (confirmed) {
							bestTraces.add(tWeak);
						}
					}

					if (SelfSimilarity.DEBUG_WE_UPDATE) {
						float [][]tmp = new float[debugWEUpdate.length][debugWEUpdate[0].length];
						for (int y = 0; y < tmp.length; y++) {
							int lenx = tmp[0].length;
							for (int x = 0; x < lenx; x++) {
								tmp[y][x] = debugWEUpdate[y][x];
							}
						}
						//Matrix.decorate(tmp);
						Matrix.saveImageGray(tmp, true, SelfSimilarity.createImageFileName(outputDir, proteinName, "update "));
					}
					
					if (bestTraces.size() == 0) {
						Trace t = (Trace) insignificantTraces.toArray()[0];
						float value = t.score(cmpSeqX, cmpSeqY, subst, gapo, gapx); 
						double bychance = EstimateEVDParams.calculatePValue(1, value, 
											cmpSeqX.length(), cmpSeqY.length(), 
											selfSimilarity, evdK, evdLambda);
						float valueng = t.score(cmpSeqX, cmpSeqY, subst, 0, 0); 
						double bychanceng = EstimateEVDParams.calculatePValue(1, valueng, 
											cmpSeqX.length(), cmpSeqY.length(), 
											selfSimilarity, evdK, evdLambda);
						if (bychanceng < pvalue && SelfSimilarity.PARAM_FORCE) {
							System.err.println("!!!adding 1st insignificant trace");
							bestTraces.add(t);
							System.err.println(proteinName+" score: "+value+" pval: "+bychance+" score no gap: "+valueng+" pval no gap:"+bychanceng); // PROBLEM hack
						}
					}
					
				} else {
					Assert.assertTrue("Extracting repeats from profile is not a selfSimilarity", !selfSimilarity);
					
					int origSeqYLength = cmpSeqY.length(); // original length of the sequence 
					
//					Extract.extractAlignments(
//						(Profile) cmpSeqX,
//						(Sequence) cmpSeqY,
//						false,
//						align,
//						data,
//						pvalue,
//						maxIteration,
//						0 /*SelfSimilarity.PARAM_MIN_TRACES_NUM*/,
//						bestTraces,
//						null /*insignificantTraces*/,
//						null, /* no stats */
//						evdK,
//						evdLambda,
//						proteinName,
//						outputDir,
//						null,
//						pb);
//						
//					if (SelfSimilarity.DEBUG_VISUAL_SUMMARY) {
//						float [][]tmp = Matrix.createEmpty(data.getMatrix());
//						Trace.paintOne(tmp, bestTraces);
//						Matrix.decorate(tmp);
//						Matrix.saveImageGray(tmp, true, SelfSimilarity.createImageFileName(outputDir, proteinName, "profile traces", gapo, gapx));
//					}
//					
//					bestTraces.clear();
			
					// Extract recursively alignments
					extractWrapaRecursive(
						(Profile) cmpSeqX,
						(Sequence) cmpSeqY,
						subst,
						gapo,
						gapx,
						bestTraces,
						pvalue,
						evdK,
						evdLambda,
						0,
						origSeqYLength);
					
					if (SelfSimilarity.DEBUG_VISUAL_SUMMARY) {
						// paint the result
						float [][]tmp = new float[cmpSeqY.length()+1][cmpSeqX.length()+1];
						Trace.paintOne(tmp, bestTraces);
						Matrix.decorate(tmp);
						Matrix.saveImageGray(tmp, true, SelfSimilarity.createImageFileName(outputDir, proteinName, "profile wrapa"));
					}
			
					
				}
					
				return data;
			}

			private static void extractWrapaRecursive(
				Profile profile,
				Sequence sequence,
				SubstitutionTable subst,
				float gapo,
				float gapx,
				Collection bestTraces,
				float pvalue,
				double evdK,
				double evdLambda,
				int origSeqYStart,
				int origSeqYLength) {
					
				AlignDataAffine dataWrapa = new LocalAlignAffineWrapa().createData(profile, sequence, gapo, gapx, subst);
				float alignScore = LocalAlignAffineWrapa.localAlignAffine(dataWrapa, true);
				// is this score significant?
				double bychance = EstimateEVDParams.calculatePValue(1 /* TODO iteration */, alignScore, 
									profile.length(), origSeqYLength, 
									false, evdK, evdLambda);
				if (SelfSimilarity.DEBUG_VISUAL_SIMPLE) {
					System.err.println("[WRAPA] Trace "+ alignScore + " to go, cutoff "+bychance);
				}
				if (alignScore == 0 || bychance > pvalue) 
					return;

				// get the trace
				Trace t = new Trace();
				dataWrapa.markMaxRegion(null, t, 0);
				int tSeqStart = t.end[0]-1;
				int tSeqEnd = t.end[t.end.length-1]-1;
								
				// convert wrap-around repeat into normal repeat
				bestTraces.add(t);
				// The size of repeat is limited by SelfSimilarity.minRepeatRatio
				// construct stripped sequences recursively
				if (tSeqStart > SelfSimilarity.PARAM_MIN_REPEAT * profile.length()) {
					// Extract recursively alignments
					extractWrapaRecursive(
						(Profile) profile,
						(Sequence) sequence.subSequence(0, tSeqStart),
						subst,
						gapo,
						gapx,
						bestTraces,
						pvalue,
						evdK,
						evdLambda,
						0,
						origSeqYLength);
				}
				
				if ((sequence.length()-tSeqEnd)-1 > SelfSimilarity.PARAM_MIN_REPEAT * profile.length()) {
					Collection traces = new Vector();
					// Extract recursively alignments
					extractWrapaRecursive(
						(Profile) profile,
						(Sequence) sequence.subSequence(tSeqEnd+1, sequence.length()),
						subst,
						gapo,
						gapx,
						traces,
						pvalue,
						evdK,
						evdLambda,
						tSeqEnd+1,
						origSeqYLength);
						
					// correct the traces for the offset from which the sequence was stripped
					Trace.move(0, tSeqEnd, traces);
					bestTraces.addAll(traces);	
				}
			}
}
