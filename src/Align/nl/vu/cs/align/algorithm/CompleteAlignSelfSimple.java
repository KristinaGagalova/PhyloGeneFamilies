package nl.vu.cs.align.algorithm;


public class CompleteAlignSelfSimple extends CompleteAlign {
	
	public CompleteAlignSelfSimple() {
		super(new LocalInit(), new SelfLocalFill(), new LocalAlignSimple());
	}

}
