package nl.vu.cs.align.algorithm;


public class CompleteAlignBrokenSelfAffine extends CompleteAlign {
	
	public CompleteAlignBrokenSelfAffine() {
		super(new LocalInit(), new BrokenSelfLocalFill(), new LocalAlignAffineGotoh());
	}

}
