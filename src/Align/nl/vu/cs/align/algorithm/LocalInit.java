package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;

public class LocalInit implements MatrixInit {

	public void init(AlignData data) {
		localInit(data);
	}

	static public void localInit(AlignData data) {
		init(data.getMatrix());
	}
	
	static public void init(float m[][]) {
		int lenX = m[0].length;
		int lenY = m.length;
		
		for (int y = 0; y < lenY; y++) {
			for (int x = 0; x < lenX; x++) {
				m[y][x] = 0;
			}
		}
	}
}
