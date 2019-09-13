package nl.vu.cs.align.algorithm;

import nl.vu.cs.align.*;
import nl.vu.cs.align.matrix.*;

/**
 * Double alignment: against words and reversed words
 */
public abstract class AlignDDP extends Align {
	
	protected Align partialAlign;
	
	public AlignDDP(Align partialAlign) {
		this.partialAlign = partialAlign;
	}
	
	/**
	 * Reverses the string
	 */
	public static String reverse(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = s.length()-1; i >= 0; i--) {
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public float align(AlignData data) {
		
		// perform the first step of alignment
		float result = partialAlign.align(data);
		
		//!!!
		Matrix.saveImageGray(data.getMatrix(), true, "data1");
		
		// reverse words
		Assert.fail("Not implemented");
		AlignData data2 = (AlignData)data.clone();
		data2.setSeqX(reverse(data2.getSeqX()));
		data2.setSeqY(reverse(data2.getSeqY()));

		// perform the second step of alignment
		float result2 = partialAlign.align(data2);
		
		//!!!
		Matrix.saveImageGray(data2.getMatrix(), true, "data2");

		Assert.assertTrue(result == result2);
		
		// mirror the resulting array
		Matrix.swapTopLeft(data2.getMatrix(), 1);
		
		//!!!
		Matrix.saveImageGray(data2.getMatrix(), true, "data2swap");
		
		// unify the data from the first and the second pass
		unify(data, data2);
		
		return result;
	}
	
	/**
	 * @return data1 = data1 * data2
	 */
	protected abstract AlignData unify(AlignData data1, AlignData data2);

}
