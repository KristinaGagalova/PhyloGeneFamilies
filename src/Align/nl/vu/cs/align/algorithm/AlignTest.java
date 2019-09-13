package nl.vu.cs.align.algorithm;

import java.io.*;

import junit.framework.*;
import nl.vu.cs.align.matrix.*;
import nl.vu.cs.align.substtable.*;

public class AlignTest extends TestCase {
	
	protected SubstitutionTable blosum62;
	protected SubstitutionTable testSubst;

	
	static protected float MA = 11;
	static protected float MB = 12;
	static protected float MC = 13;
	static protected float MD = 14;
	static protected float MM = -10;
	

	public AlignTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		blosum62 = new SubstitutionTable("BLOSUM62");
		getTestSubst();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void _testForMaxValue(Align align, AlignData data, float expected) {
		align.align(data);
		Assert.assertEquals("Wrong max value in the matrix", expected, Matrix.getMax(data.getMatrix()), Matrix.FLOAT_PRECISION);
		Assert.assertTrue("Wrong max value in the matrix", expected == Matrix.getMax(data.getMatrix()));
	}

	public void test1() {
		// fake test
	}

	protected SubstitutionTable getTestSubst() throws IOException {
		if (testSubst != null) return testSubst;
		
		SubstitutionTable subst = new SubstitutionTable("BLOSUM62");
		
		subst.setValue('A', 'A', MA);
		subst.setValue('B', 'B', MB);
		subst.setValue('C', 'C', MC);
		subst.setValue('D', 'D', MD);
		
		subst.setValue('A', 'B', MM);
		subst.setValue('A', 'C', MM);
		subst.setValue('A', 'D', MM);
		
		subst.setValue('B', 'A', MM);
		subst.setValue('B', 'C', MM);
		subst.setValue('B', 'D', MM);
		
		subst.setValue('C', 'A', MM);
		subst.setValue('C', 'B', MM);
		subst.setValue('C', 'D', MM);
		
		subst.setValue('D', 'A', MM);
		subst.setValue('D', 'B', MM);
		subst.setValue('D', 'C', MM);
		
		testSubst = subst;
		return testSubst;
	}
}
