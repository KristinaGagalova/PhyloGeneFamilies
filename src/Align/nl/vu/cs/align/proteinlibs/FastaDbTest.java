package nl.vu.cs.align.proteinlibs;

import junit.framework.*;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FastaDbTest extends TestCase {

	ProteinLib library;
	
	/**
	 * Constructor for FastaDbTest.
	 * @param arg0
	 */
	public FastaDbTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		library = new FastaDb("data/TESTDB.fasta");
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test for void FastaDb()
	 * see the file TESTDB.fasta
	 */
	public void testFastaDb() {
		assertTrue(library.get("sp") == null);
		assertEquals(library.get("P1"), "A");
		assertEquals(library.get("FINC_BOVIN"), "A");
		assertEquals(library.get("P2"), "AA");
		assertEquals(library.get("P3"), "AAA");
		assertEquals(library.get("P3a"), "AAA");
	}

}
