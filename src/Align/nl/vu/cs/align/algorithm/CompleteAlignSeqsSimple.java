package nl.vu.cs.align.algorithm;

public class CompleteAlignSeqsSimple extends CompleteAlign {

	public CompleteAlignSeqsSimple() {
		super(new LocalInit(), new LocalFill(), new LocalAlignSimple());
	}

}
