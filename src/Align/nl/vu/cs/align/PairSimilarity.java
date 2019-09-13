package nl.vu.cs.align;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.proteinlibs.*;
import nl.vu.cs.align.substtable.*;

public class PairSimilarity {
	
	private static void calcPairScores(
		ProteinLib db,
		SubstitutionTable subst1, SubstitutionTable subst2) {
		
		int amount = db.getAllProteins().size();
		String protein[] = new String[amount];
		String proteinName[] = new String[amount];
		
		// retrieve proteins from db
		int proteinNum = 0;
		for (Iterator iter = db.getAllFirstNames().iterator(); iter.hasNext();) {
			String pName = (String) iter.next();
			proteinName[proteinNum] = pName;
			protein[proteinNum] = (String)db.get(pName);
			proteinNum++;
		}
		
		// calculate the scores
		for (int protein1 = 0; protein1 < amount; protein1++) {
			for (int protein2 = protein1+1; protein2 < amount; protein2++) {
				AlignData data1 = new AlignDataAffineGotoh(protein[protein1], protein[protein2], -4, -4, subst1);
				AlignData data2 = new AlignDataAffineGotoh(protein[protein1], protein[protein2], -4, -4, subst2);
//				float matrix1[][] = new LocalInit().init(protein[protein1], protein[protein2]);
				LocalInit.localInit(data1);
				LocalInit.localInit(data2);
				LocalFill.localFill(data1);
				LocalFill.localFill(data2);
				float value1 = new LocalAlignAffineGotoh().align(data1);
				float value2 = new LocalAlignAffineGotoh().align(data2);
				System.out.print(proteinName[protein1]+ " " + value1 + " " + proteinName[protein2] + " " + value2);
				//printAmbigousSolutions(align1, align2);
//				printRegions(data1, data2);
				System.out.println();
				
			}
		}
	}

/*	private static void printRegions(AlignData a1, AlignData a2) {
		int [][]similar1 = a1.findMaxRegion();
		int [][]similar2 = a2.findMaxRegion();
		System.out.print(" Region: ");
		System.out.print(similar1[0][0]+" - "+similar1[0][1]+" vs "+similar1[1][0]+" - "+similar1[1][1]++);
		System.out.print(" region: ");
		System.out.print(similar2[0][0]+" - "+similar2[0][1]+" vs "+similar2[1][0]+" - "+similar2[1][1]++);
		float overlap1 = calcOverlapping(similar1[0][0], similar1[0][1], similar2[0][0], similar2[0][1]);
		float overlap2 = calcOverlapping(similar1[1][0], similar1[1][1], similar2[1][0], similar2[1][1]);
		System.out.print(" ");
		System.out.print(overlap1);
		System.out.print(" ");
		System.out.print(overlap2);
	}


	private static void printAmbigousSolutions(AlignData align1, AlignData align2) {
		System.out.print(" ");
		align1.printAmbiguous();
		System.out.print(" ");
		align2.printAmbiguous();
	}*/


	/** 
	 * @return region of intersection based on shorter regions (i.e. the result may 
	 * be 1.0 even if regions are different sizes. This case is shown here:
	 * 
	 *             xxxxxxxxxxxxxxxxxxxxx
	 *              \                 /
	 *                \             /        
	 *                  yyyyyyyyyyy
	 */
	static float calcOverlapping(int s1start, int s1end, int s2start, int s2end) {
		int start = Math.max(s1start, s2start);
		int end = Math.min(s1end, s2end);
		
		if (end-start < 0) return 0;
		return (1.0F*(end-start)) / Math.min(s1end-s1start, s2end-s2start);
	}

	
	public static void main(String[] args) {
		SubstitutionTable blosum = null;
		VariableTable blosumPlus = null;
		ProteinLib library = null;
		try {
			blosum = new SubstitutionTable("BLOSUM62");
			blosumPlus = new VariableTable("BLOSUM62");
			library = new FastaDb("COG0442");
		} catch (IOException ioe) {
			System.err.println("Problem with reading matrix data");
			throw new RuntimeException(ioe.toString());
		}
		blosumPlus.increaseValues(-1);
		calcPairScores(library, blosum, blosumPlus);
	}


}
