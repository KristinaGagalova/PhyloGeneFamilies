package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.matrix.*;

public class AlignDDPMin extends AlignDDP {
	
	public AlignDDPMin(Align partialAlign) {
		super(partialAlign);
	}

	protected AlignData unify(AlignData data1, AlignData data2) {
		float [][]m1 = data1.getMatrix();
		float [][]m2 = data2.getMatrix();
		
		int lenx = m1[0].length;
		int leny = m1.length;
		for (int y = 0; y < leny; y++) {
			for (int x = 0; x < lenx; x++) {
				m1[y][x] = Math.min(m1[y][x], m2[y][x]);
			}
		}
		
		return data1;
	}

}
