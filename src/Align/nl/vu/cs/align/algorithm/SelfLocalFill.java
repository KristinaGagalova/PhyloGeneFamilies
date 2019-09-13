package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;

public class SelfLocalFill implements MatrixFill {

	public void fill(AlignData data) {
		selfLocalFill(data);
	}

	public static void selfLocalFill(AlignData data) {
		new ChainFill(new LocalFill(), new SelfLocalFillMask()).fill(data);
	}
}
