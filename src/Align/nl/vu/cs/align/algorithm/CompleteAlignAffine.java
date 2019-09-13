/*
 * Created on Jul 4, 2003
 */
package nl.vu.cs.align.algorithm;


public class CompleteAlignAffine extends CompleteAlign {
	
	public CompleteAlignAffine(MatrixInit init, MatrixFill fill, AlignAffine alignMethod) {
		super(init, fill, alignMethod);
	}

	public AlignAffine getAlignAffineMethod() {
		return (AlignAffine) alignMethod;
	}

}
