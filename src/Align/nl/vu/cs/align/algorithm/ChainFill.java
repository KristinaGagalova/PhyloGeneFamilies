package nl.vu.cs.align.algorithm;

import java.util.*;

import nl.vu.cs.align.matrix.*;

public class ChainFill implements MatrixFill {
	
	Vector fillMethods;
	
	public ChainFill(MatrixFill fillOne, MatrixFill fillTwo) {
		fillMethods = new Vector(2);
		fillMethods.add(fillOne);
		fillMethods.add(fillTwo);
	}

	public void fill(AlignData data) {
		chainFill(fillMethods, data);
	}
	
	public static void chainFill(Vector fillMethods, AlignData data) {
		for (int i = 0; i < fillMethods.size(); i++) {
			((MatrixFill)fillMethods.elementAt(i)).fill(data);
		}
	}

}
