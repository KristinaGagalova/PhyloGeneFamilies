package nl.vu.cs.align.tools;

public class StringToolbox {
	public static String clearWhitespaces(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}



}
