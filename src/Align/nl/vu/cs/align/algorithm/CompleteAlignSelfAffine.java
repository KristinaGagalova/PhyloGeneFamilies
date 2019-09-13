package nl.vu.cs.align.algorithm;


/**
 * Fills in the region corresponding to filling in self comparison,
 * but the region is split into two areas: above and below diagonal.
 */
public class CompleteAlignSelfAffine extends CompleteAlign {
	
	public CompleteAlignSelfAffine() {
		super(new LocalInit(), new SelfLocalFill(), new LocalAlignAffineGotoh());
	}

}
