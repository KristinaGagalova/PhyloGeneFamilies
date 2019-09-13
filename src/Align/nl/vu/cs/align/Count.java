package nl.vu.cs.align;

import java.io.*;

public class Count {

	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new FileReader("random self-cmp"));
			
			String s;
			int count = 0;
			while ((s = in.readLine()) != null) {
				float val = Float.parseFloat(s);
				if (val > 130.0) {
					count++;
				}
			}
			System.out.println(count);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.toString());
		}
 	}
}
