package nl.vu.cs.align.algorithm;


public class CompleteAlignTest extends AlignTest {

	CompleteAlign align;

	public CompleteAlignTest(String arg0) {
		super(arg0);
	}

	public void testAlignAbstract() throws Exception {
		if (!this.getClass().equals(CompleteAlignTest.class)) {
			fail("The method is to be overridden in superclassees");
		}
	}
}
