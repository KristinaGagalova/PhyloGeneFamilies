package nl.vu.cs.align;

public class Shuffle {

	public static void main(String[] args) {
		if (args.length == 0) 
			System.err.println("Usage: program seq_len seq_rept");
		else {
			int seqlen = Integer.valueOf(args[0]).intValue();
			int seqrept = Integer.valueOf(args[1]).intValue();
			
			for (int i = 0; i < seqrept; i++) {
				int []t = RandomSimilarity.generateRandomNumbers(seqlen);
				for (int j = 0; j < t.length; j++) {
					System.out.print(t[j]);
					System.out.print("\t");
				}
				System.out.println();
			}
		}
	}
}
