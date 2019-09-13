package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

public class CompleteAlign extends Align {

	protected MatrixInit init;
	protected MatrixFill fill;
	protected Align alignMethod;
	
	public CompleteAlign(MatrixInit init, MatrixFill fill, Align alignMethod) {
		Assert.assertTrue(init != null && fill != null && alignMethod != null);
		this.init = init;
		this.fill = fill;
		this.alignMethod = alignMethod;
	}
	
	public void newMethod() {
		int tablica[] = new int[100];
	}

	public float align(AlignData data) {
		init.init(data);
		fill.fill(data);
		return alignMethod.align(data);
	}

	public Align getAlignMethod() {
		return alignMethod;
	}
}
