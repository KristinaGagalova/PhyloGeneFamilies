package nl.vu.cs.align;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.proteinlibs.*;
import nl.vu.cs.align.substtable.*;

public class SinglePairSimilarity {
	
	private static void calcPairScores(
		ProteinLib db,
		SubstitutionTable subst1) {
		
		int amount = db.getAllProteins().size();
		String protein[] = new String[amount];
		String proteinName[] = new String[amount];
		
		// retrieve proteins from db
		int proteinNum = 0;
		for (Iterator iter = db.getAllFirstNames().iterator(); iter.hasNext();) {
			String pName = (String) iter.next();
			proteinName[amount-proteinNum-1] = pName;
			protein[amount-proteinNum-1] = (String)db.get(pName);
			proteinNum++;
		}
		
		// calculate the scores
		Object firstNames[] = db.getAllFirstNames().toArray();
		for (int protein1 = 0; protein1 < amount; protein1++) {
			for (int protein2 = protein1+1; protein2 < amount; protein2++) {
				AlignAffine align = new LocalAlignAffineNice();
				AlignData data = new AlignDataAffineNice(protein[protein1], protein[protein2], -8, -2, subst1);
				LocalInit.localInit(data);
				LocalFill.localFill(data);
				Trace trace = null;
				// iteration 1
				float value = ((LocalAlignAffineNice) align).align(data, trace, null);
				Matrix.saveImageGray(data.getMatrix(), true, "pictures/"+firstNames[protein1]+" "+firstNames[protein2]+" "+0);
				int [][]traceArr = new int[2][];
				data.markMaxRegion(null, traceArr, 0/*-Matrix.INF*/);
				trace = new Trace(traceArr, (float[][])null);
				trace.setValue(-Matrix.INF);
				trace.paint(data.getMatrix());
				// iteration 2
				((LocalAlignAffineNice) align).align(data, trace, null);
				trace.setValue(value);
				trace.paint(data.getMatrix());
				Matrix.saveImageGray(data.getMatrix(), true, "pictures/"+firstNames[protein1]+" "+firstNames[protein2]+" "+1);

			}
		}
	}

	public static void main(String[] args) {
		SubstitutionTable blosum = null;
		ProteinLib library = null;
		try {
			blosum = new SubstitutionTable("BLOSUM62");
			library = new FastaDb("u:/bioinfo/courses/DMT - bioinfo/wd40.fasta");
		} catch (IOException ioe) {
			System.err.println("Problem with reading matrix data");
			throw new RuntimeException(ioe.toString());
		}
		calcPairScores(library, blosum);
	}


}
