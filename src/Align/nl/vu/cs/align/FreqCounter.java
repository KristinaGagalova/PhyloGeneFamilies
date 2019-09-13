package nl.vu.cs.align;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.proteinlibs.*;
import nl.vu.cs.align.substtable.*;



/**
 * The class reports frequencies of aa in database
 */
public class FreqCounter {
	

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: program db.fasta [protein_len]");
		}
		ProteinLib library;
		try {
			library = new FastaDb(args[0]);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.toString());
		}
		
		if (args.length == 2)
			new FreqCounter().countFreqs(library, Integer.valueOf(args[1]).intValue());
		else
			new FreqCounter().printFreqs(library);
	}

	private int aaf[] = new int [256];

	public void printFreqs(ProteinLib library) {
		Collection pnames = library.getAllFirstNames();
		
		System.out.print("Name");
		for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
			System.out.print("\t");
			System.out.print(SubstitutionTable.AACID[i]);
		}
		System.out.println();
		
		for (Iterator it = pnames.iterator(); it.hasNext();) {
			String pname = (String) it.next();
			
			System.out.print(pname);
			
			int []aafreqs = new int [256];
			
			String protein = library.get(pname);
			for (int i = 0; i < protein.length(); i++) {
				aafreqs[protein.charAt(i)]++;
			}
			
			for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
				System.out.print("\t");
				System.out.print(aafreqs[SubstitutionTable.AACID[i]]);
			}
			
			System.out.println();
		}
	}
	
	public void countFreqs(ProteinLib library, int proteinLen) {
		Collection pnames = library.getAllProteins();
		for (Iterator it = pnames.iterator(); it.hasNext();) {
			String protein = (String) it.next();
			
			for (int i = 0; i < protein.length(); i++) {
				aaf[protein.charAt(i)]++;
			}
			
		}
		
		int sum = 0;
		for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
			sum += aaf[SubstitutionTable.AACID[i]];
		}
		
		for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
			System.out.println(((char)SubstitutionTable.AACID[i])+" "+aaf[SubstitutionTable.AACID[i]]);
		}
		
		System.out.println();
				
		for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
			System.out.print(((char)SubstitutionTable.AACID[i])+" "+
				(((double)aaf[SubstitutionTable.AACID[i]])/sum)+" ");
		}
		System.out.println();
		
		System.out.println("Distributions of aa in avg protein of length "+proteinLen);
		// generate protein of desired lenght 
		int tempsum = sum;
		int templen = proteinLen;
		StringBuffer sb = new StringBuffer(proteinLen);
		for (int i = 0; i < SubstitutionTable.AACID.length; i++) {
			int aashare = ((aaf[SubstitutionTable.AACID[i]])*templen)/tempsum;
			System.out.print(((char)SubstitutionTable.AACID[i])+" "+aashare+" ");
			for (int j = 0; j < aashare; j++) {
				sb.append((char)SubstitutionTable.AACID[i]);
			}
				
			tempsum -= aaf[SubstitutionTable.AACID[i]];
			templen -= aashare;
		}
		System.out.println();
		String DEFAULT_PROTEIN = sb.toString();
		System.out.println("DEFAULT_PROTEIN");
		System.out.println(DEFAULT_PROTEIN);
	}
	
	public static void RobinsonProtein(int proteinLen) {
		
		for (int i = 0; i < SubstitutionTable.AACID_COUNT_ROBINSON.length; i++) {
			System.out.println(SubstitutionTable.AACID[i]+"\t"+SubstitutionTable.AACID_COUNT_ROBINSON[i]+"\t"+SubstitutionTable.AACID_FREQ_ROBINSON[i]);
		}
		
		int templen = proteinLen;
		int tempsum = SubstitutionTable.ROBINSON_SUM;
		for (int i = 0; i < SubstitutionTable.AACID_COUNT_ROBINSON.length; i++) {
			int aashare = ((SubstitutionTable.AACID_COUNT_ROBINSON[i])*templen)/tempsum;

			for (int j = 0; j < aashare; j++) {
				System.out.print(SubstitutionTable.AACID[i]);
			}
				
			tempsum -= SubstitutionTable.AACID_COUNT_ROBINSON[i];
			templen -= aashare;
		}
		System.out.println();
	}
	
}
