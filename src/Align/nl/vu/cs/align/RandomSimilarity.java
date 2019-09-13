package nl.vu.cs.align;

import java.io.*;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.proteinlibs.*;
import nl.vu.cs.align.substtable.*;

/**
 * Calculates local similarities of random sequences
 */
public class RandomSimilarity {

	/**
	 * @return Random sequence
	 */
	public static String generateRandomSequence(int len) {
		StringBuffer seq = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			seq.append(SubstitutionTable.AACID[(int)(Math.random()*20.0d)]);
		}
		return seq.toString();
	}

	/**
	 * @return Random sequence of numbers 0..len-1
	 */
	public static int[] generateRandomNumbers(int len) {
		int []arr = new int [len];
		// generate a sequence
		for (int i = 0; i < arr.length; i++) {
			arr[i] = i;
		}
		// randomize it
		for (int x = 0; x < len; x++) {
			int y = x + ((int)(Math.random()*(len-x)));
			int tmp = arr[x];
			arr[x] = arr[y];
			arr[y] = tmp;
		}
		return arr;
	}

	/**
	 * @return Random sequence based on other sequence, sorting is in chunks of
	 * lenght <code>chunklen</code>.
	 */
	public static String generateRandomSequence(String seq, int chunklen) {
		int len = seq.length();
		int []hash = generateRandomNumbers(len/chunklen);
		StringBuffer seqbuf = new StringBuffer(seq.length());
		for (int x = 0; x < len/chunklen; x++) {
			seqbuf.append(seq.substring(hash[x]*chunklen, hash[x]*chunklen+chunklen));
		}
		seqbuf.append(seq.substring((len/chunklen)*chunklen, len));
		return seqbuf.toString();
	}
	
	/**
	 * @return Reversed sequence
	 */
	public static String reverse(String seq) {
		StringBuffer sb = new StringBuffer(seq.length());
		for (int x = seq.length()-1; x >= 0; x--) {
			sb.append(seq.charAt(x));
		}
		return sb.toString();
	}
	
	/**
	 * @return Random sequence based on other sequence
	 */
	public static String generateRandomSequence(String seq) {
		return generateRandomSequence(seq, 1);
	}
	
	final static int TYPE_SELF_ALIGNMENT = 0;
	final static int TYPE_PSEUDO_SELF_ALIGNMENT = 1;
	final static int TYPE_MUTUAL_ALIGNMENT = 2;
	final static int TYPE_NARROW_ALIGNMENT = 3;
	final static int TYPE_PSEUDO_NARROW_ALIGNMENT = 4;
	final static int TYPE_SELF_INDEPENDENT = 5;
	

	public static void shuffleProtein(
		final int alignType,
		int gapo,
		int gapx,
		SubstitutionTable blosum,
		String protein1,
		String protein2,
		int MAX_TRY,
		int shufflingChunk,
		int procno,
		int procall) {
			
		try {
			String fname = "random ";
			if (alignType == TYPE_SELF_ALIGNMENT) {
				fname += "self";
			} else if (alignType == TYPE_PSEUDO_SELF_ALIGNMENT) {
				fname += "pseudo";
			} else if (alignType == TYPE_MUTUAL_ALIGNMENT) {
				fname += "cmp";
			} else if (alignType == TYPE_NARROW_ALIGNMENT) {
				fname += "narrow";
			} else if (alignType == TYPE_PSEUDO_NARROW_ALIGNMENT) {
				fname += "pseudo narrow";
			} else if (alignType == TYPE_SELF_INDEPENDENT) {
				fname += "self_independent";
			} else {
				Assert.fail("missing fname");
			}
			fname += " cmp ";
			fname += gapo+" "+gapx+" "+procno;
			PrintStream ps = new PrintStream(new FileOutputStream(fname));
			ProgressBar pb = new FakeProgressBar("Protein shuffling", MAX_TRY);
			for (int i = 0; i < MAX_TRY; i++) {
				CompleteAlign ca;
				String seqX, seqY;
				
				AlignData data;
				if (alignType == TYPE_SELF_ALIGNMENT) {
					// self align
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = seqX;
					ca = new CompleteAlignSelfAffine();
				} else if (alignType == TYPE_PSEUDO_SELF_ALIGNMENT) {
					// align against two proteins
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = generateRandomSequence(protein2, shufflingChunk);
					ca = new CompleteAlignSelfAffine();
				} else if (alignType == TYPE_MUTUAL_ALIGNMENT) {
					// align against two proteins
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = generateRandomSequence(protein2, shufflingChunk);
					ca = new CompleteAlignAffineGotoh();
				} else if (alignType == TYPE_NARROW_ALIGNMENT) {
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = generateRandomSequence(protein2, shufflingChunk);
					ca = new CompleteAlignNarrowAffine();
				} else if (alignType == TYPE_PSEUDO_NARROW_ALIGNMENT) {
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = generateRandomSequence(protein2, shufflingChunk);
					ca = new CompleteAlignBrokenSelfAffine();
				} else if (alignType == TYPE_SELF_INDEPENDENT) {
					seqX = generateRandomSequence(protein1, shufflingChunk);
					seqY = generateRandomSequence(protein2, shufflingChunk);
					ca = new CompleteAlign(new LocalInit(), 
											new SelfLocalIndependentFill(),
											new LocalAlignAffineGotoh());
				} else {
					Assert.fail("Wrong align type");
					return;
				}

				data = new AlignDataAffineGotoh(seqX, seqY, gapo, gapx, blosum);
				
				float value = ca.align(data);
				
				Trace t = new Trace();
				data.markMaxRegion(null, t, 0);
				int gapoCount = 0;
				int gapxCount = 0;
				
//				ps.println("Gap costs: " + (gapCosts(t.start, gapo, gapx) + gapCosts(t.start, gapo, gapx)));
//				System.out.print("trace matches: " + t.length());
//				System.out.println();
//				ps.print(" gap length: " + t.gapLength());
//				ps.print(" gap number: " + t.gapNumber());
//				ps.println();
				
				ps.println(value);
				
				pb.advance();
			}
			pb.stop();
			ps.close();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	public static int gapCosts(int []a, int gapo, int gapx) {
		int gapScore = 0;
		for (int k = 1; k < a.length; k++) {
			
			if (a[k-1] != a[k]-1) {
				gapScore += gapo;
				gapScore += (((a[k] - 1) - a[k-1]) - 1)*gapx;
			}
		}
		return -gapScore;
	}

	public static void gapCosts(Trace t, int gapoCount, int k) {
		int gapxCount;
		if (t.start[k-1] != t.start[k]-1) {
			gapoCount++;
			gapxCount = t.start[k] - 1 - t.start[k-1] - 1;
		}
	}
	
	private static int resolveType(String param) {
		param = param.toUpperCase();
		if ("SELF".equals(param)) {
			return TYPE_SELF_ALIGNMENT;
		} else if ("PSEUDO".equals(param)) {
			return TYPE_PSEUDO_SELF_ALIGNMENT;
		} else if ("NOT_SELF".equals(param)) {
			return TYPE_MUTUAL_ALIGNMENT;
		} else if ("NARROW".equals(param)) {
			return TYPE_NARROW_ALIGNMENT;
		} else if ("PSEUDO_NARROW".equals(param)) {
			return TYPE_PSEUDO_NARROW_ALIGNMENT;
		} else if ("SELF_INDEPENDENT".equals(param)) {
			return TYPE_SELF_INDEPENDENT;
		}
		Assert.fail("Wrong parameter");
		return -1;
	}
	
	public static void _main(String[] arg) {
		
		if (arg.length == 0) {
			System.out.println("Usage: program db.fasta protein_name number_of_tests [SELF|NOT_SELF|PSEUDO|NARROW] gapo gapx shuffling_chunk procno proctotal");
			System.exit(0);
		}
		final boolean selfAlignment = false;
		int procno = Integer.valueOf(arg[arg.length-2]).intValue();
		int procall =  Integer.valueOf(arg[arg.length-1]).intValue();
		int maxTry = Integer.valueOf(arg[3]).intValue();
		int gapo = Integer.valueOf(arg[5]).intValue();
		int gapx = Integer.valueOf(arg[6]).intValue();
		int shufflingChunk = Integer.valueOf(arg[7]).intValue();
		
		
		
/*		if (pseudoself) {
			System.err.println("Pseudo-self");
		}*/
		
		SubstitutionTable blosum = null;
		ProteinLib library;
		try {
			blosum = new SubstitutionTable("BLOSUM62");
			library = new FastaDb(arg[0]);
		} catch (IOException ioe) {
			System.err.println("Problem with reading matrix data");
			throw new RuntimeException(ioe.toString());
		}
		
		String protein1 = library.get(arg[1]);
		String protein2 = library.get(arg[2]);
		
		shuffleProtein(resolveType(arg[4]), gapo, gapx, blosum, protein1, protein2, maxTry/procall, shufflingChunk, procno, procall);
//		shuffleProtein(false, gapo, gapx, blosum, protein, MAX_TRY, procno, procall);
	}
	
	public static void main(String[] arg) {
		
		if (arg.length == 0) {
			System.out.println("Usage: program db.fasta protein_name number_of_shuffles");
			System.exit(0);
		}
		final boolean selfAlignment = false;
		int maxTry = Integer.valueOf(arg[2]).intValue();
		
		ProteinLib library;
		try {
			library = new FastaDb(arg[0]);
		} catch (IOException ioe) {
			System.err.println("Problem with reading matrix data");
			throw new RuntimeException(ioe.toString());
		}
		
		String protein = library.get(arg[1]);
		
		for (int i = 1; i <= maxTry; i++) {
			protein = generateRandomSequence(protein, 1);
			System.out.println(">"+i);
			System.out.println(protein);
		}
//		shuffleProtein(false, gapo, gapx, blosum, protein, MAX_TRY, procno, procall);
	}

}
