package nl.vu.cs.align.repeats;

import java.io.*;
import java.util.*;

import nl.vu.cs.align.*;
import nl.vu.cs.align.algorithm.*;
import nl.vu.cs.align.proteinlibs.*;
import nl.vu.cs.align.substtable.*;
import junit.framework.*;

/**
 * @author radek
 */
public class EstimateEVDParamsTest extends TestCase {

	public EstimateEVDParamsTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(EstimateEVDParamsTest.class);
	}

	public void testEstimateEVDParams() throws IOException {
		ComparableSequence seqX = new Sequence("xxxxxxxxxxxxxxx");
		double d[] = EstimateEVDParams.estimateEVDParams(seqX, seqX, true, new LocalAlignAffineNice(), new SubstitutionTable("BLOSUM62"), -8, -2, "name", 1, ".", new FakeProgressBar());
	}
	
	public void testIOnut() {
		FastaDb fd;
		
		try {
			fd = new FastaDb("data/ank_ref6.tfa");
    
			Vector v = new Vector(fd.getAllProteins());
	    
			System.out.println("First sequence");
			System.out.println((String)v.elementAt(2));
			System.out.println("Second sequence");    
			System.out.println((String)v.elementAt(3));
			LocalAlignAffineNice dt = new LocalAlignAffineNice();
			Collection traces = new Vector();
			double[] evd = new double[100];
			EstimateEVDParams.extractSignificantAlignments(
				new Sequence((String) v.elementAt(0)),
				new Sequence((String) v.elementAt(1)),
				false,
				dt,
				new SubstitutionTable("BLOSUM62"),
				(float) - 8.0,
				(float) - 2.0,
				(float) 0.001,
				20,
				traces,
				false,
				evd,
				new String(""),
				0,
				new String(),
				new FakeProgressBar());
				
			assertTrue(traces.size() <= 20);
			
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

	}

}
