package nl.vu.cs.align.algorithm;

public class CompleteAlignSimple extends CompleteAlign {

	public CompleteAlignSimple() {
		super(new LocalInit(), new LocalFill(), new LocalAlignSimple());
	}

}
