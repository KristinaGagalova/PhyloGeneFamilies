package nl.vu.cs.align.proteinlibs;

import java.util.*;

public class LengthComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		return ((String)arg0).length() - ((String)arg1).length();
	}

}
