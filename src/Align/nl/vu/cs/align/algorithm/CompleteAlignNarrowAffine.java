package nl.vu.cs.align.algorithm;

/**
 * This class performs narrow alignment -- alignment of a narrow section 
 * 2*(n-sqrt(n^2/2)) around diagonal.
 */
public class CompleteAlignNarrowAffine extends CompleteAlign {

	public CompleteAlignNarrowAffine() {
		super(new LocalInit(), new NarrowLocalFill(), new LocalAlignAffineGotoh());
	}

}
