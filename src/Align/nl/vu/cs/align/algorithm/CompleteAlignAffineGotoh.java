package nl.vu.cs.align.algorithm;

public class CompleteAlignAffineGotoh extends CompleteAlignAffine {

	public CompleteAlignAffineGotoh() {
		super(new LocalInit(), new LocalFill(), new LocalAlignAffineGotoh());
	}

}
